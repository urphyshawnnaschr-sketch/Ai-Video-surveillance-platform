<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('common.edit', { text: $t('components.form.atp064') })"
      :visible.sync="dialogVisible"
      width="40%"
      @close="closed"
    >
      <el-form ref="form" :model="form" label-width="140px">
        <el-form-item :label="$t('components.form.nqv7b4')" prop="name">
          <el-input v-model="form.name"></el-input>
          <span class="form-tip">{{$t('components.form.6ucwq3')}}</span>
        </el-form-item>
        <el-form-item :label="$t('common.ipAddress')" prop="ip">
          <el-input v-model="form.ip"></el-input>
        </el-form-item>
        <el-form-item :label="$t('components.form.y5j625')" prop="httpPort">
          <el-input v-model="form.httpPort"></el-input>
        </el-form-item>
        <el-form-item :label="$t('components.form.zs45z4')" prop="rtspPort">
          <el-input v-model="form.rtspPort"></el-input>
        </el-form-item>
        <el-form-item :label="$t('components.form.76g178')" prop="rtcPort">
          <el-input v-model="form.rtcPort"></el-input>
        </el-form-item>
        <el-form-item :label="$t('components.form.q51n33')" prop="rtpPortRange">
          <el-input v-model="form.rtpPortRange"></el-input>
          <span class="form-tip"
            >{{$t('components.form.ax72ey')}}</span
          >
        </el-form-item>
        <el-form-item :label="$t('components.form.6tpmss')" prop="sendRtpPortRange">
          <el-input v-model="form.sendRtpPortRange"></el-input>
          <span class="form-tip"
            >{{$t('components.form.ax72ey')}}</span
          >
        </el-form-item>
        <el-form-item
          :label="$t('common.password', { text: '' })"
          prop="secret"
        >
          <el-input v-model="form.secret"></el-input>
        </el-form-item>
        <!-- <el-form-item
          :label="$t('components.form.y572uc', { text: '' })"
          prop="nodeType"
        >
          <el-select
            :placeholder="$t('common.chooseText')"
            clearable
            filterable
            v-model="form.nodeType"
          >
            <el-option value="0" :label="$t('components.form.y572u1')"></el-option>
            <el-option value="1" :label="$t('components.form.y572u2')"></el-option>
            <el-option value="2" :label="$t('components.form.y572u3')"></el-option>
          </el-select>
        </el-form-item> -->
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
import { mediaServerInfo, mediaServerSave } from "../api";
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
      form: {},
      btnLoading: false,
    };
  },
  created() {
    if (this.currentId) {
      this.getMediaServerInfo();
    }
  },
  methods: {
    async getMediaServerInfo() {
      const res = await mediaServerInfo({ id: this.currentId });
      this.form = res.data;
      // this.form.nodeType = String(this.form.nodeType);
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
          mediaServerSave(formData)
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
<style lang="scss">
.form-tip {
  font-size: 12px;
  color: #999;
}
</style>
