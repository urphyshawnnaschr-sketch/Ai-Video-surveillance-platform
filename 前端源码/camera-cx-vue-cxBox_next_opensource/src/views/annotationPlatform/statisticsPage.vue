<template>
  <div>
    <div class="head-container">
      <div>
        <el-select
          :placeholder="$t('annotationplatform.statisticspage.712fyp')"
          clearable
          v-model="params.projectId"
          class="head-container-input"
        >
          <el-option
            v-for="(item, index) in projectOptions"
            :key="index"
            :label="item.projectName"
            :value="item.id"
          ></el-option>
        </el-select>
        <el-select
          :placeholder="$t('annotationplatform.statisticspage.8ks37c')"
          clearable
          v-model="params.teamId"
          class="head-container-input"
        >
          <el-option
            v-for="(item, index) in teamOptions"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
        <el-date-picker
          v-model="date"
          :picker-options="pickerOptions"
          type="daterange"
          :range-separator="$t('common.pickerDate.to')"
          :start-placeholder="$t('common.pickerDate.startDate')"
          :end-placeholder="$t('common.pickerDate.endDate')"
          value-format="yyyy-MM-dd"
          format="yyyy-MM-dd"
          style="margin-right: 10px"
        >
        </el-date-picker>
        <el-button type="primary" icon="el-icon-search" @click="getData(1)">{{
          $t("button.queryText")
        }}</el-button>
        <el-button icon="el-icon-refresh" @click="refreshData">{{
          $t("button.resetText", { text: "" })
        }}</el-button>
      </div>
      <div style="width: 120px; flex-shrink: 0; margin-top: 20px;">
        <el-radio-group v-model="active" @input="getData(1)">
          <el-radio-button label="1">{{$t('annotationplatform.statisticspage.gpjb47')}}</el-radio-button>
          <el-radio-button label="2">{{$t('annotationplatform.statisticspage.3411wr')}}</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    <div>
      <div class="ai_table">
        <el-row :gutter="20">
          <el-col :span="12">
            <Chart1 class="chart" :chartData="chartData.imageVOList" />
          </el-col>
          <el-col :span="12">
            <Chart2
              class="chart"
              chartId="chart2"
              :chartData="chartData.reviewVOList"
            />
          </el-col>
        </el-row>
        <ProjectTable v-if="active == 1" :tableData="tableData" />
        <ProjectTable v-if="active == 2" :tableData="tableData" />
        <div class="pagination">
          <el-pagination
            background
            :current-page="params.pageNum"
            :page-size="params.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          ></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {
  getProjectList,
  getTeamList,
  getProjectData,
  getTeamData,
  getProjectPage,
  getTeamPage,
} from "@/api/annotationPlatform/statisticsPage";
import Chart1 from "@/components/annotationPlatform/statisticsPage/chart1";
import Chart2 from "@/components/annotationPlatform/statisticsPage/chart2";
import ProjectTable from "@/components/annotationPlatform/statisticsPage/projectTable";
import TeamTable from "@/components/annotationPlatform/statisticsPage/teamTable";
export default {
  components: {
    Chart1,
    Chart2,
    ProjectTable,
    TeamTable,
  },
  data() {
    return {
      status: false,
      active: 1,
      projectOptions: [],
      teamOptions: [],
      date: [
        this.$moment(new Date()).format("YYYY-MM-DD"),
        this.$moment(new Date()).format("YYYY-MM-DD"),
      ],
      pickerOptions: {
        disabledDate(date) {
          return date.getTime() > Date.now();
        },
      },
      params: {
        pageNum: 1,
        pageSize: 10,
        projectId: "",
        teamId: "",
        startTime: "",
        endTime: "",
      },
      total: 0,
      chartData: {},
      tableData: [],
    };
  },
  created() {
    this.getProjectList();
    this.getTeamList();
    this.getData();
  },
  methods: {
    // 获取项目列表
    async getProjectList() {
      const data = await getProjectList();
      this.projectOptions = data.data;
    },
    // 获取团队列表
    async getTeamList() {
      const data = await getTeamList();
      this.teamOptions = data.data;
    },
    async getData(page) {
      if (page == 1) {
        this.params.pageNum = 1;
      }
      this.params.startTime = this.date[0];
      this.params.endTime = this.date[1];
      console.info(this.params);
      let data1, data2;
      if (this.active == 1) {
        data1 = await getProjectData(this.params);
        data2 = await getProjectPage(this.params);
      } else {
        data1 = await getTeamData(this.params);
        data2 = await getTeamPage(this.params);
      }
      this.chartData = data1.data;
      this.tableData = data2.data;
      this.total = Number(data2.count);
    },
    refreshData() {
      this.date = [
        this.$moment(new Date()).format("YYYY-MM-DD"),
        this.$moment(new Date()).format("YYYY-MM-DD"),
      ];
      Object.assign(this.params, {
        pageNum: 1,
        pageSize: 10,
        projectId: "",
        teamId: "",
        startTime: "",
        endTime: "",
      });
      this.getData();
    },
    handleCurrentChange(val) {
      this.params.pageNum = val;
      this.getData();
    },
    handleSizeChange(val) {
      this.params.pageSize = val;
      this.params.page = 1;
      this.getData();
    },
  },
};
</script>
<style scoped lang="scss">
.head-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

::v-deep .el-input {
  margin-bottom: 0 !important;
}

.chart {
  width: 100%;
  height: 350px;
  border: 1px #f1f5fb solid;
}
.head-container-input {
  margin-right: 10px;
}
</style>
