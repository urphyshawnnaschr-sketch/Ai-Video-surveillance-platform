<template>
  <div class="card-container">
    <div class="card-box">
      <div class="center">
        <div
          @click="detailFun"
          class="img-content"
          :style="{
            overflow: 'hidden',
            width: ratio == 1 ? '235px !important' : '',
            height: ratio == 1 ? '132px !important' : '',
          }"
        >
          <div class="image-wrapper">
            <MarkDetail
              :fileUrl="fileUrl"
              :dataList="JSON.parse(JSON.stringify(dataList))"
              :ratio="ratio"
              :Index="index"
              :dataListAll="roiList"
            />
            <!-- 状态标签 - 右上角 -->
            <div class="status-tag status-tag-top-right">
              <span 
                v-if="alarmData.auditResult == 0 || alarmData.auditResult == '0'" 
                class="status-badge status-unprocessed"
              >
                {{ $t('applicationMonitoring.alarmmanagement.unprocessed') }}
              </span>
              <span 
                v-if="alarmData.auditResult == 1 || alarmData.auditResult == '1'" 
                class="status-badge status-processed"
              >
                {{ $t('applicationMonitoring.alarmmanagement.processed') }}
              </span>
              <span 
                v-if="alarmData.auditResult == 2 || alarmData.auditResult == '2'" 
                class="status-badge status-closed"
              >
                {{ $t('applicationMonitoring.alarmmanagement.closed') }}
              </span>
            </div>
            <!-- 事件类型标签 - 左下角 -->
            <div class="event-type-tag" v-if="alarmData.algorithmName">
              <span class="event-type-badge">{{ alarmData.algorithmName }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="footer">
        <div class="footer-info">
          <!-- 时间信息 -->
          <div class="info-item">
            <i class="el-icon-time"></i>
            <span class="info-text">{{ alarmData.createdStr }}</span>
          </div>
          <!-- 位置信息 -->
          <div class="info-item" v-if="alarmData.cameraName">
            <i class="el-icon-location-outline"></i>
            <span class="info-text">{{ alarmData.cameraName }}</span>
          </div>
        </div>
      </div>
    </div>

    <AlarmDetail
      :alarmId="alarmData.id"
      :cameraId="alarmData.cameraId"
      :params="params"
      :algorithmId="alarmData.algorithmId"
      v-if="alarmDetailVisible"
      @close="closeFun"
    />
  </div>
</template>
<script>
  import ElImageViewer from 'element-ui/packages/image/src/image-viewer';

  import AlarmDetail from '@/components/applicationMonitoring/alarmManagement/alarmDetail/newDetail';
  import MarkResult from '@/components/markResult';
  import store from '@/store';

  import MarkDetail from './markDetail.vue';

  export default {
    components: {
      MarkResult,
      ElImageViewer,
      AlarmDetail,
      MarkDetail,
    },
    props: {
      fileUrl: {
        type: String,
        default: '',
      },
      dataList: {
        type: Array,
        default: () => [],
      },
      alarmData: {
        type: Object,
        default: () => {},
      },
      ratio: {
        type: Number,
        default: 0.5,
      },
      isAlarm: {
        type: Boolean,
        default: false,
      },
      index: {
        type: Number,
        default: null,
      },
      params: {
        type: Object,
        default: () => {},
      },
    },
    mounted() {
      this.isEnglishMode = this.isEnglish();
    },
    data() {
      return {
        isBigImg: false,
        imgRatio: 0.5,
        VUE_APP_API_BASE_URL,
        imgHeight: '',
        pointList: [],
        alarmDetailVisible: false,
        currentId: '',
        isShowTooltip: false,
        isShowPics: false,
        roiList: [],
        isEnglishMode: false,
      };
    },

    methods: {
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
      // 查看大图
      bigImgFun() {
        this.isShowPics = true;
      },
      closeViewer() {
        this.isShowPics = false;
      },
      detailFun() {
        if (this.$parent.websocket) {
          this.$parent.websocket.close();
        }
        this.$parent.websocketAutoReconnect = false;
        this.alarmDetailVisible = true;
      },
      closeFun() {
        this.alarmDetailVisible = false;
        // this.$parent.websocketAutoReconnect = true;
        // this.$parent.connectWebsocket();
      },
    },
  };
</script>
<style scoped>
  .card-container {
    height: 100%;
  }
  .card-box {
    box-sizing: border-box;
    padding: 0;
    display: flex;
    flex-direction: column;
    height: 100%;
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    border-radius: 14px;
    border: 1px solid #F3F4F6;
    box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
  }

  .center {
    flex: 1;
    min-height: 160px;
    overflow: hidden;
    /* border: 1px solid #E5E7EB; */
    border-radius: 8px 8px 0 0;
    position: relative;
    background: #fff;
  }

  .img-content {
    cursor: pointer;
    position: relative;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .image-wrapper {
      position: relative;
      width: 100%;
      height: 100%;
    }
    
   
    .status-tag-top-right {
      position: absolute;
      top: 8px;
      right: 8px;
      z-index: 10;
      
      .status-badge {
        display: inline-block;
        padding: 4px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;
        line-height: 1.5;
        color: #fff;
        white-space: nowrap;
        
        &.status-unprocessed {
          background: linear-gradient(90deg, #2B7FFF 0%, #E53935 100%);
          box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.10), 0 2px 4px -2px rgba(0, 0, 0, 0.10);
        }
        
        &.status-processed {
          background: linear-gradient(90deg, #00C950 0%, #00A63E 100%);
          box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.10), 0 2px 4px -2px rgba(0, 0, 0, 0.10);
        }
        
        &.status-closed {
          background: linear-gradient(90deg, #99A1AF 0%, #6A7282 100%);
          box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.10), 0 2px 4px -2px rgba(0, 0, 0, 0.10);
        }
      }
    }
    
  
    .event-type-tag {
      position: absolute;
      bottom: 8px;
      left: 8px;
      z-index: 10;
      
      .event-type-badge {
        display: inline-block;
        padding: 6px 12px;
        font-size: 12px;
        font-weight: 500;
        line-height: 1.5;
        color: #E53935;
        white-space: nowrap;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.95);
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.10), 0 2px 4px -2px rgba(0, 0, 0, 0.10);
      }
    }
  }

  .footer {
    padding: 12px 16px;
    background: #fff;
    border-top: none;
    border-radius: 0 0 8px 8px;
  }

  .footer-info {
    display: flex;
    flex-direction: column;
    gap: 8px;
    
    .info-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: #606266;
      line-height: 1.5;
      
      i {
        font-size: 14px;
        color: #2B7FFF;
        flex-shrink: 0;
      }
      
      .info-text {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        color: #4A5565;
      }
    }
  }
</style>
