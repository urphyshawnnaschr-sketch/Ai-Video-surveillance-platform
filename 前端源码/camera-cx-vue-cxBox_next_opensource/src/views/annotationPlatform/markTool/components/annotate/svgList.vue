<template>
  <g
    :class="{ svg: modeType != 'RECT' && modeType != 'POLYGON' }"
    @click="clickSvg($event,svgData)"
  >
    <g class="bbox-group" v-if="svgData.modeType == 'RECT'">
      <rect
        :fill="$common.hexToRgba(svgData.color, 0.3)"
        :stroke="svgData.color"
        strokeWidth="4"
        :x="svgX"
        :y="svgY"
        :width="svgWidth"
        :height="svgHeight"
      ></rect>
    </g>
    <g class="bbox-group" v-if="svgData.modeType == 'POLYGON'">
      <polygon :points="pointText" :style="polygonStyle" />
    </g>
    <g class="bbox-group" v-if="svgData.modeType == 'POINT'">
      <circle
        :cx="svgX"
        :cy="svgY"
        r="3"
        :stroke="svgData.color"
        stroke-width="2"
        :fill="$common.hexToRgba(svgData.color, 0.3)"
      />
    </g>
  </g>
</template>

<script>
export default {
  props: {
    svgData: {
      type: Object,
      default: () => {},
    },
    modeType: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      svgX: 0,
      svgY: 0,
      svgWidth: 0,
      svgHeight: 0,
      pointList: [],
      pointText: "",
      polygonStyle: {},
      status: false,
    };
  },
  watch: {
    svgData: {
      immediate: true,
      deep: true,
      handler(val) {
        const boxList = val.boxList;
        if (val.modeType == "RECT") {
          this.svgX = boxList[0][0];
          this.svgY = boxList[0][1];
          this.svgWidth = boxList[1][0] - boxList[0][0];
          this.svgHeight = boxList[2][1] - boxList[0][1];
        }
        if (val.modeType == "POLYGON") {
          this.polygonStyle = {
            fill: this.$common.hexToRgba(val.color, 0.3),
            stroke: val.color,
          };
          this.pointList = boxList;
          this.pointText = "";
          boxList.forEach((item) => {
            this.pointText = this.pointText + ` ${item[0]},${item[1]}`;
          });
        }
        if (val.modeType == "POINT") {
          this.svgX = boxList[0];
          this.svgY = boxList[1];
        }
      },
    },
  },
  methods: {
    clickSvg(e,val) {
      if (this.modeType == "ISSUE") {
        return
      }else {
        this.$emit("changeCurrent", val);
      }
    },
  },
};
</script>
<style scoped lang="scss">
.svg {
  cursor: pointer;
}
</style>
