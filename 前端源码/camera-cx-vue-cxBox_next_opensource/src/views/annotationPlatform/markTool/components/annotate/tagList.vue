<template>
  <div class="item-tag" :style="tagStype">{{ tagData.name }}</div>
</template>

<script>
export default {
  props: {
    tagData: {
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
      tagStype: {},
    };
  },
  watch: {
    tagData: {
      immediate: true,
      deep: true,
      handler(val) {
        console.info(val);
        if (val.modeType == "RECT") {
          this.tagStype = {
            background: this.$common.hexToRgba(val.color, 0.6),
            width: val.boxList[1][0] - val.boxList[0][0] + "px",
            left: val.boxList[0][0] + "px",
            top: val.boxList[0][1] + "px",
          };
        }
        if (val.modeType == "POLYGON") {
          this.tagStype = {
            background: this.$common.hexToRgba(val.color, 0.6),
            left: val.boxList[0][0] + "px",
            top: val.boxList[0][1] - 10 + "px",
          };
        }
        if (val.modeType == "POINT") {
          this.tagStype = {
            background: this.$common.hexToRgba(val.color, 0.6),
            left: val.boxList[0] + 4 + "px",
            top: val.boxList[1] - 24 + "px",
          };
        }
      },
    },
  },
  created() {},
  methods: {},
};
</script>
<style scoped lang="scss">
.item-tag {
  height: 24px;
  line-height: 24px;
  font-size: 13px;
  color: #fff;
  position: absolute;
  padding: 0 5px;
  overflow: hidden;
  z-index: 3;
}
</style>
