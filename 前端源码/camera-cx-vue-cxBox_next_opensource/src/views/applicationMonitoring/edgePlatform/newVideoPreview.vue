<template>
  <div class="wrap video-dialog">
    <div class="flex-left">
      <el-card class="box-card">
        <div slot="header">
          <div style="display: flex; align-items: center; justify-content: space-between">
            <div
              style="
                padding-right: 20px;
                font-size: 20px;
                color: #1c1f23;
                font-weight: bold;
                border-left: 2px solid #eb3a2f;
                padding-left: 10px;
              "
              >{{ $t('edgeplatform.newvideopreview.e06npu') }}</div
            >
            <div>
              <el-cascader
                v-model="searchObj.departIds"
                :options="depList"
                :props="{ value: 'id', label: 'name', multiple: true }"
                collapse-tags
                clearable
                :placeholder="$t('applicationMonitoring.alarmanalysis.1u6283')"
                style="width: 190px"
                @change="getCameras"
              >
              </el-cascader>
              <!-- <el-cascader
                v-model="searchObj.groupIds"
                placeholder="Grouped Camera"
                :options="groupList"
                :props="{multiple: true,value:'id',label:'name',children:'children'}"
                collapse-tags
                style="width: 190px;margin-left: 10px;"
                @change="getCameras"
                clearable>
              </el-cascader> -->
              <el-select
                v-model="searchObj.cameraIds"
                multiple
                filterable
                clearable
                collapse-tags
                style="width: 150px; margin-left: 10px"
                :placeholder="$t('groupview.index.30617c')"
              >
                <el-option
                  v-for="item in cameraList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                >
                </el-option>
              </el-select>
              <el-button
                type="primary"
                size="mini"
                icon="el-icon-search"
                style="margin-left: 10px"
                @click="getData"
                >{{ $t('button.queryText') }}</el-button
              >
              <el-button icon="el-icon-refresh" size="mini" @click="reset">{{
                $t('button.resetText', { text: '' })
              }}</el-button>
            </div>
          </div>
          <div style="margin-top: 16px">
            <!-- <el-button
              type="primary"
              style="margin-right: 20px"
              @click="detailFun"
              >{{
                $t("applicationMonitoring.videoStreamManagement.viewToday")
              }}</el-button
            > -->
            <el-radio-group v-model="params.limit">
              <el-radio-button label="1">{{
                $t('applicationMonitoring.common.oneScreen')
              }}</el-radio-button>
              <el-radio-button label="4">{{
                $t('applicationMonitoring.common.fourScreen')
              }}</el-radio-button>
              <el-radio-button label="9">{{
                $t('applicationMonitoring.common.nineScreen')
              }}</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <!-- 视频容器 - 已优化高度自适应 -->
        <div class="video-container">
          <div
            class="video-box"
            :class="'video' + params.limit"
            :style="{
              height: `calc(100vh - ${headerHeight + paginationHeight + 80}px)`,
              minHeight: '300px',
            }"
          >
            <div
              class="video-item"
              ref="videoItem"
              v-for="(item, index) in activeList"
              :key="index"
            >
              <div
                style="position: absolute; width: 100%; height: 100%"
                v-if="item.id && cameraOptions.length > 0"
              >
                <VideoBox
                  v-if="item.id"
                  :cameraOptions="cameraOptions"
                  :Id="item.id"
                  :index="index"
                  :ref="'video' + index"
                ></VideoBox>
              </div>
              <div v-else style="width: 100%; text-align: center">
                {{ $t('edgeplatform.newvideopreview.1f5y35') }}
              </div>
            </div>
            <div
              class="video-item"
              v-for="i in Number(params.limit) > activeList.length
                ? Number(params.limit) - activeList.length
                : 0"
              :key="'key' + i"
            >
              <div style="width: 100%; text-align: center">
                {{ $t('edgeplatform.newvideopreview.1f5y35') }}
              </div>
            </div>
          </div>
        </div>

        <div class="pagination video-p">
          <el-pagination
            background
            :current-page="params.page"
            :page-size="params.limit"
            layout="slot, prev, pager, next, jumper"
            :total="total"
            @current-change="handleCurrentChange"
          >
            <template #default>
              <span class="el-pagination__total is-first"
                >{{ $t('modeltesting.modeltesting.s249my') }}{{ total
                }}{{ $t('edgeplatform.newvideopreview.i4qck6') }}</span
              >
            </template>
          </el-pagination>
        </div>
      </el-card>
    </div>
    <div class="flex-right">
      <el-card class="box-card">
        <div
          slot="header"
          :style="{
            display: 'flex',
            flexDirection: isEnglish ? 'column' : 'row',
            alignItems: 'flex-start',
            justifyContent: 'space-between',
            fontSize: '13px',
            gap: isEnglish ? '10px' : '0',
          }"
        >
          <!-- 实时数据标题 -->
          <div
            style="
              font-weight: bold;
              font-size: 18px;
              border-left: 3px solid #eb3a2f;
              padding-left: 5px;
              width: 100%;
            "
          >
            {{ $t('applicationMonitoring.common.realData') }}
          </div>

          <!-- 语音开关区域 -->
          <div
            :style="{
              display: 'flex',
              alignItems: 'center',
              width: isEnglish ? '70%' : '100%',
              justifyContent: isEnglish ? 'space-between' : 'flex-end',
              paddingLeft: isEnglish ? '8px' : '0px',
            }"
          >
            <span style="font-weight: bold; font-size: 14px; margin-right: 8px"
              >{{ $t('edgeplatform.newvideopreview.573286') }}
            </span>
            <el-switch size="mini" v-model="VocieSwitch" @change="switchChange"> </el-switch>
          </div>

          <!-- 探测开关区域 -->
          <div
            :style="{
              display: 'flex',
              alignItems: 'center',
              width: isEnglish ? '70%' : '100%',
              justifyContent: isEnglish ? 'space-between' : 'flex-end',
              paddingLeft: isEnglish ? '8px' : '0px',
            }"
          >
            <div style="font-weight: bold; font-size: 14px; margin-right: 8px">
              {{ $t('edgeplatform.newvideopreview.48qel9') }}
            </div>
            <el-switch v-model="$store.state.alarmSwitch"> </el-switch>
          </div>
        </div>
        <div class="count">
          <img src="@/assets/images/alare.png" style="width: 100%" />
          <div class="text-cont">
            <div
              style="
                width: 218px;
                padding: 10px;
                display: flex;
                align-items: center;
                justify-content: space-between;
              "
            >
              <div>
                <div>{{ $t('applicationMonitoring.common.todayAlarmNum') }}</div>
                <div class="alarm-num">
                  <span style="font-size: 28px; margin-right: 5px">{{ alarmsCount }}</span>
                  <span>{{ $t('components.alarmday.eb47rv') }}</span>
                </div>
              </div>
              <div style="font-weight: bold; cursor: pointer" @click="detailFun">
                {{ $t('applicationMonitoring.videoStreamManagement.viewToday') }}
              </div>
            </div>
          </div>
        </div>
        <el-scrollbar class="scrollbar-wrapper">
          <ul class="list">
            <li v-for="(item, index) in alarmsList" :key="index">
              <el-image class="img" :src="getReportImageUrl(item.id)" fit="cover">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <div
                :style="{
                  width: '100%',
                  minWidth: '200px',
                  padding: '0 4px',
                  boxSizing: 'border-box',
                }"
              >
                <!-- 算法名称 - 第一行 -->
                <div
                  :style="{
                    fontSize: isEnglish ? '12px' : '14px',
                    fontWeight: 'bold',
                    marginBottom: '10px',
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                  }"
                >
                  <span>{{ $t('common.algorithmName') }}:</span>
                  <span :style="{ marginLeft: '4px' }">{{ item.algorithmName }}</span>
                </div>

                <!-- 告警时间 - 第二行 -->
                <div
                  :style="{
                    marginBottom: '10px',
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                  }"
                >
                  <span :style="{ fontWeight: 'bold' }">{{ $t('common.alarmTime') }}:</span>
                  <span :style="{ marginLeft: '4px' }">{{ item.alarmTime }}</span>
                </div>

                <!-- 区域名称和查看详情 - 第三行 -->
                <div
                  :style="{
                    display: 'flex',
                    alignItems: 'center',
                    width: '100%',
                    paddingBottom: '8px',
                  }"
                >
                  <!-- 区域名称 -->
                  <div
                    :style="{
                      flex: 1,
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                    }"
                  >
                    <span :style="{ fontWeight: 'bold' }">{{ $t('common.areaName') }}:</span>
                    <span :style="{ marginLeft: '4px' }">{{ item.wareName }}</span>
                  </div>

                  <!-- 查看详情按钮 -->
                  <div
                    :style="{
                      flex: '0 0 auto',
                      color: '#E53935',
                      cursor: 'pointer',
                      fontWeight: 'bold',
                      whiteSpace: 'nowrap',
                      minWidth: '80px',
                      textAlign: 'right',
                      paddingRight: '8px',
                    }"
                    @click="clickDetail(item)"
                    >{{ $t('alarmmanagement.alarmcard.376044') }}</div
                  >
                </div>
              </div>
            </li>
          </ul>
        </el-scrollbar>
      </el-card>
    </div>
    <AlgorithmList
      v-if="algorithmListVisible"
      @close="((algorithmListVisible = false), getAlgorithms())"
    />
    <AlarmPopup
      v-if="alarmVisible"
      :alarmData="alarmData"
      :fileUrl="getReportImageUrl(alarmData.id)"
      @close="alarmVisible = false"
    />
    <!-- 查看当日 -->
    <el-dialog
      :close-on-click-modal="false"
      width="90%"
      top="5vh"
      :title="$t('applicationMonitoring.videoStreamManagement.viewToday')"
      :visible.sync="detailVisible"
      @close="closeHandle"
    >
      <AlarmManagement :isDisabled="true" v-if="detailVisible" />
    </el-dialog>
    <!-- 告警详情 -->
    <AlarmDetail :alarmId="currentId" v-if="alarmDetailVisible" @close="closeFun" />
  </div>
