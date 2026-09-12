<template>
  <div class="trajectories-box">
    <!-- 底图 -->
    <img :src="getImageUrl(fileObj.filename)" ref="baseImage"
      @load="onImageLoad" class="base-image" :alt="$t('components.detail.26y57g')" />
<!-- :src="VUE_APP_API_BASE_URL + '/face/track/config/image?filename=' + fileObj.filename"  -->
    <!-- SVG -->
    <svg class="overlay-svg" ref="svgOverlay" :width="imageWidth" :height="imageHeight">
      <!-- 连接线 -->
      <path v-for="(path, index) in connectionPaths" :key="'path-' + index" :d="path" stroke="#FFFFFF"
        stroke-width="2.5" fill="none" />

      <!-- 点 -->
      <circle v-for="(item, index) in points" :key="item.id" :cx="item.x" :cy="item.y" :r="item.radius || 5"
        fill="#FFFFFF" @click="handleCircleClick(item)" />
    </svg>

    <!-- 点击展示的信息 -->
    <div class="details" :style="detailsStyle" ref="detailsView">
      <!-- :src="VUE_APP_API_BASE_URL + '/face/report/image?id=' + circleData.reportId" -->
      <el-image class="img" :src="getImageUrlx('/face/report/image?id=' + circleData.reportId)" ></el-image>
      <div class="info-box">
        <div class="info-item">
          <div class="title">{{$t('faceControl.faceRecognition.snapTime')}}：</div>
          <div class="text">{{ circleData.reportAt }}</div>
        </div>
        <div class="info-item">
          <div class="title">{{$t('mapimagemanagent.index.h8zi52')}}：</div>
          <div class="text">{{ circleData.cameraGroupName }}</div>
        </div>
        <div class="info-item">
          <div class="title">{{$t('groupview.index.30617c')}}：</div>
          <div class="text">{{ circleData.cameraName }}</div>
        </div>
      </div>
      <i class="el-icon-close" @click="detailsStyle.display = 'none'"></i>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
export default {
  name: "ImagePointDrawer",
  props: {
    pathList: {
      type: Array,
      default: () => [],
    },
    fileObj: {
      type: Object,
      default: () => { }
    }
  },
  data() {
    return {
      VUE_APP_API_BASE_URL,
      imageWidth: 0,
      imageHeight: 0,
      // 点击的点的数据
      circleData: {},
      // 弹窗样式
      detailsStyle: {
        display: "none",
        top: 0,
        left: 0,
      },
      VUE_APP_API_BASE_URL,
      points: [],
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
    },
  },
  created() {
  },
  watch: {
    pathList: {
      handler() {
        this.initContainer();
      },
      deep: true,
    },
  },
  methods: {
    initContainer() {
      const img = this.$refs.baseImage;
      // 计算缩放比例
      let scaleX = img.naturalWidth / img.clientWidth;
      let scaleY = img.naturalHeight / img.clientHeight;

      if (scaleX && scaleY) {
        let arr = []
        this.pathList.map(item => {
          arr.push({
            x: item.cameraPoints[0] / scaleX,
            y: item.cameraPoints[1] / scaleY,
            ...item
          })
        });
        this.points = arr;
      }
    },
    // 获取图片尺寸
    onImageLoad() {
      this.$nextTick(()=>{
        const img = this.$refs.baseImage;
        this.imageWidth = img.clientWidth;
        this.imageHeight = img.clientHeight;

        this.initContainer();
      },200)
    },
    // 二次贝塞尔曲线
    // calculateConnectionPath(from, to) {
    //   // 弯曲的弧顶 （X差值 + y差值） / 2
    //   const arcTop = ((to.x - from.x) + (to.y - from.y)) / 2
    //
    //   // 弯曲程度，可以改这个数值
    //   const curvature = arcTop > 0 ? -100 : 200;
    //
    //   return `M${from.x} ${from.y} q ${arcTop} ${curvature} ${to.x - from.x} ${to.y - from.y}`;
    // },
    // 三次贝塞尔曲线
    calculateConnectionPath(from, to) {
      if (!from || !to) return "";

      // 计算控制点（创建曲线效果）
      const midX = (from.x + to.x) / 2;
      const midY = (from.y + to.y) / 2;

      // 根据方向调整控制点偏移量
      const dx = to.x - from.x;
      const dy = to.y - from.y;
      const distance = Math.sqrt(dx * dx + dy * dy);
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
    // 点击某个点
    handleCircleClick(item) {
      this.circleData = item;
      this.detailsStyle = {
        display: "block",
        top: item.y + 10 + "px",
        left: item.x + 10 + "px",
      };

      // 判断弹窗是否超出显示范围
      this.$nextTick(() => {
        let boxHeight = this.$refs.detailsView.clientHeight;
        let boxWidth = this.$refs.detailsView.clientWidth;

        // 是否超高
        if (item.y + 10 + boxHeight > this.imageHeight) {
          this.detailsStyle.top = this.imageHeight - boxHeight + "px";
        }

        // 是否超宽
        if (item.x + 10 + boxWidth > this.imageWidth) {
          this.detailsStyle.left = this.imageWidth - boxWidth + "px";
        }
      })
    },
    // 获取图片
    getImageUrl(filename) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/face/track/config/image?filename=${filename}&X-Token=${token}`;
    },
        // 获取图片
    getImageUrlx(url) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}${url}&X-Token=${token}`;
    },
  },
};
</script>

<style scoped lang="scss">
.trajectories-box {
  width: 100%;
  position: relative;

  .base-image {
    display: block;
    width: 100%;
    // height: 100%;
  }

  // SVG 样式
  .overlay-svg {
    position: absolute;
    top: 0;
    left: 0;

    circle:hover {
      cursor: pointer;
      stroke: #6663fc;
      stroke-width: 2px;
      r: 7;
    }
  }

  .details {
    background-color: #6663fc;
    padding: 12px;
    position: absolute;
    border-radius: 6px;
    text-align: center;

    .img {
      width: 120px;
      height: 120px;
    }

    .info-box {
      padding: 10px 0;

      .info-item {
        font-size: 12px;
        display: flex;
        align-items: center;
        margin-bottom: 4px;
        color: #ffffff;

        .title {
          width: 85px;
          text-align: right;
          font-weight: normal;
          font-size: 12px;
          color: #ffffff;
        }
      }
    }

    .el-icon-close {
      color: #ffffff;
      display: block;
      text-align: center;
      font-size: 16px;
      cursor: pointer;
    }
  }
}
</style>
