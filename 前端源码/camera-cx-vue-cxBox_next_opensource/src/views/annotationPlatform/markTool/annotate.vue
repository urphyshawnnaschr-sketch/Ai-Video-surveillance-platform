<template>
  <div class="wrap">
    <ToolBar
      ref="toolBar"
      :dataList="dataList"
      @setMode="setMode"
      @changeImg="changeImg"
    />
    <div class="content">
      <div class="pagination">
        <div>
          <el-checkbox v-model="invalid">{{
            $t("marktool.annotate.w5c674")
          }}</el-checkbox>
          <el-switch
            v-model="switchVal1"
            :inactive-text="$t('marktool.annotate.xmb470')"
            style="margin-left: 20px"
          />
          <!-- <el-switch
            v-model="switchVal2"
            inactive-text="Auto Fit"
            style="margin-left: 20px"
          /> -->
        </div>
        <el-pagination
          background
          layout="prev, pager, next, jumper"
          :current-page="currentPage"
          :page-size="1"
          :pager-count="5"
          @current-change="handleCurrentChange"
          :total="total"
        />
        <span
          >{{ $t("marktool.annotate.il5my5") }}{{ (scale * 100).toFixed(2) }}%
          <el-button type="primary" @click="back">{{
            $t("marktool.annotate.13o4yx")
          }}</el-button>
          <el-button
            type="primary"
            @click="submit"
            v-if="$route.query.type == 1"
            >{{ $t("button.submitText") }}</el-button
          >
          <el-button
            type="primary"
            @click="submitReview(null)"
            v-if="$route.query.type == 2"
            >{{ $t("marktool.annotate.523uwu") }}</el-button
          >
        </span>
      </div>
      <div
        id="map"
        ref="dragWrap"
        @mouseenter="mouseEnter"
        @mouseleave="isHover = isMousedown = auxiliaryBoxVisible = false"
        @mousemove="dragMousemove"
        v-loading="loading"
        :element-loading-text="$t('marktool.annotate.6emt1s')"
      >
        <div class="auxiliaryTool" v-show="auxiliaryBoxVisible">
          <div class="auxiliaryLineX" :style="auxiliaryLineXStype"></div>
          <div class="auxiliaryLineY" :style="auxiliaryLineYStype"></div>
          <div class="auxiliaryBox" :style="auxiliaryBoxStype"></div>
        </div>
        <el-tooltip class="item" effect="dark" placement="bottom">
          <div slot="content">
            {{
              currentImgData.needReview
                ? $t("marktool.annotate.233nwi")
                : $t("marktool.annotate.566b9m")
            }}
          </div>
          <i
            v-if="
              currentImgData.review && currentImgData.review.reviewAction == 1
            "
            class="el-icon-success hege"
            style="color: #19b400"
          ></i>
          <i
            v-if="
              currentImgData.review && currentImgData.review.reviewAction == 3
            "
            class="el-icon-error hege"
            style="color: #fa5555"
          ></i>
        </el-tooltip>
        <div class="invalid" v-if="invalid">
          <img src="@/assets/images/markTool/invalid.png" />
        </div>
        <div
          class="drag-inner"
          ref="dragElement"
          :style="{ cursor: mouseType, width: '100%' }"
          @mousedown="dragMousedown"
          @mouseup.stop="isMousedown = false"
        >
          <slot>
            <div :style="canvasWH" class="canvas">
              <svg ref="svgRef" style="width: 100%; height: 100%">
                <TempSvg :modeType="modeType" @drawOver="drawOver" />
                <CurrentSvg
                  :svgData="currentSvgData"
                  :modeType="modeType"
                  v-if="currentSvgVisible && currentSvgData.svgVisible"
                />
                <SvgList
                  v-for="(item, index) in dataList"
                  :key="index"
                  :svgData="item"
                  :modeType="modeType"
                  @changeCurrent="changeCurrent"
                  v-show="item.id != currentSvgData.id && item.tagVisible"
                />
              </svg>
              <TagList
                v-for="(item, index) in dataList"
                :tagData="item"
                :key="'tag' + index"
                v-show="tagVisible && item.tagVisible"
              />
            </div>
            <img
              v-if="currentImgData.id"
              class="bg-img"
              draggable="false"
              :src="$common.handleDataset(currentImgData.id)"
              :style="imgStype"
              ref="img"
            />
          </slot>
        </div>
      </div>
    </div>
    <div class="right-wrap">
      <ul class="right-tab">
        <li
          v-for="(item, index) in rightTabList"
          :key="index"
          :class="{ active: rightActive == item }"
          @click="rightActive = item"
        >
          {{ item }}
        </li>
      </ul>
      <Tab1
        :attributeList="projectDetail.labels"
        @changeAttribute="changeAttribute"
        v-show="rightActive == $t('marktool.annotate.cot77w')"
      />
      <Tab2
        ref="tab2"
        :dataList="dataList"
        :attributeList="projectDetail.labels"
        @changeVisible="changeVisible"
        @changeCurrent="changeCurrent"
        v-show="rightActive == $t('marktool.annotate.59ocpp')"
      />
      <Tab3
        :currentImgId="currentImgData.id"
        :formData="currentImgData.review"
        @submit="submitReview"
        v-show="rightActive == $t('marktool.annotate.7yo6h2')"
      />
    </div>
  </div>
