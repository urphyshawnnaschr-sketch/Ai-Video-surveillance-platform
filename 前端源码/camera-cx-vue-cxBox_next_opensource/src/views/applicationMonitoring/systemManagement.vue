<template>
  <div>
    <div class="head-container">
      <el-tabs v-model="activeName">
        <el-tab-pane name="first">
          <span slot="label" class="required__label">{{
            $t("applicationMonitoring.systemManagement.baseConfig")
          }}</span>
          <Form1 :form1="form1" @submitForm="onSubmitForm" />
        </el-tab-pane>
        <!-- <el-tab-pane label="告警配置" name="second">
          <Form2 :form2="form2" @submitForm="onSubmitForm" />
        </el-tab-pane> -->
        <!-- <el-tab-pane
          :label="$t('applicationMonitoring.systemManagement.faceConfig')"
          name="fifth"
        >
          <Form5 :form5="form5" @submitForm="onSubmitForm" />
        </el-tab-pane> -->
        <el-tab-pane
          :label="$t('applicationMonitoring.systemManagement.utilizeConfig')"
          name="third"
        >
          <Form3 :form3="form3" @submitForm="onSubmitForm" />
        </el-tab-pane>
        <!-- <el-tab-pane
          :label="$t('applicationMonitoring.systemManagement.afterSaleService')"
          name="fouth"
        >
          <Form4 v-if="activeName == 'fouth'" />
        </el-tab-pane> -->
      </el-tabs>
    </div>
  </div>
</template>
<script>
import {
  getInfo,
  saveBasicConfig,
  saveAlarmConfig,
  saveAppConfig,
  saveFaceFrameConfig,
} from "@/api/applicationMonitoring/systemManagement";
import { getAppInfo } from "@/api/common";

import AddSystem from "@/components/applicationMonitoring/systemManagement/addSystem";
import Form1 from "@/components/applicationMonitoring/systemManagement/form1";
import Form2 from "@/components/applicationMonitoring/systemManagement/form2";
import Form3 from "@/components/applicationMonitoring/systemManagement/form3";
import Form4 from "@/components/applicationMonitoring/systemManagement/form4";
import Form5 from "@/components/applicationMonitoring/systemManagement/form5";
export default {
  components: {
    AddSystem,
    Form1,
    Form2,
    Form3,
    Form4,
    Form5,
  },
  data() {
    return {
      activeName: "first",
      form1: {
        ipAddr: "",
        algorithmUrl: "",
        streamType: "rtsp",
        mediaUrl: "",
        playUrl: "",
        pushIp: "",
        pushPort: "",
      },
      form2: {
        reportPushUrl: "",
        reportPushImage: "false",
        weworkEnable: "false",
        weworkUrl: "",
        smsEnable: "false",
        smsAppKey: "",
        smsTplId: "",
      },
      form3: {
        appName: "",
        logoUrl: "",
        screenName: "",
        screenLogoUrl: "",
      },
      form5: {
        FACE_HTTP_BASE_URL: "",
        FACE_HTTP_BASE_URL_2: "",
        // HTTP_FRAME_API_URL: "",
        // HTTP_FRAME_CALLBACK_URL: "",
        // HTTP_FRAME_ENABLE: "",
      },
    };
  },
  created() {
    this.getInfo();
  },
  methods: {
    //
    async getInfo() {
      const data = await getInfo();
      console.log("getInfo data", data);
      this.form1 = data.data.forms.form1 || {};
      this.form2 = data.data.forms.form2 || {};
      this.form3 = data.data.forms.form3 || {};
      console.log('*******form3', this.form3);
      this.form5 = data.data.forms.form4 || {};
    },

    async onSubmitForm(formName, formData) {
      if (formName === "form1") {
        await saveBasicConfig(formData);
        this.$message.success(
          $t("common.action", { text: $t("common.success") })
        );
      } else if (formName === "form2") {
        await saveAlarmConfig(formData);
        this.$message.success(
          $t("common.action", { text: $t("common.success") })
        );
      } else if (formName === "form3") {
        await saveAppConfig(formData);
        await this.getInfo();
        await this.getAppInfo();
        this.$message.success(
          $t("common.action", { text: $t("common.success") })
        );
      } else if (formName === "form5") {
        await saveFaceFrameConfig(formData);
        await this.getInfo();
        await this.getAppInfo();
        this.$message.success(
          $t("common.action", { text: $t("common.success") })
        );
      }
    },
    async getAppInfo() {
      const data = await getAppInfo();
      this.appInfo = data.data;
      this.$store.state.appInfo.appName = this.appInfo.appName;
      this.$store.state.appInfo.logoUrl = this.appInfo.logoUrl;
      this.$store.state.appInfo.screenName = this.appInfo.screenName;
      this.$store.state.appInfo.screenLogoUrl = this.appInfo.screenLogoUrl;
    },
  },
};
</script>
<style scoped lang="scss">
.search_box {
  padding-bottom: 20px;
}

.required__label:before {
  content: "*";
  color: #dd383e;
  margin-right: 4px;
}

.tip__text {
  color: #1A0808;
}
</style>
