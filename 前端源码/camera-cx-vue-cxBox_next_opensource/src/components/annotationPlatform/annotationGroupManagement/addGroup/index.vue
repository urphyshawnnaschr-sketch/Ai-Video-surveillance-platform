<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" :rules="rules" label-width="80px">
          <el-form-item :label="$t('annotationplatform.annotationgroupmanagement.k78318')" prop="name">
            <el-input v-model="params.name"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveData">{{
          $t("button.submitText")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  saveData,
  updateData,
} from "@/api/annotationPlatform/annotationGroupManagement";
export default {
  props: {
    currentEditGroup: Object,
  },
  data() {
    return {
      loading: false,
      title: this.currentEditGroup
        ? $t("common.edit", { text: $t("common.group") })
        : $t("common.add", { text: $t("common.group") }),
      dialogVisible: true,
      params: {
        name: "",
      },
      rules: {
        name: [{ required: true, message: this.$t('addgroup.index.52vjy1'), trigger: "blur" }],
      },
    };
  },
  async created() {
    Object.assign(this.params, this.currentEditGroup);
  },
  methods: {
    //
    // 保存账号
    saveData() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          if (this.currentEditGroup) {
            await updateData(this.params);
          } else {
            await saveData(this.params);
          }

          this.$message.success(
            this.currentEditGroup
              ? $t("common.edit", { text: $t("common.success") })
              : $t("common.add", { text: $t("common.success") })
          );
          this.dialogVisible = false;
        } else {
          return false;
        }
      });
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
