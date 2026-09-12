<template>
  <div class="flex-sty">
    <div class="drag-inner">
      <!-- SVG标记层：与图片容器完全重叠 -->
      <svg
        class="canvas"
        v-for="(items, ind) in svgListAll"
        :key="ind"
        :width="imgContainerWidth"
        :height="imgContainerHeight"
      >
        <g v-for="(item, index) in items.markPoints" :key="index">
          <polygon
            :points="item.pointText"
            style="stroke: #1dfff9; fill: rgba(29, 255, 249, 0.4); stroke-width: 2"
          />
          <g v-for="(pointItem, pointIndex) in item.pointList" :key="pointIndex">
            <rect
              :x="pointItem.x - 3"
              :y="pointItem.y - 3"
              width="6"
              height="6"
              fill="#ff1d23"
            ></rect>
          </g>
        </g>
      </svg>

      <!-- 异常点矩形框层 -->
      <div
        v-for="(item, index) in pointList"
        :key="index"
        class="xbox"
        :style="{
          width: item.width + 'px',
          height: item.height + 'px',
          left: item.left + 'px',
          top: item.top + 'px',
        }"
      >
        <div v-if="pageType === 'detail'">
          <div class="text" v-if="!item.isShow">
            <span style="margin: 0 5px">{{ item.type }}</span>
            <span>{{ item.confidence }}</span>
          </div>
          <div v-else class="text_P" :style="{ width: item.txt_width + 'px' }">
            <div class="textA">
              <span style="margin: 0 5px">{{ item.type }}</span>
              <span>{{ item.confidence }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 图片层：确保图片充满容器，绑定加载事件 -->
      <img
        :id="'draw_img' + Index"
        :ref="'draw_img' + Index"
        :src="fileUrl"
        draggable="false"
        class="img-content"
        @load="handleImgLoad"
        @error="handleImgError"
      />
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      Index: { type: Number, default: 0 },
      fileUrl: { type: String, default: '' },
      dataListAll: { type: Array, default: () => [] }, // SVG标记数据
      dataList: { type: Array, default: () => [] }, // 矩形框标记数据
      ratio: { type: Number, default: 0.5 }, // 业务缩放比（保留）
      pageType: { type: String, default: '' },
    },
    data() {
      return {
        svgListAll: [],
        pointList: [],
        imgOriginWidth: 0, // 图片原始宽度（加载后获取）
        imgOriginHeight: 0, // 图片原始高度（加载后获取）
        imgContainerWidth: 0, // 图片容器宽度（与图片渲染宽度一致）
        imgContainerHeight: 0, // 图片容器高度（与图片渲染高度一致）
        resizeObserver: null, // 监听容器尺寸变化
      };
    },
    mounted() {
      // 初始化：先尝试获取已加载的图片尺寸，再监听变化
      this.$nextTick(() => {
        this.getImgSize();
        this.initResizeObserver();
      });
    },
    beforeUnmount() {
      // 销毁监听，避免内存泄漏
      if (this.resizeObserver) {
        this.resizeObserver.disconnect();
      }
    },
    methods: {
      // 1. 图片加载完成：获取原始尺寸 + 计算初始标记
      handleImgLoad(e) {
        const img = e.target;
        // 记录图片原始尺寸（固定不变）
        this.imgOriginWidth = img.naturalWidth;
        this.imgOriginHeight = img.naturalHeight;
        // 获取图片容器尺寸（与图片渲染尺寸一致）
        this.getImgContainerSize(img);
        // 计算所有标记
        this.calcAllMarks();
      },

      // 2. 图片加载失败处理（避免白屏）
      handleImgError() {
        console.warn('Failed to load image，URL:', this.fileUrl);
        // 加载失败时用默认尺寸占位，确保标记容器存在
        this.imgContainerWidth = this.$refs[`draw_img${this.Index}`]?.offsetWidth || 200;
        this.imgContainerHeight = this.$refs[`draw_img${this.Index}`]?.offsetHeight || 150;
      },

      // 3. 获取图片容器尺寸（核心：确保标记与图片尺寸对齐）
      getImgContainerSize(img = null) {
        const imgDom = img || this.$refs[`draw_img${this.Index}`];
        if (!imgDom) return;

        // 容器尺寸 = 图片渲染尺寸（直接取实际尺寸，避免比例计算误差）
        this.imgContainerWidth = imgDom.offsetWidth;
        this.imgContainerHeight = imgDom.offsetHeight;
      },

      // 4. 初始化尺寸监听（窗口缩放/父容器变化时更新标记）
      initResizeObserver() {
        const imgDom = this.$refs[`draw_img${this.Index}`];
        if (!imgDom || !window.ResizeObserver) return;

        // 监听图片的尺寸变化
        this.resizeObserver = new ResizeObserver((entries) => {
          entries.forEach((entry) => {
            this.imgContainerWidth = entry.contentRect.width;
            this.imgContainerHeight = entry.contentRect.height;
            // 尺寸变化后重新计算标记
            this.calcAllMarks();
          });
        });
        this.resizeObserver.observe(imgDom);
      },

      // 5. 计算核心比例（原始坐标 → 渲染坐标）
      getRenderRatio() {
        // 比例 = 容器宽度 / 图片原始宽度（宽高比例一致，用宽度计算更稳定）
        if (!this.imgOriginWidth || !this.imgContainerWidth) return 0.5;
        return (this.imgContainerWidth / this.imgOriginWidth) * this.ratio;
      },

      // 6. 计算矩形框标记（pointList）
      calcPointList() {
        if (!this.dataList.length || this.getRenderRatio() === 0) return;

        const ratio = this.getRenderRatio();
        this.pointList = this.dataList.map((item) => {
          // 原始坐标（从获取，格式：[x1, y1, x2, y2]）
          const [x1, y1, x2, y2] = item.position || [0, 0, 0, 0];

          // 计算渲染后的数据（原始坐标 × 比例）
          const left = x1 * ratio;
          const top = y1 * ratio;
          const width = (x2 - x1) * ratio;
          const height = (y2 - y1) * ratio;
          // 计算文本宽度（适配12px字体）
          const text = `${item.type || ''}${item.confidence ? parseFloat(item.confidence).toFixed(2) : ''}`;
          const txtWidth = this.calcTextWidth(text) + 10; // +10 预留边距

          return {
            left,
            top,
            width,
            height,
            txt_width: txtWidth,
            confidence: item.confidence ? parseFloat(item.confidence).toFixed(2) : '',
            type: item.type || 'Unknown type',
            isShow: txtWidth > width, // 文本是否超出矩形框，需换行
          };
        });
      },

      // 7. 计算标记（svgListAll）
      calcSvgList() {
        if (!this.dataListAll.length || this.getRenderRatio() === 0) return;

        const ratio = this.getRenderRatio();
        this.svgListAll = this.dataListAll.map((svgItem) => {
          const markPoints =
            svgItem.markPoints?.map((mark) => {
              let pointText = ''; // SVG 的属性（格式："x1,y1 x2,y2 x3,y3"）
              const pointList =
                mark.pointList?.map((point) => {
                  // 原始坐标 → 渲染坐标
                  const x = point.x * ratio;
                  const y = point.y * ratio;
                  // 拼接字符串
                  pointText += `${x},${y} `;
                  return { x, y };
                }) || [];

              return { pointList, pointText: pointText.trim() };
            }) || [];

          return { markPoints };
        });
      },

      // 8. 统一计算所有标记（矩形框 + SVG）
      calcAllMarks() {
        this.calcPointList();
        this.calcSvgList();
      },

      // 9. 工具函数：计算文本宽度（替代临时元素，更可靠）
      calcTextWidth(text) {
        if (!text) return 0;
        const span = document.createElement('span');
        span.innerText = text;
        span.style.fontSize = '12px';
        span.style.visibility = 'hidden'; // 隐藏不影响布局
        document.body.appendChild(span);
        const width = span.offsetWidth;
        document.body.removeChild(span);
        return width;
      },

      // 10. 辅助函数：手动获取图片尺寸（应对图片已加载的情况）
      getImgSize() {
        const imgDom = this.$refs[`draw_img${this.Index}`];
        if (imgDom && imgDom.complete) {
          this.imgOriginWidth = imgDom.naturalWidth;
          this.imgOriginHeight = imgDom.naturalHeight;
          this.getImgContainerSize(imgDom);
          this.calcAllMarks();
        }
      },
    },
  };
