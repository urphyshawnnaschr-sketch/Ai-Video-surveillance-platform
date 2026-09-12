<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('alarmmanagement.alarmcollectsetting.uz5xox')"
    :visible.sync="dialogVisible"
    width="500px"
    :append-to-body="true"
    @closed="closed"
  >
    <!-- <el-form ref="form" :model="params" :rules="rules" label-width="120px">
      <el-form-item label="Select algorithms" prop="clearReportDay"> -->
    <el-table :data="tableData" height="450" border style="width: 100%">
      <el-table-column prop="name" :label="$t('common.algorithmName')">
      </el-table-column>
      <el-table-column
        :label="$t('alarmmanagement.alarmcollectsetting.m8u44x')"
        align="center"
        width="300"
      >
        <template slot-scope="scope">
          <!-- <el-switch
                v-model="params[`push_` + scope.row.id]"
                :active-value="1"
                :inactive-value="0"
                active-color="#13ce66"
                inactive-color="#ff4949"
                active-text="Manual Push"
                inactive-text="Auto Push"
              >
              </el-switch> -->

          <el-radio-group v-model="params[`push_` + scope.row.id]" size="mini">
            <el-radio label="0" border>{{
              $t("alarmmanagement.alarmcollectsetting.1kkt57")
            }}</el-radio>
            <el-radio label="1" border>{{
              $t("alarmmanagement.alarmcollectsetting.08l23o")
            }}</el-radio>
          </el-radio-group>
        </template>
      </el-table-column>
    </el-table>
    <!-- <el-table-column label="采集开关" align="center" width="80">
            <template slot-scope="scope">
              <el-switch
                v-model="params[`collect_` + scope.row.id]"
                :active-value="1"
                :inactive-value="0"
                active-color="#13ce66"
                inactive-color="#ff4949"
              >
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="Collection confidence" align="center" width="180">
            <template slot-scope="scope">
              <el-input-number
                v-model="params[`confidence_` + scope.row.id]"
                :min="0.05"
                :max="1.0"
                :step="0.05"
                step-strictly
                size="mini"
              ></el-input-number>
            </template>
          </el-table-column>
          <el-table-column
            prop="view"
            label="Collection Result"
            align="center"
            width="140"
          >
            <template slot-scope="scope">
              <el-link type="primary" @click="handleOpenAlarmCollectData(scope.row.id)">View</el-link>
            </template>
          </el-table-column>
        
      </el-form-item>
      <el-form-item label="Data collection days" prop="collectDay">
        <el-input-number
          v-model="params.collectDay"
          :min="1"
          :max="15"
          :step="1"
          step-strictly
        ></el-input-number>
      </el-form-item>
      <el-form-item label="" prop="x">
        <el-link type="primary" @click="handleRemoveCollect">
          <i class="el-icon-delete" /> Collected<span style="padding: 0 5px 0 0; color: #E53935">{{ alarmCollectCount }}</span>records，Click to clear data
        </el-link>
      </el-form-item>
    </el-form> -->
    <div style="text-align: right; margin-top: 20px">
      <el-button @click="closed">{{
        $t("button.cancelText", { text: "" })
      }}</el-button>
      <el-button type="primary" @click="submitForm">
        {{ $t("button.saveText", { text: "" }) }}
      </el-button>
    </div>

    <!-- 采集数据总览 -->
    <AlarmCollectData
      v-if="alarmCollectDataVisible"
      :currentAlgorithmId="currentAlgorithmId"
      @close="handleCloseAlarmCollectData"
    />
  </el-dialog>
</template>
<script>
import {
  saveAlarmCollect,
  getAlarmCollect,
  deleteAlarmCollect,
  getAlarmCollectCount,
  saveAlgoPush,
} from "@/api/applicationMonitoring/alarmManagement";

