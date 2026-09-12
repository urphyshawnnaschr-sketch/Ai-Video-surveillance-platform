<template>
  <div class="wrap">
    <div class="flex-left">
      <!-- <el-card class="box-card">
        <div
          slot="header"
          style="
            display: flex;
            align-items: center;
            justify-content: space-between;
          "
        >
          <span>Video Statistics</span>
          <div>
            <el-button type="primary" @click="algorithmListVisible = true">Statistics Configuration</el-button>
            <el-button type="primary" @click="detailFun">View Today</el-button>
          </div>
        </div>
        <div class="algorithms-list">
          <span v-for="(item, index) in algorithmList" :key="index"
            >{{ item.name }}:{{ item.staticsFlagVal }}</span
          >
        </div>
      </el-card> -->
      <el-card class="box-card">
        <div
          slot="header"
          style="
            display: flex;
            align-items: center;
            justify-content: space-between;
          "
        >
          <span
            >{{
              $t("applicationMonitoring.videoStreamManagement.videoChannels")
            }}
          </span>
          <div>
            <el-button
              type="primary"
              style="margin-right: 20px"
              @click="detailFun"
              >{{ $t("applicationMonitoring.videoStreamManagement.viewToday") }}
            </el-button>
            <el-radio-group v-model="params.limit">
              <el-radio-button label="1">{{
                $t("applicationMonitoring.common.oneScreen")
              }}</el-radio-button>
              <el-radio-button label="4">{{
                $t("applicationMonitoring.common.fourScreen")
              }}</el-radio-button>
              <el-radio-button label="6">{{
                $t("applicationMonitoring.common.sixScreen")
              }}</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div class="video-box" :class="'video' + params.limit">
          <div
            class="video-item"
            ref="videoItem"
            v-for="(item, index) in activeList"
            :key="index"
          >
            <div
              class="video"
              :id="'video' + item.id"
              v-show="index < params.limit"
            ></div>
            <div class="dropdown">
              <el-select
                :placeholder="$t('applicationMonitoring.common.videoSource')"
                v-model="item.id"
                class="head-container-input"
                @change="changeSelect(item, index)"
                style="width: 120px"
              >
                <el-option
                  v-for="(subItem, subIndex) in cameraOptions"
                  :key="subIndex"
                  :label="subItem.name"
                  :value="subItem.id"
                ></el-option>
              </el-select>
            </div>
            <div
              v-if="!playerList[index]"
              style="width: 100%; text-align: center"
            >
              <i class="el-icon-loading"></i>
              {{ $t("applicationMonitoring.common.videoSource") }}
            </div>
          </div>
          <div
            class="video-item"
            v-for="i in Number(params.limit) > activeList.length
              ? Number(params.limit) - activeList.length
              : 0"
            :key="'key' + i"
          >
            <div
              v-if="!playerList[index]"
              style="width: 100%; text-align: center"
            >
              <i class="el-icon-loading"></i>
              {{ $t("applicationMonitoring.common.videoSource") }}
            </div>
          </div>
        </div>
        <div class="pagination">
          <el-pagination
            background
            :current-page="params.page"
            :page-size="params.limit"
            layout="total, prev, pager, next, jumper"
            :total="total"
            @current-change="handleCurrentChange"
          ></el-pagination>
        </div>
      </el-card>
    </div>
    <div class="flex-right">
      <el-card class="box-card">
        <div
          slot="header"
          style="
            display: flex;
            align-items: center;
            justify-content: space-between;
          "
        >
          <span>{{ $t("applicationMonitoring.common.realData") }}</span>
          <span
            >{{ $t("applicationMonitoring.common.alarmPopup")
            }}<el-switch v-model="alarmSwitch"> </el-switch
          ></span>
        </div>
        <div class="count">
          {{ $t("applicationMonitoring.common.todayAlarmNum")
          }}<span>{{ alarmsCount }}</span>
        </div>
        <el-scrollbar class="scrollbar-wrapper">
          <ul class="list">
            <li v-for="(item, index) in alarmsList" :key="index">
              <el-image
                class="img"
                :src="
                  VUE_APP_API_BASE_URL + `/report/streamThumb?id=${item.id}`
                "
                fit="cover"
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <el-descriptions :column="1">
                <el-descriptions-item :label="$t('common.alarmType')">{{
                  item.algorithmName
                }}</el-descriptions-item>
                <el-descriptions-item
                  :label="$t('applicationMonitoring.common.belongingCamera')"
                  >{{ item.cameraName }}</el-descriptions-item
                >
                <el-descriptions-item :label="$t('common.areaName')">{{
                  item.wareName
                }}</el-descriptions-item>
                <el-descriptions-item :label="$t('common.alarmTime')">{{
                  item.alarmTime
                }}</el-descriptions-item>
              </el-descriptions>
            </li>
          </ul>
        </el-scrollbar>
      </el-card>
    </div>
    <AlgorithmList
      v-if="algorithmListVisible"
      @close="(algorithmListVisible = false), getAlgorithms()"
    />
    <AlarmPopup
      v-if="alarmVisible"
      :alarmData="alarmData"
      :fileUrl="VUE_APP_API_BASE_URL + `/report/streamThumb?id=${alarmData.id}`"
      @close="alarmVisible = false"
    />
    <!-- 查看当日 -->
    <el-dialog
      :close-on-click-modal="false"
      width="90%"
      top="5vh"
      :title="$t('applicationMonitoring.videoStreamManagement.viewToday')"
      :visible.sync="detailVisible"
      @close="closeHandle"
    >
      <AlarmManagement :isDisabled="true" v-if="detailVisible" />
    </el-dialog>
  </div>
