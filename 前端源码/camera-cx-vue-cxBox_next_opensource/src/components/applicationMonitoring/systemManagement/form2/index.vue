<template>
  <div>
    <el-form ref="form2" :model="form2" label-width="180px">
      <el-form-item :label="$t('noticemanagement.apimanagement.yrba10')">
        <el-input v-model="form2.reportPushUrl"></el-input>
        <div class="tip__text">{{$t('noticemanagement.apimanagement.798cp8')}}</div>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.apimanagement.ek8rhp')" prop="streamType">
        <el-radio v-model="form2.reportPushImage" label="false"
          >{{$t('noticemanagement.apimanagement.i3y411')}}</el-radio
        >
        <el-radio v-model="form2.reportPushImage" label="true">{{$t('noticemanagement.apimanagement.7b96e0')}}</el-radio>
        <div class="tip__text">{{$t('noticemanagement.apimanagement.d58e16')}}</div>
      </el-form-item>
      <el-divider></el-divider>
      <el-form-item :label="$t('form2.index.11k7hv')" prop="weworkEnable">
        <el-radio v-model="form2.weworkEnable" label="false">{{$t('noticemanagement.apimanagement.i3y411')}}</el-radio>
        <el-radio v-model="form2.weworkEnable" label="true">{{$t('noticemanagement.apimanagement.7b96e0')}}</el-radio>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.wxmanagement.h2xl3y')">
        <el-input v-model="form2.weworkUrl"></el-input>
        <div class="tip__text">{{$t('noticemanagement.wxmanagement.m95221')}}</div>
      </el-form-item>
      <el-divider></el-divider>
      <el-form-item :label="$t('noticemanagement.messagemanagement.220649')" prop="smsEnable">
        <el-radio v-model="form2.smsEnable" label="false">{{$t('noticemanagement.apimanagement.i3y411')}}</el-radio>
        <el-radio v-model="form2.smsEnable" label="true">{{$t('noticemanagement.apimanagement.7b96e0')}}</el-radio>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.messagemanagement.8ob74u')">
        <el-input v-model="form2.smsAppKey"></el-input>
        <div class="tip__text">{{$t('noticemanagement.messagemanagement.m36gog')}}</div>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.messagemanagement.38634h')">
        <el-input v-model="form2.smsTplId"></el-input>
        <div class="tip__text">{{$t('noticemanagement.messagemanagement.a0oc0i')}}</div>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.messagemanagement.vj38tr')">
        <div style="margin-bottom: 10px">
          <el-row :gutter="4">
            <el-col :span="6">
              <el-input v-model="phone"></el-input>
            </el-col>
            <el-col :span="6">
              <el-button @click.prevent="addPhone()">{{
                $t("common.add", { text: "" })
              }}</el-button>
            </el-col>
          </el-row>
        </div>
        <el-table :data="tableData" style="width: 100%">
          <el-table-column
            prop="phone"
            :label="$t('common.phoneNumber')"
            width="180"
          >
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.action', { text: '' })"
            width="80"
          >
            <template slot-scope="scope">
              <el-button type="text" @click="deletePhone(scope.row)">{{
                $t("button.deleteText", { text: "" })
              }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmitForm('form2')"
          >{{$t('marktool.annotate.q5fjzf')}}</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import {
  getListData as getPhoneList,
  deleteData as deletePhoneData,
  saveData as addPhone,
} from "@/api/applicationMonitoring/messageManagement";
export default {
  props: {
    form2: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      tableData: [],
      phone: "",
    };
  },
  async created() {
    this.getPhoneList();
  },
  methods: {
    // 获取电话号码列表
    async getPhoneList() {
      this.loading = true;
      const data = await getPhoneList({});
      this.tableData = data.data;
      this.total = Number(data.count);
      this.loading = false;
    },
    async addPhone() {
      if (this.phone == null || this.phone == "") {
        this.$message.error(this.$t('noticemanagement.apimanagement.x75428'));
        return;
      }
      const res = await addPhone({
        phone: this.phone,
      });
      if (res.code == 0) {
        this.$message.success(
          $t("common.action", { text: $t("common.success") })
        );
        await this.getPhoneList();
      }
    },
    async deletePhone(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const res = await deletePhoneData({ id: item.id });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getPhoneList();
          }
        })
        .catch(() => {});
    },
    onSubmitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          this.$emit("submitForm", "form2", this.form2);
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
  },
};
</script>
<style scoped lang="scss"></style>
