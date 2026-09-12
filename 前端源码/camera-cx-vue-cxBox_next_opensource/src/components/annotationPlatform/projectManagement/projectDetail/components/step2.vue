<template>
  <div class="step2">
    <el-form label-width="120px">
      <el-form-item :label="$t('marktool.annotate.cot77w')">
        <el-input
          v-model.trim="label"
          :placeholder="$t('addproject.step2.11t35w')"
        >
          <el-button type="primary" slot="append" @click="addTopLabel">{{
            $t("addproject.step2.459446")
          }}</el-button>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-tree
          :data="step2Form.labels"
          :props="defaultProps"
          :node-key="Math.random() + ''"
          default-expand-all
          :expand-on-click-node="false"
          ref="tree"
        >
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <EditTree-cell
              ref="editTreeCell"
              :labelList="step2Form.labels"
              :currentNode="node"
              @handleNode="handleNode"
            />
          </span>
        </el-tree>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :disabled="projectDetail.status == 99"
          @click="updateData"
          >{{ $t("button.saveText", { text: "" }) }}</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import EditTreeCell from "@/components/editTreeCell";
export default {
  components: {
    EditTreeCell,
  },
  props: {
    projectDetail: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      projectOptions: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
      label: "",
      step2Form: {},
    };
  },
  watch: {
    projectDetail: {
      immediate: true,
      deep: true,
      handler(val) {
        this.step2Form = val;
      },
    },
  },
  created() {},
  methods: {
    // 添加一级标签
    addTopLabel() {
      if (!this.label) {
        return this.$message.error(this.$t("addproject.step2.3675ee"));
      }
      let status = false;
      this.step2Form.labels.forEach((item) => {
        if (item.name == this.label) {
          status = true;
        }
      });
      if (status) {
        return this.$message.error(this.$t("addproject.step2.l90c4t"));
      }
      this.step2Form.labels.push({
        name: this.label,
        children: [],
      });
      this.label = "";
    },
    // 添加子标签
    handleNode(type, node) {
      if (type == "add") {
        const newChild = { name: "", editCell: true, children: [] };
        if (!node.data.children) {
          this.$set(node.data, "children", []);
        }
        node.data.children.push(newChild);
      }
      if (type == "delete") {
        const parent = node.parent;
        const children = parent.data.children || parent.data;
        const index = children.findIndex((d) => d.name === node.data.name);
        children.splice(index, 1);
      }
    },

    updateData() {
      this.$emit("updateData", this.step2Form);
    },
  },
};
</script>
<style scoped lang="scss">
.step2 {
  width: 600px;
  padding: 20px 0;
  margin: 0 auto;
}
::v-deep .custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  padding-right: 8px;
  .node-right {
    font-size: 16px;
    i {
      margin: 0 5px;
    }
  }
}
</style>
