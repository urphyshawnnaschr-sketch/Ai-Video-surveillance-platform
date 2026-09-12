<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      width="600px"
      top="10vh"
      :title="$t('addcamera.newadd.555rc5')"
      :visible.sync="drawDisabled"
      @closed="closed"
      append-to-body
    >
      <div class="time-cont">
        <div class="tip">{{ $t("addcamera.timeinfo.l218lk") }}</div>
        <div class="list">
          <div class="list-item" v-for="(item, index) in timeList" :key="index">
            <el-time-picker
              is-range
              v-model="item.time"
              format="HH:mm"
              value-format="HH:mm"
              :range-separator="$t('common.pickerDate.to')"
              :start-placeholder="$t('addcamera.timeinfo.5r5j4x')"
              :end-placeholder="$t('addcamera.timeinfo.dc2916')"
              :placeholder="$t('addcamera.timeinfo.yeo0o0')"
            >
            </el-time-picker>
            <span
              class="el-icon-delete del-sty"
              v-if="timeList.length > 1"
              @click="delFun(index)"
            ></span>
          </div>
        </div>
        <div class="add-btn" @click="addFun()">
          <span class="el-icon-circle-plus-outline"></span>
          <span style="margin-left: 6px">{{
            $t("common.add", { text: $t("common.duration") })
          }}</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closed">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveFun">{{
          $t("button.sureText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: {
    alarmTimeList: {
      type: Array,
      default: [],
    },
    algorithmId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      drawDisabled: true,
      timeList: [{ time: null }],
    };
  },
  watch: {
    alarmTimeList: {
      immediate: true,
      handler(val) {
        this.timeList = [];
        if (val.length > 0) {
          val.forEach((item) => {
            let obj = {
              time: [item.startTime, item.endTime],
            };
            this.timeList.push(obj);
          });
        } else {
          this.timeList = [{ time: null }];
        }
      },
    },
  },
  methods: {
    //取消
    closed() {
      this.$emit("close");
    },
    //保存绘制
    async saveFun() {
      let newArr = [];
      this.timeList.forEach((item) => {
        if (item.time && item.time.length > 0) {
          let params = {
            startTime: item.time[0],
            endTime: item.time[1],
          };
          newArr.push({
            startTime: item.time[0],
            endTime: item.time[1],
          });
        }
      });
      let obj = {
        algorithmId: this.algorithmId,
        alarmTimeArr: newArr,
      };
      this.$emit("close", obj);
    },
    // 新增时间段
    addFun() {
      this.timeList.push({
        time: null,
      });
    },
    // 删除
    delFun(index) {
      this.timeList.splice(index, 1);
    },
  },
};
</script>
<style scoped lang="scss">
.time-cont {
  .tip {
    color: #1A0808;
    font-size: 13px;
    line-height: 22px;
  }
  .list {
    padding-top: 20px;
  }
  .list-item {
    padding-bottom: 20px;
  }
  .del-sty {
    margin-left: 24px;
    cursor: pointer;
    padding: 8px;
  }
  .add-btn {
    border: 1px solid #dcdfe6;
    padding: 6px 16px;
    width: 130px;
    text-align: center;
    cursor: pointer;
  }
}
</style>
