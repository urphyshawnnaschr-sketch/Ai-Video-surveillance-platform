<template>
  <div>
    <div class="head-container">
      <el-select
        :placeholder="$t('common.camera')"
        clearable
        v-model="params.cameraId"
        class="head-container-input"
      >
        <el-option
          v-for="(item, index) in cameraOptions"
          :key="index"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-select
        :placeholder="$t('common.algorithm')"
        clearable
        v-model="params.algorithmId"
        class="head-container-input"
      >
        <el-option
          v-for="(item, index) in algorithmOptions"
          :key="index"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-select
        :placeholder="
          $t('applicationMonitoring.incrementalRraining.auditStatus')
        "
        clearable
        v-model="params.auditState"
        class="head-container-input"
      >
        <el-option
          v-for="(item, index) in auditStateOptions"
          :key="index"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-select
        :placeholder="
          $t('applicationMonitoring.incrementalRraining.auditResult')
        "
        clearable
        v-model="params.auditResult"
        class="head-container-input"
      >
        <el-option
          v-for="(item, index) in auditResultOptions"
          :key="index"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="getListData">{{
        $t("button.queryText")
      }}</el-button>
      <el-button icon="el-icon-refresh" @click="refreshData">{{
        $t("button.resetText", { text: "" })
      }}</el-button>
    </div>
    <div class="ai_table">
      <el-button
        type="primary"
        icon="el-icon-upload"
        style="margin-bottom: 10px"
        @click="exportVisible = true"
        >{{ $t("applicationMonitoring.incrementalRraining.exportData") }}
      </el-button>
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column
          align="center"
          prop="cameraName"
          :label="$t('common.cameraName')"
        >
        </el-table-column>
        <el-table-column
          align="center"
          prop="algorithmName"
          :label="$t('common.algorithmName')"
        >
        </el-table-column>
        <el-table-column
          align="center"
          prop="conf"
          :label="$t('applicationMonitoring.common.confidence')"
        >
        </el-table-column>
        <el-table-column align="center" :label="$t('common.alarmType')">
          <template slot-scope="scope">
            <template>{{ handleType(scope.row.type) }}</template>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="createdStr"
          :label="$t('common.createTime')"
          width="140"
        >
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('applicationMonitoring.incrementalRraining.auditStatus')"
        >
          <template slot-scope="scope">
            <template>{{ handleAuditState(scope.row.auditState) }}</template>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('applicationMonitoring.incrementalRraining.auditResult')"
        >
          <template slot-scope="scope">
            <template>{{ handleAuditResult(scope.row.auditResult) }}</template>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('common.action', { text: '' })"
          width="80"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="clickDetail(scope.row)">{{
              $t("applicationMonitoring.incrementalRraining.detailed")
            }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          background
          :current-page="params.page"
          :page-size="params.limit"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        ></el-pagination>
      </div>
    </div>
    <AlarmDetail
      :currentId="currentId"
      v-if="alarmDetailVisible"
      @close="(alarmDetailVisible = false), getListData()"
    />
    <Export
      v-if="exportVisible"
      @close="(exportVisible = false), getListData()"
    />
  </div>
</template>
<script>
import {
  getListData,
  getCameraListData,
  getAlgorithmListData,
  getTypeListData,
} from "@/api/applicationMonitoring/incrementalRraining";
import AlarmDetail from "@/components/applicationMonitoring/incrementalRraining/alarmDetail";
import Export from "@/components/applicationMonitoring/incrementalRraining/export";
export default {
  components: {
    AlarmDetail,
    Export,
  },
  data() {
    return {
      loading: false,
      alarmDetailVisible: false,
      exportVisible: false,
      tableData: [],
      currentId: "",
      params: {
        cameraId: "",
        algorithmId: "",
        auditState: "",
        auditResult: "",
        limit: 10,
        page: 1,
      },
      total: 0,
      cameraOptions: [],
      algorithmOptions: [],
      typeOptions: [],
      auditStateOptions: [
        {
          name: $t("applicationMonitoring.incrementalRraining.pendingApproval"),
          id: 0,
        },
        {
          name: $t("applicationMonitoring.incrementalRraining.audited"),
          id: 1,
        },
      ],
      auditResultOptions: [
        {
          name: $t("applicationMonitoring.incrementalRraining.notText"),
          id: 0,
        },
        {
          name: $t("applicationMonitoring.incrementalRraining.corret"),
          id: 1,
        },
        {
          name: $t('applicationMonitoring.incrementalRraining.error'),
          id: 2,
        },
      ],
    };
  },
  async created() {
    await this.getOptions();
    this.getListData();
  },
  methods: {
    // 获取下拉
    async getOptions() {
      const data1 = await getCameraListData();
      this.cameraOptions = data1.data;
      const data2 = await getAlgorithmListData();
      this.algorithmOptions = data2.data;
      const data3 = await getTypeListData();
      this.typeOptions = data3.data;
    },
    // 获取增量列表
    async getListData() {
      this.loading = true;
      const data = await getListData(this.params);
      this.tableData = data.data;
      this.total = Number(data.count);
      this.loading = false;
    },
    // 重置
    refreshData() {
      Object.assign(this.params, {
        cameraId: "",
        algorithmId: "",
        auditState: "",
        auditResult: "",
        limit: 10,
        page: 1,
      });
      this.getListData();
    },
    // 告警详情
    clickDetail(item) {
      this.currentId = item.id;
      this.alarmDetailVisible = true;
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getListData();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getListData();
    },
    handleType(type) {
      const obj = this.typeOptions.find((item) => item.id == type);
      return obj.name;
    },
    handleAuditState(auditState) {
      const obj = this.auditStateOptions.find((item) => item.id == auditState);
      return obj.name;
    },
    handleAuditResult(auditResult) {
      const obj = this.auditResultOptions.find(
        (item) => item.id == auditResult
      );
      return obj.name;
    },
  },
};
</script>
<style scoped lang="scss"></style>
