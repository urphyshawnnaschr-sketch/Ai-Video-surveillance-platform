<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      ::title="$t('addphone.index.e2jzm7')"
      :visible.sync="dialogVisible"
      width="400px"
      @closed="closed"
    >
      <div>
        <el-form label-width="80px">
          <el-form-item
            :label="$t('common.add', { text: $t('common.phoneNumber') })"
            prop="name"
          >
            <el-input v-model="params.phone"></el-input>
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
import { saveData } from "@/api/applicationMonitoring/messageManagement";
export default {
  data() {
    return {
      loading: false,
      dialogVisible: true,
      params: {
        phone: "",
      },
    };
  },
  created() {},
  methods: {
    async saveData() {
      const data = await saveData(this.params);
      this.$message.success($t('button.addText', {text: $t('common.success')}));
      this.dialogVisible = false;
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
