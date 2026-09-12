<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('feishu.esb_config.0e5yvd')"
      :visible.sync="dialogVisible"
      width="50%"
      @close="closed"
    >
      <el-form ref="form" :model="params" :rules="rules" label-width="120px">
        <el-form-item :label="$t('feishu.esb_config.u5152g')" prop="esbBaseUrl">
          <el-input v-model="params.esbBaseUrl" placeholder=""></el-input>
        </el-form-item>
        <el-form-item :label="$t('feishu.esb_config.2jv1zs')" prop="esbTokenUrl">
          <el-input v-model="params.esbTokenUrl" placeholder=""></el-input>
        </el-form-item>
        <el-form-item :label="$t('feishu.esb_config.deu4t4')" prop="esbUploadUrl">
          <el-input v-model="params.esbUploadUrl" placeholder=""></el-input>
        </el-form-item>
        <el-form-item :label="$t('feishu.esb_config.86bo1r')" prop="esbSendUrl">
          <el-input v-model="params.esbSendUrl" placeholder=""></el-input>
        </el-form-item>
        <el-form-item label="appKey" prop="esbAppKey">
          <el-input v-model="params.esbAppKey" placeholder=""></el-input>
        </el-form-item>
        <el-form-item label="AK" prop="esbAk">
          <el-input v-model="params.esbAk" placeholder=""></el-input>
        </el-form-item>
        <el-form-item label="SK" prop="esbSk">
          <el-input v-model="params.esbSk" placeholder=""></el-input>
        </el-form-item>
        <el-form-item :label="$t('feishu.esb_config.bc16c2')" prop="esbFsTemplatId">
          <el-input v-model="params.esbFsTemplatId" placeholder=""></el-input>
        </el-form-item>
        <el-form-item :label="$t('feishu.esb_config.4e2242')" prop="esbFsClientId">
          <el-input v-model="params.esbFsClientId" placeholder=""></el-input>
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
import { esbInfo, esbSave } from "../../api.js";
export default {
  data() {
    return {
      dialogVisible: true,
      params: {
        esbBaseUrl: "",
        esbTokenUrl: "",
        esbUploadUrl: "",
        esbSendUrl: "1",
        esbAppKey: "",
        esbAk: "",
        esbSk: "",
        esbFsTemplatId: "",
        esbFsClientId: "",
      },
      rules: {
        esbBaseUrl: [
          { required: true, message: this.$t('feishu.esb_config.5ym694'), trigger: "blur" },
        ],
        esbTokenUrl: [
          { required: true, message: this.$t('feishu.esb_config.j5kdrk'), trigger: "blur" },
        ],
        esbUploadUrl: [
          { required: true, message: this.$t('feishu.esb_config.2479t7'), trigger: "blur" },
        ],
        esbSendUrl: [
          { required: true, message: this.$t('feishu.esb_config.r1tkwk'), trigger: "blur" },
        ],
        esbAppKey: [
          { required: true, message: this.$t('feishu.esb_config.518k6x'), trigger: "blur" },
        ],
        esbAk: [{ required: true, message: this.$t('feishu.esb_config.7p2582'), trigger: "blur" }],
        esbSk: [{ required: true, message: this.$t('feishu.esb_config.7y556v'), trigger: "blur" }],
        esbFsTemplatId: [
          { required: true, message: this.$t('feishu.esb_config.n3houy'), trigger: "blur" },
        ],
        esbFsClientId: [
          { required: true, message: this.$t('feishu.esb_config.54w97u'), trigger: "blur" },
        ],
      },
      btnLoading: false,
    };
  },
  created() {
    this.getEsbInfo();
  },
  methods: {
    getEsbInfo() {
      esbInfo().then((res) => {
        this.params = res.data;
      });
    },
    closed() {
      this.$emit("closeEsb");
    },
    // 保存
    saveData() {
      this.btnLoading = true;
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.params.type = 0; // feishu
          esbSave(this.params)
            .then((res) => {
              this.$message.success(
                $t("button.saveText", { text: $t("common.success") })
              );
              this.btnLoading = false;
              this.$emit("closeEsb");
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