</script>

<style scoped lang="scss">
  .flex-sty {
    width: 100%;
    height: 100%;
  }

  .drag-inner {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: hidden; /* Prevent container overflow */

    /* SVG标记层：绝对定位，与图片重叠 */
    .canvas {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 2;
      pointer-events: none; /* Don't block clicks */
    }

    /* 矩形框标记：绝对定位，层级高于SVG */
    .xbox {
      position: absolute;
      border: 2px solid #f43838;
      background-color: rgba(255, 0, 0, 0.2);
      z-index: 3; /* Ensure visible on top */

      .text {
        width: 100%;
        height: 20px;
        line-height: 20px;
        font-size: 12px;
        color: #fff;
        background-color: rgba(255, 0, 0, 0.4);
      }

      .textA {
        line-height: 20px;
        font-size: 12px;
        color: #fff;
        background-color: rgba(255, 0, 0, 0.4);
      }

      .text_P {
        position: absolute;
        top: -20px;
        right: -2px;
        white-space: nowrap; /* Prevent text wrap */
      }
    }

    /* 图片样式：确保充满容器且不拉伸 */
    .img-content {
      width: 100%;
      height: 100%;
      object-fit: contain; /* Keep aspect ratio，Avoid stretching */
      display: block;
    }
  }
</style>
