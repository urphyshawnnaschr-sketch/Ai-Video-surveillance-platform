<template>
  <div class="videoItem">
      <video muted="muted" :id="`player${index}`" class="player" :ref="`videoElement${index}`"></video>
  </div>
</template>

<script>
  import flv from 'flv.js'

  export default {
    name: 'videoItem',
    data() {
      return {
        wsUrl: VUE_APP_WS_BASE_URL,//'ws://e4ab935113169c37.natapp.cc:18025',
        flvPlayer: null,
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
        let source = this.wsUrl  + "/live" + "?url=" + this.dataObj.rtsp_url;
        console.log(source)
        if (flv.isSupported()) {
          this.videoElement = document.getElementById(`player${this.index}`);
          this.flvPlayer = flv.createPlayer({
            type: 'flv',
            url: source, //你的地址
            isLive: false,
            hasAudio: false,
          });
          this.flvPlayer.attachMediaElement(this.videoElement);
          this.flvPlayer.load();
          this.flvPlayer.play();
        }
      },
    },
    watch: {
      dataObj(val, oVal) {
        console.log(val, oVal)
        if (JSON.stringify(val) != JSON.stringify(oVal)) {
          this.dataObj = val;
          if (this.flvPlayer) {
            this.flvPlayer.pause();
            this.flvPlayer.destroy();
            this.flvPlayer = null;
          }
          this.createVideo();
        }
      }
    }
  }
</script>

<style lang='scss' scoped>
  .videoItem{
    width: 100%;
    height: 100%;
    .player {
      width: 100%;
      height: 100%;
    }
  }
</style>