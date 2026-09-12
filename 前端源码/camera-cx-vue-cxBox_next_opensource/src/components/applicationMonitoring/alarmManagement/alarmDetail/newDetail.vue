<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('alarmdetail.index.3r91n2')"
      :visible.sync="dialogVisible"
      width="900px"
      top="5vh"
      :append-to-body="true"
      @closed="closed"
      class="dialog-pad"
    >
      <div v-loading="loading">
        <div style="padding: 30px 20px;">
          <div style="position: relative" v-if="!loading">
          <div class="left-icon" @click="switchFun(0)">
            <img src="@/assets/images/left-arrow.png" class="arrow-icon" />
          </div>
          <div class="right-icon" @click="switchFun(1)">
            <img src="@/assets/images/right-arrow.png" class="arrow-icon" />
          </div>
          <MarkDetail
            v-if="alarmData.id"
            :fileUrl="$common.handleStream(alarmData.id)"
            :dataList="handleParams(alarmData.params)"
            :ratio="ratio"
            pageType="detail"
            :dataListAll="roiList"
          />
        </div>

        <!-- <div class="show-img-btn" v-if="hasVideoUrl">
          <el-button type="primary" plain @click="handleVideoPlay"
            >{{$t('alarmdetail.newdetail.720ol9')}}</el-button
          >
        </div> -->

        <div class="alarm-cont">
          <div style="display: flex; justify-content: space-between; align-items: center">
            <div class="alarm-title" style="display: flex; align-items: center">
              {{ $t('alarmdetail.newdetail.9u8858')
              }}<el-tag
                v-if="alarmData.auditResult == 0"
                color="#00BC7D"
                effect="dark"
                size="mini"
                class="audit-blue-etag"
                >{{ $t('alarmdetail.newdetail.694hl5') }}</el-tag
              >
              <el-tag
                v-if="alarmData.auditResult == 1"
                color="#00BC7D"
                effect="dark"
                size="mini"
                class="audit-blue-etag"
                >{{ $t('alarmdetail.newdetail.v393b6') }}</el-tag
              >
              <el-tag
                v-if="alarmData.auditResult == 2"
                color="#00BC7D"
                effect="dark"
                size="mini"
                class="audit-blue-etag"
                >{{ $t('alarmdetail.newdetail.1op9d6') }}</el-tag
              >
              <el-tag
                v-if="alarmData.auditResult == 3"
                color="#00BC7D"
                effect="dark"
                size="mini"
                class="audit-blue-etag"
                >{{ $t('alarmdetail.newdetail.g3rtcg') }}</el-tag
              >
              <el-tag
                v-if="alarmData.pushed == 0"
                color="#45556C"
                effect="dark"
                size="mini"
                class="audit-dark-etag"
                >{{ $t('alarmdetail.newdetail.74s25y') }}</el-tag
              >
              <el-tag
                v-if="alarmData.pushed == 1"
                color="#45556C"
                effect="dark"
                size="mini"
                class="audit-dark-etag"
                >{{ $t('alarmdetail.newdetail.3lr5qo') }}</el-tag
              >
              <template v-if="alarmData.pushed == 2">
                <el-tooltip
                  class="item"
                  effect="dark"
                  :content="alarmData.pushMsg"
                  placement="left-end"
                >
                  <el-tag color="#45556C" effect="dark" size="mini" class="audit-dark-etag">{{
                    $t('alarmdetail.newdetail.p08cv2')
                  }}</el-tag>
                </el-tooltip>
              </template>
            </div>
            <div class="show-img-btn" style="display: flex">
              <!-- <el-button plain @click="bigImgFun">{{
                $t('alarmdetail.newdetail.vf5q31')
              }}</el-button> -->
              <div class="btn-sty" @click="bigImgFun">
                <i class="el-icon-picture-outline"></i>
                <span>{{$t('alarmdetail.newdetail.vf5q31')}}</span>
              </div>
              <el-image-viewer
                v-if="isShowPics"
                style="z-index: 9999 !important"
                :on-close="closeViewer"
                :url-list="[$common.handleStream(alarmData.id)]"
              />
              <!-- <el-button type="primary" plain @click="handleVideoPlay" v-if="hasVideoUrl">{{
                $t('alarmdetail.newdetail.e2871x')
              }}</el-button>
              <el-button @click="videoFun(alarmData.cameraId)">{{
                
              }}</el-button> -->
              <div class="btn-sty"  @click="handleVideoPlay" v-if="hasVideoUrl" >
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 16 16" fill="none">
                  <path d="M3.3335 3.33324C3.33343 3.09864 3.39526 2.86816 3.51275 2.6651C3.63024 2.46204 3.79924 2.29357 4.00266 2.1767C4.20609 2.05984 4.43675 1.99872 4.67136 1.99952C4.90596 2.00032 5.1362 2.063 5.33883 2.18124L13.3368 6.84657C13.5387 6.96369 13.7062 7.13174 13.8228 7.33391C13.9393 7.53608 14.0008 7.7653 14.001 7.99867C14.0012 8.23203 13.9401 8.46135 13.8239 8.66373C13.7077 8.8661 13.5405 9.03444 13.3388 9.15191L5.33883 13.8186C5.1362 13.9368 4.90596 13.9995 4.67136 14.0003C4.43675 14.0011 4.20609 13.94 4.00266 13.8231C3.79924 13.7062 3.63024 13.5378 3.51275 13.3347C3.39526 13.1317 3.33343 12.9012 3.3335 12.6666V3.33324Z" stroke="#E53935" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>{{$t('alarmdetail.newdetail.e2871x')}}</span>
              </div>
              <div class="btn-sty" @click="videoFun(alarmData.cameraId)">
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 16 16" fill="none">
                  <path d="M10.6665 8.66652L14.1485 10.9879C14.1987 11.0213 14.257 11.0404 14.3172 11.0433C14.3775 11.0462 14.4373 11.0327 14.4905 11.0042C14.5437 10.9758 14.5881 10.9334 14.6191 10.8817C14.6501 10.83 14.6665 10.7708 14.6665 10.7105V5.24652C14.6665 5.18787 14.6511 5.13025 14.6217 5.07948C14.5923 5.02872 14.5501 4.9866 14.4992 4.95738C14.4484 4.92817 14.3907 4.91289 14.332 4.91309C14.2734 4.91329 14.2158 4.92896 14.1652 4.95852L10.6665 6.99985" stroke="#E53935" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M9.3335 4H2.66683C1.93045 4 1.3335 4.59695 1.3335 5.33333V10.6667C1.3335 11.403 1.93045 12 2.66683 12H9.3335C10.0699 12 10.6668 11.403 10.6668 10.6667V5.33333C10.6668 4.59695 10.0699 4 9.3335 4Z" stroke="#E53935" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>{{$t('alarmdetail.newdetail.i13usk')}}</span>
              </div>
            </div>
          </div>
          <el-row :gutter="24" class="cont-sty">
            <el-col :span="12">
              <div class="grid-content bg-purple">
                {{ `${$t('common.alarmType')}：${alarmData.algorithmName}` }}
              </div>
            </el-col>
            <el-col :span="12">
              <div class="grid-content bg-purple">
                {{ $t('alarmdetail.newdetail.fh32u0') }}{{ alarmData.cameraName }}
                <el-tag>{{ alarmData.boxName }}</el-tag>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="grid-content bg-purple">
                {{ $t('faceControl.faceRecognition.belongingGroup') }}：
                {{ alarmData.groupNames }}
              </div>
            </el-col>
            <template
              v-if="
                alarmData.auditResult == 1 ||
                alarmData.auditResult == 2 ||
                alarmData.auditResult == 3
              "
            >
              <el-col :span="12">
                <div class="grid-content bg-purple">
                  {{ $t('common.alarmTime') }}： {{ alarmData.createdStr }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                  {{ $t('alarmdetail.newdetail.r67jk7') }}{{ alarmData.auditTimeLen }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                  {{ $t('alarmdetail.newdetail.szfv73') }}{{ formatAuditTime() }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                  {{ $t('common.remark') }}： {{ alarmData.auditRemark }}
                </div>
              </el-col>
            </template>
          </el-row>
          <div class="remark-sty">
            <div style="display: flex; align-items: center;gap: 10px;">
                  <el-button type="primary" class="btn mr10" icon="el-icon-circle-check" @click="markReportClick('1')"
                    >{{ $t('applicationMonitoring.alarmmanagement.positive') }}</el-button
                  >
                  <el-popover placement="top" width="160" v-model="visible">
                    <p>false alarm??</p>
                    <div style="text-align: right; margin: 0">
                      <el-button size="mini" type="text" @click="visible = false">Cancel</el-button>
                      <el-button type="primary" size="mini" @click="markReportClick('2')"
                        >OK</el-button
                      >
                    </div>
                    <el-button slot="reference" type="danger" icon="el-icon-circle-close" class="btn">{{$t('applicationMonitoring.alarmmanagement.False')}}</el-button>
                  </el-popover>
                  <div
                    :class="
                      alarmData.mark === 0
                        ? 'blackCol'
                        : alarmData.mark === 1
                          ? 'greenCol'
                          : 'redCol'
                    "
                    style="display: flex; align-items: center"
                  >
                    {{ alarmData.mark === 0 ? $t('applicationMonitoring.alarmmanagement.bePending')  : alarmData.mark === 1 ?  $t('applicationMonitoring.alarmmanagement.positive') :  $t('applicationMonitoring.alarmmanagement.False') }}
                  </div>
            </div>
            <div v-if="alarmData.auditResult == 0">
              <div class="grid-content1 bg-purple1" style="display: flex; align-items: center">
                <span style="width: 60px">{{ $t('common.remark') }}： </span
                ><el-input
                  v-model="auditRemark"
                  :placeholder="$t('alarmdetail.newdetail.uxx7hx')"
                ></el-input>
              </div>
            </div>
          </div>
        </div>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button class="footer-btn footer-btn-left" @click="handleAddCamera">
            {{ $t('alarmdetail.newdetail.h3lht6') }}
          </el-button>
          <div class="footer-buttons-right">
            <el-button 
              type="primary" 
              class="footer-btn footer-btn-primary" 
              @click="handleAuditOk" 
              :disabled="!mustAudit"
            >
              {{ $t('alarmdetail.newdetail.bw7kj4') }}
            </el-button>
            <el-button 
              class="footer-btn footer-btn-default" 
              @click="handleAuditClose" 
              :disabled="!mustAudit"
            >
              {{ $t('alarmdetail.newdetail.wsvh4z') }}
            </el-button>
            <el-button
              class="footer-btn footer-btn-default"
              @click="haneleDataPush"
              :disabled="alarmData.auditResult === '3'"
            >
              {{ $t('alarmdetail.newdetail.42ai1w') }}
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <AddCamera
      :currentId="currentId"
      :currentData="currentNode"
      v-if="addCameraVisible"
      @close="addCameraVisible = false"
    />

    <AlarmVideo :videoUrl="videoUrl" v-if="videoUrlVisible" @close="videoUrlVisible = false" />
    <el-dialog
      width="40%"
      :title="$t('alarmdetail.newdetail.720ol9')"
      :visible.sync="videoVisible"
      append-to-body
    >
      <Video v-if="videoVisible" :Id="cameraId" />
    </el-dialog>
    <level-push-dialog
      v-if="showLevelPushDialog"
      :visible.sync="showLevelPushDialog"
      :camera-id="cameraId"
      :algorithm-id="algorithmId"
      :report-id="alarmId"
      @confirmed="showLevelPushDialog = false"
    >
    </level-push-dialog>
  </div>
</template>

<script>
  import {
    saveAuditAndPush,
    saveAuditOk,
    saveDataPush,
    saveAuditClose,
    getReportMust,
    DetailInfo,
    nearlyData,
    markReportSave,
  } from '@/api/applicationMonitoring/alarmManagement';
  import axios from 'axios';
  import ElImageViewer from 'element-ui/packages/image/src/image-viewer';
  import Cookies from 'js-cookie';

  import AddCamera from '@/components/applicationMonitoring/boxManagement/addCamera/newAdd.vue';
  import MarkResult from '@/components/markResult';

  import AlarmVideo from '../alarmVideo.vue';
  import MarkDetail from '../markDetail.vue';
  import LevelPushDialog from './LevelPushDialog.vue';
  import Video from './video.vue';

  export default {
    components: {
      MarkResult,
      ElImageViewer,
      MarkDetail,
      AddCamera,
      AlarmVideo,
      Video,
      LevelPushDialog,
    },
    props: {
      // dataList: {
      //   type: Array,
      //   default: () => [],
      // },
      alarmId: {
        type: String,
        default: '',
      },
      algorithmId: {
        type: String,
        default: '',
      },
      cameraId: {
        type: String,
        default: '',
      },
      params: {
        type: Object,
        default: () => {},
      },
      // originalUrl: {
      //   type: String,
      //   default: "",
      // },
      // Index: {
      //   type: Number,
      //   default: null,
      // },
      // dataListAll: {
      //   type: Array,
      //   default: () => [],
      // },
      // fileUrl: {
      //   type: String,
      //   default: "",
      // },
    },
    data() {
      return {
        showLevelPushDialog: false,
        dialogVisible: true,
        isShowPics: false,
        ratio: 1,
        addCameraVisible: false,
        currentId: '',
        currentNode: {},
        hasVideoUrl: false,
        videoUrlVisible: false,
        mustAudit: false,
        mustPush: false,
        loading: false,
        alarmData: {},
        roiList: [],
        // 当前的告警数据ID
        currentAlarmId: '',
        auditRemark: '',
        videoVisible: false,
        cameraId: '',
        visible: false,
      };
    },
    created() {
      // this.mustAudit = this.alarmData.mustAudit;
      // this.mustPush = this.alarmData.mustPush;
      this.currentAlarmId = this.alarmId || '0';
      this.getDetail();
      //this.fetchReportMust();
      //this.fetchVideoUrl();

      //console.log('detail page params', this.params);
    },
    destroyed() {},
    methods: {
      // 获取详情数据
      getDetail() {
        this.loading = true;
        DetailInfo({ id: this.currentAlarmId })
          .then((res) => {
            const params = res.data;
            this.alarmData = params;
            console.log('this.alarmData', this.alarmData);
            this.roiList = [];
            if (params.rois) {
              let roisArr = JSON.parse(params.rois);
              let newArr = [];
              roisArr.forEach((item) => {
                newArr.push({
                  pointList: item,
                });
              });
              if (params.lines) {
                let lArr = JSON.parse(params.lines);
                lArr.forEach((item) => {
                  newArr.push({
                    pointList: item,
                  });
                });
              }
              let obj = {
                markPoints: newArr,
              };
              this.roiList.push(obj);
            }
            setTimeout(() => {
              this.loading = false;
            }, 300);
          })
          .catch(() => {
            this.loading = false;
          });

        // 查询处理状态和推送状态
        this.fetchReportMust();

        // 查询录像
        this.fetchVideoUrl();
      },
      // 切换数据
      switchFun(type) {
        // let obj = {
        //   id: this.currentAlarmId,
        //   type:type
        // }

        let query = { ...this.params, id: this.currentAlarmId, type: type };
        console.log('query', query);
        if (this.params != undefined && JSON.stringify(this.params) != '{}') {
          // 处理部门参数
          let departIds = [];
          if (this.params.departIds && this.params.departIds.length > 0) {
            this.params.departIds.forEach((item) => {
              let len = item.length - 1;
              departIds.push(item[len]);
            });
          }
          query.departIds = departIds;
        }
        nearlyData(query).then((res) => {
          if (res.data == 0) {
            this.$message({
              type: 'warning',
              message: $t('common.noData'),
            });
          } else {
            this.currentAlarmId = res.data;
            this.getDetail();
          }
        });
      },
      handleParams(params) {
        try {
          return JSON.parse(params);
        } catch (err) {
          return [];
        }
      },
      // 关闭回调
      closed() {
        this.$emit('close');
      },
      bigImgFun() {
        this.isShowPics = true;
      },
      closeViewer() {
        this.isShowPics = false;
      },
      handleAddCamera() {
        this.currentId = this.alarmData.cameraId;
        this.addCameraVisible = true;
      },
      fetchVideoUrl() {
        let that = this;
        axios({
          url: VUE_APP_API_BASE_URL + '/record/play?reportId=' + this.currentAlarmId,
          method: 'get', //默认方法，可以不写
          headers: {
            'X-Token': Cookies.get('X-Token'),
          },
        }).then((res) => {
          if (res.data.code === 0 && res.data.data != '') {
            that.videoUrl = res.data.data;
            that.hasVideoUrl = true;
          }
        });
      },
      handleVideoPlay() {
        this.videoUrlVisible = true;
      },
      /** 处理告警并推送 */
      handleAuditAndPush() {
        this.$confirm(
          this.$t('alarmdetail.newdetail.04dkv8'),
          this.$t('alarmdetail.newdetail.363ukv'),
          {
            distinguishCancelAndClose: true,
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
          },
        ).then(() => {
          this.loading = true;
          saveAuditAndPush({ id: this.currentAlarmId })
            .then((res) => {
              this.mustAudit = false;
              this.$message({
                type: 'success',
                message: $t('common.action', { text: $t('common.success') }),
              });
            })
            .catch((err) => {
              this.loading = false;
            });
        });
      },
      /** 处理告警 */
      handleAuditOk() {
        this.$confirm(
          this.$t('alarmdetail.newdetail.6q58qn'),
          this.$t('alarmdetail.newdetail.363ukv'),
          {
            distinguishCancelAndClose: true,
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
          },
        ).then(() => {
          this.loading = true;
          saveAuditOk({ id: this.currentAlarmId, auditRemark: this.auditRemark })
            .then((res) => {
              this.loading = false;
              this.mustAudit = false;
              this.$message({
                type: 'success',
                message: $t('common.action', { text: $t('common.success') }),
              });
              this.auditRemark = '';
              this.getDetail();
            })
            .catch((err) => {
              this.loading = false;
              this.auditRemark = '';
            });
        });
      },
      /** 推送告警 */
      haneleDataPush() {
        //this.showLevelPushDialog = true;
        this.$confirm(
          this.$t('alarmdetail.newdetail.8cbo4w'),
          this.$t('alarmdetail.newdetail.363ukv'),
          {
            distinguishCancelAndClose: true,
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
          },
        ).then(() => {
          this.loading = true;
          saveDataPush({ id: this.currentAlarmId })
            .then((res) => {
              this.loading = false;
              this.mustAudit = false;
              this.$message({
                type: 'success',
                message: $t('common.action', { text: $t('common.success') }),
              });
              this.auditRemark = '';
              this.getDetail();
            })
            .catch((err) => {
              this.loading = false;
              this.auditRemark = '';
              this.getDetail();
            });
        });
      },
      /** 关闭告警 */
      handleAuditClose() {
        this.$confirm(
          this.$t('alarmdetail.newdetail.in74g8'),
          this.$t('alarmdetail.newdetail.363ukv'),
          {
            distinguishCancelAndClose: true,
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
          },
        ).then(() => {
          this.loading = true;
          saveAuditClose({
            id: this.currentAlarmId,
            auditRemark: this.auditRemark,
          })
            .then((res) => {
              this.loading = false;
              this.mustAudit = false;
              this.$message({
                type: 'success',
                message: $t('common.action', { text: $t('common.success') }),
              });
              this.auditRemark = '';
              this.getDetail();
            })
            .catch((err) => {
              this.loading = false;
              this.auditRemark = '';
            });
        });
      },
      /** 获取告警处理状态和推送状态 */
      fetchReportMust() {
        ///console.log('must..', this.currentAlarmId);
        getReportMust({ id: this.currentAlarmId }).then((res) => {
          // console.log('must res..', res);
          this.mustAudit = res.data.mustAudit;
          this.mustPush = res.data.mustPush;
        });
      },
      /** 格式化处理时间 */
      formatAuditTime() {
        if (this.alarmData && this.alarmData.auditAt) {
          return this.$moment(new Date(this.alarmData.auditAt)).format('YYYY-MM-DD HH:mm:ss');
        }
        return '';
      },
      // 查看视频
      videoFun(id) {
        this.cameraId = id;
        this.videoVisible = true;
      },
      markReportClick(val) {
        let mark = '';
        if (val === '1') {
          mark = '1';
        } else {
          mark = '2';
        }
        const obj = {
          id: this.currentAlarmId,
          mark: mark,
        };
        this.loading = true;
        markReportSave(obj)
          .then((res) => {
            this.loading = false;
            this.$message({
              type: 'success',
              message: $t('common.action', { text: $t('common.success') }),
            });
            this.visible = false;
            this.getDetail();
          })
          .catch((err) => {
            this.loading = false;
          });
      },
    },
  };
</script>
<style scoped lang="scss">
  
    .dialog-pad{
      :deep(.el-dialog__body){
        padding: 0px !important;
      }
    }
  .alarm-cont {
    margin-top: 10px;
    .title {
      font-size: 13px;
      font-weight: bold;
      line-height: 24px;
    }
    .big-btn {
      font-size: 13px;
      font-weight: bold;
      line-height: 24px;
      color: #1e9fff;
      cursor: pointer;
    }
    .tip {
      font-size: 12px;
      line-height: 20px;
      color: #1A0808;
    }
  }
  .left-icon {
    position: absolute;
    z-index: 9;
    top: 50%;
    left: 20px;
    transform: translate(0, -50%);
    cursor: pointer;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #E53935;
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.10), 0 4px 6px -4px rgba(0, 0, 0, 0.10);
  }
  .right-icon {
    position: absolute;
    z-index: 9;
    top: 50%;
    right: 20px;
    transform: translate(0, -50%);
    cursor: pointer;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #E53935;
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.10), 0 4px 6px -4px rgba(0, 0, 0, 0.10);
  }
  .left-icon:hover,
  .right-icon:hover {
    background: #E53935;
  }
  .arrow-icon {
    width: 12px;
    height: 12px;
    margin-top: 50%;
    margin-left: 50%;
    transform: translate(-50%, -50%);
  }
  .show-img-btn {
    // margin-top: 10px;
    display: flex;
    align-items: center;
    gap: 5px;
    .btn-sty{
      border-radius: 10px;
      border: 1px solid #8EC5FF;
      background: #FFF;
      display: flex;
      padding: 3px 8px;
      justify-content: center;
      align-items: center;
      gap: 8px;
      color: #E53935;
      font-size: 12px;
      font-style: normal;
      font-weight: 400;
      line-height: 24px;
      cursor: pointer;
    }
  }

  .alarm-title {
    font-size: 16px;
    font-weight: bold;
    margin: 20px 0;
  }

  .el-row {
    margin-bottom: 20px;
    margin-left: 0px !important;
    margin-right: 0px !important;
    &:last-child {
      margin-bottom: 0;
    }
  }
  .el-col {
    margin-bottom: 15px;
    padding: 0px !important;
  }

  .bg-purple-light {
    background: #e5e9f2;
  }
  .grid-content {
    min-height: 36px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
  .audit-blue-etag {
    width: 60px;
    height: 24px;
    padding: 0px;
    line-height: 24px;
    font-size: 12px;
    text-align: center;
    margin-left: 15px;
    border: none !important;
    border-radius: 20px;
  }
  .audit-dark-etag {
    width: 60px;
    height: 24px;
    text-align: center;
    padding: 0px;
    border: none !important;
    margin-left: 15px;
    font-size: 12px;
    line-height: 24px;
    background: #303133;
    color: white;
    border-radius: 20px;
  }
  .mr10 {
    margin-right: 10px;
  }
  .btn {
    border-radius: 5px !important;
    border-radius: 10px !important;
    border: 1px solid #CAD5E2 !important;
    background: #FFF !important;
    color: #314158 !important;
    font-weight: normal !important;
  }
  .blackCol {
    margin-left: 10px;
    color: black;
    font-weight: bold;
  }
  .greenCol {
    margin-left: 10px;
    color: green;
    font-weight: bold;
  }
  .redCol {
    margin-left: 10px;
    color: red;
    font-weight: bold;
  }
  .cont-sty{
    padding: 24px 24px 0 24px;
    border-radius: 10px;
    background: linear-gradient(135deg, #F8FAFC 0%, #EFF6FF 100%);
  }
  .remark-sty{
    display: flex;
    padding-top: 25px;
    gap: 16px;
    align-items: flex-start;
    flex-shrink: 0;
    align-self: stretch;
    border-top: 1px solid #E2E8F0;
  }
  .dialog-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    border-top: 1px solid #E2E8F0;
    background: #F8FAFC;
    
    .footer-btn {
      border-radius: 8px !important;
      font-size: 14px;
      padding: 8px 16px;
      height: 36px;
      line-height: 20px;
      font-weight: 400;
      
      &.footer-btn-left {
        background: #FFF;
        border: 1px solid #DCDFE6;
        color: #606266;
        
        &:hover {
          background: #F5F7FA;
          border-color: #C0C4CC;
          color: #606266;
        }
      }
      
      &.footer-btn-primary {
        background: #E53935;
        border: none;
        color: #FFF;
        box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
        
        &:hover {
          background: #E53935;
          color: #FFF;
        }
        
        &:disabled {
          background: #C0C4CC;
          color: #FFF;
          cursor: not-allowed;
          box-shadow: none;
        }
      }
      
      &.footer-btn-default {
        background: #FFF;
        border: 1px solid #DCDFE6;
        color: #606266;
        
        &:hover {
          background: #F5F7FA;
          border-color: #C0C4CC;
          color: #606266;
        }
        
        &:disabled {
          background: #F5F7FA;
          color: #C0C4CC;
          border-color: #E4E7ED;
          cursor: not-allowed;
        }
      }
    }
    
    .footer-buttons-right {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }
  
</style>
