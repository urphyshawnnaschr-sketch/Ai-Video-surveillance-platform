<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('alarmmanagement.alarmclearsetting.6sh75y')"
    :visible.sync="dialogVisible"
    width="600px"
    :append-to-body="true"
    @closed="closed"
  >
    <div class="tip">
      {{ $t("alarmmanagement.alarmclearsetting.n41y16") }}<br />{{
        $t("alarmmanagement.alarmclearsetting.x62p69")
      }}<br />
    </div>
    <el-form ref="form" :model="params" :rules="rules" label-width="140px">
      <el-form-item
        :label="$t('alarmmanagement.alarmclearsetting.2srnww')"
        prop="clearReportDay"
      >
        <el-select
          :placeholder="$t('common.chooseText')"
          v-model="params.clearReportDay"
          style="width: 250px"
        >
          <el-option
            v-for="(item, index) in clearDayList"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('alarmmanagement.alarmclearsetting.y1q2z3')"
        prop="clearReportImageDay"
      >
        <el-select
          :placeholder="$t('common.chooseText')"
          v-model="params.clearReportImageDay"
          style="width: 250px"
        >
          <el-option
            v-for="(item, index) in clearReportImageDayList"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div style="text-align: right; margin-top: 20px">
      <el-button @click="closed">{{
        $t("button.cancelText", { text: "" })
      }}</el-button>
      <el-button type="primary" @click="submitForm">{{
        $t("button.saveText", { text: "" })
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import {
  getclearReportDayConfig,
  saveclearReportDayConfig,
} from "@/api/applicationMonitoring/alarmManagement";
export default {
  data() {
    return {
      dialogVisible: true,
      params: {
        clearReportDay: "30",
        clearReportImageDay: "30",
      },
      rules: {
        clearReportDay: [
          {
            required: true,
            message: this.$t("alarmmanagement.alarmclearsetting.8c25jc"),
            trigger: "change",
          },
        ],
      },
      clearDayList: [
        {
          id: "30",
          name: this.$t("alarmmanagement.alarmclearsetting.6xo6g2"),
        },
        {
          id: "90",
          name: this.$t("alarmmanagement.alarmclearsetting.qm6414"),
        },
        {
          id: "180",
          name: this.$t("alarmmanagement.alarmclearsetting.y1q223"),
        },
        {
          id: "365",
          name: this.$t("alarmmanagement.alarmclearsetting.z1q2z3"),
        },
      ],
      clearReportImageDayList: [
        {
          id: "30",
          name: this.$t("alarmmanagement.alarmclearsetting.6xo6g2"),
        },
        {
          id: "60",
          name: this.$t("alarmmanagement.alarmclearsetting.z24q2z3"),
        },
        {
          id: "90",
          name: this.$t("alarmmanagement.alarmclearsetting.qm6414"),
        },
        {
          id: "180",
          name: this.$t("alarmmanagement.alarmclearsetting.y1q223"),
        },
        {
          id: "270",
          name: this.$t("alarmmanagement.alarmclearsetting.z30q2z3"),
        },
        {
          id: "365",
          name: this.$t("alarmmanagement.alarmclearsetting.z1q2z3"),
        },
      ],
    };
  },
  created() {
    this.fetchClearReportDayConfig();
  },
  methods: {
    // 查询告警数据保存期限
    fetchClearReportDayConfig() {
      getclearReportDayConfig().then((res) => {
        this.params = res.data;
      });
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          let formData = new FormData();
          formData.append("clearReportDay", this.params.clearReportDay);
          formData.append("clearReportImageDay", this.params.clearReportImageDay);
          saveclearReportDayConfig(formData).then((res) => {
            if (res.code == 0) {
              this.$message.success(
                $t("button.saveText", { text: $t("common.success") })
              );
              this.closed();
            }
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
