<template>
  <div class="app-container">
    <!-- 顶部内容 -->
    <div class="top">
      <div class="subheading" style="width: 64px;flex: none">
        <div class="text">{{$t('components.retrieveresults.ydlm6q')}}</div>
      </div>
      <div class="result-info">
        <div class="result-item">
          <div class="key">{{$t('components.trajectory.8i64t4')}}</div>
          <div class="value">{{ detailInfo.resultNum }}{{$t('components.trajectory.715618')}}</div>
        </div>
        <div class="result-item" style="margin-right: 15px;">
          <div class="key">{{$t('components.trajectory.l1jo3i')}}</div>
          <div class="value">{{ detailInfo.resultEndCameraName }}</div>
        </div>
        <div class="result-item">
          <div class="key">{{$t('components.trajectory.c35kz3')}}</div>
          <div class="value">{{ detailInfo.resultNum?detailInfo.resultEndDate:'' }}</div>
        </div>
        <div class="result-item" style="margin-right: 15px;">
          <div class="key">{{$t('components.trajectory.f37428')}}</div>
          <div class="value">{{ detailInfo.resultStartCameraName }}</div>
        </div>
        <div class="result-item">
          <div class="key">{{$t('components.trajectory.0b2g66')}}</div>
          <div class="value">{{ detailInfo.resultNum?detailInfo.resultStartDate:'' }}</div>
        </div>
        
      </div>
      <div class="remove-button" @click="deleteFun()">{{$t('components.retrieveresults.iz0ua7')}}</div>
    </div>
    <!-- 检索条件 -->
    <el-form :inline="true" :model="queryParams" class="demo-form-inline">
      <el-form-item :label="$t('groupview.index.30617c')">
        <el-select v-model="queryParams.camera" filterable :placeholder="$t('components.retrieveresults.t4ys55')">
          <el-option v-for="(item,index) in cameraList" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('flowDsetection.flowSee.dateTime')">
        <el-date-picker size="small" v-model="queryParams.dateRange" type="datetimerange"
                        value-format="yyyy-MM-dd HH:mm:ss" @change="dateChange" :range-separator="$t('common.pickerDate.to')"
                        :start-placeholder="$t('common.pickerDate.startDate')"
                        :end-placeholder="$t('common.pickerDate.endDate')">
        </el-date-picker>
      </el-form-item>
      <el-form-item style="float: right;">
        <el-button type="primary" @click="getTableList">{{$t('button.queryText')}}</el-button>
        <el-button @click="resetQuery">{{$t('button.resetText',{text:''})}}</el-button>
      </el-form-item>
    </el-form>
    <!-- 检索结果列表 -->
    <div class="subheading-left">
      <div class="block"></div>{{$t('components.retrieveresults.967enk')}}</div>
    <div class="data-list">
      <div class="box-item" v-for="(item, index) in searchResults" :key="index" @click="handleDetails(item)">
        <el-image class="img" :src="getFaceReportImageUrl(item.reportId)"></el-image>
        <div class="info-box">
          <div class="info-item">
            <div class="title">{{$t('groupview.index.30617c')}}</div>
            <div class="text">{{ item.cameraName }}</div>
          </div>
          <div class="info-item">
            <div class="title">{{$t('faceControl.faceRecognition.snapTime')}}</div>
            <div class="text">{{ item.reportAt }}</div>
          </div>
          <div class="info-item">
            <div class="title">{{$t('components.facedetail.tp15f4')}}</div>
            <div class="text">{{ item.similarity?parseFloat(item.similarity*100).toFixed(2):0 }}%</div>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination" v-if="searchResults.length">
      <el-pagination
        background
        :current-page="queryParams.page"
        :page-size="queryParams.limit"
        layout="total, sizes, prev, pager, next, jumper"
        :total="queryParams.total"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
      ></el-pagination>
    </div>
    <!-- 人脸轨迹 -->
    <div class="subheading-left">
      <div class="block"></div>{{$t('components.retrieveresults.312ddc')}}</div>
    <trajectories v-if="fileObj.filename" :fileObj="fileObj" :pathList="pathList"></trajectories>

    <!-- 人脸识别详情 -->
     <ResultsDetails v-if="detailDrawer" :currentId="currentItemId" :currentSearchId="currentId" @close="detailDrawer = false"/>
  </div>
