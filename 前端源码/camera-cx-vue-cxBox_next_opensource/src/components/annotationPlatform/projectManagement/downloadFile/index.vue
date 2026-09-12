<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('applicationMonitoring.incrementalRraining.exportData')"
      :visible.sync="dialogVisible"
      width="1000px"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" label-width="80px">
          <el-form-item :label="$t('downloadfile.index.8k3wiy')" prop="type">
            <el-radio-group v-model="params.type">
              <el-radio label="1"
                >{{$t('downloadfile.index.3e778r')}}<el-link
                  target="_blank"
                  href="https://github.com/IndustryEssentials/label-free/wiki/VOC%E6%95%B0%E6%8D%AE%E9%9B%86"
                  >{{$t('downloadfile.index.059ic2')}}</el-link
                >
                )</el-radio
              >
              <el-radio label="2"
                >{{$t('downloadfile.index.7379gm')}}<el-link
                  target="_blank"
                  href="https://github.com/IndustryEssentials/label-free/wiki/COCO%E6%95%B0%E6%8D%AE%E9%9B%86"
                  >{{$t('downloadfile.index.pyr0gu')}}</el-link
                >
                )</el-radio
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('downloadfile.index.22277o')" prop="exportImage">
            <el-radio-group v-model="params.exportImage">
              <el-radio-button label="1">{{
                $t("common.yesText")
              }}</el-radio-button>
              <el-radio-button label="0">{{
                $t("common.noText")
              }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <div>
        <el-button type="primary" @click="saveDownload">{{$t('casketmanagement.index.m5ks68')}}</el-button>
        <el-button type="primary" @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </div>
      <div style="margin-top: 20px">
        <el-table :data="tableData" border style="width: 100%">
          <el-table-column align="center" prop="id" :label="$t('downloadfile.index.hdy48g')">
          </el-table-column>
          <el-table-column align="center" prop="type" :label="$t('downloadfile.index.8k3wiy')">
            <template slot-scope="scope">
              <template v-if="scope.row.type == 1">VOC</template>
              <template v-if="scope.row.type == 2">COCO</template>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="exportImage" :label="$t('downloadfile.index.22277o')">
            <template slot-scope="scope">
              <template v-if="scope.row.exportImage == 1">{{
                $t("common.yesText")
              }}</template>
              <template v-if="scope.row.exportImage == 0">{{
                $t("common.noText")
              }}</template>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="createdAt"
            :label="$t('common.createTime')"
          >
          </el-table-column>
          <el-table-column align="center" prop="status" :label="$t('downloadfile.index.8eoxk4')">
            <template slot-scope="scope">
              <template v-if="scope.row.status == 1">{{$t('downloadfile.index.r37el1')}}</template>
              <template v-if="scope.row.status == 0">{{$t('downloadfile.index.44ni15')}}</template>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.action', { text: '' })"
          >
            <template slot-scope="scope">
              <el-button
                type="primary"
                icon="el-icon-download"
                :disabled="scope.row.status == 0"
                @click="dowloadDataReq(scope.row)"
              ></el-button>
              <el-button
                type="danger"
                icon="el-icon-delete"
                :disabled="scope.row.status == 0"
                @click="deleteDownload(scope.row)"
              ></el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  dowloadData,
  dowloadList,
  saveDownload,
  deleteDownload,
} from "@/api/annotationPlatform/projectManagement";
export default {
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      loading: false,
      dialogVisible: true,
      params: {
        projectId: this.currentId,
        type: "1",
        exportImage: "1",
      },
      tableData: [],
      timer: null,
    };
  },
  created() {
    this.dowloadList();
  },
  methods: {
    async dowloadList() {
      const data = await dowloadList({ projectId: this.currentId });
      this.tableData = data.data;
    },
    async saveDownload() {
      clearInterval(this.timer);
      const data = await saveDownload(this.params);
      this.dowloadList();
      this.timer = setInterval(() => {
        this.dowloadList();
      }, 3000);
    },
    async dowloadDataReq(row) {
      // const data = await dowloadData({ id: row.id });
      // var blob = new Blob([data.data], { type: "application/zip" });
      // var url = window.URL.createObjectURL(blob);
      // var linkElement = document.createElement("a");
      // linkElement.setAttribute("href", url);
      // linkElement.setAttribute("downLoad", "download");
      // linkElement.click();


      try {
        // 假设你有一个名为 apiDownloadData 的 API 函数来获取数据
        // 这里需要根据你的实际 API 调用方式修改
        const response = await dowloadData({ id: row.id }); 

        // 检查响应是否成功
        if (!response || !response.data) {
          throw new Error('Download data is empty');
        }

        // 1. 创建 Blob 对象
        // 注意: 如果 response.data 已经是 Blob 或 ArrayBuffer，可能不需要再包装
        // 通常 API 返回的是 ArrayBuffer 用于二进制文件
        const blob = new Blob([response.data], { type: 'application/zip' });

        // 2. 创建下载链接
        const url = window.URL.createObjectURL(blob);
        const linkElement = document.createElement('a');
        linkElement.href = url;

        // 3. 设置下载属性和文件名
        // 从 Content-Disposition 响应头中提取文件名是最佳实践
        let filename = 'download.zip'; // 默认文件名
        const contentDisposition = response.headers['content-disposition'];
        if (contentDisposition) {
          const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
          const matches = filenameRegex.exec(contentDisposition);
          if (matches != null && matches[1]) {
            filename = matches[1].replace(/['"]/g, ''); // 移除引号
          }
        }
        linkElement.download = filename; // 正确的属性名

        // 4. 触发点击事件进行下载
        document.body.appendChild(linkElement); // 需要先将元素添加到 DOM
        linkElement.click();

        // 5. 清理 - 移除元素并释放 URL
        document.body.removeChild(linkElement);
        window.URL.revokeObjectURL(url); // 释放内存

      } catch (error) {
        console.error('Download failed:', error);
        // 给用户提示，例如使用 Element UI 的 Message
        this.$message.error(`Download failed: ${error.message}`);
  
      }
    },
    async deleteDownload(row) {
      await deleteDownload({ id: row.id });
      this.dowloadList();
      this.$message.success(
        $t("button.deleteText", { text: $t("common.success") })
      );
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
