<template>
  <el-dialog
    :title="$t('noticemanagement.cameraSensorConfiguration.dataImport')"
    :visible.sync="visible"
    width="40%"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <!-- 下载表单模板区域 -->
    <div class="import-section">
      <div class="section-header">
        <div class="vertical-line"></div>
        <h3>{{ $t('noticemanagement.cameraSensorConfiguration.downloadFormTemplate') }}</h3>
      </div>
      <div class="tip-area">
        <p>{{ $t('noticemanagement.cameraSensorConfiguration.importOnlyFirstSheet') }}</p>
        <p>{{ $t('noticemanagement.cameraSensorConfiguration.doNotModifyTableHeader') }}</p>
      </div>
      <a href="javascript:;" class="download-link" @click="downloadTemplate">
        <i class="el-icon-download"></i>
        {{ $t('noticemanagement.cameraSensorConfiguration.downloadBatchImportTemplate') }}
      </a>
    </div>

    <!-- 上传文件区域 -->
    <div class="import-section">
      <div class="section-header">
        <div class="vertical-line"></div>
        <h3>{{ $t('noticemanagement.cameraSensorConfiguration.uploadFile') }}</h3>
      </div>
      <div class="tip-area">
        <p>{{ $t('noticemanagement.cameraSensorConfiguration.allowFileFormat') }}</p>
        <p>{{ $t('noticemanagement.cameraSensorConfiguration.maxDataCountPerFile') }}</p>
        <p>{{ $t('noticemanagement.cameraSensorConfiguration.maxFileSize') }}</p>
      </div>
      <el-upload
        class="upload-component"
        action=""
        :auto-upload="false"
        :on-change="handleFileChange"
        :file-list="fileList"
        :limit="1"
        :accept="'.xls,.xlsx'"
      >
        <a href="javascript:;" class="upload-link" slot="trigger">
          <i class="el-icon-upload2"></i>
          {{ $t('noticemanagement.cameraSensorConfiguration.upload') }}
        </a>
      </el-upload>
    </div>

    <div slot="footer">
      <el-button type="primary" @click="handleImport">
        {{ $t('noticemanagement.cameraSensorConfiguration.confirm') }}
      </el-button>
      <el-button @click="handleClose">
        {{ $t('noticemanagement.cameraSensorConfiguration.cancel') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
  import { downloadSensorTemplate, importSensorData } from './sensorApi';

  export default {
    name: 'CustomImportDialog',
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        fileList: [],
        currentFile: null,
      };
    },
    methods: {
      // 下载模板
      async downloadTemplate() {
        try {
          const response = await downloadSensorTemplate();
          this.handleFileDownload(response, 'Batch Template.xlsx');
        } catch (error) {
          this.$message.error(
            $t('noticemanagement.cameraSensorConfiguration.templateDownloadFailed'),
          );
          console.error('Template error:', error);
        }
      },

      // 处理文件下载
      handleFileDownload(response, fileName) {
        const blob = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },

      // 文件选择变化
      handleFileChange(file, fileList) {
        this.fileList = fileList.slice(-1);
        this.currentFile = file.raw;
      },

      // 确认导入
      async handleImport() {
        if (!this.currentFile) {
          this.$message.warning(
            $t('noticemanagement.cameraSensorConfiguration.pleaseSelectFileToImport'),
          );
          return;
        }

        const formData = new FormData();
        formData.append('file', this.currentFile);

        try {
          await importSensorData(formData);
          await this.$message.success(
            $t('noticemanagement.cameraSensorConfiguration.importSuccess'),
          );
          this.$emit('import-success');
          this.handleClose();
        } catch (error) {
          console.log(error, error);
        }
      },

      // 关闭弹窗
      handleClose() {
        this.fileList = [];
        this.currentFile = null;
        this.$emit('close');
      },
    },
  };
</script>

<style scoped>
  :deep(.el-dialog__body) {
    padding: 0px 20px !important;
  }

  .import-section {
    margin-bottom: 20px;
  }

  /* 区域头部（蓝色竖线+加粗标题） */
  .section-header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }

  .vertical-line {
    width: 4px;
    height: 20px;
    background-color: #E53935;
    margin-right: 8px;
  }

  .section-header h3 {
    font-size: 16px;
    font-weight: bold;
    margin: 0;
  }

  /* 提示区域（蓝底灰字） */
  .tip-area {
    background-color: #eef1f8;
    padding: 12px;
    margin-bottom: 10px;
    color: #666;
    font-size: 12px;
  }

  /* 下载链接样式 */
  .download-link {
    color: #E53935;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    font-size: 14px;
    text-decoration: none;

    .el-icon-download {
      margin-right: 5px;
    }
  }

  /* 上传链接样式 */
  .upload-link {
    color: #E53935;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    font-size: 14px;
    text-decoration: none;
    .el-icon-upload2 {
      margin-right: 5px;
    }
  }
</style>
