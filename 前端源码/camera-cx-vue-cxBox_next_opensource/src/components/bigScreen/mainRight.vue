<template>
  <div class="mainRight">
    <div style="border: 1px solid #003c74; margin: 0 10px 10px">
      <div class="header">
        <div class="headerTitle" style="position: relative">
          <Title :name="$t('bigscreen.mainright.200fo6')" />

          <!-- <div class="oprBtn">
             <div class="btn" @click="changeSreen(1)">Single Screen</div>
             <div class="btn" @click="changeSreen(4)">Four Screens</div>
           </div> -->
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
            <VideoItem
              :cameraOptions="cameraOptions"
              :Id="item.id"
              :index="key"
              :ref="'video' + key"
            ></VideoItem>
          </div>
          <div class="videoName">
            {{ item.name }}
          </div>
        </div>
      </div>
    </div>

    <!-- <div style="border: 1px solid #003c74; margin: 0 10px 10px">
      <Title :name="$t('bigscreen.mainright.27b247')"></Title>
      <div style="padding: 10px">
        <nightingale2 v-if="pireArr2.length > 0" :pireArr="pireArr2" />
      </div>
    </div> -->

    <div style="border: 1px solid #003c74; margin: 0 10px 10px">
      <Title :name="$t('bigscreen.mainright.x4h0un')"></Title>
      <div style="padding: 10px">
        <!--        <div class="status-title">-->
        <!--            <div class="border-left-sm"></div>-->
        <!--            <div class="title-font-sm">近7日告警数量趋势分析图</div>-->
        <!--        </div>-->
        <LineEchart
          v-if="xlabelArr.length > 0"
          :xLabel="xlabelArr"
          :goToSchool="goToSchoolArr"
        />
      </div>
    </div>
    <div>
      <div style="border: 1px solid #003c74;margin: 0 10px 10px;">

        <Title :name="$t('bigscreen.mainright.a7k3m2')"></Title>
        
        <div style="padding: 10px;">
          <nightingale v-if="pireArr.length>0" :pireArr="pireArr" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Title from "./Title";
import VideoItem from "@/components/bigVideoItem.vue";
import LineEchart from "@/components/echart/line/newChart.vue";
import LineEchart2 from "@/components/echart/line/newChart2.vue";
import nightingale from "@/components/echart/nightingale/newChart.vue";
import nightingale2 from "@/components/echart/nightingale/newChartForAuditResultStatics.vue";
import request from "@/utils/request.js";
export default {
  data() {
    return {
      screenNum: 1, // 默认屏幕数量
      hackReset: false, // 强制刷新子组件
      videoElement: null,
      flvPlayer: null,
      realVideoArr: [],
      videoArr: [],
      xlabelArr: [],
      goToSchoolArr: [],
      pireArr: [],
      pireArr2: [],
      cameraOptions: [],
      timer: null,
    };
  },
  components: {
    Title,
    VideoItem,
    LineEchart2,
    LineEchart,
    nightingale,
    nightingale2,
  },
  mounted() {
    this.getCameraListData();
    this.getActiveCameras();
    this.getCountAlgorithm7Day();
    this.getCountAlgorithmCount7Day();
    // this.getAuditResultStatics7Day();

    // 创建图表数据刷新任务
    this.timer = setInterval(this.reloadChartDatas, 60000);
  },
  beforeDestroy() {
    // 在组件销毁前清除定时器
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  methods: {
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
      // if(this.videoArr.length>0){//每10秒提交一次正在播放的摄像头
      //     this.savePlayings();
      //     let that =this;
      //     this.timer = setInterval(function () {
      //         that.savePlayings();
      //     }, 10000);
      // }
    },
    savePlayings() {
      // let newArr = [];
      // if(this.videoArr.length>0){
      //   this.videoArr.forEach((item)=>{
      //     if(item.id){
      //       newArr.push(item.id)
      //     }
      //   })
      // }
      // request.post('/stream/playings',{cameraIds:newArr}).then(({data}) => {
      // }).catch(res => {
      //   clearInterval(this.timerPlayings);
      //   this.timerPlayings=''
      // })
    },
    refreshChild() {
      this.hackReset = false;
      this.$nextTick(() => {
        this.hackReset = true;
      });
    },
    getCountAlgorithmCount7Day() {
      request.get("/statistic/countAlgorithmCount7Day").then(({ data }) => {
        this.xlabelArr = [];
        this.goToSchoolArr = [];
        this.dayNum = data.total;
        for (let item of data.datas) {
          this.xlabelArr.push(item.name);
          this.goToSchoolArr.push(item.count);
        }
      });
    },

    getCountAlgorithm7Day() {
      request.get("/statistic/countAlgorithm7Day").then(({ data }) => {
        this.pireArr = [];
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

    getAuditResultStatics7Day() {
      let form = new FormData();

      form.append(
        "startDate",
        this.$moment(new Date()).subtract(7, "d").format("yyyy-MM-DD HH:mm:ss")
      );
      form.append(
        "endDate",
        this.$moment(new Date()).format("yyyy-MM-DD HH:mm:ss")
      );

      request.post("/report/auditResultStatics", form).then(({ data }) => {
        this.pireArr2 = [];
        data.forEach((item) => {
          if (item.type !== "total") {
            let obj = {
              value: item.count,
              name: item.name,
            };
            this.pireArr2.push(obj);
          }
        });
      });
    },
    // 刷新图表数据
    reloadChartDatas() {
      this.getCountAlgorithm7Day();
      this.getCountAlgorithmCount7Day();
      // this.getAuditResultStatics7Day();
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
      width: 100px;
      height: 30px;
      position: absolute;
      top: 5px;
      right: 20px;
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
    height: 256px;
    .video1 {
      // width: 100%;
      // height: 100%;
      width: 510px;
      height: 256px;
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
