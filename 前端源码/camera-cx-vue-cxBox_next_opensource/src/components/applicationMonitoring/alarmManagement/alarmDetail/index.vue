<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('alarmdetail.index.3r91n2')"
      :visible.sync="dialogVisible"
      width="1200px"
      :append-to-body="true"
      :key="currentId"
      @closed="closed"
    >
      <el-row :gutter="20" v-if="detail">
        <el-col :span="10">
          <el-form label-width="120px">
            <el-form-item :label="$t('common.cameraName')">
              {{ detail.camera.name }}
            </el-form-item>
            <el-form-item :label="$t('alarmdetail.index.880cd2')">
              {{ detail.camera.rtspUrl }}
            </el-form-item>
            <el-form-item :label="$t('common.alarmInterval')">
              {{ detail.camera.alarmInterval }}
            </el-form-item>
            <el-form-item :label="$t('common.algorithmName')">
              {{ detail.algorithm.name }}
            </el-form-item>
            <el-form-item :label="$t('common.alarmTime')">
              {{ detail.report.createdStr }}
            </el-form-item>
            <el-form-item :label="$t('alarmdetail.index.1j63ns')">
              {{ detail.report.fileName }}
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="14">
          <MarkResult
            v-if="detail.report?.id"
            :fileUrl="$common.handleStream(detail.report.id)"
            :dataList="boxList"
          />
          <div v-if="radioList.length > 1" class="radio-btn">
            <div class="title">{{ $t("alarmdetail.index.yg6f49") }}</div>
            <div style="flex: 1">
              <el-radio-group
                v-model="radioItem"
                size="mini"
                @input="radioChange"
              >
                <el-radio-button
                  v-for="(item, ind) in radioList"
                  :key="ind"
                  :label="item.index"
                  >{{
                    item.type + ` ` + formatConfidence(item.confidence)
                  }}</el-radio-button
                >
              </el-radio-group>
            </div>
          </div>
        </el-col>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import MarkResult from "@/components/markResult";
import { getListDataDetail } from "@/api/applicationMonitoring/alarmManagement";
export default {
  components: {
    MarkResult,
  },
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      detail: null,
      boxList: [],
      radioList: [],
      radioItem: null,
      index: 0,
      timeObj: {},
    };
  },
  created() {
    this.getListDataDetail();
    // let that = this;
    // this.timeObj = setInterval(function () {
    //   let len = that.radioList.length
    //   if(len>1){
    //     let num = len-1;
    //     if(that.index==num){
    //       that.index = 0;
    //     }else{
    //       that.index = that.index?(Number(that.index)+1):1;
    //     }
    //   }
    //   that.radioChange(that.index);
    // }, 1500);
  },
  destroyed() {
    // clearInterval(this.timeObj);
  },
  methods: {
    // 获取摄像头详情
    async getListDataDetail() {
      this.boxList = [];
      const data = await getListDataDetail({ id: this.currentId });
      this.detail = data.data;
      let arr = JSON.parse(data.data.report.params);
      arr.map((item, i) => {
        item.index = i;
      });
      this.radioList = arr;
      // if(arr.length>1){
      //   this.boxList.push(arr[0])
      //   this.radioItem = arr[0].index
      // }else{
      //   this.boxList = arr
      // }
      this.boxList = arr;
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
    // 切换数据
    radioChange(e) {
      if (this.radioList.length > 1) {
        this.boxList = [];
        this.radioItem = e;
        this.boxList.push(this.radioList[e]);
      }
    },
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
.image {
  width: 100%;
  height: auto;
  display: block;
}
</style>
<style lang="scss">
.radio-btn {
  display: flex;
  margin-top: 10px;
  .el-radio-button {
    margin-bottom: 10px;
    margin-right: 10px;
  }
  .el-radio-button:first-child .el-radio-button__inner {
    border: 1px solid #dcdfe6;
  }
  .el-radio-button__inner {
    border-left: 1px solid #dcdfe6;
  }
  .el-radio-button--mini .el-radio-button__inner {
  }
  .el-radio-button:last-child .el-radio-button__inner {
  }
  .title {
    font-weight: bold;
    margin-right: 20px;
  }
}
</style>
