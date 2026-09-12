<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('common.add', { text: $t('common.team') })"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" :rules="rules" label-width="80px">
          <el-form-item :label="$t('teammanagement.index.hf347w')" prop="name">
            <el-input v-model="params.name"></el-input>
          </el-form-item>
          <el-form-item :label="$t('teammanagement.index.358hi5')" prop="description">
            <el-input
              v-model="params.description"
              type="textarea"
              :placeholder="$t('addteam.index.02vnyb')"
              :rows="4"
            ></el-input>
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
import { saveData } from "@/api/annotationPlatform/teamManagement";
export default {
  data() {
    return {
      loading: false,
      dialogVisible: true,
      params: {
        name: "",
        description: "",
      },
      rules: {
        name: [{ required: true, message: this.$t('teammanagement.index.88ppqq'), trigger: "blur" }],
      },
    };
  },
  methods: {
    //
    // 保存账号
    saveData() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          await saveData(this.params);
          this.$message.success(
            $t("common.add", { text: $t("common.success") })
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
