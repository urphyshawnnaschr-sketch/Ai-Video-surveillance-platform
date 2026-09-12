<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      width="80%"
      top="5vh"
      :visible.sync="innerVisible"
      @closed="closed"
      append-to-body
      custom-class="roi-draw-dialog"
    >
      <div class="roi-title" slot="title">
        <h3>ROI Draw</h3>
        <p class="roi-subtitle">Define the designated area for algorithm recognition</p>
      </div>
      <div class="roi-draw-container">
        <!-- 左侧区域列表 -->
        <div class="roi-left-panel">
          
          <div class="roi-region-section">
            <div class="roi-region-header">
              <span>Draw</span>
              <span class="roi-count">{{ regionList.length }}</span>
            </div>
            <div v-if="regionList.length === 0" class="roi-empty">
              <div class="roi-empty-icon">
                <i class="el-icon-document"></i>
              </div>
              <p>No drawing</p>
              <p class="roi-empty-hint">Click canvas to start drawing</p>
            </div>
            <div v-else class="roi-region-list">
              <div
                v-for="(region, index) in regionList"
                :key="index"
                class="roi-region-item"
                :class="{ active: currentRegionIndex === index }"
                @click="selectRegion(index)"
              >
                <div class="roi-region-dot" :style="{ backgroundColor: region.color }"></div>
                <span class="roi-region-name">{{ region.name }}</span>
                <i class="el-icon-delete roi-region-delete" @click.stop="deleteRegion(index)"></i>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧图片绘制区域 -->
        <div class="roi-right-panel">
          <div class="roi-right-header">
            <div class="roi-status-bar">
              <span v-if="isDrawing && currentPointCount > 0" class="roi-status-text">
                <template v-if="nameEn == 'person_tracker' || nameEn == 'peopleTrack'">
                  Marked{{ currentPointCount }}point(s){{ currentPointCount < 2 ? ',At least' + (2 - currentPointCount) + 'point(s)' : ',Can Draw' }}
                </template>
                <template v-else>
                  Marked{{ currentPointCount }}point(s){{ currentPointCount < 3 ? ',At least' + (3 - currentPointCount) + 'point(s)' : ',Can Draw' }}
                </template>
              </span>
              <span v-else-if="isDrawing" class="roi-status-text">
                Click to start drawing
              </span>
              <span v-else class="roi-status-text">
                Click to start drawing
              </span>
            </div>
          </div>
          <div class="roi-draw-area">
            <MarkDetail
              ref="markDetail"
              v-if="detailObj.fileName"
              :fileUrl="$common.handleCameraImgUrl(detailObj.fileName)"
              :dataList="currentRegionIndex !== null && isDrawing ? formattedDataList : (isDrawing ? [] : [])"
              :dataListAll="regionListWithColors"
              :nameEn="nameEn"
              :disabled="drawDisabled"
              :isShow="false"
              :isLineMode="isLineMode"
              @saveList="saveList"
            />
          </div>
          <div class="roi-right-footer">
            <!-- 完成绘制按钮：绘制中时显示 -->
            <el-button
              type="primary"
              class="roi-complete-btn"
              :class="{ 'btn-hidden': !isDrawing }"
              @click="completeDraw"
            >
              <i class="el-icon-check"></i>
              <span>Draw</span>
            </el-button>
            <!-- 绘制下一区域按钮：不在绘制中时显示，放在完成绘制按钮的位置 -->
            <el-button
              type="primary"
              class="roi-draw-next-btn"
              :class="{ 'btn-hidden': isDrawing }"
              @click="newDraw"
              v-if="nameEn != 'person_tracker' || nameEn != 'peopleTrack'"
            >
              <i class="el-icon-plus"></i>
              <span>Draw Next</span>
            </el-button>
            <el-button
              type="primary"
              class="roi-draw-next-btn"
              :class="{ 'btn-hidden': isDrawing }"
              v-if="nameEn == 'person_tracker' || nameEn == 'peopleTrack'"
              @click="LineDraw"
              >Draw Line</el-button
            >
            <el-button
              class="roi-clear-btn"
              :class="{ 'btn-hidden': !isDrawing }"
              @click="clearDraw"
            >
              <i class="el-icon-refresh-left"></i>
              <span>Clear</span>
            </el-button>
            <!-- 绘制状态提示框 -->
            <div v-if="isDrawing" class="roi-drawing-status">
              <span class="roi-drawing-status-text">Drawing...</span>
            </div>
            <div v-else class="roi-drawing-status">
              <span class="roi-drawing-status-text">Select left region to,or Draw New</span>
            </div>
            <el-button
              type="success"
              class="roi-save-btn"
              @click="saveDraw"
            >
              <i class="el-icon-document-checked"></i>
              <span>SaveConfiguration</span>
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import MarkDetail from "./markDetail.vue";
import {
  saveInfo,
  deleteInfo,
} from "@/api/applicationMonitoring/cameraManagement";

