<template>
  <div class="annotateDetail">
    <img class="bg-img" ref="img" :src="$common.handleDataset(currentImgData.id)" />
    <div class="canvas" :style="canvasWH">
      <svg ref="svgRef" style="width: 100%; height: 100%">
        <SvgList v-for="(item, index) in dataList" :key="index" :svgData="item" />
      </svg>
      <TagList v-for="(item, index) in dataList" :tagData="item" :key="'tag' + index" v-show="item.tagVisible" />
    </div>
  </div>
</template>

<script>
import colorList from "@/utils/colorList";
import SvgList from "@/views/annotationPlatform/markTool/components/annotate/svgList";
import TagList from "@/views/annotationPlatform/markTool/components/annotate/tagList";
import { annotationType } from "@/utils/commonData";
export default {
  components: {
    SvgList,
    TagList,
  },
  props: {
    currentImgData: {
      type: Object,
      default: () => { },
    },
    tagVisible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      canvasWH: {
        width: "0px",
        height: "0px",
        position: "absolute",
      },
      dataList: [],
    };
  },
  created() {
    let img = new Image();
    img.src = this.$common.handleDataset(this.currentImgData.commit?.imageId);
    img.onload = () => {
      const scale = this.$refs.img.clientWidth / img.width;
      Object.assign(this.canvasWH, {
        width: img.width + "px",
        height: img.height + "px",
        transform: `scale(${scale}) translate(0px, 0px)`,
      });
      if (this.currentImgData.status) {
        const getObjectKey = (object, value) => {
          return Object.keys(object).find((key) => object[key] == value);
        };
        const treeToArray = (tree) => {
          let name = "";
          if (!tree.length) {
            return;
          }
          if (tree[0].children.length) {
            name = treeToArray(tree[0].children);
          } else {
            name = tree[0].name;
          }
          return name;
        };
        this.invalid = this.currentImgData.commit.isValid ? false : true;
        if (this.currentImgData.commit.annotations) {
          const tempColorList = [];
          this.currentImgData.commit.annotations.forEach((item, index) => {
            const obj = {
              id: Math.random() + "",
              modeType: getObjectKey(annotationType, item.annotationType), // 画框类型
              boxList: [], // 画框数据
              tagName: item.tagName, // 一级名称
              labelTree: item.annotation.label, // tree
              name: treeToArray(item.annotation.label), // 当前名称
              color: "", // 颜色
              tagVisible: true,
              svgVisible: true,
            };
            if (getObjectKey(annotationType, item.annotationType) == "RECT") {
              obj.boxList = [
                [item.annotation.box[0], item.annotation.box[1]],
                [item.annotation.box[2], item.annotation.box[1]],
                [item.annotation.box[0], item.annotation.box[3]],
                [item.annotation.box[2], item.annotation.box[3]],
              ];
            }
            if (
              getObjectKey(annotationType, item.annotationType) == "POLYGON"
            ) {
              obj.boxList = item.annotation.line.map((i) => {
                return [i.x, i.y];
              });
            }
            if (getObjectKey(annotationType, item.annotationType) == "POINT") {
              obj.boxList = [item.annotation.point.x, item.annotation.point.y];
            }

            const arrIndex = tempColorList.indexOf(
              treeToArray(item.annotation.label)
            );
            let colorIndex = 0;
            if (arrIndex > -1) {
              colorIndex = arrIndex;
            } else {
              tempColorList.push(treeToArray(item.annotation.label));
              colorIndex = tempColorList.length - 1;
            }
            obj.color = colorList[colorIndex];
            this.dataList.push(obj);
          });
        }
        console.info(this.dataList)
      }
    };
  },
  methods: {},
};
</script>
<style scoped lang="scss">
.annotateDetail {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  .bg-img {
    max-width: 100%;
    max-height: 100%;
    display: block;
    position: absolute;
  }
}
</style>
