<template>
  <div class="model-cont">
    <!-- <div class="top-tabs">
      <div v-for="(items,index) in tagList" :key="index" :class="[items.isCheck?'tab-check':'tab-item']" @click="checkFun(index)">
        <div :class="[items.isCheck?'tab-name':'']">
          {{ items.name }}
        </div>
      </div>
    </div> -->
    <div class="head-cont">
      <div class="head-title-section">
        <div class="head-txt">{{ $t('modeltesting.modeltesting.l614hj') }}</div>
        <div class="head-subtitle">Select the algorithm model that fits your business scenario for deployment and management</div>
      </div>
      <div class="view-toggle">
        <i 
          :class="['view-icon', { active: viewMode === 'grid' }]"
          @click="viewMode = 'grid'"
          title="Grid View"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M12.6667 2H3.33333C2.59695 2 2 2.59695 2 3.33333V12.6667C2 13.403 2.59695 14 3.33333 14H12.6667C13.403 14 14 13.403 14 12.6667V3.33333C14 2.59695 13.403 2 12.6667 2Z" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 6H14" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 10H14" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M6 2V14" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M10 2V14" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </i>
        <i 
          :class="['view-icon', { active: viewMode === 'list' }]"
          @click="viewMode = 'list'"
          title="List View"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M2 3.33325H2.00667" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 8H2.00667" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12.6667H2.00667" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M5.33325 3.33325H13.9999" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M5.33325 8H13.9999" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M5.33325 12.6667H13.9999" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </i>
      </div>
    </div>
    <div class="search-action-bar">
      <div class="search-section">
        <el-input
          :placeholder="$t('modeltesting.modeltesting.lr84lr') || 'SearchAlgorithm Model...'"
          v-model="name"
          class="search-input"
          @keyup.enter.native="seachFun"
        >
          <i slot="prefix" class="el-input__icon el-icon-search"></i>
        </el-input>
      </div>
      <div class="action-buttons">
        <el-button 
          type="primary" 
          v-if="btnData.includes('algorithm-add')" 
          @click="addData"
          class="action-btn"
        >
          <i class="el-icon-plus"></i>
          {{ $t('common.add', { text: $t('common.algorithm') }) }}
        </el-button>
        <el-button 
          v-if="btnData.includes('algorithm-add')"
          @click="handleShowAlarmCollect"
          class="action-btn"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 16 16" fill="none" class="action-icon">
            <path d="M8.33329 14.6666H12C12.3536 14.6666 12.6927 14.5261 12.9428 14.2761C13.1928 14.026 13.3333 13.6869 13.3333 13.3333V4.66659L9.99996 1.33325H3.99996C3.64634 1.33325 3.3072 1.47373 3.05715 1.72378C2.8071 1.97382 2.66663 2.31296 2.66663 2.66659V8.99992" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9.33337 1.33325V3.99992C9.33337 4.35354 9.47385 4.69268 9.7239 4.94273C9.97395 5.19278 10.3131 5.33325 10.6667 5.33325H13.3334" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M8.91869 10.4174C9.05019 10.2859 9.1545 10.1298 9.22566 9.95802C9.29683 9.78621 9.33346 9.60206 9.33346 9.4161C9.33346 9.23013 9.29683 9.04599 9.22566 8.87418C9.1545 8.70237 9.05019 8.54626 8.91869 8.41477C8.7872 8.28327 8.63109 8.17896 8.45928 8.10779C8.28747 8.03663 8.10332 8 7.91736 8C7.73139 8 7.54725 8.03663 7.37544 8.10779C7.20363 8.17896 7.04752 8.28327 6.91602 8.41477L3.57603 11.7561C3.41752 11.9145 3.30151 12.1103 3.23869 12.3254L2.68069 14.2388C2.66396 14.2961 2.66296 14.3569 2.67779 14.4148C2.69262 14.4727 2.72274 14.5255 2.76499 14.5678C2.80724 14.6101 2.86008 14.6402 2.91796 14.655C2.97585 14.6698 3.03666 14.6688 3.09403 14.6521L5.00736 14.0941C5.22247 14.0313 5.41828 13.9153 5.57669 13.7568L8.91869 10.4174Z" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          {{ $t('alarmmanagement.alarmcollectsetting.uz5xox') }}
        </el-button>
        <el-button 
          v-if="btnData.includes('algorithm-alarm-level')"
          @click="addAlarmLevelData"
          class="action-btn"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 16 16" fill="none" class="action-icon">
            <path d="M14.4866 12L9.15329 2.66665C9.037 2.46146 8.86836 2.29078 8.66457 2.17203C8.46078 2.05329 8.22915 1.99072 7.99329 1.99072C7.75743 1.99072 7.52579 2.05329 7.322 2.17203C7.11822 2.29078 6.94958 2.46146 6.83329 2.66665L1.49995 12C1.38241 12.2036 1.32077 12.4346 1.32129 12.6697C1.32181 12.9047 1.38447 13.1355 1.50292 13.3385C1.62136 13.5416 1.79138 13.7097 1.99575 13.8259C2.20011 13.942 2.43156 14.0021 2.66662 14H13.3333C13.5672 13.9997 13.797 13.938 13.9995 13.8208C14.202 13.7037 14.3701 13.5354 14.487 13.3327C14.6038 13.1301 14.6653 12.9002 14.6653 12.6663C14.6652 12.4324 14.6036 12.2026 14.4866 12Z" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M8 6V8.66667" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M8 11.3333H8.00667" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          {{ $t('modeltesting.modeltesting.30q323') }}
        </el-button>
        <!-- <el-button 
          v-if="btnData.includes('algorithm-alarm-voice')"
          @click="addAlarmVoice"
          class="action-btn"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 16 16" fill="none" class="action-icon">
            <path d="M7.33325 3.1346C7.33312 3.04175 7.30548 2.95101 7.25383 2.87384C7.20217 2.79668 7.12882 2.73654 7.04302 2.70102C6.95722 2.6655 6.86283 2.6562 6.77175 2.67428C6.68066 2.69236 6.59698 2.73701 6.53125 2.8026L4.27525 5.05794C4.18819 5.14552 4.08461 5.21496 3.97051 5.26222C3.85642 5.30948 3.73408 5.33363 3.61059 5.33327H1.99992C1.82311 5.33327 1.65354 5.40351 1.52851 5.52853C1.40349 5.65356 1.33325 5.82313 1.33325 5.99994V9.99994C1.33325 10.1767 1.40349 10.3463 1.52851 10.4713C1.65354 10.5964 1.82311 10.6666 1.99992 10.6666H3.61059C3.73408 10.6662 3.85642 10.6904 3.97051 10.7377C4.08461 10.7849 4.18819 10.8544 4.27525 10.9419L6.53058 13.1979C6.59632 13.2638 6.68012 13.3087 6.77138 13.3269C6.86264 13.345 6.95724 13.3357 7.0432 13.3001C7.12916 13.2645 7.20262 13.2041 7.25425 13.1267C7.30589 13.0493 7.33338 12.9583 7.33325 12.8653V3.1346Z" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M10.6667 6C11.0995 6.57699 11.3334 7.27877 11.3334 8C11.3334 8.72123 11.0995 9.42301 10.6667 10" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12.9094 12.2427C13.4666 11.6855 13.9085 11.0241 14.2101 10.2961C14.5116 9.56815 14.6668 8.78793 14.6668 7.99999C14.6668 7.21205 14.5116 6.43183 14.2101 5.70387C13.9085 4.97591 13.4666 4.31448 12.9094 3.75732" stroke="currentColor" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          {{ $t('voicemanagement.addvoice.5t82l9') }}
        </el-button> -->
      </div>
    </div>
    <div class="stats-section">
      <div class="stat-card">
        <div class="stat-content">
          <div class="stat-label">Downloaded Model</div>
          <div class="stat-number">{{ summaryObj.downloadCount }}</div>
        </div>
        <img src="@/assets/images/modelTesting/tip-icon1.png"/>
      </div>
      <div class="stat-card">
        <div class="stat-content">
          <div class="stat-label">Run</div>
          <div class="stat-number">{{ summaryObj.runningCount }}</div>
        </div>
        <img src="@/assets/images/modelTesting/tip-icon2.png"/>
      </div>
      <div class="stat-card">
        <div class="stat-content">
           <div class="stat-label">Available</div>
          <div class="stat-number">{{ summaryObj.availableCount }}</div>
        </div>
        <img src="@/assets/images/modelTesting/tip-icon3.png"/>
      </div>
    </div>
    <div class="flex-cont">
      <div class="right-cont" id="rightCont" v-loading="loading">
        <div class="top-tip" v-if="isShow">
          <div class="go-back" @click="goBack">
            <span class="el-icon-back"></span>
            <span style="padding: 0px 32px 0px 5px">{{
              $t('modeltesting.modeldetail.70611g')
            }}</span>
          </div>
          <div>
            {{ $t('modeltesting.modeltesting.6uq31o', [algorithmList.length]) }}
          </div>
        </div>
        <div>
          <!-- 网格视图 -->
          <div
            v-if="viewMode === 'grid' && algorithmList.length > 0"
            :class="[
              type == 0
                ? 'flex-item flex-item-w32'
                : type == 2
                  ? 'flex-item flex-item-w19'
                  : 'flex-item flex-item-w24',
            ]"
          >
            <div
              v-for="(items, ind) in algorithmList"
              :key="ind"
              class="algorithm-item"
            >
              <div
                v-if="items.hasUpdate"
                style="position: absolute; top: 0px; right: -1px; z-index: 99"
              >
                <img src="@/assets/images/modelTesting/update-icon.png" style="width: 60px" />
              </div>
              <div
                class="img-cont"
                :style="{
                  filter: !items.hasLocalFile && !items.hasGitFile ? 'grayscale(100%)' : '',
                }"
              >
                <el-image
                  v-if="items.image && items.isShowImg && items.cachedImageUrl"
                  :src="items.cachedImageUrl"
                  style="width: 100%; height: 100%"
                  @error="handleImageError(ind)"
                ></el-image>
                <img
                  v-else-if="Object.keys(customImg).includes(items.nameEn)"
                  :src="customImg[items.nameEn]"
                  style="width: 100%; height: 100%"
                />
                <!-- <img
                  v-else
                  src="@/assets/images/modelTesting/default.png"
                  style="width: 100%; height: 100%"
                /> -->
              </div>
              <div class="alg-cont">
                <div class="alg-flex">
                  <div style="display: flex;flex-wrap: wrap;">
                    <div class="alg-txt">{{ items.name }}</div>
                    <div v-if="items.alarmLevel.name" style="margin-left: 10px">
                      <el-tag
                        size="mini"
                        style="font-size: 12px"
                        :style="{
                          borderColor: items.alarmLevel.showColor,
                          color: items.alarmLevel.showColor,
                          backgroundColor: items.alarmLevel.showColorAlpha,
                        }"
                        >{{ items.alarmLevel.name }}</el-tag
                      >
                    </div>
                  </div>
                  <div class="alg-btn-flex">
                    <div
                      class="alg-btn"
                      v-if="btnData.includes('algorithm-edit')"
                      @click.stop="editData(items)"
                    >
                      {{ $t('modeltesting.modeltesting.941srx') }}
                    </div>
                    <el-dropdown
                      @command="(command) => handleCommand(command, items)"
                      trigger="click"
                    >
                      <span class="more-btn" @click.stop="">
                        {{ $t('applicationMonitoring.alarmmanagement.m120pn')
                        }}
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item
                          v-if="btnData.includes('algorithm-import')"
                          command="2"
                          icon="el-icon-download"
                          >{{ $t('modeltesting.uploadzip.2ge2s7') }}</el-dropdown-item
                        >
                        <el-dropdown-item
                          v-if="items.hasLocalFile && btnData.includes('algorithm-version')"
                          command="3"
                          icon="el-icon-refresh"
                          >{{ $t('modeltesting.modeltesting.5o0o75') }}</el-dropdown-item
                        >
                         <!-- <el-dropdown-item
                          v-if="items.hasLocalFile && btnData.includes('algorithm-unload')"
                          command="1"
                          icon="el-icon-delete"
                          style="color: #F54900 !important;border-top: 1px solid #F3F4F6;"
                          >{{ $t('modeltesting.modeltesting.g1h7cd') }}</el-dropdown-item
                        >
                        <el-dropdown-item
                          v-if="btnData.includes('algorithm-delete')"
                          command="4"
                          icon="el-icon-circle-close"
                          style="color:#E7000B !important;"
                          >{{ $t('modeltesting.modeltesting.8gi65j') }}</el-dropdown-item
                        > -->
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </div>
                <div class="alg-tip">{{ items.marks }}</div>
              </div>
            </div>
          </div>
          <!-- 列表视图 -->
          <div v-if="viewMode === 'list' && algorithmList.length > 0" class="list-view">
            <div
              v-for="(items, ind) in algorithmList"
              :key="ind"
              class="algorithm-list-item"
            >
              <div
                v-if="items.hasUpdate"
                style="position: absolute; top: 0px; right: -1px; z-index: 99"
              >
                <img src="@/assets/images/modelTesting/update-icon.png" style="width: 60px" />
              </div>
              <div class="list-item-left">
                <div
                  class="list-img-cont"
                  :style="{
                    filter: !items.hasLocalFile && !items.hasGitFile ? 'grayscale(100%)' : '',
                  }"
                >
                  <el-image
                    v-if="items.image && items.isShowImg && items.cachedImageUrl"
                    :src="items.cachedImageUrl"
                    style="width: 100%; height: 100%"
                    @error="handleImageError(ind)"
                  ></el-image>
                  <img
                    v-else-if="Object.keys(customImg).includes(items.nameEn)"
                    :src="customImg[items.nameEn]"
                    style="width: 100%; height: 100%"
                  />
                </div>
              </div>
              <div class="list-item-content">
                <div class="list-item-header">
                  <div class="list-item-title">
                    <span class="alg-txt">{{ items.name }}</span>
                    <el-tag
                      v-if="items.alarmLevel.name"
                      size="mini"
                      style="font-size: 12px; margin-left: 10px"
                      :style="{
                        borderColor: items.alarmLevel.showColor,
                        color: items.alarmLevel.showColor,
                        backgroundColor: items.alarmLevel.showColorAlpha,
                      }"
                      >{{ items.alarmLevel.name }}</el-tag
                    >
                  </div>
                </div>
                <div class="list-item-processing">
                    <span class="processing-tag">{{items.pushEnable==0 ? 'Handle' : 'Manual handling' }}</span>
                  </div>
                <div class="list-item-description">{{ items.marks }}</div>
              </div>
              <div class="list-item-actions">
                  <div
                    class="alg-btn"
                    v-if="btnData.includes('algorithm-edit')"
                    @click.stop="editData(items)"
                  >
                    {{ $t('modeltesting.modeltesting.941srx') }}
                  </div>
                  <el-dropdown
                    @command="(command) => handleCommand(command, items)"
                    trigger="click"
                  >
                    <span class="more-btn" @click.stop="">
                      {{ $t('applicationMonitoring.alarmmanagement.m120pn')
                      }}
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item
                        v-if="btnData.includes('algorithm-import')"
                        command="2"
                        icon="el-icon-download"
                        >{{ $t('modeltesting.uploadzip.2ge2s7') }}</el-dropdown-item
                      >
                      <el-dropdown-item
                        v-if="items.hasLocalFile && btnData.includes('algorithm-version')"
                        command="3"
                        icon="el-icon-refresh"
                        
                        >{{ $t('modeltesting.modeltesting.5o0o75') }}</el-dropdown-item
                      >
                      <el-dropdown-item
                        v-if="items.hasLocalFile && btnData.includes('algorithm-unload')"
                        command="1"
                        icon="el-icon-delete"
                        style="color: #F54900 !important;border-top: 1px solid #F3F4F6;"
                        >{{ $t('modeltesting.modeltesting.g1h7cd') }}</el-dropdown-item
                      >
                      <el-dropdown-item
                        v-if="btnData.includes('algorithm-delete')"
                        command="4"
                        icon="el-icon-circle-close"
                        style="color:#E7000B !important;"
                        >{{ $t('modeltesting.modeltesting.8gi65j') }}</el-dropdown-item
                      >
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
            </div>
          </div>
          <el-empty :description="description" v-if="algorithmList.length === 0">
            <el-button type="primary" v-if="isShow" @click="goBack">{{
              $t('modeltesting.modeltesting.2b4246')
            }}</el-button>
          </el-empty>
        </div>
      </div>
    </div>
    <AddAlgorithm
      :currentId="currentId"
      :hasLocalFile="hasLocalFile"
      v-if="addAlgorithmVisible"
      @close="((addAlgorithmVisible = false), getListData())"
    />
    <ListAlarmLevel v-if="listAlarmLevelVisible" @close="listAlarmLevelVisible = false" />
    <el-dialog
      :title="$t('modeltesting.modeltesting.5o0o75')"
      :visible.sync="dialogVisible"
      :close-on-press-escape="false"
      :close-on-click-modal="false"
      width="80%"
    >
      <AlgorithmUpgrade
        v-if="dialogVisible"
        :id="rowId"
        :algorithmName="algorithmName"
        :platform="platform"
        :nameEn="nameEn"
        @close="handleClose"
      />
    </el-dialog>
    <!-- 新增算法--上传zip -->
    <!-- <AddPage v-if="addDialogVisible" @closeAdd="closeAdd"/> -->
    <AddInfo v-if="addDialogVisible" @closeAdd="closeAdd" />
    <!-- 手动上传算法文件 -->
    <UploadZip
      v-if="importDialog"
      :algoName="algoName"
      :algoCode="algoCode"
      @closeImport="closeImport"
    />
    <!-- <el-dialog
      title="Upload algorithm version file"
      :visible.sync="importDialog"
      width="600px"
      :before-close="handleDialogClose"
    >
      <ImportAlgorithm v-if="importDialog" :platform="platform" :nameEn="nameEn" pageType="1" @closeImport="closeImport"/>
    </el-dialog> -->
    <!-- 语音管理 -->
    <VoiceManagement v-if="voiceDialog" @closeHandle="closeHandle" />

    <!-- 告警数据采集期限设置 -->
    <AlarmCollectSetting v-if="alarmCollectVisible" @close="handleCloseAlarmCollect" />
  </div>
