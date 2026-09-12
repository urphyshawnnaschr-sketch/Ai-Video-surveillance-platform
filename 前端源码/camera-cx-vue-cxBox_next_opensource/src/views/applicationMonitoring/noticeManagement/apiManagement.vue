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
      <!-- <el-divider></el-divider>
      <el-form-item label="Push to WeWork group bot" prop="weworkEnable">
        <el-radio v-model="form2.weworkEnable" label="false">Disable</el-radio>
        <el-radio v-model="form2.weworkEnable" label="true">Enable</el-radio>
      </el-form-item>
      <el-form-item label="WeWork group bot URL">
        <el-input v-model="form2.weworkUrl"></el-input>
        <div class="tip__text">
          Please enable WeWork group bot first，Then copy and paste group URL，For example：https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=1111111-2222-3333-4444444
        </div>
      </el-form-item>
      <el-divider></el-divider>
      <el-form-item label="Push SMS notification" prop="smsEnable">
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
      <el-divider></el-divider>
      <el-form-item :label="$t('noticemanagement.apimanagement.52y7r3')">
        <!-- <el-table
        :data="listData"
        border
        style="width: 100%">
          <el-table-column prop="name" label="Parameter Name"></el-table-column>
          <el-table-column prop="illustrate" label="Parameter description"></el-table-column>
          <el-table-column prop="type" label="Parameter Type"></el-table-column>
          <el-table-column prop="remarks" :label="$t('common.remark')"></el-table-column>
        </el-table> -->
        <div style="cursor: pointer; color: #eb3a2f" @click="DownloadFun()">{{$t('noticemanagement.apimanagement.y4l8q2')}}</div>
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
import { saveAlarmPushConfig } from "./api";
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
      },
      listData: [
        {
          name: "cmpn_cd",
          illustrate: this.$t('noticemanagement.apimanagement.7q6elm'),
          type: "string",
          remarks: this.$t('noticemanagement.apimanagement.6zdur2'),
        },
        {
          name: "camera_id",
          illustrate: this.$t('noticemanagement.apimanagement.2f2b6a'),
          type: "string",
          remarks: "",
        },
        {
          name: "camera_name",
          illustrate: $t("common.cameraName") + " ",
          type: "string",
          remarks: "",
        },
        {
          name: "algorithm_id",
          illustrate: $t('common.algorithm') +"id",
          type: "string",
          remarks: "",
        },
        {
          name: "algorithm_name",
          illustrate: $t('common.algorithmName'),
          type: "string",
          remarks: "",
        },
        {
          name: "level",
          illustrate: this.$t('noticemanagement.apimanagement.ber82b'),
          type: "string",
          remarks: this.$t('noticemanagement.apimanagement.g1e5pt'),
        },
        {
          name: "img_path",
          illustrate: this.$t('noticemanagement.apimanagement.6jlfv4'),
          type: "string",
          remarks: "",
        },
        {
          name: "img_ext",
          illustrate: this.$t('noticemanagement.apimanagement.fgb3dg'),
          type: "string",
          remarks: "",
        },
        {
          name: "img_name",
          illustrate: $t("faceControl.faceRecognition.picture", {
            count: $t("common.name"),
          }),
          type: "string",
          remarks: "",
        },
        {
          name: "alarm_dt",
          illustrate: $t("common.alarmTime"),
          type: "string",
          remarks: "Yyyy-mm-dd hh:mm:ss",
        },
        {
          name: "report_id",
          illustrate: this.$t('noticemanagement.apimanagement.9wfs4l'),
          type: "string",
          remarks: "http://domain:port/report/stream?id=report_id 图片地址",
        },
        {
          name: "params",
          illustrate: this.$t('noticemanagement.apimanagement.zss3su'),
          type: "string",
          remarks: this.$t('noticemanagement.apimanagement.3j9371'),
        },
        {
          name: "webUrl",
          illustrate: this.$t('noticemanagement.apimanagement.s5858u'),
          type: "string",
          remarks: "",
        },
        {
          name: "box_sn",
          illustrate: $t('common.boxNumber'),
          type: "string",
          remarks: "",
        },
        {
          name: "box_id",
          illustrate: this.$t('noticemanagement.apimanagement.e42nwe'),
          type: "string",
          remarks: "",
        },
        {
          name: "box_ip",
          illustrate: this.$t('noticemanagement.apimanagement.6v1835'),
          type: "string",
          remarks: "",
        },
        {
          name: "alarm_count",
          illustrate: this.$t('noticemanagement.apimanagement.8v6sc0'),
          type: "string",
          remarks: "",
        },
      ],
    };
  },
  async created() {
    this.getPhoneList();
    this.getInfo();
  },
  methods: {
    DownloadFun() {
      var a = document.createElement("a"); // 创建一个<a></a>标签
      a.href = "/static/third_part_data.docx";
      a.download = "third_part_data.docx"; // 设置下载文件文件名
      a.style.display = "none"; // 隐藏a标签
      document.body.appendChild(a); // 将a标签追加到文档对象中
      a.click(); // 模拟点击了a标签，会触发a标签的的读取，浏览器就会自动下载了
      a.remove(); // 一次性的，用完就删除a标签
    },
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
          const data = await saveAlarmPushConfig(this.form2);
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
