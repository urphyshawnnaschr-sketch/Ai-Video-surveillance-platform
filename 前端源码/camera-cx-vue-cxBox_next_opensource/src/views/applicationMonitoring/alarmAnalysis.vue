<template>
  <div class="analysis-cont">
      <div class="query-cont">
        <!-- <el-form :inline="true" :model="params" class="demo-form-inline">
          <el-form-item label="">
            <el-cascader
              v-model="departIds"
              :options="depList"
              :props="{ value: 'id', label: 'name', multiple: true }"
              collapse-tags
              clearable
              :placeholder="$t('applicationMonitoring.alarmanalysis.k88ytr')"
              :show-all-levels="false"
              style="width: 180px"
              @change="departChange"
            >
            </el-cascader>
          </el-form-item>
          <el-form-item label="">
              <el-select v-model="params.locationIds" :placeholder="$t('applicationMonitoring.alarmanalysis.7u1i62')" clearable>
                  <el-option
                  v-for="item in boxArr"
                  :key="item.locationId"
                  :label="item.locationName"
                  :value="item.locationId">
                  </el-option>
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
              :clearable="false"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getData()">{{ $t('button.queryText') }}</el-button>
            <el-button @click="refreshData">{{$t("button.resetText", { text: "" })}}</el-button>
          </el-form-item>
        </el-form> -->
        <el-button type="primary" @click="handleFun()">{{ $t('applicationMonitoring.alarmanalysis.6ui52t') }}</el-button>
      </div>
      <!-- <div class="cont">
          <div>
              <span class="title">{{ $t('applicationMonitoring.alarmanalysis.7m532b') }}</span>
          </div>
          <el-table
          :data="tableData"
          border
          :default-sort = "{prop: 'date', order: 'descending'}"
          style="width: 100%;margin-top: 16px;">
              <el-table-column prop="departName" :label="$t('applicationMonitoring.alarmanalysis.1u6283')" column-key="departName" :filters="departList" :filter-method="filterHandler"></el-table-column>
              <el-table-column prop="boxName" :label="$t('components.detail.fwu578')" column-key="boxName" :filters="locationList" :filter-method="filterHandler"></el-table-column>
              <el-table-column prop="alarmTotal" :label="$t('components.images.jyp5sy')" sortable width="120"></el-table-column>
              <el-table-column prop="alarmHandled" :label="$t('applicationMonitoring.alarmanalysis.0cubxf')" sortable width="120"></el-table-column>
              <el-table-column prop="alarmClosed" :label="$t('applicationMonitoring.alarmanalysis.224w12')" sortable width="120"></el-table-column>
              <el-table-column prop="alarmUnhandle" :label="$t('alarmdetail.newdetail.694hl5')" sortable width="90"></el-table-column>
              <el-table-column prop="alarmRate" :label="$t('applicationMonitoring.alarmanalysis.5v8x55')" sortable width="90">
                  <template slot-scope="scope">
                      {{ scope.row.alarmRate }}%
                  </template>
              </el-table-column>
          </el-table>
      </div> -->
      <div class="cont">
          <div>
              <span class="title">{{ $t('applicationMonitoring.alarmanalysis.in95i8') }}</span>
          </div>
          <div class="chart" id="chart"></div>
      </div>
      <div class="cont">
          <div>
              <span class="title">{{ $t('applicationMonitoring.alarmanalysis.gb1tj2') }}</span>
          </div>
          <div class="chart" id="dealingChart"></div>
      </div>

      <!-- 事件处理目标值 -->
       <el-dialog
          :title="$t('common.prompt')"
          :visible.sync="eventVisible"
          width="400px"
          :before-close="handleClose">
          <el-table
          v-if="eventVisible"
          :data="eventList"
          border
          style="width: 100%;">
              <el-table-column :label="$t('applicationMonitoring.alarmanalysis.6ui52d')" width="80">
                  <template slot-scope="scope">
                      {{ scope.row.month }}{{$t('applicationMonitoring.alarmanalysis.6ui52v')}}
                  </template>
              </el-table-column>
              <el-table-column :label="$t('applicationMonitoring.alarmanalysis.6ui52e')">
                  <template slot-scope="scope">
                       <el-input-number v-model="scope.row.value" :min="1" :step="1" :precision="0"></el-input-number>
                  </template>
              </el-table-column>
          </el-table>
          <div style="margin-top: 16px;text-align: right;">
              <el-button @click="(eventVisible = false), getData()">{{$t("button.cancelText", { text: '' })}}</el-button>
              <el-button type="primary" @click="saveFun()">{{$t("button.saveText", { text: '' })}}</el-button>
          </div>
      </el-dialog>
  </div>