</template>
<script>
  import {
    getListData,
    getTagListData,
    deleteFile,
    checkAlgorithmVersion,
    summary
  } from '@/api/applicationMonitoring/modelTesting';
  import Cookies from 'js-cookie';

  import { deleteAlgorithm } from '@/api/applicationMonitoring/algorithmManagement';
  import AddAlgorithm from '@/components/applicationMonitoring/modelTesting/addAlgorithm';
  import AddInfo from '@/components/applicationMonitoring/modelTesting/addPage/addInfo.vue';
  import AddPage from '@/components/applicationMonitoring/modelTesting/addPage/index';
  import AlarmCollectSetting from '@/components/applicationMonitoring/alarmManagement/alarmCollectSetting';
  import AlgorithmUpgrade from '@/components/applicationMonitoring/modelTesting/algorithmUpgrade';
  import hunzhuangImg from '@/assets/images/modelTesting/hunzhuang.png';
  import ImportAlgorithm from '@/components/applicationMonitoring/modelTesting/importAlgorithm';
  import ListAlarmLevel from '@/components/applicationMonitoring/algorithmManagement/listAlarmLevel';
  import overImg from '@/assets/images/modelTesting/over.png';
  import peoplecountingImg from '@/assets/images/modelTesting/peoplecounting.png';
  import sleepImg from '@/assets/images/modelTesting/sleep.png';
  import spillImg from '@/assets/images/modelTesting/spill.png';
  import temperatureAlarmImg from '@/assets/images/modelTesting/temperatureAlarm.png';
  import UploadZip from '@/components/applicationMonitoring/modelTesting/uploadZip.vue';
  import VoiceManagement from '@/components/applicationMonitoring/modelTesting/voiceManagement/index.vue';
  import waeringImg from '@/assets/images/modelTesting/waering.png';

  export default {
    components: {
      AddAlgorithm,
      ListAlarmLevel,
      AlgorithmUpgrade,
      AddPage,
      ImportAlgorithm,
      VoiceManagement,
      AddInfo,
      UploadZip,
      AlarmCollectSetting,
    },
    data() {
      return {
        name: '',
        algorithmList: [],
        tagList: [],
        isShow: false,
        tagId: '',
        description: '',
        addAlgorithmVisible: false,
        listAlarmLevelVisible: false,
        currentId: '',
        hasLocalFile: false,
        VUE_APP_API_BASE_URL,
        type: 1,
        dialogVisible: false,
        rowId: '',
        algorithmName: '',
        platform: '',
        nameEn: '',
        notify: null,
        loading: false,
        addDialogVisible: false,
        importDialog: false,
        isImportClose: true,
        algorithmArr: [],
        voiceDialog: false,
        btnData: [],
        isDetail: false,
        algoName: '',
        algoCode: '',
        alarmCollectVisible: false,
        viewMode: 'grid', // 'grid' or 'list'
        customImg: {
          hunzhuang: hunzhuangImg,
          over: overImg,
          peoplecounting: peoplecountingImg,
          sleep: sleepImg,
          spill: spillImg,
          waering: waeringImg,
          temperatureAlarm: temperatureAlarmImg,
        },
        summaryObj:{},
        imageCache: {}, // 内存缓存
      };
    },
    watch: {
      '$store.state.algorithmDownload': {
        immediate: true,
        deep: true,
        handler(val) {
          if (val) {
            this.getListData();
          }
        },
      },
    },
    created() {
      this.clearInvalidCache(); // 清理无效缓存
      this.getBtn();
      this.getListData();
      this.getSummary();
    },
    mounted() {
      window.addEventListener('resize', this.handleResize); // 添加窗口大小改变事件监听器
      this.handleResize();
    },
    beforeDestroy() {
      window.removeEventListener('resize', this.handleResize); // 移除窗口大小改变事件监听器
      if (this.notify) {
        this.notify.close();
      }
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
              // 如果数据不是有效的64（不以:开头），删除它
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
        const cacheKey = `algo_img_${filename}`;
        
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
                
                // 检查是否是有效的数据（以:开头）
                if (cachedData && cachedData.startsWith('data:image')) {
                  // 从缓存中找到有效的64
                  this.imageCache[cacheKey] = cachedData;
                  resolve(cachedData);
                  return;
                } else {
                  // 旧的无效缓存，删除它
                  console.log('Clear cache:', cacheKey);
                  this.deleteImageFromDB(cacheKey);
                }
              }
              
              // 3. 缓存中没有或无效，从服务器获取
              const originalUrl = this.getCoverImageUrl(filename);
              try {
                const response = await fetch(originalUrl);
                if (!response.ok) throw new Error('Failed to load image');
                
                const blob = await response.blob();
                
                // 转换为64
                const reader = new FileReader();
                reader.onloadend = () => {
                  const base64data = reader.result;
                  // 保存到和内存
                  this.saveImageToDB(cacheKey, base64data);
                  this.imageCache[cacheKey] = base64data;
                  resolve(base64data);
                };
                reader.onerror = () => {
                  console.error('Conversion failed');
                  resolve(''); // 返回空字符串，不显示图片
                };
                reader.readAsDataURL(blob);
              } catch (error) {
                console.error('Failed to get:', error);
                resolve(''); // 返回空字符串，不显示图片
              }
            };

            request.onerror = () => {
              console.error('Read');
              // 尝试从服务器获取
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
        const originalUrl = this.getCoverImageUrl(filename);
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
          const request = indexedDB.open('AlgorithmImageCache', 1);
          
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

      getBtn() {
        this.btnData = [];
        this.isDetail = false;
        const menuArr = JSON.parse(sessionStorage.getItem('menuTree'));
        let newArr = [];
        this.getList(menuArr).filter((item, index) => {
          newArr.push(item.auth);
        });
        this.btnData = newArr;
        let detailArr = menuArr.filter((items, i) => {
          return items.path == '/algorithmManagement/modelDetail';
        });
        if (detailArr.length > 0) {
          this.isDetail = true;
        }
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
      handleResize() {
        const element = document.getElementById('rightCont');
        let width = element.offsetWidth;
        if (width <= 1000) {
          this.type = 0;
        } else if (width >= 1480) {
          this.type = 2;
        } else {
          this.type = 1;
        }
      },
      // 获取算法汇总数据
      async getSummary(){
        const res = await summary();
        this.summaryObj = res.data;
      },
      // 检查算法更新
      checkVersion() {
        checkAlgorithmVersion().then((res) => {
          let Arr = res.data;
          let Str = '';
          if (this.notify) {
            this.notify.close();
          }
          if (Arr.length > 0) {
            Arr.forEach((item) => {
              Str +=
                '<li style="font-size: 13px;line-height: 22px;color:#000;position: relative;margin-left: 20px;">' +
                item.name +
                '<span style="position: absolute; left: -10px;">•</span></li>';
            });
            this.notify = this.$notify({
              title: this.$t('modeltesting.modeltesting.q0j8ah'),
              dangerouslyUseHTMLString: true,
              message:
                '<div><div style="color: #666;font-size: 12px;">' +
                this.$t('modeltesting.modeltesting.37bblp', [Arr.length]) +
                '</div><ul style="margin-top:10px">' +
                Str +
                '</ul></div>',
              duration: 30000,
            });
          }
        });
      },
      // 获取算法列表
      async getListData() {
        this.loading = true;
        let obj = {
          name: this.name,
          tagId: this.tagId,
        };
        this.algorithmList = [];
        const data = await getListData(obj);
        this.loading = false;
        const firstShowData = data.data.filter((item) => {
          return !item.name.includes('Coming soon') && !item.name.includes('Loading');
        });
        const filterData = data.data.filter((item) => {
          return item.name.includes('Coming soon') || item.name.includes('Loading');
        });
        const arr = [...firstShowData, ...filterData];
        
        // 先设置列表,但图片暂不显示
        if (arr.length > 0) {
          arr.forEach((item) => {
            item.isShowImg = false; // 先设为
            item.cachedImageUrl = ''; // 初始化为空
          });
        }
        
        this.algorithmList = arr;
        this.algorithmArr = arr;
        
        // 异步加载图片
        if (arr.length > 0) {
          for (const item of arr) {
            if (item.image) {
              item.cachedImageUrl = await this.getCachedImageUrl(item.image);
              item.isShowImg = true; // 图片加载完成后才显示
            } else {
              item.isShowImg = true; // 没有图片的直接显示
            }
          }
        }
      },
      // 图片识别失败
      handleImageError(index) {
        this.algorithmList[index].isShowImg = false;
      },
      // 获取行业列表
      async getTagListData() {
        this.tagList = [];
        const data = await getTagListData({ type: 1 });
        data.data.forEach((item) => {
          item.isCheck = false;
        });
        this.tagList = [
          {
            id: '',
            name: this.$t('common.allText'),
            isCheck: true,
          },
          ...data.data,
        ];
      },
      // 切换左侧选中
      checkFun(idx) {
        this.tagId = this.tagList[idx].id;
        this.tagList.map((item, index) => {
          item.isCheck = false;
          if (idx == index) {
            item.isCheck = true;
          }
        });
        this.description = '';
        if (idx == 0) {
          this.getListData();
          return;
        }
        if (this.algorithmArr.length > 0) {
          let newArr = [];
          this.algorithmArr.forEach((items) => {
            if (items.tagIds && items.tagIds.length > 0) {
              items.tagIds.forEach((tagItem) => {
                if (this.tagList[idx].id == tagItem) {
                  newArr.push(items);
                }
              });
            }
          });
          this.algorithmList = JSON.parse(JSON.stringify(newArr));
        }
      },
      // 搜索
      seachFun() {
        this.isShow = true;
        this.description = this.$t('modeltesting.modeltesting.2j5hu5');
        this.getListData();
      },
      // 返回
      goBack() {
        this.isShow = false;
        this.name = '';
        this.description = '';
        this.getListData();
      },
      detailFun(id) {
        this.$router.push({
          path: '/algorithmManagement/modelDetail',
          query: {
            id: id,
          },
        });
      },
      // 删除文件
      async deleteData(item) {
        this.$confirm(
          this.$t('modeltesting.modeltesting.2ih6g8'),
          this.$t('modeltesting.modeltesting.e3761n', [item.name]),
          {
            confirmButtonText: this.$t('modeltesting.modeltesting.xgox27'),
            cancelButtonText: $t('button.cancelText', { text: '' }),
            confirmButtonClass: 'custom-confirm-class',
            dangerouslyUseHTMLString: true,
            customClass: 'elmessagewidth',
          },
        )
          .then(async () => {
            const res = await deleteFile({ id: item.id });
            if (res.code == 0) {
              this.$message.success($t('button.deleteText', { text: $t('common.success') }));
              await this.getListData();
            }
          })
          .catch(() => {});
      },
      // 算法下载--算法编辑
      editData(item) {
        this.currentId = item.id;
        this.hasLocalFile = item.hasLocalFile;
        this.addAlgorithmVisible = true;
      },
      addAlarmLevelData() {
        this.listAlarmLevelVisible = true;
      },
      // 算法升级
      upgradeFun(items) {
        this.rowId = items.id;
        this.algorithmName = items.name;
        this.platform = items.platform;
        this.nameEn = items.nameEn;
        this.dialogVisible = true;
      },
      handleClose() {
        this.dialogVisible = false;
        this.getListData();
        // this.getTagListData()
      },
      // 新增算法
      addData() {
        this.addDialogVisible = true;
      },
      closeAdd() {
        this.addDialogVisible = false;
        this.getListData();
      },
      // 手动导入模型文件
      uploadFun(items) {
        this.platform = items.platform;
        this.nameEn = items.nameEn;
        this.algoName = items.name;
        this.algoCode = items.nameEn;
        this.importDialog = true;
      },
      closeImport(item) {
        this.importDialog = false;
      },
      handleDialogClose() {
        if (this.isImportClose) {
          this.importDialog = false;
        }
      },
      // 告警语音管理
      addAlarmVoice() {
        this.voiceDialog = true;
      },
      // 关闭语音管理
      closeHandle() {
        this.voiceDialog = false;
        this.getListData();
      },
      // 删除算法
      deleteFun(item) {
        this.$confirm(
          this.$t('modeltesting.modeltesting.y953nw'),
          this.$t('modeltesting.modeltesting.4sc89u', [item.name]),
          {
            confirmButtonText: this.$t('modeltesting.modeltesting.wc3s2f'),
            cancelButtonText: $t('button.cancelText', { text: '' }),
            confirmButtonClass: 'custom-confirm-class',
            dangerouslyUseHTMLString: true,
            customClass: 'elmessagewidth',
          },
        )
          .then(async () => {
            const res = await deleteAlgorithm({ id: item.id });
            if (res.code == 0) {
              this.$message.success($t('button.deleteText', { text: $t('common.success') }));
              await this.getListData();
            }
          })
          .catch(() => {});
      },
      // 更多
      handleCommand(command, items) {
        if (command == 1) {
          //卸载算法
          this.deleteData(items);
        } else if (command == 2) {
          //导入文件模型
          this.uploadFun(items);
        } else if (command == 3) {
          //版本管理
          this.upgradeFun(items);
        } else {
          //删除算法
          this.deleteFun(items);
        }
      },
      // 关闭告警收集收集弹窗
      handleCloseAlarmCollect() {
        this.alarmCollectVisible = false;
      },
      // 打开告警收集收集弹窗
      handleShowAlarmCollect() {
        this.alarmCollectVisible = true;
      },
      // 获取图片
      getCoverImageUrl(filename) {
        const token = Cookies.get('X-Token');
        return `${VUE_APP_API_BASE_URL}/algorithm/picStream?file=${filename}&X-Token=${token}&t=${new Date().getTime()}`;
      },
    },
  };