</template>
<script>
  import {
    getAlgorithms,
    getListPageActives,
    getAlarms,
    getCounter,
    getCameraListData,
    departTree,
    listData7,
    listData8,
    listPage9,
    groupTree,
  } from '@/api/applicationMonitoring/videoStreamManagement';
  import Cookies from 'js-cookie';

  import AlarmDetail from '@/components/applicationMonitoring/alarmManagement/alarmDetail/newDetail';
  import AlarmPopup from '@/components/applicationMonitoring/videoStreamManagement/alarmPopup/newPopup';
  import AlgorithmList from '@/components/applicationMonitoring/videoStreamManagement/algorithmList';
  import VideoBox from '@/components/applicationMonitoring/videoStreamManagement/videoBox';
  import store from '@/store';

  import AlarmManagement from '../alarmManagement';

  export default {
    components: {
      AlgorithmList,
      AlarmPopup,
      AlarmManagement,
      AlarmDetail,
      VideoBox,
    },
    data() {
      return {
        VUE_APP_API_BASE_URL,
        detailVisible: false,
        // alarmSwitch: true,
        VocieSwitch: sessionStorage.getItem('VocieSwitch') == 'false' ? false : true,
        algorithmListVisible: false,
        alarmVisible: false,
        alarmData: {},
        algorithmList: [],
        cameraOptions: [],
        activeList: [],
        alarmsList: [],
        playerList: [],
        alarmsCount: 0,
        websocket: null,
        timer: null,
        params: {
          page: 1,
          limit: 4,
        },
        total: 0,
        // timerPlayings:'',
        playerKeyframe: [],
        timerKeyframe: '',
        checkKeyframeRun: false,
        playType: 0,
        currentId: '',
        alarmDetailVisible: false,
        searchObj: {
          groupIds: [],
          cameraIds: [],
          departIds: [],
        },
        depList: [],
        groupList: [],
        cameraList: [],
        copyCameraList: [],
        // 高度计算相关参数
        headerHeight: 140, // 头部区域高度
        paginationHeight: 50, // 分页区域高度
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
          if (typeof val == 'string') {
            this.params.limit = Number(val);
            this.params.page = 1;
            this.params.limit = Number(this.params.limit);
            this.getListPageActives(1);
          }
        },
      },
      // 监听窗口大小变化，重新计算视频容器高度
      '$el.offsetWidth': {
        handler() {
          this.$nextTick(() => {
            // 可以在这里添加需要的调整逻辑
          });
        },
        immediate: true,
      },
    },
    async created() {
      await this.getDep();
      await this.getGruop();
      await this.getCameras();
      await this.getCameraListData();
      await this.getListPageActives();
      this.getAlarms();
      this.getCounter();
      this.connectWebsocket();

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize);
    },
    methods: {
      // 处理窗口大小变化
      handleResize() {
        // 触发重绘
        this.$forceUpdate();
      },

      // 获取组织
      async getDep() {
        const res = await departTree();
        if (res.data && res.data.length > 0) {
          this.depList = this.getTreeData(res.data);
        }
      },
      // 获取分组摄像机
      async getGruop() {
        const res = await groupTree();
        if (res.data && res.data.length > 0) {
          this.groupList = this.getTreeData(res.data);
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
      // 根据组织和分组获取摄像机
      async getCameras() {
        let arr = [];
        let depArr = [];
        if (this.searchObj.groupIds.length > 0) {
          const flattenedArray = [].concat(...this.searchObj.groupIds);
          arr = [...new Set(flattenedArray)];
        }
        if (this.searchObj.departIds.length > 0) {
          const depdArray = [].concat(...this.searchObj.departIds);
          depArr = [...new Set(depdArray)];
        }
        let obj = {
          cameraGroupIds: arr,
          departIds: depArr,
          running: '1',
        };
        const res = await listData7(obj);
        this.cameraList = res.data;
      },
      // 获取算法列表
      async getAlgorithms() {
        const data = await getAlgorithms();
        this.algorithmList = data.data;
      },
      // 获取摄像头列表
      async getCameraListData() {
        let arr = [];
        let depArr = [];
        if (this.searchObj.groupIds.length > 0) {
          const flattenedArray = [].concat(...this.searchObj.groupIds);
          arr = [...new Set(flattenedArray)];
        }
        if (this.searchObj.departIds.length > 0) {
          const depdArray = [].concat(...this.searchObj.departIds);
          depArr = [...new Set(depdArray)];
        }
        let obj = {
          cameraGroupIds: arr,
          departIds: depArr,
          cameraIds: this.searchObj.cameraIds,
          running: '1',
        };
        const data = await listData8(obj);
        this.cameraOptions = data.data;
        //this.copyCameraList = data.data;
      },
      // 点击查询
      async getData() {
        await this.getCameraListData();
        await this.getListPageActives(1);
      },
      // 点击重置
      async reset() {
        this.searchObj = {
          cameraIds: [],
          departIds: [],
          groupIds: [],
        };

        // this.cameraOptions = this.copyCameraList;

        // if(this.cameraOptions.length < this.params.limit) {
        //   this.activeList = [];
        //   this.cameraOptions.forEach((item) => {
        //     this.activeList.push({
        //       id: item.id,
        //       name: item.name,
        //     });
        //   });
        // } else {
        //   for(let i = 0; i < this.params.limit; i++) {
        //     if(this.cameraOptions[i]) {
        //       this.activeList.push({
        //         id: this.cameraOptions[i].id,
        //         name: this.cameraOptions[i].name,
        //       });
        //     }
        //   }
        // }

        await this.getCameraListData();
        await this.getListPageActives(1);
      },
      // 获取视频流并播放
      async getListPageActives(type) {
        this.activeList = [];
        let arr = [];
        let depArr = [];
        if (this.searchObj.groupIds.length > 0) {
          const flattenedArray = [].concat(...this.searchObj.groupIds);
          arr = [...new Set(flattenedArray)];
        }
        if (this.searchObj.departIds.length > 0) {
          const depdArray = [].concat(...this.searchObj.departIds);
          depArr = [...new Set(depdArray)];
        }
        let obj = {
          cameraGroupIds: arr,
          departIds: depArr,
          cameraIds: this.searchObj.cameraIds,
          running: '1',
          ...this.params,
        };
        const data = await listPage9(obj);
        this.activeList = data.data;

        /*
        let obj = {
          playType: this.playType,
          ...this.params,
        };
        const data = await getListPageActives(obj);
        this.activeList = data.data;
        */

        //console.log("activeList", this.activeList);

        // if (this.activeList.length > 0 && type == 1) {
        //   this.activeList.forEach((item, index) => {
        //     let obj = localStorage.getItem("playType" + item.id);
        //     if (obj != null) {
        //       let NewObj = JSON.parse(obj);
        //       if (NewObj.cameraId == item.id) {
        //         this.$refs["video" + index][0].playType = NewObj.playType;
        //       }
        //     } else {
        //       this.$refs["video" + index][0].playType = 0;
        //     }
        //     this.$refs["video" + index][0].cameraId = this.activeList[index].id;
        //     this.$refs["video" + index][0].getPlayUrl();
        //   });
        // }
        this.total = Number(data.count);
      },
      // 分页
      handleCurrentChange(val) {
        this.params.page = val;
        this.getListPageActives(1);
      },
      // async savePlayings(){
      //     let newArr = [];
      //     if(this.activeList.length>0){
      //         this.activeList.forEach((item)=>{
      //             if(item.id){
      //                 newArr.push(item.id)
      //             }
      //         })
      //     }
      //     playings({cameraIds:newArr}).then(res=>{

      //     }).catch(res => {
      //       clearInterval(this.timerPlayings);
      //       this.timerPlayings=''
      //     });;
      // },
      // 获取实时告警数据
      async getAlarms() {
        const data = await getAlarms();
        this.alarmsList = data.data;
      },
      // 获取告警数量
      async getCounter() {
        const data = await getCounter();
        this.alarmsCount = data.data;
      },
      // 创建链接
      connectWebsocket() {
        if (typeof WebSocket === 'undefined') {
          console.log(this.$t('applicationMonitoring.alarmmanagement.5n3r45'));
          return;
        } else {
          let cookie = Cookies.get('X-Token') || this.$route.query.token;
          let url = `${VUE_APP_WS_BASE_URL}/report/${cookie}`;
          // 打开一个
          this.websocket = new WebSocket(url);
          // 建立连接
          this.websocket.onopen = () => {
            console.log(this.$t('applicationMonitoring.alarmmanagement.861i43'));
          };
          // 客户端接收服务端返回的数据
          this.websocket.onmessage = (evt) => {
            console.log('evt', evt);
            const data = JSON.parse(evt.data);
            console.log('data_Data coming in', data);
            if (data.type == 'REPORT_SHOW') {
              if (this.VocieSwitch) {
                let audio = new Audio(
                  VUE_APP_API_BASE_URL + '/algorithm/sound/stream?id=' + data.algorithmId,
                );
                // 播放音频
                audio.play();
              }

              if (this.$store.state.alarmSwitch) {
                this.alarmVisible = false;
                clearTimeout(this.timer);
                this.$nextTick(() => {
                  this.alarmData = data;
                  this.alarmVisible = true;
                  this.timer = setTimeout(() => {
                    this.alarmVisible = false;
                  }, 5000);
                });
              }
            }
            this.getAlarms();
            this.getCounter();
          };
          // 发生错误时
          this.websocket.onerror = (evt) => {
            console.log(this.$t('applicationMonitoring.alarmmanagement.s4s787'), evt);
          };
          // 关闭连接
          this.websocket.onclose = (evt) => {
            console.log(this.$t('applicationMonitoring.alarmmanagement.2ygku1'), evt);
          };
        }
      },
      switchChange() {
        sessionStorage.setItem('VocieSwitch', this.VocieSwitch);
      },
      // 查看当日
      detailFun() {
        this.detailVisible = true;
      },
      // 关闭弹窗
      closeHandle() {
        this.detailVisible = false;
        this.getAlgorithms();
        this.getListPageActives(1);
        this.getAlarms();
        this.getCounter();
        this.getCameraListData();
        this.connectWebsocket();
      },
      // 告警详情
      clickDetail(item) {
        this.currentId = item.id;
        this.alarmDetailVisible = true;
      },
      closeFun() {
        this.currentId = '';
        this.alarmDetailVisible = false;
      },
      // 下拉列表选中摄像头
      changeSelectCamera(val) {
        if (val.length == 0) {
          this.cameraOptions = this.copyCameraList;
          return;
        }

        let newCameraOptions = [];
        this.copyCameraList.forEach((item) => {
          if (val.includes(item.id)) {
            newCameraOptions.push(item);
          }
        });
        this.cameraOptions = newCameraOptions;

        if (this.cameraOptions.length < this.params.limit) {
          this.activeList = [];
          this.cameraOptions.forEach((item) => {
            this.activeList.push({
              id: item.id,
              name: item.name,
            });
          });
        } else {
          for (let i = 0; i < this.params.limit; i++) {
            if (this.cameraOptions[i]) {
              this.activeList.push({
                id: this.cameraOptions[i].id,
                name: this.cameraOptions[i].name,
              });
            }
          }
        }
      },
      // 获取报告图片URL
      getReportImageUrl(id) {
        const token = Cookies.get('X-Token');
        return `${VUE_APP_API_BASE_URL}/report/streamThumb?id=${id}&X-Token=${token}&t=${new Date().getTime()}`;
      },
    },
    beforeDestroy() {
      // clearInterval(this.timerKeyframe);
      // this.timerKeyframe=''
      // clearInterval(this.timerPlayings);
      // this.timerPlayings=''
      // this.playerList.forEach((item) => {
      //   item.destroy();
      // });
      this.websocket?.close();
      // 移除窗口大小变化监听
      window.removeEventListener('resize', this.handleResize);
    },
  };
</script>
<style scoped lang="scss">
  .video-dialog {
    :deep(.el-dialog__body) {
      background: #f1f5fb;
    }
  }
  .wrap {
    display: flex;
    height: 100%;
    min-height: 100vh;
    box-sizing: border-box;

    .video-p {
      :deep(.el-pagination) {
        text-align: left !important;
      }
    }

    .flex-left {
      flex: 1;
      display: flex;
      flex-direction: column;

      .video-container {
        flex: 1;
        display: flex;
        flex-direction: column;
      }

      .video-box {
        width: 100%;
        background: rgba(47, 64, 86, 0.7);
        display: grid;
        overflow: hidden;
        .video-item {
          border: 1px #2f4056 solid;
          display: flex;
          align-items: center;
          position: relative;
          overflow: hidden; /* Prevent content overflow */

          .video {
            position: absolute;
          }
          .dropdown {
            position: absolute;
            right: 10px;
            top: 10px;
            z-index: 10;
          }
        }
      }
      .video1 {
        grid-template-columns: 100%;
        grid-template-rows: 100%;
      }
      .video4 {
        grid-template-columns: 50% 50%;
        grid-template-rows: 50% 50%;
      }
      .video6 {
        grid-template-columns: 33.33% 33.33% 33.33%;
        grid-template-rows: 50% 50% 50%;
      }
      .video9 {
        grid-template-columns: 33.33% 33.33% 33.33%;
        grid-template-rows: 33.33% 33.33% 33.33%;
      }
    }
    .flex-right {
      width: 300px;
      margin-left: 20px;
      flex-shrink: 0;

      .count {
        // font-size: 16px;
        // font-weight: bold;
        // display: flex;
        // align-items: center;
        // justify-content: center;
        // span {
        //   font-size: 24px;
        //   color: #f43838;
        // }
        position: relative;
        .text-cont {
          position: absolute;
          top: 0px;
          color: #fff;
          font-size: 12px;
          .alarm-num {
            font-family: yousheheibiao;
            font-weight: bold;
            text-align: center;
            margin-top: 5px;
          }
        }
      }
      .scrollbar-wrapper {
        height: calc(100vh - 265px);
        overflow: hidden !important;
      }
      .list {
        li {
          border: 1px #ccc solid;
          border-radius: 4px;
          margin-top: 20px;
          .img {
            width: 100%;
            height: 150px;
          }
        }
      }
    }
  }
</style>
