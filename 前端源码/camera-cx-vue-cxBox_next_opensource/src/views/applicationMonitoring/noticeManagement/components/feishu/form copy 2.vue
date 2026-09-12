<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="50%"
      @close="closed"
    >
      <el-form ref="form" :model="params" :rules="rules" label-width="120px">
        <el-form-item :label="$t('dingding.form.j76q5x')" prop="name">
          <el-input
            v-model="params.name"
            :placeholder="$t('dingding.form.412xwj')"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('dingding.form.rih8yz')" prop="webhook">
          <el-input
            v-model="params.webhook"
            :placeholder="$t('feishu.form copy 2.2q3mhy')"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('dingding.form.i8hni5')" prop="signature">
          <el-input
            v-model="params.signature"
            :placeholder="$t('feishu.form copy 2.sypm6r')"
          ></el-input>
        </el-form-item>
        <el-form-item label="APP ID" prop="appId">
          <el-input
            v-model="params.appId"
            :placeholder="$t('feishu.form copy 2.nsve22')"
          ></el-input>
        </el-form-item>
        <el-form-item label="APP Secret" prop="appSecret">
          <el-input
            v-model="params.appSecret"
            :placeholder="$t('feishu.form copy 2.nsve22')"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('dingding.form.pb5d56')" prop="state">
          <el-switch
            v-model="params.state"
            active-value="1"
            inactive-value="0"
            active-color="#13ce66"
            inactive-color="#ff4949"
          >
          </el-switch>
        </el-form-item>
        <el-form-item :label="$t('common.remark')" prop="remark">
          <el-input
            type="textarea"
            v-model="params.remark"
            :placeholder="$t('dingding.form.2oochb')"
          ></el-input>
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
import { socialHookSave, socialHookInfo } from "../../api.js";
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
      title: this.currentId
        ? $t("common.edit", { text: $t("common.anonymousLetterGroup") })
        : $t("common.add", { text: $t("common.anonymousLetterGroup") }),
      params: {
        name: "",
        webhook: "",
        sign: "",
        state: "1",
        remark: "",
      },
      rules: {
        name: [{ required: true, message: this.$t('dingding.form.28cp35'), trigger: "blur" }],
        webhook: [
          { required: true, message: this.$t('dingding.form.rwps2k'), trigger: "blur" },
        ],
      },
      btnLoading: false,
    };
  },
  created() {
    if (this.currentId) {
      this.getSocailHookInfo();
    }
  },
  methods: {
    getSocailHookInfo() {
      socialHookInfo({ id: this.currentId }).then((res) => {
        this.params = res.data;
        this.params.state = this.params.state == 1 ? "1" : "0";
      });
    },
    closed() {
      this.$emit("closeAdd");
    },
    // 保存
    saveData() {
      this.btnLoading = true;
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.params.type = 0; // feishu
          socialHookSave(this.params)
            .then((res) => {
              this.$message.success(
                $t("button.saveText", { text: $t("common.success") })
              );
              this.btnLoading = false;
              this.$emit("closeAdd");
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
