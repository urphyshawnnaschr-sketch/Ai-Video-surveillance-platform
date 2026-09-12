<template>
  <div class="area-management">
    <!-- 左侧树形区域 -->
    <div class="flex-left">
      <div class="top-title">
        <div class="title">{{ $t('systemManage.alarmPushConfiguration.area') }}</div>
      </div>
      <!-- 树形搜索框 -->
      <el-input
        :placeholder="$t('systemManage.alarmPushConfiguration.pleaseInput')"
        v-model="treeFilterText"
        class="tree-search"
        suffix-icon="el-icon-search"
      ></el-input>
      <!-- 树形组件 -->
      <el-tree
        class="filter-tree"
        :data="areaTreeData"
        :props="treeDefaultProps"
        default-expand-all
        :filter-node-method="filterTreeNode"
        ref="areaTree"
        node-key="id"
        @node-click="handleTreeNodeClick"
        v-model="selectedAreaId"
        :current-node-key="selectedAreaId"
        :highlight-current="true"
      >
        <div class="custom-tree-node" slot-scope="{ node }">
          <div class="node-label">{{ node.label }}</div>
        </div>
      </el-tree>
    </div>

    <!-- 右侧表格区域 -->
    <div class="flex-right">
      <!-- 表格顶部：标题+新增按钮 -->
      <div class="table-header">
        <div class="table-title">{{
          $t('systemManage.alarmPushConfiguration.areaSafetyResponsibilityGridConfig')
        }}</div>
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="handleAddArea"
          :disabled="!selectedAreaId"
          >{{ $t('systemManage.alarmPushConfiguration.add') }}</el-button
        >
      </div>

      <!-- 表格内容 -->
      <el-table
        :data="areaTableData"
        border
        style="width: 100%"
        v-loading="tableLoading"
        :header-cell-style="{ 'text-align': 'left' }"
        :cell-style="{ 'text-align': 'left' }"
        row-key="id"
      >
        <!-- 序号列 -->
        <el-table-column
          align="left"
          :label="$t('systemManage.alarmPushConfiguration.index')"
          width="70"
        >
          <template slot-scope="scope">
            {{ (params.pageNum - 1) * params.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <!-- 区域等级列 -->
        <el-table-column
          align="left"
          prop="groupLevel"
          :label="$t('systemManage.alarmPushConfiguration.areaGrade')"
          width="100"
        >
          <template slot-scope="scope">
            {{
              scope.row.groupLevel == 1
                ? `${$t('systemManage.alarmPushConfiguration.levelOne')}`
                : scope.row.groupLevel == 2
                  ? `${$t('systemManage.alarmPushConfiguration.levelTwo')}`
                  : `${$t('systemManage.alarmPushConfiguration.levelThree')}`
            }}
          </template>
        </el-table-column>
        <!-- 区域名称列 -->
        <el-table-column
          align="left"
          prop="name"
          width="140"
          :label="$t('systemManage.alarmPushConfiguration.areaName')"
        ></el-table-column>
        <!-- 算法列 -->
        <el-table-column
          align="left"
          :label="$t('systemManage.alarmPushConfiguration.associatedAlgorithm')"
          width="250"
        >
          <template slot-scope="scope">
            <el-tag
              type="info"
              size="mini"
              v-for="(item, index) in scope.row.algorithmNames.split(',')"
              :key="index"
              class="algorithm-tag"
              >{{ item }}</el-tag
            >
          </template>
        </el-table-column>
        <!-- 推送接收人列 -->
        <el-table-column
          align="left"
          prop="responsiblePerson"
          width="200"
          :label="$t('systemManage.alarmPushConfiguration.pushRecipient')"
        ></el-table-column>
        <!-- 接收推送群列 -->
        <el-table-column
          align="left"
          prop="socialHookNames"
          width="400"
          :label="$t('systemManage.alarmPushConfiguration.pushGroup')"
        >
        </el-table-column>
        <!-- 操作列（新增编辑按钮） -->
        <el-table-column
          align="left"
          :label="$t('systemManage.alarmPushConfiguration.action')"
          width="100"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              size="mini"
              style="color: #E53935"
              @click="handleEditArea(scope.row)"
              >{{ $t('systemManage.alarmPushConfiguration.edit') }}</el-button
            >
            <el-button
              type="text"
              size="mini"
              style="color: #f56c6c"
              @click="handleDeleteArea(scope.row.id)"
              >{{ $t('systemManage.alarmPushConfiguration.delete') }}</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableTotal"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        ></el-pagination>
      </div>
    </div>

    <AreaFormDialog
      :visible="dialogVisible"
      :is-edit-mode="isEditMode"
      :initial-form="form"
      :algorithm-options="algorithmOptions"
      :person-options="personOptions"
      :social-hook-options="socialHookOptions"
      @submit="handleDialogSubmit"
      @close="dialogVisible = false"
    />
  </div>
</template>

<script>
  import {
    groupTree,
    getAreaTable,
    deleteArea,
    addArea,
    editArea,
    getAlgorithmOptions,
    getPeopleData,
    getSocialHookList,
  } from './api';
  import AreaFormDialog from './AreaFormDialog.vue';

  export default {
    name: 'alarmPushConfiguration',
    components: {
      AreaFormDialog,
    },
    data() {
      return {
        areaTreeData: [],
        treeFilterText: '',
        treeDefaultProps: {
          children: 'children',
          label: 'name',
          id: 'id',
        },
        selectedAreaId: '',
        areaTableData: [],
        tableLoading: false,
        tableTotal: 0,
        params: {
          pageNum: 1,
          pageSize: 20,
          groupId: '',
        },

        dialogVisible: false,
        isEditMode: false,
        form: {
          id: '',
          groupId: '',
          groupLevel: '',
          name: '',
          algorithmIds: [],
          algorithmNames: '',
          responsiblePerson: '',
          responsiblePersonNo: [],
          socialHookIds: [],
          socialHookNames: '',
        },

        // 下拉选项数据
        algorithmOptions: [],
        personOptions: [],
        socialHookOptions: [],
        currentTreeNode: {},
      };
    },
    watch: {
      treeFilterText(val) {
        this.$refs.areaTree?.filter(val);
      },

      selectedAreaId(val) {
        this.form.groupId = val;
        this.params.groupId = val;
      },
    },
    created() {
      this.initBaseData();
    },
    methods: {
      async initBaseData() {
        try {
          const [treeRes, algorithmRes, peopleRes, groupRes] = await Promise.all([
            groupTree(),
            getAlgorithmOptions(),
            getPeopleData({
              pageNum: 1,
              pageSize: 1000,
              departIds: [],
            }),
            getSocialHookList(),
          ]);
          this.areaTreeData = treeRes.data || [];
          // 初始选中第一个树形节点（若有数据）
          if (this.areaTreeData.length > 0) {
            const firstNode = this.areaTreeData[0]; // 获取第一个节点数据
            // 确保树形组件已渲染，再调用 setCurrentNode
            this.$nextTick(() => {
              this.$refs.areaTree?.setCurrentNode(firstNode);
              // 同步选中状态到数据，并加载表格（复用原有逻辑）
              this.handleTreeNodeClick(firstNode);
            });
          }
          this.algorithmOptions = algorithmRes.data || [];
          this.personOptions = peopleRes.data.filter((item) => item.staffNo) || [];
          this.socialHookOptions = groupRes.data;
          // 加载初始表格数据
          this.getAreaTableData();
        } catch (error) {
          console.error('Initialization API：', error);
        }
      },

      // 树形相关方法：节点过滤（按名称模糊匹配）
      filterTreeNode(value, data) {
        if (!value) return true;
        return data.name?.indexOf(value) !== -1;
      },
      // 树形节点点击：更新选中状态并刷新表格
      handleTreeNodeClick(data) {
        this.currentTreeNode = { ...data };
        this.selectedAreaId = data.id;
        this.params.pageNum = 1;
        this.getAreaTableData();
      },

      async getAreaTableData() {
        this.tableLoading = true;
        try {
          const res = await getAreaTable(this.params);
          this.areaTableData = res.data.records || [];
          this.tableTotal = Number(res.data.total) || 0;
        } catch (error) {
          this.$message.error(this.$t('systemManage.alarmPushConfiguration.tableDataLoadFailed'));
          console.error('Table API：', error);
        } finally {
          this.tableLoading = false;
        }
      },
      // 页码切换
      handlePageChange(val) {
        this.params.pageNum = val;
        this.getAreaTableData();
      },
      // 每页条数切换
      handlePageSizeChange(val) {
        this.params.pageSize = val;
        this.params.pageNum = 1; // 切换条数重置页码
        this.getAreaTableData();
      },
      // 删除区域
      async handleDeleteArea(id) {
        try {
          await this.$confirm(
            `${this.$t('systemManage.alarmPushConfiguration.deleteIrrecoverableConfirm')}`,
            `${this.$t('systemManage.alarmPushConfiguration.deleteConfirm')}`,
            {
              confirmButtonText: `${this.$t('systemManage.alarmPushConfiguration.confirm')}`,
              cancelButtonText: `${this.$t('systemManage.alarmPushConfiguration.cancel')}`,
              type: 'warning',
            },
          );
          await deleteArea([id]);
          this.$message.success(this.$t('systemManage.alarmPushConfiguration.deleteSuccess'));
          this.getAreaTableData(); // 刷新表格
        } catch (error) {
          if (error !== 'cancel') {
            this.$message.error(this.$t('systemManage.alarmPushConfiguration.deleteFailedRetry'));
          }
        }
      },

      handleAddArea() {
        this.isEditMode = false;
        const currentNode = this.currentTreeNode;
        this.form = {
          id: '',
          groupId: currentNode.id || '',
          groupLevel: currentNode?.level ?? '',
          name: currentNode?.name ?? '',
          algorithmIds: [],
          algorithmNames: '',
          responsiblePerson: '',
          responsiblePersonNo: [],
          socialHookIds: [],
          socialHookNames: '',
        };
        this.$nextTick(() => {
          this.dialogVisible = true;
        });
      },

      handleEditArea(row) {
        this.isEditMode = true;
        this.form = {
          id: row.id || '',
          groupId: row.groupId || '',
          groupLevel: row.groupLevel,
          name: row.name,
          algorithmIds: row.algorithmIds ? row.algorithmIds.split(',') : [],
          algorithmNames: row.algorithmNames || '',
          responsiblePerson: row.responsiblePerson || '',
          responsiblePersonNo: row.responsiblePersonNo ? row.responsiblePersonNo.split(',') : [],
          socialHookIds: row.socialHookIds ? row.socialHookIds.split(',') : [],
          socialHookNames: row.socialHookNames || '',
        };
        this.dialogVisible = true;
      },

      async handleDialogSubmit(submitData) {
        try {
          if (this.isEditMode) {
            await editArea(submitData);
            this.$message.success(this.$t('systemManage.alarmPushConfiguration.editSuccess'));
          } else {
            await addArea(submitData);
            this.$message.success(this.$t('systemManage.alarmPushConfiguration.addSuccess'));
          }
          this.dialogVisible = false;
          this.getAreaTableData();
        } catch (error) {
          this.$message.error(`${msg}`);
          console.error(`${msg}API：`, error);
        }
      },
    },
  };
</script>

<style scoped lang="scss">
  .area-management {
    display: flex;
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    background-color: #f5f7fa;

    // 一、左侧树形区域样式
    .flex-left {
      min-width: 200px;
      max-width: 400px;
      background: #fff;
      padding: 16px;
      margin-right: 16px;
      flex-shrink: 0;

      // 树形标题栏
      .top-title {
        margin-bottom: 16px;
        .title {
          font-size: 20px;
          font-weight: bold;
          color: #1A0808;
        }
      }

      // 树形搜索框
      .tree-search {
        width: 100%;
        margin-bottom: 12px;
        box-sizing: border-box;
      }

      // 自定义树形节点
      .custom-tree-node {
        display: flex;
        align-items: center;
        width: 100%;
        height: 32px;

        .node-label {
          font-size: 14px;
          color: #303133;
          cursor: pointer;
          transition: color 0.2s;
          &:hover {
            color: #E53935;
          }
        }
      }

      // 树形组件容器（带滚动）
      .filter-tree {
        width: 100%;
        max-height: calc(100vh - 220px);
        overflow-y: auto;
        overflow-x: hidden;

        // 滚动条优化（内核）
        &::-webkit-scrollbar {
          width: 6px;
        }
        &::-webkit-scrollbar-thumb {
          border-radius: 3px;
          background-color: #e5e7eb;
        }
        &::-webkit-scrollbar-track {
          background-color: transparent;
        }
      }
    }

    // 二、右侧表格区域样式
    .flex-right {
      flex: 1;
      min-width: 900px;
      background: #fff;
      padding: 16px;
      box-sizing: border-box;

      // 表格顶部：标题+新增按钮区域
      .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .table-title {
          font-size: 20px;
          font-weight: bold;
          color: #1A0808;
        }

        // 新增按钮禁用状态优化
        .el-button.is-disabled {
          background-color: #eef1f5;
          border-color: #dcdfe6;
          color: #c0c4cc;
          cursor: not-allowed;
        }
      }

      // 表格内算法标签样式
      .algorithm-tag {
        margin-right: 6px;
        margin-bottom: 4px;
      }

      // 分页组件容器
      .pagination {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        margin-top: 16px;
      }

      // 穿透修改表格样式
      :deep(.el-table) {
        // 表头样式优化
        .el-table__header {
          th {
            text-align: left !important;
            background-color: #fafbfc;
            font-weight: 500;
            color: #1A0808;
            border-bottom: 1px solid #e5e7eb;
          }
        }

        // 表格内容样式优化
        .el-table__body {
          td {
            text-align: left !important;
            color: #303133;
            border-bottom: 1px solid #f2f3f5;
          }
        }

        // 表格行效果
        .el-table__row {
          transition: background-color 0.2s;
          &:hover > td {
            background-color: #f9fafb !important;
          }
        }

        // 表格加载状态优化
        .el-table-loading {
          background-color: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }
</style>
