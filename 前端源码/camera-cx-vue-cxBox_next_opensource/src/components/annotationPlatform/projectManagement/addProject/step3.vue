<template>
  <div class="step3">
    <el-form :model="step3Form" label-width="80px">
      <el-form-item :label="$t('addproject.step3.bx3e7t')">
        <el-switch
          v-model="step3Form.needReview"
          :active-value="1"
          :inactive-value="0"
        ></el-switch>
        <span style="margin: 0 10px 0 20px">{{$t('addproject.step3.i17c13')}}</span>
        <el-input-number
          v-model="step3Form.reviewRatio"
          :precision="0"
          :min="1"
          :max="100"
        ></el-input-number>
      </el-form-item>
      <el-form-item :label="$t('addproject.step3.1esn65')">
        <el-select
          v-model="step3Form.userGroupId"
          :placeholder="$t('addproject.step3.x4rcu2')"
          style="width: 100%"
          multiple
          clearable
        >
          <el-option
            v-for="(item, index) in groupOptions"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('marktool.annotate.xmb470')" v-if="$parent.$parent.$refs.step1.step1Form.projectType != 1">
        <el-select
          v-model="step3Form.algorithmIds"
          :placeholder="$t('addproject.step3.88599y')"
          style="width: 100%"
          multiple
          clearable
        >
          <el-option
            v-for="(item, index) in algorithmOptions"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { getGroupListData } from "@/api/annotationPlatform/projectManagement";
import { getAlgorithmListData } from "@/api/applicationMonitoring/incrementalRraining";
export default {
  data() {
    return {
      groupOptions: [],
      algorithmOptions: [],
      step3Form: {
        needReview: 1,
        reviewRatio: 100,
        userGroupId: [],
        algorithmIds: [],
      },
      submitData: {},
    };
  },
  async created() {
    this.getOptions();
  },
  methods: {
    // 获取下拉
    async getOptions() {
      const data1 = await getGroupListData();
      this.groupOptions = data1.data;
      const data2 = await getAlgorithmListData();
      this.algorithmOptions = data2.data;
    },
    submit() {
      this.submitData = this.step3Form;
      return true;
    },
  },
};
</script>
<style scoped lang="scss">
.step3 {
  width: 700px;
  padding: 20px 0;
  margin: 0 auto;
}
</style>
