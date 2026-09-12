<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('alarmmanagement.alarmcollectdata.c88s05')"
    :visible.sync="dialogVisible"
    width="90%"
    :append-to-body="true"
    @closed="closed"
    class="my-cust-class"
  >
    <div>
      <!-- 查询条件 -->
      <div style="margin: 15px 10px 0px 10px">
        <el-row>
          <el-col :span="18">
            <el-form :inline="true" :model="params" class="demo-form-inline">
              <el-form-item :label="$t('common.algorithmName')">
                <el-select
                  :placeholder="$t('common.chooseText')"
                  v-model="params.algorithmId"
                  clearable
                  style="width: 150px"
                >
                  <el-option
                    v-for="(item, index) in algoList"
                    :key="index"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('common.caBelongingOrganization')">
                <el-cascader
                  v-model="params.departIds"
                  :options="depList"
                  :props="{ value: 'id', label: 'name', multiple: true }"
                  collapse-tags
                  clearable
                  style="width: 150px"
                >
                </el-cascader>
              </el-form-item>
              <el-form-item :label="$t('common.camera')">
                <el-select
                  :placeholder="$t('common.camera')"
                  clearable
                  filterable
                  v-model="params.cameraId"
                  style="width: 150px"
                >
                  <el-option
                    v-for="(item, index) in cameraOptions"
                    :key="index"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('flowsee.index.fgf4dc')">
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
                  @change="dateChange"
                  :disabled="isDisabled"
                  :clearable="false"
                >
                </el-date-picker>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="getData()">{{
                  $t('button.queryText')
                }}</el-button>
                <el-button @click="refreshData">{{
                  $t('button.resetText', { text: '' })
                }}</el-button>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="6">
            <div
              style="display: flex; flex-direction: row; justify-content: end; align-items: center"
            >
              <template v-if="checkedList.length == 0">
                <el-button
                  type="primary"
                  icon="el-icon-download"
                  @click="handleExport"
                  :loading="exportAllLoading"
                  >{{ exportAllText }}</el-button
                >
              </template>
              <template v-if="checkedList.length > 0">
                <div
                  style="
                    margin: 0 10px;
                    border: 1px solid #dcdfe6;
                    padding: 6px 10px;
                    padding-bottom: -2px;
                    font-size: 12px;
                  "
                >
                  <el-checkbox
                    v-model="isAllChecked"
                    @change="handleAllCheck"
                    :indeterminate="halfCheck"
                  >
                    <span style="font-size: 12px"
                      >{{ $t('alarmmanagement.alarmcollectdata.r8kc6i')
                      }}{{ checkedPageTotal }})</span
                    >
                  </el-checkbox>
                </div>
                <el-button type="primary" @click="handleExport" :loading="exportAllLoading"
                  >{{ $t('alarmmanagement.alarmcollectdata.x2206g') }}{{ checkedTotal
                  }}{{ $t('alarmmanagement.alarmcollectdata.ex53gn')
                  }}<span v-if="exportAllLoading">{{
                    $t('alarmmanagement.alarmcollectdata.28xble')
                  }}</span></el-button
                >
                <el-button @click="handleAllUncheck">{{
                  $t('alarmmanagement.alarmcollectdata.6h9588')
                }}</el-button>
              </template>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- table -->
      <div style="margin: 0 10px 0px 10px">
        <div v-if="tableData.length">
          <div :class="'table-sty' + params.limit">
            <div v-for="(item, index) in tableData" :key="item.id" class="item-sty">
              <AlarmCollectCard
                :fileUrl="VUE_APP_API_BASE_URL + `/report/streamThumb?id=${item.id}`"
                :originalUrl="$common.handleStream(item.id)"
                :dataList="handleParams(item.params)"
                :index="index"
                :alarmData="item"
                :isAlarm="true"
                :initialChecked="getCheckboxCheckedValue(item.id)"
                @checkboxChange="handleCheckboxChange"
              >
              </AlarmCollectCard>
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
    </div>
  </el-dialog>
</template>

