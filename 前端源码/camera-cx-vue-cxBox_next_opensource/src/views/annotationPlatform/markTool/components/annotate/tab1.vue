<template>
  <div class="tab-wrap">
    <div class="tab-title">{{ $t("annotate.tab1.oz2x77") }}</div>
    <div class="tree">
      <el-tree
        :data="newAttributeList"
        :props="defaultProps"
        node-key="name"
        default-expand-all
        :expand-on-click-node="false"
        ref="tree"
      >
        <span slot-scope="{ node, data }">
          <span v-if="data.children.length">{{ data.name }}</span>
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
  },
  data() {
    return {
      defaultProps: {
        children: "children",
        label: "name",
      },
      newAttributeList: [],
      attributeArr: [], // 平铺所有可选标签
      tagName: "", // 一级标签名
      name: "", // 选中标签值
      tempTagArr: [], // 选中的所有节点
      labelTree: [], // 返回给后端的
    };
  },
  watch: {
    attributeList: {
      deep: true,
      handler(val) {
        this.newAttributeList = JSON.parse(JSON.stringify(val));
        // 将所有的末级数据拼成数组并将第一个设置为默认值
        this.treeToArray(this.newAttributeList);
        this.attributeArr.forEach((item, index) => {
          item.color = colorList[index];
        });

        this.$nextTick(() => {
          this.changeAttribute(
            this.attributeArr[0].name,
            this.$refs.tree.getNode(this.attributeArr[0].name)
          );
        });
      },
    },
    name: {
      deep: true,
      handler(val) {
        const obj = this.attributeArr.find((i) => i.name == val);
        this.$emit("changeAttribute", {
          ...obj,
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
      this.name = val;
    },
    treeToArray(tree) {
      tree.forEach((item) => {
        if (item.children.length) {
          this.treeToArray(item.children);
        } else {
          this.attributeArr.push(item);
        }
      });
    },
    treeToArrayByFather(item) {
      this.tempTagArr.push(item.data);
      if (this.$common.checkIsJSON(item.parent.data)) {
        this.treeToArrayByFather(item.parent);
      }
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
  }
}
</style>