export default {
  components: {
    MarkDetail,
  },
  props: {
    detailObj: {
      type: Object,
      default: () => {},
    },
    dataList: {
      type: Array,
      default: [],
    },
    dataListAll: {
      type: Array,
      default: [],
    },
    algorithmId: {
      type: String,
      default: "",
    },
    nameEn: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      innerVisible: true,
      drawDisabled: true,
      newList: [],
      regionList: [],
      currentRegionIndex: null,
      isDrawing: false,
      currentPointCount: 0,
      isLineMode: false, // 默认是绘制区域模式，点击绘制线才切换为线模式
    };
  },
  computed: {
    regionListWithColors() {
      // 将区域列表转换为 markDetail 需要的格式，包含颜色信息
      // 如果正在编辑某个区域，返回空数组，不显示任何已完成的区域（包括红框区域）
      if (this.currentRegionIndex !== null && this.isDrawing) {
        // 编辑模式下，不显示任何已完成的区域，只显示当前正在编辑的区域（通过 dataList 传递）
        return [];
      }
      // 如果不在编辑模式，返回所有区域
      return this.regionList.map((region, index) => ({
        name: region.name || `Region${index + 1}`,
        markPoints: region.markPoints || [],
        color: region.color,
        r: region.r,
        g: region.g,
        b: region.b,
      }));
    },
    formattedDataList() {
      // 格式化当前选中区域的数据为 markDetail 需要的格式
      if (this.currentRegionIndex === null || !this.regionList[this.currentRegionIndex]) {
        return [];
      }
      const region = this.regionList[this.currentRegionIndex];
      if (!region.markPoints || region.markPoints.length === 0) {
        return [];
      }
      // 转换为 markDetail 期望的格式：[[{x, y}, {x, y}, ...], ...]
      return region.markPoints.map(mark => {
        if (mark.pointList && Array.isArray(mark.pointList)) {
          return mark.pointList;
        }
        return [];
      }).filter(arr => arr.length > 0);
    },
  },
  async created() {
    this.initRegionList();
    // 如果有已有区域，默认不处于绘制状态；否则默认可以开始绘制
    if (this.regionList.length > 0) {
      this.isDrawing = false;
      this.drawDisabled = true;
    } else {
      this.isDrawing = true;
      this.drawDisabled = false;
    }
  },
  methods: {
    // 初始化区域列表
    initRegionList() {
      // 优先使用 dataList（当前算法的数据），如果没有则从 dataListAll 中过滤当前算法的数据
      if (this.dataList && this.dataList.length > 0) {
        // 使用 dataList（当前算法的数据）
        let regionIndex = 1;
        this.regionList = [];
        this.dataList.forEach((points) => {
          const color = this.generateRandomColor();
          this.regionList.push({
            name: `Region${regionIndex}`,
            markPoints: [{
              pointList: points,
              pointText: points.map(p => `${p.x},${p.y}`).join(' ')
            }],
            ...color,
          });
          regionIndex++;
        });
      } else if (this.dataListAll && this.dataListAll.length > 0) {
        // 从父组件传入的数据初始化
        // 根据 algorithmId 过滤，只回显当前算法的区域
        // 在内部，始终使用"区域一"、"区域二"等名称，并为每个区域生成随机颜色
        // 将每个 markPoint 拆分为独立的区域对象
        this.regionList = [];
        let regionIndex = 1;
        
        // 过滤出当前算法的数据
        // 只处理 algorithmId 匹配的数据，没有绘制区域的算法不会回显
        this.dataListAll.forEach((item) => {
          // 只有当 algorithmId 匹配时才处理（如果 algorithmId 为空，说明是旧数据，不处理）
          if (this.algorithmId && item.algorithmId === this.algorithmId) {
            if (item.markPoints && item.markPoints.length > 0) {
              // 为每个 markPoint 创建一个独立的区域对象
              item.markPoints.forEach((markPoint) => {
                const color = this.generateRandomColor();
                this.regionList.push({
                  name: `Region${regionIndex}`, // 制使用"一"、"二"等
                  markPoints: [markPoint], // 每个区域对象只包含一个 markPoint
                  color: color.color, // 每个区域使用随机颜色
                  r: color.r,
                  g: color.g,
                  b: color.b,
                });
                regionIndex++;
              });
            }
          }
        });
      }
    },
    // 生成随机颜色
    generateRandomColor() {
      const r = Math.floor(Math.random() * 256);
      const g = Math.floor(Math.random() * 256);
      const b = Math.floor(Math.random() * 256);
      return {
        r,
        g,
        b,
        color: `rgb(${r}, ${g}, ${b})`,
      };
    },
    // 选择区域
    selectRegion(index) {
      this.currentRegionIndex = index;
      // 选中区域时，进入编辑模式，显示完成绘制和清除按钮
      this.isDrawing = true;
      this.drawDisabled = false;
      // 更新点数
      const region = this.regionList[index];
      if (region && region.markPoints && region.markPoints.length > 0) {
        // 统计所有点的数量
        let totalPoints = 0;
        region.markPoints.forEach(mark => {
          if (mark.pointList && Array.isArray(mark.pointList)) {
            totalPoints += mark.pointList.length;
          }
        });
        this.currentPointCount = totalPoints;
      }
      // 加载区域数据到 markDetail
      this.$nextTick(() => {
        if (this.$refs.markDetail) {
          // 设置 dataList，让 markDetail 加载区域数据
          this.newList = this.formattedDataList;
          // 等待 dataList watch 处理完成后再触发 draggingChange
          setTimeout(() => {
            if (this.$refs.markDetail && this.$refs.markDetail.svgList.length > 0) {
              // 选中第一个区域（因为 formattedDataList 只包含当前选中的区域）
              this.$refs.markDetail.draggingChange(0);
            }
          }, 100);
        }
      });
    },
    // 编辑区域
    editRegion(index) {
      this.currentRegionIndex = index;
      this.isDrawing = true;
      this.drawDisabled = false;
      this.currentPointCount = 0;
      // 加载区域数据到 markDetail
      this.$nextTick(() => {
        if (this.$refs.markDetail) {
          this.newList = this.formattedDataList;
          this.$refs.markDetail.clear();
          this.$refs.markDetail.resetting();
        }
      });
    },
    // 删除区域
    deleteRegion(index) {
      this.$confirm('Are you sure to change the region?？', 'Tip', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }).then(() => {
        this.regionList.splice(index, 1);
        if (this.currentRegionIndex === index) {
          this.currentRegionIndex = null;
          this.isDrawing = false;
          this.drawDisabled = true;
        } else if (this.currentRegionIndex > index) {
          this.currentRegionIndex--;
        }
        // 如果删除后没有区域了，允许开始绘制
        if (this.regionList.length === 0) {
          this.isDrawing = true;
          this.drawDisabled = false;
        }
      }).catch(() => {});
    },
    // 开始绘制新区域
    newDraw() {
      // 重置所有状态，确保从干净的状态开始
      this.currentRegionIndex = null;
      this.isDrawing = true;
      this.drawDisabled = false;
      this.currentPointCount = 0;
      this.newList = [];
      this.isLineMode = false; // 默认是绘制区域模式
      this.$nextTick(() => {
        if (this.$refs.markDetail) {
          // 先清空所有状态
          this.$refs.markDetail.clear();
          // 然后重置为可绘制状态
          this.$refs.markDetail.resetting();
        }
      });
    },
    // 保存当前绘制的点
    saveList(list) {
      // 确保只在绘制状态下更新点数
      if (!this.isDrawing) {
        return;
      }
      
      if (list && list.length > 0 && list[0]) {
        // 兼容两种数据格式
        if (list[0].pointList && Array.isArray(list[0].pointList)) {
          // 确保正确统计所有点
          this.currentPointCount = list[0].pointList.length;
        } else if (Array.isArray(list[0])) {
          this.currentPointCount = list[0].length;
        } else {
          this.currentPointCount = 0;
        }
      } else {
        this.currentPointCount = 0;
      }
    },
    // 完成绘制
    completeDraw() {
      if (!this.$refs.markDetail) {
        return;
      }
      // 保存当前绘制的区域
      const savedRegions = this.$refs.markDetail.save();
      // 使用 isLineMode 状态判断是绘制线还是绘制区域
      const minPoints = this.isLineMode ? 2 : 3;
      
      if (savedRegions.length > 0 && savedRegions[0].pointList.length >= minPoints) {
        if (this.currentRegionIndex !== null) {
          // 如果是编辑模式，更新现有区域
          const color = this.regionList[this.currentRegionIndex];
          this.regionList[this.currentRegionIndex] = {
            name: color.name, // 保持原有名称，不可修改
            markPoints: savedRegions,
            color: color.color,
            r: color.r,
            g: color.g,
            b: color.b,
          };
        } else {
          // 如果是新建模式，为每个保存的区域创建一个独立的区域对象
          // 这样每个区域（三角形、线等）都有独立的名称和颜色
          savedRegions.forEach((savedRegion) => {
            if (savedRegion.pointList && savedRegion.pointList.length >= minPoints) {
              const color = this.generateRandomColor();
              this.regionList.push({
                name: `Region${this.regionList.length + 1}`,
                markPoints: [savedRegion], // 每个区域对象只包含一个 markPoint
                ...color,
              });
            }
          });
        }
        // 清除当前绘制
        this.$refs.markDetail.clear();
        this.currentRegionIndex = null;
      } else {
        const minPoints = this.isLineMode ? 2 : 3;
        this.$message.warning(`At least${minPoints}points to draw`);
        return;
      }
      // 重置绘制状态
      this.isDrawing = false;
      this.currentPointCount = 0;
      this.drawDisabled = true;
      this.newList = [];
      this.isLineMode = false; // 完成绘制后，重置为区域模式
    },
    // 清除当前绘制
    clearDraw() {
      if (this.$refs.markDetail) {
        this.$refs.markDetail.clear();
        // 重置 markDetail 状态，允许重新绘制
        this.$refs.markDetail.resetting();
      }
      this.currentPointCount = 0;
      // 如果是在编辑模式，清除后保持在编辑模式，允许重新绘制
      if (this.currentRegionIndex !== null) {
        // 保持在编辑模式，不清除 currentRegionIndex
        // 保持在绘制状态，不清除 isDrawing
        // 保持可绘制状态，不清除 drawDisabled
        this.newList = []; // 清空数据列表，让用户可以重新绘制
      } else {
        // 如果是新建模式，清除后退出绘制
        this.isDrawing = false;
        this.drawDisabled = true;
      }
    },
    // 取消绘制
    cancelDraw() {
      this.$emit("close");
    },
    // 保存绘制
    async saveDraw() {
      // if (this.regionList.length === 0) {
      //   this.$message.warning('请至少绘制一个区域');
      //   return;
      // }
      
      let markPointsArr = [];
      let lineMarkPoints = [];
      
      // 收集所有区域的标记点
      this.regionList.forEach((region) => {
        if (region.markPoints && region.markPoints.length > 0) {
          region.markPoints.forEach((mark) => {
            if (mark.pointList && mark.pointList.length > 0) {
              if (
                (this.nameEn == "person_tracker" || this.nameEn == "peopleTrack") &&
                mark.pointList.length == 2
              ) {
                lineMarkPoints.push(mark.pointList);
              } else {
                markPointsArr.push(mark.pointList);
              }
            }
          });
        }
      });
      console.log(markPointsArr,"======",markPointsArr.length)
      // 验证点数
      if (markPointsArr.length > 0) {
        for (var i = 0; i < markPointsArr.length; i++) {
          if (markPointsArr[i].length < 3) {
            this.$message.error(this.$t('addcamera.drawdialog.1334e1'));
            return;
          }
        }
      }

      // 为整个算法生成一个统一的颜色（用于在.vue中显示）
      // 在内部，每个区域保持独立颜色；但返回给时，同一算法的所有区域使用同一颜色
      const algorithmColor = this.generateRandomColor();
      
      let obj = {
        algorithmId: this.algorithmId,
        lineMarkPoints: lineMarkPoints,
        markPointsArr: markPointsArr,
        // 返回时，同一算法的所有区域使用同一个颜色
        colorInfoList: this.regionList.map(region => ({
          name: region.name,
          color: algorithmColor.color,
          r: algorithmColor.r,
          g: algorithmColor.g,
          b: algorithmColor.b,
        })),
      };
      this.innerVisible = false;
      this.$emit("close", obj);
    },
    closed() {
      this.$emit("close");
    },
    // 绘制线（只需要两个点）
    LineDraw() {
      // 如果有正在绘制的区域，先保存
      if (this.$refs.markDetail && this.$refs.markDetail.svgList.length > 0) {
        const currentRegion = this.$refs.markDetail.svgList[this.$refs.markDetail.svgList.length - 1];
        if (currentRegion.pointList.length > 0 && currentRegion.pointList.length < 2) {
          // 如果当前区域有点但少于2个，继续绘制
          this.currentRegionIndex = null;
          this.isDrawing = true;
          this.drawDisabled = false;
          this.currentPointCount = currentRegion.pointList.length;
          this.isLineMode = true; // 切换到绘制线模式
          return;
        } else if (currentRegion.pointList.length >= 2) {
          // 如果已经有2个点，完成当前线的绘制
          this.completeDraw();
          return;
        }
      }
      // 开始绘制新线
      this.currentRegionIndex = null;
      this.isDrawing = true;
      this.drawDisabled = false;
      this.currentPointCount = 0;
      this.newList = [];
      this.isLineMode = true; // 切换到绘制线模式
      this.$nextTick(() => {
        if (this.$refs.markDetail) {
          this.$refs.markDetail.clear();
          this.$refs.markDetail.resetting();
        }
      });
    },
  },
};
</script>

