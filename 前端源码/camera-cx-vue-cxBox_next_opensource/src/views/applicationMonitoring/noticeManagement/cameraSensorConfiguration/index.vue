<template>
  <div class="sensor-camera-config">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>{{ $t('noticemanagement.cameraSensorConfiguration.cameraSensorConfiguration') }}</h2>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
      <!-- 表格头部：搜索区 + 操作按钮 -->
      <div class="table-header">
        <div class="search-container">
          <el-select
            v-model="searchParams.cameraId"
            :placeholder="$t('noticemanagement.cameraSensorConfiguration.pleaseEnterCameraId')"
            filterable
            class="search-item"
            clearable
          >
            <el-option
              v-for="item in cameraListOptions"
              :key="item.cameraId"
              :label="item.cameraName"
              :value="item.cameraId"
            >
            </el-option>
          </el-select>
          <el-input
            v-model="searchParams.sensorCode"
            :placeholder="$t('noticemanagement.cameraSensorConfiguration.pleaseEnterSensorCode')"
            class="search-item"
            clearable
          ></el-input>
          <el-input
            v-model="searchParams.sensorName"
            :placeholder="$t('noticemanagement.cameraSensorConfiguration.pleaseEnterSensorName')"
            class="search-item"
            clearable
          ></el-input>

          <div style="display: flex; justify-content: flex-end">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">{{
              $t('button.queryText')
            }}</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">{{
              $t('button.resetText', { text: '' })
            }}</el-button>
          </div>
        </div>

        <div class="table-actions">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
            {{ $t('noticemanagement.cameraSensorConfiguration.add') }}
          </el-button>
          <el-button type="info" icon="el-icon-download" @click="dialogImportVisible = true">
            {{ $t('noticemanagement.cameraSensorConfiguration.importData') }}
          </el-button>
          <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
            {{ $t('noticemanagement.cameraSensorConfiguration.batchDelete') }}
          </el-button>
        </div>
      </div>

      <!-- 表格内容 - 使用动态高度 -->
      <el-table
        :data="tableData"
        border
        style="width: 100%; margin-top: 15px"
        v-loading="tableLoading"
        @selection-change="handleSelectionChange"
        row-key="sensorCode"
        ref="tableRef"
      >
        <el-table-column type="selection" width="100" align="left"></el-table-column>
        <el-table-column
          prop="sensorCode"
          :label="$t('noticemanagement.cameraSensorConfiguration.sensorCode')"
          align="left"
          min-width="100"
        >
        </el-table-column>
        <el-table-column
          prop="sensorName"
          :label="$t('noticemanagement.cameraSensorConfiguration.sensorName')"
          align="left"
          min-width="100"
        >
        </el-table-column>
        <el-table-column
          prop="cameraId"
          :label="$t('noticemanagement.cameraSensorConfiguration.cameraId')"
          align="left"
          min-width="100"
        >
        </el-table-column>
        <el-table-column
          prop="cameraName"
          :label="$t('noticemanagement.cameraSensorConfiguration.cameraName')"
          align="left"
          min-width="180"
        >
        </el-table-column>
        <el-table-column
          :label="$t('noticemanagement.cameraSensorConfiguration.action')"
          align="left"
          width="100"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="handleEdit(scope.row)">
              {{ $t('noticemanagement.cameraSensorConfiguration.edit') }}
            </el-button>
            <el-button
              type="text"
              size="mini"
              style="color: #f56c6c"
              @click="handleDelete(scope.row.sensorCode)"
            >
              {{ $t('noticemanagement.cameraSensorConfiguration.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          background
          :current-page="pagination.pageNum"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <SensorFormDialog
      :visible="dialogVisible"
      :is-edit="isEditMode"
      :form-data="formData"
      @close="dialogVisible = false"
      @submit="handleDialogSubmit"
      :cameraListOptions="cameraListOptions"
    />
    <ImportFileButtonDialog
      :visible="dialogImportVisible"
      @close="dialogImportVisible = false"
      @import-success="fetchTableData"
    />
  </div>
</template>

<script>
  import {
    getSensorPage,
    addSensorConfig,
    updateSensorConfig,
    batchDeleteSensor,
  } from './sensorApi';
  import { cameraList } from '@/api/annotationPlatform/mapImageManagent';

  import ImportFileButtonDialog from './ImportFileButtonDialog.vue';
  import SensorFormDialog from './SensorFormDialog.vue';

  export default {
    name: 'SensorCameraConfig',
    components: {
      SensorFormDialog,
      ImportFileButtonDialog,
    },
    data() {
      return {
        // 表格数据
        tableData: [],
        tableLoading: false,
        selectedIds: [], // 批量选择的ID
        // 新增导入弹窗控制
        dialogImportVisible: false,

        // 搜索参数
        searchParams: {
          sensorCode: '',
          sensorName: '',
          cameraId: '',
        },

        // 分页参数
        pagination: {
          pageNum: 1,
          pageSize: 10,
          total: 0,
        },

        // 弹窗相关
        dialogVisible: false,
        isEditMode: false,
        formData: {
          sensorCode: '',
          sensorName: '',
          cameraId: null,
        },
        cameraListOptions: [],
      };
    },
    created() {
      this.initBaseData();
      this.fetchTableData();
    },
    methods: {
      // 获取表格数据
      async fetchTableData() {
        this.tableLoading = true;
        try {
          const params = {
            ...this.searchParams,
            pageNum: this.pagination.pageNum,
            pageSize: this.pagination.pageSize,
          };
          const response = await getSensorPage(params);
          /** 后端系统配置问题，前端妥协 */
          this.tableData = response.data.records.map((row) => {
            return {
              ...row,
              cameraId: row.cameraId === 0 ? null : row.cameraId,
            };
          });
          this.pagination.total = Number(response.data.total) || 0;
        } catch (error) {
          this.$message.error('Load Data');
          console.error('Get Table Data:', error);
        } finally {
          this.tableLoading = false;
        }
      },

      // 搜索
      handleSearch() {
        this.pagination.pageNum = 1;
        this.fetchTableData();
      },

      // 重置搜索
      resetSearch() {
        this.searchParams = {
          sensorCode: '',
          sensorName: '',
          cameraId: '',
        };
        this.pagination.pageNum = 1;
        this.pagination.pageSize = 10;
        this.fetchTableData();
      },

      // 分页大小改变
      handleSizeChange(size) {
        this.pagination.pageSize = size;
        this.pagination.pageNum = 1;
        this.fetchTableData();
      },

      // 页码改变
      handleCurrentChange(page) {
        this.pagination.pageNum = page;
        this.fetchTableData();
      },

      // 选择项改变
      handleSelectionChange(selection) {
        this.selectedIds = selection.map((item) => item.sensorCode);
      },

      async initBaseData() {
        try {
          const [res] = await Promise.all([
            cameraList({
              pageNum: 1,
              pageSize: 1000,
            }),
          ]);

          this.cameraListOptions = res.data || [];
        } catch (error) {
          console.error('Initialization API：', error);
        }
      },
      // 新增
      handleAdd() {
        this.isEditMode = false;
        this.formData = {
          sensorCode: '',
          sensorName: '',
          cameraId: null,
        };
        this.dialogVisible = true;
      },

      // 编辑
      handleEdit(row) {
        this.isEditMode = true;
        this.formData = { ...row };
        this.dialogVisible = true;
      },

      // 单个删除
      async handleDelete(sensorCode) {
        try {
          await this.$confirm(
            this.$t('noticemanagement.cameraSensorConfiguration.confirmDeleteSingleConfig'),
            this.$t('noticemanagement.cameraSensorConfiguration.confirmOperation'),
            {
              confirmButtonText: this.$t('noticemanagement.cameraSensorConfiguration.confirm'),
              cancelButtonText: this.$t('noticemanagement.cameraSensorConfiguration.cancel'),
              type: 'warning',
            },
          );
          const sensorCodeList = [sensorCode];
          await batchDeleteSensor(sensorCodeList);
          this.$message.success(
            this.$t('noticemanagement.cameraSensorConfiguration.deleteSuccess'),
          );
          this.fetchTableData();
        } catch (error) {
          console.log(error, 'error');
        }
      },

      // 批量删除
      async handleBatchDelete() {
        if (this.selectedIds.length === 0) {
          this.$message.info($t('noticemanagement.cameraSensorConfiguration.noSelectedItem'));
          return;
        }

        try {
          // 等待用户确认
          await this.$confirm(
            $t('noticemanagement.cameraSensorConfiguration.confirmBatchDeleteConfig', {
              length: this.selectedIds.length,
            }),
            $t('noticemanagement.cameraSensorConfiguration.confirmOperation'),
            {
              confirmButtonText: $t('noticemanagement.cameraSensorConfiguration.confirm'),
              cancelButtonText: $t('noticemanagement.cameraSensorConfiguration.cancel'),
              type: 'warning',
            },
          );

          await batchDeleteSensor(this.selectedIds);
          this.$message.success(
            $t('noticemanagement.cameraSensorConfiguration.batchDeleteSuccess'),
          );
          this.selectedIds = [];
          this.fetchTableData();
        } catch (error) {
          // 区分取消操作和实际错误
          if (error === 'cancel') {
            this.$refs.tableRef.clearSelection();
            this.selectedIds = [];
          }
        }
      },

      // 处理弹窗提交
      async handleDialogSubmit(formData) {
        try {
          if (this.isEditMode) {
            await updateSensorConfig(formData);
            this.$message.success($t('noticemanagement.cameraSensorConfiguration.editSuccess'));
          } else {
            await addSensorConfig(formData);
            this.$message.success($t('noticemanagement.cameraSensorConfiguration.addSuccess'));
          }
          this.dialogVisible = false;
          this.fetchTableData();
        } catch (error) {
          console.log(error, 'error');
        }
      },
    },
  };
</script>

<style scoped lang="scss">
  .sensor-camera-config {
    background-color: #fff;
    width: 100%;
    height: 100%;
    box-sizing: border-box;

    .page-title {
      padding: 16px;

      h2 {
        margin: 0;
        font-size: 20px;
        color: #333;
      }
    }

    .table-card {
      padding: 0px 16px;
      background-color: #fff;

      .table-header {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: center;
      }

      .search-container {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        align-items: flex-end;
        justify-content: flex-end;

        .search-item {
          width: 170px;
        }
      }

      .table-actions {
        display: flex;
        gap: 0px;
      }

      .pagination-container {
        margin-top: 15px;
        text-align: right;
        padding: 10px 0;
      }
    }
  }
  :deep(.el-button--info) {
    color: rgb(94, 94, 94);
    background-color: rgb(230, 230, 230);
    border-color: rgb(230, 230, 230);
  }
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
</style>
