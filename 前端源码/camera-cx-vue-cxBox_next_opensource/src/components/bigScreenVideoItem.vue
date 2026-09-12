<template>
  <div class="videoItem">
    <div class="itemTop">
      {{dataObj.displayName ? dataObj.displayName : dataObj.name}}
      <div style="float: right;font-size: 22px">
        <!-- <span style="font-size: 18px;cursor: pointer;" @click="control">云台</span> -->
        <a-icon type="fullscreen" style="margin:0 10px" @click="fullScreen(index)"/>
        <!--<a-icon type="close" style="margin-right: 10px" @click="close"/>-->
      </div>
    </div>
    <div class="itemMain">
      <div class="loding">
        <a-spin tip="Loading..." :spinning="spinning"/>
      </div>
      <video muted="muted" :id="`player${index}`" class="player" :ref="`videoElement${index}`"></video>
    </div>
  </div>
</template>

<script>
  import flv from 'flv.js'

  export default {
    name: 'BigScreenVideoItem',
    data() {
      return {
        wsUrl: window._CONFIG['wsDomainURL'],
        // wsUrl: window.g.ws_video,
        url: '/viap/viaCameraModel/ViaCameraId',
        socketObj: null,
        clickIndex: 0,
        videoElement: null,
        flvPlayer: null,
        spinning: true,
        hackReset: true, // 强制刷新子组件
        speed: 0.5,
        time: 1,
        showControl: false,
        pos: {x: 0, y: 0, z: 0}
      };
    },
    props: {
      dataObj: {
        type: Object,
        default: () => {
        }
      },
      index: {
        type: Number,
        default: 0
      },
      name: {
        type: String,
        default: ''
      },
    },
    created() {

    },
    mounted() {
      this.createVideo();
      let self = this;
      window.addEventListener('resize', function () {
        if (!document.fullscreenElement && !document.webkitIsFullScreen && !document.mozFullScreen && !document.msFullscreenElement) {
          self.exitFullScreen()
        }
      })
    },
    beforeDestroy() {
      if (this.flvPlayer) {
        this.flvPlayer.pause();
        this.flvPlayer.destroy();
        this.flvPlayer = null;
      }
    },
    methods: {
      createVideo() {
        let selt = this;
//        console.log(this.dataObj)
        let protocol = this.dataObj.rtsp.split(':')[0].toUpperCase();//data.protocolTxt;
        let source = this.wsUrl + protocol + "/" + "?url=" + this.dataObj.rtsp;
        if (flv.isSupported()) {
          this.videoElement = document.getElementById(`player${this.index}`);
          this.flvPlayer = flv.createPlayer({
            type: 'flv',
            url: source, //你的地址
            // url:'http://10.128.117.119/alarm-video/flv1.flv',
            isLive: false,
            hasAudio: false,
          });
          this.flvPlayer.attachMediaElement(this.videoElement);
          this.flvPlayer.load();
          this.flvPlayer.play();
          this.videoElement.ontimeupdate = function () {
            if(selt.videoElement.paused){
              selt.spinning = false;
            }else {
              selt.spinning = true;
            }
//            selt.spinning = false;
          }
        }
      },
      refreshChild() {
        this.hackReset = false
        this.$nextTick(() => {
          this.hackReset = true
        })
      },
      refreshVideo() {
        if (this.flvPlayer) {
          this.flvPlayer.pause();
          this.flvPlayer.destroy();
          this.flvPlayer = null;
        }
        this.createVideo();
      },
      full() {
        this.clickIndex++
        if (this.clickIndex % 2 == 1) {
          this.$emit('selectFull', this.index + 1)
          console.log(this.index + 1)
        } else {
          this.$emit('selectFull', null)
          console.log('2')
        }
      },
      close() {
        this.flvPlayer.pause();
        this.flvPlayer.destroy();
        this.flvPlayer = null;
        this.$emit('closeVideo', this.index);
      },
      // 退出全屏方法
      exitFullScreen() {
        // this.refreshChild()
        if (document.exitFullscreen) {
          document.exitFullscreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.msExitFullscreen) {
          document.msExiFullscreen();
        } else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen();
        } else if (document.webkitExitFullscreen) {
          document.webkitExitFullscreen();
        }
      },
      //打开全屏方法
      openFullscreen(element) {
        // this.refreshChild()
        if (element.requestFullscreen) {
          element.requestFullscreen();
        } else if (element.mozRequestFullScreen) {
          element.mozRequestFullScreen();
        } else if (element.msRequestFullscreen) {
          element.msRequestFullscreen();
        } else if (element.webkitRequestFullscreen) {
          element.webkitRequestFullScreen();
        }
      },
      // 全屏点击
      fullScreen(index) {
        // let fullDom = this.$refs.videoMain;
        let fullDom = document.getElementById(`player${index}`);
        this.openFullscreen(fullDom);
      },
    },
    watch: {
      dataObj(val, oVal) {
        if (JSON.stringify(val) != JSON.stringify(oVal)) {
          this.dataObj = val;
          this.spinning = true;
          if (this.flvPlayer) {
            this.flvPlayer.pause();
            this.flvPlayer.destroy();
            this.flvPlayer = null;
            this.spinning = false;
          }
          this.createVideo();
        }
      }
    }
  }
</script>

<style lang='scss' scoped>
  .videoItem {
    width: 100%;
    height: 100%;
    background: #000;
    border-radius: 12px 12px
    /*margin: 4px;*/
  }

  .itemTop {
    height: 30px;
    color: #fff;
    line-height: 30px;
    text-indent: 10px;
    font-size: 12px;
  }

  .itemMain {
    height: calc(100% - 30px);
    position: relative;
  }

  .player {
    width: 100%;
    height: 100%;
    object-fit: fill;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .canvas {
    position: absolute;
    top: 0;
    left: 0;
  }

  .loding {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%)
  }

  .remote {
    width: 150px;
    height: 110px;
    background: rgba(189, 182, 182, 0.5);
    position: absolute;
    bottom: 10px;
    left: 40px;
    font-size: 30px;
    text-align: center;
  }
</style>