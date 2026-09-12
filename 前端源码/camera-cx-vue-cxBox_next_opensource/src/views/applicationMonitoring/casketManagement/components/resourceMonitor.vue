<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      width="80%"
      top="10vh"
      :title="$t('applicationMonitoring.casketManagement.ITMonitoring')"
      :visible.sync="resourceVisible"
      @closed="closed"
      append-to-body
    >
      <div class="resource-cont">
        <div class="title">
          {{ $t("applicationMonitoring.common.baseInfo") }}
        </div>
        <div class="top-cont">
          <div class="card-item">device mode: {{ baseObj.deviceMode }}</div>
          <div class="card-item">host CPU: {{ baseObj.cpuVersion }}</div>
          <div class="card-item">
            kernel version: {{ baseObj.kernelVersion }}
          </div>
          <div class="card-item">
            LynDriver version: {{ baseObj.lyndriverVersion }}
          </div>
          <div class="card-item">
            LynSDK version: {{ baseObj.lynsdkVersion }}
          </div>
          <div class="card-item">OS version: {{ baseObj.osVersion }}</div>
        </div>
        <div class="title">
          {{ $t("applicationMonitoring.casketManagement.hostSideInformation") }}
        </div>
        <div class="chart-cont">
          <el-row>
            <el-col :span="12" style="padding-right: 20px">
              <el-card class="box-card">
                <div class="flex-item">
                  <div class="center">
                    <div class="title-txt">
                      {{
                        $t(
                          "applicationMonitoring.casketManagement.totalHostDiskCount"
                        )
                      }}
                    </div>
                    <div class="colorG">{{ baseObj.diskTotalStr }}</div>
                  </div>
                  <div class="center">
                    <div class="title-txt">
                      {{
                        $t(
                          "applicationMonitoring.casketManagement.totalAvailableHostdisks"
                        )
                      }}
                    </div>
                    <div class="colorG">{{ baseObj.diskFreeTotalStr }}</div>
                  </div>
                </div>
              </el-card>
              <el-card class="box-card">
                <div class="center">
                  <div class="title-txt">
                    {{
                      $t(
                        "applicationMonitoring.casketManagement.totalHostMemory"
                      )
                    }}
                  </div>
                  <div class="colorG">{{ baseObj.memoryTotalStr }}</div>
                </div>
              </el-card>
              <el-card class="card-H260">
                <div class="title-txt center">
                  {{
                    $t("applicationMonitoring.casketManagement.CPUUsageRate")
                  }}
                </div>
                <LineEchart
                  v-if="params.cpuDatas"
                  id="cpuDatas"
                  :xLabel="params.labels"
                  :yList="params.cpuDatas"
                />
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card class="card-H200">
                <div>
                  <div class="title-txt">
                    {{
                      $t("applicationMonitoring.casketManagement.CPUUsageRate")
                    }}
                  </div>
                  <div class="mar-20">
                    <el-progress :percentage="params.cpuUsed"></el-progress>
                  </div>
                </div>
                <div>
                  <div class="title-txt">
                    {{
                      $t(
                        "applicationMonitoring.casketManagement.memoryUsageRate"
                      )
                    }}
                  </div>
                  <div class="mar-20">
                    <el-progress :percentage="params.memoryUsed"></el-progress>
                  </div>
                </div>
              </el-card>
              <el-card class="card-H260">
                <div class="title-txt center">
                  {{
                    $t("applicationMonitoring.casketManagement.memoryUsageRate")
                  }}
                </div>
                <LineEchart
                  v-if="params.memoryDatas"
                  id="memoryDatas"
                  :xLabel="params.labels"
                  :yList="params.memoryDatas"
                />
              </el-card>
            </el-col>
          </el-row>
        </div>
        <div class="title">
          {{ $t("applicationMonitoring.casketManagement.chipSideInformation") }}
        </div>
        <div class="chart-cont">
          <el-row>
            <el-col :span="12" style="padding-right: 20px">
              <el-card class="card-H260">
                <div class="title-txt center">
                  {{
                    $t(
                      "applicationMonitoring.casketManagement.APUUtilizationRate"
                    )
                  }}
                </div>
                <LineEchart
                  v-if="params.apuDatas"
                  id="apuDatas"
                  :xLabel="params.labels"
                  :yList="params.apuDatas"
                />
              </el-card>
              <el-card class="card-H260">
                <div class="title-txt center">
                  {{
                    $t(
                      "applicationMonitoring.casketManagement.VICUtilizationRate"
                    )
                  }}
                </div>
                <LineEchart
                  v-if="params.vicDatas"
                  id="vicDatas"
                  :xLabel="params.labels"
                  :yList="params.vicDatas"
                />
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card class="card-H260">
                <div class="title-txt center">
                  {{
                    $t(
                      "applicationMonitoring.casketManagement.IPEUtilizationRate"
                    )
                  }}
                </div>
                <LineEchart
                  v-if="params.ipeDatas"
                  id="ipeDatas"
                  :xLabel="params.labels"
                  :yList="params.ipeDatas"
                />
              </el-card>
              <el-card class="card-H260">
                <div class="title-txt center">
                  {{ $t("applicationMonitoring.casketManagement.temperature") }}
                </div>
                <LineEchart
                  v-if="params.temperatureDatas"
                  id="temperatureDatas"
                  :xLabel="params.labels"
                  :yList="params.temperatureDatas"
                />
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { aiboxStatus } from "@/api/applicationMonitoring/casketManagement";
import LineEchart from "./lineEchart/index.vue";
export default {
  components: {
    LineEchart,
  },
  props: {
    rowId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      resourceVisible: true,
      baseObj: {},
      params: {},
    };
  },
  created() {
    this.getData();
  },
  methods: {
    // 获取数据
    async getData() {
      const { data } = await aiboxStatus({ id: this.rowId });
      this.baseObj = { ...data.base };
      this.params = { ...data };
      console.log(this.params);
    },
    // 关闭弹窗
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss">
.resource-cont {
  color: black;
  .top-cont {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
  }
  .title {
    margin-bottom: 20px;
    font-size: 16px;
    font-weight: bold;
  }
  .card-item {
    width: 25%;
    margin-bottom: 20px;
  }
  .chart-cont {
    margin-bottom: 20px;
    .box-card {
      width: 100%;
      height: 90px;
    }
    .center {
      text-align: center;
    }
    .flex-item {
      display: flex;
      justify-content: space-around;
    }
    .title-txt {
      font-weight: bold;
    }
    .colorG {
      font-weight: bold;
      color: #6eaf46;
      margin-top: 15px;
    }
    .card-H200 {
      width: 100%;
      height: 190px;
    }
    .mar-20 {
      margin: 15px 0px;
    }
    .card-H260 {
      width: 100%;
      height: 260px;
    }
  }
}
::v-deep .el-card.is-always-shadow {
  margin-bottom: 10px !important;
}
::v-deep .el-progress-bar__outer {
  height: 30px !important;
  border-radius: unset;
}
::v-deep .el-progress-bar__inner {
  border-radius: unset;
}
</style>