<style scoped lang="scss">
.roi-draw-container {
  display: flex;
  // height: 70vh;
  // padding-top: 20px;
}

.roi-left-panel {
  width: 300px;
  border-right: 1px solid #E2E8F0;
  display: flex;
  flex-direction: column;
  background: #F8FAFC;
}

.roi-title {
  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #1E293B;
  }
  
  .roi-subtitle {
    font-size: 12px;
    color: #64748B;
  }
}

.roi-region-section {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.roi-region-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #1E293B;
  
  .roi-count {
    color: #64748B;
    font-weight: normal;
  }
}

.roi-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #94A3B8;
  
  .roi-empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
    opacity: 0.5;
  }
  
  p {
    margin: 4px 0;
    font-size: 14px;
  }
  
  .roi-empty-hint {
    font-size: 12px;
    color: #CBD5E1;
  }
}

.roi-region-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.roi-region-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #FFFFFF;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  
  &:hover {
    border-color: #2B7FFF;
    background: #F0F7FF;
  }
  
  &.active {
    border-color: #2B7FFF;
    background: #F0F7FF;
  }
  
  .roi-region-dot {
    width: 6px;
    height: 100%;
    border-top-left-radius: 8px;
    border-bottom-left-radius: 8px;
    position: absolute;
    top: 0px;
    left: 0px;
  }
  
  .roi-region-name {
    flex: 1;
    font-size: 14px;
    color: #1E293B;
    margin-left: 10px;
    // text-align: center;
  }
  
  .roi-region-delete {
    font-size: 16px;
    color: #94A3B8;
    cursor: pointer;
    padding: 4px;
    
    &:hover {
      color: #EF4444;
    }
  }
}

