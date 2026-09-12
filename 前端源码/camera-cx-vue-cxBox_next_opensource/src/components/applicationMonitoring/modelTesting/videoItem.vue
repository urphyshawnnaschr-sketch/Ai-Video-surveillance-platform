<template>
    <div class="videoItem">
      <div :id="'video' + index" class="videobg">
      </div>
    </div>
  </template>
  
  <script>
  import request from "@/utils/request.js";
  import Cookies from "js-cookie";
  import Player from "xgplayer";
  import FlvPlugin from "xgplayer-flv";
  import "xgplayer/dist/index.min.css";
  export default {
    name: "videoItem",
    data() {
      return {
        flvPlayer: null,
        VUE_APP_API_BASE_URL
      };
    },
    props: ["cameraUrl", "index","videoWith",],
    created() {},
    mounted() {
      this.getPlayUrl();
    },
    beforeDestroy() {
      // this.player.pause();
      // this.player.unload();
      // this.player.detachMediaElement();
      // this.player.destroy();
      // this.player= null;
    },
    methods: {
      getPlayUrl() {
        this.player = new Player({
          id: "video" + this.index,
          // isLive: true,
          playsinline: true,
          url: VUE_APP_API_BASE_URL+"/model/test/steamMp4?file=" + this.cameraUrl,
          volume: 0,
          autoplay: true,
          fluid: true,
          // plugins: [FlvPlugin],
          width: this.videoWith,
          height:367
        });
      },
    },
    watch: {},
  };
  </script>
  
  <style lang='scss' scoped>
  .videoItem {
    width: 100%;
    height: 100%;
    .player {
      width: 100%;
      height: 100%;
    }
  }
  </style>