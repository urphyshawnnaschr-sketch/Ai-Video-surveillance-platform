<template>
  <div class="image-point-drawer">
    <div class="image-container">
      <img
          src="@/assets/images/login/bg1.png"
          ref="baseImage"
          @load="onImageLoad"
          class="base-image"
      >
      <svg
          class="overlay-svg"
          ref="svgOverlay"
          :width="imageWidth"
          :height="imageHeight"
          @mousemove="handleMouseMove"
      >
        <!-- 顺序连接线 -->
        <path
            v-for="(path, index) in connectionPaths"
            :key="'path-'+index"
            :d="path"
            stroke="#3498db"
            stroke-width="2"
            fill="none"
        />

        <!-- 点 -->
        <circle
            v-for="(point, index) in points"
            :key="point.id"
            :cx="point.x"
            :cy="point.y"
            :r="point.radius || 5"
            :fill="point.color || pointColors[index % pointColors.length]"
            @mouseover="activePoint = point"
            @mouseleave="activePoint = null"
            @click="handlePointClick(point)"
            class="point"
        />

        <!-- 悬停信息 -->
        <g v-if="activePoint" class="point-info">
          <rect
              :x="activePoint.x + 10"
              :y="activePoint.y - 25"
              width="120"
              height="20"
              rx="3"
              fill="rgba(0,0,0,0.7)"
          />
          <text
              :x="activePoint.x + 15"
              :y="activePoint.y - 10"
              fill="white"
              font-size="12"
          >
            {{ activePoint.name }} ({{ activePoint.x }}, {{ activePoint.y }})
          </text>
        </g>
      </svg>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ImagePointDrawer',
  props: {
    points: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      imageWidth: 0,
      imageHeight: 0,
      activePoint: null,
      pointColors: ['#e74c3c', '#3498db', '#2ecc71', '#f39c12', '#9b59b6', '#1abc9c', '#d35400']
    };
  },
  computed: {
    connectionPaths() {
      const paths = [];
      for (let i = 0; i < this.points.length - 1; i++) {
        const from = this.points[i];
        const to = this.points[i + 1];
        paths.push(this.calculateConnectionPath(from, to));
      }
      return paths;
    }
  },
  methods: {
    onImageLoad() {
      const img = this.$refs.baseImage;
      this.imageWidth = img.clientWidth;
      this.imageHeight = img.clientHeight;
    },
    calculateConnectionPath(from, to) {
      if (!from || !to) return '';

      // 计算控制点（创建曲线效果）
      const midX = (from.x + to.x) / 2;
      const midY = (from.y + to.y) / 2;

      // 根据方向调整控制点偏移量
      const dx = to.x - from.x;
      const dy = to.y - from.y;
      const distance = Math.sqrt(dx*dx + dy*dy);
      const offset = Math.min(50, distance * 0.3);

      // 弯曲程度，可以改这个数值
      const curvature = 70;

      // 垂直方向偏移创建曲线
      let cp1x = from.x + curvature;
      let cp1y = from.y + offset + curvature;
      let cp2x = to.x - curvature;
      let cp2y = to.y - offset - curvature;

      // 如果是水平线，则改为垂直偏移
      if (Math.abs(dy) < 5) {
        cp1x = from.x + offset;
        cp1y = from.y;
        cp2x = to.x - offset;
        cp2y = to.y;
      }

      return `M${from.x},${from.y} C${cp1x},${cp1y} ${cp2x},${cp2y} ${to.x},${to.y}`;
    },
    handleMouseMove(event) {
      const svg = this.$refs.svgOverlay;
      if (!svg) return;

      const rect = svg.getBoundingClientRect();

      // 计算鼠标在内的坐标
      this.mouseX = event.clientX - rect.left;
      this.mouseY = event.clientY - rect.top;
    },
    handlePointClick(point) {
      this.$emit('point-click', point);
    }
  }
};
</script>

<style scoped>
.image-point-drawer {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  height: 650px;
  margin: 0 auto;
}

.image-container {
  position: relative;
  display: inline-block;

  width: 100%;
  height: 100%;
}

.base-image {
  display: block;
  width: 100%;
  height: 100%;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.overlay-svg {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
}

.overlay-svg .point {
  pointer-events: auto;
  cursor: pointer;
  transition: r 0.2s ease, filter 0.2s ease;
  filter: drop-shadow(0 0 2px rgba(0,0,0,0.5));
}

.overlay-svg .point:hover {
  r: 7;
  stroke: white;
  stroke-width: 2;
}

.point-info text {
  pointer-events: none;
  user-select: none;
}

.sequence-indicator {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #eee;
  box-shadow: inset 0 1px 3px rgba(0,0,0,0.05);
}

.sequence-title {
  font-weight: bold;
  color: #2c3e50;
  margin-right: 10px;
}

.sequence-item {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  background: white;
  border-radius: 20px;
  border: 1px solid #ddd;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.sequence-item:hover {
  background: #e3f2fd;
  border-color: #bbdefb;
}

.sequence-item.active {
  background: #bbdefb;
  border-color: #90caf9;
  transform: translateY(-2px);
  box-shadow: 0 3px 6px rgba(0,0,0,0.1);
}

.sequence-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  background: #3498db;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
}
</style>