<template>
  <div class="tab-wrap">
    <el-form :model="form" :rules="rules" ref="ruleForm">
      <el-form-item prop="issue">
        <el-select
          v-model="form.issue"
          multiple
          filterable
          allow-create
          :placeholder="$t('annotate.tab3.68883c')"
          style="width: 100%"
        >
          <el-option
            :label="$t('annotate.tab3.546057')"
            :value="$t('annotate.tab3.546057')"
          ></el-option>
          <el-option
            :label="$t('annotate.tab3.1g178c')"
            :value="$t('annotate.tab3.1g178c')"
          ></el-option>
          <el-option
            :label="$t('annotate.tab3.62p86w')"
            :value="$t('annotate.tab3.62p86w')"
          ></el-option>
          <el-option
            :label="$t('annotate.tab3.2x51yq')"
            :value="$t('annotate.tab3.2x51yq')"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="text">
        <el-input
          v-model="form.text"
          type="textarea"
          :placeholder="$t('annotate.tab3.ent3s5')"
          maxlength="64"
        ></el-input
      ></el-form-item>
    </el-form>
    <el-button v-if="$route.query.type == 2" type="primary" @click="submit">{{
      $t("annotate.tab3.3x6s2h")
    }}</el-button>
  </div>
</template>

<script>
export default {
  props: {
    currentImgId: {
      type: String,
      default: "",
    },
    formData: {
      type: Object,
      default: () => null,
    },
  },
  data() {
    return {
      form: {
        issue: [],
        text: "",
      },
      rules: {
        issue: [
          {
            required: true,
            message: this.$t("annotate.tab3.68883c"),
            trigger: "blur",
          },
        ],
      },
    };
  },
  watch: {
    currentImgId: {
      immediate: true,
      deep: true,
      handler(val) {
        if (this.formData?.comment) {
          const formData = JSON.parse(this.formData.comment);
          Object.assign(this.form, formData);
        } else {
          this.$nextTick(() => {
            this.$refs.ruleForm.resetFields();
          });
        }
      },
    },
  },

  methods: {
    submit() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.$emit("submit", this.form);
        }
      });
    },
  },
};
</script>
<style scoped lang="scss">
.tab-wrap {
  padding: 20px;

  .issue-list {
    line-height: 24px;
    font-size: 13px;
    margin-top: 20px;

    li {
      border-top: 1px #c6c7c9 solid;
      padding: 10px;
      cursor: pointer;

      &.active {
        background: rgba($color: #E53935, $alpha: 0.2);
      }
    }
  }
}
</style>
