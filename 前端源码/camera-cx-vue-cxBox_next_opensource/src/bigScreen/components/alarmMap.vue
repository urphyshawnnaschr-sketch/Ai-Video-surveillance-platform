<template>
  <div class="block">
    <div class="block-title flex-item">
      <div>{{ $t("components.alarmmap.2fms18") }}</div>
      <div class="flex-item" style="cursor: pointer">
         <img
           src="@/assets//images//bigScreen/swtich.png"
          style="width: 16px; margin-right: 5px;"
        />
        <div style="color: #FFE0B2; font-size: 13px; margin-right: 20px" @click="changeMode()">
          {{ $t("components.alarmmap.modeSwitch") }}
        </div>
        <img
           src="@/assets//images//bigScreen/swtich.png"
          style="width: 16px; margin-right: 5px;"
        />
        <div style="color: #FFE0B2; font-size: 13px; margin-right: 20px" @click="changeIndex()">
          {{ $t("components.alarmmap.ot8kdl") }}
        </div>
      </div>
    </div>
    <div style="padding: 20px">
      <div
        style="height: 100%; width: 100%"
        v-for="(item, index) in mapList"
        :key="item.id + index"
        v-if="item.id == configId"
      >
        <images
          :image-url="getMapImageUrl(item.cover)"
          :marker-icon-url="getMapImageUrl(item.svgName)"
          :markersEd="markersEd"
        />
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import images from "@/components/bigScreen/components/images.vue";
import { configList, stat} from "@/api/annotationPlatform/mapImageManagent";
import { getMyDate } from "../../utils/common";
export default {
  components: {
    images,
  },
  data() {
    return {
      mapList: [],
      markersEd: [],
      configId: "",
      mapTimer: null,
      Index: 0,
      VUE_APP_API_BASE_URL,
      switchMode:1,
      imageUrl:''
    };
  },
  created() {
    this.getConfigList();
    // 开启地图定时器
    this.mapTimer = setInterval(this.stat, 15000); // 每10秒请求一次接口
  },
  beforeDestroy() {
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
        isShowStatus:this.switchMode
      }).then((res) => {
        if(res.data.length){
          this.markersEd = res.data.map((item) => {
          return {
            originX: item.position[0],
            originY: item.position[1],
            ...item,
          };
        });
        }else{
         this.markersEd = []
        }
       
      });
    },
    async getConfigList() {
      const res = await configList();
      this.mapList = res.data;
      if (this.mapList.length > 0) {
        this.configId = this.mapList[this.Index].id;
        this.changeMap(this.mapList[this.Index]);
      }
    },
    changeMode() {
     this.switchMode=== 0 ? this.switchMode = 1 : this.switchMode = 0;
     this.stat();
},
    changeIndex() {
      if (this.Index < this.mapList.length - 1) {
        this.Index = this.Index + 1;
      } else {
        this.Index = 0;
      }
      this.changeMap(this.mapList[this.Index]);
    },
    changeMap(item) {
      this.configId = item.id;
      this.stat();
    },
    getMapImageUrl(filename) {
      console.log(filename);
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/map/file/stream?filename=${filename}&X-Token=${token}`;
    },
  },
};
</script>

<style scoped lang="scss">
.flex-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
