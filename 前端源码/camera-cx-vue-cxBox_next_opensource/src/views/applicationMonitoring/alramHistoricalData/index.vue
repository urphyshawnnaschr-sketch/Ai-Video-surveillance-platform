<template>
  <div class="wrapper">
    <!-- 查询条件 -->
    <div>
      <el-row>
        <el-col :span="24">
          <el-form :inline="true" :model="params" class="demo-form-inline">
            <!-- 处理状态 -->
            <el-form-item label="">
              <el-select
                :placeholder="$t('applicationMonitoring.alarmmanagement.8e114y')"
                clearable
                :multiple="true"
                v-model="params.auditResult"
                style="width: 180px"
              >
                <el-option
                  v-for="(item, index) in auditResultOptions"
                  :key="index"
                  :label="item.name"
                  :value="item.id"
                >
                </el-option>
              </el-select>
            </el-form-item>

            <!-- 摄像头 -->
            <el-form-item label="">
              <el-select
                :placeholder="$t('common.camera')"
                clearable
                filterable
                v-model="params.cameraId"
                style="width: 180px"
              >
                <el-option
                  v-for="(item, index) in cameraOptions"
                  :key="index"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>

            <!-- 时间范围 -->
            <el-form-item label="">
              <el-date-picker
                v-model="date"
                :default-time="['00:00:00', '23:59:59']"
                type="datetimerange"
                :range-separator="$t('common.pickerDate.to')"
                :start-placeholder="$t('common.pickerDate.startDate')"
                :end-placeholder="$t('common.pickerDate.endDate')"
                value-format="yyyy-MM-dd HH:mm:ss"
                format="yyyy-MM-dd HH:mm:ss"
                style="width: 340px"
                @change="dateChange"
                :clearable="false"
                :picker-options="pickerOptions"
              >
              </el-date-picker>
            </el-form-item>

            <!-- 查询/重置按钮 -->
            <el-form-item>
              <el-button type="primary" @click="getData()">{{ $t('button.queryText') }}</el-button>
              <el-button @click="refreshData">{{ $t('button.resetText', { text: '' }) }}</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </div>

    <!-- 表格组件 -->
    <div style="margin: 0 10px 10px 10px">
      <!-- 加载状态 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
        :empty-text="$t('common.noData')"
      >
        <!-- 发生时间 -->
        <el-table-column
          :label="$t('applicationMonitoring.alarmmanagement.happenTime')"
          align="center"
          prop="createdStr"
          width="200"
        >
          <template #default="scope">
            {{ scope.row.createdStr || '-' }}
          </template>
        </el-table-column>

        <!-- 算法名称 -->
        <el-table-column
          :label="$t('applicationMonitoring.alarmmanagement.algorithmName')"
          align="center"
          prop="algorithmName"
          width="140"
        >
          <template #default="scope">
            {{ scope.row.algorithmName || '-' }}
          </template>
        </el-table-column>

        <!-- 处理状态 -->
        <el-table-column
          :label="$t('applicationMonitoring.alarmmanagement.8e114y')"
          align="center"
          prop="auditResult"
          width="160"
        >
          <template #default="scope">
            <span v-if="scope.row.auditResult == '0'">{{
              $t('alarmdetail.newdetail.694hl5')
            }}</span>
            <span v-else-if="scope.row.auditResult == '1'">{{
              $t('alarmdetail.newdetail.v393b6')
            }}</span>
            <span v-else-if="scope.row.auditResult == '2'">{{
              $t('alarmdetail.newdetail.1op9d6')
            }}</span>
            <span v-else-if="scope.row.auditResult == '3'">{{
              $t('alarmdetail.newdetail.automaticProcessing')
            }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <!-- 处理时间 -->
        <el-table-column
          :label="$t('applicationMonitoring.alarmmanagement.handleTime')"
          align="center"
          prop="auditTimeLen"
          width="140"
        >
          <template #default="scope">
            {{ scope.row.auditTimeLen || '-' }}
          </template>
        </el-table-column>

        <!-- 类型 -->
        <el-table-column
          :label="$t('applicationMonitoring.alarmmanagement.alarmType')"
          align="center"
          prop="typeName"
          width="140"
        >
          <template #default="scope">
            {{ scope.row.typeName || '-' }}
          </template>
        </el-table-column>
        <!-- 摄像头 -->
        <el-table-column :label="$t('common.camera')" align="left" prop="cameraName">
          <template #default="scope">
            {{ scope.row.cameraName || '-' }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          background
          :current-page="params.page"
          :page-size="params.limit"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
  import {
    getListData,
    getCameraListData,
    listTabs,
  } from '@/api/applicationMonitoring/alarmManagement';
  import store from '@/store';

  export default {
    data() {
      return {
        loading: false,
        tableData: [], // 表格数据源
        date: [
          // 默认时间范围：当天
          this.$moment(new Date().setHours(0, 0, 0)).format('YYYY-MM-DD HH:mm:ss'),
          this.$moment(new Date().setHours(23, 59, 59)).format('YYYY-MM-DD HH:mm:ss'),
        ],
        // 查询参数
        params: {
          cameraId: '', // 摄像头ID
          auditResult: [], // 处理状态
          startDate: '', // 开始时间
          endDate: '', // 结束时间
          limit: 10, // 每页条数
          page: 1, // 当前页码
          algorithmId: '', // 算法ID（保留用于接口兼容）
          isHistory: 1, //不限制时间
        },
        total: 0, // 总条数
        cameraOptions: [], // 摄像头下拉选项
        algorithmOptions: [], // 算法下拉选项（保留用于接口兼容）
        VUE_APP_API_BASE_URL: process.env.VUE_APP_API_BASE_URL,
        // 处理状态选项
        auditResultOptions: [
          { id: '0', name: this.$t('alarmdetail.newdetail.694hl5') },
          { id: '1', name: this.$t('alarmdetail.newdetail.v393b6') },
          { id: '2', name: this.$t('alarmdetail.newdetail.1op9d6') },
          { id: '3', name: this.$t('alarmdetail.newdetail.automaticProcessing') },
        ],
        pickerOptions: {
          shortcuts: [
            {
              text: this.$t('flowDsetection.flowSee.finalWeek'),
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                picker.$emit('pick', [start, end]);
              },
            },
            {
              text: this.$t('flowDsetection.flowSee.finalMonth'),
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                picker.$emit('pick', [start, end]);
              },
            },
            {
              text: this.$t('flowDsetection.flowSee.finalThreeMonths'),
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                picker.$emit('pick', [start, end]);
              },
            },
          ],
        },
      };
    },
    computed: {
      isEnglish() {
        const getLanguage = () => {
          const langMap = {
            'zh-CN': 'zh-CN',
            en: 'en-US',
          };
          return langMap[store.state.locale] || 'zh-CN';
        };
        return getLanguage().startsWith('en');
      },
    },
    async created() {
      await this.getOptions();
      await this.getListTabs();
      this.getData();
    },
    methods: {
      async getOptions() {
        const data1 = await getCameraListData();
        this.cameraOptions = data1.data || [];
      },
      // 获取算法列表
      async getListTabs() {
        const formData = new FormData();
        if (this.date && this.date.length > 0) {
          formData.append('startDate', this.date[0]);
          formData.append('endDate', this.date[1]);
        }
        const res = await listTabs(formData);
        this.algorithmOptions = res.data || [];
      },
      // 查询按钮：重置页码并获取数据
      getData() {
        this.params.page = 1;
        this.getListData();
      },
      // 获取表格数据
      async getListData() {
        this.loading = true;
        // 处理时间参数
        if (this.date && this.date.length > 0) {
          this.params.startDate = this.date[0];
          this.params.endDate = this.date[1];
        }
        // 处理多值参数（转为逗号分隔字符串）
        const requestParams = {
          ...this.params,
          auditResult: this.params.auditResult.length ? this.params.auditResult.join(',') : '',
          departIds: '',
        };

        try {
          const data = await getListData(requestParams);
          this.tableData = data.data || [];
          this.total = Number(data.count) || 0; // 总条数
        } catch (error) {
          console.error('Failed to get：', error);
          this.tableData = [];
          this.total = 0;
        } finally {
          this.loading = false;
        }
      },
      // 时间范围变更：重新获取数据
      async dateChange() {
        await this.getListTabs();
        await this.getListData();
      },
      // 重置查询条件
      refreshData() {
        // 重置时间为当天
        const today = new Date();
        this.date = [
          this.$moment(today.setHours(0, 0, 0)).format('YYYY-MM-DD HH:mm:ss'),
          this.$moment(today.setHours(23, 59, 59)).format('YYYY-MM-DD HH:mm:ss'),
        ];
        // 重置参数
        const pageSize = this.params.limit;
        Object.assign(this.params, {
          cameraId: '',
          auditResult: [],
          algorithmId: '',
          limit: pageSize,
          page: 1,
        });
        // 重新获取数据
        this.getListData();
        this.getListTabs();
      },
      // 分页：页码变更
      handleCurrentChange(val) {
        this.params.page = val;
        this.getListData();
      },
      // 分页：每页条数变更
      handleSizeChange(val) {
        this.params.limit = val;
        this.params.page = 1;
        this.getListData();
      },
    },
  };
</script>

<style lang="scss" scoped>
  .wrapper {
    background: #fff;
    border-radius: 8px;
    position: relative;
    padding-bottom: 10px;
    padding: 0;
  }

  /* 分页样式 */
  .pagination {
    text-align: right;
    margin-top: 15px;
  }

  /* 表单间距优化 */
  .demo-form-inline {
    padding: 4px;
    display: flex;
    align-items: center;
    gap: 16px;
    flex-wrap: wrap;
    padding-bottom: 10px;
  }
</style>
