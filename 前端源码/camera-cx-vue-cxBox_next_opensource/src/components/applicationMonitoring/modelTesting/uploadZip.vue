<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('modeltesting.uploadzip.2ge2s7')"
      :visible.sync="dialogVisible"
      width="700px"
      append-to-body
      :before-close="handleDialogClose"
    >
      <el-row :gutter="16">
        <el-col :span="8">
          <p style="color: #1A0808; font-size: 12px; margin-bottom: 7px">
            {{ $t("modeltesting.uploadzip.h380w0") }}
          </p>
          <p style="color: #1A0808; font-size: 13px; margin-bottom: 16px">
            {{ $t("modeltesting.uploadzip.nncz5n") }}
          </p>
          <p style="color: #1A0808; font-size: 12px; margin-bottom: 7px">
            {{ $t("modeltesting.uploadzip.6s5890") }}
          </p>
          <p style="color: #1A0808; font-size: 13px; margin-bottom: 16px">
            chaoxing-people-1.0.zip
          </p>
          <p style="color: #1A0808; font-size: 12px; margin-bottom: 0px">
            {{ $t("modeltesting.uploadzip.ve4i6r") }}
          </p>
          <div class="pkg-txt">
            <i class="el-icon-first-aid-kit"></i>
            <span class="txt">xxx-xxx-1.0.zip</span>
          </div>
          <div class="fil-txt">- libxxx.so</div>
          <div class="fil-txt">- VERSION.txt</div>
          <div class="fil-txt">{{ $t("modeltesting.uploadzip.34vr5h") }}</div>
        </el-col>
        <el-col :span="16">
          <p>{{ `${$t("common.algorithm")}：${algoName}` }}</p>
          <div
            v-for="(item, index) in platformList"
            :key="index"
            style="margin-bottom: 35px"
          >
            <el-divider content-position="left"
              ><span style="color: #1A0808"
                >{{ $t("modeltesting.addalgorithm.eg4662") }}{{ item }}</span
              ></el-divider
            >

            <el-upload
              class="upload-demo"
              action=""
              :data="{
                platform: item,
              }"
              :file-list="fileList[item]"
              :on-change="
                (file, fileList) => handleChange(file, fileList, item)
              "
              :http-request="handleUpload"
              accept=".zip"
            >
              <el-button size="small" type="primary">{{
                $t("button.clickToUpload")
              }}</el-button>
            </el-upload>
          </div>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>
<script>
import { getPlatform } from "@/api/applicationMonitoring/algorithmManagement";
import { uploadAlgorithmFile } from "@/api/applicationMonitoring/modelTesting";
export default {
  props: {
    algoName: {
      type: String,
      default: "",
    },
    algoCode: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      platformList: [],
      fileList: {},
      uploading: false,
      progressTimer: null,
      progressNum: 0,
    };
  },
  created() {
    // 获取平台列表
    this.getPlatformList();
  },
  beforeDestroy() {
    // 清除定时器
    if (this.progressTimer) {
      clearInterval(this.progressTimer);
      this.progressTimer = null;
    }
  },
  methods: {
    handleDialogClose() {
      this.$emit("closeImport");
    },
    closed() {
      this.$emit("closeImport");
    },
    // 获取平台列表
    getPlatformList() {
      getPlatform().then((res) => {
        this.platformList = res.data;
        this.platformList.forEach((item) => {
          this.$set(this.fileList, item, []);
        });
      });
    },
    //
    handleChange(file, fileList, platform) {
      if (this.uploading) {
        this.$message.info(this.$t("modeltesting.uploadzip.42okhm"));
        return false;
      }
      this.fileList[platform] = fileList;
    },
    // 上传文件
    handleUpload(file) {
      // 同时只允许一个文件上传
      if (this.uploading) {
        this.$message.info(this.$t("modeltesting.uploadzip.42okhm"));
        return;
      }
      this.uploading = true;

      // 修改文件状态为上传中
      this.fileList[file.data.platform].forEach((item) => {
        if (item.uid == file.file.uid) {
          item.status = "uploading";
        }
      });

      // 清除之前的进度条
      this.progressNum = 0;
      if (this.progressTimer) {
        clearInterval(this.progressTimer);
        this.progressTimer = null;
      }

      // 模拟上传进度
      this.progressTimer = setInterval(() => {
        // 每次进度条 +7
        this.progressNum += 7;
        // 限制进度条最大值为90
        if (this.progressNum >= 90) {
          clearInterval(this.progressTimer);
          this.progressTimer = null;
          this.progressNum = 0;
        }
        // 更新文件列表中的进度条状态
        this.fileList[file.data.platform].forEach((item) => {
          if (item.uid == file.file.uid) {
            item.percent = this.progressNum;
          }
        });
      }, 500);

      let formData = new FormData();
      formData.append("platform", file.data.platform);
      formData.append("nameEn", this.algoCode);
      formData.append("file", file.file);
      uploadAlgorithmFile(formData)
        .then((res) => {
          //   this.btnLoading = false;
          this.$message.success(this.$t("addproject.index.92j503"));
          this.uploading = false;

          // 清除进度条
          if (this.progressTimer) {
            clearInterval(this.progressTimer);
            this.progressTimer = null;
          }

          // 修改文件状态为成功
          this.fileList[file.data.platform].forEach((item) => {
            if (item.uid == file.file.uid) {
              item.status = "success";
            }
          });
        })
        .catch(() => {
          this.uploading = false;
          // 上传错误，删除文件
          let idx = this.fileList[file.data.platform].findIndex((item) => {
            return item.uid == file.file.uid;
          });
          if (idx > -1) {
            this.fileList[file.data.platform].splice(idx, 1);
          }
        });
    },
  },
};
</script>
<style scoped lang="scss">
.title-sty {
  font-size: 13px;
  color: #1A0808;
  font-weight: bold;
  margin-bottom: 20px;
}
.form-tip {
  font-size: 12px;
  color: #1A0808;
}

.pkg-txt {
  margin-top: 7px;
  display: flex;
  flex-direction: row;
  align-items: center;

  .txt {
    margin-left: 8px;
    margin-top: -2px;
  }
}
.fil-txt {
  padding-left: 14px;
  margin-top: 7px;
  font-size: 12px;
}
</style>
