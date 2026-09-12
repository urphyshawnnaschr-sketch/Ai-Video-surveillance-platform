<template>
  <div>
    <div class="result">
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
        <div class="text" v-if="!item.isShow">
          <span style="margin-right: 5px">{{ item.type }}</span>
          <span>{{ formatConfidence(item.confidence) }}</span>
        </div>
        <div v-else class="text_P" :style="{ width: item.txt_width }">
          <div class="textA">
            <span style="margin-right: 5px">{{ item.type }}</span>
            <span>{{ formatConfidence(item.confidence) }}</span>
          </div>
        </div>
      </div>
      <el-image
        id="img"
        style="width: 100%"
        :src="this.fileUrl"
        :preview-src-list="[this.fileUrl]"
      >
        <div slot="error" class="image-slot">
          <i class="el-icon-picture-outline"></i>
        </div>
        <div slot="placeholder" class="image-slot">
          {{ $t("components.markresult.6w9m2k") }}<span class="dot">...</span>
        </div>
      </el-image>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    fileUrl: {
      type: String,
      default: "",
    },
    dataList: {
      type: Array,
      default: () => [],
    },
    imgRatio: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      imgHeight: "",
      pointList: [],
      isShow: false,
    };
  },
  watch: {
    dataList: {
      immediate: true,
      handler(val) {
        this.$nextTick(() => {
          this.pointList = [];
          let img = new Image();
          img.src = this.fileUrl;
          img.onload = () => {
            this.imgHeight = img.height;
            setTimeout(() => {
              const ratio =
                document.getElementById("img").offsetHeight / this.imgHeight;
              val.forEach((item) => {
                let width =
                  (item.position[2] - item.position[0]) * ratio * this.imgRatio;
                // 创建临时元素
                const _span = document.createElement("span");
                // 放入文本
                _span.innerText =
                  item.type + this.formatConfidence(item.confidence);
                // 设置文字大小
                _span.style.fontSize = "12px";
                // 放入中
                document.body.appendChild(_span);
                // 获取的宽度
                let txt_width = _span.offsetWidth + 14;
                // 从中删除该
                document.body.removeChild(_span);
                if (item.position[3] * this.imgRatio > this.imgHeight) {
                  item.position[3] = this.imgHeight / this.imgRatio;
                }
                this.pointList.push({
                  left: item.position[0] * ratio * this.imgRatio + "px",
                  top: item.position[1] * ratio * this.imgRatio + "px",
                  width:
                    (item.position[2] - item.position[0]) *
                      ratio *
                      this.imgRatio +
                    "px",
                  height:
                    (item.position[3] - item.position[1]) *
                      ratio *
                      this.imgRatio +
                    "px",
                  txt_width: txt_width + 3 + "px",
                  confidence: item.confidence,
                  type: item.type,
                  isShow: txt_width > width ? true : false,
                });
              });
            }, 50);
          };
        });
      },
    },
  },
  mounted() {},
  methods: {
    // 置信度保留2位小数
    formatConfidence(num) {
      try {
        if (num) {
          let str = num.toString();
          let index = str.indexOf(".");
          if (index > -1) {
            return str.substring(0, index + 3);
          } else {
            return str;
          }
        } else {
          return 0;
        }
      } catch (e) {
        return num;
      }
    },
  },
};
</script>
<style scoped lang="scss">
.result {
  width: 100%;
  position: relative;
  .xbox {
    position: absolute;
    border: 2px solid #f43838;
    background-color: rgba(255, 0, 0, 0.2);

    z-index: 2;
    .text {
      width: 100%;
      height: 20px;
      line-height: 20px;
      font-size: 12px;
      color: #fff;
      background-color: rgba(255, 0, 0, 0.4);
      padding: 0 5px;
    }
    .textA {
      line-height: 20px;
      font-size: 12px;
      color: #fff;
      background-color: rgba(255, 0, 0, 0.4);
      padding: 0 5px;
    }
    .text_P {
      position: absolute;
      top: -20px;
      // left: -2px;
      right: -2px;
    }
  }
  .xboxA {
    position: absolute;
    border: 2px solid #2b8904;
    background-color: rgba(102, 142, 6, 0.2);
    z-index: 2;
    .text {
      width: 100%;
      height: 20px;
      line-height: 20px;
      font-size: 13px;
      color: #fff;
      background-color: rgba(102, 142, 6, 0.4);
      padding: 0 5px;
    }
    .textA {
      line-height: 20px;
      font-size: 13px;
      color: #fff;
      background-color: rgba(102, 142, 6, 0.4);
      padding: 0 5px;
    }
    .text_P {
      position: absolute;
      top: -20px;
      // left: -2px;
      right: -2px;
    }
  }
}
</style>
