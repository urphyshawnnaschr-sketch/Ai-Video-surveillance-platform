<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clear-flex">
            <div>
              {{ $t("applicationMonitoring.algorithmArrangeUpdata.boxList") }}
            </div>
            <div class="alg">{{ algorithmName }}</div>
          </div>
          <el-checkbox-group v-model="formatData.boxIds">
            <el-checkbox
              v-for="(item, index) in boxList"
              :key="item.locationId"
              :label="item.locationId"
              style="display: block; margin-top: 10px"
              >{{ item.boxName }}
              <span v-if="item.algorithmVersion">{{
                "(" + item.algorithmVersion + ")"
              }}</span></el-checkbox
            >
          </el-checkbox-group>
          <!-- <ul>
            <li class="item" v-for="(item,index) in boxList" :key="item.id">
            {{ item.name }}
            </li>
          </ul> -->
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span
              >{{ $t("applicationMonitoring.algorithmArrangeUpdata.fileList") }}
            </span>
            <el-button
              style="float: right"
              type="primary"
              size="mini"
              @click="changeBoxAlgorithmVersion"
              >{{
                $t(
                  "applicationMonitoring.algorithmArrangeUpdata.switchingVersions"
                )
              }}</el-button
            >
          </div>
          <!-- <el-radio-group v-model="formatData.fileName">
            <el-radio
              v-for="item in fileList"
              :key="item.id"
              :label="item.name"
              style="display: block; margin-top: 10px"
            >
            {{  item.name }}
            <div style="padding: 5px 0px 5px 20px;">
              <el-progress
                :percentage="handleProcess(item.localLength,item.length)"
              ></el-progress>
            </div>
          </el-radio>
          </el-radio-group> -->
          <div class="title">
            {{
              $t("applicationMonitoring.algorithmArrangeUpdata.latestVersion")
            }}
          </div>
          <div class="radio-sty" v-if="lastVersionFile.name">
            <el-radio
              v-model="formatData.fileName"
              :label="lastVersionFile.name"
              >{{ lastVersionFile.name }}</el-radio
            >
            <div style="width: 50%">
              <el-progress
                class="percen-sty"
                :percentage="
                  handleProcess(
                    lastVersionFile.localLength,
                    lastVersionFile.length
                  )
                "
              ></el-progress>
            </div>
          </div>
          <div class="title">
            {{ $t("applicationMonitoring.common.historicalversion") }}
          </div>
          <div class="radio-sty" v-for="(item, index) in fileList">
            <el-radio v-model="formatData.fileName" :label="item.name">{{
              item.name
            }}</el-radio>
            <div style="width: 50%">
              <el-progress
                class="percen-sty"
                :percentage="handleProcess(item.localLength, item.length)"
              ></el-progress>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span
              >{{ $t("applicationMonitoring.algorithmArrangeUpdata.progress") }}
            </span>
          </div>
          <ul>
            <li
              style="margin-top: 10px"
              v-for="(item, index) in boxList"
              :key="item.locationId"
            >
              <span
                style="
                  color: #1A0808;
                  font-size: 13px;
                  float: left;
                  width: 200px;
                "
                >{{ item.boxName }}:</span
              >
              <span style="color: #1A0808; font-size: 12px">{{
                state(item.boxUpdateStatus)
              }}</span>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import Cookies from "js-cookie";
import {
  getBoxAndHistoryVersion,
  changeBoxAlgorithmVersion,
} from "@/api/applicationMonitoring/algorithmManagement";
export default {
  data() {
    return {
      formatData: {
        boxIds: [],
        fileName: "",
      },
      boxList: [],
      fileList: [],
      boxCheckList: "",
      fileCheckList: "",
      VUE_APP_API_BASE_URL,
      token: Cookies.get("X-Token"),
      file: "",
      id: "",
      timer: null,
      lastVersionFile: {},
      algorithmName: "",
    };
  },
  created() {
    this.id = this.$route.query.id;
    this.algorithmName = this.$route.query.name;
    this.getBoxAndHistoryVersion();
  },
  destroyed() {
    clearInterval(this.timer);
  },
  methods: {
    state(s) {
      let obj = {
        0: $t("applicationMonitoring.algorithmArrangeUpdata.noUpdateTask"),
        1: $t("applicationMonitoring.algorithmArrangeUpdata.waitingUpdates"),
        2: $t("applicationMonitoring.algorithmArrangeUpdata.updating"),
        3: $t("applicationMonitoring.algorithmArrangeUpdata.updateCompleted"),
        4: $t(
          "applicationMonitoring.algorithmArrangeUpdata.algorithmFileDownload"
        ),
      };
      return obj[s];
    },
    async getBoxAndHistoryVersion() {
      let { data } = await getBoxAndHistoryVersion({ id: this.id });
      this.boxList = data.boxList;
      let newArr = data.fileList.filter((item) => {
        return item.name != data.lastVersionFile.name;
      });
      this.fileList = newArr;
      this.lastVersionFile = data.lastVersionFile;
      let Arr = data.boxList.filter((item) => {
        return item.boxUpdateStatus != 0;
      });
      clearInterval(this.timer);
      if (Arr.length > 0) {
        let that = this;
        this.timer = setInterval(function () {
          that.getBoxAndHistoryVersion();
        }, 3000);
      } else {
        clearInterval(this.timer);
      }
    },
    async changeBoxAlgorithmVersion() {
      //this.formatData.locationId = this.boxCheckList
      let { code } = await changeBoxAlgorithmVersion({
        boxIds: this.formatData.boxIds + "",
        fileName: this.formatData.fileName,
        algorithmId: this.id,
      });
      if (code == 0) {
        this.$message.success($t("applicationMonitoring.common.updateSuccess"));
        this.formatData.fileName = "";
        this.formatData.boxIds = [];
        this.getBoxAndHistoryVersion();
      }
    },
    // 处理进度条
    handleProcess(local, total) {
      if (local && total) {
        let num = Number(((Number(local) / Number(total)) * 100).toFixed(2));
        return num > 100 ? Number("100.00") : num;
      } else {
        return 0;
      }
    },
  },
};
</script>
<style scoped>
.item {
  padding: 10px 0;
  font-size: 13px;
  color: #666;
}
.title {
  font-size: 13px;
  font-weight: bold;
  margin-bottom: 10px;
}
.radio-sty {
  display: flex;
  margin-bottom: 5px;
}
.clear-flex {
  display: flex;
  justify-content: space-between;
}
.alg {
  flex: 1;
  padding-left: 20px;
  text-align: right;
}
</style>
<style lang="scss">
.percen-sty {
  .el-progress-bar {
    width: 92% !important;
    margin-right: -75px !important;
  }
  .el-progress__text {
    margin-left: 30px !important;
  }
}
</style>
