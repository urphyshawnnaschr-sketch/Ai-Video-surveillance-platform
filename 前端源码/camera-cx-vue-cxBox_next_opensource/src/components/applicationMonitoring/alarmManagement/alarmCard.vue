<template>
  <div>
    <div class="result">
      <div class="img-box">
        <div
          class="img-content"
          :style="{
            overflow: 'hidden',
            width: ratio == 1 ? '235px !important' : '',
            height: ratio == 1 ? '132px !important' : '',
          }"
        >
          <div
            v-for="(item, index) in pointList"
            :key="index"
            :class="item.type == 'hook' ? 'xboxA' : 'xbox'"
            :style="{
              width: item.width,
              height: item.height,
              left: item.left,
              top: item.top,
            }"
          >
            <div
              class="text"
              v-if="ratio == 0.5"
              :style="{
                color: '#fff',
                textAlign: 'center',
                backgroundColor: item.type == 'hook' ? 'green' : 'red',
                position: 'absolute',
                minWidth: '100%',
              }"
            >
              {{ item.confidence }}
            </div>
          </div>
          <!-- <el-image id="img_alarm_card" style="max-width: 100%; max-height: 200px;" :src="this.fileUrl"
            :preview-src-list="[this.fileUrl]" fit="contain">
            <div slot="placeholder" class="image-slot">
              Loading image<span class="dot">...</span>
            </div>
          </el-image> -->
          <img
            id="img_alarm_card"
            ref="page_image_url"
            :src="this.fileUrl"
            style="width: 100%"
          />
        </div>
      </div>
      <div style="padding: 5px 10px">
        <div class="names-text">
          <el-tooltip
            effect="dark"
            v-if="alarmData.cameraName"
            :content="alarmData.cameraName"
            placement="top-start"
            :disabled="isShowTooltip"
          >
            <div
              class="cameraName"
              ref="cameraName"
              @mouseover="onMouseOver(1, alarmData.cameraName)"
            >
              {{ alarmData.cameraName }}
            </div>
          </el-tooltip>

          <div class="al-info">
            <el-tooltip
              effect="dark"
              v-if="alarmData.algorithmName"
              :disabled="isShowTooltip"
              :content="alarmData.algorithmName"
            >
              <div
                class="al-name"
                ref="alName"
                @mouseover="onMouseOver(2, alarmData.algorithmName)"
              >
                {{ alarmData.algorithmName }}
              </div>
            </el-tooltip>
            <div v-if="alarmData.alarmLevel.name">
              <el-tag
                size="mini"
                style="font-size: 12px"
                :style="{
                  borderColor: alarmData.alarmLevel.showColor,
                  color: alarmData.alarmLevel.showColor,
                  backgroundColor: alarmData.alarmLevel.showColorAlpha,
                }"
                >{{ alarmData.alarmLevel.name }}</el-tag
              >
            </div>
          </div>
        </div>
        <div class="times-text">
          <time class="time">{{ alarmData.createdStr }}</time>
          <el-button type="text" class="button" @click="clickDetail()">{{
            $t("alarmmanagement.alarmcard.376044")
          }}</el-button>
        </div>
      </div>
    </div>

    <AlarmDetail
      :currentId="currentId"
      v-if="alarmDetailVisible"
      @close="alarmDetailVisible = false"
    />
  </div>
