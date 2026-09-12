<template><div class="step4"><el-form
      ref="step4Form"
      :model="step4Form"
      :rules="step4Rules"
      label-width="80px"
    ><!--<el-form-item label="Import Annotation"> <el-radio-group v-model="step4Form.importAnnotation"> <el-radio-button label="1"> Contain Annotation Info </el-radio-button> <el-radio-button label="2"> not Contain Annotation Info </el-radio-button> </el-radio-group> </el-form-item>--><el-form-item :label="$t('faceControl.faceRecognition.uploadFiles')" prop="dataFile"><el-upload
          action=""
          :http-request="handleUploadFile"
          :show-file-list="false"
          accept=".zip"
          style="display: inline"
        ><el-button type="primary" icon="el-icon-plus">{{$t('faceControl.faceRecognition.uploadFiles')}}</el-button></el-upload>{{step4Form.dataFile}}</el-form-item><el-form-item><div class="demo"><p>{{$t('addproject.step4.552mc8')}}</p><p>{{$t('addproject.step4.j27i23')}}</p><p>{{$t('addproject.step4.3778o3')}}</p><p>{{$t('addproject.step4.2t65ru')}}</p></div></el-form-item><el-form-item :label="$t('addproject.step4.70gtj4')"><el-upload
          action=""
          :http-request="handleUploadDemo"
          :show-file-list="false"
          accept=".doc,.docx"
          style="display: inline"
        ><el-button type="primary" icon="el-icon-plus">{{$t('addproject.step4.6p8cbx')}}</el-button></el-upload>{{step4Form.demoFile}}</el-form-item><el-form-item><div class="demo"><p>{{$t('addproject.step4.noowxf')}}</p><p>{{$t('addproject.step4.wx1p4g')}}</p></div></el-form-item></el-form></div></template>
<script>
import {
  getNextId,
  uploadDataset,
  uploadDemo,
} from "@/api/annotationPlatform/projectManagement";
export default {
  data() {
    return {
      step4Form: {
        id: "",
        importAnnotation: 1,
        dataFile: "",
        demoFile: "",
        dataFileId: '',
        docFileId: '',
      },
      step4Rules: {
        dataFile: [
          { required: true, message: this.$t('addproject.step4.0j1942'), trigger: "change" },
        ],
      },
      submitData: {},
    };
  },
  async created() {},
  mounted() {},
  methods: {
    async handleUploadFile(files) {
      this.$emit("changeLoading", true);
      const file = files.file;
      if (!this.step4Form.id) {
        const data = await getNextId();
        this.step4Form.id = data.data;
      }
      const chunkList = [];
      const chunkSize = 5 * 1024 * 1024;
      const chunkCount = Math.ceil(file.size / chunkSize);
      let current = 0;
      let fileName = file.name;
      while (current < chunkCount) {
        chunkList.push({
          chunk: file.slice(current * chunkSize, (current + 1) * chunkSize),
          index: current,
        });
        current++;
      }
      for (let i = 0; i < chunkList.length; i++) {
        const item = chunkList[i];
        let a = new window.File(
          [item.chunk],
          `${file.name}_${item.index}.tmp`,
          { type: "application/x-zip-compressed" }
        );
        let form = new FormData();
        form.append("file", a);
        form.append("projectId", this.step4Form.id);
        form.append("end", item.index == chunkCount - 1 ? 1 : 0);
        const data = await uploadDataset(form);
        this.step4Form.dataFileId = data.data
      }
      this.$emit("changeLoading", false);
      this.step4Form.dataFile = fileName;
      console.info(this.step4Form);
      this.$message.success(this.$t('addproject.index.92j503'));
    },
    async handleUploadDemo(files) {
      this.$emit("changeLoading", true);
      const file = files.file;
      if (!this.step4Form.id) {
        const data = await getNextId();
        this.step4Form.id = data.data;
      }
      let form = new FormData();
      form.append("file", file);
      form.append("projectId", this.step4Form.id);
      form.append("fileType", 3);
      const data = await uploadDemo(form);
      this.$emit("changeLoading", false);
      this.step4Form.demoFile = file.name;
      this.step4Form.docFileId = data.data;
      this.$message.success(this.$t('addproject.index.92j503'));
    },
    async submit() {
      try {
        await this.$refs.step4Form.validate();
        this.submitData = this.step4Form;
        return true;
      } catch (error) {
        this.$message.error(this.$t('addproject.step1.7w0zd6'));
        return false;
      }
    },
  },
};
</script>
<style scoped lang="scss">
.step4 {
  width: 700px;
  padding: 20px 0;
  margin: 0 auto;
  .demo {
    img {
      width: 100%;
      display: block;
      margin: 10px 0;
    }
    p {
      line-height: 20px;
      margin: 0;
      &.first {
        margin-top: 20px;
      }
    }
  }
}
</style>
