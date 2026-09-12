<template>
  <div class="flex-sty">
    <div v-if="isShow" class="list-sty">
      <div v-for="(items, ind) in svgList" class="list-item">
        <div
          class="title"
          :style="{ color: items.dragging ? '#E53935' : '' }"
          @click="draggingChange(ind)"
        >
          {{ $t("addcamera.markdetail.1xx58c") }}{{ ind + 1 }}
        </div>
        <div class="del" @click="delFun(ind)">
          {{ $t("button.deleteText", { text: "" }) }}
        </div>
      </div>
    </div>
    <div class="drag-inner" :style="{ height: imgHeight ? imgHeight : 'auto', position: 'relative' }">
      <!-- 已完成的区域 SVG，显示已保存的区域 -->
      <div v-if="dataListAll && dataListAll.length > 0" style="position: absolute; top: 0; left: 0; z-index: 1; pointer-events: none; width: 100%; height: 100%;">
        <svg class="canvas" v-for="(items, ind) in svgListAll" :key="ind" style="pointer-events: none; width: 100%; height: 100%;">
          <g v-for="(item, index) in items.markPoints" :key="index">
            <text
              :x="item.pointList[0].x - 10"
              :y="item.pointList[0].y - 10"
              :fill="items.color"
              style="font-size: 13px; font-weight: bold"
            >
              {{ items.name }}
            </text>
            <polygon
              :points="item.pointText"
              :style="{
                fill:
                  'rgba(' + items.r + ',' + items.g + ',' + items.b + ',0.5)',
                stroke: 'rgb(' + items.r + ',' + items.g + ',' + items.b + ')',
                'stroke-width': 2,
              }"
            />
            <g
              v-for="(pointItem, pointIndex) in item.pointList"
              :key="pointIndex"
            >
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
      </div>
      <!-- 绘制中的 SVG，显示当前正在绘制的点和连线 -->
      <svg
        class="canvas"
        @click="draw"
        @mousemove="mousemoveFun"
        @mouseup="mouseUpFun"
        @mouseleave="mouseUpFun"
        :style="{
          position: 'absolute',
          top: 0,
          left: 0,
          zIndex: 10,
          pointerEvents: 'auto',
          width: '100%',
          height: '100%'
        }"
      >
        <g v-for="(item, index) in svgIndexList" :key="index">
          <!-- 当有2个或更多点时，显示连线（会自动连接所有点） -->
          <polyline
            v-if="item.pointList && item.pointList.length >= 2"
            :points="item.pointText"
            :style="{
              stroke: '#1dfff9',
              strokeWidth: 2,
              fill: 'none',
            }"
          />
          <!-- 当有3个或更多点时，显示填充的多边形区域（会自动闭合） -->
          <polygon
            v-if="item.pointList && item.pointList.length >= 3"
            :points="item.pointText"
            :style="{
              stroke: '#1dfff9',
              strokeWidth: 2,
              fill: item.dragging ? 'rgba(29, 255, 249, 0.4)' : 'rgba(29, 255, 249, 0.2)',
            }"
          />
          <!-- 显示所有点 -->
          <g
            v-for="(pointItem, pointIndex) in item.pointList"
            :key="pointIndex"
          >
            <rect
              :x="pointItem.x - 3"
              :y="pointItem.y - 3"
              width="6"
              height="6"
              fill="#ff1d23"
              @mousedown.stop="draggingFun(pointIndex, $event)"
              style="cursor: move;"
            ></rect>
          </g>
        </g>
      </svg>

      <img
        class="bg-img"
        id="draw_img"
        draggable="false"
        ref="draw_img"
        :src="fileUrl"
      />
    </div>
  </div>
</template>

