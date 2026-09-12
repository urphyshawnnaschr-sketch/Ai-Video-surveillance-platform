<template>
  <g>
    <g v-if="svgData.modeType == 'RECT'">
      <g class="bbox-group">
        <rect
          fill="rgba(32, 153, 250, 0.5)"
          stroke="#E53935"
          strokeWidth="4"
          :x="svgX"
          :y="svgY"
          :width="svgWidth"
          :height="svgHeight"
        ></rect>
      </g>
      <g v-for="(item, index) in svgData.boxList" :key="index">
        <rect
          :x="item[0] - 3"
          :y="item[1] - 3"
          width="6"
          height="6"
          fill="#E53935"
        ></rect>
      </g>
    </g>
    <g v-if="svgData.modeType == 'POLYGON'">
      <polygon
        :points="pointText"
        style="fill: rgba(32, 153, 250, 0.5); stroke: #E53935; stroke-width: 3"
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
    <g class="bbox-group" v-if="svgData.modeType == 'POINT'">
      <circle
        :cx="svgX"
        :cy="svgY"
        r="4"
        stroke="#E53935"
        stroke-width="2"
        fill="rgba(32, 153, 250, 0.3)"
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
  },
  data() {
    return {
      svgX: 0,
      svgY: 0,
      svgWidth: 0,
      svgHeight: 0,
      pointList: [],
      pointText: "",
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
  methods: {},
};
</script>
<style scoped lang="scss"></style>
