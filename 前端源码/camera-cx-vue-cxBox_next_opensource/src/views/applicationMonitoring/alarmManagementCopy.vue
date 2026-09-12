<template>
  <div
    style="
      background: #fff;
      border-radius: 8px;
      position: relative;
      height: 100vh;
    "
  >
    <div class="top-tabs">
      <div
        v-for="(items, index) in algorithmOptions"
        :key="index"
        :class="[items.isCheck ? 'tab-check' : 'tab-item']"
        @click="tabClick($event, index)"
      >
        <div :class="[items.isCheck ? 'tab-name' : '']">
          {{ items.name }}
        </div>
      </div>
    </div>
    <div class="alarm-M">
      <div class="seach-sty">
        <div style="flex: 1; display: flex">
          <div class="seach-flex">
            <div style="margin-right: 5px; padding-bottom: 8px">
              {{ $t("applicationMonitoring.alarmmanagement.8wv6o1") }}
            </div>
            <div style="flex: 1">
              <el-select
                :placeholder="$t('common.camera')"
                clearable
                v-model="params.cameraId"
                style="width: 150px"
              >
                <el-option
                  v-for="(item, index) in cameraOptions"
                  :key="index"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </div>
          </div>
          <div
            class="seach-flex"
            style="padding-bottom: 8px; margin-right: 15px"
          >
            <div style="margin-right: 5px">
              {{ $t("applicationMonitoring.alarmmanagement.7mi84i") }}
            </div>
            <div style="flex: 1">
              <el-date-picker
                v-model="date"
                :default-time="['00:00:00', '23:59:59']"
                type="datetimerange"
                :range-separator="$t('common.pickerDate.to')"
                :start-placeholder="$t('common.pickerDate.startDate')"
                :end-placeholder="$t('common.pickerDate.endDate')"
                value-format="yyyy-MM-dd HH:mm:ss"
                format="yyyy-MM-dd HH:mm:ss"
                style="width: 330px"
                @change="dateChange"
                :disabled="isDisabled"
                :clearable="false"
              >
              </el-date-picker>
            </div>
          </div>
          <el-button type="primary" icon="el-icon-search" @click="getData()">{{
            $t("button.queryText")
          }}</el-button>
          <el-button icon="el-icon-refresh" @click="refreshData">{{
            $t("button.resetText", { text: "" })
          }}</el-button>
        </div>
        <div class="clear-flex">
          <!-- <div @click="dowloadData">导出</div> -->
          <div class="clear-title">
            <el-popover placement="bottom-start" width="300" trigger="hover">
              <div style="font-size: 12px; line-height: 18px">
                {{ $t("applicationMonitoring.alarmmanagement.4857w4") }}
              </div>
              <span class="el-icon-warning-outline" slot="reference"></span>
            </el-popover>
            <span style="padding: 0px 10px 0px 5px"
              >{{ $t("faceControl.faceRecognition.dataPeriod") }}:</span
            >
          </div>
          <el-select
            :placeholder="$t('common.chooseText')"
            v-model="clearReportDay"
            style="width: 100px"
            @change="dayChange()"
          >
            <el-option
              v-for="(item, index) in clearDayList"
              :key="index"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </div>
      </div>
      <div>
        <div v-if="tableData.length">
          <div class="table-sty">
            <div v-for="item in tableData" :key="item.id" class="item-sty">
              <AlarmCard
                :fileUrl="
                  VUE_APP_API_BASE_URL + `/report/streamThumb?id=${item.id}`
                "
                :dataList="handleParams(item.params)"
                :alarmData="item"
                :isAlarm="true"
                @hoverFun="hoverFun"
              >
              </AlarmCard>
              <div
                style="
                  padding: 0px 10px 10px 0px;
                  text-align: right;
                  color: #E53935;
                  cursor: pointer;
                "
                @click="deleteFun(item.id)"
              >
                {{ $t("annotationplatform.annotationgroupmanagement.5t6l7l") }}
              </div>
            </div>
          </div>
        </div>
        <div class="noData" v-else>
          <el-empty :description="$t('common.noData')"></el-empty>
        </div>
        <div class="pagination">
          <el-pagination
            background
            :current-page="params.page"
            :page-size="params.limit"
            :page-sizes="[10]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          ></el-pagination>
        </div>
      </div>
    </div>
    <div class="big-img" v-if="isBigImg">
      <MarkResult
        v-if="hoverId"
        :fileUrl="VUE_APP_API_BASE_URL + `/report/streamThumb?id=${hoverId}`"
        :dataList="hoverList"
        :imgRatio="imgRatio"
      />
    </div>
  </div>
</template>
<script>
import Cookies from "js-cookie";
import MarkResult from "@/components/markResult";
import {
  getListData,
  getCameraListData,
  saveclearReportDayConfig,
  listTabs,
  exportAlarm,
  deleteAlarm,
} from "@/api/applicationMonitoring/alarmManagement";
import { getAfterSales } from "@/api/applicationMonitoring/systemManagement";
import AlarmDetail from "@/components/applicationMonitoring/alarmManagement/alarmDetail";
import AlarmCard from "@/components/applicationMonitoring/alarmManagement/newCard";