<script>
export default {
  props: {
    fileUrl: {
      type: String,
      default: "",
    },
    dataList: {
      type: Array,
      default: () => [],
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    dataListAll: {
      type: Array,
      default: () => [],
    },
    nameEn: {
      type: String,
      default: "",
    },
    isLineMode: {
      type: Boolean,
      default: false,
    },
    isShow: {
      type: Boolean,
      default: false,
    },
    pageType: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      svgList: [],
      pointCount: 0,
      pointList: [],
      finish: true,
      index: null,
      pointIndex: null,
      isTrue: false,
      isDisabled: false,
      svgListAll: [],
      imgWidth: "",
      imgHeight: "",
      svgIndexList: [],
    };
  },
  watch: {
    disabled: {
      immediate: true,
      handler(val) {
        this.isDisabled = val;
      },
    },
    dataList: {
      immediate: true,
      handler(val) {
        // 如果正在绘制中（finish === false），不要清空 svgList
        // 只有在初始化或外部数据更新时才重新构建
        if (val.length > 0 && this.finish) {
          this.$nextTick(() => {
            this.pointList = [];
            let img = new Image();
            img.src = this.fileUrl;
            img.onload = () => {
              this.imgWidth = img.width;
              setTimeout(() => {
                this.imgHeight = this.$refs.draw_img.offsetHeight + "px";
                const ratio = this.imgWidth / this.$refs.draw_img.offsetWidth;
                console.log(ratio);
                this.finish = true;
                this.svgList = [];
                this.svgIndexList = [];
                val.forEach((item) => {
                  let pointText = "";
                  let pointArr = [];
                  console.log(item, "=");
                  item.forEach((i) => {
                    pointArr.push({
                      x: Math.ceil(i.x / ratio),
                      y: Math.ceil(i.y / ratio),
                    });
                    pointText =
                      pointText +
                      ` ${Math.ceil(i.x / ratio)},${Math.ceil(i.y / ratio)}`;
                  });
                  this.svgList.push({
                    pointList: pointArr,
                    pointText: pointText,
                    dragging: false,
                  });
                  this.svgIndexList.push({
                    pointList: pointArr,
                    pointText: pointText,
                    dragging: false,
                  });
                });
                if (this.index != null) {
                  this.svgIndexList = [];
                  this.svgList[this.index].dragging = true;
                  this.svgIndexList.push(this.svgList[this.index]);
                }
              }, 50);
            };
          });
        } else if (val.length === 0 && this.finish) {
          // 只有在 finish 为 true 时才清空（初始化时）
          // 如果正在绘制（finish === false），不清空，保持当前绘制的点
          // 不自动设置 isDisabled，应该由 disabled prop 控制
          this.finish = true;
          // 不清空 svgList，保持当前绘制的点
        }
      },
    },
    dataListAll: {
      immediate: true,
      handler(val) {
        if (val.length > 0) {
          this.svgListAll = [];
          this.$nextTick(() => {
            this.pointList = [];
            let img = new Image();
            img.src = this.fileUrl;
            img.onload = () => {
              this.imgWidth = img.width;
              setTimeout(() => {
                this.imgHeight = this.$refs.draw_img.offsetHeight + "px";
                const ratio = this.imgWidth / this.$refs.draw_img.offsetWidth;
                val.forEach((item) => {
                  let newMarkArr = [];
                  item.markPoints.forEach((items) => {
                    let arr = [];
                    let pointText = "";
                    items.pointList.forEach((ite) => {
                      arr.push({
                        x: Math.ceil(ite.x / ratio),
                        y: Math.ceil(ite.y / ratio),
                      });
                      pointText =
                        pointText +
                        ` ${Math.ceil(ite.x / ratio)},${Math.ceil(
                          ite.y / ratio
                        )}`;
                    });
                    newMarkArr.push({
                      pointList: arr,
                      pointText: pointText,
                    });
                  });
                  this.svgListAll.push({
                    b: item.b,
                    r: item.r,
                    g: item.g,
                    color: item.color,
                    markPoints: newMarkArr,
                    name: item.name,
                  });
                });
                console.log(this.svgListAll);
              }, 50);
            };
          });
        } else {
          this.svgListAll = [];
        }
      },
    },
  },
  created() {
    this.isDisabled = this.disabled;
    setTimeout(() => {
      this.imgHeight = this.$refs.draw_img.offsetHeight + "px";
      if (this.pageType == "add") {
        this.$emit("getHeight", this.$refs.draw_img.offsetHeight);
      }
    }, 50);
  },
  methods: {
    // 标记
    draw(e) {
      if (this.isDisabled) {
        return;
      }
      // 如果正在拖动点，不处理点击事件
      if (this.isTrue) {
        return;
      }
      // if(this.dataList.length > 1){
      //   this.$message.warning("请删除之后再重新绘制");
      //     return
      // }
      if (
        this.svgList.length - 1 > 0 &&
        this.isLineMode
      ) {
        if (this.svgList[this.svgList.length - 1].pointList.length == 2) {
          this.$message.warning(this.$t("addcamera.markdetail.w483uo"));
          return;
        }
      }
      const { offsetX, offsetY } = e;
      const point = [{ x: offsetX, y: offsetY }];
      
      // 如果已经有选中的区域（index !== null），在现有区域上添加点
      if (this.index !== null && this.svgList[this.index]) {
        const obj = this.svgList[this.index];
        const objIndex = this.svgIndexList.length > 0 && this.svgIndexList[0] === this.svgList[this.index] 
          ? this.svgIndexList[0] 
          : null;
        
        // 检查是否重复点
        const isDuplicate = obj.pointList.some(
          (p) => Math.abs(p.x - offsetX) < 5 && Math.abs(p.y - offsetY) < 5
        );
        
        if (!isDuplicate) {
          // 如果是绘制线模式，最多只能有2个点
          if (this.isLineMode && obj.pointList.length >= 2) {
            // 如果已经有2个点，不允许再添加
            this.$message.warning(this.$t("addcamera.markdetail.w483uo"));
            return;
          }
          
          // 直接添加点
          obj.pointList.push({ x: offsetX, y: offsetY });
          // 去重，确保没有重复的点
          const uniquePoints = [];
          const pointSet = new Set();
          for (let i = 0; i < obj.pointList.length; i++) {
            const p = obj.pointList[i];
            const pointKey = `${p.x},${p.y}`;
            if (!pointSet.has(pointKey)) {
              pointSet.add(pointKey);
              uniquePoints.push(p);
            }
          }
          obj.pointList = uniquePoints;
          // 重新生成 pointText
          obj.pointText = obj.pointList.map(p => `${p.x},${p.y}`).join(' ');

          // 同步更新 svgIndexList
          if (objIndex) {
            objIndex.pointList.push({ x: offsetX, y: offsetY });
            // 去重，确保没有重复的点
            const uniquePointsIndex = [];
            const pointSetIndex = new Set();
            for (let i = 0; i < objIndex.pointList.length; i++) {
              const p = objIndex.pointList[i];
              const pointKey = `${p.x},${p.y}`;
              if (!pointSetIndex.has(pointKey)) {
                pointSetIndex.add(pointKey);
                uniquePointsIndex.push(p);
              }
            }
            objIndex.pointList = uniquePointsIndex;
            // 重新生成 pointText
            objIndex.pointText = objIndex.pointList.map(p => `${p.x},${p.y}`).join(' ');
          }
          
          // 每次添加点后，立即触发 saveList 事件以同步点数
          this.emitSaveList();
        }
        return;
      }
      
      // 如果没有选中的区域，按原来的逻辑处理
      if (this.finish) {
        this.finish = false;
        this.svgList.push({
          pointList: point,
          pointText: `${point[0].x},${point[0].y}`,
          dragging: false,
        });
        this.svgIndexList.push({
          pointList: point,
          pointText: `${point[0].x},${point[0].y}`,
          dragging: false,
        });
        // 添加第一个点后立即触发事件
        this.emitSaveList();
      } else {
        const obj = this.svgList[this.svgList.length - 1];
        const objIndex = this.svgIndexList[this.svgIndexList.length - 1];
        
        // 检查是否重复点
        const isDuplicate = obj.pointList.some(
          (p) => Math.abs(p.x - offsetX) < 5 && Math.abs(p.y - offsetY) < 5
        );
        
        if (!isDuplicate) {
          // 如果是绘制线模式，最多只能有2个点
          if (this.isLineMode && obj.pointList.length >= 2) {
            // 如果已经有2个点，不允许再添加
            this.$message.warning(this.$t("addcamera.markdetail.w483uo"));
            return;
          }
          
          // 直接添加点
          obj.pointList.push({ x: offsetX, y: offsetY });
          // 去重，确保没有重复的点
          const uniquePoints = [];
          const pointSet = new Set();
          for (let i = 0; i < obj.pointList.length; i++) {
            const p = obj.pointList[i];
            const pointKey = `${p.x},${p.y}`;
            if (!pointSet.has(pointKey)) {
              pointSet.add(pointKey);
              uniquePoints.push(p);
            }
          }
          obj.pointList = uniquePoints;
          // 重新生成 pointText
          obj.pointText = obj.pointList.map(p => `${p.x},${p.y}`).join(' ');

          // 同步更新 svgIndexList
          objIndex.pointList.push({ x: offsetX, y: offsetY });
          // 去重，确保没有重复的点
          const uniquePointsIndex = [];
          const pointSetIndex = new Set();
          for (let i = 0; i < objIndex.pointList.length; i++) {
            const p = objIndex.pointList[i];
            const pointKey = `${p.x},${p.y}`;
            if (!pointSetIndex.has(pointKey)) {
              pointSetIndex.add(pointKey);
              uniquePointsIndex.push(p);
            }
          }
          objIndex.pointList = uniquePointsIndex;
          // 重新生成 pointText
          objIndex.pointText = objIndex.pointList.map(p => `${p.x},${p.y}`).join(' ');
          
          // 每次添加点后，立即触发 saveList 事件以同步点数
          this.emitSaveList();
        }
      }
    },
    // 触发 saveList 事件
    emitSaveList() {
      let newArr = [];
      let ratio = this.scaleRatio();
      // 只处理当前正在绘制的区域（最后一个区域）
      if (this.svgIndexList.length > 0) {
        const lastItem = this.svgIndexList[this.svgIndexList.length - 1];
        if (lastItem && lastItem.pointList && Array.isArray(lastItem.pointList)) {
          let newPointList = [];
          // 使用 Set 来去重，确保每个点只被统计一次
          const pointSet = new Set();
          // 遍历所有点，确保每个点都被统计
          for (let i = 0; i < lastItem.pointList.length; i++) {
            const items = lastItem.pointList[i];
            if (items && typeof items.x === 'number' && typeof items.y === 'number') {
              // 使用坐标作为唯一标识，避免重复点
              const pointKey = `${items.x},${items.y}`;
              if (!pointSet.has(pointKey)) {
                pointSet.add(pointKey);
                newPointList.push({
                  x: parseInt(items.x * ratio),
                  y: parseInt(items.y * ratio),
                });
              }
            }
          }
          // 无论有多少点，都发送事件
          newArr.push({
            pointList: newPointList,
          });
        } else {
          // 如果没有 pointList，发送空数组
          newArr.push({
            pointList: [],
          });
        }
      } else {
        // 如果没有正在绘制的区域，发送空数组
        newArr.push({
          pointList: [],
        });
      }
      this.$emit("saveList", newArr);
    },
    // 重新标记
    resetting() {
      // 不在这里设置 isDisabled，应该由 disabled prop 控制
      // 只有当 disabled prop 为 false 时，isDisabled 才会是 false
      this.finish = true;
      // 重置编辑相关状态，确保可以开始新的绘制
      this.index = null;
      this.pointIndex = null;
      this.isTrue = false;
    },
    // 保存
    save() {
      let ratio = this.scaleRatio();
      let newArr = [];
      // 只保存当前正在绘制的区域（svgIndexList）
      if (this.svgIndexList.length > 0) {
        this.svgIndexList.forEach((item, ind) => {
          let newPointList = [];
          item.pointList.forEach((items, index) => {
            newPointList.push({
              x: parseInt(items.x * ratio),
              y: parseInt(items.y * ratio),
            });
          });
          newArr.push({
            pointList: newPointList,
          });
        });
      }
      return newArr;
    },
    // 清空
    clear() {
      this.svgList = [];
      this.svgIndexList = [];
      this.finish = true;
      this.pointList = [];
      // 重置编辑相关状态，确保可以开始新的绘制
      this.index = null;
      this.pointIndex = null;
      this.isTrue = false;
      // 清空后立即触发事件，通知父组件点数为0
      this.emitSaveList();
    },
    //
    scaleRatio() {
      if (this.$refs.draw_img) {
        let nw = this.$refs.draw_img.naturalWidth;
        let ow = this.$refs.draw_img.offsetWidth;
        return ow == 0 ? 0 : nw / ow;
      }
      return 0;
    },
    draggingChange(index) {
      // 选中区域时，不禁用绘制功能，允许继续添加点
      // this.isDisabled = true; // 注释掉，允许继续添加点
      this.index = index;
      // 设置当前区域为 dragging 状态
      if (this.svgList[index]) {
        this.svgList[index].dragging = true;
        // 更新 svgIndexList，只显示当前选中的区域
        this.svgIndexList = [this.svgList[index]];
        // 设置 finish 为 false，允许继续添加点
        this.finish = false;
      }
      this.isTrue = false;
      // 触发 saveList 事件，更新点数
      this.emitSaveList();
    },
    draggingFun(pointIndex, event) {
      // 阻止事件冒泡，避免触发 draw 方法
      if (event) {
        event.stopPropagation();
        event.preventDefault();
      }
      // 如果已经有选中的区域（index !== null），可以拖动点
      if (this.index !== null && this.svgList[this.index]) {
        // 确保该区域处于 dragging 状态
        this.svgList[this.index].dragging = true;
        // 更新 svgIndexList
        if (this.svgIndexList.length === 0 || this.svgIndexList[0] !== this.svgList[this.index]) {
          this.svgIndexList = [this.svgList[this.index]];
        }
        this.pointIndex = pointIndex;
        this.isTrue = true;
      }
    },
    mousemoveFun(event) {
      if (this.isTrue) {
        // 获取鼠标相对于元素的坐标
        const x = event.offsetX;
        const y = event.offsetY;
        // 更新点的位置
        this.svgList[this.index].pointList[this.pointIndex].x = x;
        this.svgList[this.index].pointList[this.pointIndex].y = y;
        // 重新生成 pointText
        this.svgList[this.index].pointText = this.svgList[this.index].pointList.map(p => `${p.x},${p.y}`).join(' ');
        
        // 同步更新 svgIndexList（svgIndexList 中只包含当前编辑的区域）
        if (this.svgIndexList.length > 0 && this.svgIndexList[0] === this.svgList[this.index]) {
          this.svgIndexList[0].pointList[this.pointIndex].x = x;
          this.svgIndexList[0].pointList[this.pointIndex].y = y;
          this.svgIndexList[0].pointText = this.svgIndexList[0].pointList.map(p => `${p.x},${p.y}`).join(' ');
        }

        // 触发 saveList 事件，更新点数
        this.emitSaveList();
      }
    },
    // 鼠标抬起事件
    mouseUpFun(event) {
      if (this.isTrue) {
        // 停止拖动
        this.isTrue = false;
        // 触发 saveList 事件，保存修改后的点
        this.emitSaveList();
      }
      // 如果点击的不是点，允许继续添加点
      if (event && event.target && event.target.tagName !== 'rect') {
        // 确保 finish 为 false，允许继续添加点
        if (this.index !== null && this.svgList[this.index]) {
          this.finish = false;
        }
      }
    },
    // 删除
    delFun(index) {
      this.svgList.splice(index, 1);
      if (this.svgList.length == 0) {
        // 不自动设置 isDisabled，应该由 disabled prop 控制
        this.finish = true;
      }
      this.svgIndexList = [];
      this.svgList.map((item) => {
        item.dragging = false;
        this.svgIndexList.push(item);
      });
    },
  },
};
</script>
<style scoped lang="scss">
.flex-sty {
  display: flex;
  .list-sty {
    padding-right: 24px;
    width: 120px;
    min-height: 30px;
    .list-item {
      padding: 10px 0px;
      font-size: 16px;
      display: flex;
      align-items: center;
      .title {
        margin-right: 16px;
        cursor: pointer;
      }
      .title:hover {
        color: #E53935;
      }
      .del {
        color: #eb3a2f;
        cursor: pointer;
        font-size: 13px;
      }
    }
  }
}
.drag-inner {
  position: relative;
  // margin: 0 auto;
  // width: 640px;
  //   height: 360px;
  width: 100%;
  flex: 1;
  .canvas {
    width: 100%;
    height: 100%;
    position: absolute;
    left: 0;
    right: 0;
    z-index: 2;
  }
  .bg-img {
    width: 100%;
    // width: 640px;
    //   height: 360px;
    // display: block;
  }
}
</style>

