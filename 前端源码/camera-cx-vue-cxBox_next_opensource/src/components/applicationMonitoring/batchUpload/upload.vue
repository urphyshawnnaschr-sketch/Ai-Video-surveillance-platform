<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('batchupload.upload.48vg72')"
      :visible.sync="dialogVisible"
      width="800px"
      top="8vh"
      @close="closed"
    >
      <!-- 初始化 -->
      <div class="upload-cont">
        <div class="left">
          <div class="top-title">{{ $t("batchupload.upload.44mllg") }}</div>
          <div class="tip">
            {{ $t("batchupload.upload.613sa4") }}<br />{{
              $t("batchupload.upload.58wrm4")
            }}<br />{{ $t("batchupload.upload.a67eak") }}
          </div>
          <div class="download-file">
            <div class="file-name">{{ $t("batchupload.upload.p7h47h") }}</div>
            <div class="download-btn" @click="downLoadFun">
              {{ $t("modeltesting.modeltesting.39e71x") }}
            </div>
          </div>
        </div>
        <div class="right">
          <div>
            <div class="top-title">
              {{ $t("faceControl.faceRecognition.uploadFiles") }}
            </div>
            <div class="tip">{{ $t("batchupload.upload.f0unpn") }}</div>
          </div>
          <div class="center">
            <el-upload
              action=""
              :auto-upload="false"
              :on-remove="handleRemove"
              :on-change="handleChange"
              accept=".xlsx"
              :file-list="fileList"
              multiple
              :disabled="isUpload"
              style="display: inline"
            >
              <el-button icon="el-icon-upload2">{{
                $t("batchupload.upload.p820x7")
              }}</el-button>
            </el-upload>
          </div>
          <div class="center">
            <el-button
              v-if="isShowBtn"
              type="primary"
              @click.stop="handleUpload"
              :loading="uploadLoading"
              >{{ $t("batchupload.upload.7873eq") }}</el-button
            >
            <div v-if="isImportBtn">
              <el-button type="primary" @click.stop="startVerification">{{
                $t("batchupload.upload.s63286")
              }}</el-button>
              <el-button @click="closed">{{
                $t("button.cancelText", { text: "" })
              }}</el-button>
            </div>
            <div v-if="isShowInfo">
              <div
                v-if="stateObj.status == 1 || status == 1"
                style="display: flex; justify-content: space-between"
              >
                <div style="width: 100%">
                  <el-progress :percentage="stateObj.percentage"></el-progress>
                </div>
              </div>
              <div
                class="error-file"
                v-if="stateObj.status == 3 || status == 3"
              >
                <div style="color: red">
                  {{ stateObj.text ? stateObj.text : typeText }}
                </div>
                <div style="display: flex">
                  <div class="download-btn" @click="downLoadFile">
                    {{ $t("modeltesting.modeltesting.39e71x") }}
                  </div>
                  <div
                    class="download-btn"
                    style="margin-left: 10px"
                    @click="deleteFun"
                  >
                    {{ $t("annotationplatform.incrementalrraining.5ms4b7") }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  deleteAll,
  uploads,
  statusByTag,
  exportData,
} from "@/api/applicationMonitoring/batchUpload";
export default {
  components: {},
  props: {
    status: {
      type: Number,
      default: null,
    },
    typeText: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      fileList: [],
      VUE_APP_API_BASE_URL,
      isShowBtn: false, //上传按钮展示
      timerNum: null,
      stateObj: {},
      isUpload: false,
      isImportBtn: false, //导入按钮的展示
      isShowInfo: false,
      uploadLoading: false,
    };
  },
  created() {
    if (this.status == 1) {
      this.isShowInfo = true;
      this.countNum();
      this.timerNum = setInterval(this.countNum, 3000);
    } else if (this.status == 3) {
      this.isShowInfo = true;
    }
  },
  methods: {
    closed() {
      this.$emit("close");
    },
    // 选择文件时，往里添加
    handleChange(file, fileList) {
      console.log(file);
      this.fileList = fileList;
      if (fileList.length > 0) {
        this.isShowBtn = true;
      }
    },
    // 上传
    handleUpload() {
      this.uploadLoading = true;
      let formData = new FormData();
      this.fileList.forEach((file) => {
        formData.append("files", file.raw);
      });
      uploads(formData)
        .then((res) => {
          this.uploadLoading = false;
          this.$store.commit("setTagInfo", res.data.tag);
          this.isUpload = true;
          this.isShowBtn = false;
          this.isImportBtn = true;
        })
        .catch((res) => {
          this.uploadLoading = false;
        });
    },
    handleRemove(file, fileList) {
      this.fileList = fileList;
      if (fileList.length == 0) {
        this.isShowBtn = false;
      }
    },

    // 开始导入
    startVerification() {
      this.isImportBtn = false;
      // 定时刷新统计数据
      this.countNum();
      this.timerNum = setInterval(this.countNum, 3000);
    },
    // 下载模板
    downLoadFun() {
      const url =
        VUE_APP_API_BASE_URL + "/camera/batch/import/downloadTemplateFile";
      const link = document.createElement("a");
      link.href = url;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    // 下载报错文件
    downLoadFile() {
      let params = {
        tag: this.$store.state.tagInfo,
      };
      exportData(params).then((res) => {
        var blob = new Blob([res.data], { type: "application/zip" });
        var url = window.URL.createObjectURL(blob);
        var linkElement = document.createElement("a");
        linkElement.setAttribute("href", url);
        linkElement.setAttribute(
          "downLoad",
          this.$t("batchupload.upload.7x2xud")
        );
        linkElement.click();
        document.body.removeChild(linkElement);
      });
      this.clearData();
    },
    // 查询统计数据
    countNum() {
      statusByTag({ tag: this.$store.state.tagInfo }).then((res) => {
        let data = res.data;
        this.stateObj = data;
        this.isShowInfo = true;
        if (data.percentage == 100) {
          clearInterval(this.timerNum);
          this.timerNum = null;
        }
        if (data.status == 2) {
          this.$message.success(data.text);
          this.isUpload = false;
          this.fileList = [];
        }
      });
    },
    // 清除
    deleteFun() {
      deleteAll().then((res) => {
        this.clearData();
      });
    },
    clearData() {
      this.isShowInfo = false;
      this.isUpload = false;
      this.fileList = [];
      this.status = 0;
      this.$store.commit("setTagInfo", "");
    },
  },
  beforeDestroy() {
    if (this.timerNum) {
      clearInterval(this.timerNum);
      this.timerNum = null;
    }
  },
};
</script>
<style scoped lang="scss">
.upload-cont {
  display: flex;
  justify-content: space-between;
  .left,
  .right {
    width: 49%;
    border-radius: 6px;
    border: 1px solid #dcdcdc;
    padding: 16px;
    .top-title {
      font-size: 16px;
      font-weight: 500;
      line-height: 22px;
      color: #000;
    }
    .tip {
      margin-top: 8px;
      color: rgba(0, 0, 0, 0.4);
      font-size: 12px;
      font-style: normal;
      font-weight: 400;
      line-height: 20px;
      letter-spacing: 0.5px;
    }
  }
  .left {
    .download-file {
      display: flex;
      justify-content: space-between;
      padding-top: 100px;
      font-size: 13px;
      .file-name {
        color: #000;
      }
      .download-btn {
        color: #E53935;
        cursor: pointer;
      }
    }
  }
  .right {
    .center {
      text-align: center;
      width: 100%;
      margin-top: 60px;
    }
    .fail-tip {
      color: #d54941;
      font-size: 12px;
      margin-top: 10px;
    }
    .file-sty {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 70%;
      margin-left: 15%;
      font-size: 12px;
      margin-top: 10px;
    }
    .error-file {
      display: flex;
      justify-content: space-between;
      .download-btn {
        color: #E53935;
        cursor: pointer;
      }
    }
  }
}
</style>