</template>
<script>
import Cookies from "js-cookie";
import ResultsDetails from './resultsDetails.vue'
import trajectories from './trajectories.vue';
import { searchInfo,searchDelete,searchList,flowCameras,flowPath,configPrimary } from "../api"
export default {
  props:{
    currentId:{
      type:String,
      default:""
    }
  },
  data() {
    return {
      VUE_APP_API_BASE_URL,
      detailInfo:{},
      detailDrawer:false,
      // 查询参数
      queryParams: {
        camera: '',
        dateRange: [],
        page: 1,
        limit: 10,
        total: 0,
      },
      cameraList:[],
      // 检索结果数据
      searchResults: [],
      pathList:[],
      currentItemId:"",
      fileObj:{}
    };
  },
  created() {
    if(this.currentId){
      // this.getData();
    }
  },
  methods: {
    getData(){
      this.getCameras();
      this.getInfo();
      this.getTableList();
      this.getPath();
      this.getMap();
    },
    // 获取摄像头
    async getCameras(){
      const res = await flowCameras({searchId:this.currentId});
      this.cameraList = res.data;
    },
    // 获取检索结果
    async getInfo(){
      const res = await searchInfo({id:this.currentId});
      this.detailInfo = res.data;
    },
    // 获取检索结果列表
    async getTableList() {
      let obj = {
        searchId:this.currentId,
        limit:this.queryParams.limit,
        page:this.queryParams.page,
        startDate:this.queryParams.startDate,
        endDate:this.queryParams.endDate,
        cameraId:this.queryParams.camera
      }
      const res = await searchList(obj);
      this.searchResults = res.data;
      this.queryParams.total = parseInt(res.count)
    },
    handleCurrentChange(val) {
      this.queryParams.page = val;
      this.getTableList();
    },
    handleSizeChange(val) {
      this.queryParams.limit = val;
      this.queryParams.page = 1;
      this.getTableList();
    },
    // 删除
    deleteFun(){
      searchDelete({id:this.currentId}).then(res=>{
        this.$message.success($t("button.deleteText", { text: $t("common.success") }));
        this.$emit('refreshFun')
      })
    },
    // 获取路径
    async getPath(){
      let obj = {
        searchId:this.currentId,
        limit:this.queryParams.limit,
        page:this.queryParams.page,
        startDate:this.queryParams.startDate,
        endDate:this.queryParams.endDate,
        cameraId:this.queryParams.camera
      }
      const res = await flowPath(obj)
      this.pathList = res.data;
    },
    // 获取地图
    async getMap(){
      const res = await configPrimary();
      this.fileObj = res.data.faceTrackConfig
    },
    // 时间范围选择
    dateChange(date) {
      this.queryParams.startDate = date[0];
      this.queryParams.endDate = date[1];
    },
    // 查询条件重置
    resetQuery() {
      this.queryParams = {
        page: 1,
        limit: 10,
        total: 0
      };
      this.getTableList();
    },
    // 打开详情弹窗
    handleDetails(item) {
      this.currentItemId = item.reportId;
      this.detailDrawer = true;
    },
    // 获取人脸报警图片
    getFaceReportImageUrl(id) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/face/report/image?id=${id}&X-Token=${token}&_t=${new Date().getTime()}`;
    },
  },
  components: {
    ResultsDetails,
    trajectories,
  },
};
</script>
<style scoped lang="scss">
.app-container {
  .subheading-left {
    display: flex;
    align-items: center;
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 20px;

    .block {
      width: 3px;
      height: 14px;
      border-radius: 2px;
      background: #EB3A2F;
      margin-right: 10px;
    }
  }

  .top {
    height: max-content;
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;

    .result-info {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      margin-left: 40px;
      flex: 1;
      .result-item {
        margin-right: 30px;
        margin-bottom: 10px;
        .key {
          font-size: 12px;
          color: #606266;
        }

        .value {
          font-size: 16px;
          color: #303133;
          height: 30px;
          display: flex;
          align-items: center;
        }
      }

      .remove-button {
        height: max-content;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 2px 8px;
        border-radius: 4px;
        border: 1px solid #F56C6C;
        font-size: 12px;
        color: #F56C6C;
        cursor: pointer;
      }
    }
    .remove-button {
        height: max-content;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 2px 8px;
        border-radius: 4px;
        border: 1px solid #F56C6C;
        font-size: 12px;
        color: #F56C6C;
        cursor: pointer;
        
      }
  }

  .data-list {
    display: grid;
    justify-content: space-between;
    grid-template-columns: repeat(auto-fill, 220px);
    gap: 8px;

    .box-item {
      margin-bottom: 20px;
      border-radius: 6px;
      overflow: hidden;
      border: 1px solid #DCDCDC;
      cursor: pointer;

      .img {
        width: 100%;
        height: 200px;
      }

      .info-box {
        padding: 14px;

        .info-item {
          font-size: 12px;
          display: flex;
          align-items: center;
          margin-bottom: 4px;

          .title {
            width: 55px;
            text-align: right;
            color: #909399;
            margin-right: 8px;
            font-weight: normal;
            font-size: 12px;
          }

          .text {
            color: #060606;
          }
        }
      }
    }
  }
}
</style>
