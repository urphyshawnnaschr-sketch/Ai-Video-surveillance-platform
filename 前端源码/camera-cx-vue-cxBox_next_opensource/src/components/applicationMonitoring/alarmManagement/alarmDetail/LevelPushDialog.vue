<template>
  <el-dialog
    :visible.sync="visible"
    :title="$t('systemManage.alarmPushConfiguration.levelPush')"
    :close-on-click-modal="false"
    @close="handleClose"
    width="800px"
  >
    <el-table
      ref="table"
      :data="tableData"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
      :row-class-name="tableRowClassName"
    >
      <!-- 多选列 -->
      <el-table-column
        type="selection"
        width="55"
        align="center"
        :selectable="isSelectable"
      ></el-table-column>

      <!-- 层级列 -->
      <el-table-column
        prop="level"
        :label="$t('systemManage.alarmPushConfiguration.areaLevel')"
        width="100"
        align="center"
      >
      </el-table-column>

      <!-- 内容列 -->
      <el-table-column
        prop="responsiblePerson"
        :label="$t('systemManage.alarmPushConfiguration.configuredResponsiblePerson')"
      >
        <template slot-scope="scope">
          <div class="content-item">{{ scope.row.responsiblePerson }}</div>
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button class="btn" @click="handleCancel">{{
        $t('systemManage.alarmPushConfiguration.cancel')
      }}</el-button>
      <el-button class="btn" type="primary" @click="handleConfirm">{{
        $t('systemManage.alarmPushConfiguration.confirm')
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {
    qryResponsiblePerson,
    sendGroupPushConfigMessage,
  } from '@/api/applicationMonitoring/alarmManagement';
  import { processResponsiblePersons } from './handleTableData';

  export default {
    name: 'LevelPushDialog',
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      cameraId: {
        type: String,
      },
      algorithmId: {
        type: String,
      },
      reportId: {
        type: String,
      },
    },
    data() {
      return {
        tableData: [],
        selectedRows: [], // 存储选中的行数据
        level1: 1,
        level2: 2,
        level3: 3,
        // 用于记录上一次的选中状态，解决循环触发问题
        prevSelection: [],
      };
    },
    watch: {
      visible(val) {
        if (val) {
          this.initTableData();
        } else {
          this.clearSelection();
        }
      },
      cameraId: {
        deep: true,
        immediate: true,
        handler() {
          if (this.visible) {
            this.initTableData();
          }
        },
      },
    },
    methods: {
      async initTableData() {
        const res = await qryResponsiblePerson({
          cameraId: this.cameraId,
          algorithmId: this.algorithmId,
        });

        this.tableData = processResponsiblePersons(res.data);
      },

      // 获取指定层级的行数据
      getLevelRow(level) {
        return this.tableData.find((row) => row.groupLevel === level) || null;
      },

      // 判断行是否可选择
      isSelectable(row) {
        // 如果一级被选中，二级和三级不可选
        if (this.selectedRows.some((r) => r.groupLevel === this.level1)) {
          return row.groupLevel === this.level1;
        }
        // 如果二级被选中，三级不可单独选择
        if (this.selectedRows.some((r) => r.groupLevel === this.level2)) {
          return row.groupLevel !== this.level3;
        }
        return true;
      },

      // 监听选择变化（核心多选逻辑）
      handleSelectionChange(selection) {
        // 避免循环触发
        if (this.isSameSelection(selection, this.prevSelection)) {
          return;
        }

        this.$nextTick(() => {
          const level1Row = this.getLevelRow(this.level1);
          const level2Row = this.getLevelRow(this.level2);
          const level3Row = this.getLevelRow(this.level3);

          // 存储当前选中状态用于判断
          const isLevel1Selected = selection.some((r) => r.groupLevel === this.level1);
          const wasLevel1Selected = this.prevSelection.some((r) => r.groupLevel === this.level1);
          const isLevel2Selected = selection.some((r) => r.groupLevel === this.level2);
          const wasLevel2Selected = this.prevSelection.some((r) => r.groupLevel === this.level2);

          // 处理一级取消选择逻辑：如果之前选中现在取消，同时取消二级和三级
          if (wasLevel1Selected && !isLevel1Selected) {
            if (level2Row) this.$refs.table.toggleRowSelection(level2Row, false);
            if (level3Row) this.$refs.table.toggleRowSelection(level3Row, false);
          }
          // 处理一级选择逻辑：选中一级时自动选中二级和三级
          else if (!wasLevel1Selected && isLevel1Selected) {
            if (level2Row) this.$refs.table.toggleRowSelection(level2Row, true);
            if (level3Row) this.$refs.table.toggleRowSelection(level3Row, true);
          }
          // 处理二级取消选择逻辑：如果之前选中现在取消，同时取消三级
          else if (wasLevel2Selected && !isLevel2Selected && !isLevel1Selected) {
            if (level3Row) this.$refs.table.toggleRowSelection(level3Row, false);
          }
          // 处理二级选择逻辑：选中二级时自动选中三级（一级未选中）
          else if (!wasLevel2Selected && isLevel2Selected && !isLevel1Selected) {
            if (level3Row) this.$refs.table.toggleRowSelection(level3Row, true);
          }

          // 更新选中行数据和上一次选中状态
          this.selectedRows = this.$refs.table.selection;
          this.prevSelection = [...this.selectedRows];
        });
      },

      // 判断两个选择集合是否相同
      isSameSelection(sel1, sel2) {
        if (sel1.length !== sel2.length) return false;
        return sel1.every((item) => sel2.some((item2) => item.id === item2.id));
      },

      // 点击行触发选中/取消（增强多选交互）
      handleRowClick(row, column, event) {
        // 避免点击复选框时重复触发
        if (column.type !== 'selection') {
          this.$refs.table.toggleRowSelection(row);
        }
      },

      // 选中行样式（视觉反馈）
      tableRowClassName({ row }) {
        // 判断当前行是否被选中
        const isSelected = this.selectedRows.some((item) => item.id === row.id);
        return isSelected ? 'selected-row' : '';
      },

      // 确认操作
      async handleConfirm() {
        // 检查是否有选中项
        if (this.selectedRows.length === 0) {
          this.$message.warning(
            this.$t('systemManage.alarmPushConfiguration.pleaseSelectAtLeastOne'),
          );
          return;
        }

        // 提取所有选中行的并合并（处理可能的空值）
        const allIds = this.selectedRows
          .map((row) => row?.ids || '')
          .filter(Boolean)
          .join(',');

        // 检查是否有有效id
        if (!allIds) {
          this.$message.warning(
            this.$t('systemManage.alarmPushConfiguration.noResponsiblePersonCannotPush'),
          );
          return;
        }

        try {
          // 调用接口发送请求
          await sendGroupPushConfigMessage({
            reportId: this.reportId,
            groupPushConfigIds: allIds.split(','),
          });

          // 成功后通知父组件
          this.$emit('confirmed');
          this.$message.success(this.$t('systemManage.alarmPushConfiguration.pushSuccess'));
        } finally {
          this.handleClose();
        }
      },

      // 取消/关闭弹窗
      handleCancel() {
        this.$emit('update:visible', false);
        this.clearSelection();
      },
      handleClose() {
        this.$emit('update:visible', false);
        this.clearSelection();
      },

      // 清空选中状态
      clearSelection() {
        this.$nextTick(() => {
          if (this.$refs.table) {
            this.$refs.table.clearSelection();
          }
          this.selectedRows = [];
          this.prevSelection = [];
        });
      },
    },
  };
</script>

<style scoped>
  ::v-deep .selected-row {
    background-color: #f5f7fa !important;
  }

  ::v-deep .el-table-row.el-table__row--disabled td {
    background-color: #f5f5f5;
    color: #c0c4cc;
  }

  .content-item {
    line-height: 1.6;
    padding: 4px 0;
  }

  ::v-deep .el-table td {
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;
  }

  ::v-deep .el-table-column--selection .cell {
    text-align: center;
  }

  .dialog-footer {
    text-align: right;
  }
  .btn {
    width: 80px;
    height: 32px;
  }
</style>
