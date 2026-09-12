<template>
  <div class="new-popup" v-if="dialogVisible">
    <div class="top">
      <div style="font-weight: bold">{{ alarmData.algorithmName }}</div>
      <div class="el-icon-close" style="cursor: pointer" @click="closed"></div>
    </div>
    <div class="alarm-cont">
      <div>{{ alarmData.alarmTime }}</div>
      <div style="margin-left: 30px">{{ alarmData.cameraName }}</div>
    </div>
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
              {{ parseFloat(item.confidence).toFixed(2) }}
            </div>
          </div>
          <img id="img_alarm_popup" v-if="alarmData.id" :src="fileUrl" class="img" />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    props: {
      alarmData: {
        type: Object,
        default: () => {},
      },
      fileUrl: {
        type: String,
        default: '',
      },
    },
    data() {
      return {
        dialogVisible: true,
        imgUrl: '',
        pointList: [],
        imgHeight: '',
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
                  document.getElementById('img_alarm_popup').offsetHeight / this.imgHeight;
                arr.forEach((item) => {
                  if (item.position[3] * 0.5 > this.imgHeight) {
                    item.position[3] = this.imgHeight / 0.5;
                  }
                  let leftNum = item.position[0] * ratio * 0.5;
                  this.pointList.push({
                    ...item,
                    left: leftNum > 0 ? leftNum + 'px' : '0px',
                    top: item.position[1] * ratio * 0.5 - 5 + 'px',
                    width: (item.position[2] - item.position[0]) * ratio * 0.5 + 'px',
                    height: (item.position[3] - item.position[1]) * ratio * 0.5 + 'px',
                  });
                });
              }, 50);
            };
          });
        },
      },
    },
    methods: {
      // 关闭回调
      closed() {
        this.$emit('close');
      },
    },
  };
</script>
<style scoped lang="scss">
  .new-popup {
    width: 35%;
    background: #fff;
    box-shadow:
      0px 8px 20px 0px rgba(0, 0, 0, 0.08),
      0px 12px 32px 0px rgba(0, 0, 0, 0.04);
    position: fixed;
    right: 0;
    bottom: 20px;
    z-index: 9999;
    padding: 20px;
    .top {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 16px;
      margin-bottom: 10px;
    }
    .alarm-cont {
      font-size: 13px;
      color: #1A0808;
      margin-bottom: 10px;
      display: flex;
    }
    .result {
      width: 100%;
      .img-box {
        width: 100%;
        .img-content {
          position: relative;
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
      .img {
        width: 100%;
        object-fit: cover;
        display: block;
      }
    }
  }
</style>
