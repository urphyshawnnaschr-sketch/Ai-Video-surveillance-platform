<!--
 * @Author: 张健 327650114@qq.com
 * @Date: 2022-09-30 11:09:07
 * @LastEditors: 张健 327650114@qq.com
 * @LastEditTime: 2022-09-30 11:16:12
 * @FilePath: \gis_front\src\omosViews\plantingReport\components\components\editableCell.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="editable-cell">
    <div class="node-left">
      <div v-if="node.data.editCell" class="editable-cell-input-wrapper">
        <el-input v-model="node.data.name" class="input" /><i
          class="el-icon-check"
          @click="save"
        ></i>
      </div>
      <div v-else class="editable-cell-text-wrapper">
        {{ node.data.name }}
      </div>
    </div>
    <div class="node-right">
      <i class="el-icon-edit" @click="editLabel"></i>
      <i class="el-icon-minus" @click="deleteLabel"></i>
      <i v-if="node.level < 3" class="el-icon-plus" @click="addLabel"></i>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    currentNode: Object,
    labelList: Array,
  },
  data() {
    return {
      allList: [],
      node: {},
      type: "",
    };
  },
  watch: {
    currentNode: {
      immediate: true,
      deep: true,
      handler(val) {
        this.node = val;
      },
    },
    labelList: {
      immediate: true,
      deep: true,
      handler(val) {
        this.allList = this.$common.translateTreeToData(val);
      },
    },
  },
  methods: {
    // 保存字段
    save() {
      if (!this.node.data.name) {
        return this.$message.error(this.$t('components.edittreecell.4ej716'));
      }
      const list = this.node.parent.data.children
        ? this.node.parent.data.children
        : this.node.parent.data;
      let count = 0;
      list.forEach((item) => {
        if (item.name == this.node.data.name) {
          count++;
        }
      });
      if (count > 1) {
        return this.$message.error(this.$t('components.edittreecell.8ojpx7'));
      }
      this.node.data.editCell = false;
    },
    // 点击修改
    editLabel() {
      // 如果有标签为编辑状态，阻止操作
      if (this.handleAllList()) {
        return;
      }
      this.node.data.editCell = true;
    },
    // 点击添加
    addLabel() {
      // 如果有标签为编辑状态，阻止操作
      if (this.handleAllList()) {
        return;
      }
      this.$emit("handleNode", "add", this.node);
    },
    deleteLabel() {
      this.$emit("handleNode", "delete", this.node);
    },
    handleAllList() {
      let status = false;
      this.allList.forEach((item) => {
        if (item.editCell) {
          status = true;
        }
      });
      return status;
    },
  },
};
</script>

<style lang="scss" scoped>
.editable-cell {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .input {
    margin-right: 10px;
  }
}

::v-deep .el-input {
  width: 120px;
  margin-bottom: 0 !important;
}

::v-deep .el-input__inner {
  height: 24px;
  line-height: 24px;
}
</style>
