<template>
  <div class="div-annotation-container">
    <h2 v-if="title">{{ title }}</h2>
    <div class="image-container" ref="imageContainer">
      <img
        :src="imageUrl"
        ref="image"
        @load="initContainer"
        class="annotated-image"
        v-if="imageUrl"
      />

      <!-- Marker -->
      <div
        v-for="marker in markers"
        :key="marker.objectId"
        class="marker"
        :style="{ left: `${marker.x}px`, top: `${marker.y}px` }"
      >
        <!-- SVG -->
        <SvgIcon
          :src="getMapImageUrl(marker.svgName)"
          :size="32"
          :color="marker.color"
          @click="(event) => handleMarkerClick(event, marker)"
          class="marker__svg"
        />
      </div>

      <!-- 遮罩层 -->
      <div class="marker-bg" v-if="activeMarker" @click.stop="closeMarkerDetail"></div>

      <div
        class="marker-detail"
        v-if="activeMarker"
        :style="{
          left: `${activeMarker.x + 20}px`,
          top: `${activeMarker.y + 20}px`,
        }"
      >
        <div class="marker-detail__content">
          <!-- 类型图标与名称 -->
          <div class="marker-detail__header">
            <img
              :src="
                activeMarker.type === 'box'
                  ? require('@/assets/images/box-icon.png')
                  : require('@/assets/images/camera-icon.png')
              "
              class="marker-detail__type-icon"
            />
            <span class="marker-detail__name">{{ activeMarker.objectName }}</span>
          </div>

          <!-- 所属机构 -->
          <div class="marker-detail__org" v-if="activeMarker.relName">
            <span class="marker-detail__label">{{ $t('common.caBelongingOrganization') }}：</span>
            <span>{{ activeMarker.relName || '--' }}</span>
          </div>

          <!-- 异常统计 -->
          <div class="marker-detail__error">
            <div class="marker-detail__error-left">
              <img src="@/assets/images/error-icon.png" class="marker-detail__error-icon" />
              <span>{{ $t('components.images.jyp5sy') }}</span>
            </div>
            <span class="marker-detail__error-count">{{ activeMarker.num }}</span>
          </div>

          <!-- 报告列表 -->
          <div class="marker-detail__report-list">
            <div
              v-for="(report, idx) in activeMarker.reportList"
              :key="report.id"
              class="marker-detail__report-item"
              @click.stop="handleReportClick(report)"
            >
              {{ idx + 1 }}、{{ report.algorithmName }} /
              {{ $moment(report.createdAt).format('YYYY-MM-DD HH:mm:ss') }} /
              {{ report.cameraName }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 告警详情弹窗 -->
    <AlarmDetail
      :alarmId="alarmData.id"
      :params="alarmData"
      v-if="isAlarmDetailVisible"
      @close="isAlarmDetailVisible = false"
    />
  </div>
</template>

<script>
  import Cookies from 'js-cookie';

  import AlarmDetail from '@/components/applicationMonitoring/alarmManagement/alarmDetail/newDetail';

  import SvgIcon from './SvgIcon.vue';

  export default {
    name: 'DivImageAnnotation',
    components: { AlarmDetail, SvgIcon },
    props: {
      imageUrl: { type: String, required: true },
      markerIconUrl: {
        type: String,
        default: 'https://cdn-icons-png.flaticon.com/512/684/684908.png',
      },
      title: { type: String, default: '' },
      iconSize: { type: Number, default: 30 },
      isAdd: { type: Boolean, default: false },
      isEdit: { type: Boolean, default: false },
      markersEd: {
        type: Array,
        default: () => [],
        validator: (val) => val.every((item) => item.objectId),
      },
      editIndex: { type: Number, default: 0 },
    },
    data() {
      return {
        markers: [],
        activeMarker: null,
        isAlarmDetailVisible: false,
        alarmData: {},
        VUE_APP_API_BASE_URL,
        cache: {
          containerWidth: 0,
          containerHeight: 0,
          scaleX: 1,
          scaleY: 1,
        },
      };
    },

    watch: {
      imageUrl: {
        handler() {
          this.$nextTick(() => this.initContainer());
        },
        immediate: true,
      },
      markersEd: {
        handler(newVal) {
          this.updateMarkers(newVal);
        },
        deep: true,
        immediate: true,
      },
    },
    methods: {
      getMapImageUrl(filename) {
        const token = Cookies.get('X-Token');
        return `${VUE_APP_API_BASE_URL}/map/file/stream?filename=${filename}&X-Token=${token}`;
      },

      initContainer() {
        const img = this.$refs.image;
        if (!img) return;

        this.cache.containerWidth = img.clientWidth;
        this.cache.containerHeight = img.clientHeight;
        this.cache.scaleX = img.naturalWidth / img.clientWidth;
        this.cache.scaleY = img.naturalHeight / img.clientHeight;

        // 更新定位
        this.updateMarkers(this.markersEd);
      },

      updateMarkers(markersEd) {
        if (!markersEd.length) {
          this.markers = [];
          this.activeMarker = null;
          return;
        }
        this.markers = markersEd.map((item) => ({
          ...item,
          x: item.originX / this.cache.scaleX - this.iconSize / 2,
          y: item.originY / this.cache.scaleY - this.iconSize / 2,
        }));
      },

      handleMarkerClick(event, marker) {
        // 阻止事件冒泡到父容器或其他元素
        event.stopPropagation();
        this.activeMarker = this.activeMarker?.objectId === marker.objectId ? null : marker;
      },

      // 关闭详情
      closeMarkerDetail() {
        this.activeMarker = null;
      },

      handleReportClick(report) {
        this.alarmData = { ...report };
        this.isAlarmDetailVisible = true;
      },
    },
  };
</script>

<style scoped lang="scss">
  .div-annotation-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    font-family: Arial, sans-serif;
    width: 100%;
    position: relative;
    height: 100%;
    // padding: 16px 0;

    h2 {
      margin-bottom: 16px;
      font-size: 18px;
      color: #333;
    }
  }

  .image-container {
    position: relative;
    width: 100%;
    // height: calc(100% - 60px);
    height: 100%;
    overflow: auto;

    .annotated-image {
      display: block;
      width: 100%;
      height: auto;
      object-fit: contain;
    }
  }

  .marker {
    position: absolute;
    z-index: 100;
    width: auto;
    height: auto;
  }

  .marker__svg {
    cursor: pointer;
    transition: transform 0.2s;

    &:hover {
      transform: scale(1.1);
    }
  }

  .marker-bg {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 101;
    cursor: pointer;
  }

  // 详情面板
  .marker-detail {
    position: absolute;
    z-index: 102;
    width: 240px;
    &__content {
      padding: 16px;
      border-radius: 10px;
      background: linear-gradient(
        127.09deg,
        rgba(6, 11, 40, 0.94) 19.41%,
        rgba(10, 14, 35, 0.49) 76.65%
      );
      color: #fff;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    }

    &__header {
      display: flex;
      align-items: center;
      margin-bottom: 12px;

      .marker-detail__type-icon {
        width: 16px;
        height: 16px;
        margin-right: 8px;
      }

      .marker-detail__name {
        font-size: 13px;
        line-height: 1.5;
      }
    }

    &__org {
      font-size: 12px;
      line-height: 24px;
      margin-bottom: 12px;

      .marker-detail__label {
        color: #ccc;
      }
    }

    &__error {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 12px;
      line-height: 24px;
      margin-bottom: 12px;

      &-left {
        display: flex;
        align-items: center;

        .marker-detail__error-icon {
          width: 16px;
          height: 16px;
          margin-right: 8px;
        }
      }

      &-count {
        color: #ff4d4f;
      }
    }

    &__report-list {
      font-size: 12px;
      max-height: 200px; // 限制高度，避免溢出
      overflow-y: auto; // 内容过多时可滚动
    }
  }

  .marker-detail__report-item {
    cursor: pointer;
    margin-bottom: 8px;
    line-height: 1.5;
    &:hover {
      color: #E53935;
    }
  }
</style>
