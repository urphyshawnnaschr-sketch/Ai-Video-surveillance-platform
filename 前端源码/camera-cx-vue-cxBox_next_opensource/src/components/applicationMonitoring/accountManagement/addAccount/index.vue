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
          <el-form-item :label="$t('common.accountName')" prop="name">
            <el-input
              v-model="params.name"
              :disabled="currentAccount == null ? false : true"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('common.password', { text: $t('addaccount.index.81e2nd') })"
            prop="password"
            v-if="!currentAccount"
          >
            <el-input v-model="params.password"></el-input>
          </el-form-item>
          <!-- <el-form-item label="角色" prop="roleIds">
            <el-select
              style="width: 100%"
              v-model="params.roleIds"
              placeholder="Please select role"
              multiple
            >
              <el-option
                v-for="(item, index) in roleOptions"
                :key="index"
                :label="item.nameCh"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item> -->
          <el-form-item :label="$t('applicationMonitoring.accountmanagement.f2649e')" prop="val">
            <el-radio-group v-model="params.state">
              <el-radio-button label="0">{{$t('applicationMonitoring.accountmanagement.fi166w')}}</el-radio-button>
              <el-radio-button label="1">{{$t('applicationMonitoring.accountmanagement.r4bhe1')}}</el-radio-button>
            </el-radio-group>
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
  getListDataDetail,
  getRoleList,
  saveData,
  updateData,
} from "@/api/applicationMonitoring/accountManagement";
export default {
  props: {
    currentAccount: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      loading: false,
      title: this.currentAccount
        ? $t("common.edit", { text: $t("common.account") })
        : $t("common.add", { text: $t("common.account") }),
      dialogVisible: true,
      roleOptions: [],
      params: {
        id: "",
        name: "",
        account: "",
        password: "",
        state: "0",
        // roleIds: [],
      },
      rules: {
        name: [{ required: true, message: this.$t('teammanagement.teamdetail.8y333y'), trigger: "blur" }],
        password: [
          { required: true, message: this.$t('addaccount.index.26s81i'), trigger: "blur" },
        ],
        // roleIds: [{ required: true, message: "请选择角色", trigger: "blur" }],
      },
      tableData: [],
    };
  },
  async created() {
    // await this.getRoleList();
    if (this.currentAccount) {
      // this.currentAccount.roles.forEach((item) => {
      //   this.params.roleIds.push(item.id);
      // });
      Object.assign(this.params, this.currentAccount);
    }
  },
  methods: {
    async getRoleList() {
      const data = await getRoleList();
      this.roleOptions = data.data;
    },
    // 获取账号详情
    async getListDataDetail() {
      const data = await getListDataDetail({ id: this.currentAccount.id });
      Object.assign(this.params, {
        name: data.data.name,
        account: data.data.account,
        password: data.data.password,
        state: data.data.state,
      });
    },
    //
    // 保存账号
    saveData() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.params.account = this.params.name;
          if (this.currentAccount) {
            delete this.params.password;
            await updateData(this.params);
          } else {
            await saveData(this.params);
          }
          this.$message.success(
            this.currentAccount
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
