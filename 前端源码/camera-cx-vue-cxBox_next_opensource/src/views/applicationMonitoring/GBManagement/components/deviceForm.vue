<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('common.edit', { text: $t('components.deviceform.iqb8xy') })"
      :visible.sync="dialogVisible"
      width="500px"
      @close="closed"
    >
      <el-form ref="form" :model="form" label-width="120px">
        <el-form-item :label="$t('components.deviceform.566963')" prop="name">
          {{ form.name }}
        </el-form-item>
        <el-form-item :label="$t('components.deviceform.5c9k98')" prop="deviceId">
          {{ form.deviceId }}
        </el-form-item>
        <el-form-item :label="$t('components.deviceform.8h7dks')" prop="streamMode">
          <el-select
            v-model="form.streamMode"
            :placeholder="$t('common.chooseText')"
          >
            <el-option value="UDP">UDP</el-option>
            <el-option value="TCP">TCP</el-option>
            <el-option value="TCP-PASSIVE">TCP-PASSIVE</el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('components.deviceform.g10ufr')" prop="charset">
          <el-select
            v-model="form.charset"
            :placeholder="$t('common.chooseText')"
          >
            <el-option value="GB2312">GB2312</el-option>
            <el-option value="UTF-8">UTF-8</el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closed">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveData" :loading="btnLoading">{{
          $t("button.saveText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { deviceInfo, deviceSave } from "../api";
export default {
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      form: {
        name: "",
        deviceId: "",
        streamMode: "UDP",
        charset: "GB2312",
      },
      btnLoading: false,
    };
  },
  created() {
    if (this.currentId) {
      this.getDeviceInfo();
    }
  },
  methods: {
    async getDeviceInfo() {
      const res = await deviceInfo({ id: this.currentId });
      this.form = res.data;
    },
    closed() {
      this.$emit("closeForm");
    },
    // 保存
    saveData() {
      this.btnLoading = true;
      this.$refs.form.validate((valid) => {
        if (valid) {
          const formData = {
            ...this.form,
            id: this.currentId,
          };
          deviceSave(formData)
            .then((res) => {
              this.$message.success(
                $t("button.saveText", { text: $t("common.success") })
              );
              this.btnLoading = false;
              this.$emit("closeForm");
            })
            .catch(() => {
              this.btnLoading = false;
            });
        } else {
          this.btnLoading = false;
          return false;
        }
      });
    },
  },
};
</script>
<style lang="scss"></style>
