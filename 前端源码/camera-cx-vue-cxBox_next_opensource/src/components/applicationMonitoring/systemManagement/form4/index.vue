<template>
  <div>
    <el-form ref="form4" :model="form4" label-width="180px">
      <el-form-item :label="$t('form4.index.k6v8gf')">
        <el-upload
          action=""
          :http-request="handleUploadImg"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.gif"
          style="display: inline"
        >
          <div v-if="form4.screenLogoUrl && !showUploadButton" class="file-img">
            <img
              :src="VUE_APP_API_BASE_URL + form4.screenLogoUrl"
              class="img"
              @error="handleImageError"
            />
            <div class="del-sty">
              <div class="el-icon-delete" @click.stop="delFun()"></div>
            </div>
          </div>
          <!-- <el-button type="primary" icon="el-icon-plus" v-else>上传</el-button> -->
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="SubmitForm('form4')">{{
          $t("button.submitText")
        }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import {
  upload,
  saveAfterSalesConfig,
  getAfterSales,
} from "@/api/applicationMonitoring/systemManagement";
export default {
  data() {
    return {
      VUE_APP_API_BASE_URL,
      form4: {
        screenLogoUrl: "",
      },
      url: "",
      showUploadButton: false,
    };
  },
  created() {
    this.getData();
  },
  methods: {
    handleImageError() {
      this.showUploadButton = true;
    },
    // 获取数据
    async getData() {
      const res = await getAfterSales({ tag: "afterSalesQRCodes" });
      if (res.code == 0) {
        if (res.data) {
          this.url = res.data;
          this.form4.screenLogoUrl = "/config/upload/stream?file=" + res.data;
        }
      }
    },
    // 上传
    async handleUploadImg(files) {
      const file = files.file ? files.file : files[0];
      // 渲染上传文件
      let formData = new FormData();
      formData.append("file", file);
      const data = await upload(formData);
      this.url = data.data;
      this.form4.screenLogoUrl = "/config/upload/stream?file=" + data.data;
    },
    // 保存
    async SubmitForm() {
      // if(!this.url){
      //     this.$message.waring("请上传");
      //     return;
      // }
      await saveAfterSalesConfig({ afterSalesQRCodes: this.url });
      this.$message.success(
        $t("button.saveText", { text: $t("common.success") })
      );
      this.$store.commit("setAfterSalesUrl", this.url);
    },
    delFun() {
      this.url = "";
      this.form4.screenLogoUrl = "";
    },
  },
};
</script>
<style scoped lang="scss">
.img {
  width: 150px;
  height: 150px;
  object-fit: cover;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 150px;
  height: 150px;
  line-height: 150px;
  text-align: center;
  border: 1px dashed #d9d9d9;
}
.file-img {
  width: 150px;
  height: 150px;
  position: relative;
}
.file-img:hover {
  .del-sty {
    width: 100%;
    height: 100%;
    position: absolute;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 20px;
    top: 0px;
  }
}
.del-sty {
  display: none;
}
</style>
