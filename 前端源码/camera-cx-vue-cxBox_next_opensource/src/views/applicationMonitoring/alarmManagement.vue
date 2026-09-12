<template>
  <div
    class="wrapper"
    v-loading="exportLoading"
    :element-loading-text="$t('applicationMonitoring.alarmmanagement.4857c1')"
  >
    <!-- 顶部统计卡片 -->
    <div class="statistics-cards">
      <div class="stat-card stat-card-orange">
        <div class="stat-content">
          <div class="stat-label">{{ $t('applicationMonitoring.alarmmanagement.unprocessed') }}</div>
          <div class="stat-value">{{ statisticsData.unHandleCount || 0 }}</div>
        </div>
        <div class="stat-icon">
          <img src="@/assets/images/alarm/icon1.png" style="width: 36px;"/>
        </div>
      </div>
      <div class="stat-card stat-card-green">
        <div class="stat-content">
          <div class="stat-label">{{ $t('applicationMonitoring.alarmmanagement.processed') }}</div>
          <div class="stat-value">{{ statisticsData.handleCount || 0 }}</div>
        </div>
        <div class="stat-icon">
          <img src="@/assets/images/alarm/icon2.png" style="width: 36px;"/>
        </div>
      </div>
      <div class="stat-card stat-card-gray">
        <div class="stat-content">
          <div class="stat-label">{{ $t('applicationMonitoring.alarmmanagement.closed') }}</div>
          <div class="stat-value">{{ statisticsData.closeCount || 0 }}</div>
        </div>
        <div class="stat-icon">
          <img src="@/assets/images/alarm/icon3.png" style="width: 36px;"/>
        </div>
      </div>
      <div class="stat-card stat-card-blue">
        <div class="stat-content">
          <div class="stat-label">{{ $t('applicationMonitoring.alarmmanagement.todayDetections') }}</div>
          <div class="stat-value">{{ statisticsData.todayCount || 0 }}</div>
        </div>
        <div class="stat-icon">
          <img src="@/assets/images/alarm/icon4.png" style="width: 36px;"/>
        </div>
      </div>
    </div>

    <!-- tab 报警 -->
    <div class="top-tabs">
      <div
        v-for="(items, index) in algorithmOptions"
        :key="index"
        :class="[items.isCheck ? 'tab-check' : 'tab-item']"
        @click="tabClick($event, index)"
      >
        <span class="tab-text">{{ items.name }}</span>
        <span v-if="items.count !== undefined" class="tab-count">{{ items.count }}</span>
      </div>
    </div>

    <!-- 查询条件 -->
    <div >
      <el-row class="search-sty">
        <el-col :span="24">
          <el-form :inline="true" :model="params" class="demo-form-inline">
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
                ></el-option>
              </el-select>
            </el-form-item>
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

            <el-form-item label="">
              <el-select
                :placeholder="$t('applicationMonitoring.alarmmanagement.positiveFalseReport')"
                clearable
                :multiple="true"
                v-model="params.markList"
                style="width: 180px"
              >
                <el-option
                  v-for="(item, index) in positiveFalseReportOptions"
                  :key="index"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>

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
                :disabled="isDisabled"
                :clearable="false"
                :picker-options="pickerOptions"
              >
              </el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="border-radius: 6px !important;" @click="getData()">{{ $t('button.queryText') }}</el-button>
              <el-button @click="refreshData" style="border-radius: 6px !important;">{{ $t('button.resetText', { text: '' }) }}</el-button>
              <el-dropdown trigger="click" @command="handleMoreCommand" style="margin-left: 10px;">
                <el-button size="small" style="border-radius: 6px !important;">
                  <svg t="1766583482793" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1796" width="10" height="10"><path d="M512 298.6496a85.3504 85.3504 0 1 0 0-170.6496 85.3504 85.3504 0 0 0 0 170.6496z" fill="#5A5A68" p-id="1797"></path><path d="M512 512m-85.3504 0a85.3504 85.3504 0 1 0 170.7008 0 85.3504 85.3504 0 1 0-170.7008 0Z" fill="#5A5A68" p-id="1798"></path><path d="M512 896a85.3504 85.3504 0 1 0 0-170.7008 85.3504 85.3504 0 0 0 0 170.7008z" fill="#5A5A68" p-id="1799"></path></svg>
                  <span style='margin-left: 5px;'>{{ $t('applicationMonitoring.alarmmanagement.expandOperation') }}</span>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    command="export_img"
                    v-if="btnData.includes('alarm-export-image')"
                  >
                    <i class="el-icon-download"></i> {{ $t('applicationMonitoring.alarmmanagement.exportAlertImages') }}
                  </el-dropdown-item>
                  <el-dropdown-item
                    command="export_data"
                    v-if="btnData.includes('alarm-export-data')"
                  >
                    <i class="el-icon-download"></i> {{ $t('applicationMonitoring.alarmmanagement.exportAlertData') }}
                  </el-dropdown-item>
                  <el-dropdown-item
                    command="alarm_clear"
                    v-if="btnData.includes('alarm-clear-report')"
                  >
                    <i class="el-icon-setting"></i> {{ $t('applicationMonitoring.alarmmanagement.dataStorageSettings') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <div style="display: flex; justify-content: space-between; align-items: center; margin: 16px 0px">
        <div>
          <div>
            <span class="tip-title">{{ $t('applicationMonitoring.alarmmanagement.detectionResults') }}</span>
            <span class="tip-total">({{ total }})</span>
          </div>
          <div class="tip-txt">
            {{ $t('applicationMonitoring.alarmmanagement.totalRecordsDetected', [total]) }}
          </div>
        </div>
          <div class="operation-btns">
            <div class="view-mode-btns">
              <div class="view-mode-container">
                <div 
                  :class="['view-mode-btn', viewMode == 'large' ? 'active' : '']"
                  @click="handleShowNum('large')"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 2V14" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M2 8H14" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M12.6667 2H3.33333C2.59695 2 2 2.59695 2 3.33333V12.6667C2 13.403 2.59695 14 3.33333 14H12.6667C13.403 14 14 13.403 14 12.6667V3.33333C14 2.59695 13.403 2 12.6667 2Z" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ $t('applicationMonitoring.alarmmanagement.large') }}</span>
                </div>
                <div 
                  :class="['view-mode-btn', viewMode == 'medium' ? 'active' : '']"
                  @click="handleShowNum('medium')"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M12.6667 2H3.33333C2.59695 2 2 2.59695 2 3.33333V12.6667C2 13.403 2.59695 14 3.33333 14H12.6667C13.403 14 14 13.403 14 12.6667V3.33333C14 2.59695 13.403 2 12.6667 2Z" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M2 6H14" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M2 10H14" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M6 2V14" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M10 2V14" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ $t('applicationMonitoring.alarmmanagement.medium') }}</span>
                </div>
                <div 
                  :class="['view-mode-btn', viewMode == 'small' ? 'active' : '']"
                  @click="handleShowNum('small')"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M6 2H2.66667C2.29848 2 2 2.29848 2 2.66667V6C2 6.36819 2.29848 6.66667 2.66667 6.66667H6C6.36819 6.66667 6.66667 6.36819 6.66667 6V2.66667C6.66667 2.29848 6.36819 2 6 2Z" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M13.3333 2H9.99992C9.63173 2 9.33325 2.29848 9.33325 2.66667V6C9.33325 6.36819 9.63173 6.66667 9.99992 6.66667H13.3333C13.7014 6.66667 13.9999 6.36819 13.9999 6V2.66667C13.9999 2.29848 13.7014 2 13.3333 2Z" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M13.3333 9.33325H9.99992C9.63173 9.33325 9.33325 9.63173 9.33325 9.99992V13.3333C9.33325 13.7014 9.63173 13.9999 9.99992 13.9999H13.3333C13.7014 13.9999 13.9999 13.7014 13.9999 13.3333V9.99992C13.9999 9.63173 13.7014 9.33325 13.3333 9.33325Z" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M6 9.33325H2.66667C2.29848 9.33325 2 9.63173 2 9.99992V13.3333C2 13.7014 2.29848 13.9999 2.66667 13.9999H6C6.36819 13.9999 6.66667 13.7014 6.66667 13.3333V9.99992C6.66667 9.63173 6.36819 9.33325 6 9.33325Z" stroke="#4A5565" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ $t('applicationMonitoring.alarmmanagement.small') }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>

    <!-- table -->
    <div>
      <div v-if="tableData.length">
        <div :class="getTableClass()">
          <div v-for="(item, index) in tableData" :key="item.id" class="item-sty">
            <AlarmCard
              :fileUrl="getReportImageUrl(item.id)"
              :dataList="handleParams(item.params)"
              :index="index"
              :alarmData="item"
              :isAlarm="true"
              :params="params"
            >
            </AlarmCard>
          </div>
        </div>
      </div>
      <div class="noData" v-else>
        <el-empty :description="$t('common.noData')"></el-empty>
      </div>
      <div class="pagination">
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

    <!-- 告警数据保存期限设置 -->
    <AlarmClearSetting v-if="alarmClearVisible" @close="handleCloseAlarmClear" />

    <!-- 告警数据采集期限设置 -->
    <AlarmCollectSetting v-if="alarmCollectVisible" @close="handleCloseAlarmCollect" />

    <!-- 下载 -->
    <Download v-if="downloadVisible" :seachObj="params" @close="closeHandle" />
  </div>
</template>

<script>
  import {
    getListData,
    getCameraListData,
    saveclearReportDayConfig,
    listTabs,
    exportAlarm,
    summanyReport,
  } from '@/api/applicationMonitoring/alarmManagement';
  import Cookies from 'js-cookie';

  import { listTree } from '@/api/applicationMonitoring/boxManagement';
  import { getAfterSales } from '@/api/applicationMonitoring/systemManagement';
  import AlarmCard from '@/components/applicationMonitoring/alarmManagement/newCard';
  import AlarmClearSetting from '@/components/applicationMonitoring/alarmManagement/alarmClearSetting';
  import AlarmCollectSetting from '@/components/applicationMonitoring/alarmManagement/alarmCollectSetting';
  import AlarmDetail from '@/components/applicationMonitoring/alarmManagement/alarmDetail';
  import Download from '@/components/applicationMonitoring/alarmManagement/downLoad';
  import store from '@/store';

  export default {
    components: {
      AlarmDetail,
      AlarmCard,
      Download,
      AlarmClearSetting,
      AlarmCollectSetting,
    },
    props: {
      isDisabled: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        depList: [],
        imgRatio: 0.5,
        loading: false,
        tableData: [],
        date: [new Date(), new Date()],
        params: {
          departIds: [],
          cameraId: '',
          algorithmId: '',
          alarmLevelId: '',
          type: '',
          startDate: '',
          endDate: '',
          auditResult: [],
          markList: [],
          limit: 12,
          page: 1,
        },
        total: 0,
        cameraOptions: [],
        algorithmOptions: [],
        VUE_APP_API_BASE_URL,
        clearDayList: [
          {
            id: '30',
            name: this.$t('applicationMonitoring.alarmmanagement.0d5feh'),
          },
          {
            id: '20',
            name: this.$t('applicationMonitoring.alarmmanagement.gmv893'),
          },
          {
            id: '14',
            name: this.$t('applicationMonitoring.alarmmanagement.f320ly'),
          },
          {
            id: '10',
            name: this.$t('applicationMonitoring.alarmmanagement.6f383w'),
          },
          {
            id: '7',
            name: this.$t('applicationMonitoring.alarmmanagement.852fko'),
          },
          {
            id: '5',
            name: this.$t('applicationMonitoring.alarmmanagement.m84bsz'),
          },
          {
            id: '3',
            name: this.$t('applicationMonitoring.alarmmanagement.87kl0z'),
          },
          {
            id: '1',
            name: this.$t('applicationMonitoring.alarmmanagement.65oh86'),
          },
        ],
        clearReportDay: '30',
        oldDay: '30',
        dowloadLoading: false,
        downloadVisible: false,
        btnData: [],
        showNum: 12,
        viewMode: 'medium', // 'large', 'medium', 'small'
        statisticsData: {
          unprocessed: 0,
          processed: 0,
          closed: 0,
          todayDetections: 0,
        },
        websocket: null,
        websocketTimer: null,
        websocketConnected: false,
        websocketReady: false,
        websocketAutoReconnect: true,
        alarmClearVisible: false,
        alarmCollectVisible: false,
        exportLoading: false,
        // 处理状态
        auditResultOptions: [
          { id: '0', name: this.$t('alarmdetail.newdetail.694hl5') },
          { id: '1', name: this.$t('alarmdetail.newdetail.v393b6') },
          { id: '2', name: this.$t('alarmdetail.newdetail.1op9d6') },
          { id: '3', name: this.$t('alarmdetail.newdetail.automaticProcessing') },
        ],
        // 正报/误报
        positiveFalseReportOptions: [
          { id: 0, name: this.$t('applicationMonitoring.alarmmanagement.bePending') },
          { id: 1, name: this.$t('applicationMonitoring.alarmmanagement.positive') },
          { id: 2, name: this.$t('applicationMonitoring.alarmmanagement.False') },
        ],
        // 时间快速选择选项
        pickerOptions: {
          shortcuts: [
            {
              text: $t('flowDsetection.flowSee.finalWeek'),
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                picker.$emit('pick', [start, end]);
              },
            },
            {
              text: $t('flowDsetection.flowSee.finalMonth'),
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                picker.$emit('pick', [start, end]);
              },
            },
            {
              text: $t('flowDsetection.flowSee.finalThreeMonths'),
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                picker.$emit('pick', [start, end]);
              },
            },
          ],
        },
        // 消防火警察状态 事件状态 0-瞬时 1-开始 2-停止 3-事件脉冲 4-事件联动结果更新
        hikStatusOption: [
          { value: 0, label: this.$t('alarmdetail.newdetail.instantaneous') },
          { value: 1, label: this.$t('alarmdetail.newdetail.start') },
          { value: 2, label: this.$t('alarmdetail.newdetail.stop') },
          { value: 3, label: this.$t('alarmdetail.newdetail.eventPulse') },
          { value: 4, label: this.$t('alarmdetail.newdetail.eventLinkageResultUpdate') },
        ],
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
    watch: {
      'params.limit': {
        deep: true,
        handler(val) {
          this.params.page = 1;
          // 根据更新
          if (val === 4) {
            this.viewMode = 'large';
            this.showNum = 4;
          } else if (val === 12) {
            // 保持当前，不自动切换
            this.showNum = 12;
          }
          this.getListData();
        },
      },
    },
    async created() {
      this.getBtn();
      this.getTree();
      this.date = [
        this.$moment(new Date(this.date[0].setHours(0, 0, 0))).format('YYYY-MM-DD HH:mm:ss'),
        this.$moment(new Date(this.date[1].setHours(23, 59, 59))).format('YYYY-MM-DD HH:mm:ss'),
      ];
      this.getDay();
      await this.getOptions();
      // this.getListData();
      // await this.getListTabs();
      this.connectWebsocket();
    },
    beforeDestroy() {
      if (this.websocket) {
        this.websocket.close();
        this.websocket = null;
      }

      if (this.websocketTimer) {
        clearInterval(this.websocketTimer);
        this.websocketTimer = null;
      }
    },
    methods: {
      getBtn() {
        this.btnData = [];
        const menuArr = JSON.parse(sessionStorage.getItem('menuTree'));
        if (!menuArr) return;
        this.getList(menuArr);
      },
      getList(data) {
        let arr = [];
        data.forEach((item) => {
          if (item.path == this.$route.path || item.path == '/dataManagement/alarmManagement') {
            if (item.children.length > 0) {
              arr = item.children.filter((items, ind) => {
                return items.type == 2;
              });
              let newArr = [];
              arr.filter((item, index) => {
                newArr.push(item.auth);
              });
              this.btnData = newArr;
            }
          } else {
            if (item.children.length > 0) {
              this.getList(item.children);
            }
          }
        });
      },
      getData() {
        this.params.page = 1;
        this.getListData();
        this.getListTabs();
      },
      // 获取下拉
      async getOptions() {
        const data1 = await getCameraListData();
        this.cameraOptions = data1.data;
      },
      // 获取有告警的算法
      async getListTabs() {
        this.algorithmOptions = [];
        let formData = new FormData();
        if (this.date && this.date.length > 0) {
          formData.append('startDate', this.date[0]);
          formData.append('endDate', this.date[1]);
        }
        const res = await listTabs(formData);
        let hasCheck = false;
        let totalCount = 0;
        if (res.data.length > 0) {
          res.data.forEach((item) => {
            item.isCheck = this.params.algorithmId == item.id ? true : false;
            hasCheck = item.isCheck;
            // 确保转换为数字类型后再相加
            const count = Number(item.count) || 0;
            totalCount += count;
          });
        }
        this.algorithmOptions = res.data;
        let obj = {
          id: '',
          name: $t('common.allText'),
          isCheck: !hasCheck,
          count: totalCount || this.total || 188,
        };
        this.algorithmOptions.unshift(obj);
      },
      // 选中算法
      tabClick(e, index) {
        // 使用确保更新在下一帧
        this.$nextTick(() => {
          this.algorithmOptions.forEach((item, ind) => {
            item.isCheck = index === ind;
            if (index === ind) {
              this.params.algorithmId = item.id;
            }
          });
          this.params.page = 1;
          this.getListData();
        });
      },
      // 获取告警列表
      async getListData() {
        this.loading = true;
        if (this.date && this.date.length > 0) {
          this.params.startDate = this.date[0];
          this.params.endDate = this.date[1];
        }
        console.log('params___', this.params);
        let obj = {
          ...this.params,
          markList: this.params.markList,
        };
        console.log('obj:', obj);
        obj.auditResult = obj.auditResult.length ? obj.auditResult.join(',') : '';
        // obj.markList = obj.markList.length ? obj.markList.join(',') : '';

        let arr = [];
        if (obj.departIds && obj.departIds.length > 0) {
          obj.departIds.forEach((item, ind) => {
            let len = item.length - 1;
            arr.push(item[len]);
          });
        }
        obj.departIds = arr.length > 0 ? arr.join(',') : '';
        console.log('obj2:', obj);
        // obj.markList = [0, 1];
        const data = await getListData(obj);
        console.log('data', data);
        this.tableData = data.data.map((item) => {
          if (item?.srcIndex || item?.srcName) {
            const status = this.hikStatusOption.filter((opt) => {
              return opt.value === item.hikStatus;
            });

            const statusText = status.length > 0 ? status[0].label : '';
            item.cameraName = item.srcIndex
              ? `${item.srcName}(${item.srcIndex})${statusText}`
              : `${item.srcName}${statusText}`;
          }
          return item;
        });

        this.total = Number(data.count);
        this.loading = false;
        // 更新统计数据
        this.getStatisticsData(obj);
      },

      // 改变时间
      async dateChange() {
        await this.getListTabs();
        await this.getListData();
      },
      // 重置
      refreshData() {
        let dateList = [new Date(), new Date()];
        this.date = [
          this.$moment(new Date(dateList[0].setHours(0, 0, 0))).format('YYYY-MM-DD HH:mm:ss'),
          this.$moment(new Date(dateList[1].setHours(23, 59, 59))).format('YYYY-MM-DD HH:mm:ss'),
        ];
        let len = this.params.limit;
        Object.assign(this.params, {
          cameraId: '',
          algorithmId: '',
          alarmLevelId: '',
          auditResult: [],
          markList: [],
          type: '',
          limit: len,
          page: 1,
        });
        this.getListData();
        this.getListTabs();
      },
      /** 创建链接 */
      connectWebsocket() {
        //
        this.getData();

        //
        if (typeof WebSocket === 'undefined') {
          console.log('Your browser does not support');
          return;
        } else {
          let cookie = Cookies.get('X-Token') || this.$route.query.token;
          let url = `${VUE_APP_WS_BASE_URL}/report/${cookie}`;
          // 打开一个
          this.websocket = new WebSocket(url);
          // 建立连接
          this.websocket.onopen = () => {
            console.log('Connection：');
            this.websocketConnected = true;
            this.websocketReady = true;
            this.websocketTimer = setInterval(this.handleSocketReconnected, 15000);
          };
          // 客户端接收服务端返回的数据
          this.websocket.onmessage = (evt) => {
            const data = JSON.parse(evt.data);
            if (data.type == 'REPORT_SHOW') {
              this.getListData();
            }
          };
          // 发生错误时
          this.websocket.onerror = (evt) => {
            console.log('websocketError：', evt);
            this.websocketConnected = false;
          };
          // 关闭连接
          this.websocket.onclose = (evt) => {
            console.log('websocketClose：', evt);
            this.websocketConnected = false;
          };
        }
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
      handleParams(params) {
        try {
          return JSON.parse(params);
        } catch (err) {
          return [];
        }
      },
      // 切换定时任务清除告警信息天数
      dayChange() {
        let str = '';
        this.clearDayList.forEach((item) => {
          if (this.clearReportDay == item.id) {
            str = item.name;
          }
        });
        this.$confirm(
          `${this.$t('applicationMonitoring.alarmmanagement.4857w2', [
            str,
          ])}<div style="color: red;">${this.$t('applicationMonitoring.alarmmanagement.4857w3', [
            str,
          ])}</div>`,
          $t('common.prompt'),
          {
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
            dangerouslyUseHTMLString: true,
            type: 'warning',
          },
        )
          .then(() => {
            saveclearReportDayConfig({
              clearReportDay: this.clearReportDay,
            }).then((res) => {
              this.$message({
                message: $t('button.saveText', { text: $t('common.success') }),
                type: 'success',
                duration: 500,
              });
              this.oldDay = this.clearReportDay;
            });
          })
          .catch(() => {
            this.clearReportDay = this.oldDay;
          });
      },
      //获取清除告警信息天数
      async getDay() {
        const res = await getAfterSales({ tag: 'clearReportDay' });
        this.clearReportDay = res.data ? res.data : '30';
        this.oldDay = res.data ? res.data : '30';
      },
      handleCommand(command) {
        if (command == 'img') {
          this.dowloadData();
        } else {
          this.downloadVisible = true;
        }
      },
      closeHandle() {
        this.downloadVisible = false;
      },
      // 导出告警图片
      dowloadData() {
        this.$confirm($t('applicationMonitoring.alarmmanagement.3statx'),
          $t('common.prompt'),
          {
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
            dangerouslyUseHTMLString: true,
            type: 'warning',
          },
        )
          .then(() => {
            this.executeDownload();
          });
      },
      executeDownload() {
        //this.downloadVisible = true;
        this.dowloadLoading = true; 
        let Obj = {
          startDate: this.date[0],
          endDate: this.date[1],
          algorithmId: this.params.algorithmId,
          cameraId: this.params.cameraId,
          markList: this.params.markList,
        };
        this.exportLoading = true;
        exportAlarm(Obj)
          .then((res) => {
            this.exportLoading = false;
            this.dowloadLoading = false;
            // 创建临时下载链接
            const blob = new Blob([res.data], { type: 'application/zip' });
            const url = URL.createObjectURL(blob);

            // 创建隐藏的 a 标签模拟下载
            const link = document.createElement('a');
            link.href = url;
            link.download = this.$t('applicationMonitoring.alarmmanagement.02e7hq', [
              new Date().getTime(),
            ]);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          })
          .catch((res) => {
            this.exportLoading = false;
            this.dowloadLoading = false;
          });
      },
      // 获取部门树
      getTree() {
        listTree().then((res) => {
          if (res.data && res.data.length > 0) {
            this.depList = this.getTreeData(res.data);
          }
        });
      },
      getTreeData(data) {
        data.forEach((item) => {
          if (item.children.length < 1) {
            item.children = undefined;
          } else {
            this.getTreeData(item.children);
          }
        });
        return data;
      },
      // 展示数量处理，大图(4)、中图(12-3列)、小图(12-4列)
      handleShowNum(mode) {
        if (mode === 4 || mode === 'large') {
          this.params.limit = 4;
          this.showNum = 4;
          this.viewMode = 'large';
        } else if (mode === 'medium') {
          this.params.limit = 12;
          this.showNum = 12;
          this.viewMode = 'medium';
        } else if (mode === 'small') {
          this.params.limit = 12;
          this.showNum = 12;
          this.viewMode = 'small';
        }
        this.params.page = 1;
        this.getListData();
      },
      // 获取统计数据
      async getStatisticsData(params) {
        // 这里可以调用获取统计数据
        let obj = {
          mark:params.markList,
          cameraId:params.cameraId,
          auditResult: params.auditResult,
          startTime:params.startDate,
          endTime:params.endDate
        }
        const res = await summanyReport(obj);
        this.statisticsData = res.data;
      },
      // 处理断线重连
      handleSocketReconnected() {
        if (this.websocketReady) {
          if (!this.websocketConnected && this.websocketAutoReconnect) {
            this.websocketReady = false;
            this.connectWebsocket();
          } else if (this.websocketConnected) {
            this.websocket.send('ping');
          }
        }
      },
      // 关闭告警存储期限弹窗
      handleCloseAlarmClear() {
        this.alarmClearVisible = false;
      },
      // 关闭告警收集收集弹窗
      handleCloseAlarmCollect() {
        this.alarmCollectVisible = false;
      },
      /** 右上角更多功能，下拉列表 */
      handleMoreCommand(command) {
        // 打开告警存储期限弹窗
        if (command == 'alarm_clear') {
          this.alarmClearVisible = true;
        }
        // 打开告警数据收集弹窗
        if (command == 'alarm_collect') {
          this.alarmCollectVisible = true;
        }
        // 导出告警数据
        if (command == 'export_data') {
          this.downloadVisible = true;
        }
        // 导出告警原图
        if (command == 'export_img') {
          this.dowloadData();
        }
      },
      // 获取报警图片
      getReportImageUrl(id) {
        const token = Cookies.get('X-Token');
        return `${VUE_APP_API_BASE_URL}/report/streamThumb?id=${id}&X-Token=${token}&t=${new Date().getTime()}`;
      },
      // 获取表格布局类名
      getTableClass() {
        if (this.viewMode === 'large') {
          return 'table-sty4';
        } else if (this.viewMode === 'medium') {
          return 'table-sty12';
        } else if (this.viewMode === 'small') {
          return 'table-sty12-small';
        }
        return 'table-sty12'; // 默认中图
      },
      // 格式化数字，去掉前导零
      formatCount(count) {
        if (count === undefined || count === null) return '';
        // 转换为数字再转回字符串，自动去掉前导零
        const num = Number(count);
        return isNaN(num) ? count : num.toString();
      },
    },
  };
