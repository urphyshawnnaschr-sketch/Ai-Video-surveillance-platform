<template>
  <div class="step1">
    <el-form
      ref="step1Form"
      :model="step1Form"
      :rules="step1Rules"
      label-width="80px"
    >
      <el-form-item :label="$t('projectmanagement.index.e313kw')" prop="projectName">
        <el-input
          v-model.trim="step1Form.projectName"
          :placeholder="$t('addproject.index.03kd42')"
          maxlength="64"
          show-word-limit
        ></el-input>
      </el-form-item>
      <el-form-item :label="$t('addproject.step1.1db8gj')" prop="projectDetail">
        <el-input
          v-model="step1Form.projectDetail"
          type="textarea"
          :placeholder="$t('addproject.index.4i77d8')"
          maxlength="256"
          show-word-limit
          :rows="4"
        ></el-input>
      </el-form-item>
      <el-form-item :label="$t('addproject.step4.70gtj4')">
        <el-upload
          action=""
          :http-request="handleUploadDemo"
          :show-file-list="false"
          accept=".doc,.docx"
          style="display: inline"
        >
          <el-button type="primary" icon="el-icon-plus">{{$t('addproject.step4.6p8cbx')}}</el-button>
        </el-upload>
        {{ step1Form.demoFile }}
      </el-form-item>
      <el-form-item>
        <div class="demo">
          <p>{{$t('addproject.step4.noowxf')}}</p>
          <p>{{$t('addproject.step4.wx1p4g')}}</p>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :disabled="projectDetail.status == 99" @click="updateData">{{ $t('button.saveText', { text: '' }) }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import {
  uploadDemo,
} from "@/api/annotationPlatform/projectManagement";
export default {
  props: {
    projectDetail: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      step1Form: {},
      step1Rules: {
        projectName: [
          { required: true, message: this.$t('addproject.index.03kd42'), trigger: "blur" },
        ],
      },
      submitData: {},
    };
  },
  watch: {
    projectDetail: {
      immediate: true,
      deep: true,
      handler(val) {
        this.step1Form = val;
      },
    },
  },
  async created() {},
  methods: {
    async handleUploadDemo(files) {
      console.info(files)
      const file = files.file;
      let form = new FormData();
      form.append("file", file);
      form.append("projectId", this.step1Form.id);
      form.append("fileType", 3);
      await uploadDemo(form);
      this.$set(this.step1Form,'demoFile',file.name)
      this.$message.success(this.$t('addproject.index.92j503'));
    },
    updateData() {
      delete this.step1Form.docFileId
      delete this.step1Form.docPath
      this.$emit('updateData',this.step1Form)
    },
  },
};
</script>
<style scoped lang="scss">
.step1 {
  width: 600px;
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
    }
  }
}
</style>
