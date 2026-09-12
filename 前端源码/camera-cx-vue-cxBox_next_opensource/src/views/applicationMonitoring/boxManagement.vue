<template>
  <div>
    <div class="flex">
      <div class="flex-tree">
        <div class="sidebar-title">
          {{ $t('applicationMonitoring.boxManagement.deviceOverview') }}
        </div>
        <div style="padding: 0px 24px;border-bottom: 1px solid #E2E8F0;margin-bottom: 16px;">
          <div
            class="top-title"
            :class="{ active: isDefault }"
            @click="allClick()"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 16 16" fill="none" class="title-icon">
              <g clip-path="url(#clip0_241_1594)">
                <path d="M13.3335 1.3335H2.66683C1.93045 1.3335 1.3335 1.93045 1.3335 2.66683V5.3335C1.3335 6.06988 1.93045 6.66683 2.66683 6.66683H13.3335C14.0699 6.66683 14.6668 6.06988 14.6668 5.3335V2.66683C14.6668 1.93045 14.0699 1.3335 13.3335 1.3335Z" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M13.3335 9.3335H2.66683C1.93045 9.3335 1.3335 9.93045 1.3335 10.6668V13.3335C1.3335 14.0699 1.93045 14.6668 2.66683 14.6668H13.3335C14.0699 14.6668 14.6668 14.0699 14.6668 13.3335V10.6668C14.6668 9.93045 14.0699 9.3335 13.3335 9.3335Z" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M4 4H4.00667" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M4 12H4.00667" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
              </g>
              <defs>
                <clipPath id="clip0_241_1594">
                  <rect width="16" height="16" fill="white"/>
                </clipPath>
              </defs>
            </svg>
            <span>{{ $t('applicationMonitoring.boxManagement.allDevices') }}</span>
          </div>
        </div>
        <el-tree
          :data="treeData"
          :props="defaultProps"
          :highlight-current="true"
          :default-expand-all="true"
          node-key="id"
          class="device-tree"
          ref="tree"
        >
          <div
            class="custom-tree-node"
            slot-scope="{ node, data }"
            style="width: 100%; line-height: 26px"
          >
            <div @click.stop="handleNodeClick(data)" class="label-color">
              <div>
                <span
                  :class="data.depart ? 'el-icon-office-building' : 'el-icon-cpu'"
                  style="padding-right: 5px"
                ></span>
                <span>{{ node.label }}</span>
              </div>
              <div v-if="!data.depart" class="status-dot">
                <span :class="data.online ? 'dot-green' : 'dot-red'"></span>
              </div>
            </div>
            <!-- <span v-if="data.type == 2" style="margin-left: 10px">
              <span :class="{ 'tree-on': handleTreeData(data.meId) }">{{
                handleTreeData(data.meId) ? "On" : "Close"
              }}</span>
            </span>
            <span style="margin-left: 10px;" @click.stop="openTreeMenu(data)">
              <i class="el-icon-setting"></i>
            </span> -->
          </div>
        </el-tree>
      </div>
      <div class="flex-right">
        <div class="head-container">
          <div>
            <el-input
              v-model="params.name"
              :placeholder="$t('common.cameraName')"
              style="width: 200px; margin-right: 10px"
            ></el-input>
            <el-select
              v-model="params.algorithmIds"
              multiple
              collapse-tags
              style="width: 200px; margin-right: 10px"
              :placeholder="$t('common.chooseText')"
            >
              <el-option
                v-for="item in algorithmList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              >
              </el-option>
            </el-select>
          </div>
          <div>
            <el-button
              type="primary"
              icon="el-icon-search"
              @click="getListData"
              >{{ $t("button.queryText") }}</el-button
            >
            <el-button icon="el-icon-refresh" @click="refreshData">{{
              $t("button.resetText", { text: "" })
            }}</el-button>
          </div>
          <!-- <div v-if="btnData.includes('box-batch-add')">
            <el-button  @click="batchAdd" v-if="isBtnShow">Batch Add</el-button>
            <el-button v-if="!isBtnShow" :class="{ 'el-import-hover': (stateObj.status == 1), 'el-fail-hover': (stateObj.status == 3) }" @click="batchAdd">
              <i :class="{ 'el-icon-loading el-import-text': (stateObj.status == 1), 'el-icon-warning el-fail-text': (stateObj.status == 3) }"></i>
              <span :class="{ 'el-import-text': (stateObj.status == 1), 'el-fail-text': (stateObj.status == 3) }">{{ stateObj.text }}</span>
            </el-button>
          </div> -->
        </div>
        <div class="center-cont">
          <div class="top-cont">
            <div class="img-flex">
              <img src="@/assets/images/boxs-icon.png"/>
              <div>
                <div v-if="currentNode.depart">{{ currentNode.name }}</div>
                <div v-if="!currentNode.depart && !isShow">{{$t('applicationMonitoring.boxManagement.allDevices')}}</div>
              </div>
              <div v-if="!currentNode.depart && isShow">
                <div>{{ currentNode.name }}</div>
                <div
                  v-if="
                    params.locationId &&
                    btnData.includes('box-algorithm-overview')
                  "
                >
                  <el-popover
                    placement="right"
                    width="800"
                    trigger="click"
                    @show="handleShow"
                    v-if="currentNode.online"
                  >
                    <el-table
                      :data="gridData"
                      border
                      max-height="300"
                      class="popover-table"
                    >
                      <el-table-column
                        align="center"
                        property="algorithmName"
                        :label="$t('common.algorithmName')"
                      ></el-table-column>
                      <el-table-column
                        align="center"
                        property="isRun"
                        :label="
                          $t(
                            'applicationMonitoring.boxManagement.algorithmStatus'
                          )
                        "
                        width="80"
                      >
                        <template slot-scope="scope">
                          <span
                            :style="{
                              color: scope.row.isRun ? '#2C8AFB' : '#EB3A2F',
                            }"
                            >{{
                              scope.row.isRun
                                ? $t("applicationMonitoring.common.running")
                                : $t("applicationMonitoring.common.notRunning")
                            }}</span
                          >
                        </template>
                      </el-table-column>
                      <el-table-column
                        align="center"
                        property="cameraNames"
                        :label="
                          $t(
                            'applicationMonitoring.boxManagement.associatedCamera'
                          )
                        "
                      ></el-table-column>
                      <el-table-column
                        align="center"
                        property="currentVersion"
                        :label="
                          $t('applicationMonitoring.boxManagement.currentVersion')
                        "
                      ></el-table-column>
                      <el-table-column
                        align="center"
                        property="highVersion"
                        :label="
                          $t('applicationMonitoring.boxManagement.newVersion')
                        "
                      ></el-table-column>
                      <el-table-column
                        align="center"
                        property="upgrade"
                        :label="$t('common.action', { text: '' })"
                        width="80"
                      >
                        <template slot-scope="scope">
                          <el-button
                            type="text"
                            v-if="scope.row.upgrade"
                            style="color: #2c8afb"
                            @click="upgradeFun(scope.row)"
                            >{{
                              $t(
                                "applicationMonitoring.boxManagement.algorithmUpgrade"
                              )
                            }}</el-button
                          >
                        </template>
                      </el-table-column>
                    </el-table>
                    <div class="popover-text" slot="reference">{{ $t('applicationMonitoring.boxManagement.localAlgorithmOverview') }}</div>
                  </el-popover>
                  </div>
              </div>
            </div>
            <div v-if="!currentNode.depart && isShow">
              <el-button
                type="primary"
                icon="el-icon-plus"
                v-if="btnData.includes('box-add')"
                @click="addCamera"
                >{{ $t("common.add", { text: $t("common.camera") }) }}</el-button
              >
            </div>
          </div>
          <div class="bottom-cont">
            <div class="border-R item-cont">
              <div>{{ $t('applicationMonitoring.boxManagement.runningChannels') }}</div>
              <div class="num-txt">{{ staticsObj.countRun }}/{{ staticsObj.countAll }}</div>
            </div>
            <div class="border-R item-cont">
              <div>{{ $t('applicationMonitoring.boxManagement.closedChannels') }}</div>
              <div class="num-txt">{{ staticsObj.countClose }}/{{ staticsObj.countAll }}</div>
            </div>
            <div class="item-cont">
              <div>{{ $t('applicationMonitoring.boxManagement.abnormalChannels') }}</div>
              <div class="num-txt">{{ staticsObj.countExp }}</div>
            </div>
          </div>
        </div>
        <div class="table-cont">
          <div v-if="tableData.length > 0">
            <el-table
              :data="tableData"
              border
              style="width: 100%"
              v-loading="loading"
              :default-sort="{ prop: 'date', order: 'descending' }"
            >
              <el-table-column
                align="center"
                :label="$t('common.liveMap')"
                width="90"
              >
                <template slot-scope="scope">
                  <el-image
                    v-if="scope.row.isShowImg && scope.row.cachedImageUrl"
                    class="table-image"
                    :src="scope.row.cachedImageUrl"
                    :preview-src-list="[scope.row.cachedImageUrl]"
                    @error="handleImageError(scope.$index)"
                  >
                  </el-image>
                  <img
                    v-else
                    src="@/assets/images/no-img.png"
                    class="table-image"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="name"
                :label="$t('common.cameraName')"
              >
              </el-table-column>
              <el-table-column
                align="center"
                prop="locationName"
                :label="
                  $t('applicationMonitoring.boxManagement.computingDevice')
                "
                :filters="locationList"
                :filter-method="filterHandler"
              >
              </el-table-column>
              <!-- <el-table-column align="center" prop="intervalTime" label="流类型">
                <template slot-scope="scope">
                  <el-dropdown style="margin-left: 10px">
                    <span class="el-dropdown-link">
                      <span v-if="scope.row.rtspType == 0">Real-time</span>
                      <span v-if="scope.row.rtspType == 1">Backup</span>
                      <span v-if="scope.row.rtspType == 2">Image</span>
                      <i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item
                        @click.native="switchRtspType(0, scope.row)"
                        >Real-time video URL</el-dropdown-item
                      >
                      <el-dropdown-item
                        @click.native="switchRtspType(1, scope.row)"
                        >Backup video URL</el-dropdown-item
                      >
                      <el-dropdown-item
                        @click.native="switchRtspType(2, scope.row)"
                        >Image URL</el-dropdown-item
                      >
                    </el-dropdown-menu>
                  </el-dropdown>
                </template>
              </el-table-column> -->
              <el-table-column
                align="center"
                prop="alarmInterval"
                :label="$t('common.alarmInterval')"
                width="120"
              >
              </el-table-column>
              <el-table-column
                align="center"
                prop="intervalTime"
                :label="$t('common.identificationInterval')"
                width="120"
              >
              </el-table-column>
              <el-table-column
                align="center"
                prop="algorithmNames"
                :label="$t('common.associationAlgorithm')"
                sortable
                width="100"
              >
              </el-table-column>
              <!-- <el-table-column align="center" v-if="btnData.includes('box-time-config')" label="时段配置">
                <template slot-scope="scope">
                  <el-button type="text" @click="configure(scope.row)"
                    >Configuration</el-button
                  >
                </template>
              </el-table-column> -->
              <el-table-column
                align="center"
                :label="$t('applicationMonitoring.boxManagement.useSwitch')"
                width="80"
              >
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.running"
                    @change="switchRunning(scope.row)"
                    :active-value="1"
                    :inactive-value="0"
                    :disabled="!btnData.includes('box-run-state')"
                  ></el-switch>
                  <!-- <el-popover
                    v-if="lenghtNum==6&&scope.row.running==0"
                    placement="right"
                    title="Cannot open more channels"
                    width="200"
                    trigger="click"
                  >
                    <div>
                      <div>Maximum support6channels computing simultaneously,<br/>Please close other channels first and retry。</div>
                    </div>
                    <el-switch
                    v-model="scope.row.running"
                    slot="reference"
                    disabled
                  ></el-switch>
                  </el-popover>
                  <el-switch
                    v-else
                    v-model="scope.row.running"
                    @change="switchRunning(scope.row)"
                    :active-value="1"
                    :inactive-value="0"
                    :disabled="!btnData.includes('box-run-state')"
                  ></el-switch> -->
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                :label="$t('applicationMonitoring.boxManagement.lastHeartbeat')"
                sortable
                width="150"
              >
                <template slot-scope="scope">
                  {{ getMyDate(Number(scope.row.aiboxExecTime)) }}
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                :label="$t('common.action', { text: '' })"
                width="100"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    v-if="btnData.includes('box-edit')"
                    @click="editData(scope.row)"
                    class="action-btn-edit"
                    icon="el-icon-setting"
                  ></el-button>
                  <!-- <el-button
                    type="text"
                    v-if="btnData.includes('box-delete')"
                    @click="deleteData(scope.row)"
                    class="action-btn-delete"
                    icon="el-icon-delete"
                    style="color: red;"
                  ></el-button> -->
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                :label="
                  $t('applicationMonitoring.incrementalRraining.runState')
                "
                prop="aiboxExecMsg"
                fixed="right"
                width="120"
                :filters="statusList"
                :filter-method="filterHandler"
              >
                <template slot-scope="scope">
                  <div class="status-cell">
                    <span
                      v-if="scope.row.execStatus == 0"
                      class="status-tag status-stopped"
                    >
                      <i class="el-icon-circle-close status-stopped"></i>
                      {{ $t("applicationMonitoring.common.notRunning") || "Stopped" }}
                    </span>
                    <span
                      v-if="scope.row.execStatus == 1"
                      class="status-tag status-running"
                    >
                      <i clss="el-icon-circle-check status-dot-green"></i>
                      <!-- scope.row.aiboxExecMsg -->
                       {{ $t("applicationMonitoring.common.running") || "Run" }}
                    </span>
                    <el-popover
                      v-if="scope.row.execStatus == 2"
                      placement="top"
                      width="200"
                      trigger="hover"
                      :content="scope.row.aiboxExecMsg"
                    >
                      <span
                        slot="reference"
                        class="status-tag status-abnormal"
                      >
                        <i class="el-icon-circle-close status-dot-red"></i>
                        {{ $t("applicationMonitoring.common.abnormal") || "Exception" }}
                      </span>
                    </el-popover>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination">
              <el-pagination
                background
                :current-page="params.page"
                :page-size="params.limit"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @current-change="handleCurrentChange"
                @size-change="handleSizeChange"
              ></el-pagination>
            </div>
          </div>
          <div v-else style="text-align: center; padding: 60px 0px">
            <img
              src="@/assets/images/security-camera.png"
              style="width: 120px; height: 120px"
            />
            <div style="color: #1A0808; font-size: 13px; margin-top: 16px">
              {{ $t("applicationMonitoring.boxManagement.algorithmTrip") }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- <div v-show="showTreeMenu" class="treeMenu">
      <div
        @click="addRegion"
        v-if="currentNode.type == 0 || currentNode.type == 1"
      >
        Add sub region
      </div>
      <div
        @click="editRegion"
        v-if="currentNode.type == 0 || currentNode.type == 1"
      >
        Edit this region
      </div>
      <div @click="deleteRegion" v-if="currentNode.type == 1 && isShow">Delete this region</div>
      <div
        @click="addBox"
        v-if="currentNode.type == 0 || currentNode.type == 1"
      >
        Add Box
      </div>
      <div @click="editBox" v-if="currentNode.type == 2">Edit Box</div>
      <div @click="deleteRegion" v-if="currentNode.type == 2 && isShow">Delete Box</div>
    </div> -->
    <ConfigureTime
      :currentId="currentId"
      v-if="configureTimeVisible"
      @close="(configureTimeVisible = false), getListData()"
    />
    <AddCamera
      :currentId="currentId"
      :currentData="currentNode"
      v-if="addCameraVisible"
      @close="(addCameraVisible = false), getListData()"
    />
    <AddRegion
      :currentData="currentNode"
      :type="addRegionType"
      v-if="addRegionVisible"
      @success="(addRegionVisible = false), getTreeData()"
      @close="addRegionVisible = false"
    />
    <AddBox
      :currentData="currentNode"
      :type="addRegionType"
      v-if="addBoxVisible"
      @success="(addBoxVisible = false), getTreeData()"
      @close="(addBoxVisible = false), getTreeData()"
    />
    <!-- 批量导入 -->
    <UploadInfo
      v-if="uploadVisible"
      :status="stateObj.status"
      :typeText="stateObj.text"
      @close="closeHandel"
    />

    <!-- 算法升级 -->
    <el-dialog
      :title="$t('applicationMonitoring.boxManagement.algorithmUpgrade')"
      :visible.sync="dialogVisible"
      width="70%"
    >
      <AlgorithmUpgrade
        v-if="dialogVisible"
        :id="rowId"
        :algorithmName="algorithmName"
        :platform="platform"
        :nameEn="nameEn"
      />
    </el-dialog>

    <div class="data-tip">
      <el-dialog
        :visible.sync="dataVisible"
        width="280px"
        top="40vh"
        :show-close="false"
      >
        <div v-if="dataVisible">
          <div v-if="isError">
            <div style="margin-bottom: 10px">{{ errorCont }}</div>
            <div>
              {{ $t("applicationMonitoring.boxManagement.cameraText") }}
            </div>
            <div>{{ $t("applicationMonitoring.boxManagement.netText") }}</div>
            <div>{{ $t("applicationMonitoring.boxManagement.pwdText") }}</div>
            <div style="margin-top: 15px; text-align: right">
              <el-button @click="closeTip">{{
                $t("button.cancelText", { text: "" })
              }}</el-button>
            </div>
          </div>
          <div v-else>
            {{ $t("applicationMonitoring.boxManagement.dataHandle") }}
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import {
  getTreeData,
  deleteRegion,
  getListData,
  switchRtspType,
  switchRunning,
  deleteData,
  getHeart,
  getAlgorithmInfo,
} from "@/api/applicationMonitoring/cameraManagement";
import {
  treeBasic,
  cameraStatics,
  cameraStaticsV2,
  cameraList,
  algorithmListAll,
} from "@/api/applicationMonitoring/boxManagement";
import { statusByTag } from "@/api/applicationMonitoring/batchUpload";
import ConfigureTime from "@/components/applicationMonitoring/boxManagement/configureTime";
import AddCamera from "@/components/applicationMonitoring/boxManagement/addCamera/newAdd.vue";
import AddRegion from "@/components/applicationMonitoring/boxManagement/addRegion";
import AddBox from "@/components/applicationMonitoring/boxManagement/addBox";
import UploadInfo from "@/components/applicationMonitoring/batchUpload/upload.vue";
import AlgorithmUpgrade from "@/components/applicationMonitoring/modelTesting/algorithmUpgrade";
import { getMyDate } from "@/utils/common.js";
export default {
  components: {
    ConfigureTime,
    AddCamera,
    AddRegion,
    AddBox,
    UploadInfo,
    AlgorithmUpgrade,
  },
  data() {
    return {
      isDefault: true, //默认展示全部
      getMyDate: getMyDate,
      configureTimeVisible: false,
      addCameraVisible: false,
      addRegionVisible: false,
      addBoxVisible: false,
      loading: false,
      showTreeMenu: false,
      treeData: [],
      tableData: [],
      heartData: [],
      currentId: "",
      currentNode: {},
      addRegionType: "add",
      params: {
        // locationType: 2,
        algorithmIds: [],
        name: "",
        locationId: "",
        departId: "",
        limit: 10,
        page: 1,
      },
      total: 0,
      defaultProps: {
        children: "children",
        label: "name",
      },
      timer: null,
      isShow: false,
      uploadVisible: false,
      stateObj: {},
      isBtnShow: true,
      timerState: null,
      statusBtn: "primary",
      gridData: [],
      dialogVisible: false,
      rowId: "",
      algorithmName: "",
      platform: "",
      nameEn: "",
      btnData: [],
      btnObjList: [],
      lenghtNum: null,
      dataVisible: false,
      isError: false,
      errorCont: "",
      staticsObj: {},
      statusList: [],
      locationList: [],
      algorithmList: [],
      imageCache: {}, // 内存缓存
      originalPadding: '', // 保存原始 padding
    };
  },
  async created() {
    // 设置 main-scroll 的 padding 为 0
    const mainScroll = document.querySelector('.main-scroll');
    if (mainScroll) {
      this.originalPadding = mainScroll.style.padding || '16px'; // 保存原始 padding
      mainScroll.style.padding = '0';
    }
    
    this.clearInvalidCache(); // 清理无效缓存
    this.getBtn();
    await this.getHeart();
    await this.getTreeData();
    this.getStateFun();
    this.getListData();
    this.getAlgorithm();
    this.timer = setInterval(() => {
      this.getHeart();
    }, 60000);
  },
  methods: {
    // 清理无效的缓存数据
    async clearInvalidCache() {
      try {
        const db = await this.openImageDB();
        const transaction = db.transaction(['images'], 'readwrite');
        const store = transaction.objectStore('images');
        const request = store.openCursor();

        request.onsuccess = (event) => {
          const cursor = event.target.result;
          if (cursor) {
            const data = cursor.value.data;
            if (!data || !data.startsWith('data:image')) {
              console.log('Clear cache:', cursor.value.id);
              cursor.delete();
            }
            cursor.continue();
          }
        };
      } catch (error) {
        console.error('Clear cache:', error);
      }
    },

    // 从缓存获取或存储图片
    async getCachedImageUrl(filename) {
      const cacheKey = `camera_img_${filename}`;
      
      try {
        // 1. 先从内存缓存获取
        if (this.imageCache[cacheKey]) {
          return this.imageCache[cacheKey];
        }

        // 2. 从获取
        const db = await this.openImageDB();
        const transaction = db.transaction(['images'], 'readonly');
        const store = transaction.objectStore('images');
        const request = store.get(cacheKey);

        return new Promise((resolve) => {
          request.onsuccess = async () => {
            if (request.result && request.result.data) {
              const cachedData = request.result.data;
              
              // 检查是否是有效的数据
              if (cachedData && cachedData.startsWith('data:image')) {
                this.imageCache[cacheKey] = cachedData;
                resolve(cachedData);
                return;
              } else {
                console.log('Clear cache:', cacheKey);
                this.deleteImageFromDB(cacheKey);
              }
            }
            
            // 3. 缓存中没有或无效，从服务器获取
            const originalUrl = this.$common.handleCameraImgUrl(filename);
            try {
              const response = await fetch(originalUrl);
              if (!response.ok) throw new Error('Failed to load image');
              
              const blob = await response.blob();
              
              // 转换为64
              const reader = new FileReader();
              reader.onloadend = () => {
                const base64data = reader.result;
                this.saveImageToDB(cacheKey, base64data);
                this.imageCache[cacheKey] = base64data;
                resolve(base64data);
              };
              reader.onerror = () => {
                console.error('Conversion failed');
                resolve('');
              };
              reader.readAsDataURL(blob);
            } catch (error) {
              console.error('Failed to get:', error);
              resolve('');
            }
          };

          request.onerror = () => {
            console.error('Read');
            this.fetchAndCacheImage(filename, cacheKey).then(resolve);
          };
        });
      } catch (error) {
        console.error('Cache:', error);
        return '';
      }
    },

    // 辅助方法：从服务器获取并缓存图片
    async fetchAndCacheImage(filename, cacheKey) {
      const originalUrl = this.$common.handleCameraImgUrl(filename);
      try {
        const response = await fetch(originalUrl);
        if (!response.ok) throw new Error('Failed to load image');
        
        const blob = await response.blob();
        
        return new Promise((resolve) => {
          const reader = new FileReader();
          reader.onloadend = () => {
            const base64data = reader.result;
            this.saveImageToDB(cacheKey, base64data);
            this.imageCache[cacheKey] = base64data;
            resolve(base64data);
          };
          reader.onerror = () => {
            resolve('');
          };
          reader.readAsDataURL(blob);
        });
      } catch (error) {
        console.error('Failed to get:', error);
        return '';
      }
    },

    // 删除中的图片
    async deleteImageFromDB(key) {
      try {
        const db = await this.openImageDB();
        const transaction = db.transaction(['images'], 'readwrite');
        const store = transaction.objectStore('images');
        store.delete(key);
      } catch (error) {
        console.error('Cache failed:', error);
      }
    },

    // 打开数据库
    openImageDB() {
      return new Promise((resolve, reject) => {
        const request = indexedDB.open('CameraImageCache', 1);
        
        request.onerror = () => reject(request.error);
        request.onsuccess = () => resolve(request.result);
        
        request.onupgradeneeded = (event) => {
          const db = event.target.result;
          if (!db.objectStoreNames.contains('images')) {
            db.createObjectStore('images', { keyPath: 'id' });
          }
        };
      });
    },

    // 保存图片到
    async saveImageToDB(key, base64Data) {
      try {
        const db = await this.openImageDB();
        const transaction = db.transaction(['images'], 'readwrite');
        const store = transaction.objectStore('images');
        store.put({ id: key, data: base64Data, timestamp: Date.now() });
      } catch (error) {
        console.error('Failed to cache image:', error);
      }
    },

    // 获取算法
    async getAlgorithm() {
      const res = await algorithmListAll();
      this.algorithmList = res.data;
    },
    // 图片识别失败
    handleImageError(index) {
      this.tableData[index].isShowImg = false;
    },
    getBtn() {
      this.btnData = [];
      this.btnObjList = [];
      this.isDetail = false;
      const menuArr = JSON.parse(sessionStorage.getItem("menuTree"));
      let newArr = [];
      this.getbtnList(menuArr);
      this.btnObjList.filter((item, index) => {
        newArr.push(item.auth);
      });
      this.btnData = newArr;
    },
    getbtnList(data) {
      let arr = [];
      data.forEach((item) => {
        if (item.path == this.$route.path) {
          arr = item.children.filter((items, ind) => {
            return items.type == 2;
          });
          this.btnObjList = arr;
        } else {
          this.getbtnList(item.children);
        }
      });
    },
    // 获取本地算法
    handleShow() {
      if (!this.currentNode.online && !this.isDefault) {
        this.$message.error(
          $t("applicationMonitoring.boxManagement.actionError")
        );
        return;
      }
      let formData = new FormData();
      formData.append("boxId ", this.params.locationId);
      getAlgorithmInfo(formData).then((res) => {
        this.gridData = res.data;
      });
    },
    upgradeFun(row) {
      this.rowId = row.algorithmId;
      this.algorithmName = row.algorithmName;
      this.platform = row.platform;
      this.nameEn = row.algorithmNameEn;
      this.dialogVisible = true;
    },
    // 获取批量上传的整体进度
    async getStateFun() {
      // 清除摄像头批量导入数据状态定时器
      clearInterval(this.timerState);
      this.timerState = null;
      // 查询摄像头批量导入数据状态
      let params = {
        tag: this.$store.state.tagInfo,
      };
      const res = await statusByTag(params);
      let obj = {};
      let str = res.data;
      // str.status 0-无导入 1-正在导入 2-导入完成(全部成功) 3-导入完成(包含错误)
      if (str.status == 0 || str.status == 2) {
        this.isBtnShow = true;
        this.stateObj = {
          status: str.status,
          text: "",
          typeStr: "upload",
        };
      } else {
        this.isBtnShow = false;
        this.stateObj = {
          status: str.status,
          text: str.text,
          typeStr: "upload",
        };
        // 开启导入数据定时器
        if (str.percentage != 100) {
          this.timerState = setInterval(() => {
            this.getStateFun();
          }, 3000);
        }
      }
    },
    // 获取位置
    async getTreeData() {
      // const data = await getTreeData({ locationType: 2 });
      const data = await treeBasic();
      this.treeData = data.data;
    },
    // 监测盒子是否在线
    async getHeart() {
      this.count++;
      const data = await getHeart();
      this.heartData = data.data;
    },
    // 获取摄像头列表
    async getListData() {
      this.loading = true;
      const data = await cameraList(this.params);
      this.tableData = data.data;
      this.statusList = [];
      this.locationList = [];
      
      // 先设置列表，但图片暂不显示
      if (this.tableData.length > 0) {
        this.tableData.forEach((item) => {
          this.$set(item, 'isShowImg', false);
          this.$set(item, 'cachedImageUrl', '');
        });
      }
      
      if (data.data && data.data.length > 0) {
        const statusArr = Array.from(
          new Set(data.data.map((item) => item.aiboxExecMsg))
        ).map((aiboxExecMsg) => {
          return data.data.find((item) => item.aiboxExecMsg === aiboxExecMsg);
        });
        statusArr.forEach((items) => {
          if (items.aiboxExecMsg) {
            this.statusList.push({
              text: items.aiboxExecMsg,
              value: items.aiboxExecMsg,
            });
          }
        });
        const locationArr = Array.from(
          new Set(data.data.map((item) => item.locationName))
        ).map((locationName) => {
          return data.data.find((item) => item.locationName === locationName);
        });
        locationArr.forEach((ite) => {
          this.locationList.push({
            text: ite.locationName,
            value: ite.locationName,
          });
        });
      }

      let arr = this.tableData.filter((item) => {
        return item.running == 1;
      });
      this.lenghtNum = arr.length;
      this.total = Number(data.count);
      this.loading = false;
      this.getStatics();
      
      // 异步加载图片
      if (this.tableData.length > 0) {
        for (const item of this.tableData) {
          if (item.fileName) {
            const cachedUrl = await this.getCachedImageUrl(item.fileName);
            this.$set(item, 'cachedImageUrl', cachedUrl);
            this.$set(item, 'isShowImg', true);
          } else {
            this.$set(item, 'isShowImg', true);
          }
        }
      }
    },
    // 筛选
    filterHandler(value, row, column) {
      const property = column["property"];
      return row[property] === value;
    },
    async getStatics() {
      //const res = await cameraStatics({id:this.params.locationId?this.params.locationId:this.params.departId?this.params.departId:null})
      const res = await cameraStaticsV2(this.params);
      this.staticsObj = res.data;
    },
    // 重置
    refreshData() {
      this.currentNode = {};
      Object.assign(this.params, {
        // locationType: 2,
        name: "",
        locationId: "",
        algorithmIds: [],
        departId: "",
        limit: 10,
        page: 1,
      });
      this.isShow = false;
      this.isDefault = true;
      this.getListData();
    },
    // 新增区域
    addRegion() {
      this.addRegionType = "add";
      this.addRegionVisible = true;
    },
    // 编辑区域
    editRegion() {
      this.addRegionType = "edit";
      this.addRegionVisible = true;
    },
    // 删除区域
    // deleteRegion() {
    //   this.$confirm(`确定删除该条数据?`, "提示", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //     type: "warning",
    //   })
    //     .then(async () => {
    //       const res = await deleteRegion({ id: this.currentNode.meId });
    //       if (res.code == 0) {
    //         this.$message.success("删除成功");
    //         await this.getTreeData();
    //       }
    //     })
    //     .catch(() => {});
    // },
    // 新增盒子
    addBox() {
      this.addRegionType = "add";
      this.addBoxVisible = true;
    },
    // 编辑盒子
    editBox(item) {
      this.addRegionType = "edit";
      this.addBoxVisible = true;
    },
    // 新增摄像头
    addCamera() {
      if (!this.currentNode.online && !this.isDefault) {
        this.$message.error(
          this.$t("applicationMonitoring.boxManagement.actionError")
        );
        return;
      }
      this.currentId = "";
      this.addCameraVisible = true;
    },
    // 编辑摄像头
    editData(item) {
      if (!this.currentNode.online && !this.isDefault) {
        this.$message.error(
          this.$t("applicationMonitoring.boxManagement.actionError")
        );
        return;
      }
      this.currentId = item.id;
      this.addCameraVisible = true;
    },
    // 切换流类型
    async switchRtspType(val, item) {
      const params = {
        id: item.id,
        rtspType: val,
      };
      await switchRtspType(params);
      this.getListData();
    },
    // 配置
    configure(item) {
      this.currentId = item.id;
      this.configureTimeVisible = item;
    },
    // 切换运行状态
    async switchRunning(item) {
      this.loading = true;
      this.dataVisible = true;
      switchRunning({ id: item.id })
        .then((res) => {
          this.dataVisible = false;
          this.loading = false;
          if (res.msg == "OK") {
            setTimeout(() => {
              this.getListData(1);
            }, 500);
            this.$message.success(
              $t("common.action", { text: $t("common.success") })
            );
          } else {
            this.$message.info(res.msg);
          }
        })
        .catch((res) => {
          this.errorCont = res;
          // this.isError = true;
          this.isError = false;
          this.dataVisible = false;
          this.loading = false;
          this.getListData();
        });
    },
    closeTip() {
      this.dataVisible = false;
      this.isError = false;
      this.errorCont = "";
    },
    // 删除算法
    async deleteData(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })

        .then(async () => {
          const res = await deleteData({ id: item.id });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getListData();
          }
        })
        .catch(() => {});
    },
    // 点击全部
    allClick() {
      this.isDefault = true;
      this.currentNode = {};
      this.isShow = false;
      Object.assign(this.params, {
        departId: "",
        locationId: "",
      });
      this.getListData();
    },
    // 点击节点
    handleNodeClick(node) {
      this.isDefault = false;
      this.currentNode = node;
      // 设置树节点为选中状态
      this.$nextTick(() => {
        if (this.$refs.tree && node.id) {
          this.$refs.tree.setCurrentKey(node.id);
        }
      });
      if (node.depart) {
        this.params.locationId = "";
        Object.assign(this.params, {
          departId: node.id,
        });
        this.isShow = false;
        this.getListData();
      } else {
        this.params.departId = "";
        Object.assign(this.params, {
          locationId: node.id,
        });
        this.isShow = true;
        this.getListData();
      }
    },
    openTreeMenu(data) {
      this.showTreeMenu = true; // 显示菜单
      this.currentNode = data; // 存储数据
      if (data.parent == "#") {
        this.isShow = false;
      } else {
        this.isShow = true;
      }
      document
        .querySelector(".treeMenu")
        .setAttribute(
          "style",
          `top:${event.clientY}px;left:${event.clientX}px;`
        );
      document.addEventListener("click", this.closeTreeMenu);
      document.addEventListener("contextmenu", this.closeTreeMenu);
    },
    closeTreeMenu() {
      this.showTreeMenu = false; // 关闭菜单
      document.removeEventListener("click", this.closeTreeMenu);
      document.removeEventListener("contextmenu", this.closeTreeMenu);
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
    handleTreeData(id) {
      const obj = this.heartData.find((i) => i.id == id);
      if (obj.online == 1) {
        return true;
      } else {
        return false;
      }
    },
    // 关闭弹窗回调
    close() {
      this.configureTimeVisible = false;
      this.addCameraVisible = false;
      this.getListData();
    },
    // 获取状态
    getStatus(item) {
      const str = {
        1000: $t("applicationMonitoring.boxManagement.reasoning"),
        2000: $t("applicationMonitoring.boxManagement.downloadAlgorithm"),
        3000: $t("applicationMonitoring.boxManagement.unUsed"),
        8000: $t("applicationMonitoring.boxManagement.wakeUp"),
        9000: $t("applicationMonitoring.boxManagement.pretreatment"),
      }[item];
      return str;
    },
    // 批量新增
    batchAdd() {
      this.uploadVisible = true;
    },
    // 关闭批量新增的弹窗
    closeHandel() {
      this.uploadVisible = false;
      this.getListData();
      this.getStateFun();
    },
  },
  beforeDestroy() {
    // 恢复 main-scroll 的 padding
    const mainScroll = document.querySelector('.main-scroll');
    if (mainScroll && this.originalPadding) {
      mainScroll.style.padding = this.originalPadding;
    }
    
    clearInterval(this.timer);
    clearInterval(this.timerState);
    this.timerState = null;
  },
};
</script>
<style scoped lang="scss">
.el-import-hover:hover {
  border: 1px solid #d1d1d1 !important;
}
.el-success-hover:hover {
  border: 1px solid #7bc139 !important;
}
.el-fail-hover:hover {
  border: 1px solid #e6a23c !important;
}
.el-import-text {
  color: #1A0808 !important;
}
.el-success-text {
  color: #7bc139 !important;
}
.el-fail-text {
  color: #e6a23c !important;
}
.search_box {
  padding-bottom: 20px;
}
.flex {
  display: flex;
  .flex-tree {
    width: 280px;
    height: calc(100vh - 64px);
    overflow-y: auto;
    background: #fff;
    border-right: 1px solid #E2E8F0;
    flex-shrink: 0;
    .sidebar-title {
      font-size: 16px;
      color: #1D293D;
      padding: 16px 24px;
    }
    .top-title {
      font-size: 14px;
      padding: 12px;
      border-radius: 10px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 20px;
      transition: all 0.3s;
      background: #F8FAFC;
      color: #45556C;
      .title-icon {
        flex-shrink: 0;
        display: inline-block;
        vertical-align: middle;
        transform: translateY(1px);
      }
      span {
        color: inherit;
        display: inline-block;
        vertical-align: middle;
      }
      &:hover {
        background: #F8FAFC;
      }
      &.active {
        background: linear-gradient(90deg, #2B7FFF 0%, #E53935 100%);
        color: #fff;
        span {
          color: inherit;
        }
      }
    }
      .device-tree {
        padding: 16px;
        :deep(){
          .el-tree-node__content{
            height: 36px;
            border-radius: 10px;
            padding-right: 12px;
          }
          .el-tree-node.is-current > .el-tree-node__content {
            background: linear-gradient(90deg, #2B7FFF 0%, #E53935 100%) !important;
            > .custom-tree-node {
              color: #fff !important;
              span {
                color: #fff !important;
              }
              .el-icon-office-building,
              .el-icon-cpu {
                color: #fff !important;
              }
              .label-color {
                color: #fff !important;
                span {
                  color: #fff !important;
                }
              }
            }
          }
        }
      }
    .custom-tree-node {
      font-size: 13px;
      display: flex;
      align-items: center;
      .label-color {
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex: 1;
        color: #314158;
        cursor: pointer;
        .node-icon {
          margin-right: 8px;
          color: #314158;
          font-size: 16px;
        }
        .node-label {
          color: #314158;
          flex: 1;
        }
        .status-dot {
          margin-left: 10px;
          display: flex;
          align-items: center;
          .dot-green,
          .dot-red {
            display: inline-block;
            width: 8px;
            height: 8px;
            border-radius: 50%;
            flex-shrink: 0;
          }
          .dot-green {
            background: #10B981;
          }
          .dot-red {
            background: #EF4444;
          }
        }
      }
      .tree-on {
        color: #E53935;
      }
    }
    // .custom-tree-node:hover {
    //   .label-color {
    //     color: #2563EB !important;
    //     .node-icon {
    //       color: #2563EB !important;
    //     }
    //   }
    // }
  }
  .flex-right {
    flex: 1;
    min-width: 0;
    padding: 16px;
    .head-container {
      padding:16px;
      border-radius: 14px;
      background: #FFF;
      box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
      display: flex;
      justify-content: space-between;
      align-items: center;
      :deep(.el-input__inner) {
        border-radius: 10px !important;
        border: 1px solid #E2E8F0;
        background: #F8FAFC;
      }
      :deep(.el-select .el-input__inner) {
        border-radius: 10px !important;
        border: 1px solid #E2E8F0;
        background: #F8FAFC;
      }
    }
    .center-cont{
      border-radius: 14px;
      background: #FFF;
      box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
      margin: 16px 0px;
      .top-cont{
        border-bottom: 1px solid #E2E8F0;
        padding: 16px;
        color: #1D293D;
        font-size: 16px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .img-flex{
          display: flex;
          align-items: flex-start;
          img{
            width: 38px;
            margin-right: 12px;
          }
        }
        
      }
      .popover-text{
        background: linear-gradient(90deg, #2B7FFF 0%, #E53935 100%);
        background-clip: text;
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        font-size: 12px;
      }
       .bottom-cont{
         display: flex;
         align-items: center;
         background: #F8FAFC;
         border-bottom-left-radius: 14px;
         border-bottom-right-radius: 14px;
         .border-R{
           border-right: 1px solid #E2E8F0;
         }
         .item-cont{
           flex: 1;
           padding: 16px;
           font-size: 12px;
           color: #45556C;
           text-align: center;
         }
         .num-txt{
           color: #0F172B;
           font-size: 18px;
         }
       }
    }
  }
}
.count-flex {
  display: flex;
  align-items: center;
  width: 30%;
  justify-content: space-between;
}
.count-title {
  font-size: 12px;
  color: #1A0808;
  margin-right: 5px;
}
.num {
  font-size: 20px;
  color: #303133;
}
.el-dropdown-link {
  color: #E53935;
}
.treeMenu {
  position: fixed;
  z-index: 99999;
  top: 50%;
  left: 50%;
  background-color: white;
  overflow: hidden;
  border-radius: 5px;
  border: 1px solid #e6ebf5;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
  div {
    padding: 3px 20px;
    box-sizing: border-box;
    //width: 50px;
    text-align: center;
  }
  div:hover {
    background-color: #eee;
    cursor: pointer;
  }
}
.add-box {
  padding-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.button-sty {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  margin-left: 20px;
  padding: 0px 15px;
  line-height: 32px;
  font-size: 13px;
  cursor: pointer;
  .icon-succes {
    color: red;
  }
}
.table-cont {
  margin-top: 10px;
  :deep(.el-table) {
    border-radius: 8px;
    overflow: hidden;
  }
  :deep(.el-table__header) {
    th {
      background: #F9FAFB;
      color: #374151;
      font-weight: 500;
      font-size: 13px;
      border-color: #E5E7EB;
    }
  }
  :deep(.el-table__body) {
    td {
      color: #6B7280;
      font-size: 13px;
      border-color: #E5E7EB;
    }
  }
  :deep(.el-table--border .el-table__cell:first-child .cell) {
    padding: 0px !important;
  }
  .table-image {
    width: 100%;
    margin-top: 5px;
    border-radius: 6px;
  }
  .status-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    .status-tag {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      .status-dot-green {
        color: #10B981;
      }
      .status-dot-red {
        color: #EF4444;
      }
      &.status-running {
        color: #10B981;
      }
      &.status-stopped {
        color: #9CA3AF;
      }
      &.status-abnormal {
        color: #EF4444;
        .abnormal-reason-btn {
          color: #0F172B;
          padding: 0;
          margin-right: 4px;
          font-size: 13px;
          &:hover {
            color: #0F172B;
          }
        }
      }
    }
  }
  .action-btn-edit,
  .action-btn-delete {
    padding: 4px 8px;
    font-size: 16px;
    i {
      color: #6B7280;
    }
    &:hover {
      i {
        color: #2563EB;
      }
    }
  }
  .action-btn-delete:hover {
    i {
      color: #EF4444;
    }
  }
}
.green {
  color: #67c23a;
}
.red {
  color: red;
}
.data-tip {
  :deep(.el-dialog__header) {
    padding: 0px;
  }
}
</style>
<style lang="scss">
// 全局样式：el-button 圆角
.el-button {
  border-radius: 10px !important;
}
// 全局样式：el-tree 选中节点背景
.device-tree {
  .el-tree-node.is-current > .el-tree-node__content {
    background: linear-gradient(90deg, #2B7FFF 0%, #E53935 100%) !important;
    > .custom-tree-node {
      color: #fff !important;
      span {
        color: #fff !important;
      }
      .el-icon-office-building,
      .el-icon-cpu {
        color: #fff !important;
      }
      .label-color {
        color: #fff !important;
        span {
          color: #fff !important;
        }
      }
    }
  }
}
</style>