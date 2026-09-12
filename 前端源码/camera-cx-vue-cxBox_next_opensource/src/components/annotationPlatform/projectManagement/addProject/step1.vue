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
      <el-form-item :label="$t('projectmanagement.index.vo4q4w')" prop="projectType">
        <el-radio-group v-model="step1Form.projectType">
          <el-radio-button
            :label="item.value"
            v-for="(item, index) in projectTypeOptions"
            :key="index"
            >{{ item.name }}</el-radio-button
          >
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <div class="demo">
          <p>{{$t('addproject.step1.34z0j4')}}</p>
          <img src="@/assets/images/projectManagement/demo.png" />
          <p>{{$t('addproject.step1.1fwek8')}}</p>
          <p>{{$t('addproject.step1.u0l3m2')}}</p>
        </div>
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
    </el-form>
  </div>
</template>
<script>
import { projectType } from "@/utils/commonData";
export default {
  data() {
    return {
      projectTypeOptions: projectType,
      step1Form: {
        projectName: "",
        projectType: 1,
        projectDetail: "",
      },
      step1Rules: {
        projectName: [
          { required: true, message: this.$t('addproject.index.03kd42'), trigger: "blur" },
        ],
        projectType: [
          { required: true, message: this.$t('addproject.index.x48y73'), trigger: "change" },
        ],
      },
      submitData: {},
    };
  },
  async created() {},
  methods: {
    async submit() {
      try {
        await this.$refs.step1Form.validate();
        this.submitData = this.step1Form;
        return true;
      } catch (error) {
        this.$message.error(this.$t('addproject.step1.7w0zd6'))
        return false;
      }
    },
  },
};
</script>
<style scoped lang="scss">
.step1 {
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
    }
  }
}
</style>
