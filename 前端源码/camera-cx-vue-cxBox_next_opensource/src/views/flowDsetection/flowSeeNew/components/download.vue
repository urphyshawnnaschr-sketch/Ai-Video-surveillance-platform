<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('flowDsetection.flowSee.exportAlarmData')"
    :visible.sync="dialogVisible"
    width="600px"
    :append-to-body="true"
    @closed="closed"
  >
    <div class="tip">
      {{ $t("flowDsetection.flowSee.friendlyTips") }}
      <br />
      {{ $t("flowDsetection.flowSee.onDayTips") }}
      <br />
      {{ $t("flowDsetection.flowSee.onWeeklyTips") }}
      <br />
      {{ $t("flowDsetection.flowSee.onMonthTips") }}
      <br />
      {{ $t("flowDsetection.flowSee.onCustomizeTips") }}
    </div>
    <el-form ref="form" :model="params" :rules="rules" label-width="140px">
      <el-form-item
        :label="$t('flowDsetection.flowSee.chooseExportType')"
        prop="countType"
      >
        <div class="radio-flex">
          <div
            v-for="(item, index) in radioList"
            :key="index"
            :class="[item.check ? 'check-item item-sty' : 'item-sty']"
            @click="checkFun(index)"
          >
            {{ item.name }}
          </div>
        </div>
      </el-form-item>
      <el-form-item v-if="isShow" prop="dateTime" label-width="0px">
        <el-date-picker
          v-model="params.dateTime"
          :default-time="['00:00:00', '23:59:59']"
          type="datetimerange"
          :picker-options="pickerOptions"
          align="right"
          :range-separator="$t('common.pickerDate.to')"
          :start-placeholder="$t('common.pickerDate.startDate')"
          :end-placeholder="$t('common.pickerDate.endDate')"
          value-format="yyyy-MM-dd HH:mm:ss"
          format="yyyy-MM-dd HH:mm:ss"
        >
        </el-date-picker>
      </el-form-item>
    </el-form>
    <div style="text-align: right; margin-top: 20px">
      <el-button @click="closed">{{
        $t("button.cancelText", { text: "" })
      }}</el-button>
      <el-button type="primary" @click="exportFun" :loading="dowloadLoading">{{
        $t("button.export")
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { exportData } from "../api";
export default {
  props: {
    seachObj: {
      type: Object,
      default: {},
    },
  },
  data() {
    return {
      dowloadLoading: false,
      isShow: false,
      dialogVisible: true,
      params: {
        countType: null,
        dateTime: null,
      },
      rules: {
        countType: [
          {
            required: true,
            message: $t("form.tip.chooseExportDateRange"),
            trigger: "change",
          },
        ],
        dateTime: [
          {
            required: true,
            message: $t("form.tip.chooseDateRange"),
            trigger: "change",
          },
        ],
      },
      radioList: [
        {
          value: 0,
          name: $t("flowDsetection.flowSee.onDay"),
          check: false,
        },
        {
          value: 1,
          name: $t("flowDsetection.flowSee.onWeek"),
          check: false,
        },
        {
          value: 2,
          name: $t("flowDsetection.flowSee.onMonth"),
          check: false,
        },
        {
          value: 9,
          name: $t("flowDsetection.flowSee.customize"),
          check: false,
        },
      ],
      pickerOptions: {
        shortcuts: [
          {
            text: $t("flowDsetection.flowSee.finalWeek"),
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: $t("flowDsetection.flowSee.finalMonth"),
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: $t("flowDsetection.flowSee.finalThreeMonths"),
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
    };
  },
  created() {},

  methods: {
    // 关闭回调
    closed() {
      this.$emit("close");
    },
    checkFun(index) {
      this.radioList.forEach((item, ind) => {
        item.check = false;
        if (index == ind) {
          item.check = true;
          this.params.countType = item.value;
          this.isShow = false;
          if (item.value == 9) {
            this.isShow = true;
          }
        }
      });
    },
    exportFun() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.dowloadLoading = true;
          let start = "";
          let end = "";
          if (this.params.dateTime && this.params.dateTime.length > 0) {
            start = this.params.dateTime[0];
            end = this.params.dateTime[1];
          }
          let form = new FormData();
          form.append("countType", this.params.countType);
          form.append("startTime", start);
          form.append("endTime", end);
          exportData(form)
            .then((res) => {
              this.dowloadLoading = false;
              var blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
              });
              var url = window.URL.createObjectURL(blob);
              var linkElement = document.createElement("a");
              linkElement.setAttribute("href", url);
              linkElement.click();
              document.body.removeChild(linkElement);
            })
            .catch((res) => {
              this.dowloadLoading = false;
            });
        } else {
          return false;
        }
      });
    },
  },
};
</script>
<style scoped lang="scss">
.tip {
  font-size: 12px;
  color: #1A0808;
  line-height: 20px;
  margin-bottom: 20px;
}
.radio-flex {
  display: flex;
}
.item-sty {
  font-size: 13px;
  padding: 0px 20px;
  border: 1px solid #dcdfe6;
  margin-right: 20px;
  cursor: pointer;
}
.check-item {
  background: #ecf5ff;
  border: 1px solid #337ecc !important;
}
</style>
