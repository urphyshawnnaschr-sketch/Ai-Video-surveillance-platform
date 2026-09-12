<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('common.password', { text: $t('components.editpassword.233cje') })"
      :visible.sync="dialogVisible"
      width="500px"
      @closed="closed"
    >
      <div style="font-size: 12px; color: #939393; margin-bottom: 10px">{{$t('components.editpassword.utu3wc')}}</div>
      <el-form ref="form" :model="params" :rules="rules" label-width="80px">
        <el-form-item :label="$t('common.accountName')">
          <el-input v-model="currentName" disabled></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('common.password', { text: $t('addaccount.index.81e2nd') })"
          prop="password"
        >
          <el-input v-model="params.password"></el-input>
          <div style="font-size: 12px; color: #939393">{{$t('components.editpassword.p05m5w')}}</div>
        </el-form-item>
      </el-form>
      <div>
        <el-button type="primary" @click="saveData">{{
          $t("button.saveText", { text: "" })
        }}</el-button>
        <el-button @click="closed">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { updatePassword } from "../api";
export default {
  props: {
    currentId: {
      type: String,
      default: null,
    },
    currentName: {
      type: String,
      default: "",
    },
  },
  data() {
    let editpasswordErr  = this.$t('components.editpassword.vl95j6');
    let inputPasswordTxt = $t("form.tip.inputPassword");
    return {
      dialogVisible: true,
      params: {
        password: "",
      },
      rules: {
        password: [
          {
            required: true,
            message: inputPasswordTxt,
            trigger: "blur",
          },
          {
            validator: function (rule, value, callback) {
              const regex =
                /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[\W_]).{8,26}$/;
              if (!regex.test(value)) {
                callback(new Error(editpasswordErr));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
      },
    };
  },
  async created() {},
  methods: {
    // 新增
    saveData() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          // let obj = {
          //     id:this.currentId,
          //     password: this.params.password,
          // }
          let formData = new FormData();
          formData.append("accountId", this.currentId);
          formData.append("password", this.params.password);
          const res = await updatePassword(formData);
          this.$message.success(
            $t("button.saveText", { text: $t("common.success") })
          );
          this.$emit("close");
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
