<template>
  <div>
    <el-form ref="form2" :model="form2" label-width="180px">
      <!-- <el-form-item label="第三方接口地址">
        <el-input v-model="form2.reportPushUrl"></el-input>
        <div class="tip__text">
          Please fill in third-party API URL，Only supports；For example：http://domain.com:port/api/alarm_data
        </div>
      </el-form-item>
      <el-form-item label="Push image" prop="streamType">
        <el-radio v-model="form2.reportPushImage" label="false"
          >Disable</el-radio
        >
        <el-radio v-model="form2.reportPushImage" label="true">Enable</el-radio>
        <div class="tip__text">Note，Image is passed as string</div>
      </el-form-item> -->
      <el-divider></el-divider>
      <el-form-item :label="$t('noticemanagement.wxmanagement.b456no')" prop="weworkEnable">
        <el-radio v-model="form2.weworkEnable" label="false">{{$t('noticemanagement.apimanagement.i3y411')}}</el-radio>
        <el-radio v-model="form2.weworkEnable" label="true">{{$t('noticemanagement.apimanagement.7b96e0')}}</el-radio>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.wxmanagement.641801')">
        <el-input v-model="form2.weworkUrl"></el-input>
        <div class="tip__text">{{$t('noticemanagement.wxmanagement.l97841')}}</div>
      </el-form-item>
      <el-divider></el-divider>
      <el-form-item :label="$t('noticemanagement.wxmanagement.k6p9vu')" prop="weworkEnable">
        <el-radio v-model="form2.dingdingEnable" label="false">{{$t('noticemanagement.apimanagement.i3y411')}}</el-radio>
        <el-radio v-model="form2.dingdingEnable" label="true">{{$t('noticemanagement.apimanagement.7b96e0')}}</el-radio>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.wxmanagement.j78571')">
        <el-input v-model="form2.dingdingUrl"></el-input>
        <div class="tip__text">{{$t('noticemanagement.wxmanagement.14g21v')}}</div>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.wxmanagement.vu2we1')">
        <el-input
          v-model="form2.dingdingSign"
          :placeholder="$t('noticemanagement.wxmanagement.7k1qb8')"
          type="textarea"
        ></el-input>
        <div class="tip__text">{{$t('noticemanagement.wxmanagement.1i5ts2')}}</div>
      </el-form-item>
      <el-divider></el-divider>
      <!-- <el-form-item label="是否推送短信通知" prop="smsEnable">
        <el-radio v-model="form2.smsEnable" label="false">Disable</el-radio>
        <el-radio v-model="form2.smsEnable" label="true">Enable</el-radio>
      </el-form-item>
      <el-form-item label="SMS PlatformAPP_KEY">
        <el-input v-model="form2.smsAppKey"></el-input>
        <div class="tip__text">
          Currently only Yunpian supported，Please register on Yunpian first to getAPP_KEY
        </div>
      </el-form-item>
      <el-form-item label="SMS platform templateID">
        <el-input v-model="form2.smsTplId"></el-input>
        <div class="tip__text">
          Currently only Yunpian supported，Please apply for SMS template on Yunpian first，Then get templateID
        </div>
      </el-form-item>
      <el-form-item label="SMS push mobile number">
        <div style="margin-bottom: 10px">
          <el-row :gutter="4">
            <el-col :span="6">
              <el-input v-model="phone"></el-input>
            </el-col>
            <el-col :span="6">
              <el-button @click.prevent="addPhone()">Add</el-button>
            </el-col>
          </el-row>
        </div>
        <el-table :data="tableData" style="width: 100%">
          <el-table-column prop="phone" :label="$t('common.phoneNumber')" width="180">
          </el-table-column>
          <el-table-column align="center" :label="$t('common.action', {text: ''})" width="80">
            <template slot-scope="scope">
              <el-button type="text" @click="deletePhone(scope.row)"
                >{{ $t("button.deleteText", { text: "" }) }}</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-form-item> -->
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
import { getInfo } from "@/api/applicationMonitoring/systemManagement";
import { saveAlarmWeworkConfig } from "./api";
export default {
  props: {},
  data() {
    return {
      tableData: [],
      phone: "",
      form2: {
        reportPushUrl: "",
        reportPushImage: "false",
        weworkEnable: "false",
        weworkUrl: "",
        smsEnable: "false",
        smsAppKey: "",
        smsTplId: "",
        dingdingEnable: false,
        dingdingSign: "",
        dingdingUrl: "",
      },
    };
  },
  async created() {
    this.getPhoneList();
    this.getInfo();
  },
  methods: {
    async getInfo() {
      const data = await getInfo();
      this.form2 = data.data.forms.form2 || {};
    },
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
          const data = await saveAlarmWeworkConfig(this.form2);
          this.$message.success(
            $t("button.saveText", { text: $t("common.success") })
          );
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
