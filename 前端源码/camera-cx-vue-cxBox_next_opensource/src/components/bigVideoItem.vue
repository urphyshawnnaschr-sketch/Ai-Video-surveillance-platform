<template>
  <div class="videoItem">
    <div class="action-mask" v-show="showErrorMask">
      <el-link type="danger">{{
        $t("components.bigvideoitem.5aljn0")
      }}</el-link>
    </div>
    <div class="action-mask" v-show="showLoadMask">
      <div><i class="el-icon-loading" />{{ $t("videobox.index.lqy3y4") }}</div>
    </div>
    <div class="play-next-camera" v-show="playReady && playNextSec > 0">
      <span>{{ playNextSec }}{{ $t("components.bigvideoitem.w81bc5") }}</span>
    </div>
    <video
      v-show="Id"
      id="video"
      autoplay
      controls
      muted
      width="100%"
      height="100%"
      ref="video"
    ></video>
    <div class="dropdown" v-if="Id">
      <el-select
        :placeholder="$t('applicationMonitoring.common.videoSource')"
        v-model="cameraId"
        class="head-container-input"
        @change="changeSelect()"
        style="width: 100px; zoom: 0.8"
      >
        <el-option
          v-for="(subItem, subIndex) in cameraOptions"
          :key="subIndex"
          :label="subItem.name"
          :value="subItem.id"
        ></el-option>
      </el-select>
    </div>
    <div v-if="!Id" style="width: 100%; text-align: center; margin-top: 25%">
      {{ $t("videobox.index.96pi56") }}
    </div>
  </div>
</template>