</template>
<script>
import AlarmDetail from "@/components/applicationMonitoring/alarmManagement/alarmDetail";
export default {
  components: {
    AlarmDetail,
  },
  props: {
    fileUrl: {
      type: String,
      default: "",
    },
    dataList: {
      type: Array,
      default: () => [],
    },
    alarmData: {
      type: Object,
      default: () => {},
    },
    ratio: {
      type: Number,
      default: 0.5,
    },
    isAlarm: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      imgHeight: "",
      pointList: [],
      alarmDetailVisible: false,
      currentId: "",
      isShowTooltip: false,
    };
  },
  methods: {
    // 告警详情
    clickDetail() {
      if (this.isAlarm) {
        this.$emit("clickDetail", this.alarmData.id);
        return;
      }
      this.currentId = this.alarmData.id;
      this.alarmDetailVisible = true;
    },
    onMouseOver(index, str) {
      const tag = index == 1 ? this.$refs.cameraName : this.$refs.alName;
      const parentWidth = tag.offsetWidth; // 获取元素父级可视宽度
      // 创建临时元素
      const _span = document.createElement("span");
      // 放入文本
      _span.innerText = str;
      // 设置文字大小
      _span.style.fontSize = "14px";
      // 放入中
      document.body.appendChild(_span);
      // 获取的宽度
      const contentWidth = _span.offsetWidth;
      this.isShowTooltip = contentWidth <= parentWidth;
      document.body.removeChild(_span);
    },
  },
  watch: {
    dataList: {
      immediate: true,
      deep: true,
      handler(val) {
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
                this.$refs.page_image_url.offsetHeight / this.imgHeight;
              val.forEach((item) => {
                if (item.position[3] * this.ratio > this.imgHeight) {
                  item.position[3] = this.imgHeight / this.ratio;
                }
                this.pointList.push({
                  ...item,
                  left: item.position[0] * ratio * this.ratio + "px",
                  top: item.position[1] * ratio * this.ratio + "px",
                  width:
                    (item.position[2] - item.position[0]) * ratio * this.ratio +
                    "px",
                  height:
                    (item.position[3] - item.position[1]) * ratio * this.ratio +
                    "px",
                });
              });
            }, 100);
          };
        });
      },
    },
  },
  mounted() {},
};
</script>
<style scoped lang="scss">
.result {
  width: 100%;

  .img-box {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    .img-content {
      position: relative;
      width: 396px;
      height: 223px;
    }
  }

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

// .names-text {
//   display: flex;
//   flex-direction: row;
//   justify-content: space-between;
//   min-height: 42px;

//   span {
//     font-size: 16px;
//   }
// }
.cameraName {
  font-size: 16px;
  margin-bottom: 10px;
  height: 25px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.times-text {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;

  .time {
    font-size: 13px;
    color: #1A0808;
  }
}

.image-slot {
  min-height: 120px;
}

.al-info {
  font-size: 12px;
  display: flex;
  justify-content: space-between;
}

.al-name {
  font-size: 13px;
  margin-right: 5px;
  height: 25px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

::v-deep #img_alarm_card {
  display: block;
}

@media screen and (max-width: 3072px) {
  .img-content {
    position: relative !important;
    width: 684px !important;
    height: 385px !important;
  }
}
@media screen and (max-width: 2560px) {
  .img-content {
    position: relative !important;
    width: 554px !important;
    height: 312px !important;
  }
}
@media screen and (max-width: 2048px) {
  .img-content {
    position: relative !important;
    width: 427px !important;
    height: 241px !important;
  }
}
@media screen and (max-width: 1920px) {
  .img-content {
    position: relative !important;
    width: 396px !important;
    height: 223px !important;
  }
}
@media screen and (max-width: 1856px) {
  .img-content {
    position: relative !important;
    width: 380px !important;
    height: 214px !important;
  }
}
@media screen and (max-width: 1794px) {
  .img-content {
    position: relative !important;
    width: 364px !important;
    height: 205px !important;
  }
}
@media screen and (max-width: 1680px) {
  .img-content {
    position: relative !important;
    width: 335px !important;
    height: 189px !important;
  }
}
@media screen and (max-width: 1600px) {
  .img-content {
    position: relative !important;
    width: 315px !important;
    height: 178px !important;
  }
}
@media screen and (max-width: 1440px) {
  .img-content {
    position: relative !important;
    width: 275px !important;
    height: 155px !important;
  }
}
@media screen and (max-width: 1400px) {
  .img-content {
    position: relative !important;
    width: 265px !important;
    height: 149px !important;
  }
}
@media screen and (max-width: 1366px) {
  .img-content {
    position: relative !important;
    width: 256px !important;
    height: 144px !important;
  }
}
@media screen and (max-width: 1280px) {
  .img-content {
    position: relative !important;
    width: 235px !important;
    height: 132px !important;
  }
}
@media screen and (max-width: 1024px) {
  .img-content {
    position: relative !important;
    width: 171px !important;
    height: 96px !important;
  }
}
@media screen and (max-width: 800px) {
  .img-content {
    position: relative !important;
    width: 115px !important;
    height: 65px !important;
  }
}
</style>
