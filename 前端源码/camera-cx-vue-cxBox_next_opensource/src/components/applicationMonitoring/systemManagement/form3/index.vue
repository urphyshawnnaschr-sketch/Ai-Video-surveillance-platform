<template>
  <div>
    <el-form ref="form3" :model="form3" label-width="180px">
      <el-form-item :label="$t('form3.index.6486h1')">
        <el-input
          v-model="form3.appName"
          :placeholder="$t('form3.index.68l1cj')"
        ></el-input>
      </el-form-item>
      <el-form-item :label="$t('form3.index.lu2mbd')">
        <el-upload
          ref="upload1"
          action=""
          :http-request="handleUploadImg1"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.gif"
          style="display: inline"
        >
          <div v-if="showLogoUrl&&!showUploadButton" class="file-img">
            <img
              :src="showLogoUrl"
              class="logo"
              @error="handleImageError" 
            />
            <div class="del-sty">
              <div class="el-icon-delete" @click.stop="delFun()"></div>
            </div>
          </div>
          <el-button type="primary" icon="el-icon-plus" v-else
            >{{$t('form3.index.o1y2s5')}}</el-button
          >
        </el-upload>
      </el-form-item>
      <el-form-item :label="$t('form3.index.y3zl08')">
        <el-input
          v-model="form3.screenName"
          :placeholder="$t('form3.index.y3zl08')"
        ></el-input>
      </el-form-item>
      <el-form-item :label="$t('form3.index.lu2mbd')">
        <el-upload
          ref="upload2"
          action=""
          :http-request="handleUploadImg2"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.gif"
          style="display: inline"
        >
          <div v-if="showScreenLogoUrl&&!showScreenButton" class="file-img">
            <img
              :src="showScreenLogoUrl"
              class="logo"
              @error="handleImageError1" 
            />
            <div class="del-sty">
              <div class="el-icon-delete" @click.stop="delFun1()"></div>
            </div>
          </div>
          <el-button type="primary" icon="el-icon-plus" v-else
            >{{$t('form3.index.o1y2s5')}}</el-button
          >
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmitForm('form3')"
          >{{$t('marktool.annotate.q5fjzf')}}</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { upload } from "@/api/applicationMonitoring/systemManagement";
export default {
  props: {
    form3: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      VUE_APP_API_BASE_URL,
      logoUrl: "",
      screenLogoUrl: "",
      showUploadButton: true,
      showScreenButton: true,
      showLogoUrl: '',
      showScreenLogoUrl: ''
    };
  },
  watch: {
    form3: {
      handler(val) {
        if(val.logoUrl){
          this.showUploadButton = false;
          this.logoUrl = val.logoUrl;
          this.showLogoUrl = `${VUE_APP_API_BASE_URL}/config/upload/stream?file=${val.logoUrl}&t=${new Date().getTime()}`;
        }

        if(val.screenLogoUrl){
          this.showScreenButton = false;
          this.screenLogoUrl = val.screenLogoUrl;
          this.showScreenLogoUrl = `${VUE_APP_API_BASE_URL}/config/upload/stream?file=${val.screenLogoUrl}&t=${new Date().getTime()}`;
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    handleImageError() {
      this.showUploadButton = true;
    },
    handleImageError1() {
      this.showScreenButton = true;
    },
    // 上传
    async handleUploadImg1(files) {
      const file = files.file ? files.file : files[0];
      // 渲染上传文件
      let formData = new FormData();
      formData.append("file", file);
      const res = await upload(formData);
      if(res.code === 0){
        this.logoUrl = res.data;
        this.showLogoUrl = `${VUE_APP_API_BASE_URL}/config/upload/stream?file=${res.data}&t=${new Date().getTime()}`;
        this.showUploadButton = false;
        return;
      }
    },
    // 上传
    async handleUploadImg2(files) {
      const file = files.file ? files.file : files[0];
      // 渲染上传文件
      let formData = new FormData();
      formData.append("file", file);
      const res = await upload(formData);
      if(res.code === 0){
        this.screenLogoUrl = res.data;
        this.showScreenLogoUrl = `${VUE_APP_API_BASE_URL}/config/upload/stream?file=${res.data}&t=${new Date().getTime()}`;
        this.showScreenButton = false;
        return;
      }
    },
    onSubmitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          this.form3.logoUrl = this.logoUrl;
          this.form3.screenLogoUrl = this.screenLogoUrl;
          this.$emit("submitForm", "form3", this.form3);
        } else {
          return false;
        }
      });
    },
    // 删除
    delFun() {
      this.logoUrl = "";
      this.showLogoUrl = "";
      this.showUploadButton = true;
    },
    delFun1() {
      this.screenLogoUrl = "";
      this.showScreenLogoUrl = "";
      this.showScreenButton = true;
    }
  },
};
</script>
<style scoped lang="scss">
.logo {
  width: 80px;
  height: 80px;
  object-fit: cover;
}
.file-img{
  width: 80px;
  height: 80px;
  position: relative;
}
.file-img:hover{
  .del-sty{
      width: 100%;
      height: 100%;
      position: absolute;
      background: rgba(0, 0, 0, 0.40);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #FFF;
      font-size: 20px;
      top: 0px;
  }
}
.del-sty{
    display: none;
}
</style>
