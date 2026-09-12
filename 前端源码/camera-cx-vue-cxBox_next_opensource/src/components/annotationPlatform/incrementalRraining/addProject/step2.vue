<template>
  <div class="step2">
    <el-form :model="step2Form" label-width="120px">
      <el-form-item :label="$t('addproject.step2.y4opyv')">
        <el-select
          v-model="step2Form.project"
          :placeholder="$t('annotationplatform.statisticspage.712fyp')"
          @change="changeProject"
          style="width: 100%"
          clearable
        >
          <el-option
            v-for="(item, index) in projectOptions"
            :key="index"
            :label="item.projectName"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('marktool.annotate.cot77w')">
        <el-input
          v-model.trim="step2Form.label"
          :placeholder="$t('addproject.step2.11t35w')"
        >
          <el-button type="primary" slot="append" @click="addTopLabel">{{
            $t("addproject.step2.459446")
          }}</el-button>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-tree
          :data="labelList"
          :props="defaultProps"
          :node-key="Math.random() + ''"
          default-expand-all
          :expand-on-click-node="false"
          ref="tree"
        >
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <EditTree-cell
              ref="editTreeCell"
              :labelList="labelList"
              :currentNode="node"
              @handleNode="handleNode"
            />
          </span>
        </el-tree>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { getProjectListData } from "@/api/annotationPlatform/projectManagement";
import { defaultLabel } from "@/utils/commonData";
import EditTreeCell from "@/components/editTreeCell";
export default {
  components: {
    EditTreeCell,
  },
  data() {
    return {
      projectOptions: [],
      labelList: defaultLabel,
      defaultProps: {
        children: "children",
        label: "name",
      },
      step2Form: {
        project: "",
        label: "",
      },
      submitData: [],
    };
  },
  created() {
    this.getProjectListData();
  },
  methods: {
    // 获取项目列表
    async getProjectListData() {
      const data = await getProjectListData();
      this.projectOptions = data.data;
    },
    changeProject(val) {
      if (val) {
        const obj = this.projectOptions.find((item) => item.id == val);
        const labelArr = [];
        obj.labels.forEach((item) => {
          if (typeof item) {
            labelArr.push(JSON.parse(item));
          } else {
            labelArr.push(item);
          }
        });
        this.labelList = labelArr;
      } else {
        this.labelList = defaultLabel;
      }
    },
    // 添加一级标签
    addTopLabel() {
      if (!this.step2Form.label) {
        return this.$message.error(this.$t("addproject.step2.3675ee"));
      }
      let status = false;
      this.labelList.forEach((item) => {
        if (item.name == this.step2Form.label) {
          status = true;
        }
      });
      if (status) {
        return this.$message.error(this.$t("addproject.step2.l90c4t"));
      }
      this.labelList.push({
        name: this.step2Form.label,
        children: [],
      });
      this.step2Form.label = "";
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

    submit() {
      let status = false;
      const labelList = this.$common.translateTreeToData(this.labelList);
      labelList.forEach((item) => {
        if (item.editCell) {
          status = true;
        }
      });
      if (!this.labelList.length) {
        this.$message.error(this.$t("addproject.step2.epv5kl"));
        return false;
      }
      if (status) {
        this.$message.error(this.$t("addproject.step2.h7vn78"));
        return false;
      }
      this.submitData = this.labelList;
      return true;
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
