<template>
  <div>
    <div class="popup-bg" v-if="dialogVisible">
      <div
        :class="[
          alarmData.algorithmNameEn == 'hook_detect' ? 'popupA' : 'popup',
        ]"
      >
        <div class="close" @click="closed"></div>
        <!-- <img v-if="imgUrl" :src="imgUrl" class="img" />
        <div class="text1">{{ alarmData.algorithmName }}</div>
        <div class="text2">{{ alarmData.alarmInterval }}</div> -->
        <!-- <img v-if="alarmData.id" :src="VUE_APP_API_BASE_URL+'/report/streamThumb?id='+alarmData.id" class="img" /> -->

        <div class="result">
          <div class="img-box">
            <div class="img-content">
              <div
                :class="item.type == 'hook' ? 'xboxA' : 'xbox'"
                v-for="(item, index) in pointList"
                :key="index"
                :style="{
                  width: item.width,
                  height: item.height,
                  left: item.left,
                  top: item.top,
                }"
              >
                <div
                  class="text"
                  :style="{
                    color: '#fff',
                    textAlign: 'center',
                    backgroundColor: item.type == 'hook' ? 'green' : 'red',
                  }"
                >
                  {{ item.confidence }}
                </div>
              </div>
              <img
                id="img_alarm_popup"
                v-if="alarmData.id"
                :src="fileUrl"
                class="img"
              />
            </div>
          </div>
        </div>
        <div class="text1">{{ alarmData.cameraName }}</div>
        <div class="text2">{{ alarmData.algorithmName }}</div>
        <div class="text3">{{ alarmData.alarmTime }}</div>
      </div>
    </div>
  </div>
</template>
<script>
import { getStream } from "@/api/common";
export default {
  props: {
    alarmData: {
      type: Object,
      default: () => {},
    },
    fileUrl: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      imgUrl: "",
      // VUE_APP_API_BASE_URL,
      pointList: [],
      imgHeight: "",
    };
  },
  watch: {
    alarmData: {
      immediate: true,
      handler(val) {
        let arr = val.params ? JSON.parse(val.params) : [];
        this.$nextTick(() => {
          this.pointList = [];
          let img = new Image();
          img.src = this.fileUrl;
          if (!this.fileUrl) {
            return;
          }
          img.onload = () => {
            this.imgHeight = img.height;
            setTimeout(() => {
              const ratio =
                document.getElementById("img_alarm_popup").offsetHeight /
                this.imgHeight;
              arr.forEach((item) => {
                if (item.position[3] * 0.5 > this.imgHeight) {
                  item.position[3] = this.imgHeight / 0.5;
                }
                let leftNum = item.position[0] * ratio * 0.5;
                this.pointList.push({
                  ...item,
                  left: leftNum > 0 ? leftNum + "px" : "0px",
                  top: item.position[1] * ratio * 0.5 - 5 + "px",
                  width:
                    (item.position[2] - item.position[0]) * ratio * 0.5 + "px",
                  height:
                    (item.position[3] - item.position[1]) * ratio * 0.5 + "px",
                });
              });
            }, 50);
          };
        });
      },
    },
  },
  async created() {
    // const res = await getStream({ id: this.alarmData.id });
    // const href = window.URL.createObjectURL(res.data); //转成格式
    // this.imgUrl = href;
  },
  methods: {
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss">
.popup-bg {
  // background: rgba($color: #000000, $alpha: 0.5);
  .popupA {
    width: 384.5px;
    height: 178px;
    background: url("~@/assets/images/videoStreamManagement/popupBg.png")
      no-repeat center center;
    background-size: 100% 100%;
    position: fixed;
    right: 0;
    bottom: 0;
    margin-left: -300px;
    margin-top: -175px;
    z-index: 30;
    .close {
      width: 45px;
      height: 45px;
      position: absolute;
      top: 0;
      right: 0;
      cursor: pointer;
    }
    .img {
      width: 237px;
      // height: 117px;
      // position: absolute;
      // top: 120px;
      // left: 35px;
      object-fit: cover;
      display: block;
    }
    .text1 {
      width: 200px;
      height: auto;
      font-size: 12px;
      color: #fff;
      position: absolute;
      top: 53px;
      left: 235px;
      transform: scale(0.7);
    }
    .text2 {
      width: 200px;
      height: auto;
      font-size: 12px;
      color: #fff;
      position: absolute;
      top: 100px;
      left: 235px;
      transform: scale(0.7);
    }
    .text3 {
      width: 200px;
      height: auto;
      font-size: 12px;
      color: #fff;
      position: absolute;
      top: 150px;
      left: 235px;
      transform: scale(0.7);
    }
    .img-box {
      width: 237px;
      height: 117px;
      position: absolute;
      top: 33px;
      left: 12px;
      .img-content {
        position: relative;
      }
    }
  }
  .popup {
    width: 769px;
    height: 360px;
    background: url("~@/assets/images/videoStreamManagement/popupBg.png")
      no-repeat center center;
    background-size: 100% 100%;
    position: fixed;
    left: 50%;
    top: 50%;
    margin-left: -300px;
    margin-top: -175px;
    z-index: 30;
    .close {
      width: 45px;
      height: 45px;
      position: absolute;
      top: 0;
      right: 0;
      cursor: pointer;
    }
    .img {
      width: 474px;
      max-height: 266px;
      // position: absolute;
      // top: 120px;
      // left: 35px;
      object-fit: cover;
      display: block;
    }
    .text1 {
      width: 200px;
      height: auto;
      font-size: 13px;
      color: #fff;
      position: absolute;
      top: 112px;
      left: 526px;
    }
    .text2 {
      width: 200px;
      height: auto;
      font-size: 13px;
      color: #fff;
      position: absolute;
      top: 210px;
      left: 526px;
    }
    .text3 {
      width: 200px;
      height: auto;
      font-size: 13px;
      color: #fff;
      position: absolute;
      top: 310px;
      left: 526px;
    }
    .img-box {
      width: 474px;
      height: 266px;
      position: absolute;
      top: 72px;
      left: 23px;
      .img-content {
        position: relative;
      }
    }
  }
  .result {
    width: 247px;

    .xbox {
      position: absolute;
      border: 2px solid #f43838;
      background-color: rgba(255, 0, 0, 0.2);
      font-size: 12px;
      color: #1e9fff;
      z-index: 2;
    }
    .xboxA {
      position: absolute;
      border: 2px solid #2b8904;
      background-color: rgba(102, 142, 6, 0.2);
      font-size: 12px;
      color: #1e9fff;
      z-index: 2;
    }
  }
}
</style>