<script>
import request from "@/utils/request.js";
import Hls from "hls.js";
export default {
  name: "bigVideoItem",
  data() {
    return {
      recvOnly: true,
      resolution: {
        height: 360,
        label: "nHD",
        width: 640,
      },
      resolutionSel: "nHD",
      resolutionOpt: [],
      useCamera: false,
      cameraId: "",
      // 播放器
      webrtcPlayer: null,
      // 播放地址
      webrtcUrl: null,
      // hls播放器
      hlsPlayer: null,
      // hls播放地址
      hlsUrl: null,
      // 显示视频读取错误遮罩
      showErrorMask: false,
      // 显示视频正在加载遮罩
      showLoadMask: true,
      // 切换播放定时任务
      playNextTaskTimer: null,
      // 切换播放的时间戳, 每次切换播放，都需要在时间戳上增加3分钟毫秒数
      playNextTaskTimeMills: 0,
      // 切换播放读秒定时器
      playNextSecTimer: null,
      // 切换播放读秒
      playNextSec: 0,
      // 切换是否预备状态，至少有一个能播放的才设置为
      playReady: false,
      // 当前播放索引
      playIndex: 0,
    };
  },
  props: {
    index: {
      type: Number,
      default: null,
    },
    Id: {
      type: String,
      default: "",
    },
    cameraOptions: {
      type: Array,
      default: [],
    },
  },
  created() {
    this.cameraId = this.Id;
  },
  mounted() {
    if (!this.Id) {
      this.showLoadMask = false;
      // console.log("未接入摄像头，无法播放视频");
      return;
    }

    // console.log("初始化视频组件 BigVideo", this.cameraId);

    this.fetchPlayUrl();

    // 每隔3分钟切换下一个摄像头播放
    this.playNextTaskTimer = setInterval(this.playNextTask, 5000);

    // 下一次执行时间
    this.playNextTaskTimeMills = new Date().getTime() + 5 * 60 * 1000;
  },
  beforeDestroy() {
    // console.log("销毁视频组件 BigVideo", this.cameraId);
    // 关闭播放
    this.closeWebrtc();

    // 关闭播放
    this.closeHls();

    // 关闭切换播放定时器
    if (this.playNextTaskTimer) {
      // console.log("关闭切换播放定时器");
      clearInterval(this.playNextTaskTimer);
      this.playNextTaskTimer = null;
    }

    // 关闭播放读秒定时器
    if (this.playNextSecTimer) {
      // console.log("关闭播放读秒定时器");
      clearInterval(this.playNextSecTimer);
      this.playNextSecTimer = null;
    }
  },
  methods: {
    // 切换摄像头
    changeSelect() {
      this.fetchPlayUrl("changeSelect");
    },
    async fetchPlayUrl(id, index) {
      if (!this.cameraId) {
        return;
      }

      // 关闭播放器
      this.closeWebrtc();
      this.closeHls();

      // 关闭错误提示，显示加载提示
      this.showErrorMask = false;
      this.showLoadMask = true;

      try {
        const res = await request.post(
          "/stream/getBoxPlayUrl?cameraId=" + this.cameraId
        );
        const data = res.data;
        if (data.code == 200) {
          // 关闭加载
          this.showLoadMask = false;

          // 下次播放时间
          this.playNextTaskTimeMills = new Date().getTime() + 5 * 60 * 1000;

          // 如果是摄像头，使用播放
          if ("H265" == data.codec) {
            // 停止播放
            this.closeWebrtc();
            this.webrtcUrl = null;

            // 播放HLS
            this.hlsUrl = data.url;
            this.playHls();
          }

          // 如果是摄像头，使用播放
          if ("H264" == data.codec) {
            // 停止播放
            this.closeHls();
            this.hlsUrl = null;

            // 播放
            this.webrtcUrl = data.url;
            this.playWebrtc();
          }
        } else {
          this.showLoadMask = false;
          this.showErrorMask = true;

          // 下一次触发时间
          this.playNextTaskTimeMills =
            new Date().getTime() + 5 * 60 * 1000 + 3000;

          // 切换下一个播放
          setTimeout(this.playNextCamera, 3000);
        }
      } catch (err) {
        this.showErrorMask = true;
        this.showLoadMask = false;
      }
    },
    playWebrtc() {
      try {
        // 禁止视频画面点击
        const videos = document.querySelectorAll("video"); //获取所有标签
        videos.forEach((video) => {
          // 阻止单击暂停
          video.addEventListener("click", (event) => {
            event.preventDefault();
          });
        });

        const { width, height } = this.resolution;
        this.webrtcPlayer = new ZLMRTCClient.Endpoint({
          element: this.$refs.video,
          debug: false,
          zlmsdpUrl: this.webrtcUrl,
          simulcast: false,
          useCamera: false,
          audioEnable: false,
          videoEnable: true,
          recvOnly: true,
          resolution: { width, height },
          usedatachannel: false,
        });

        var that = this;

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_NOT_SUPPORT,
          function (e) {
            that.showLoadMask = false;
            that.showErrorMask = true;
          }
        );

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_ICE_CANDIDATE_ERROR,
          function (e) {
            // ICE 协商出错
            that.showLoadMask = false;
            that.showErrorMask = true;
          }
        );

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_ON_REMOTE_STREAMS,
          function (e) {
            //获取到了远端流，可以播放
            // console.log("播放成功", e.streams);
            that.playReady = true;
            that.showLoadMask = false;
            that.showErrorMask = false;
          }
        );

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_OFFER_ANWSER_EXCHANGE_FAILED,
          function (e) {
            // offer anwser 交换失败
            // console.log("offer answer");
            that.showLoadMask = false;
            that.showErrorMask = true;
          }
        );

        this.webrtcPlayer.on(
          ZLMRTCClient.Events.WEBRTC_ON_CONNECTION_STATE_CHANGE,
          function (state) {
            // console.log("state", state);
            // RTC 状态变化 ,详情参考 https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection/connectionState
            if (
              state == "disconnected" ||
              state == "failed" ||
              state == "closed"
            ) {
              that.showLoadMask = false;
              that.showErrorMask = true;

              // 下一次触发时间
              that.playNextTaskTimeMills =
                new Date().getTime() + 5 * 60 * 1000 + 3000;

              // 切换下一个播放
              setTimeout(that.playNextCamera, 3000);
            }

            if (state == "connected") {
              that.showErrorMask = false;
              that.showLoadMask = false;
            }
          }
        );
      } catch (err) {
        console.error("webrtc error", err);
      }
    },
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
      } catch (err) {
        console.error("close webrtc error", err);
      }
    },
    /** 播放hls */
    playHls() {
      try {
        const video = document.getElementById("video");

        // HLS 流地址
        // const videoSrc = 'https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8';//测试开源地址
        // const videoSrc = 'http://cmgw-vpc.lechange.com:8888/LCO/9C07128PAG4B9E2/0/1/20250324T021313/openhz0735a494162c4ba6bb287d7b6a5f4bb7.m3u8?source=open';//大华提供地址165

        const videoSrc = this.hlsUrl;
        if (Hls.isSupported()) {
          this.hlsPlayer = new Hls();
          this.hlsPlayer.loadSource(videoSrc);
          this.hlsPlayer.attachMedia(video);
          this.hlsPlayer.on(Hls.Events.MANIFEST_PARSED, () => {
            // console.log("HLS stream loaded successfully");
            this.playReady = true;
            this.showErrorMask = false;
            video.play();
          });
          this.hlsPlayer.on(Hls.Events.ERROR, (event, data) => {
            console.error("HLS error:", data);
          });

          video.addEventListener("pause", () => {
            // console.log("视频暂停");
            video.play();
          });
        } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
          // 如果浏览器原生支持 HLS（如 Safari）
          video.src = videoSrc;
          video.addEventListener("loadedmetadata", () => {
            console.log("Native HLS playback supported");
            video.play();
          });
        } else {
          console.error("Your browser does not support HLS playback.");
          this.showLoadMask = false;
          this.showErrorMask = true;
        }
      } catch (err) {
        console.error("hls error", err);
      }
    },
    // 关闭HLS
    closeHls() {
      try {
        if (this.hlsPlayer) {
          this.hlsPlayer.stopLoad();
          this.hlsPlayer.detachMedia();
          this.hlsPlayer = null;
        }
      } catch (err) {
        console.error("close hls error", err);
      }
    },
    // 播放下一个视频
    playNextTask() {
      // console.log("play next task", new Date());
      let that = this;
      const currentTimeMills = new Date().getTime();
      // console.log('定时切换任务时间差', currentTimeMills, this.playNextTaskTimeMills, (currentTimeMills - this.playNextTaskTimeMills));
      if (currentTimeMills < this.playNextTaskTimeMills) {
        return;
      }

      //
      if (this.playReady) {
        // console.log("执行切换任务", new Date());
        if (this.cameraOptions.length <= 1) {
          // console.log("仅有一个摄像头，不执行切换");
          return;
        }

        // 下一次触发时间
        this.playNextTaskTimeMills =
          new Date().getTime() + 5 * 60 * 1000 + 10000;

        //
        this.playNextSec = 10;
        this.playNextSecTimer = setInterval(() => {
          that.playNextSec = that.playNextSec - 1;
          if (that.playNextSec <= 0) {
            clearInterval(that.playNextSecTimer);
            that.playNextSecTimer = null;

            // 切换下一个播放
            that.playNextCamera();
          }
        }, 1000);
      }
    },
    // 播放下一个
    playNextCamera() {
      // 下一次触发时间
      this.playNextTaskTimeMills = new Date().getTime() + 5 * 60 * 1000 + 3000;
      this.playIndex = this.playIndex + 1;
      const cameraLength = this.cameraOptions.length;
      if (this.playIndex >= cameraLength) {
        this.playIndex = 0;
      }
      this.cameraId = this.cameraOptions[this.playIndex]["id"];
      // 播放新视频
      this.fetchPlayUrl();
    },
  },
};
</script>

<style lang="scss" scoped>
.videoItem {
  width: 100%;
  height: 100%;
  position: relative;
  .dropdown {
    position: absolute;
    right: 10px;
    top: 10px;
    z-index: 10;
  }
  //   .player {
  //     width: 100%;
  //     height: 100%;
  //   }

  .action-mask {
    top: 0px;
    bottom: 0px;
    width: 100%;
    // height: 100%;
    position: absolute;
    display: flex; /* Enable Flexbox */
    justify-content: center; /* Center horizontally */
    align-items: center; /* Center vertically */
    width: 100%; /* or fixed degree */
    // height: 100%; /* 或固定高度 */
    z-index: 1;
  }

  .play-next-camera {
    right: 0px;
    bottom: 0px;
    text-align: right;
    position: absolute;
    display: flex; /* Enable Flexbox */
    justify-content: end; /* Center horizontally */
    align-items: center; /* Center vertically */
    width: 100%; /* or fixed degree */
    z-index: 10;
    padding: 15px;
    font-size: 13px;
  }
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