export default {
  components: {
    AlarmDetail,
    AlarmCard,
    MarkResult,
  },
  props: {
    isDisabled: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      imgRatio: 0.5,
      loading: false,
      tableData: [],
      date: [new Date(), new Date()],
      params: {
        cameraId: "",
        algorithmId: "",
        alarmLevelId: "",
        type: "",
        startDate: "",
        endDate: "",
        limit: 10,
        page: 1,
      },
      total: 0,
      cameraOptions: [],
      algorithmOptions: [],
      VUE_APP_API_BASE_URL,
      clearDayList: [
        {
          id: "30",
          name: $t("faceControl.faceRecognition.nearDate", { count: 30 }),
        },
        {
          id: "20",
          name: $t("faceControl.faceRecognition.nearDate", { count: 20 }),
        },
        {
          id: "14",
          name: $t("faceControl.faceRecognition.nearDate", { count: 14 }),
        },
        {
          id: "10",
          name: $t("faceControl.faceRecognition.nearDate", { count: 10 }),
        },
        {
          id: "7",
          name: $t("faceControl.faceRecognition.nearDate", { count: 7 }),
        },
        {
          id: "5",
          name: $t("faceControl.faceRecognition.nearDate", { count: 5 }),
        },
        {
          id: "3",
          name: $t("faceControl.faceRecognition.nearDate", { count: 3 }),
        },
        {
          id: "1",
          name: $t("faceControl.faceRecognition.nearDate", { count: 1 }),
        },
      ],
      clearReportDay: "30",
      oldDay: "30",
      isShowTip: false,
      isBigImg: false,
      hoverId: "",
      hoverList: [],
    };
  },
  async created() {
    this.date = [
      this.$moment(new Date(this.date[0].setHours(0, 0, 0))).format(
        "YYYY-MM-DD HH:mm:ss"
      ),
      this.$moment(new Date(this.date[1].setHours(23, 59, 59))).format(
        "YYYY-MM-DD HH:mm:ss"
      ),
    ];
    this.getDay();
    await this.getListTabs();
    await this.getOptions();
    this.getListData();
    this.connectWebsocket();
  },
  beforeDestroy() {
    this.websocket.close();
  },
  methods: {
    getData() {
      this.params.page = 1;
      this.getListData();
      this.getListTabs();
    },
    // 获取下拉
    async getOptions() {
      const data1 = await getCameraListData();
      this.cameraOptions = data1.data;
    },
    // 获取有告警的算法
    async getListTabs() {
      let formData = new FormData();
      if (this.date && this.date.length > 0) {
        formData.append("startDate", this.date[0]);
        formData.append("endDate", this.date[1]);
      }
      const res = await listTabs(formData);
      if (res.data.length > 0) {
        res.data.forEach((item) => {
          item.isCheck = false;
        });
      }
      this.algorithmOptions = res.data;
    },
    // 选中算法
    tabClick(e, index) {
      this.algorithmOptions.forEach((item, ind) => {
        item.isCheck = false;
        if (index == ind) {
          item.isCheck = true;
          this.params.algorithmId = item.id;
        }
      });
      Object.assign(this.params, {
        cameraId: "",
        algorithmId: "",
        alarmLevelId: "",
        type: "",
        limit: 10,
        page: 1,
      });
      this.getListData();
    },
    // 获取告警列表
    async getListData() {
      this.loading = true;
      this.isBigImg = false;
      if (this.date && this.date.length > 0) {
        this.params.startDate = this.date[0];
        this.params.endDate = this.date[1];
      }
      const data = await getListData(this.params);
      this.tableData = data.data;
      this.total = Number(data.count);
      this.loading = false;
    },
    // 改变时间
    async dateChange() {
      this.isBigImg = false;
      await this.getListTabs();
      await this.getListData();
    },
    // 重置
    refreshData() {
      let dateList = [new Date(), new Date()];
      this.date = [
        this.$moment(new Date(dateList[0].setHours(0, 0, 0))).format(
          "YYYY-MM-DD HH:mm:ss"
        ),
        this.$moment(new Date(dateList[1].setHours(23, 59, 59))).format(
          "YYYY-MM-DD HH:mm:ss"
        ),
      ];

      Object.assign(this.params, {
        cameraId: "",
        algorithmId: "",
        alarmLevelId: "",
        type: "",
        limit: 10,
        page: 1,
      });
      this.getListData();
      this.getListTabs();
    },
    // 创建链接
    connectWebsocket() {
      if (typeof WebSocket === "undefined") {
        console.log("Your browser does not support");
        return;
      } else {
        let cookie = Cookies.get("X-Token") || this.$route.query.token;
        let url = `${VUE_APP_WS_BASE_URL}/report/${cookie}`;
        // 打开一个
        this.websocket = new WebSocket(url);
        // 建立连接
        this.websocket.onopen = () => {
          console.log("Connection：");
        };
        // 客户端接收服务端返回的数据
        this.websocket.onmessage = (evt) => {
          const data = JSON.parse(evt.data);
          if (data.type == "REPORT_SHOW") {
            this.getListData();
          }
        };
        // 发生错误时
        this.websocket.onerror = (evt) => {
          console.log("websocketError：", evt);
        };
        // 关闭连接
        this.websocket.onclose = (evt) => {
          console.log("websocketClose：", evt);
        };
      }
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getListData();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getListData();
    },
    handleParams(params) {
      try {
        return JSON.parse(params);
      } catch (err) {
        return [];
      }
    },
    // 切换定时任务清除告警信息天数
    dayChange() {
      console.log(this.clearReportDay);
      let str = "";
      this.clearDayList.forEach((item) => {
        if (this.clearReportDay == item.id) {
          str = item.name;
        }
      });
      this.$confirm(
        `${this.$t("applicationMonitoring.alarmmanagement.4857w2", [
          str,
        ])}<div style="color: red;">${this.$t(
          "applicationMonitoring.alarmmanagement.4857w3",
          [str]
        )}</div>`,
        $t("common.prompt"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          dangerouslyUseHTMLString: true,
          type: "warning",
        }
      )
        .then(() => {
          saveclearReportDayConfig({
            clearReportDay: this.clearReportDay,
          }).then((res) => {
            this.$message({
              message: $t("button.saveText", { text: $t("common.success") }),
              type: "success",
              duration: 500,
            });
            this.oldDay = this.clearReportDay;
          });
        })
        .catch(() => {
          this.clearReportDay = this.oldDay;
        });
    },
    //获取清除告警信息天数
    async getDay() {
      const res = await getAfterSales({ tag: "clearReportDay" });
      this.clearReportDay = res.data ? res.data : "30";
      this.oldDay = res.data ? res.data : "30";
    },
    // 鼠标悬浮
    onHover() {
      this.isShowTip = true;
    },
    // 鼠标离开
    hideMessage() {
      this.isShowTip = false;
    },
    // 图片鼠标悬浮
    hoverFun(item) {
      this.isBigImg = item.isShow;
      this.hoverId = item.id;
      this.hoverList = item.dataList;
    },
    // 导出告警图片
    async dowloadData() {
      let Obj = {
        startDate: this.date[0],
        endDate: this.date[1],
      };
      const data = await exportAlarm(Obj);
      var blob = new Blob([data.data], { type: "application/zip" });
      var url = window.URL.createObjectURL(blob);
      var linkElement = document.createElement("a");
      linkElement.setAttribute("href", url);
      linkElement.click();
    },
    // 删除
    async deleteFun(id) {
      const res = await deleteAlarm({ id: id });
      this.$message({
        message: $t("button.deleteText", { text: $t("common.success") }),
        type: "success",
        duration: 500,
      });
    },
  },
};
</script>
<style scoped lang="scss">
.alarm-M {
  padding: 16px;
  .seach-sty {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    padding: 5px 0px 15px 0px;
    border-bottom: 1px solid #edf0f3;
    margin-bottom: 20px;
    .seach-flex {
      display: flex;
      align-items: center;
      font-size: 13px;
      margin-right: 10px;
    }
  }
}
.noData {
  line-height: 120px;
  text-align: center;
}

