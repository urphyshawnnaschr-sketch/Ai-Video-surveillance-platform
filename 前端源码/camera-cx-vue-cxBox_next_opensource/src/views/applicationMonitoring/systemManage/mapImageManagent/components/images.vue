<template>
  <div class="div-annotation-container">
    <h2>{{ title }}</h2>
    <div class="image-container" ref="imageContainer" @click="addMarker">
      <img
        :src="imageUrl"
        ref="image"
        @load="initContainer"
        class="annotated-image"
      />
      <!-- <div
        v-for="(marker, index) in markers"
        :key="index"
        class="marker"
        :style="{
          left: `${marker.x}px`,
          top: `${marker.y}px`,
          width: `${iconSize}px`,
          height: `${iconSize}px`,
          backgroundImage: `url(${markerIconUrl})`,
        }"
      ></div> -->
      <!-- <a href="#"  v-for="(marker, index) in markersEd"
        :key="marker.id"
        class="marker" :style="{
          left: `${marker.x}px`,
          top: `${marker.y}px`,
          width: `${iconSize}px`,
          height: `${iconSize}px`,
          maskImage: `url(${markerIconUrl})`,
          maskRepeat: 'no-repeat',
          maskPosition: 'center',
          //backgroundColor: marker.color,
        }">
      </a> -->
      <a
        href="#"
        v-for="(marker, index) in markers"
        :key="index"
        class="marker"
        :style="{
          left: `${marker.x}px`,
          top: `${marker.y}px`,
          width: `${iconSize}px`,
          height: `${iconSize}px`,
          // maskImage: `url(${markerIconUrl})`,
          // maskSize: '100% 100%',
          // maskRepeat: 'no-repeat',
          // maskPosition: 'center',
          // WebkitMaskImage: `url(${markerIconUrl})`, // 兼容性写法
          // WebkitMaskRepeat: 'no-repeat',
          // WebkitMaskSize: '100% 100%',
          // WebkitMaskPosition: 'center',
          //backgroundColor: marker.color,
        }"
        @click="handleRightClick(marker, index)"
      >
        <img
          :src="markerIconUrl"
          style="width: 100%; height: 100%; object-fit: contain;"
        />
      </a>
      <div
        class="marker-container"
        :style="{
          left: `${currentMarker.x + 30}px`,
          top: `${currentMarker.y + 30}px`,
        }"
        v-if="showBtn"
      >
        <span
          class="marker-btn"
          style="padding-right: 10px"
          @click.stop="changeMarker()"
        >
          <i class="el-icon-rank"></i>
          <span>{{ $t("classify.toolbar.22y565") }}</span>
        </span>
        <span
          class="marker-btn"
          style="color: #dd383e"
          @click.stop="objectDelete()"
        >
          <i class="el-icon-delete"></i>
          <span>{{ $t("button.deleteText", { text: "" }) }}</span>
        </span>
      </div>
    </div>
    <!-- <div class="controls">
      <button @click="clearMarkers" class="btn clear-btn">Clear Marks</button>
      <button @click="saveImage" class="btn save-btn">Save Image</button>
    </div> -->
  </div>
</template>

<script>
//import html2canvas from 'html2canvas';