</template>
<script>
import { listTree } from "@/api/applicationMonitoring/boxManagement";
import {
alarmList,
alarmTrend,
dealingTrend,
boxList,
targetList,
targetSave
} from "@/api/applicationMonitoring/alarmManagement";
import * as echarts from "echarts";
export default {
data() {
  return {
      departIds:[],
      boxArr:[],
      date: [new Date(), new Date()],
      depList:[],
      params:{
          departIds:null,
          locationIds:null,
      },
      tableData:[],
      departList:[],
      locationList:[],
      eventVisible:false,
      eventList:[],
  };
},
created() {
  this.date = [
    this.$moment(new Date(this.date[0].setHours(0, 0, 0))).format(
      "YYYY-MM-DD HH:mm:ss"
    ),
    this.$moment(new Date(this.date[1].setHours(23, 59, 59))).format(
      "YYYY-MM-DD HH:mm:ss"
    ),
  ];
  this.getTree();
  this.getBox();
  this.getListData();
  this.getAlarmTrend();
  this.getDealingTrend();
},
methods: {
  // 获取盒子接口
  async getBox(){
      const res = await boxList({objectAll:true})
      this.boxArr = res.data;
  },
  // 获取列表数据
  async getListData(){
      let arr =[];
      if(this.params.locationIds){
          arr.push(this.params.locationIds)
      }
      let obj = {
          departIds:this.params.departIds,
          locationIds:arr,
      }
      if (this.date && this.date.length > 0) {
          obj.startDate = this.date[0];
          obj.endDate = this.date[1];
      }
      this.departList = [];
      this.locationList = [];
      const res = await alarmList(obj)
      this.tableData = res.data;

      const departArr = Array.from(new Set(res.data.map(item => item.departId))).map(departId => {
          return res.data.find(item => item.departId === departId);
      });
      departArr.forEach(item=>{
          this.departList.push({
              text:item.departName,
              value:item.departName,
          })
      })
      const locationArr = Array.from(new Set(res.data.map(item => item.boxId))).map(boxId => {
          return res.data.find(item => item.boxId === boxId);
      });
      locationArr.forEach(item=>{
          this.locationList.push({
              text:item.boxName,
              value:item.boxName
          })
      })
  },
  // 筛选
  filterHandler(value, row, column) {
      const property = column['property'];
      return row[property] === value;
  },
  // 组织切换
  departChange(e){
      let arr = [];
      if(e.length>0){
          e.forEach(item=>{
              if(item.length>0){
                  arr.push(item[0])
              }
          })
      }
      this.params.departIds = arr;
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
  // 查询
  getData() {
      this.getListData();
  },
  // 重置
  refreshData() {
      let dateList = [new Date(), new Date()];
      this.date = [
          this.$moment(new Date(dateList[0].setHours(0, 0, 0))).format(
          "YYYY-MM-DD HH:mm:ss"
          ),
          this.$moment(new Date(dateList[1].setHours(23, 59, 59))).format(
          "YYYY-MM-DD HH:mm:ss"
          ),
      ];
      Object.assign(this.params, {
          departIds: null,
          locationIds:null,
      });
      this.getListData();
  },
  // 改变时间
  dateChange() {
      this.getListData();
  },
  // 获取告警趋势
  async getAlarmTrend(){
      const res = await alarmTrend();
      const chartDom = document.getElementById("chart");
      const myChart = echarts.init(chartDom);
      myChart.clear();
      let Arr = res.data;
      const timeArr = [];//时间
      const alarmArr = [];//告警数量
      const confirmArr = [];//确认数量
      const closeArr = [];//关闭数量
      const untreatedArr = [];//未处理数量
      const processRateArr = [];//处理率
      Arr.forEach(item=>{
          timeArr.push(item.time);
          alarmArr.push(item.total);
          confirmArr.push(item.handled);
          closeArr.push(item.closed);
          untreatedArr.push(item.unhandle);
          processRateArr.push(item.rate);
      })
      const option = {
          tooltip: {
              trigger: 'axis',
              axisPointer: {
              type: 'cross',
              crossStyle: {
                  color: '#999'
              }
              },
              formatter: function(params) {
                    let result = `<div style="font-weight:bold">${params[0].name}</div>`;
                    params.forEach(item => {
                    result += `
                        <div>
                        <span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${item.color}"></span>
                        ${item.seriesName}: ${item.value}${item.seriesName === $t('applicationMonitoring.alarmanalysis.5v8x55') ? ' %' : ''}
                        </div>
                    `;
                    });
                    return result;
                }
          },
            toolbox: {
                feature: {
                    dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
                }
            },

          legend: {
              data: [this.$t('components.images.jyp5sy'), this.$t('applicationMonitoring.alarmanalysis.tsj5hw'), this.$t('applicationMonitoring.alarmanalysis.224w12'),this.$t('applicationMonitoring.alarmanalysis.1h3273'),this.$t('applicationMonitoring.alarmanalysis.5v8x55')],
          },
          xAxis: [
              {
                  type: 'category',
                  data: timeArr,
                  axisPointer: {
                      type: 'shadow'
                  }
              }
          ],
          yAxis: [
              {
                  type: 'value',
                  name: this.$t('projectdetail.overview.7m7byy'),
              },
              {
                  type: 'value',
                  name: this.$t('applicationMonitoring.alarmanalysis.5v8x55'),
                  axisLabel: {
                      formatter: '{value} %'
                  }
              }
          ],
          series: [
              {
                  name: this.$t('components.images.jyp5sy'),
                  type: 'bar',
                  data: alarmArr,
                  itemStyle:{
                      color:''
                  }
              },
              {
                  name: this.$t('applicationMonitoring.alarmanalysis.tsj5hw'),
                  type: 'bar',
                  data: confirmArr
              },
              {
                  name: this.$t('applicationMonitoring.alarmanalysis.224w12'),
                  type: 'bar',
                  data: closeArr
              },
              {
                  name: this.$t('applicationMonitoring.alarmanalysis.1h3273'),
                  type: 'bar',
                  data: untreatedArr
              },
              {
                  name: this.$t('applicationMonitoring.alarmanalysis.5v8x55'),
                  type: 'line',
                  yAxisIndex: 1,
                  tooltip: {
                    valueFormatter: function (value) {
                      return value + ' %';
                    }
                  },
                  data: processRateArr
              }
          ],
        dataZoom: [
            {
                type: 'inside',
                start: 0,
                end: 100
            },
            {
                start: 0,
                end: 100
            }
        ],
      };
      myChart.setOption(option);
      
  },
  // 获取处理趋势
  async getDealingTrend(){
      const res = await dealingTrend();
      const chartDom = document.getElementById("dealingChart");
      const myChart = echarts.init(chartDom);
      myChart.clear();
      let Arr = res.data;
      const timeArr = [];//时间
      const alarmArr = [];//告警数量
      const confirmArr = [];//确认数量
      const closeArr = [];//关闭数量
      const averageDurationArr = [];//平均处理时长
      Arr.forEach(item=>{
          timeArr.push(item.time);
          alarmArr.push(item.total);
          confirmArr.push(item.handled);
          closeArr.push(item.closed);
          averageDurationArr.push(item.avgTime);
      })
      const option = {
          tooltip: {
              trigger: 'axis',
              axisPointer: {
              type: 'cross',
              crossStyle: {
                  color: '#999'
              }
              },
              formatter: function(params) {
                    let result = `<div style="font-weight:bold">${params[0].name}</div>`;
                    params.forEach(item => {
                    result += `
                        <div>
                        <span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${item.color}"></span>
                        ${item.seriesName}: ${item.value}${item.seriesName === $t('applicationMonitoring.alarmanalysis.6ui52q') ? ' min' : ''}
                        </div>
                    `;
                    });
                    return result;
                }
          },
          legend: {
              data: [this.$t('components.images.jyp5sy'), this.$t('applicationMonitoring.alarmanalysis.tsj5hw'), this.$t('applicationMonitoring.alarmanalysis.224w12'),this.$t('applicationMonitoring.alarmanalysis.6ui52q')],
          },
          xAxis: [
              {
                  type: 'category',
                  data: timeArr,
                  axisPointer: {
                      type: 'shadow'
                  }
              }
          ],
          yAxis: [
              {
                  type: 'value',
                  name: this.$t('projectdetail.overview.7m7byy'),
              },
              {
                  type: 'value',
                  name: this.$t('applicationMonitoring.alarmanalysis.6ui52q'),
                  axisLabel: {
                      formatter: '{value} min'
                  }
              }
          ],
          series: [
              {
                  name: this.$t('components.images.jyp5sy'),
                  type: 'bar',
                  data: alarmArr
              },
              {
                  name: this.$t('applicationMonitoring.alarmanalysis.tsj5hw'),
                  type: 'bar',
                  data: confirmArr
              },
              {
                  name: this.$t('applicationMonitoring.alarmanalysis.224w12'),
                  type: 'bar',
                  data: closeArr
              },
              {
                  name: this.$t('applicationMonitoring.alarmanalysis.6ui52q'),
                  type: 'line',
                  yAxisIndex: 1,
                  tooltip: {
                      valueFormatter: function (value) {
                      return value + ' min';
                      }
                  },
                  data: averageDurationArr
              }
          ],
          toolbox: {
                feature: {
                    dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
                }
            },
          dataZoom: [
            {
                type: 'inside',
                start: 0,
                end: 100
            },
            {
                start: 0,
                end: 100
            }
        ],
      };
      myChart.setOption(option);
  },
  handleFun(){
      targetList().then(res=>{
          this.eventList = res.data
          this.eventVisible = true;
      }).catch(()=>{
          this.eventVisible = false;
      })
  },
  handleClose(){
      this.eventVisible = false;
  },
  saveFun(){
      targetSave({items:this.eventList}).then(res=>{
          this.$message.success($t("button.saveText", { text: $t("common.success") }));
          this.eventVisible = false;
          this.getData();
      })
  }
},
};
</script>
<style scoped lang="scss">
.analysis-cont{
  .query-cont{
      padding: 20px;
      background: #FFF;
      display: flex;
      justify-content: space-between;
      align-items: center;
      :deep(.el-form-item--small.el-form-item){
          margin-bottom: 0px !important;
      }
  }
  .cont{
      padding: 20px;
      background: #FFF;
      border-radius: 6px;
      margin-top: 20px;
  }
  .title{
      font-size: 16px;
      font-weight: 600;
      line-height: 20px;
      border-bottom: 2px solid #EB3A2F
  }
  .chart {
      // width: 100%;
      height: 558px;
  }
  
}
</style>