.table-sty {
  display: grid;
  grid-template-columns: repeat(5, 19%);
  justify-content: space-between;
  grid-row-gap: 20px;
  .item-sty {
    border: 1px solid #d3d7dd;
    border-radius: 6px;
  }
}

.top-tabs {
  background: #e7ebf0;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  display: flex;
  width: 100%;
  overflow-x: auto;
  .tab-item {
    flex-shrink: 0;
    padding: 0px 20px;
    margin: 10px 0px;
    border-left: 1px solid #c2c6cd;
    font-size: 16px;
    font-weight: bold;
    line-height: 20px;
    color: #6c727d;
    cursor: pointer;
  }
  .tab-item:first-child {
    border-left: none;
  }
  .tab-check {
    flex-shrink: 0;
    padding: 10px 20px 10px 10px;
    font-size: 16px;
    line-height: 20px;
    font-weight: bold;
    color: #000;
    background: #fff;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
  }
  .tab-check + .tab-item {
    border-left: none;
  }
  .tab-name {
    border-left: 2px solid #E53935;
    padding-left: 8px;
  }
}
.big-img {
  width: 700px;
  padding: 10px;
  background: #fff;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 10px 10px 10px rgba(0, 0, 0, 0.3);
  z-index: 99;
  pointer-events: none;
}
</style>
<style lang="scss">
.clear-flex {
  display: flex;
  align-items: center;
  .clear-title {
    font-size: 13px;
    position: relative;
    padding-bottom: 8px;
  }
  .el-icon-warning-outline:before {
    color: #b3b3b3;
  }
  .clear-tip {
    position: absolute;
    width: 290px;
    background: #fff;
    z-index: 999;
    padding: 10px;
    top: 30px;
    right: -100px;
    border-radius: 8px;
    box-shadow: 0px 0px 2px rgba(0, 0, 0, 0.3);
    font-size: 12px;
    line-height: 18px;
  }
}
</style>
