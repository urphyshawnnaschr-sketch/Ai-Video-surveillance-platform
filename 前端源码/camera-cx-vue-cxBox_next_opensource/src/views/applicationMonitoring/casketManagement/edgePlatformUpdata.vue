<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>{{
              $t("applicationMonitoring.algorithmArrangeUpdata.boxList")
            }}</span>
          </div>
          <el-radio-group v-model="formatData.locationId">
            <el-radio
              v-for="(item, index) in boxList"
              :key="item.id"
              :label="item.id"
              style="display: block; margin-top: 10px"
              >{{ item.name }}
              <span v-if="item.boxVersion">{{
                "(" + item.boxVersion + ")"
              }}</span></el-radio
            >
          </el-radio-group>
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
            <span>{{
              $t("applicationMonitoring.common.historicalversion")
            }}</span>
            <el-button
              style="float: right"
              type="primary"
              size="mini"
              @click="changeBoxVersion()"
              >{{
                $t(
                  "applicationMonitoring.algorithmArrangeUpdata.switchingVersions"
                )
              }}</el-button
            >
          </div>
          <el-radio-group v-model="fileCheckList">
            <el-radio
              v-for="(item, index) in fileList"
              :key="item"
              :label="item"
              style="display: block; margin-top: 10px"
            ></el-radio>
          </el-radio-group>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span
              >{{
                $t("applicationMonitoring.casketManagement.newVersionReleased")
              }}
            </span>
            <!-- <el-button style="float: right" type="primary" @click="boxFileUpload()">发布版本</el-button> -->
          </div>
          <el-form ref="form" label-width="80px">
            <el-form-item
              :label="$t('applicationMonitoring.casketManagement.newVersion')"
            >
              <el-input v-model="formatData.fileName"></el-input>
            </el-form-item>
            <!-- <el-form-item label="盒子ID">
              <el-input v-model="formatData.locationId"></el-input>
            </el-form-item> -->
            <el-form-item
              :label="$t('applicationMonitoring.casketManagement.updateFile')"
            >
              <el-upload
                style="width: 100px"
                :action="VUE_APP_API_BASE_URL + '/location/boxFileUpload'"
                :data="{
                  ...formatData,
                }"
                :before-upload="beforeUpload"
                :headers="{
                  'X-Token': token,
                }"
                :on-success="onSuccess"
                accept=".zip"
              >
                <el-button type="primary"
                  >{{
                    $t(
                      "applicationMonitoring.casketManagement.uploadAndPublish"
                    )
                  }}
                </el-button>
              </el-upload>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import {
  getBoxAndVersion,
  changeBoxVersion,
  getBoxAndHistoryVersion,
  boxFileUpload,
} from "./api";
import Cookies from "js-cookie";
export default {
  data() {
    return {
      formatData: {
        fileName: "",
        locationId: "",
      },
      boxList: [],
      fileList: [],
      boxCheckList: "",
      fileCheckList: "",
      VUE_APP_API_BASE_URL,
      token: Cookies.get("X-Token"),
      file: "",
    };
  },
  created() {
    this.getBoxAndVersion();
  },
  methods: {
    onSuccess(response) {
      console.log(response);
      if (response.code == 0) {
        this.$message.success(
          $t("applicationMonitoring.casketManagement.publishSuccess")
        );
        this.getBoxAndVersion();
      }
    },
    async getBoxAndVersion() {
      let { data } = await getBoxAndVersion();
      this.boxList = data.boxList;
      this.fileList = data.fileList;
    },
    async changeBoxVersion() {
      //this.formatData.locationId = this.boxCheckList
      let { code } = await changeBoxVersion({
        locationId: this.formatData.locationId,
        fileName: this.fileCheckList,
      });
      if (code == 0) {
        this.$message.success(
          $t("applicationMonitoring.casketManagement.updateSuccess")
        );
        this.getBoxAndVersion();
      }
    },
    // async getBoxAndHistoryVersion(datas){
    //   let { code } = getBoxAndHistoryVersion(datas)
    //   if(code == 0) {

    //       this.$message.success('切换成功')
    //   }
    // },

    beforeUpload() {
      if (!this.formatData.locationId) {
        this.$message.warning($t("form.tip.chooseBox"));
        return false;
      }
      if (!this.formatData.fileName) {
        this.$message.warning(
          this.$t("casketmanagement.edgeplatformupdata.3c76l6")
        );
        return false;
      }
    },
    // httpRequest(files){
    //   const file = files.file;
    //   console.log(files)
    //   this.file = file
    //   this.boxFileUpload()

    // },
    // async boxFileUpload(){
    //   let form = new FormData();
    //   console.log(this.file)
    //   form.append("file", this.file);
    //   form.append("fileName", this.formatData.fileName);
    //   form.append("locationId", this.formatData.locationId);
    //   const {code} = await boxFileUpload(form)
    //   if(code == 0){

    //   }

    // }
  },
};
</script>
<style scoped>
.item {
  padding: 10px 0;
  font-size: 13px;
  color: #666;
}
</style>