</script>
<style scoped lang="scss">
  .model-cont {
    // border-radius: 8px;
    overflow: auto;
    // background: #fff;
    // padding: 20px;
    .head-cont {
      padding-bottom: 20px;
      position: relative;
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      .head-title-section {
        flex: 1;
        .head-txt {
          font-size: 24px;
          color: #202b3d;
          font-weight: bold;
          margin-bottom: 8px;
        }
        .head-subtitle {
          font-size: 14px;
          color: #666666;
          line-height: 20px;
        }
      }
      .view-toggle {
        display: flex;
        align-items: center;
        border-radius: 10px;
        border: 1px solid #E5E7EB;
        background: #FFF;
        padding: 4px;
        gap: 4px;
        .view-icon {
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 4px;
          cursor: pointer;
          color: #4A5565;
          transition: all 0.3s;
          background: transparent;
          border: none;
          svg {
            width: 16px;
            height: 16px;
          }
          &:hover {
            background: #f5f7fa;
          }
          &.active {
            background: #E53935;
            color: #fff;
            &:hover {
              background: #E53935;
            }
          }
        }
      }
    }
    .search-action-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      gap: 16px;
      .search-section {
        flex: 1;
        max-width: 400px;
        .search-input {
          :deep(.el-input__inner ){
            border-radius: 10px !important;
            border: 1px solid #E5E7EB;
            background: #FFF;
          }
        }
      }
      .action-buttons {
        display: flex;
        align-items: center;
        gap: 10px;
        flex-wrap: wrap;
        .action-btn {
          border-radius: 10px !important;
          padding: 8px 16px;
          font-size: 12px;
          i {
            margin-right: 4px;
            display: inline-flex;
            align-items: center;
          }
          svg.action-icon {
            color: #364153;
            transition: color 0.3s;
            display: inline-block;
            vertical-align: bottom;
            margin-right: 4px;
            flex-shrink: 0;
          }
          &:hover {
            svg.action-icon {
              color: inherit;
            }
          }
          &.el-button--primary:hover {
            svg.action-icon {
              color: #fff;
            }
          }
        }
      }
    }
    .stats-section {
      display: flex;
      gap: 16px;
      margin-bottom: 24px;
      .stat-card {
        flex: 1;
        border-radius: 14px;
        border: 1px solid #E5E7EB;
        background: #FFF;
        padding: 20px;
        display: flex;
        align-items: center;
        transition: all 0.3s;
        &:hover {
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        }
        img{
          width: 36px;
        }
        .stat-content {
          flex: 1;
          .stat-number {
            font-size: 24px;
            font-weight: bold;
            color: #6A7282;
            line-height: 32px;
          }
          .stat-label {
            font-size: 14px;
            color: #6A7282;
            margin-top: 4px;
          }
        }
      }
    }
    .flex-cont {
      display: flex;
      // padding: 20px 26px;
    }
    .top-tabs {
      background: rgba(227, 227, 227, 0.17);
      border-top-left-radius: 10px;
      border-top-right-radius: 10px;
      display: flex;
      width: 100%;
      overflow-x: auto;
      .tab-item {
        flex-shrink: 0;
        padding: 0px 20px;
        margin: 10px 0px;
        border-left: 1px solid #c2c6cd;
        font-size: 18px;
        color: #8692aa;
        cursor: pointer;
      }
      .tab-item:first-child {
        border-left: none;
      }
      .tab-check {
        flex-shrink: 0;
        padding: 10px 10px 10px 10px;
        font-size: 18px;
        font-weight: bold;
        color: #202b3d;
        background: #fff;
      }
      .tab-check + .tab-item {
        border-left: none;
      }
      .tab-name {
        border-left: 2px solid #f05924;
        padding-left: 8px;
      }
    }
    // .left-cont{
    //   width: 100px;
    //   margin-right: 10px;
    //   height:calc(100vh - 268px);
    //   overflow-y: scroll;
    //   .left-item{
    //     padding: 13px 16px;
    //     border: 1px solid #E7E7E7;
    //     color: rgba(0, 0, 0, 0.60);
    //     background: #F3F3F3;
    //     cursor: pointer;
    //     font-size: 13px;
    //   }
    //   .left-item:nth-child(n+2){
    //     border-top: none!important;;
    //   }
    //   .left-item:hover{
    //     color: #FFFFFF;
    //     background: #54B5E9;
    //   }
    //   .bg-color{
    //     color: #FFFFFF !important;
    //     background: #54B5E9 !important;
    //   }
    // }
    .right-cont {
      flex: 1;
      height: calc(100vh - 353px);
      overflow-y: scroll;
      .flex-item {
        display: grid;
        justify-content: space-between;
        .algorithm-item {
          margin-bottom: 16px;
          // cursor: pointer;
          // border: 1px solid #edf0f3;
          // border-radius: 6px;
          // box-shadow: 0px 12px 32px 0px rgba(0, 0, 0, 0.04), 0px 8px 20px 0px rgba(0, 0, 0, 0.08);
          border-radius: 14px;
          border: 1px solid #E5E7EB;
          opacity: 0.9;
          background: #FFF;
          position: relative;
          .img-cont {
            height: 145px;
            // background: #54B5E9;
            :deep(.el-image){
              border-top-left-radius: 14px;
              border-top-right-radius: 14px;
            }
          }
          .alg-cont {
            // height: 125px;
            padding: 16px 16px 10px 16px;
            .alg-flex{
              display: flex;
              align-items: center;
              justify-content: space-between;
              width: 100%;
              > div:first-child {
                flex: 1;
                min-width: 0;
              }
              .alg-btn-flex {
                flex-shrink: 0;
              }
            }
            .alg-txt {
              color: #333333;
              font-size: 16px;
              font-weight: bold;
              white-space: nowrap; /* No wrap */
              overflow: hidden; /* Hide overflow */
              text-overflow: ellipsis; /* Use ellipsis to indicate hidden content */
            }
            .alg-tip {
              color: #666666;
              margin-top: 10px;
              // height: 58px;
              font-size: 13px;
              // line-height: 29px;
              overflow: hidden;
              text-overflow: ellipsis;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
            }
            .alg-btn-flex {
              // text-align: center;
              // margin-top: 10px;
              display: flex;
              // justify-content: end;
              align-items: center;
              position: relative;
              font-size: 13px;
            }
            .alg-btn {
              color: #E53935;
              cursor: pointer;
              // border-right: 1px solid #F05924;
              padding-right: 10px;
            }
            .alg-del-btn {
              color: #202b3d;
              cursor: pointer;
            }
            .optimize-btn {
              color: #202b3d;
            }
            .more-btn {
              color: #202b3d;
              // padding: 10px;
              cursor: pointer;
            }
            .upload-btn {
              color: #E53935;
              cursor: pointer;
              display: none;
            }
          }
        }
        .algorithm-item:hover {
          // box-shadow: 0px 12px 32px 0px rgba(0, 0, 0, 0.08), 0px 8px 20px 0px rgba(0, 0, 0, 0.1);
          // border-bottom-left-radius: 6px;
          // border-bottom-right-radius: 6px;
          border-radius: 14px;
          border: 1px solid #BEDBFF;
          background: #FFF;
          box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
          .upload-btn {
            display: block;
          }
          .optimize-btn {
            display: none;
          }
        }
      }
      .flex-item-w24 {
        grid-template-columns: repeat(auto-fill, 24%);
      }
      .flex-item-w32 {
        grid-template-columns: repeat(auto-fill, 32%);
      }
      .flex-item-w19 {
        grid-template-columns: repeat(auto-fill, 19%);
      }
    }
    .list-view {
      .algorithm-list-item {
        display: flex;
        border-radius: 10px;
        border: 1px solid #E5E7EB;
        opacity: 0.9;
        background: #FFF;
        margin-bottom: 16px;
        padding: 16px;
        position: relative;
        transition: all 0.3s;
        &:hover {
          border-radius: 10px;
          border: 1px solid #BEDBFF;
          background: #FFF;
          box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.10), 0 1px 2px -1px rgba(0, 0, 0, 0.10);
        }
        .list-item-left {
          margin-right: 16px;
          .list-img-cont {
            width: 120px;
            height: 90px;
            border-radius: 4px;
            overflow: hidden;
            background: #f5f5f5;
            flex-shrink: 0;
          }
        }
        .list-item-content {
          flex: 1;
          display: flex;
          flex-direction: column;
          .list-item-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            .list-item-title {
              display: flex;
              align-items: center;
              .alg-txt {
                color: #333333;
                font-size: 16px;
                font-weight: bold;
                margin-right: 8px;
              }
            }
          }
          .list-item-processing {
              .processing-tag {
                color: #0055FF;
                font-size: 12px;
                font-style: normal;
                font-weight: 400;
                line-height: 20px; /* 142.857% */
                margin-bottom: 20px;
              }
            }
          .list-item-description {
            color: #666666;
            font-size: 13px;
            line-height: 20px;
            margin-bottom: 12px;
            flex: 1;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
        }
        .list-item-actions {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            height: 20px;
            gap: 12px;
            .alg-btn {
              color: #E53935;
              cursor: pointer;
              font-size: 12px;
              padding: 4px 8px;
              &:hover {
                color: #E53935;
              }
            }
            .more-btn {
              color: #202b3d;
              cursor: pointer;
              font-size: 12px;
            }
          }
      }
    }
    .top-tip {
      padding-bottom: 16px;
      font-size: 13px;
      line-height: 22px;
      display: flex;
      .go-back {
        cursor: pointer;
      }
      .go-back:hover {
        color: #54b5e9;
      }
    }
  }
</style>
<style lang="scss">
  .model-cont {
    .head-cont {
      .el-button--default {
        background: transparent !important;
        height: 28px !important;
        border-radius: unset !important;
        border: none !important;
        margin-top: -6px !important;
      }
      .el-icon-search:before {
        color: #1A0808;
      }
      .el-button--default:hover {
        .el-icon-search:before {
          color: #54b5e9;
        }
      }
    }
    
  }
  .customClass {
    z-index: 9999 !important;
  }
  .el-dropdown-menu__item:focus,
  .el-dropdown-menu__item:not(.is-disabled):hover {
    background-color: transparent !important;
    color: unset !important;
  }
</style>
