<template>
  <div class="history-cont">
    <div class="query-cont">
      <el-row>
        <el-form label-position="right" label-width="60px" style="display: flex;flex-wrap: wrap;">
          <el-form-item :label="$t('mapimagemanagent.index.h8zi52')" label-width="80px">
            <el-cascader
              v-model="groupIds"
              :options="groupCamera"
              :props="props"
              collapse-tags
              style="width: 150px"
              @change="getCameras"
              clearable></el-cascader>
          </el-form-item>
          <el-form-item :label="$t('groupview.index.30617c')">
            <el-select
              v-model="formatData.cameraIds"
              multiple
              collapse-tags
              style="width: 150px"
              :placeholder="$t('common.chooseText')">
              <el-option
                v-for="item in cameraList"
                :key="item.cameraId"
                :label="item.cameraName"
                :value="item.cameraId">
              </el-option>
            </el-select>
          </el-form-item>
            <el-form-item :label="$t('common.name')">
              <el-input
                style="width: 150px"
                v-model="formatData.userName"
                placeholder=""
              ></el-input>
          </el-form-item>
          <el-form-item :label="$t('components.facedetail.tp15f4')">
            <el-select
              v-model="formatData.isStranger"
              clearable
              collapse-tags
              style="width: 150px"
              :placeholder="$t('common.chooseText')">
              <el-option
                v-for="item in strangerTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('faceControl.faceRecognition.date')">
            <el-date-picker
              v-model="times"
              style="width: 330px;"
              :default-time="['00:00:00', '23:59:59']"
              type="datetimerange"
              :range-separator="$t('common.pickerDate.to')"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
              :start-placeholder="$t('common.pickerDate.startDate')"
              :end-placeholder="$t('common.pickerDate.endDate')">
            </el-date-picker>
          </el-form-item>
          <el-form-item label-width="10px">
            <el-button type="primary" icon="el-icon-search" @click="getTable"
                >{{$t('button.queryText')}}</el-button
              >
              <el-button icon="el-icon-refresh" @click="reset"
                >{{$t('button.resetText',{text:''})}}</el-button
              >
          </el-form-item>
        </el-form>
      </el-row>
      <div>
        <el-button size="mini" icon="el-icon-date" @click="restFun">{{$t('components.rsetsettings.1md222')}}</el-button>
        <el-button size="mini" icon="el-icon-date" @click="tremFun">{{$t('components.saveterm.arhpig')}}</el-button>
        <el-button size="mini" icon="el-icon-date" @click="similarityFun">{{$t('components.similarity.cb1l2r')}}</el-button>
      </div>
    </div>
    <div class="tab-sty">
      <el-tabs v-model="activeName" @tab-click="changeCamera()" type="border-card">
        <el-tab-pane v-for="(item,index) in groupList" :key="index" :name="item.id">
          <span slot="label"><i class="el-icon-date" v-if="item.name == $t('faceControl.faceRecognition.total')"></i> {{item.name}}</span>
        </el-tab-pane>
        <el-tab-pane  :label="$t('faceControl.faceRecognition.stranger')" name="#"></el-tab-pane>
      </el-tabs>
      <div class="flexbg" ref="flexbg" v-if="dataSource.length">
        <div class="flexItem" v-for="item in dataSource" :key="item.id">
          <div @click.stop="detailFun(item)">
            <el-image
                    style="width: 240px; height: 200px"
                    :src="getFaceReportImageUrl(item.id)"
                  >
                    <div slot="error" class="image-slot">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
          </div>
          <div class="text" style="width: 100%;" @click.stop="detailFun(item)">
            <div v-if="item.strangerType!=1" class="text-flex">
              <div class="tits">{{  item.userName || '--' }}</div>
              <div style="color: #2099fa;cursor: pointer;font-size: 14px;" @click.stop="viewFu(item)">{{$t('facehistory.index.7w985s')}}</div>
            </div>
            <div v-if="item.strangerType==1" class="text-flex">
              <div>
                <div class="tits">{{ item.userName }}</div>
              </div>
              <div style="color: #2099fa;cursor: pointer;font-size: 14px;" @click.stop="addHei(item)">{{$t('faceControl.faceRecognition.faceStore')}}</div>
            </div>
            <div style="padding: 10px;">
              <div class="flex">
                <div class="text-title">{{$t('groupview.index.30617c')}}</div>
                <div class="text-cont" >{{ item.cameraName }}</div>
              </div>
              <div  class="flex">
                <div class="text-title">{{$t('faceControl.faceRecognition.snapTime')}}</div>
                <div class="text-cont">{{ item.reportTime }}</div>
              </div>
              <div v-if="item.strangerType!=1"  class="flex">
                <div class="text-title">{{$t('faceControl.faceRecognition.belongingGroup')}}</div>
                <div class="text-cont">{{ item.groupName }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <el-empty :description="$t('common.noData')"></el-empty>
      </div>
      <div class="pagination" v-if="dataSource.length">
        <el-pagination
          background
          :current-page="params.page"
          :page-size="params.limit"
          layout="total, sizes, prev, pager, next, jumper"
          :total="params.total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        ></el-pagination>
      </div>
    </div>
    <!-- 保存期限 -->
     <SaveTerm v-if="termVisible" @close="termVisible = false"/>
     <!-- 去重设置 -->
      <RsetSettings v-if="restVisible" @close="restVisible = false"/>
      <!-- 详情 -->
      <FaceDetail v-if="detailDrawer" :searchObj="searchObj" :currentId="currentId" @close="closeDetail" />
      <!-- 人脸入库 -->
      <Detail
      v-if="detailVisible"
      :currentItme="currentItme"
      :visible.sync="detailVisible"
      @close="close()"
    ></detail>
    <!-- 查看轨迹 -->
    <Trajectory v-if="drawer" :currentItme="currentItme" @close="close()"/>
    <!-- 相似度设置 -->
     <Similarity v-if="similarityVisible" @close="similarityVisible = false" />
  </div>
</template>

<script>
import Cookies from "js-cookie";
import Tables from '@/components/Table/index.vue'
import { groupListData,faceList,camerasList,groupTree } from './api';
import { getMyDate } from '@/utils/common.js';
import SaveTerm from './components/saveTerm.vue';
import RsetSettings from './components/rsetSettings.vue';
import FaceDetail from './components/faceDetail.vue';
import Detail from './components/detail.vue'
import Similarity from './components/similarity.vue';
import Trajectory from './components/trajectory.vue';
export default {
  components:{
    Tables,
    SaveTerm,
    RsetSettings,
    FaceDetail,
    Detail,
    Similarity,
    Trajectory
  },
  data() {
    return {
      groupIds:[],
      props: { 
        multiple: true,
        value:'id',
        label:'name',
        children:'children'
      },
      currentId:'',
      detailDrawer:false,
      restVisible:false,
      termVisible:false,
      VUE_APP_API_BASE_URL,
      getMyDate:getMyDate,
      formatData:{
        userName:'',
      },
      activeName:'',
      groupList:[],
      times:[],
      timeObj:null,
      params: {
        page: 1,
        limit: 50,
        total: 0,
      },
      loading:false,
      dataSource:[],
      detailVisible:false,
      currentItme:{},
      groupName: "",
      drawer:false,
      cameraList:[],
      groupCamera:[],
      similarityVisible:false,
      searchObj:{},
      strangerTypes: [
        { label: $t('faceControl.faceRecognition.stranger'), value: 1 },
        { label: $t('faceControl.faceRecognition.notStranger'), value: 0 }, 
      ],
    };
  },
  created(){
    this.getGroup();
    this.getCameras();
    this.groupListData();
  },
  methods:{
    // 获取分组
    async getGroup(){
        const res = await groupTree();
        if (res.data && res.data.length > 0) {
            this.groupCamera = this.getTreeData(res.data);
        }
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
    // 获取摄像头
    async getCameras(){
      let arr = [];
      if(this.groupIds.length>0){
        const flattenedArray = [].concat(...this.groupIds);
        arr = [...new Set(flattenedArray)]
      }else{
        arr = []
      }         
      const res = await camerasList({groupIds:arr});
      this.cameraList = res.data;
    },
    // 查看轨迹
    async viewFu(item) {
      this.currentItme = item;
      this.drawer = true;
    },
    // 获取tab
    async groupListData() {
      const { data } = await groupListData();
      data.map((item,ind)=>{
        if(item.name==this.$t('faceControl.faceRecognition.total')){
          item.id = item.id.toString()
        }
      })
      this.groupList = data;
      this.changeCamera();
    },
    // 切换tab
    changeCamera() {
      this.formatData.groupId = this.activeName == 0 ? '' : this.activeName;
      this.params.page = 1;

      // console.log('activeName', this.activeName);

      if(this.activeName == '#') { // 点击陌生人tab
        this.formatData.isStranger = 1
        this.formatData.groupId = null
      } else {
        const activeItem = this.groupList.find(item => item.id === this.activeName);
        if (activeItem && activeItem.name === 'Whitelist') {
          this.formatData.isStranger = 0;
        } else {
          this.formatData.isStranger = null;
        }
      }
      this.getTable();
    },
    // 获取数据
    async getTable(){
      if(this.times.length){
        this.formatData.startDate = this.times[0]; // encodeURIComponent(this.times[0])
        this.formatData.endDate = this.times[1]; // encodeURIComponent(this.times[1])
      }
      let arr = [];
      if(this.groupIds.length>0){
        const flattenedArray = [].concat(...this.groupIds);
        arr = [...new Set(flattenedArray)]
      }else{
        arr = []
      }      
      this.formatData = { 
        ...this.formatData,
        limit: this.params.limit,
        page: this.params.page,
        groupIds:arr,
      }
      console.log('activeName', this.activeName);
      // delete this.formatData.isStranger
      if(this.activeName == '#') {
        this.formatData.isStranger = 1
        this.formatData.groupId = null
      } else {
        //this.formatData.isStranger = null;
        const activeItem = this.groupList.find(item => item.id === this.activeName);
        if (activeItem && activeItem.name === 'Whitelist') {
          this.formatData.isStranger = 0;
        }
      }
      this.loading = true
      const { data,count } = await faceList(this.formatData)
      this.dataSource = data
      this.params.total = parseInt(count)
      this.loading = false
      this.mans();
      if(this.timeObj){
        clearInterval(this.timeObj);
        this.timeObj = null;
      }
      let that = this;
      this.timeObj = setInterval(function () {
        that.getTable();
      }, 5000);
    },
    handleCurrentChange(val) {
      this.params.page = val;
      this.getTable();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getTable();
    },
    reset(){
     this.times = []
     this.formatData = {}
     this.params.page = 1
     this.getTable()
   },
    mans(){
      this.$nextTick(()=>{
        let total = this.dataSource.length
        let limit = Math.floor( this.$refs.flexbg.clientWidth / 150)
        if(total % limit){
          this.totals = Number(limit - (total % limit))
        } else {
          this.totals = 0
        }
      })
    },
    // 查看详情
    detailFun(item){
      this.searchObj = this.getSearch()
      this.currentId = item.id;
      this.detailDrawer = true;
    },
    // 获取查询条件
    getSearch(){
      if(this.times.length){
        this.formatData.startDate = encodeURIComponent(this.times[0]) 
        this.formatData.endDate =  encodeURIComponent(this.times[1]) 
      }
      let arr = [];
      if(this.groupIds.length>0){
        const flattenedArray = [].concat(...this.groupIds);
        arr = [...new Set(flattenedArray)]
      }else{
        arr = []
      }      
      let obj = { 
        ...this.formatData,
        limit: this.params.limit,
        page: this.params.page,
        groupIds:arr,
      }
      //delete obj.isStranger
      if(this.activeName == '#'){
        //obj.isStranger = 1
        obj.groupId = null
      }
      return obj 
    },
    closeDetail(){
      this.currentId = "";
      this.detailDrawer = false;
    },
    // 人脸入库
    addHei(item){
      this.currentItme = item;
      this.detailVisible = true;
    },
    close(){
      this.currentItme = {};
      this.detailVisible = false;
      this.drawer = false;
      this.getTable();
    },
    tremFun(){
      this.termVisible = true
    },
    // 去重
    restFun(){
      this.restVisible = true
    },
    similarityFun(){
      this.similarityVisible = true;
    },
    // 获取人脸报警图片
    getFaceReportImageUrl(id) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/face/report/image?id=${id}&X-Token=${token}`;
    },
  },
  destroyed() {
    clearInterval(this.timeObj);
    this.timeObj = null;
  },
};
</script>
<style scoped lang="scss">
.history-cont{
  .query-cont{
    padding: 16px;
    background: #fff;
    border-radius: 6px;
    margin-bottom: 16px;
  }
  :deep(.el-tabs--border-card>.el-tabs__content){
    display: none !important;
  }
  .flexbg{
    width: 100%;
    display: grid;
    justify-content: space-between;
    grid-template-columns: repeat(auto-fill, 240px);
    grid-gap: 15px;
    padding: 16px 0px; 
  }
  .title{
    margin: 10px;
    margin-left: 0;
  }
  .tits{
    width: 60px;
    height: 20px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    color: #303133;
    font-size: 16px;
  }
  .flexItem{
    display: inline-flex;
    //padding-bottom: var(--Space-Size-small, 8px);
    margin-bottom: 8px;
    flex-direction: column;
    align-items: flex-start;
    gap:  8px;
    border-radius: 6px;
    background: #FFF;
  }
  .flexItem:hover{
    box-shadow: 0px 12px 32px 0px rgba(0, 0, 0, 0.04), 0px 8px 20px 0px rgba(0, 0, 0, 0.08);
  }
  .text-flex{
    padding: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .text-title{
    font-size: 12px;
    color: #909399;
    margin-right: 10px;
    text-align: right;
    width: 50px;
    line-height: 20px;
  }
  .text-cont{
    color: #060606;
    line-height: 20px;
  }
  .flex{
    display: flex;
  }
}
</style>
