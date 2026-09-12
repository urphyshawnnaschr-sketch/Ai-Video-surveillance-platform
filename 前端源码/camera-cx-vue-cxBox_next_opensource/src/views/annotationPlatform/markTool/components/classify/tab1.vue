<template>
  <div class="tab-wrap">
    <div class="tab-title">{{ $t("annotate.tab1.oz2x77") }}</div>
    <div class="tree">
      <el-tree
        :data="attributeList"
        :props="defaultProps"
        node-key="name"
        default-expand-all
        :expand-on-click-node="false"
        ref="tree"
      >
        <span slot-scope="{ node, data }">
          <div class="item" v-if="data.children.length">
            <span class="line" :style="{ background: data.color }"></span
            >{{ data.name }}
          </div>
          <span v-else
            ><el-radio
              v-model="name"
              :label="data.name"
              @input="changeAttribute($event, node)"
            ></el-radio
          ></span>
        </span>
      </el-tree>
    </div>
  </div>
</template>

<script>
import colorList from "@/utils/colorList";
export default {
  props: {
    attributeList: {
      type: Array,
      default: () => [],
    },
    currentImgData: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      defaultProps: {
        children: "children",
        label: "name",
      },
      tagName: "", // 一级标签名
      color: "",
      name: "", // 选中标签值
      tempTagArr: [], // 选中的所有节点
      labelTree: [], // 返回给后端的
    };
  },
  watch: {
    attributeList: {
      deep: true,
      handler(val) {
        val.forEach((item, index) => {
          item.color = colorList[index];
        });
      },
    },
    currentImgData: {
      deep: true,
      handler(val) {
        if (val.commit.annotations.length) {
          const name = this.treeToArray(
            val.commit.annotations[0].annotation.label
          );
          this.changeAttribute(name, this.$refs.tree.getNode(name));
        }
      },
    },
    name: {
      deep: true,
      handler(val) {
        this.$emit("changeAttribute", {
          color: this.color,
          name: val,
          tagName: this.tagName,
          labelTree: this.labelTree,
        });
      },
    },
  },
  created() {},
  methods: {
    changeAttribute(val, node) {
      this.tempTagArr = [];
      this.treeToArrayByFather(node);
      const newTempTagArr = JSON.parse(JSON.stringify(this.tempTagArr));
      for (let i = 0; i < newTempTagArr.length; i++) {
        if (newTempTagArr[i + 1]) {
          newTempTagArr[i + 1].children = [
            {
              name: newTempTagArr[i].name,
              editCell: newTempTagArr[i].editCell,
              children: newTempTagArr[i].children,
            },
          ];
        }
      }
      this.labelTree = newTempTagArr.slice(-1);
      this.tagName = this.labelTree[0].name;
      this.color = this.labelTree[0].color;
      this.name = val;
    },
    treeToArrayByFather(item) {
      this.tempTagArr.push(item.data);
      if (this.$common.checkIsJSON(item.parent.data)) {
        this.treeToArrayByFather(item.parent);
      }
    },
    treeToArray(tree) {
      let name = "";
      if (!tree.length) {
        return;
      }
      if (tree[0].children.length) {
        name = this.treeToArray(tree[0].children);
      } else {
        name = tree[0].name;
      }
      return name;
    },
  },
};
</script>
<style scoped lang="scss">
.tab-wrap {
  padding: 20px;
  .tab-title {
    font-size: 13px;
    color: #999;
    padding-bottom: 10px;
  }
  .tree {
    font-size: 13px;
    .item {
      display: flex;
      align-items: center;
    }
    .line {
      width: 4px;
      height: 14px;
      display: inline-block;
      margin-right: 10px;
    }
  }
}
</style>
