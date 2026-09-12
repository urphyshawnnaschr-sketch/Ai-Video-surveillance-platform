<template><div class="main1"><div class="mainLeft"><div style="border: 1px solid #003c74; margin-bottom: 10px"><Title :name="$t('bigscreen.mainleft.kp3c04')" /><div class="creamBox" style="padding: 10px"><div class="numbg1"><div style="height: 30px; font-size: 40px; margin-bottom: 10px">{{runingNum}}<span style="font-size: 16px">{{$t("bigscreen.mainleft.8ye8wj")}}</span></div><div style="height: 30px; font-size: 16px">{{$t("bigscreen.mainleft.p122g2")}}</div></div><div class="numbg2"><div style="height: 30px; font-size: 40px; margin-bottom: 10px">{{totleNum}}<span style="font-size: 16px">{{$t("bigscreen.mainleft.8ye8wj")}}</span></div><div style="height: 30px; font-size: 16px">{{$t("bigscreen.mainleft.96uz76")}}</div></div></div></div><div style="border: 1px solid #003c74; margin-bottom: 10px"><Title :name="$t('bigscreen.mainleft.tdget5')" /><div style="width: 410px; height: 326px; padding: 10px"><Category /></div></div><div style="border: 1px solid #003c74"><Title :name="$t('bigscreen.mainleft.2dvar7')"><el-link
            type="primary"
            style="font-size: 13px; color: #fff; cursor: pointer"
            :underline="false"
            @click="goAlarmList"
            >{{$t("bigscreen.mainleft.k89i56")}}</el-link
          ></Title><div><alarmList :data="warningList" /></div></div></div><div class="mainCenter"><!--<Title name="System Management":isShow="true"/> <div class="sysBox"> <sysMage v-for="(item,index) in systemData":key="index":name="item.name":url="item.src":num="item.num"></sysMage> </div> <Title name="This Day Alert Situation Analyze Count image":isShow="true"/> <div style="display: flex"> <div style="width: 50%;height: 340px;"> <annular:dataArr="countAlgorithm1DayArr":show="true"/> <annular v-if="countAlgorithm1DayArr.length>0":dataArr="countAlgorithm1DayArr"/> </div> <div> <Map /> </div> </div>--><div style="border: 1px solid #003c74;margin: 0 0 10px 10px;"><div class="header"><div class="headerTitle" style="position: relative"><Title :name="$t('bigscreen.mainleft.43m765')"/><div class="oprBtn"><div :class="configId == item.id?'btn btnActive':'btn'" v-for="(item,index) in mapList" :key="item.id" @click="changeMap(item)">{{item.name}}</div><!--<div class="btn"@click="changeSreen(4)"> Four Screen </div>--></div></div></div><div class="videoMain"><div style="height:100%;width:100%" v-for="(item,index) in mapList" :key="item.id+index" v-if="item.id == configId"><images :image-url="getMapImageUrl(item.cover)"
                :marker-icon-url="getMapImageUrl(item.svgName)" :markersEd="markersEd" /></div></div></div><div style="border: 1px solid #003c74; margin: 0 0 0 10px"><Title :name="$t('bigscreen.mainleft.wxj36q')" :isShow="true"><el-link
            type="primary"
            style="font-size: 13px; color: #fff; cursor: pointer"
            :underline="false"
            @click="goAlarmList"
            >{{$t("bigscreen.mainleft.k89i56")}}</el-link
          ></Title><div style="width: 100%"><alarmListPicture :dataList="warningList" /></div></div></div></div></template>