<script>
  import {
    getListData,
    getCameraListData,
    listTabs,
    exportCollectData,
  } from '@/api/applicationMonitoring/alarmManagement';
  import { getListData as getAlgoListData } from '@/api/applicationMonitoring/algorithmManagement';
  import { listTree } from '@/api/applicationMonitoring/boxManagement';
  import AlarmDetail from '@/components/applicationMonitoring/alarmManagement/alarmDetail';
  import AlarmCollectCard from '@/components/applicationMonitoring/alarmManagement/AlarmCollectCard';

  export default {
    components: {
      AlarmDetail,
      AlarmCollectCard,
    },
    props: {
      isDisabled: {
        type: Boolean,
        default: false,
      },
      currentAlgorithmId: {
        type: String,
        default: '',
      },
    },
    data() {
      return {
        dialogVisible: true,
        isAllChecked: false,
        checkedList: [],
        checkedTotal: 0,
        checkedPageTotal: 0,
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
          limit: 12,
          page: 1,
          display: 8,
        },
        total: 0,
        cameraOptions: [],
        algorithmOptions: [],
        VUE_APP_API_BASE_URL,
        dowloadLoading: false,
        downloadVisible: false,
        btnData: [],
        algoList: [],
        halfCheck: true,
        exportAllLoading: false,
        exportAllText: this.$t('alarmmanagement.alarmcollectdata.2m362k'),
      };
    },
    watch: {
      'params.limit': {
        deep: true,
        handler(val) {
          this.params.page = 1;
          this.showNum = this.params.limit;
          this.getListData();
        },
      },
      checkedList: {
        handler() {
          this.checkedTotal = this.checkedList.length;
          this.refreshCurrentPageTotal();
        },
      },
    },
    async created() {
      this.params.algorithmId = this.currentAlgorithmId;
      this.getBtn();
      this.getTree();
      this.date = [
        this.$moment(new Date(this.date[0].setHours(0, 0, 0))).format('YYYY-MM-DD HH:mm:ss'),
        this.$moment(new Date(this.date[1].setHours(23, 59, 59))).format('YYYY-MM-DD HH:mm:ss'),
      ];
      this.fetchAlgoList();
      await this.getOptions();
      this.getListData();
      await this.getListTabs();
    },
    beforeDestroy() {},
    methods: {
      closed() {
        this.$emit('close');
      },
      getBtn() {
        this.btnData = [];
        const menuArr = JSON.parse(sessionStorage.getItem('menuTree'));
        let newArr = [];
        this.getList(menuArr).filter((item, index) => {
          newArr.push(item.auth);
        });
        this.btnData = newArr;
      },
      getList(data) {
        let arr = [];
        data.forEach((item) => {
          if (item.path == this.$route.path) {
            arr = item.children.filter((items, ind) => {
              return items.type == 2;
            });
          } else {
            this.getList(item.children);
          }
        });
        return arr;
      },
      getData() {
        //this.params.page = 1;
        this.getListData();
        //this.getListTabs();
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
        if (res.data.length > 0) {
          res.data.forEach((item) => {
            item.isCheck = this.params.algorithmId == item.id ? true : false;
            hasCheck = item.isCheck;
          });
        }
        this.algorithmOptions = res.data;
        let obj = {
          id: '',
          name: $t('common.allText'),
          isCheck: !hasCheck,
        };
        this.algorithmOptions.unshift(obj);
      },
      // 获取告警列表
      async getListData() {
        this.loading = true;
        if (this.date && this.date.length > 0) {
          this.params.startDate = this.date[0];
          this.params.endDate = this.date[1];
        }
        let obj = {
          ...this.params,
        };
        let arr = [];
        if (obj.departIds && obj.departIds.length > 0) {
          obj.departIds.forEach((item, ind) => {
            let len = item.length - 1;
            arr.push(item[len]);
          });
        }
        obj.departIds = arr.length > 0 ? arr.join(',') : '';
        const data = await getListData(obj);
        this.tableData = data.data;
        this.total = Number(data.count);
        this.loading = false;

        // 刷新当前页选中数量
        this.refreshCurrentPageTotal();
      },
      // 改变时间
      async dateChange() {
        //await this.getListTabs();
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
          type: '',
          limit: len,
          page: 1,
        });
        this.getListData();
        //this.getListTabs();
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
      closeHandle() {
        this.downloadVisible = false;
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
      // 关闭告警收集收集弹窗
      handleCloseAlarmCollect() {
        this.alarmCollectVisible = false;
      },
      // 选中
      handleCheckboxChange(data) {
        if (data.checked) {
          this.checkedList.push(data.id);
        } else {
          let index = this.checkedList.indexOf(data.id);
          if (index !== -1) {
            this.checkedList.splice(index, 1);
          }
        }
      },
      // 返回是否选中
      getCheckboxCheckedValue(cid) {
        return this.checkedList.indexOf(cid) >= 0;
      },
      // 本页面全选
      handleAllCheck(checked) {
        if (checked) {
          this.tableData.forEach((item) => {
            if (this.checkedList.indexOf(item.id) == -1) {
              this.checkedList.push(item.id);
            }
          });
        } else {
          this.tableData.forEach((item) => {
            let index = this.checkedList.indexOf(item.id);
            if (index !== -1) {
              this.checkedList.splice(index, 1);
            }
          });
        }

        this.tableData = [];
        this.getData();
      },
      // 取消全部
      handleAllUncheck() {
        this.checkedList = [];
        this.tableData = [];
        this.indeterminate = false;
        this.getData();
      },
      // 刷新当前页的选中数量
      refreshCurrentPageTotal() {
        this.checkedPageTotal = 0;
        this.tableData.forEach((item) => {
          if (this.checkedList.indexOf(item.id) >= 0) {
            this.checkedPageTotal = this.checkedPageTotal + 1;
          }
        });

        if (this.checkedPageTotal == 0) {
          this.isAllChecked = false;
        }

        if (this.checkedPageTotal == this.tableData.length) {
          this.isAllChecked = true;
        } else {
          this.isAllChecked = false;
        }

        if (this.checkedPageTotal > 0 && this.checkedPageTotal < this.tableData.length) {
          this.halfCheck = true;
        } else {
          this.halfCheck = false;
        }
      },
      // 获取算法列表
      fetchAlgoList() {
        getAlgoListData().then((res) => {
          this.algoList = res.data;
        });
      },
      // 导出数据
      handleExport() {
        if (this.date && this.date.length > 0) {
          this.params.startDate = this.date[0];
          this.params.endDate = this.date[1];
        }
        var deIds = [];
        if (this.params.departIds && this.params.departIds.length > 0) {
          this.params.departIds.forEach((item) => {
            if (item && item.length > 0) {
              deIds.push(item[item.length - 1]);
            }
          });
        }

        let data = {
          cameraId: this.params.cameraId,
          algorithmId: this.params.algorithmId,
          departIds: deIds,
          startTime: this.params.startDate,
          endTime: this.params.endDate,
          reportIds: this.checkedList,
        };
        this.exportAllLoading = true;
        this.exportAllText = this.$t('alarmmanagement.alarmcollectdata.qb6txs');

        let algoName = this.$t('alarmmanagement.alarmcollectdata.2b5528');
        if (this.params.algorithmId) {
          this.algorithmOptions.forEach((item) => {
            if (item.id == this.params.algorithmId) {
              algoName = item.name;
            }
          });
        }
        // 导出文件名称
        const filename =
          algoName +
          '_' +
          this.formatDate(this.params.startDate) +
          '_' +
          this.formatDate(this.params.endDate) +
          '.zip';

        exportCollectData(data)
          .then((res) => {
            this.exportAllLoading = false;
            this.exportAllText = this.$t('alarmmanagement.alarmcollectdata.2m362k');
            const blob = new Blob([res.data], { type: 'application/zip' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.setAttribute('href', url);
            link.setAttribute('download', filename);
            link.click();
            window.URL.revokeObjectURL(url);
          })
          .catch((err) => {
            this.exportAllText = this.$t('alarmmanagement.alarmcollectdata.2m362k');
            this.exportAllLoading = false;
          });
      },
      // 格式化时间
      formatDate(dateStr) {
        const date = this.$moment(dateStr, 'YYYY-MM-DD HH:mm:ss');
        return date.format('YYYYMMDDHHmmss');
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
  }
  .top-tabs {
    background: #e7ebf0;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
    display: flex;
    width: 100%;
    overflow-x: auto;
    .tab-item {
      flex-shrink: 0;
      padding: 0px 20px;
      margin: 10px 0px;
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
      padding: 10px 20px 10px 10px;
      font-size: 16px;
      line-height: 20px;
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
  .table-sty12 {
    display: grid;
    grid-template-columns: repeat(4, 24%);
    justify-content: space-between;
    grid-row-gap: 20px;
  }
  .table-sty4 {
    display: grid;
    grid-template-columns: repeat(2, 49%);
    justify-content: space-between;
    grid-row-gap: 20px;
  }
  .item-sty {
    border: 1px solid #d3d7dd;
    // border-radius: 6px;
  }

  .pagination {
    text-align: right;
    margin-top: 10px;
  }

  .operation-btns {
    display: flex;
    flex-direction: row;
    justify-content: end;
    margin-bottom: 18px;
  }

  .my-cust-class {
    ::v-deep {
      .el-dialog__body {
        padding-top: 0;
      }
    }
  }
</style>
