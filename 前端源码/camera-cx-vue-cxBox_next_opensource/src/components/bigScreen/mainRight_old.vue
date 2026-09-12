<template>
  <div class="mainRight">
    <div class="header">
      <div class="headerTitle">
        <Title :name="$t('bigscreen.mainright.200fo6')" />
      </div>
      <div class="oprBtn">
        <div class="btn" @click="changeSreen(1)">
          {{ $t("applicationMonitoring.common.oneScreen") }}
        </div>
        <div class="btn" @click="changeSreen(4)">
          {{ $t("applicationMonitoring.common.fourScreen") }}
        </div>
      </div>
    </div>
    <div class="videoMain">
      <div
        v-for="(item, key) in videoArr"
        :key="key"
        :class="{
          video4: screenNum == 4,
          video1: screenNum == 1,
          video6: screenNum == 6,
        }"
        ref="videobg"
      >
        <div class="video">
          <VideoItem :cameraId="item.id" :index="key"></VideoItem>
        </div>
        <div class="videoName">
          {{ item.name }}
        </div>
      </div>
    </div>
    <Title :name="$t('bigscreen.mainright_old.v005w8')"></Title>
    <div>
      <div class="status-title">
        <div class="border-left-sm"></div>
        <div class="title-font-sm">
          {{ $t("bigscreen.mainright_old.wx81e4") }}
        </div>
      </div>
      <LineEchart
        :xLabel="xlabelArr"
        :goToSchool="goToSchoolArr"
        :show="true"
      />
    </div>
    <div>
      <div class="status-title">
        <div class="border-left-sm"></div>
        <div class="title-font-sm">
          {{ $t("bigscreen.mainright_old.o7ri79") }}
        </div>
      </div>
      <nightingale :pireArr="pireArr" :show="true" />
    </div>
  </div>
</template>

<script>
import Title from "./Title";
import VideoItem from "@/components/bigVideoItem.vue";
import LineEchart from "@/components/echart/line";
import nightingale from "@/components/echart/nightingale";
import request from "@/utils/request.js";
export default {
  data() {
    return {
      screenNum: 4, // 默认屏幕数量
      hackReset: false, // 强制刷新子组件
      videoElement: null,
      flvPlayer: null,
      realVideoArr: [],
      videoArr: [],

      xlabelArr: [],
      goToSchoolArr: [],
      pireArr: [],
    };
  },
  components: {
    Title,
    VideoItem,
    LineEchart,
    nightingale,
  },
  mounted() {
    this.getActiveCameras();
    this.getCountAlgorithm7Day();
    this.getCountAlgorithmCount7Day();
  },
  beforeDestroy() {
    // 在组件销毁前清除定时器
    clearInterval(this.timer);
  },
  methods: {
    changeSreen(num) {
      this.screenNum = num;
      this.cutSreenData(this.realVideoArr, this.screenNum);
    },
    getActiveCameras() {
      request
        .post("/camera/listPageActives?page=1&limit=4")
        .then(({ data }) => {
          this.realVideoArr = data;
          this.cutSreenData(this.realVideoArr, this.screenNum);
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
    },
    refreshChild() {
      this.hackReset = false;
      this.$nextTick(() => {
        this.hackReset = true;
      });
    },
    getCountAlgorithmCount7Day() {
      request.get("/statistic/countAlgorithmCount7Day").then(({ data }) => {
        this.dayNum = data.total;
        for (let item of data.datas) {
          this.xlabelArr.push(item.name);
          this.goToSchoolArr.push(item.count);
        }
      });
    },

    getCountAlgorithm7Day() {
      request.get("/statistic/countAlgorithm7Day").then(({ data }) => {
        let resData = data.length > 10 ? data.slice(0, 10) : data;
        for (let item of resData) {
          let obj = {
            value: item.count,
            name: item.name,
          };
          this.pireArr.push(obj);
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.mainRight {
  width: 560px;
  .header {
    display: flex;
    justify-content: space-between;
    .headerTitle {
      flex: 1;
    }
    .oprBtn {
      width: 120px;
      height: 40px;
      position: absolute;
      right: 0;
      border-radius: 25px;
      border: 1px solid transparent;
      background-clip: padding-box, border-box;
      background-origin: padding-box, border-box;
      background-image: linear-gradient(to right, #499de0, #499de0),
        linear-gradient(to bottom, #499de0, #021034);
      display: flex;
      align-items: center;
      .btn {
        flex: 1;
        text-align: center;
        color: #fff;
        cursor: pointer;
      }
    }
  }
  .videoMain {
    padding: 6px;
    display: flex;
    flex-wrap: wrap;
    height: 300px;
    .video1 {
      // width: 100%;
      // height: 100%;
      width: 510px;
      height: 300px;
      padding: 0 12px;
      display: flex;
      flex-direction: column;
    }
    .video4 {
      width: 45%;
      height: 150px;
      padding: 0 12px;
      display: flex;
      flex-direction: column;
    }
    .video6 {
      width: 33%;
      height: 150px;
      padding: 0 12px;
      display: flex;
      flex-direction: column;
    }
    .video {
      // flex: 1;
      width: 100%;
      height: 100%;
      background: #000;
    }
    .videoName {
      height: 20px;
      width: 100%;
      color: #4191eb;
      font-weight: 400;
      font-size: 13px;
      line-height: 20px;
    }
    .player {
      height: 100%;
      width: 100%;
    }
  }
  .status-title {
    display: flex;
    margin-bottom: 25px;
    .border-left-sm {
      height: 20px;
      border-left: 3px solid #20ddff;
    }

    .title-font-sm {
      margin-left: 10px;
      font-size: 13px;
      color: #19a9cd;
    }
  }
}
</style>