.roi-left-footer {
  padding: 16px;
  border-top: 1px solid #E2E8F0;
}

// .roi-draw-next-btn 样式已移到右侧底部，与 .roi-complete-btn 共享样式

.roi-right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #FFFFFF;
}

.roi-right-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #1E293B;
  color: #FFFFFF;
}

.roi-status-bar {
  flex: 1;
  text-align: center;
  .roi-status-text {
    color: #E53935;
    font-size: 12px;
    padding: 5px 10px;
    border-radius: 10px;
    border: 1px solid #BEDBFF;
    background: rgba(255, 255, 255, 0.95);
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.10), 0 4px 6px -4px rgba(0, 0, 0, 0.10);
  }
}

.roi-close-btn {
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
  
  &:hover {
    opacity: 0.7;
  }
}

.roi-draw-area {
  flex: 1;
  // overflow: auto;
  position: relative;
}

.roi-right-footer {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #E2E8F0;
  background: #F8FAFC;
}

.roi-complete-btn,
.roi-draw-next-btn,
.roi-clear-btn,
.roi-save-btn {
  border-radius: 10px;
  
  &.btn-hidden {
    display: none;
  }
}

.roi-complete-btn,
.roi-draw-next-btn {
  background: linear-gradient(90deg, #2B7FFF 0%, #E53935 100%);
  border: none;
  
  &:hover {
    opacity: 0.9;
  }
}

.roi-clear-btn {
  background: #FFFFFF;
  border: 1px solid #E2E8F0;
  color: #64748B;
  
  &:hover {
    border-color: #CBD5E1;
    color: #475569;
  }
}

.roi-drawing-status {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: auto;
  margin-right: 12px;
  
  .roi-drawing-status-text {
    font-size: 12px;
    color: #94A3B8;
    white-space: nowrap;
  }
}

.roi-save-btn {
  margin-left: 0;
}
</style>

<style lang="scss">
.roi-draw-dialog {
  .el-dialog__body {
    padding: 0;
  }
  .el-dialog__header{
    border-bottom: 1px solid #E2E8F0;
    padding-bottom: 10px;
  }
}
</style>