</script>

<style lang="scss" scoped>
  .wrapper {
    // background: #fff;
    // border-radius: 8px;
    position: relative;
    padding-bottom: 10px;
  }
  .top-tabs {
    border-radius: 14px;
    border: 1px solid #F3F4F6;
    background: #FFF;
    box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
    display: flex;
    // width: 100%;
    overflow-x: auto;
    .tab-item {
      flex-shrink: 0;
      padding: 0px 20px;
      border-left: 1px solid #c2c6cd;
      font-size: 16px;
      font-weight: bold;
      line-height: 20px;
      color: #6c727d;
      cursor: pointer;
    }
    .tab-item:first-child {
      border-left: none;
    }
    .tab-check {
      flex-shrink: 0;
      // padding: 10px 20px 10px 10px;
      font-size: 16px;
      // line-height: 20px;
      font-weight: bold;
      color: #000;
      background: #fff;
      border-top-left-radius: 8px;
      border-top-right-radius: 8px;
    }
    .tab-check + .tab-item {
      border-left: none;
    }
    .tab-name {
      border-left: 2px solid #E53935;
      padding-left: 8px;
    }
  }

  .noData {
    line-height: 120px;
    text-align: center;
  }
  // 统计卡片样式
  .statistics-cards {
    display: flex;
    gap: 16px;
    // padding: 20px;
    // background: #fff;
    // border-radius: 8px;
    margin-bottom: 16px;
    
    .stat-card {
      flex: 1;
      display: flex;
      align-items: center;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      
      .stat-content {
        flex: 1;
        
        .stat-label {
          font-size: 14px;
          color: #4A5565;
          margin-bottom: 8px;
        }
        
        .stat-value {
          font-size: 24px;
          font-weight: bold;
          line-height: 1;
        }
      }
      
      &.stat-card-orange {
        border-radius: 14px;
        border: 1px solid #F3F4F6;
        background: linear-gradient(135deg, #FFF7ED 0%, #FFEDD4 100%);
        box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
        .stat-content {
          .stat-value {
            color: #F54900;
          }
        }
      }
      
      &.stat-card-green {
        border-radius: 14px;
        border: 1px solid #F3F4F6;
        background: linear-gradient(135deg, #F0FDF4 0%, #DCFCE7 100%);
        box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
        
        .stat-content {
          .stat-value {
            color: #00A63E;
          }
        }
      }
      
      &.stat-card-gray {
        border-radius: 14px;
        border: 1px solid #F3F4F6;
        background: linear-gradient(135deg, #F9FAFB 0%, #F3F4F6 100%);
        box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
        .stat-content {
          .stat-value {
            color: #4A5565;
          }
        }
      }
      
      &.stat-card-blue {
        border-radius: 14px;
        border: 1px solid #F3F4F6;
        background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%);
        box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
        .stat-content {
          .stat-value {
            color: #E53935;
          }
        }
      }
    }
  }

  // Tab样式调整
  .top-tabs {
    border-radius: 8px;
    padding: 8px;
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    overflow-x: auto;
    
    .tab-item {
      flex-shrink: 0;
      padding: 8px 16px;
      border-radius: 4px;
      font-size: 14px;
      color: #606266;
      cursor: pointer;
      transition: background-color 0.2s ease, color 0.2s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      background: transparent;
      line-height: 1.5;
      height: 36px;
      min-height: 36px;
      box-sizing: border-box;
      vertical-align: middle;
      will-change: auto;
      
      &:hover {
        background: rgba(64, 158, 255, 0.1);
        color: #E53935;
      }
      
      .tab-text {
        font-weight: 500;
        line-height: 1.5;
      }
      
      .tab-count {
        color: #909399;
        font-size: 12px;
        line-height: 1.5;
      }
    }
    
    .tab-check {
      flex-shrink: 0;
      padding: 8px 16px;
      border-radius: 4px;
      font-size: 14px;
      font-weight: 500;
      color: #fff;
      background: #E53935;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      line-height: 1.5;
      height: 36px;
      min-height: 36px;
      box-sizing: border-box;
      vertical-align: middle;
      transition: none;
      will-change: auto;
      
      .tab-text {
        font-weight: 500;
        line-height: 1.5;
      }
      
      .tab-count {
        color: rgba(255, 255, 255, 0.9);
        font-size: 12px;
        line-height: 1.5;
      }
    }
  }

  // 布局样式
  .table-sty4 {
    display: grid;
    grid-template-columns: repeat(2, calc(50% - 10px));
    gap: 20px;
  }
  
  .table-sty12 {
    display: grid;
    grid-template-columns: repeat(3, calc(33.333% - 14px));
    gap: 20px;
  }
  
  .table-sty12-small {
    display: grid;
    grid-template-columns: repeat(4, calc(25% - 15px));
    gap: 20px;
  }

  .pagination {
    text-align: right;
    margin-top: 10px;
  }
  .tip-title{
    color: #1E2939;
    font-size: 16px;
    font-weight: 400;
    line-height: 28px;
  }
  .tip-total{
    color: #E53935;
    font-size: 16px;
    font-style: normal;
    font-weight: 400;
    line-height: 28px; 
    margin-left: 5px;
  }
  .tip-txt{
    color: #6A7282;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 20px; 
    margin-top: 10px;
  }
  .operation-btns {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    
    .view-mode-btns {
      display: flex;
      align-items: center;
      
      .view-mode-container {
        display: flex;
        align-items: center;
        gap: 4;
        border-radius: 10px;
        background: #F3F4F6;
        border-radius: 6px;
        padding: 4px;
        
        .view-mode-btn {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;
          padding: 8px 16px;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.2s ease;
          background: transparent;
          color: #4A5565;
          font-size: 14px;
          
          .view-icon {
            width: 16px;
            height: 16px;
            color: #4A5565;
            transition: color 0.2s ease;
          }
          
          span {
            font-size: 14px;
            line-height: 1;
          }
          
          &.active {
            border-radius: 8px;
            background: linear-gradient(90deg, #2B7FFF 0%, #E53935 100%);
            box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
            color: #fff;
            
            .view-icon {
              color: #fff;
            }
            
            span {
              color: #fff;
            }
            
            svg path {
              stroke: #fff;
            }
          }
          
          svg path {
            stroke: #4A5565;
            transition: stroke 0.2s ease;
          }
        }
      }
    }
    
    .expand-btn-wrapper {
      display: flex;
      align-items: center;
    }
  }
  .search-sty{
    border-radius: 14px;
    border: 1px solid #F3F4F6;
    background: #FFF;
    box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
    padding: 20px 20px 0px 20px;
  }
</style>
