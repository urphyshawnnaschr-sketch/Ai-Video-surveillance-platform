<template>
  <div class="step3">
    <el-form :model="step3Form" label-width="80px">
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
      <el-form-item>
        <el-button
          type="primary"
          :disabled="projectDetail.status == 99"
          @click="updateData"
          >{{ $t("button.saveText", { text: '' }) }}</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { getGroupListData } from "@/api/annotationPlatform/projectManagement";
export default {
  props: {
    projectDetail: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      groupOptions: [],
    };
  },
  watch: {
    projectDetail: {
      immediate: true,
      deep: true,
      handler(val) {
        this.step3Form = val;
      },
    },
  },
  async created() {
    this.getOptions();
  },
  methods: {
    // 获取下拉
    async getOptions() {
      const data1 = await getGroupListData();
      this.groupOptions = data1.data;
    },
    updateData() {
      this.$emit("updateData", this.step3Form);
    },
  },
};
</script>
<style scoped lang="scss">
.step3 {
  width: 600px;
  padding: 20px 0;
  margin: 0 auto;
}
</style>
