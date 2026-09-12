<template>
  <g>
    <g class="bbox-group" v-if="modeType == 'RECT'">
      <rect
        v-if="isMousemove"
        fill="rgba(32, 153, 250, 0.3)"
        stroke="#E53935"
        strokeWidth="4"
        :x="svgX"
        :y="svgY"
        :width="svgWidth"
        :height="svgHeight"
      ></rect>
    </g>
    <g class="bbox-group" v-if="modeType == 'POLYGON'">
      <g v-if="!finish">
        <polyline
          :points="pointText"
          style="fill: none; stroke: #E53935; stroke-width: 3"
        />
        <g v-for="(item, index) in pointList" :key="index">
          <rect
            :x="item[0] - 3"
            :y="item[1] - 3"
            width="6"
            height="6"
            fill="#E53935"
          ></rect>
        </g>
      </g>
    </g>
  </g>
</template>

<script>
export default {
  props: {
    modeType: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      currentSvgData: {
        // 当前选中的坐标数据
        startX: 0,
        startY: 0,
        endX: 0,
        endY: 0,
      },
      isMousedown: false,
      isMousemove: false,
      svgX: 0,
      svgY: 0,
      svgWidth: 0,
      svgHeight: 0,
      pointCount: 0,
      pointList: [],
      pointText: "",
      finish: false,
    };
  },
  watch: {
    currentSvgData: {
      handler(val) {
        this.svgWidth = Math.abs(val.startX - val.endX);
        this.svgHeight = Math.abs(val.startY - val.endY);
        if (val.startX < val.endX) {
          // 向右拉框
          this.svgX = val.startX;
          if (val.startY < val.endY) {
            this.svgY = val.startY;
          } else {
            this.svgY = val.endY;
          }
        } else {
          // 向左拉框
          this.svgX = val.endX;
          if (val.startY < val.endY) {
            this.svgY = val.startY;
          } else {
            this.svgY = val.endY;
          }
        }
      },
      deep: true,
    },
    pointList: {
      handler(val) {
        this.pointText = "";
        val.forEach((item) => {
          this.pointText = this.pointText + ` ${item[0]},${item[1]}`;
        });
      },
    },
  },
  mounted() {
    this.$parent.$refs.svgRef.addEventListener(
      "mousedown",
      this.svgMousedown,
      false
    );
    this.$parent.$refs.svgRef.addEventListener(
      "mousemove",
      this.svgMousemove,
      false
    );
    this.$parent.$refs.svgRef.addEventListener(
      "mouseup",
      this.svgMouseup,
      false
    );
    document.addEventListener("keydown", this.handleWatchEnter);
  },
  methods: {
    // 画框开始
    svgMousedown(e) {
      const { offsetX, offsetY, buttons } = e;
      if (this.modeType == "RECT") {
        this.isMousedown = true;
        Object.assign(this.currentSvgData, {
          startX: offsetX,
          startY: offsetY,
        });
      }
      if (this.modeType == "POLYGON") {
        if (buttons == 1) {
          if (this.finish) {
            this.finish = false;
            this.pointCount = 0;
            this.pointList = [];
          }
          this.pointCount++;
          this.pointList.push([offsetX, offsetY]);
        } else {
          if (this.pointCount > 2) {
            this.pointList.pop();
            this.$emit("drawOver", this.pointList);
            this.finish = true;
          } else {
            this.$message.error(this.$t("annotate.tempsvg.jm456w"));
            this.finish = false;
            this.pointCount = 0;
            this.pointList = [];
          }
        }
      }
      if (this.modeType == "POINT") {
        this.svgX = offsetX;
        this.svgY = offsetY;
        this.$emit("drawOver", [offsetX, offsetY]);
      }
    },
    // 画框中
    svgMousemove(e) {
      const { offsetX, offsetY } = e;
      if (this.isMousedown) {
        this.isMousemove = true;
        if (this.modeType == "RECT") {
          Object.assign(this.currentSvgData, {
            endX: offsetX,
            endY: offsetY,
          });
        }
      }
      if (this.modeType == "POLYGON") {
        if (!this.finish) {
          this.$set(this.pointList, this.pointCount, [offsetX, offsetY]);
        }
      }
    },
    // 画框结束
    svgMouseup(e) {
      const { offsetX, offsetY } = e;
      this.isMousedown = this.isMousemove = false;
      if (this.modeType == "RECT") {
        if (
          this.currentSvgData.startX != offsetX ||
          this.currentSvgData.startY != offsetY
        ) {
          this.$emit("drawOver", [
            [this.svgX, this.svgY],
            [this.svgX + this.svgWidth, this.svgY],
            [this.svgX, this.svgY + this.svgHeight],
            [this.svgX + this.svgWidth, this.svgY + this.svgHeight],
          ]);
        }
      }
    },
    handleWatchEnter(e) {
      var key = window.event ? e.keyCode : e.which;
      if (key == 27) {
        this.finish = false;
        this.pointCount = 0;
        this.pointList = [];
      }
    },
  },
};
</script>
<style scoped lang="scss"></style>