<script>
import Title from "./Title";
import request from "@/utils/request.js";
import Category from "@/components/echart/earlyWarning/newChart.vue";
import alarmList from "@/components/alarmListCopy";
import sysMage from "@/components/systemManageCopy";
import annular from "@/components/echart/annular/newChart.vue";
import VideoItem from "@/components/bigVideoItem.vue";
// import Map from "@/components/echart/map/index.vue";
import alarmListPicture from "@/components/alarmListPictureCopy";
import ScoketService from "@/utils/websocket.js";
import images from "./components/images.vue";
import { configList, stat } from "@/api/annotationPlatform/mapImageManagent";
import alarmMap from '../../bigScreen/components/alarmMap.vue';
import Cookies from "js-cookie";
export default {
  data() {
    return {
      runingNum: 0,
      totleNum: 0,
      warningList: [],

      systemData: [
        {
          src: require("@/assets/img/resBg.png"),
          name: this.$t("bigscreen.mainleft.2iv356"),
          label: "server_count",
          num: 0,
        },
        {
          src: require("@/assets/img/resBg1.png"),
          name: this.$t("bigscreen.mainleft.5sq66y"),
          label: "dataset_count",
          num: 0,
        },
        {
          src: require("@/assets/img/resBg2.png"),
          name: this.$t("bigscreen.mainleft.756bc9"),
          label: "today_dataset_count",
          num: 0,
        },
        {
          src: require("@/assets/img/resBg3.png"),
          name: this.$t("bigscreen.mainleft.wbbe1p"),
          label: "mark_dataset_count",
          num: 0,
        },
        {
          src: require("@/assets/img/resBg4.png"),
          name: this.$t("bigscreen.mainleft.28lg5d"),
          label: "task_count",
          num: 0,
        },
        {
          src: require("@/assets/img/resBg5.png"),
          name: this.$t("bigscreen.mainleft.948w0b"),
          label: "model_train_count",
          num: 0,
        },
        {
          src: require("@/assets/img/resBg6.png"),
          name: this.$t("bigscreen.mainleft.p7q864"),
          label: "model_deploy_count",
          num: 0,
        },
        {
          src: require("@/assets/img/resBg7.png"),
          name: this.$t("bigscreen.mainleft.k89i55"),
          label: "model_image_count",
          num: 0,
        },
      ],

      countAlgorithm1DayArr: [],
      ws: null,

      cameraOptions: [],
      videoArr: [],
      realVideoArr: [],
      screenNum: 1, // 默认屏幕数量
      mapList: [],
      markersEd: [],
      configId: "",
      mapTimer: null,
      VUE_APP_API_BASE_URL,
    };
  },
  components: {
    VideoItem,
    Title,
    Category,
    alarmList,
    sysMage,
    annular,
    Map,
    alarmListPicture,
    images,
    alarmMap
  },
  mounted() {
    this.getCameraListData();
    this.getCountCamera();
    this.getCountNewly();
    this.getActiveCameras();
    this.timer = setInterval(() => {
      this.getCountNewly(); // 发送接口请求
    }, 10000); // 每10秒请求一次接口
    this.getCountData();
    this.getCountAlgorithm1Day();
    // ScoketService.Instance.connect(
    //     `${VUE_APP_WS_BASE_URL}/report/${this.guid()}`
    // );
    // ScoketService.Instance.send({
    //     action: "REPORT_SHOW",
    // });
    // ScoketService.Instance.registerCallBack("REPORT_SHOW", (data) => {
    //     this.warningList.push(data)
    // });

    this.getConfigList();
    // 开启地图定时器
    this.mapTimer = setInterval(this.stat, 15000); // 每10秒请求一次接口
  },
  beforeDestroy() {
    // 在组件销毁前清除定时器
    clearInterval(this.timer);
    // 关闭
    ScoketService.Instance.onClose();
    // 关闭地图定时器
    if (this.mapTimer) {
      clearInterval(this.mapTimer);
      this.mapTimer = null;
    }
  },
  methods: {
    ///获取当前图层的设备图标
    stat() {
      stat({
        mapId: this.configId,
      }).then((res) => {
        console.log(res);
        //this.formLabelAlign.rules = res.data
        //this.tableDataEd = res.data;
        this.markersEd = res.data.map((item) => {
          return {
            originX: item.position[0],
            originY: item.position[1],
            ...item,
          };
        });
        console.log(this.markersEd, "?????");
      });
    },
    async getConfigList() {
      const res = await configList();
      this.mapList = res.data;
      console.log(this.mapList,"========m")
      if (this.mapList.length > 0) {
        this.configId = this.mapList[0].id;
        this.changeMap(this.mapList[0]);
      }
    },
    changeMap(item) {
      this.configId = item.id;
      this.stat();
    },
    // 获取摄像头列表
    getCameraListData() {
      request.post("/camera/listData2").then(({ data }) => {
        // let Arr = data.filter(item=>{
        //     return item.running == "1"
        // })
        //this.cameraOptions = Arr;
        this.cameraOptions = data;
      });
    },
    getActiveCameras() {
      request
        .post("/camera/listPageActives?page=1&limit=4")
        .then(({ data }) => {
          this.realVideoArr = data;
          this.cutSreenData(this.realVideoArr, this.screenNum);
        });
    },
    changeSreen(num) {
      this.screenNum = num;
      this.cutSreenData(this.realVideoArr, this.screenNum);
    },
    getCountCamera() {
      request.get("/statistic/countCamera").then(({ data }) => {
        this.runingNum = data.runing;
        this.totleNum = data.total;
      });
    },
    cutSreenData(data, index) {
      let arr = JSON.parse(JSON.stringify(data)); // 由于数据的唯一性，进行数据深拷贝
      if (arr.length < index) {
        let empyArr = new Array(index - arr.length).fill({});
        arr = arr.concat(empyArr);
      }
      this.videoArr = arr.splice(0, index); // 截取数据
      this.refreshChild();
      // if(this.videoArr.length>0){//每10秒提交一次正在播放的摄像头
      //     this.savePlayings();
      //     let that =this;
      //     this.timer = setInterval(function () {
      //         that.savePlayings();
      //     }, 10000);
      // }
    },
    refreshChild() {
      this.hackReset = false;
      this.$nextTick(() => {
        this.hackReset = true;
      });
    },
    getCountNewly() {
      request.get("/statistic/countNewly").then(({ data }) => {
        this.warningList = data;
      });
    },
    guid() {
      return "xxx-xxx-4y".replace(/[xy]/g, function (c) {
        var r = (Math.random() * 16) | 0,
          v = c == "x" ? r : (r & 0x3) | 0x8;
        return v.toString(16);
      });
    },
    getCountData() {
      request.get("/statistic/countData").then(({ data }) => {
        for (let i in data) {
          for (let item of this.systemData) {
            if (item.label == i) {
              item.num = data[i];
            }
          }
        }
      });
    },
    getCountAlgorithm1Day() {
      request.get("/statistic/countAlgorithm1Day").then(({ data }) => {
        this.dayNum = data.total;
        let resData =
          data.datas.length > 7 ? data.datas.slice(0, 7) : data.datas;
        for (let item of resData) {
          let obj = {
            name: item.name,
            value: item.count,
          };
          this.countAlgorithm1DayArr.push(obj);
        }
      });
    },
    goAlarmList() {
      this.$router.push("/dataManagement/alarmManagement");
    },
    getMapImageUrl(filename) {
      console.log(filename);
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/map/file/stream?filename=${filename}&X-Token=${token}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.main1 {
  display: flex;
}
.mainLeft {
  width: 510px;
  .creamBox {
    display: flex;
    justify-content: space-around;
    .numbg1,
    .numbg2 {
      background: url("@/assets/images/yuan1.png") no-repeat;
      width: 142px;
      height: 142px;
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #fff;
    }
    .numbg2 {
      background: url("@/assets/images/yuan2.png") no-repeat;
    }
  }
}

.mainCenter {
  width: 810px;
  .sysBox {
    display: flex;
  }
}

.header {
  display: flex;
  justify-content: space-between;
  .headerTitle {
    flex: 1;
  }
  .oprBtn {
    //width: 100px;
    height: 30px;
    position: absolute;
    top: 5px;
    right: 20px;
    //border-radius: 25px;
    // border: 1px solid transparent;
    // background-clip: padding-box, border-box;
    // background-origin: padding-box, border-box;
    //background-image: linear-gradient(to right, #499DE0, #499DE0), linear-gradient(to bottom, #499DE0, #021034);
    display: flex;
    align-items: center;
    .btn {
      text-align: center;
      color: #fff;
      cursor: pointer;
      border-radius: 8px;
      background: linear-gradient(
        127deg,
        rgba(6, 11, 38, 0.74) 28.26%,
        rgba(26, 31, 55, 0.5) 91.2%
      );
      backdrop-filter: blur(60px);
      padding: 6px 15px;
      font-size: 13px;
      flex-shrink: 0;
    }
    .btnActive {
      border-radius: 8px;
      background: linear-gradient(
        127deg,
        #0075ff 28.26%,
        rgba(26, 31, 55, 0.5) 91.2%
      );
      backdrop-filter: blur(60px);
    }
  }
}

.videoMain {
  padding: 6px;
  height: 440px;
}
</style>
