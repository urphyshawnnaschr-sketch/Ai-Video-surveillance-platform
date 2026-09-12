<template>
  <div class="video-cont">
    <div class="action-mask" v-show="showErrorMask" style="color: red !important;" >
      <el-link type="danger" @click="fetchPlayUrl">{{ errorState }}</el-link>
    </div>
    <div class="action-mask" v-show="showLoadMask" style="color: #ffffff">
      <div><i class="el-icon-loading" />{{ $t("videobox.index.lqy3y4") }}</div>
    </div>

    <video
      id="video"
      ref="video"
      controls
      autoplay
      muted
      width="100%"
      height="100%"
    ></video>
    <div class="dropdown">
      <el-select
        :placeholder="$t('applicationMonitoring.common.videoSource')"
        v-model="cameraId"
        class="head-container-input"
        @change="changeSelect()"
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
    <div v-if="!Id" style="width: 100%; text-align: center">
      {{ $t("videobox.index.96pi56") }}
    </div>
  </div>
</template>

<script>
import Hls from "hls.js";
import { getBoxPlayUrl } from "@/api/applicationMonitoring/videoStreamManagement";
export default {
  props: {
    cameraOptions: {
      type: Array,
      default: [],
    },
    index: {
      type: Number,
      default: null,
    },
    Id: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      recvOnly: true,
      streamUrl: "",
      resolution: {
        height: 360,
        label: "nHD",
        width: 640,
      },
      resolutionSel: "nHD",
      resolutionOpt: [],
      useCamera: false,
      playType: 0,
      cameraId: "",
      timer: null,
      timerObj: null,
      // 播放器
      webrtcPlayer: null,
      // 播放地址
      webrtcUrl: null,
      // hls播放器
      hlsPlayer: null,
      // hls播放地址
      hlsUrl: null,
      // 显示播放错误提示
      showErrorMask: false,
      // 显示加载状态提示
      showLoadMask: true,
      // 视频无法播放提示
      errorState: this.$t("videobox.index.08h2i8"),
    };
  },
  created() {
    // 当前摄像头ID
    this.cameraId = this.Id;
    //console.log("Video created", this.cameraId);
    // 播放
    this.fetchPlayUrl();
  },
  beforeDestroy() {
    //console.log('Video destroyed', this.cameraId);
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
    if (this.timerObj) {
      clearInterval(this.timerObj);
      this.timerObj = null;
    }

    // 关闭播放器
    this.closeWebrtc();
    // 关闭播放器
    this.closeHls();
  },
  methods: {
    // 获取视频流
    fetchPlayUrl(src) {
      // 播放播放器
      this.closeWebrtc();
      // 播放播放器
      this.closeHls();

      //
      this.showErrorMask = false;
      this.showLoadMask = true;

      // 重新获取播放地址
      getBoxPlayUrl({ cameraId: this.cameraId, playType: this.playType })
        .then((res) => {
          // 关闭视频加载提示
          this.showLoadMask = false;

          if (res.data.code == 200) {
            const codec = res.data.codec;
            if (codec == "H264") {
              this.webrtcUrl = res.data.url;
              this.playWebrtc();
            }

            if (codec == "H265") {
              this.hlsUrl = res.data.url;
              this.playHls();
            }
          } else {
            this.errorState = this.$t("videobox.index.545os5");
            this.showErrorMask = true;
          }
        })
        .catch((err) => {
          // console.log(err);
          this.errorState = this.$t("videobox.index.ob42b4");
          this.showErrorMask = true;
        });
    },
    // 切换摄像头
    changeSelect() {
      this.fetchPlayUrl("changeSelect");
    },
    //播放
    playWebrtc() {
      try {
        this.webrtcPlayer = new ZLMRTCClient.Endpoint({
          element: this.$refs.video,
          debug: false,
          zlmsdpUrl: this.webrtcUrl,
          simulcast: false,
          useCamera: false,
          audioEnable: false,
          videoEnable: true,
          recvOnly: true,
          resolution: this.resolution,
          usedatachannel: false,
        });

        var that = this;

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_NOT_SUPPORT,
          function (e) {
            that.errorState = this.$t("videobox.index.5by3gv");
            that.showErrorMask = true;
            that.closeWebrtc();
          }
        );

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_ICE_CANDIDATE_ERROR,
          function (e) {
            // ICE 协商出错
            that.errorState = this.$t("videobox.index.1r36o4");
            that.showErrorMask = true;
            that.closeWebrtc();
          }
        );

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_ON_REMOTE_STREAMS,
          function (e) {
            //获取到了远端流，可以播放
            //console.log(this.$t("videobox.index.54y6ax"), e.streams);
            that.errorState = "";
            that.showErrorMask = false;
            that.showLoadMask = false;
          }
        );

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_OFFER_ANWSER_EXCHANGE_FAILED,
          function (e) {
            // offer anwser 交换失败
            that.errorState = this.$t("videobox.index.k5sx7k");
            that.showErrorMask = true;
            that.closeWebrtc();
          }
        );

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_ON_CONNECTION_STATE_CHANGE,
          function (state) {
            // RTC 状态变化 ,详情参考 https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection/connectionState
            //console.log('state', state)
            if (state == "disconnected") {
              that.errorState = this.$t("videobox.index.f73jj6");
              that.showErrorMask = true;
              that.closeWebrtc();
            }

            if (state == "failed") {
              that.errorState = this.$t("videobox.index.f73jj6");
              that.showErrorMask = true;
              that.closeWebrtc();
            }

            if (state == "closed") {
              that.errorState = this.$t("videobox.index.g60a70");
              that.showErrorMask = true;
              that.closeWebrtc();
            }

            if (state == "connected") {
              that.errorState = "";
              that.showErrorMask = true;
            }
          }
        );
      } catch (e) {
        console.error(this.$t("videobox.index.v7jy74"), e);
      }
    },
    // 关闭播放器
    closeWebrtc() {
      try {
        if (this.webrtcPlayer) {
          this.webrtcPlayer.close();
          this.webrtcPlayer = null;
          var remote = this.$refs.video;
          if (remote) {
            remote.srcObject = null;
            remote.load();
          }
        }
      } catch (e) {
        console.error(this.$t("videobox.index.s86ek4"), e);
      }
    },
    // 播放hls
    playHls() {
      try {
        const video = document.getElementById("video");
        const videoSrc = this.hlsUrl;
        if (Hls.isSupported()) {
          this.hlsPlayer = new Hls();
          this.hlsPlayer.loadSource(videoSrc);
          this.hlsPlayer.attachMedia(video);
          this.hlsPlayer.on(Hls.Events.MANIFEST_PARSED, () => {
            //console.log("HLS stream loaded successfully");
            video.play();
          });
          this.hlsPlayer.on(Hls.Events.ERROR, (event, data) => {
            //console.error("HLS error:", data);
          });
          video.addEventListener("pause", () => {
            video.play();
          });
        } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
          // 如果浏览器原生支持 HLS（如 Safari）
          video.src = videoSrc;
          video.addEventListener("loadedmetadata", () => {
            video.play();
          });
        } else {
          this.errorState = this.$t("videobox.index.v95f7k");
          this.showErrorMask = true;
        }
      } catch (e) {
        console.error(this.$t("videobox.index.8046qf"), e);
      }
    },
    // 关闭播放器
    closeHls() {
      try {
        if (this.hlsPlayer) {
          this.hlsPlayer.stopLoad();
          this.hlsPlayer.detachMedia();
          this.hlsPlayer = null;
        }
      } catch (e) {
        console.error(this.$t("videobox.index.43m4p3"), e);
      }
    },
  },
};
</script>
<style scoped lang="scss">
.video-cont {
  position: relative;
  height: 100%;
  .dropdown {
    position: absolute;
    right: 10px;
    top: 10px;
    z-index: 10;
  }

  .action-mask {
    position: absolute;
    top: 50%;           /* Element top margin adjusted to parent50% */
    left: 50%;          /* Element left margin adjusted to parent50% */
    transform: translate(-50%, -50%); /* Pull back itself50%Width
    width: 100%;
    // height: 100%;
    display: flex; /* Enable Flexbox */
    justify-content: center; /* Center horizontally */
    align-items: center; /* Center vertically */
    width: 100%; /* or fixed degree */
    // height: 100%; /* 或固定高度 */
    z-index: 1;
    height: 30px;
    text-align: center;
  }

  // .play-next-camera {
  //   right: 0px;
  //   bottom: 0px;
  //   text-align: right;
  //   position: absolute;
  //   display: flex; /* 启用 Flexbox */
  //   justify-content: end; /* 横向居中 */
  //   align-items: center; /* 垂直居中 */
  //   width: 100%; /* 或固定宽度 */
  //   z-index: 10;
  //   padding: 15px;
  //   font-size: 13px;
  // }
}

video::-webkit-media-controls-play-button {
  display: none;
}

video::-webkit-media-controls-timeline {
  display: none;
}
/*全屏按钮*/
// video::-webkit-media-controls-fullscreen-button {
//     display: none;
// }

/*观看的当前时间*/
// video::-webkit-media-controls-current-time-display {
//     display: none;
// }
/*剩余观看时间*/
video::-webkit-media-controls-time-remaining-display {
  display: none;
}
/*音量按钮*/
video::-webkit-media-controls-mute-button {
  display: none;
}
/*隐藏切换隐藏字幕的按钮*/
video::-webkit-media-controls-toggle-closed-captions-button {
  display: none;
}
/*音量的控制条*/
video::-webkit-media-controls-volume-slider {
  display: none;
}
</style>
