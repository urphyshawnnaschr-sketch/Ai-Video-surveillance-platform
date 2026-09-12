<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :visible.sync="dialogVisible"
      width="1500px"
      top="5vh"
      append-to-body
      @closed="closed"
      class="camera-dialog-wrapper"
    >
    <div slot="title" style="color: #303133;font-size: 18px;">
      {{ $t('common.edit', { text: $t('common.camera') }) }}
      <span class="tip-txt">{{ $t('addcamera.newadd.45c40y') }}</span>
    </div>
      <div ref="dialogCont">
        <el-form
          :model="detail"
          :rules="rules"
          ref="detailForm"
          label-width="130px"
          class="camera-dialog"
        >
          <div class="camera-left">
            <el-form-item :label="$t('common.cameraName')" prop="name">
              <el-input
                :placeholder="$t('addcamera.newadd.3ovcud')"
                v-model="detail.name"
              ></el-input>
            </el-form-item>
            <el-form-item prop="rtspUrl">
              <template slot="label">
                <span style="margin-right: 5px">{{ $t('addcamera.index.3j841d') }}</span>
                <el-popover placement="top-start" width="400" trigger="hover">
                  <div style="font-size: 13px; color: 606266">
                    <div
                      style="
                        font-weight: bold;
                        font-size: 16px;
                        color: #303133;
                        line-height: 26px;
                        margin-bottom: 10px;
                      "
                    >
                      {{ $t('addcamera.newadd.18o158') }}
                    </div>
                    <div style="line-height: 22px">
                      {{ $t('addcamera.newadd.fss11n') }}
                    </div>
                    <div style="font-weight: bold; line-height: 22px; margin: 10px 0px">
                      {{ $t('addcamera.newadd.3jp424') }}
                    </div>
                    <div style="line-height: 22px">
                      {{ $t('addcamera.newadd.7ojr2w') }}
                    </div>
                    <div style="line-height: 22px; margin: 20px 0px">
                      <div style="font-weight: bold">
                        {{ $t('addcamera.newadd.ic8um4') }}
                      </div>
                      <div> rtsp://admin:passwor@192.168.1.203:554/Streaming/Channels/101 </div>
                      <div style="font-weight: bold">
                        {{ $t('addcamera.newadd.2eyjko') }}
                      </div>
                      <div>
                        rtsp://admin:admin@192.168.1.118:554/cam/realmonitor?channel=1&subtype=0
                      </div>
                      <div style="font-weight: bold">
                        {{ $t('addcamera.newadd.8f8447') }}
                      </div>
                      <div>rtsp://admin:admin@192.168.1.70:554/video1</div>
                    </div>
                    <div style="font-weight: bold; line-height: 22px">
                      {{ $t('addcamera.newadd.43308h') }}
                    </div>
                  </div>
                  <span class="el-icon-question" slot="reference"></span>
                </el-popover>
              </template>
              <el-input
                :placeholder="$t('addcamera.newadd.heg6k1')"
                v-model="detail.rtspUrl"
                type="textarea"
                :rows="2"
              ></el-input>
            </el-form-item>
            <!-- <el-form-item prop="rtspUrl2">
              <template slot="label">
                <span style="margin-right: 5px">{{ $t('addcamera.index.streamAddress2') }}</span>
                <el-popover placement="top-start" width="400" trigger="hover">
                  <div style="font-size: 13px; color: 606266">
                    <div
                      style="
                        font-weight: bold;
                        font-size: 16px;
                        color: #303133;
                        line-height: 26px;
                        margin-bottom: 10px;
                      "
                    >
                      {{ $t('addcamera.newadd.18o158') }}
                    </div>
                    <div style="line-height: 22px">
                      {{ $t('addcamera.newadd.fss11n') }}
                    </div>
                    <div style="font-weight: bold; line-height: 22px; margin: 10px 0px">
                      {{ $t('addcamera.newadd.3jp424') }}
                    </div>
                    <div style="line-height: 22px">
                      {{ $t('addcamera.newadd.7ojr2w') }}
                    </div>
                    <div style="line-height: 22px; margin: 20px 0px">
                      <div style="font-weight: bold">
                        {{ $t('addcamera.newadd.ic8um4') }}
                      </div>
                      <div> rtsp://admin:passwor@192.168.1.203:554/Streaming/Channels/101 </div>
                      <div style="font-weight: bold">
                        {{ $t('addcamera.newadd.2eyjko') }}
                      </div>
                      <div>
                        rtsp://admin:admin@192.168.1.118:554/cam/realmonitor?channel=1&subtype=0
                      </div>
                      <div style="font-weight: bold">
                        {{ $t('addcamera.newadd.8f8447') }}
                      </div>
                      <div>rtsp://admin:admin@192.168.1.70:554/video1</div>
                    </div>
                    <div style="font-weight: bold; line-height: 22px">
                      {{ $t('addcamera.newadd.43308h') }}
                    </div>
                  </div>
                  <span class="el-icon-question" slot="reference"></span>
                </el-popover>
              </template>
              <el-input
                :placeholder="$t('addcamera.newadd.heg6k1')"
                v-model="detail.rtspUrl2"
                type="textarea"
                :rows="2"
              ></el-input>
            </el-form-item> -->
            <!-- <el-form-item :label="$t('addcamera.newadd.11qe20')">
              <el-checkbox v-model="isExternalRtspComputed"></el-checkbox>
            </el-form-item> -->
            <el-form-item
              :label="$t('addcamera.newadd.11qf20')"
              prop="equipmentCode"
              v-if="detail.isExternalRtsp === 1"
            >
              <el-input
                :placeholder="$t('addcamera.newadd.11qg20')"
                v-model="detail.equipmentCode"
              ></el-input>
            </el-form-item>
            <el-form-item
              :label="$t('addcamera.newadd.11qh20')"
              prop="bitstreamType"
              v-if="detail.isExternalRtsp === 1"
            >
              <template>
                <el-radio v-model="detail.bitstreamType" :label="0">{{
                  $t('addcamera.newadd.11qi20')
                }}</el-radio>
                <el-radio v-model="detail.bitstreamType" :label="1">{{
                  $t('addcamera.newadd.11qj20')
                }}</el-radio>
              </template>
            </el-form-item>
            <el-form-item
              :label="$t('addcamera.newadd.11ql20')"
              prop="equipmentCode"
              v-if="detail.isExternalRtsp === 1 && detail.equipmentCode"
            >
              <el-button type="primary" @click="obtainClick">Get</el-button>
            </el-form-item>
            <el-form-item>
              <template slot="label">
                <span style="margin-right: 5px">{{ $t('addcamera.newadd.27uc8y') }}</span>
                <el-popover placement="top-start" width="400" trigger="hover">
                  <div style="font-size: 13px; color: 606266">
                    <div
                      style="
                        font-weight: bold;
                        font-size: 16px;
                        color: #303133;
                        line-height: 26px;
                        margin-bottom: 10px;
                      "
                    >
                      {{ $t('addcamera.newadd.683k74') }}
                    </div>
                    <div style="line-height: 22px; margin: 20px 0px">
                      <div>
                        {{ $t('addcamera.newadd.hnfs8y') }}
                      </div>
                      <div>{{ $t('addcamera.newadd.70pbcs') }}</div>
                      <div>
                        {{ $t('addcamera.newadd.hsrdq7') }}
                      </div>
                      <div>
                        {{ $t('addcamera.newadd.6eot54') }}
                      </div>
                      <div>
                        {{ $t('addcamera.newadd.939yx3') }}
                      </div>
                    </div>
                    <div>
                      {{ $t('addcamera.newadd.m64u06') }}
                    </div>
                  </div>
                  <span class="el-icon-question" slot="reference"></span>
                </el-popover>
              </template>
              <div style="display: flex; flex-direction: row; justify-content: start">
                <div class="take-btn" @click="takePhoto">
                  <span class="el-icon-picture-outline"></span>
                  <span style="padding-left: 10px">{{ $t('addcamera.index.sg6fq2') }}</span>
                </div>
                <div
                  class="take-btn"
                  @click="handleSyncVideoCodec"
                  style="margin-left: 10px"
                  v-if="currentId && currentId != ''"
                >
                  <span class="el-icon-sort"></span>
                  <span style="padding-left: 10px">{{ $t('addcamera.newadd.g20fp3') }}</span>
                </div>
              </div>
              <div class="image-box" v-loading="photoLoading">
                <div
                  v-if="detail.fileName"
                  :style="{ height: imageHeight ? imageHeight + 'px' : 'auto' }"
                >
                  <MarkDetail
                    ref="markDetail"
                    :fileUrl="$common.handleCameraImgUrl(detail.fileName)"
                    :dataListAll="dataListAll"
                    :disabled="drawDisabled"
                    @getHeight="getHeight"
                    pageType="add"
                  />
                </div>
                <div class="img-cont" v-if="!detail.fileName && isHide">
                  <img
                    src="@/assets/images/no-data.png"
                    style="width: 290px; margin-bottom: 24px"
                  />
                  <div style="color: #1A0808; line-height: 22px; font-size: 12px">
                    {{ $t('addcamera.newadd.8b02ux') }}
                  </div>
                </div>
              </div>
            </el-form-item>
            <div class="flex-item">
              <el-form-item :label="$t('common.identificationInterval')" prop="intervalTime" class="flex-form-item">
                <el-input-number
                  v-model="detail.intervalTime"
                  :min="0.05"
                  :max="99999.99"
                  :precision="2"
                  :step="5"
                  style="width: 100%"
                ></el-input-number>
                <div class="tip-item">{{ $t('addcamera.newadd.5wo26t') }}</div>
              </el-form-item>
              <el-form-item
                :label="$t('common.alarmInterval')"
                prop="alarmInterval"
                class="flex-form-item"
              >
                <el-input-number
                  v-model="detail.alarmInterval"
                  :min="1"
                  :max="99999.99"
                  :precision="2"
                  :step="5"
                  style="width: 100%"
                ></el-input-number>
                <div class="tip-item">
                  {{ $t('addcamera.newadd.m4wr88') }}{{ $t('addcamera.newadd.xdd3p6') }}
                </div>
              </el-form-item>
            </div>
            <el-form-item :label="$t('addcamera.newadd.ljt751')" v-if="scEnabled">
              <el-select
                v-model="detail.soundColumnId"
                clearable
                :placeholder="$t('common.chooseText')"
                style="width: 100%"
              >
                <el-option
                  v-for="item in soundColumnList"
                  :key="item.id"
                  :label="item.sn"
                  :value="item.id"
                >
                </el-option>
              </el-select>
              <div class="tip-item">{{ $t('addcamera.newadd.39448y') }}</div>
            </el-form-item>
          </div>
          <div class="camera-right">
            <el-form-item style="display: flex; flex-direction: column" prop="algorithmvos" label-width="100%">
              <template slot="label">
               <div style="display: flex;justify-content: space-between;align-items: center;margin-bottom: 10px;">
                 <div>
                  <span style="color: red">*</span>
                  <span style="margin: 0px 5px">{{ $t('addcamera.index.37842u') }}</span>
                  <el-popover placement="top-start" width="400" trigger="hover">
                    <div style="font-size: 13px; color: 606266">
                      <div
                        style="
                          font-size: 18px;
                          color: #303133;
                          line-height: 26px;
                          margin-bottom: 10px;
                        "
                      >
                        {{ $t('addcamera.index.56257p') }}
                      </div>
                      <div style="line-height: 22px">
                        {{ $t('addcamera.index.424m2o') }}
                      </div>
                      <div style="font-weight: bold; line-height: 22px; margin: 10px 0px">
                        {{ $t('addcamera.index.01v8ns') }}
                      </div>
                      <div style="line-height: 22px">
                        {{ $t('addcamera.index.m19lxu') }}
                      </div>
                      <div style="font-weight: bold; line-height: 22px; margin: 10px 0px">
                        {{ $t('addcamera.index.d582ei') }}
                      </div>
                      <div style="line-height: 22px">
                        {{ $t('addcamera.index.4x6clo') }}
                      </div>
                      <div style="line-height: 22px">
                        {{ $t('addcamera.index.q7r7gh') }}
                      </div>
                    </div>
                    <span class="el-icon-question" slot="reference"></span>
                  </el-popover>
                </div>
                <div class="tip-item tip-bg">
                  {{ $t('addcamera.newadd.9xx7r2') }}
                </div>
               </div>
              </template>
              
              <el-table
                class="table-style"
                :data="tableData"
                border
                v-loading="loading"
                height="500"
              >
                <el-table-column
                  :label="$t('common.algorithmName')"
                  prop="name"
                  align="center"
                >
                  <template slot-scope="scope">
                    <div class="algorithm-name-cell">
                      <span
                        v-if="scope.row.checked"
                        class="algorithm-dot algorithm-dot-green"
                      ></span>
                      <span :style="{color: scope.row.checked?'#101828':''}">{{ scope.row.name || scope.row.nameEn }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('applicationMonitoring.boxManagement.useSwitch')"
                  width="80"
                  align="center"
                >
                  <template slot-scope="scope">
                    <el-switch v-model="scope.row.checked"></el-switch>
                  </template>
                </el-table-column>
                <el-table-column align="center">
                  <template slot="header" slot-scope="scope">
                    <span style="margin-right: 5px">{{ $t('addcamera.newadd.ym46oi') }}</span>
                    <el-popover placement="top-start" width="400" trigger="hover">
                      <div style="font-size: 13px; color: 606266">
                        <div
                          style="
                            font-size: 16px;
                            color: #303133;
                            line-height: 26px;
                            margin-bottom: 10px;
                          "
                        >
                          {{ $t('addcamera.newadd.12xu6m') }}
                        </div>
                        <div style="line-height: 22px">
                          {{ $t('addcamera.newadd.s394i5') }}
                        </div>
                        <div style="line-height: 22px; margin: 10px 0px">
                          {{ $t('addcamera.newadd.4grl4x') }}<br />
                          {{ $t('addcamera.newadd.8r48wa') }}<br />
                          {{ $t('addcamera.newadd.sacv58') }}
                        </div>
                      </div>
                      <span class="el-icon-question" slot="reference"></span>
                    </el-popover>
                  </template>
                  <template slot-scope="scope">
                    <el-slider
                      v-model="scope.row.confidence"
                      :max="100"
                      :format-tooltip="tooltipText"
                    ></el-slider>
                    <div
                      style="
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        font-size: 12px;
                        line-height: 20px;
                        color: #1A0808;
                      "
                    >
                      <div>{{ $t('addcamera.newadd.673fee') }}</div>
                      <div>{{ $t('addcamera.newadd.sri461') }}</div>
                      <div>{{ $t('addcamera.newadd.284165') }}</div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column width="90" align="center">
                  <template slot="header" slot-scope="scope">
                    <span style="margin-right: 5px">{{ $t('addcamera.index.19x4c2') }}</span>
                    <el-popover placement="top-start" width="300" trigger="hover">
                      <div style="font-size: 13px; color: 606266">
                        <div
                          style="
                            font-size: 16px;
                            color: #303133;
                            line-height: 26px;
                            margin-bottom: 10px;
                          "
                        >
                          {{ $t('addcamera.newadd.b4b9pn') }}
                        </div>
                        <div style="line-height: 22px">
                          {{ $t('addcamera.newadd.ujaz6b') }}
                        </div>
                      </div>
                      <span class="el-icon-question" slot="reference"></span>
                    </el-popover>
                  </template>
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="drawFun(scope.row)"
                      :class="{ 'status-drawn': scope.row.markPoints }"
                    >{{
                      scope.row.markPoints
                        ? $t('addcamera.index.q7mn07')
                        : $t('addcamera.drawdialog.62ien4')
                    }}</el-button>
                  </template>
                </el-table-column>
                <el-table-column width="90" align="center">
                  <template slot="header" slot-scope="scope">
                    <span style="margin-right: 5px">{{ $t('addcamera.newadd.555rc5') }}</span>
                    <el-popover placement="top-start" width="300" trigger="hover">
                      <div style="font-size: 13px; color: 606266">
                        <div
                          style="
                            font-size: 16px;
                            color: #303133;
                            line-height: 26px;
                            margin-bottom: 10px;
                          "
                        >
                          {{ $t('addcamera.newadd.va2mew') }}
                        </div>
                        <div style="line-height: 22px">
                          {{ $t('addcamera.newadd.wg23e6') }}
                        </div>
                      </div>
                      <span class="el-icon-question" slot="reference"></span>
                    </el-popover>
                  </template>
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="setFun(scope.row)"
                      :class="{ 'status-set': scope.row.alarmTimes.length > 0 }"
                    >{{
                      scope.row.alarmTimes.length > 0
                        ? $t('addcamera.newadd.7ipy5v')
                        : $t('projectmanagement.projectdetail.2g5lee')
                    }}</el-button>
                  </template>
                </el-table-column>
                <el-table-column width="80" align="center">
                  <template slot="header">
                    <span style="margin-right: 5px">{{ $t('addcamera.newadd.ummyqc') }}</span>
                  </template>
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="socialOpen(scope.row)"
                      :class="{ 'status-selected': scope.row.socials.length > 0 }"
                    >
                      {{
                        scope.row.socials.length > 0
                          ? $t('addcamera.newadd.7ipy5v')
                          : $t('projectmanagement.projectdetail.2g5lee')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  :label="$t('alarmmanagement.alarmcollectsetting.uz5xox')"
                  width="170"
                >
                  <template slot-scope="scope">
                    <el-radio-group v-model="scope.row.autoPush" size="mini">
                      <el-radio :label="0" border>{{
                        $t('alarmmanagement.alarmcollectsetting.1kkt57')
                      }}</el-radio>
                      <el-radio :label="1" border>{{
                        $t('alarmmanagement.alarmcollectsetting.08l23o')
                      }}</el-radio>
                    </el-radio-group>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </div>
        </el-form>
      </div>
      <!-- 绘制弹窗 -->
      <DrawDialog
        :detailObj="detail"
        :dataList="dataList"
        :dataListAll="dataListAll"
        :nameEn="nameEn"
        :algorithmId="algorithmId"
        v-if="innerVisible"
        @close="closeHandle"
      />
      <!-- 告警时段 -->
      <TimeInfo
        v-if="timeVisible"
        :alarmTimeList="alarmTimeList"
        :algorithmId="algorithmId"
        @close="timeClose"
      />

      <!-- 推送群设置 -->
      <social-hook-selected
        v-if="socialVisible"
        :selected="socialSelected"
        @closeSocial="socialClose"
      ></social-hook-selected>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t('button.cancelText', { text: '' })
        }}</el-button>
        <el-button type="primary" @click="saveFun">{{ $t('addcamera.newadd.ij0lpf') }}</el-button>
      </span>
    </el-dialog>

    <div class="data-tip">
      <el-dialog
        :visible.sync="dataVisible"
        width="280px"
        top="40vh"
        :show-close="false"
        append-to-body
      >
        <div v-if="dataVisible">
          <div v-if="isError">
            <div>{{ $t('addcamera.newadd.y86afh') }}</div>
            <div style="margin-top: 15px; text-align: right">
              <el-button @click="closeTip(1)">{{
                $t('button.cancelText', { text: '' })
              }}</el-button>
              <el-button type="primary" @click="closeTip">{{
                $t('addcamera.newadd.lfydhp')
              }}</el-button>
            </div>
          </div>
          <div v-else>
            {{ $t('applicationMonitoring.boxManagement.dataHandle') }}
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
  import {
    getAlgorithmListData,
    getListDataDetail,
    saveCameraDetail,
    cameraInfo,
    submitCamera,
    takePhoto,
    saveVideoCodec,
    getRtspHandler,
  } from '@/api/applicationMonitoring/cameraManagement';
  import Cookies from 'js-cookie';

  import { listData } from '@/api/applicationMonitoring/soundColumnManagement';
  import SocialHookSelected from '@/views/applicationMonitoring/noticeManagement/components/socialHookSelected.vue';

  import DrawDialog from './drawDialog';
  import MarkDetail from './markDetail.vue';
  import TimeInfo from './timeInfo.vue';

  export default {
    components: {
      MarkDetail,
      DrawDialog,
      TimeInfo,
      SocialHookSelected,
    },
    props: {
      currentId: {
        type: String,
        default: '',
      },
      currentData: {
        type: Object,
        default: () => {},
      },
      gbId: {
        type: String,
        default: '0',
      },
      gbName: {
        type: String,
        default: '',
      },
      gbUrl: {
        type: String,
        default: '',
      },
      gbMediaServerId: {
        type: String,
        default: '',
      },
    },
    data() {
      return {
        loading: false,
        btnLoading: false,
        photoLoading: false,
        dialogVisible: true,
        params: {
          cameraId: this.currentId,
          limit: 10,
          page: 1,
        },
        detail: {},
        currentAlgorithmId: '',
        tableData: [],
        dataList: [],
        dataListAll: [],
        drawDisabled: true,
        innerVisible: false,
        algorithmId: '',
        nameEn: '',
        soundColumnList: [],
        rules: {
          name: [
            {
              required: true,
              message: this.$t('form.tip.inputCameraName'),
              trigger: 'blur',
            },
          ],
          rtspUrl: [
            {
              required: true,
              message: this.$t('addcamera.index.38h7cr'),
              trigger: 'blur',
            },
          ],
          intervalTime: [
            {
              required: true,
              message: this.$t('addcamera.index.ws9o1n'),
              trigger: 'blur',
            },
          ],
          alarmInterval: [
            {
              required: true,
              message: $t('addcamera.index.2pp436'),
              trigger: 'blur',
            },
          ],
          equipmentCode: [
            {
              required: true,
              message: $t('addcamera.newadd.11qg20'),
              trigger: 'blur',
            },
          ],
          bitstreamType: [
            {
              required: true,
              message: $t('addcamera.newadd.11qk20'),
              trigger: 'change',
            },
          ],
        },
        alarmTimeList: [],
        timeVisible: false,
        imageHeight: null,
        isHide: true,
        dataVisible: false,
        isError: false,
        scEnabled: false,
        // 已选择的推送群列表
        socialSelected: [],
        // 是否显示推送群设置弹窗
        socialVisible: false,
        // 选择的推送群列表
        socialSelected: [],
      };
    },
    computed: {
      isExternalRtspComputed: {
        get() {
          return this.detail.isExternalRtsp === 1;
        },
        set(value) {
          this.detail.isExternalRtsp = value ? 1 : 0;
        },
      },
    },
    async created() {
      await this.getAlgorithmListData();
      if (this.currentId) {
        await this.getListDataDetail();
      } else {
        // 摄像头名称设置成国标名称
        this.detail = { ...this.detail, name: this.gbName, rtspUrl: this.gbUrl };
      }

      // 判断是否启用音柱功能
      let sc_enabled = Cookies.get('sound_column_enabled');
      if (sc_enabled == 'true') {
        this.scEnabled = true;
        this.getSoundColumnList();
      }
    },
    methods: {
      // 获取图片高度
      getHeight(num) {
        if (num) {
          this.imageHeight = num;
        }
      },
      // 获取音柱列表
      async getSoundColumnList() {
        const res = await listData();
        this.soundColumnList = res.data;
      },
      // 获取摄像头详情
      async getListDataDetail() {
        let formData = new FormData();
        formData.append('id', this.currentId);
        const data = await cameraInfo(formData);
        this.detail = data.data;
        this.detail.soundColumnId =
          !this.detail.soundColumnId || this.detail.soundColumnId == '0'
            ? ''
            : this.detail.soundColumnId;
        let algorithmsArr = this.detail.algorithms;
        if (algorithmsArr && algorithmsArr.length > 0) {
          algorithmsArr.forEach((item, index) => {
            this.tableData.forEach((ite) => {
              if (ite.id == item.algorithmId) {
                ite.alarmTimes = item.alarmTimes;
                ite.socials = item.socials || [];
              }
            });
          });
        }
      },
      // 获取关联算法列表
      async getAlgorithmListData(str) {
        this.loading = true;
        let params = {
          cameraId: this.currentId,
          locationId: this.currentData.id,
        };
        const data = await getAlgorithmListData(params);
        this.loading = false;
        this.dataListAll = [];
        data.data.forEach((item) => {
          item.status = 1;
          item.alarmTimes = [];
          item.socials = [];
          if (item.nameEn == 'helmet') {
            //安全帽
            item.confidence = item.confidence ? item.confidence * 100 : 75;
          } else if (item.nameEn == 'object') {
            //占道
            item.confidence = item.confidence ? item.confidence * 100 : 55;
          } else if (item.nameEn == 'fire') {
            //烟火
            item.confidence = item.confidence ? item.confidence * 100 : 70;
          } else {
            item.confidence = item.confidence ? item.confidence * 100 : 50;
          }
          if (item.markPoints) {
            let mArr = JSON.parse(item.markPoints);
            let newArr = [];
            mArr.forEach((item) => {
              let pointText = '';
              item.forEach((i) => {
                pointText = pointText + ` ${i.x},${i.y}`;
              });
              newArr.push({
                pointList: item,
                pointText: pointText,
              });
            });
            if (item.lineMarkPoints) {
              let lArr = JSON.parse(item.lineMarkPoints);
              lArr.forEach((item) => {
                let pointText = '';
                item.forEach((i) => {
                  pointText = pointText + ` ${i.x},${i.y}`;
                });
                newArr.push({
                  pointList: item,
                  pointText: pointText,
                });
              });
            }
            let r = Math.floor(Math.random() * 256);
            let g = Math.floor(Math.random() * 256);
            let b = Math.floor(Math.random() * 256);
            let obj = {
              markPoints: newArr,
              name: item.name,
              color: 'rgb(' + r + ',' + g + ',' + b + ')',
              r: r,
              g: g,
              b: b,
            };
            this.dataListAll.push(obj);
          }
        });

        this.tableData = data.data;
      },
      saveFun() {
        this.$refs.detailForm.validate((valid) => {
          if (valid) {
            this.saveData();
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      // 保存摄像头数据
      saveData() {
        const loading = this.$loading({
          lock: true,
          text: this.$t('addcamera.newadd.42617q'),
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.15)',
        });
        // 处理缩放参数
        this.btnLoading = false;
        this.dataVisible = false;
        let arr = [];
        this.tableData.forEach((item) => {
          if (item.checked) {
            arr.push({
              alarmTimes: item.alarmTimes,
              algorithmConf: item.confidence ? item.confidence / 100 : 0,
              algorithmId: item.id,
              algorithmNameEn: item.nameEn,
              drawBoxs: item.markPoints ? JSON.parse(item.markPoints) : [],
              drawLines: item.lineMarkPoints ? JSON.parse(item.lineMarkPoints) : [],
              socials: item.socials,
              autoPush: item.autoPush,
            });
          }
        });
        this.detail.algorithms = arr;
        this.detail.locationId = this.detail.locationId
          ? this.detail.locationId
          : this.currentData?.id;
        this.detail.gbId = this.gbId;
        this.detail.gbName = this.gbName;
        this.detail.gbMediaServerId = this.gbMediaServerId;
        submitCamera(this.detail)
          .then((data) => {
            loading.close();
            this.dataVisible = false;
            this.btnLoading = false;

            if (data.msg == 'OK') {
              this.dialogVisible = false;
              this.$message.success($t('button.saveText', { text: $t('common.success') }));
            } else {
              this.$message.info(data.msg);
            }
          })
          .catch(() => {
            this.btnLoading = false;
            this.isError = false;
            loading.close();
          });
      },
      closeTip(type) {
        this.dataVisible = false;
        this.isError = false;
        if (type == 1) {
          this.dialogVisible = false;
          return;
        }
        this.saveData();
      },
      // 拍照
      async takePhoto() {
        this.photoLoading = true;
        let locationId = this.detail.locationId ? this.detail.locationId : this.currentData?.id;
        let currentCameraId = this.detail.id || '';
        takePhoto({
          rtspUrl: this.detail.rtspUrl,
          locationId: locationId,
          cameraId: currentCameraId,
        })
          .then((res) => {
            this.detail.fileName = res.data.fileName;
            this.detail.videoFps = res.data.videoFps;
            this.detail.videoCodec = res.data.videoCodec;
            this.detail.videoWidth = res.data.videoWidth;
            this.detail.videoHeight = res.data.videoHeight;
            this.photoLoading = false;
            this.$message.success(this.$t('addcamera.index.8b5r3b'));
            setTimeout(() => {
              this.isHide = false;
            }, 100);
          })
          .catch((error) => {
            this.photoLoading = false;
          });
      },
      clickNewMark() {
        this.$refs.markDetail.resetting();
      },
      clickSaveMark() {
        let a = this.$refs.markDetail.save();
        this.tableData.forEach((item) => {
          if (item.id == this.currentAlgorithmId) {
            const markPoints = [];
            a.forEach((i) => {
              markPoints.push(i.pointList);
            });
            item.markPoints = JSON.stringify(markPoints);
          }
        });
      },
      clickClearMark() {
        this.$refs.markDetail.clear();
      },

      // 关闭回调
      closed() {
        this.$emit('close');
      },
      //展示绘制弹窗
      async drawFun(obj) {
        // if(!this.detail.id){
        // this.$message.warning("请先保存再绘制");
        // return;
        // }
        this.tableData.forEach((item) => {
          item.status = 1;
          if (item.id == obj.id) {
            this.dataList = obj.markPoints ? JSON.parse(obj.markPoints) : [];
            if (obj.lineMarkPoints) {
              this.dataList.push(JSON.parse(obj.lineMarkPoints)[0]);
            }
            this.algorithmId = obj.id;
            this.nameEn = obj.nameEn;
          }
        });
        this.innerVisible = true;
      },
      //关闭弹窗回调
      closeHandle(item) {
        this.$data.innerVisible = false;
        if (item) {
          this.dataListAll = [];
          this.tableData.forEach((ite) => {
            if (ite.id == item.algorithmId) {
              ite.lineMarkPoints =
                item.lineMarkPoints && item.lineMarkPoints.length > 0
                  ? JSON.stringify(item.lineMarkPoints)
                  : '';
              ite.markPoints =
                item.markPointsArr && item.markPointsArr.length > 0
                  ? JSON.stringify(item.markPointsArr)
                  : '';
            }
          });
          this.getDraw();
        }
      },
      getDraw() {
        this.dataListAll = [];
        this.tableData.forEach((item) => {
          if (item.markPoints) {
            let mArr = JSON.parse(item.markPoints);
            let newArr = [];
            mArr.forEach((item) => {
              let pointText = '';
              item.forEach((i) => {
                pointText = pointText + ` ${i.x},${i.y}`;
              });
              newArr.push({
                pointList: item,
                pointText: pointText,
              });
            });
            if (item.lineMarkPoints) {
              let lArr = JSON.parse(item.lineMarkPoints);
              lArr.forEach((item) => {
                let pointText = '';
                item.forEach((i) => {
                  pointText = pointText + ` ${i.x},${i.y}`;
                });
                newArr.push({
                  pointList: item,
                  pointText: pointText,
                });
              });
            }
            let r = Math.floor(Math.random() * 256);
            let g = Math.floor(Math.random() * 256);
            let b = Math.floor(Math.random() * 256);
            let obj = {
              markPoints: newArr,
              name: item.name,
              color: 'rgb(' + r + ',' + g + ',' + b + ')',
              r: r,
              g: g,
              b: b,
              algorithmId: item.id, // 添加算法ID，用于在中过滤
            };
            this.dataListAll.push(obj);
          }
        });
      },
      tooltipText(value) {
        return value / 100;
      },
      //  设置时段
      setFun(row) {
        this.alarmTimeList = row.alarmTimes;
        this.algorithmId = row.id;
        this.timeVisible = true;
      },
      // 关闭设置时段
      timeClose(item) {
        this.timeVisible = false;
        this.alarmTimeList = [];
        this.algorithmId = '';
        if (item) {
          this.tableData.forEach((ite) => {
            if (ite.id == item.algorithmId) {
              ite.alarmTimes = item.alarmTimeArr;
            }
          });
        }
      },
      // 同步视频编码
      handleSyncVideoCodec() {
        this.photoLoading = true;
        saveVideoCodec({ id: this.currentId })
          .then((res) => {
            this.photoLoading = false;
            this.detail.videoFps = res.data.videoFps;
            this.detail.videoCodec = res.data.videoCodec;
            this.detail.videoWidth = res.data.videoWidth;
            this.detail.videoHeight = res.data.videoHeight;
            this.$message.success(this.$t('addcamera.newadd.qxf80a'));
          })
          .catch((err) => {
            this.photoLoading = false;
          });
      },
      // 打开推送群设置
      socialOpen(row) {
        if (!row.checked) {
          this.$message.warning($t('addcamera.newadd.87a386'));
          return;
        }
        this.currentAlgorithmId = row.id;
        this.socialSelected = row.socials || [];
        this.socialVisible = true;
      },
      // 关闭推送群设置, updated false-不更新，true-更新; selectType 0-feishu,1-wechat,2-dingding
      socialClose(updated, selected) {
        console.log('selected', selected);
        console.log('updated', updated);
        console.log('currentAlgorithmId', this.currentAlgorithmId);
        if (updated) {
          // this.tableData.forEach((item) => {
          //   console.log("item", item.id);
          //   if (item.id == this.currentAlgorithmId) {
          //     console.log("found....");
          //     let socials = [];
          //     if(item['socials']) {
          //       socials = item.socials;
          //     }
          //     console.log("socials>>>>>", socials);
          //     selected.forEach((it) => {
          //       if(it.checked) {
          //         if(socials.indexOf(it.id) == -1) {
          //           socials.push(it.id);
          //         }
          //       } else {
          //         let index = socials.indexOf(it.id);
          //         if(index != -1) {
          //           socials.splice(index, 1);
          //         }
          //       }
          //     });
          //     item.socials = socials;
          //   }
          // });
        }
        console.log('socials', this.tableData);
        this.socialVisible = false;
      },
      // 手动获取流地址
      async obtainClick() {
        let obj = {
          id: this.currentId,
          equipmentCode: this.detail.equipmentCode,
        };
        const res = await getRtspHandler(obj);
        this.detail.rtspUrl = res.data;
      },
    },
  };
</script>
<style scoped lang="scss">
  .tip-txt {
    color: #606266;
    font-size: 12px;
    margin-left: 10px;
  }
  .camera-dialog {
    width: 100%;
    display: flex;
    gap: 16px;

    .camera-left {
      width: 550px;
      flex-shrink: 0;
      border-radius: 14px;
      background: #F9FAFB;
      padding: 20px;
      :deep(.el-input__inner) {
        border-radius: 10px;
        border: 1px solid #E2E8F0;
        background: #FFF;
      }
      :deep(.el-textarea__inner) {
        border-radius: 10px;
        border: 1px solid #E2E8F0;
        background: #FFF;
      }
      :deep(.el-select .el-input__inner) {
        border-radius: 10px;
        border: 1px solid #E2E8F0;
        background: #FFF;
      }
      :deep(.el-input-number) {
        width: 100%;
        .el-input__inner {
          border-radius: 10px;
          border: 1px solid #E2E8F0;
          background: #FFF;
          text-align: left;
        }
        .el-input-number__decrease,
        .el-input-number__increase {
          // border-radius: 10px;
          border-color: #E2E8F0;
          background: #F8FAFC;
          color: #45556C;
          &:hover {
            color: #E53935;
          }
        }
      }
    }

    .camera-right {
      flex: 1;
      border-radius: 14px;
      background: #F9FAFB;
      padding: 20px;
      :deep(.el-form-item__label) {
        text-align: left !important;
      }
      :deep(.el-form-item__content) {
        margin-left: 0px !important;
      }
      :deep(.el-table__header tr th) {
        border-right: 1px solid #E5E7EB !important;
        background: #F9FAFB;
        color: #374151;
        font-weight: 500;
        font-size: 13px;
      }
      :deep(.el-table__body td) {
        border-color: #E5E7EB;
        color: #6B7280;
        font-size: 13px;
      }
      :deep(.el-button--text) {
        color: #45556C;
        font-size: 13px;
        padding: 0;
        &:hover {
          color: #E53935;
        }
        &.status-drawn,
        &.status-set,
        &.status-selected {
          border-radius: 10px;
          background: linear-gradient(90deg, #2B7FFF 0%, #E53935 100%);
          box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
          color: #FFF;
          padding: 6px 12px;
          &:hover {
            color: #FFF;
            opacity: 0.9;
          }
        }
      }
      :deep(.el-switch) {
        .el-switch__core {
          border-radius: 12px;
        }
        &.is-checked .el-switch__core {
          background-color: #E53935;
        }
      }
      .algorithm-name-cell {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        .algorithm-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          display: inline-block;
          flex-shrink: 0;
        }
        .algorithm-dot-green {
          background: #10B981;
        }
      }
    }
    .take-btn {
      border: 1px solid #E2E8F0;
      border-radius: 10px;
      height: 36px;
      line-height: 36px;
      padding: 0 16px;
      text-align: center;
      font-size: 12px;
      color: #45556C;
      background: #FFF;
      margin-bottom: 12px;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s;
      &:hover {
        border-color: #E53935;
        color: #E53935;
      }
    }
    .flex-item {
      display: flex;
      justify-content: space-between;
      gap: 20px;
      .flex-form-item {
        flex: 1;
        min-width: 0;
      }
    }
    .tip-item {
      color: #86909c;
      font-size: 12px;
      line-height: 20px;
      
    }
    .tip-bg{
      border-radius: 10px;
      border: 1px solid #FEF3C6;
      background: #FFFBEB;
      padding: 6px 12px;
    }
    :deep(.el-form-item__label) {
      color: #374151;
      font-size: 14px;
      font-weight: 500;
    }
    :deep(.el-form-item) {
      margin-bottom: 20px;
    }
  }

  .image-box {
    min-height: 290px;
    border-radius: 10px;
    border: 1px solid #E2E8F0;
    background: #FFF;
    overflow: hidden;
  }
  .img-cont {
    width: 100%;
    text-align: center;
    padding: 24px 0px;
  }

  .image {
    width: 100%;
    height: auto;
    display: block;
  }

  .btn-list {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;
  }
  .table-style {
    width: 100%;
    border-radius: 10px;
    overflow: hidden;
    :deep(.el-table) {
      border-radius: 10px;
      overflow: hidden;
    }
    :deep(.el-table--border) {
      border: 1px solid #E5E7EB;
    }
    :deep(.el-radio--mini.is-bordered) {
      padding: 6px 10px 0px 5px !important;
      border-radius: 10px;
      border-color: #E2E8F0;
    }
    :deep(.el-radio) {
      margin-right: 5px;
    }
    :deep(.el-radio--mini.is-bordered.is-checked) {
      border-color: #E53935;
    }
    :deep(.el-slider) {
      .el-slider__runway {
        background-color: #E2E8F0;
      }
      .el-slider__bar {
        background-color: #E53935;
      }
      .el-slider__button {
        border-color: #E53935;
      }
    }
  }
</style>
<style lang="scss" scoped>
  .camera-dialog-wrapper {
    :deep(.el-dialog) {
      border-radius: 14px;
      overflow: hidden;
    }
    :deep(.el-dialog__body) {
      padding: 20px !important;
      // background: #F8FAFC;
    }
    :deep(.el-dialog__footer) {
      padding: 16px 24px;
      // border-top: 1px solid #E5E7EB;
      // background: #F9FAFB;
      .el-button {
        border-radius: 10px;
      }
    }
  }
  .input-num {
    .el-input__inner {
      text-align: left !important;
    }
  }
  .data-tip {
    :deep(.el-dialog__header) {
      padding: 0px;
    }
  }
</style>
