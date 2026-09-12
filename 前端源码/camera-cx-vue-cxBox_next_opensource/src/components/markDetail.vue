<template>
  <div>
    <div class="drag-inner">
      <svg class="canvas" @click="draw" v-if="dataListAll.length == 0">
        <g v-for="(item, index) in svgList" :key="index">
          <polygon
            :points="item.pointText"
            style="
              fill: rgba(32, 153, 250, 0.5);
              stroke: #E53935;
              stroke-width: 2;
            "
          />
          <g
            v-for="(pointItem, pointIndex) in item.pointList"
            :key="pointIndex"
          >
            <rect
              :x="pointItem.x - 3"
              :y="pointItem.y - 3"
              width="6"
              height="6"
              fill="#E53935"
            ></rect>
          </g>
        </g>
      </svg>
      <div v-else>
        <svg class="canvas" v-for="(items, ind) in dataListAll" :key="ind">
          <g v-for="(item, index) in items.markPoints" :key="index">
            <text
              :x="item.pointList[0].x - 10"
              :y="item.pointList[0].y - 10"
              :fill="items.color"
              style="font-size: 13px; font-weight: bold"
            >
              {{ items.name }}
            </text>
            <polygon
              :points="item.pointText"
              :style="{
                fill:
                  'rgba(' + items.r + ',' + items.g + ',' + items.b + ',0.5)',
                stroke: 'rgb(' + items.r + ',' + items.g + ',' + items.b + ')',
                'stroke-width': 2,
              }"
            />
            <g
              v-for="(pointItem, pointIndex) in item.pointList"
              :key="pointIndex"
            >
              <rect
                :x="pointItem.x - 3"
                :y="pointItem.y - 3"
                width="6"
                height="6"
                :fill="items.color"
              ></rect>
            </g>
          </g>
        </svg>
      </div>

      <img class="bg-img" draggable="false" ref="draw_img" :src="fileUrl" />
    </div>
  </div>
</template>

<script>
export default {
  props: {
    fileUrl: {
      type: String,
      default: "",
    },
    dataList: {
      type: Array,
      default: () => [],
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    dataListAll: {
      type: Array,
      default: () => [],
    },
    nameEn: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      svgList: [],
      pointCount: 0,
      pointList: [],
      finish: true,
    };
  },
  watch: {
    dataList: {
      immediate: true,
      deep: true,
      handler(val) {
        if (val.length > 0) {
          if (this.nameEn == "person_tracker") {
            //判断是不是人流量绘制
            this.finish = true;
          } else {
            this.finish = false;
          }
          this.svgList = [];
          val.forEach((item) => {
            let pointText = "";
            item.forEach((i) => {
              pointText = pointText + ` ${i.x},${i.y}`;
            });
            this.svgList.push({
              pointList: item,
              pointText: pointText,
            });
          });
        } else {
          this.finish = true;
          this.svgList = [];
        }
      },
    },
  },
  methods: {
    // 标记
    draw(e) {
      if (this.disabled) {
        return;
      }
      if (this.dataList.length > 1) {
        this.$message.warning(this.$t("modeltesting.markdetail.b47pf8"));
        return;
      }
      if (this.svgList.length - 1 > 0 && this.nameEn == "person_tracker") {
        if (this.svgList[this.svgList.length - 1].pointList.length == 2) {
          this.$message.warning(this.$t("addcamera.markdetail.w483uo"));
          return;
        }
      }
      const { offsetX, offsetY } = e;
      const point = [{ x: offsetX, y: offsetY }];
      if (this.finish) {
        this.finish = false;
        this.svgList.push({
          pointList: point,
          pointText: `${point[0].x},${point[0].y}`,
        });
      } else {
        const obj = this.svgList[this.svgList.length - 1];
        obj.pointList.push({ x: offsetX, y: offsetY });
        obj.pointText = obj.pointText + ` ${point[0].x},${point[0].y}`;
      }

      this.$emit("dataChange", this.svgList);
    },
    // 重新标记
    resetting() {
      this.finish = true;
    },
    // 保存
    save() {
      return this.svgList;
    },
    // 清空
    clear() {
      this.svgList = [];
    },
    //
    scaleRatio() {
      if (this.$refs.draw_img) {
        let nw = this.$refs.draw_img.naturalWidth;
        let ow = this.$refs.draw_img.offsetWidth;
        return ow == 0 ? 0 : nw / ow;
      }
      return 0;
    },
  },
};
</script>
<style scoped lang="scss">
.drag-inner {
  position: relative;
  margin: 0 auto;
  width: 640px;
  height: 360px;
  .canvas {
    width: 100%;
    height: 100%;
    position: absolute;
    left: 0;
    right: 0;
    z-index: 2;
  }
  .bg-img {
    width: 640px;
    height: 360px;
    // display: block;
  }
}
</style>
