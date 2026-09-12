<template>
  <div class="flow-see">
    <div class="seach-sty">
      <el-form
        label-position="right"
        label-width="80px"
        style="flex: 1; display: flex"
      >
        <el-form-item :label="$t('common.caBelongingOrganization') + ':'">
          <el-cascader
            v-model="params.departIds"
            :options="depList"
            :props="{ value: 'id', label: 'name', multiple: true }"
            collapse-tags
            clearable
          >
          </el-cascader>
        </el-form-item>
        <el-form-item :label="$t('flowDsetection.flowSee.camera') + ':'">
          <el-cascader
            v-model="params.cameraId"
            :options="cameraOptions"
            :props="{ value: 'id', label: 'name', multiple: true }"
            collapse-tags
            clearable
          >
          </el-cascader>
        </el-form-item>
        <el-form-item :label="$t('flowDsetection.flowSee.dateTime') + ':'">
          <el-date-picker
            v-model="date"
            :default-time="['00:00:00', '23:59:59']"
            type="datetimerange"
            :range-separator="$t('common.pickerDate.to')"
            :start-placeholder="$t('common.pickerDate.startDate')"
            :end-placeholder="$t('common.pickerDate.endDate')"
            value-format="yyyy-MM-dd HH:mm:ss"
            format="yyyy-MM-dd HH:mm:ss"
            style="width: 330px"
            :clearable="false"
            :picker-options="pickerOptions"
          >
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div>
        <el-button
          size="mini"
          style="height: 34px; padding: 0px 15px"
          type="primary"
          icon="el-icon-search"
          @click="queryFun()"
          >{{ $t("button.searchText") }}</el-button
        >
        <el-button
          size="mini"
          style="height: 34px; padding: 0px 15px"
          icon="el-icon-refresh"
          @click="refreshData"
          >{{ $t("button.resetText", { text: "" }) }}</el-button
        >
        <el-button
          size="mini"
          style="height: 34px; padding: 0px 15px"
          type="primary"
          icon="el-icon-download"
          @click="downloadFun"
          >{{ $t("button.exportData") }}</el-button
        >
      </div>
    </div>
    <div style="display: flex">
      <div class="left-list">
        <div
          v-for="(item, index) in summaryList"
          :key="index"
          class="left-item"
        >
          <div class="item-title">{{ getTitle(item.type) }}</div>
          <div class="item-cont">
            <div style="text-align: center">
              <div class="cont-title">
                {{ $t("flowDsetection.flowSee.entry") }}
              </div>
               <div class="cont-num">{{ item.enterCount }}</div>
            </div>
            <div style="text-align: center">
              <div class="cont-title">
                {{ $t("flowDsetection.flowSee.level") }}
              </div>
              <div class="cont-num">{{ item.leaveCount }}</div>
            </div>
            <div style="text-align: center">
              <div class="cont-title">
                {{ $t("flowDsetection.flowSee.remaining") }}
              </div>
              <div class="cont-num">{{ item.remainCount }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="right-chart">
        <div
          class="mar-tb"
          style="display: flex; justify-content: space-between"
        >
          <div class="title">
            {{ $t("flowDsetection.flowSee.changesTraffic") }}
          </div>
          <div>
            <el-radio-group
              v-model="countType"
              size="small"
              @input="handleSelect"
            >
              <el-radio-button :label="0">{{
                $t("flowDsetection.flowSee.onDay")
              }}</el-radio-button>
              <el-radio-button :label="1">{{
                $t("flowDsetection.flowSee.onWeek")
              }}</el-radio-button>
              <el-radio-button :label="2">{{
                $t("flowDsetection.flowSee.onMonth")
              }}</el-radio-button>
              <el-radio-button :label="3">{{
                $t("flowDsetection.flowSee.customize")
              }}</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div class="chart" id="chart"></div>
      </div>
    </div>
    <div class="border-sty">
      <div class="title mar-tb">
        {{ $t("flowDsetection.flowSee.singleTotal") }}
      </div>
      <div>
        <div class="table-sty" v-if="tableData.length > 0">
          <div v-for="item in tableData" :key="item.id" class="item-sty">
            <div class="list-flex">
              <div class="list-title">{{ item.cameraName }}</div>
              <div class="list-title" style="color: #1A0808">
                {{ item.departName
                }}{{ item.boxIp ? "(" + item.boxIp + ")" : "" }}
              </div>
            </div>
            <div class="list-flex" style="margin-top: 16px">
              <div style="text-align: center">
                <div class="mini-title">
                  {{ $t("flowDsetection.flowSee.entry") }}
                </div>
                <div class="list-num">{{ item.enterCount }}</div>
              </div>
              <div style="text-align: center">
                <div class="mini-title">
                  {{ $t("flowDsetection.flowSee.level") }}
                </div>
                <div class="list-num">{{ item.leaveCount }}</div>
              </div>
              <div style="text-align: center">
                <div class="mini-title">
                  {{ $t("flowDsetection.flowSee.remaining") }}
                </div>
                <div class="list-num">{{ item.remainCount }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="noData" v-else>
          <el-empty :description="$t('common.noData')"></el-empty>
        </div>
        <div class="pagination" style="text-align: right">
          <el-pagination
            background
            :current-page="params.page"
            :page-size="params.limit"
            :page-sizes="[params.limit]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          ></el-pagination>
        </div>
      </div>
    </div>
    <Download v-if="downloadVisible" :seachObj="params" @close="closeHandle" />
  </div>
</template>
<script>
import { summaryV2, listPage, timelineV2, listTree, treeForCass } from "./api";
import { getCameraListData } from "@/api/applicationMonitoring/alarmManagement";
import * as echarts from "echarts";
import Download from "./components/download.vue";
export default {
  components: {
    Download,
  },
  data() {
    return {
      countType: 0,
      date: [],
      depList: [],
      params: {
        cameraId: "",
        startDate: "",
        endDate: "",
        departIds: "",
        limit: 9,
        page: 1,
      },
      cameraOptions: [],
      summaryList: {},
      total: 0,
      tableData: [],
      downloadVisible: false,
      timer: null,
      timerObj: null,
      timerChart: null,
      pickerOptions: {
        shortcuts: [
          {
            text: $t("flowDsetection.flowSee.onDay"),
            onClick(picker) {
              const start = new Date();
              start.setHours(0, 0, 0, 0); // 设置为00:00:00
              const end = new Date();
              end.setHours(23, 59, 59, 999); // 设置为23:59:59
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: $t("flowDsetection.flowSee.onWeek"),
            onClick(picker) {
              const offset = 0;
              const now = new Date();
              const dayOfWeek = now.getDay();
              const start = new Date(now);
              const end = new Date(now);

              // 调整日期到本周的开始日期（星期一）
              if (dayOfWeek !== 0) {
                start.setDate(start.getDate() - dayOfWeek + 1);
              }

              // 调整日期到本周的结束日期（星期日）
              if (dayOfWeek !== 6) {
                end.setDate(end.getDate() + 7 - dayOfWeek);
              }

              // 可以选择性地添加天数偏移量
              start.setDate(start.getDate() + offset * 7);
              start.setHours(0, 0, 0, 0);
              end.setDate(end.getDate() + offset * 7);
              end.setHours(23, 59, 59, 999);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: $t("flowDsetection.flowSee.onMonth"),
            onClick(picker) {
              const start = new Date();
              const now = new Date();
              start.setDate(1); // 设置为本月的第一天
              start.setHours(0, 0, 0, 0); // 设置为00:00:00
              const end = new Date(now.getFullYear(), now.getMonth() + 1, 0);
              end.setHours(23, 59, 59, 999); // 设置为23:59:59
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
    };
  },
  async created() {
    this.getListTree();
    await this.getOptions();
    await this.getSummaryData();
    await this.getLine();
    await this.getListData();
    let that = this;
    this.timer = setInterval(function () {
      that.getSummaryData();
    }, 5000);
    this.timerObj = setInterval(function () {
      that.getListData();
    }, 5000);
    this.timerChart = setInterval(function () {
      that.getLine();
    }, 5000);
  },
  methods: {
    //获取组织树
    getListTree() {
      this.treeData = [];
      listTree().then((res) => {
        if (res.data && res.data.length > 0) {
          this.treeData = res.data;
          this.depList = this.getData(res.data);
        }
      });
    },
    getData(data) {
      data.forEach((item) => {
        if (item.children.length < 1) {
          item.children = undefined;
        } else {
          this.getData(item.children);
        }
      });
      return data;
    },
    // 获取统计图数据
    async getLine() {
      let formData = new FormData();
      formData.append("type", this.countType);
      if (this.countType == 3) {
        if (this.date && this.date.length > 0) {
          formData.append("startTime", this.date[0]);
          formData.append("endTime", this.date[1]);
        } else {
          formData.append("startTime", "");
          formData.append("endTime", "");
        }
      }
      const res = await timelineV2(formData);
      let obj = res.data;
      let xAxis = obj.labels;
      let enterCount = obj.enterCounts;
      let leaveCount = obj.leaveCounts;
      let remainCount = obj.remainCounts;
      let seriesData = [
        {
          name: $t("flowDsetection.flowSee.entry"),
          type: "line",
          data: enterCount,
          itemStyle: {
            color: "#E53935", // 这里设置折线的颜色为红色
          },
        },
        {
          name: $t("flowDsetection.flowSee.level"),
          type: "line",
          data: leaveCount,
          itemStyle: {
            color: "#2EAB00", // 这里设置折线的颜色为红色
          },
        },
        {
          name: $t("flowDsetection.flowSee.remaining"),
          type: "line",
          data: remainCount,
          itemStyle: {
            color: "#DD383E", // 这里设置折线的颜色为红色
          },
        },
      ];
      const chartDom = document.getElementById("chart");
      const myChart = echarts.init(chartDom);
      myChart.clear();
      const option = {
        backgroundColor: "#fff",
        tooltip: {
          trigger: "axis",
        },
        legend: {
          icon: "rect",
          // 图例的位置
          left: "center",
          // 图例的标记的样式
          itemWidth: 12, // 图例标记图形宽度
          itemHeight: 8, // 图例标记图形高度
          itemGap: 10, // 图例每项之间的间隔
          data: [
            $t("flowDsetection.flowSee.entry"),
            $t("flowDsetection.flowSee.level"),
            $t("flowDsetection.flowSee.remaining"),
          ],
          top: "3%",
          right: "3%",
        },
        grid: {
          top: "15%",
          left: "3%",
          right: "3%",
          bottom: 40,
          containLabel: true,
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: xAxis,
        },
        yAxis: {
          type: "value",
        },
        series: seriesData,
        dataZoom: [
          {
            height: 20, //时间滚动条的高度
            type: "slider", //的作用是指定数据缩放的类型，表示使用滑动条进行缩放，表示使用鼠标滚轮进行缩放。
            xAxisIndex: 0, //作用在x轴的下标（因为x轴可以有多个）
            filterMode: "filter", //间滚动条的过滤模式,'filter'表示滑动时间条时会直接过滤掉不在时间范围内的数据，'weakFilter'表示滑动时间条时会逐渐过滤掉不在时间范围内的数据。
            start: 0, //默认开始位置（百分比）
            end: 100, //默认结束位置（百分比）
          },
          {
            type: "inside",
            xAxisIndex: 0,
            filterMode: "filter",
            start: 0,
            end: 100,
          },
        ],
      };
      myChart.setOption(option);
    },
    // 获取摄像头下拉
    async getOptions() {
      const res = await treeForCass();
      this.cameraOptions = res.data;
    },
    handleSelect() {
      this.getLine();
      let that = this;
      if (this.timerChart) {
        clearInterval(this.timerChart);
        this.timerChart = null;
      }
      this.timerChart = setInterval(function () {
        that.getLine();
      }, 5000);
    },
    // 获取总数据
    async getSummaryData() {
      let formData = new FormData();
      if (this.date && this.date.length > 0) {
        formData.append("startTime", this.date[0]);
        formData.append("endTime", this.date[1]);
      } else {
        formData.append("startTime", "");
        formData.append("endTime", "");
      }
      const res = await summaryV2(formData);
      this.summaryList = res.data;
    },
    // 重置
    refreshData() {
      this.date = [];
      let len = this.params.limit;
      Object.assign(this.params, {
        cameraId: "",
        departIds: "",
        limit: len,
        page: 1,
      });
      this.queryFun();
    },
    // 查询
    queryFun() {
      this.getSummaryData();
      this.getLine();
      this.getListData();
      let that = this;
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
      if (this.timerObj) {
        clearInterval(this.timerObj);
        this.timerObj = null;
      }
      if (this.timerChart) {
        clearInterval(this.timerChart);
        this.timerChart = null;
      }
      this.timer = setInterval(function () {
        that.getSummaryData();
      }, 5000);
      this.timerObj = setInterval(function () {
        that.getListData();
      }, 5000);
      this.timerChart = setInterval(function () {
        that.getLine();
      }, 5000);
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getListData();
      if (this.timerObj) {
        clearInterval(this.timerObj);
        this.timerObj = null;
      }
      let that = this;
      this.timerObj = setInterval(function () {
        that.getListData();
      }, 5000);
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getListData();
      if (this.timerObj) {
        clearInterval(this.timerObj);
        this.timerObj = null;
      }
      let that = this;
      this.timerObj = setInterval(function () {
        that.getListData();
      }, 5000);
    },
    // 获取表格数据
    async getListData() {
      let cameraIds = [];
      if (this.params.cameraId && this.params.cameraId.length > 0) {
        this.params.cameraId.forEach((item) => {
          cameraIds.push(item[item.length - 1]);
        });
      }
      let departIds = [];
      if (this.params.departIds && this.params.departIds.length > 0) {
        this.params.departIds.forEach((item) => {
          departIds.push(item[item.length - 1]);
        });
      }
      let formData = new FormData();
      formData.append("page", this.params.page);
      formData.append("limit", this.params.limit);
      formData.append("cameraIds", cameraIds.toString());
      formData.append("departIds", departIds.toString());
      if (this.date && this.date.length > 0) {
        formData.append("startTime", this.date[0]);
        formData.append("endTime", this.date[1]);
      } else {
        formData.append("startTime", "");
        formData.append("endTime", "");
      }
      const res = await listPage(formData);
      this.tableData = res.data;
      this.total = Number(res.count);
    },
    // 导出
    downloadFun() {
      this.downloadVisible = true;
    },
    closeHandle() {
      this.downloadVisible = false;
    },
    getTitle(type) {
      const str = {
        0: $t("flowDsetection.flowSee.onDay"),
        1: $t("flowDsetection.flowSee.onWeek"),
        2: $t("flowDsetection.flowSee.onMonth"),
        3: $t("flowDsetection.flowSee.customize"),
      }[type];
      return str;
    },
  },
  beforeDestroy() {
    // 在组件销毁前清除定时器
    if (this.timerObj) {
      clearInterval(this.timerObj);
      this.timerObj = null;
    }
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
    if (this.timerChart) {
      clearInterval(this.timerChart);
      this.timerChart = null;
    }
  },
};
</script>
<style scoped lang="scss">
.flow-see {
  background: #fff;
  border-radius: 6px;
  padding: 16px;
  .el-menu-demo {
    width: 272px;
    margin-left: 50%;
    transform: translate(-50%, 0);
  }
  :deep(.el-menu) {
    border-bottom: solid 1px #e6e6e6 !important;
  }
  :deep(.el-menu--horizontal > .el-menu-item.is-active) {
    border-bottom: 2px solid #E53935;
    color: #E53935;
  }
  :deep(.el-menu-item) {
    min-width: 60px !important;
    text-indent: 0px;
    color: #000;
    font-weight: bold;
  }
  :deep(.el-menu .el-menu-item:hover) {
    background: transparent !important;
    color: #E53935;
  }
  .seach-sty {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    padding: 5px 0px 15px 0px;
    border-bottom: 1px solid #edf0f3;
    margin-bottom: 20px;
    .seach-flex {
      display: flex;
      align-items: center;
      font-size: 13px;
      margin-right: 10px;
    }
  }
  .flex-cont {
    margin-top: 24px;
    display: flex;
  }
  .marg-lr {
    margin: 0px 16px;
  }
  .module-cont {
    padding: 16px;
    background: #fff;
    border: 1px solid #ebeef5;
    box-shadow: 0px 12px 32px 4px rgba(0, 0, 0, 0.04),
      0px 8px 20px 0px rgba(0, 0, 0, 0.08);
  }
  .title {
    font-size: 20px;
    line-height: 28px;
    color: #303133;
  }
  .num {
    padding: 16px 0px;
    font-size: 36px;
    color: #1A0808;
  }
  .mar-tb {
    margin: 16px 0px;
  }
  .table-sty {
    display: grid;
    grid-template-columns: repeat(3, 32%);
    justify-content: space-between;
    grid-row-gap: 20px;
    margin-bottom: 16px;
  }
  .item-sty {
    border: 1px solid #ebeef5;
    padding: 24px;
  }
  .noData {
    line-height: 120px;
    text-align: center;
  }
  .chart {
    width: 100%;
    height: 558px;
  }
  .list-title {
    color: #303133;
    font-size: 16px;
    line-height: 24px;
  }
  .list-flex {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    align-items: center;
    .mini-title {
      color: #1A0808;
      font-size: 12px;
      line-height: 20px;
    }
    .list-num {
      color: #303133;
      font-size: 20px;
      line-height: 28px;
    }
  }
  .border-sty {
    border: 1px solid #ebeef5;
    padding: 16px;
  }
  .left-list {
    width: 346px;
    margin-right: 32px;
    .left-item {
      border: 1px solid #e4e7ed;
      margin-bottom: 20px;
    }
    .item-title {
      padding: 20px;
      text-align: center;
      color: #303133;
      font-size: 16px;
      line-height: 24px;
      border-bottom: 1px solid #e4e7ed;
    }
    .item-cont {
      padding: 20px;
      display: flex;
      justify-content: space-between;
    }
    .cont-title {
      color: #1A0808;
      font-size: 12px;
      line-height: 20px;
    }
    .cont-num {
      color: #303133;
      font-size: 16px;
      line-height: 24px;
    }
  }
  .right-chart {
    border: 1px solid #e4e7ed;
    padding: 20px;
    flex: 1;
    margin-bottom: 20px;
  }
}
</style>