</template>

<script>
import ToolBar from "./components/annotate/toolBar";
import Tab1 from "./components/annotate/tab1";
import Tab2 from "./components/annotate/tab2";
import Tab3 from "./components/annotate/tab3";
import TempSvg from "./components/annotate/tempSvg";
import CurrentSvg from "./components/annotate/currentSvg";
import SvgList from "./components/annotate/svgList";
import TagList from "./components/annotate/tagList";
import colorList from "@/utils/colorList";
import SvgIcon from "@/components/svgIcon";
import { annotationType } from "@/utils/commonData";
import {
  getDetail,
  getAssign,
  getAssignReview,
  getHistory,
  saveCommit,
  saveReview,
  release,
} from "@/api/annotationPlatform/projectManagement";
const emptySvg = {
  id: "",
  modeType: "", // 画框类型
  boxList: [], // 画框数据
  tagName: "", // 一级名称
  labelTree: [], // tree
  name: "", // 当前名称
  color: "", // 颜色
  tagVisible: false,
  svgVisible: false,
};
export default {
  components: {
    Tab1,
    Tab2,
    Tab3,
    ToolBar,
    TempSvg,
    CurrentSvg,
    SvgList,
    TagList,
    SvgIcon,
  },
  data() {
    return {
      loading: false,
      projectId: this.$route.query.id,
      projectDetail: {},
      imgList: [], // 图片列表
      currentPage: 1, // 当前页
      total: 0, // 总数
      currentImgData: {}, // 当前图片数据
      rightTabList: [
        this.$t("marktool.annotate.cot77w"),
        this.$t("marktool.annotate.59ocpp"),
        this.$t("marktool.annotate.7yo6h2"),
      ],
      rightActive: this.$t("marktool.annotate.cot77w"),
      attributeList: [], // 所有属性标签
      modeType: "", // 工具栏选择类型
      mouseType: "", // 鼠标样式
      isMousedown: false,
      isHover: false, // 鼠标是否在容器上
      moveStart: {}, // 拖拽坐标
      imgWidth: "0px", // 图片初始宽度
      canvasWH: {
        width: "0px",
        height: "0px",
        position: "absolute",
      },
      translate: { x: 0, y: 0 }, // 容器位移样式
      scale: 1, // 初始比例
      scaleZoom: {
        // 缩放范围
        max: 10,
        min: 0.1,
      },
      currentSvgVisible: false, // 当前是否隐藏
      currentSvgData: JSON.parse(JSON.stringify(emptySvg)),
      dataList: [], // svg列表
      currentTagData: {}, // 当前属性标签
      tagVisible: true, // 标签是否全部隐藏
      imgStype: {}, // 图片滤镜样式
      invalid: false, // 标为无效图片
      switchVal1: true, // 预标注
      switchVal2: true, // 自动贴合
      auxiliaryBoxVisible: false,
      auxiliaryLineXStype: {},
      auxiliaryLineYStype: {},
      auxiliaryBoxStype: {},
      timer: null,
    };
  },
  watch: {
    dataList: {
      handler(val) {
        if (this.currentSvgData?.id) {
          const data = val.find((i) => i.id == this.currentSvgData.id);
          if (!data) {
            this.currentSvgData = JSON.parse(JSON.stringify(emptySvg));
          }
        }
      },
    },
    currentImgData: {
      handler(val) {
        this.loading = true;
        let img = new Image();
        img.src = this.$common.handleDataset(val.id);
        img.onload = () => {
          this.$refs.dragElement.style.width = `${img.width}px`;
          Object.assign(this.canvasWH, {
            width: this.$refs.img.clientWidth + "px",
            height: this.$refs.img.clientHeight + "px",
          });
          if (img.width > this.$refs.dragWrap.clientWidth) {
            this.scale = this.$refs.dragWrap.clientWidth / img.width;
            this.$refs.dragElement.style.transform = `scale(${this.scale}) translate(0px, 0px)`;
          } else {
            this.$refs.dragElement.style.transform = `scale(1) translate(0px, 0px)`;
          }
          this.loading = false;
          if (val.status) {
            const getObjectKey = (object, value) => {
              return Object.keys(object).find((key) => object[key] == value);
            };
            const treeToArray = (tree) => {
              let name = "";
              if (!tree.length) {
                return;
              }
              if (tree[0].children.length) {
                name = treeToArray(tree[0].children);
              } else {
                name = tree[0].name;
              }
              return name;
            };
            if (val.commit.isValid == undefined || val.commit.isValid == 1) {
              this.invalid = false;
            } else {
              this.invalid = true;
            }
            if (val.commit.annotations) {
              const tempColorList = [];
              val.commit.annotations.forEach((item, index) => {
                const obj = {
                  id: Math.random() + "",
                  modeType: getObjectKey(annotationType, item.annotationType), // 画框类型
                  boxList: [], // 画框数据
                  tagName: item.tagName, // 一级名称
                  labelTree: item.annotation.label, // tree
                  name: treeToArray(item.annotation.label) || item.tagName, // 当前名称
                  color: "", // 颜色
                  tagVisible: true,
                  svgVisible: true,
                };
                if (
                  getObjectKey(annotationType, item.annotationType) == "RECT"
                ) {
                  obj.boxList = [
                    [item.annotation.box[0], item.annotation.box[1]],
                    [item.annotation.box[2], item.annotation.box[1]],
                    [item.annotation.box[0], item.annotation.box[3]],
                    [item.annotation.box[2], item.annotation.box[3]],
                  ];
                }
                if (
                  getObjectKey(annotationType, item.annotationType) == "POLYGON"
                ) {
                  obj.boxList = item.annotation.line.map((i) => {
                    return [i.x, i.y];
                  });
                }
                if (
                  getObjectKey(annotationType, item.annotationType) == "POINT"
                ) {
                  obj.boxList = [
                    item.annotation.point.x,
                    item.annotation.point.y,
                  ];
                }

                const arrIndex = tempColorList.indexOf(
                  treeToArray(item.annotation.label)
                );
                let colorIndex = 0;
                if (arrIndex > -1) {
                  colorIndex = arrIndex;
                } else {
                  tempColorList.push(treeToArray(item.annotation.label));
                  colorIndex = tempColorList.length - 1;
                }
                obj.color = colorList[colorIndex];
                this.dataList.push(obj);
              });
            }
          }
        };
      },
      deep: true,
    },
  },
  async created() {
    await this.getDetail();
    if (this.$route.query.type == 1) {
      this.getAssign();
    } else {
      this.getAssignReview();
    }
  },
  mounted() {
    const dragWrap = this.$refs.dragWrap;
    // 在容器中阻止右键菜单
    dragWrap.oncontextmenu = (event) => {
      event.preventDefault();
    };
    // 绑定滚动事件
    window.addEventListener("mousewheel", this.handleScroll, false);
  },
  methods: {
    async getDetail() {
      const data = await getDetail({ id: this.projectId });
      this.projectDetail = data.data;
      if (this.projectDetail.labelTaskTimeOut) {
        let time = 0;
        this.timer = setInterval(() => {
          time = time + 1000;
          if (time >= this.projectDetail.labelTaskTimeOut * 60000) {
            clearInterval(this.timer);
            this.$alert(
              this.$t("marktool.annotate.5316q3"),
              this.$t("marktool.annotate.92l4x5"),
              {
                confirmButtonText: $t("button.sureText", { text: "" }),
                showClose: false,
                callback: async () => {
                  const res = await release({
                    projectId: this.projectId,
                    imageIds: [this.currentImgData.id],
                  });
                  this.$router.push({
                    path: "/annotationPlatform/projectManagement",
                  });
                },
              }
            );
          }
        }, 1000);
      }
    },
    async getAssignReview() {
      this.dataList = [];
      const data = await getAssignReview({
        count: 1,
        projectId: this.projectId,
      });
      if (data.data.images.length) {
        this.imgList = data.data.images;
        this.total = Number(data.data.total) + this.imgList.length;
        this.currentImgData = this.imgList[0];
        this.currentPage = this.total;
      } else {
        if (this.total == this.currentPage || data.data.total == "0") {
          this.$message.error(this.$t("marktool.annotate.p7n2eh"));
          setTimeout(() => {
            this.$router.push({
              path: "/annotationPlatform/projectManagement",
            });
          }, 50);
        } else {
          this.total = Number(data.data.total) + this.imgList.length;
          this.currentPage = this.total;
          await this.getHistory();
        }
      }
    },
    async getAssign() {
      this.dataList = [];
      const data = await getAssign({
        count: 1,
        projectId: this.projectId,
      });
      if (data.data.images.length) {
        this.imgList = data.data.images;
        this.total = Number(data.data.total) + this.imgList.length;
        this.currentPage = this.total;
        this.currentImgData = this.imgList[0];
      } else {
        if (this.total == this.currentPage || data.data.total == "0") {
          this.$message.error(this.$t("marktool.annotate.p7n2eh"));
          setTimeout(() => {
            this.$router.push({
              path: "/annotationPlatform/projectManagement",
            });
          }, 1000);
        } else {
          this.total = Number(data.data.total) + this.imgList.length;
          this.currentPage = this.total;
          await this.getHistory();
        }
      }
    },
    async getHistory() {
      this.dataList = [];
      const data = await getHistory({
        projectId: this.projectId,
        page: this.currentPage,
        limit: 1,
        type: this.$route.query.type == 1 ? 0 : 1,
      });
      if (data.data.length) {
        this.currentImgData = data.data[0];
      } else {
        this.$message.error(this.$t("marktool.annotate.p7n2eh"));
        setTimeout(() => {
          this.$router.push({
            path: "/annotationPlatform/projectManagement",
          });
        }, 1000);
      }
    },
    setMode(val) {
      switch (val) {
        case "RECT":
          this.modeType = val;
          this.mouseType = "crosshair";
          this.currentSvgData = JSON.parse(JSON.stringify(emptySvg));
          break;
        case "POLYGON":
          this.modeType = val;
          this.mouseType = "crosshair";
          this.currentSvgData = JSON.parse(JSON.stringify(emptySvg));
          break;
        case "POINT":
          this.modeType = val;
          this.mouseType = "crosshair";
          this.currentSvgData = JSON.parse(JSON.stringify(emptySvg));
          break;
        case "REVOKE":
          this.currentSvgData = JSON.parse(JSON.stringify(emptySvg));
          this.$refs.tab2.removeCurrent();
          this.$refs.toolBar.recoverList.push(
            this.$refs.toolBar.revokeList.pop()
          );
          this.dataList =
            this.$refs.toolBar.revokeList[
              this.$refs.toolBar.revokeList.length - 1
            ] || [];
          break;
        case "RECOVER":
          this.currentSvgData = JSON.parse(JSON.stringify(emptySvg));
          this.$refs.tab2.removeCurrent();
          this.$refs.toolBar.revokeList.push(
            this.$refs.toolBar.recoverList.pop()
          );
          this.dataList =
            this.$refs.toolBar.revokeList[
              this.$refs.toolBar.revokeList.length - 1
            ] || [];
          break;
        case "SHOWTAG":
          this.tagVisible = true;
          break;
        case "HIDETAG":
          this.tagVisible = false;
          break;
        case "HIDECURRENT":
          this.currentSvgData.svgVisible = false;
          this.currentSvgData.tagVisible = false;
          this.dataList.forEach((item) => {
            if (this.currentSvgData.id == item.id) {
              item.tagVisible = false;
            }
          });
          break;
        case "HIDEALL":
          this.currentSvgData.svgVisible = false;
          this.currentSvgData.tagVisible = false;
          this.dataList.forEach((item) => {
            item.svgVisible = false;
            item.tagVisible = false;
          });
          break;
        case "SHOWCURRENT":
          this.currentSvgData.svgVisible = true;
          this.currentSvgData.tagVisible = true;
          this.dataList.forEach((item) => {
            item.tagVisible = false;
            if (this.currentSvgData.id == item.id) {
              item.tagVisible = true;
            }
          });
          break;
        case "SHOWALL":
          this.currentSvgData.svgVisible = true;
          this.currentSvgData.tagVisible = true;
          this.dataList.forEach((item) => {
            item.svgVisible = true;
            item.tagVisible = true;
          });
        case "SHOWALL":
          this.currentSvgData.svgVisible = true;
          this.currentSvgData.tagVisible = true;
          this.dataList.forEach((item) => {
            item.svgVisible = true;
            item.tagVisible = true;
          });
          break;
        case "ZOOMOUT":
          this.handleScroll({ wheelDelta: -120 }, "event");
          break;
        case "ZOOMIN":
          this.handleScroll({ wheelDelta: 120 }, "event");
          break;
        case "MOVE":
          this.mouseType = "pointer";
          this.modeType = val;
          break;
        case "DELETECURRENT":
          this.currentSvgVisible = false;
          for (let i = 0; i < this.dataList.length; i++) {
            if (this.currentSvgData.id == this.dataList[i].id) {
              this.dataList.splice(i, 1);
              break;
            }
          }
          this.currentSvgData = JSON.parse(JSON.stringify(emptySvg));
          this.$refs.toolBar.revokeList.push(
            JSON.parse(JSON.stringify(this.dataList))
          );

          break;
        case "DELETEALL":
          this.$refs.toolBar.revokeList.push([]);
          this.dataList =
            this.$refs.toolBar.revokeList[
              this.$refs.toolBar.revokeList.length - 1
            ] || [];
          break;
        default:
          this.modeType = "";
          this.mouseType = "auto";
          break;
      }
    },
    // 滚轮缩放事件
    handleScroll(e, event) {
      if (this.isHover || event == "event") {
        let speed = e.wheelDelta / 120;
        if (
          e.wheelDelta > 0 &&
          Math.round(this.scale * 100) / 100 < this.scaleZoom.max
        ) {
          this.scale += 0.1 * speed;
          this.$refs.dragElement.style.transform = `scale(${this.scale}) translate(${this.translate.x}px, ${this.translate.y}px)`;
        } else if (
          e.wheelDelta < 0 &&
          Math.round(this.scale * 100) / 100 > this.scaleZoom.min
        ) {
          this.scale += 0.1 * speed;
          this.$refs.dragElement.style.transform = `scale(${this.scale}) translate(${this.translate.x}px, ${this.translate.y}px)`;
        }
      }
    },
    // 拖拽开始
    dragMousedown(event) {
      if (this.modeType == "MOVE") {
        this.moveStart.x = event.clientX;
        this.moveStart.y = event.clientY;
      }
      this.isMousedown = true;
    },
    mouseEnter() {
      this.isHover = true;
      if (
        (this.modeType == "RECT" ||
          this.modeType == "POLYGON" ||
          this.modeType == "POINT") &&
        this.projectDetail.needAuxiliaryBox
      ) {
        this.auxiliaryBoxVisible = true;
      }
    },
    // 拖拽中
    dragMousemove(event) {
      if (this.modeType == "MOVE") {
        if (this.isMousedown) {
          this.translate.x += (event.clientX - this.moveStart.x) / this.scale;
          this.translate.y += (event.clientY - this.moveStart.y) / this.scale;
          this.$refs.dragElement.style.transform = `scale(${this.scale}) translate(${this.translate.x}px, ${this.translate.y}px)`;
          this.moveStart.x = event.clientX;
          this.moveStart.y = event.clientY;
        }
      }
      if (
        (this.modeType == "RECT" ||
          this.modeType == "POLYGON" ||
          this.modeType == "POINT") &&
        this.projectDetail.needAuxiliaryBox
      ) {
        this.auxiliaryLineXStype = {
          left: 0,
          top: event.clientY - 120 + "px",
        };
        this.auxiliaryLineYStype = {
          left: event.clientX - 300 + "px",
          top: 0,
        };
        this.auxiliaryBoxStype = {
          left: event.clientX - 300 + "px",
          top: event.clientY - 120 + "px",
          width: this.projectDetail.auxiliaryBox.width + "px" || 0,
          height: this.projectDetail.auxiliaryBox.height + "px" || 0,
          marginTop:
            "-" + (this.projectDetail.auxiliaryBox.height / 2 - 1) + "px",
          marginLeft:
            "-" + (this.projectDetail.auxiliaryBox.width / 2 - 1) + "px",
        };
        console.info(this.auxiliaryBoxStype);
      }
    },
    // 拉框结束
    drawOver(val) {
      const id = Math.random();
      this.currentSvgVisible = false;
      const obj = {
        id: id + "",
        name: this.currentTagData.name,
        color: this.currentTagData.color,
        tagName: this.currentTagData.tagName,
        labelTree: this.currentTagData.labelTree,
        boxList: val,
        modeType: this.modeType,
        tagVisible: true,
        svgVisible: true,
      };
      this.$nextTick(() => {
        Object.assign(this.currentSvgData, obj);
        this.currentSvgVisible = true;
      });
      this.dataList.push(obj);
      this.$refs.toolBar.recoverList = [];
      this.$refs.toolBar.revokeList.push(
        JSON.parse(JSON.stringify(this.dataList))
      );
      this.$refs.tab2.setCurrent(obj);
    },
    // 改变标签属性
    changeAttribute(obj) {
      this.currentTagData = obj;
      this.dataList.forEach((item) => {
        if (item.id == this.currentSvgData.id) {
          item.name = obj.name;
          item.color = obj.color;
          item.tagName = obj.tagName;
          item.labelTree = obj.labelTree;
        }
      });
    },
    // 改变当前svg
    changeCurrent(val) {
      this.currentSvgVisible = true;
      this.currentSvgData = val;
    },
    // 改变隐藏
    changeVisible(val) {
      this.dataList.forEach((item) => {
        if (item.id == val.id) {
          item.svgVisible = val.svgVisible;
          item.tagVisible = val.tagVisible;
        }
      });
    },
    // 改变图片样式
    changeImg(val) {
      this.imgStype = {
        filter: `brightness(${val.imgNum1}%) contrast(${
          val.imgNum2
        }%) saturate(${val.imgNum3}%) opacity(${100 - val.imgNum4}%) `,
      };
    },
    async submit() {
      const annotations = [];
      this.dataList.forEach((item) => {
        let box = [];
        let line = [];
        let point = {};
        if (annotationType[item.modeType] == 2) {
          box = [...item.boxList[0], ...item.boxList[3]];
        }
        if (annotationType[item.modeType] == 7) {
          line = item.boxList.map((i) => {
            return {
              x: i[0],
              y: i[1],
            };
          });
        }
        if (annotationType[item.modeType] == 5) {
          point = {
            x: item.boxList[0],
            y: item.boxList[1],
          };
        }
        annotations.push({
          annotationType: annotationType[item.modeType],
          tagName: item.tagName,
          annotation: {
            box,
            line,
            point,
            label: item.labelTree,
          },
        });
      });
      const params = {
        projectId: this.projectId,
        commits: [
          {
            imageId: this.currentImgData.id,
            isValid: this.invalid ? 0 : 1,
            annotations,
          },
        ],
      };
      await saveCommit(params);
      this.$message.success(this.$t("marktool.annotate.mt510c"));
      this.getAssign();
    },
    async submitReview(obj) {
      let reviewAction = obj ? 3 : 7;
      const comment = {
        issue: obj ? obj.issue : "",
        text: obj ? obj.text : "",
      };
      const annotations = [];
      this.dataList.forEach((item) => {
        let box = [];
        let line = [];
        let point = {};
        if (annotationType[item.modeType] == 2) {
          box = [...item.boxList[0], ...item.boxList[3]];
        }
        if (annotationType[item.modeType] == 7) {
          line = item.boxList.map((i) => {
            return {
              x: i[0],
              y: i[1],
            };
          });
        }
        if (annotationType[item.modeType] == 5) {
          point = {
            x: item.boxList[0],
            y: item.boxList[1],
          };
        }
        annotations.push({
          annotationType: annotationType[item.modeType],
          tagName: item.tagName,
          annotation: {
            box,
            line,
            point,
            label: item.labelTree,
          },
        });
      });
      const params = {
        projectId: this.projectId,
        reviewList: [
          {
            imageId: this.currentImgData.id,
            isValid: this.invalid ? 0 : 1,
            annotations: annotations,
            commitId: this.currentImgData.commitId,
            comment: JSON.stringify(comment),
            reviewAction,
          },
        ],
      };
      await saveReview(params);
      this.$message.success(this.$t("marktool.annotate.g2f2d8"));
      this.getAssignReview();
    },
    async back() {
      await this.$confirm(
        this.$t("marktool.annotate.8wv3ri"),
        $t("common.prompt"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          type: "warning",
        }
      );
      const res = await release({
        projectId: this.projectId,
        imageIds: [this.currentImgData.id],
      });
      this.$router.push({
        path: "/annotationPlatform/projectManagement",
      });
    },
    // 分页
    handleCurrentChange(val) {
      this.currentPage = val;
      if (this.imgList.length && this.total == this.currentPage) {
        if (this.$route.query.type == 1) {
          this.getAssign();
        } else {
          this.getAssignReview();
        }
      } else {
        this.getHistory();
      }
    },
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
};
</script>
<style scoped lang="scss">
.wrap {
  display: flex;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;

  .content {
    flex: 1;
    overflow: hidden;
  }

  .pagination {
    align-items: center;
    justify-content: space-between;
    display: flex;
    padding: 5px 10px;
    margin: 0;
  }

  #map {
    height: calc(100vh - 138px);
    background: #f5f8fb;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;

    .auxiliaryTool {
      pointer-events: none;

      .auxiliaryLineX {
        position: absolute;
        width: 100%;
        height: 2px;
        background: #344799;
        z-index: 3;
        pointer-events: none;
      }

      .auxiliaryLineY {
        position: absolute;
        width: 2px;
        height: 100%;
        background: #344799;
        z-index: 3;
        pointer-events: none;
      }

      .auxiliaryBox {
        position: absolute;
        border: 2px #344799 solid;
        z-index: 3;
        pointer-events: none;
      }
    }

    .hege {
      width: 40px;
      height: 40px;
      font-size: 40px;
      position: absolute;
      right: 20px;
      top: 20px;
      z-index: 40;
    }

    .invalid {
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.7);
      position: absolute;
      left: 0;
      top: 0;
      z-index: 30;
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        width: 230px;
        display: block;
      }
    }

    .drag-inner {
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      .canvas {
        z-index: 2;
      }

      .bg-img {
        // max-width: 100%;
        display: block;
      }

      .temp-issue {
        position: absolute;
        z-index: 10;
      }

      .item-issue {
        position: absolute;
        cursor: pointer;
        z-index: 5;
      }
    }
  }

  .right-wrap {
    width: 300px;
    background: #fff;
    flex-shrink: 0;

    .right-tab {
      width: 100%;
      background: #f4f5f8;
      display: flex;

      li {
        height: 40px;
        font-size: 13px;
        text-align: center;
        flex: 1;
        flex-shrink: 0;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;

        &.active {
          background: #fff;
        }
      }
    }
  }
}
</style>
