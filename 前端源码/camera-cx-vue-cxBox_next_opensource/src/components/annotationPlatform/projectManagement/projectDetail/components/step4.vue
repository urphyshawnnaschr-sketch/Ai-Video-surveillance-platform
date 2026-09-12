<template>
  <div class="step4">
    <el-form ref="step4Form" :model="step4Form" label-width="120px">
      <el-form-item :label="$t('components.step4.4kre7v')">
        <el-input-number v-model="step4Form.labelTaskTimeOut"></el-input-number>{{$t('components.step4.y302c2')}}</el-form-item>
      <el-form-item :label="$t('components.step4.yjg712')">
        <el-switch
          v-model="step4Form.needAuxiliaryBox"
          :active-value="1"
          :inactive-value="0"
        ></el-switch>
        <span style="margin-left: 20px">{{$t('components.step4.87xuvd')}}</span>
        <el-input-number
          v-model="step4Form.auxiliaryBox.width"
        ></el-input-number>
        <span> px</span>
        <span style="margin-left: 20px">{{$t('components.step4.yirf30')}}</span>
        <el-input-number
          v-model="step4Form.auxiliaryBox.height"
        ></el-input-number>
        <span> px</span>
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
export default {
  props: {
    projectDetail: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      step4Form: {},
    };
  },
  watch: {
    projectDetail: {
      immediate: true,
      deep: true,
      handler(val) {
        this.step4Form = val;
      },
    },
  },
  async created() {},
  mounted() {},
  methods: {
    updateData() {
      this.$emit("updateData", this.step4Form);
    },
  },
};
</script>
<style scoped lang="scss">
.step4 {
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
      &.first {
        margin-top: 20px;
      }
    }
  }
}
</style>