export default {
  name: "DivImageAnnotation",
  props: {
    imageUrl: {
      type: String,
      required: true,
    },
    markerIconUrl: {
      type: String,
      default: "https://cdn-icons-png.flaticon.com/512/684/684908.png",
    },
    title: {
      type: String,
      default: "",
    },
    iconSize: {
      type: Number,
      default: 30,
    },
    isAdd: {
      type: Boolean,
      default: false,
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
    markersEd: {
      type: Array,
      default: () => [],
    },
    editIndex: {
      type: Number,
      default: 0,
    },
  },
  created() {
    console.log("DivImageAnnotation created", this.markerIconUrl);
  },
  data() {
    return {
      markers: [],
      containerWidth: 0,
      containerHeight: 0,
      scaleX: 0,
      scaleY: 0,
      showBtn: false,
      currentMarker: {},
      currentIndex: 0,
    };
  },
  methods: {
    handleRightClick(marker, index) {
      this.showBtn = !this.showBtn;
      this.currentMarker = marker;
      this.currentIndex = index;
    },
    objectDelete() {
      this.showBtn = false;
      this.markers = this.markers.filter(
        (item, index) => this.currentIndex != index
      );
      this.$emit("objectDelete", this.currentMarker.id);
    },
    initContainer() {
      const img = this.$refs.image;
      this.containerWidth = img.clientWidth;
      this.containerHeight = img.clientHeight;
      // 计算缩放比例
      this.scaleX = img.naturalWidth / img.clientWidth;
      this.scaleY = img.naturalHeight / img.clientHeight;

      if (this.markersEd.length) {
        this.markers = this.markersEd.map((item) => {
          return {
            ...item,
            x: item.originX / this.scaleX - this.iconSize / 2,
            y: item.originY / this.scaleY - this.iconSize / 2,
          };
        });
      } else {
        this.markers = [];
      }

      console.log(
        this.containerWidth,
        this.containerHeight,
        this.markersEd,
        "<<<<"
      );
    },
    addMarker(event) {
      //this.clearMarkers()
      const img = this.$refs.image;
      if (!img.complete) return;
      // console.log('显示尺寸:', img.clientWidth, img.clientHeight);
      // console.log('实际尺寸:', img.naturalWidth, img.naturalHeight);

      // 获取相对原色图片坐标
      const originX = event.offsetX * this.scaleX - this.iconSize / 2;
      const originY = event.offsetY * this.scaleY - this.iconSize / 2;

      // 获取当前显示图片的坐标
      const boundedX = event.offsetX - this.iconSize / 2;
      const boundedY = event.offsetY - this.iconSize / 2;

      if (!this.isAdd && !this.isEdit) {
        //this.$message.error("请点击【添加到本图层】或【位置移动】按钮");
        return;
      }

      let data = {
        x: boundedX,
        y: boundedY,
        originX: originX,
        originY: originY,
      };

      if (this.isAdd) {
        if (this.markers.length) {
          let index = this.markers.findIndex((item) => !item.id);
          console.log(index, "/?????");
          if (index < 0) {
            this.markers.push(data);
          } else {
            this.markers[this.markers.length - 1] = data;
          }
        } else {
          this.markers.push(data);
        }
      }

      if (this.isEdit) {
        this.markers[this.editIndex] = {
          ...this.markers[this.editIndex],
          ...data,
        };
      }

      this.$forceUpdate();
      this.$emit("addPosition", data);
    },
    changeMarker() {
      this.showBtn = false;
      this.$emit("edit", {
        $index: this.currentIndex,
        row: this.currentMarker,
      });
    },
    clearMarkers() {
      this.markers = [];
    },
    async saveImage() {
      // try {
      //   const container = this.$refs.imageContainer;
      //   const canvas = await html2canvas(container);
      //   const link = document.createElement('a');
      //   link.download = 'annotated-image.png';
      //   link.href = canvas.toDataURL('image/png');
      //   link.click();
      // } catch (error) {
      //   console.error('Save Image失败:', error);
      // }
    },
  },
  watch: {
    imageUrl() {
      this.$nextTick(() => {
        this.initContainer();
      });
    },
    markersEd: {
      handler() {
        this.initContainer();
      },
      deep: true,
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
  max-width: 100%;
  position: relative;
}

.image-container {
  position: relative;
  display: inline-block;
  margin: 10px 0;
  border: 1px solid #ddd;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: crosshair;
}

.annotated-image {
  display: block;
  max-width: 100%;
  height: auto;
}

.marker {
  position: absolute;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  /* pointer-events: none; 防止标记阻挡点击事件 */
  // background: #d32f2f;
  position: absolute;
}

.controls {
  margin: 10px 0;
}

.btn {
  padding: 8px 16px;
  margin: 0 5px;
  border: none;
  cursor: pointer;
  font-size: 13px;
  transition: background-color 0.3s;
}

.clear-btn {
  background-color: #f44336;
  color: white;
}

.clear-btn:hover {
  background-color: #d32f2f;
}

.save-btn {
  background-color: #4caf50;
  color: white;
}

.save-btn:hover {
  background-color: #388e3c;
}
.marker-container {
  position: absolute;
  padding: 12px 8px;
  border-radius: 6px;
  background: #fff;

  /* --shadow-elevated */
  box-shadow: 0px 0px 1px 0px rgba(0, 0, 0, 0.3),
    0px 4px 14px 0px rgba(0, 0, 0, 0.1);
  font-size: 13px;
  font-style: normal;
  font-weight: 600;
  cursor: pointer;
  .marker-btn {
    color: #333;
    i {
      margin-right: 3px;
    }
  }
}
</style>