</template>
<script>
import Cookies from "js-cookie";
import Player from "xgplayer";
import { FlvPlugin, Events } from "xgplayer-flv";
import "xgplayer/dist/index.min.css";
import AlgorithmList from "@/components/applicationMonitoring/videoStreamManagement/algorithmList";
import AlarmPopup from "@/components/applicationMonitoring/videoStreamManagement/alarmPopup";
import AlarmManagement from "./alarmManagement";
import {
  getAlgorithms,
  getListPageActives,
  getPlayUrl,
  getAlarms,
  getCounter,
  getCameraListData,
  playings,
} from "@/api/applicationMonitoring/videoStreamManagement";
import { getStream } from "@/api/common";
export default {
  components: {
    AlgorithmList,
    AlarmPopup,
    AlarmManagement,
  },
  data() {
    return {
      VUE_APP_API_BASE_URL,
      detailVisible: false,
      alarmSwitch: true,
      algorithmListVisible: false,
      alarmVisible: false,
      alarmData: {},
      algorithmList: [],
      cameraOptions: [],
      activeList: [],
      alarmsList: [],
      playerList: [],
      alarmsCount: 0,
      websocket: null,
      timer: null,
      params: {
        page: 1,
        limit: 6,
      },
      total: 0,
      timerPlayings: "",
    };
  },
  watch: {
    "params.limit": {
      deep: true,
      handler(val) {
        this.params.page = 1;
        this.getListPageActives();
      },
    },
  },
  created() {
    this.getAlgorithms();
    this.getListPageActives();
    this.getAlarms();
    this.getCounter();
    this.getCameraListData();
    this.connectWebsocket();
  },
  methods: {
    // 获取算法列表
    async getAlgorithms() {
      const data = await getAlgorithms();
      this.algorithmList = data.data;
    },
    // 获取摄像头列表
    async getCameraListData() {
      const data = await getCameraListData();
      let Arr = data.data.filter((item) => {
        return item.running == "1";
      });
      this.cameraOptions = Arr;
    },
    // 获取视频流并播放
    async getListPageActives() {
      const data = await getListPageActives(this.params);
      this.activeList = data.data;
      this.total = Number(data.count);
      this.playerList.forEach((item) => {
        item.destroy();
      });
      this.playerList = [];
      for (let i = 0; i < this.activeList.length; i++) {
        const item = this.activeList[i];
        getPlayUrl({ cameraId: item.id }).then((res) => {
          const playUrl = res.data;
          this.$nextTick(() => {
            this.playerList[i] = new Player({
              id: "video" + item.id,
              isLive: true,
              playsinline: true,
              url: playUrl,
              volume: 0,
              autoplay: true,
              fluid: true,
              autoplayMuted: true,
              plugins: [FlvPlugin],
              width: this.$refs.videoItem[0].offsetWidth,
              height: this.$refs.videoItem[0].offsetHeight,
              commonStyle: {
                progressColor: "transparent",
                sliderBtnStyle: {
                  color: "transparent",
                },
              },
            });
          });
        });
      }
      if (this.activeList.length > 0) {
        //每10秒提交一次正在播放的摄像头
        this.savePlayings();
        let that = this;
        if (this.timerPlayings) {
          clearInterval(this.timerPlayings);
          this.timerPlayings = "";
        }
        this.timerPlayings = setInterval(function () {
          that.savePlayings();
        }, 10000);
      }
    },
    savePlayings() {
      let newArr = [];
      if (this.activeList.length > 0) {
        this.activeList.forEach((item) => {
          if (item.id) {
            newArr.push(item.id);
          }
        });
      }
      playings({ cameraIds: newArr })
        .then((res) => {})
        .catch((res) => {
          clearInterval(this.timerPlayings);
          this.timerPlayings = "";
        });
    },
    // 获取实时告警数据
    async getAlarms() {
      const data = await getAlarms();
      this.alarmsList = data.data;
      // this.alarmsList.forEach(async (item) => {
      //   const res = await getStream({ id: item.id });
      //   const href = window.URL.createObjectURL(res.data); //转成格式
      //   this.$set(item, "imgUrl", href);
      // });
    },
    // 获取告警数量
    async getCounter() {
      const data = await getCounter();
      this.alarmsCount = data.data;
    },
    // 创建链接
    connectWebsocket() {
      if (typeof WebSocket === "undefined") {
        console.log(this.$t('applicationMonitoring.alarmmanagement.5n3r45'));
        return;
      } else {
        let cookie = Cookies.get("X-Token") || this.$route.query.token;
        let url = `${VUE_APP_WS_BASE_URL}/report/${cookie}`;
        // 打开一个
        this.websocket = new WebSocket(url);
        // 建立连接
        this.websocket.onopen = () => {
          console.log(this.$t('applicationMonitoring.alarmmanagement.861i43'));
        };
        // 客户端接收服务端返回的数据
        this.websocket.onmessage = (evt) => {
          const data = JSON.parse(evt.data);
          if (data.type == "REPORT_SHOW") {
            if (this.alarmSwitch) {
              this.alarmVisible = false;
              clearTimeout(this.timer);
              this.$nextTick(() => {
                this.alarmData = data;
                this.alarmVisible = true;
                this.timer = setTimeout(() => {
                  this.alarmVisible = false;
                }, 5000);
              });
            }
          }
          this.getAlarms();
          this.getCounter();
        };
        // 发生错误时
        this.websocket.onerror = (evt) => {
          console.log(this.$t('applicationMonitoring.alarmmanagement.s4s787'), evt);
        };
        // 关闭连接
        this.websocket.onclose = (evt) => {
          console.log(this.$t('applicationMonitoring.alarmmanagement.2ygku1'), evt);
        };
      }
    },
    changeSelect(val, index) {
      this.playerList[index].destroy();
      this.activeList[index] = val;
      getPlayUrl({ cameraId: this.activeList[index].id }).then((res) => {
        const playUrl = res.data;
        this.$nextTick(() => {
          this.playerList[index] = new Player({
            id: "video" + this.activeList[index].id,
            isLive: true,
            playsinline: true,
            url: playUrl,
            volume: 0,
            autoplay: true,
            fluid: true,
            plugins: [FlvPlugin],
            width: this.$refs.videoItem[0].offsetWidth,
            height: this.$refs.videoItem[0].offsetHeight,
          });
        });
      });
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getListPageActives();
    },
    // 查看当日
    detailFun() {
      this.detailVisible = true;
    },
    // 关闭弹窗
    closeHandle() {
      this.detailVisible = false;
      this.getAlgorithms();
      this.getListPageActives();
      this.getAlarms();
      this.getCounter();
      this.getCameraListData();
      this.connectWebsocket();
    },
  },
  beforeDestroy() {
    clearInterval(this.timerPlayings);
    this.timerPlayings = "";
    this.playerList.forEach((item) => {
      item.pause();
      item.unload();
      item.detachMediaElement();
      item.destroy();
    });
    this.playerList = [];
    this.websocket.close();
  },
};
</script>
<style scoped lang="scss">
.wrap {
  display: flex;
  .flex-left {
    flex: 1;
    .algorithms-list {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .video-box {
      width: 100%;
      height: 530px;
      background: rgba(47, 64, 86, 0.7);
      display: grid;
      overflow: hidden;
      .video-item {
        border: 1px #2f4056 solid;
        display: flex;
        align-items: center;
        position: relative;
        .video {
          position: absolute;
        }
        .dropdown {
          position: absolute;
          right: 10px;
          top: 10px;
          z-index: 10;
        }
      }
    }
    .video1 {
      grid-template-columns: 100%;
      grid-template-rows: 100%;
    }
    .video4 {
      grid-template-columns: 50% 50%;
      grid-template-rows: 50% 50%;
    }
    .video6 {
      grid-template-columns: 33.33% 33.33% 33.33%;
      grid-template-rows: 50% 50% 50%;
    }
  }
  .flex-right {
    width: 280px;
    margin-left: 20px;
    flex-shrink: 0;

    .count {
      font-size: 16px;
      font-weight: bold;
      display: flex;
      align-items: center;
      justify-content: center;
      span {
        font-size: 24px;
        color: #f43838;
      }
    }
    .scrollbar-wrapper {
      height: calc(100vh - 265px);
      overflow: hidden !important;
    }
    .list {
      li {
        border-bottom: 1px #ccc solid;
        margin-top: 20px;
        .img {
          width: 100%;
          height: 100px;
        }
      }
    }
  }
}
</style>