import AlarmCollectData from "@/components/applicationMonitoring/alarmManagement/alarmCollectData";
export default {
  components: {
    AlarmCollectData,
  },
  data() {
    return {
      dialogVisible: true,
      params: {
        collectDay: 1,
      },
      rules: {
        collectDay: [
          {
            required: true,
            message: this.$t("alarmmanagement.alarmcollectsetting.l14952"),
            trigger: "change",
          },
        ],
      },
      tableData: [],
      alarmCollectDataVisible: false,
      alarmCollectCount: 0,
      currentAlgorithmId: null,
    };
  },
  created() {
    this.fetchAlarmCollect();
    this.fetchAlarmCollectCount();
  },
  methods: {
    // 查询采集数据总数
    fetchAlarmCollectCount() {
      getAlarmCollectCount().then((res) => {
        this.alarmCollectCount = res.data;
      });
    },
    // 查询数据采集设置
    fetchAlarmCollect() {
      getAlarmCollect().then((res) => {
        this.params.collectDay = res.data.collectDay;
        this.tableData = res.data.collectAlgos;
        this.tableData.forEach((item) => {
          this.$set(
            this.params,
            `confidence_` + item.id,
            item.collectConfidence
          );
          this.$set(
            this.params,
            `collect_` + item.id,
            String(item.collectFlag ?? "0")
          );
          this.$set(
            this.params,
            `push_` + item.id,
            String(item.pushEnable ?? "1")
          );
        });
      });
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
    submitForm() {
      // this.$refs.form.validate(async (valid) => {
      //   if (valid) {
      var collectAlgos = [];
      this.tableData.forEach((item) => {
        let collect = 0;
        let confidence = 0.01;
        let push = "1";
        let collect_key = `collect_` + item.id;
        let confidence_key = `confidence_` + item.id;
        let push_key = `push_` + item.id;

        for (const key in this.params) {
          if (this.params.hasOwnProperty(key)) {
            if (key == collect_key) {
              collect = this.params[key];
            }
            if (key == confidence_key) {
              confidence = this.params[key];
            }
            if (key == push_key) {
              push = this.params[key];
            }
          }
        }

        collectAlgos.push({
          algoId: item.id,
          collectFlag: collect,
          collectConfidence: confidence,
          pushEnable: push,
        });
      });

      let data = {
        collectDay: this.params.collectDay,
        collectAlgos: collectAlgos,
      };
      saveAlarmCollect(data).then((res) => {
        this.$message.success(
          $t("button.saveText", { text: $t("common.success") })
        );
        this.closed();
      });
      //   } else {
      //     return false;
      //   }
      // });
    },
    // 打开采集数据总览
    handleOpenAlarmCollectData(algorithmId) {
      this.currentAlgorithmId = algorithmId;
      this.alarmCollectDataVisible = true;
    },
    // 关闭采集数据总览
    handleCloseAlarmCollectData() {
      this.alarmCollectDataVisible = false;
    },
    // 清除数据
    handleRemoveCollect() {
      this.$confirm(
        this.$t("alarmmanagement.alarmcollectsetting.uz5xov"),
        $t("common.prompt"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          type: "warning",
        }
      ).then(() => {
        deleteAlarmCollect().then((res) => {
          this.alarmCollectCount = 0;
          this.$message({
            type: "success",
            message: $t("button.deleteText", { text: "!" }),
          });
        });
      });
    },
  },
};
</script>
<style scoped lang="scss">
::v-deep .el-radio {
  margin-right: 8px;
}
// .tip {
//   font-size: 12px;
//   color: #1A0808;
//   line-height: 20px;
//   margin-bottom: 20px;
// }
// .radio-flex {
//   display: flex;
// }
// .item-sty {
//   font-size: 13px;
//   padding: 0px 20px;
//   border: 1px solid #dcdfe6;
//   ;
//   margin-right: 20px;
//   cursor: pointer;
// }
// .check-item {
//   background: #ecf5ff;
//   border: 1px solid #337ecc !important;
// }
</style>
