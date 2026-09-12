<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('versioninfo.historicalversion.3ll166')"
    :visible.sync="dialogVisible"
    width="800px"
    append-to-body
    @close="closed"
  >
    <div class="history-cont">
      <div class="top-cont">
        <el-button type="info" icon="el-icon-arrow-left" @click="closed">{{
          $t("marktool.annotate.13o4yx")
        }}</el-button>
        <div class="tip">
          {{ $t("versioninfo.historicalversion.it14m2") }}{{ newDate }}
        </div>
      </div>
      <div class="anchor-point">
        <!-- 滚动区域 -->
        <div class="scroll-content" @scroll="onScroll">
          <div class="scroll-item" v-for="(item, index) in keyList">
            <div v-for="(items, ind) in params[item]" class="item-sty">
              <div class="title">{{ items.date }}</div>
              <div>
                <el-table :data="items.tableData" border style="width: 100%">
                  <el-table-column
                    v-for="(item, index) in items.titleList"
                    :label="item"
                    :prop="'temp' + index"
                    :width="index == 0 ? '60' : index == 1 ? '150' : ''"
                  ></el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </div>
        <!-- 按钮 -->
        <div class="operation-btn">
          <div
            v-for="(item, index) in keyList"
            :key="index"
            @click="jump(index)"
            :class="[activeStep === index ? 'key-item check' : 'key-item']"
          >
            {{ item }}
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { getFileOrigin } from "@/api/common";
export default {
  data() {
    return {
      dialogVisible: true,
      // titleList:[],
      // tableData:[],
      keyList: [],
      loading: false,
      activeStep: 0,
      params: {},
      newDate: "",
    };
  },
  created() {
    this.getData();
  },
  methods: {
    // 获取数据
    async getData() {
      const res = await getFileOrigin();
      // console.log(res)
      let dataObj = res.data;
      if (JSON.stringify(dataObj) != "{}") {
        let keys = Object.keys(dataObj);
        this.keyList = keys.reverse();
        keys.forEach((item, index) => {
          dataObj[item].forEach((items) => {
            if (index == 0) {
              this.newDate = items.date;
            }
            let tableData = [];
            let notesArr = JSON.parse(JSON.stringify(items.notes));
            items.titleList = notesArr[0];
            let Arr = notesArr.splice(1, notesArr.length);
            if (Arr.length > 0) {
              Arr.forEach((i) => {
                let obj = { ...i };
                let newData = {};
                for (let key in obj) {
                  newData["temp" + key] = obj[key];
                }
                tableData.push(newData);
              });
              items.tableData = tableData;
            }
          });
        });
        this.params = dataObj;
      }
    },
    closed() {
      this.$emit("closeHistory");
    },
    // 滚动触发按钮高亮
    onScroll(e) {
      let scrollItems = document.querySelectorAll(".scroll-item");
      let target = document.querySelector(".scroll-content");
      // 判断滚动条是否滚动到底部
      if (target.scrollHeight <= target.scrollTop + target.clientHeight) {
        this.activeStep = scrollItems.length - 1;
        return;
      }
      for (let i = scrollItems.length - 1; i >= 0; i--) {
        // 判断滚动条滚动距离是否大于当前滚动项可滚动距离
        let judge =
          e.target.scrollTop >=
          scrollItems[i].offsetTop - scrollItems[0].offsetTop;
        // console.log(e.target.scrollTop,"++++++++",scrollItems[i].offsetTop,"------",scrollItems[0].offsetTop)
        if (judge) {
          this.activeStep = i;
          break;
        }
      }
    },
    // 点击切换锚点
    jump(index) {
      let target = document.querySelector(".scroll-content");
      let scrollItems = document.querySelectorAll(".scroll-item");
      // 判断滚动条是否滚动到底部
      if (target.scrollHeight <= target.scrollTop + target.clientHeight) {
        this.activeStep = index;
      }
      let total = scrollItems[index].offsetTop - scrollItems[0].offsetTop; // 锚点元素距离其(这里是)顶部的距离(待滚动的距离)
      let distance = document.querySelector(".scroll-content").scrollTop; // 滚动条距离滚动区域顶部的距离
      // let distance = document.body.scrollTop || document.documentElement.scrollTop || window.pageYOffset // 滚动条距离滚动区域顶部的距离(滚动区域为窗口)
      // 滚动动画实现, 使用的递归实现平滑滚动，将距离细分为50小段，10ms滚动一次
      // 计算每一小段的距离
      let step = total / 50;
      if (total > distance) {
        smoothDown(document.querySelector(".scroll-content"));
      } else {
        let newTotal = distance - total;
        step = newTotal / 50;
        smoothUp(document.querySelector(".scroll-content"));
      }

      // 参数为滚动区域
      function smoothDown(element) {
        if (distance < total) {
          distance += step;
          element.scrollTop = distance;
          setTimeout(smoothDown.bind(this, element), 10);
        } else {
          element.scrollTop = total;
        }
      }

      // 参数为滚动区域
      function smoothUp(element) {
        if (distance > total) {
          distance -= step;
          element.scrollTop = distance;
          setTimeout(smoothUp.bind(this, element), 10);
        } else {
          element.scrollTop = total;
        }
      }
    },
  },
};
</script>
<style scoped lang="scss">
:deep(.el-dialog__body) {
  padding: 6px 20px 30px 20px;
}
.top-cont {
  .tip {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.4);
    line-height: 20px;
    padding: 5px 0px;
  }
}
.anchor-point {
  color: #000;
  height: calc(100vh - 300px);
  flex-basis: 100%;
  display: flex;
  overflow: hidden;
  .scroll-content {
    flex: 1;
    height: 100%;
    overflow: scroll;
  }
  .operation-btn {
    margin-left: 40px;
    height: 100%;
    .key-item {
      color: #000;
      font-size: 13px;
      line-height: 22px;
      padding: 5px 10px;
    }
    .check {
      border-left: 2px solid #E53935 !important;
      color: #E53935 !important;
      font-weight: bold;
    }
  }
  .item-sty {
    padding-top: 24px;
    .title {
      font-size: 20px;
      font-weight: bold;
      line-height: 20px;
      margin-bottom: 10px;
    }
  }
}
</style>
