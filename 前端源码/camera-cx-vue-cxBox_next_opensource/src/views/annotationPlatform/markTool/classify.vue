<template>
  <div class="wrap">
    <ToolBar ref="toolBar" @setMode="setMode" @changeImg="changeImg" />
    <div class="content">
      <div class="pagination">
        <el-button
          v-if="$route.query.type == 1"
          type="primary"
          @click="goClassify"
          >{{ $t("marktool.classify.32173v")
          }}<template v-if="$route.query.type == 1">{{
            $t("marktool.classify.m3u92c")
          }}</template
          ><template v-else>{{
            $t("marktool.classify.f182o1")
          }}</template></el-button
        >
        <el-pagination
          background
          layout="prev, pager, next, jumper, total"
          :current-page="currentPage"
          :page-size="1"
          :pager-count="5"
          @current-change="handleCurrentChange"
          :total="total"
        />
        <span>
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
        @mouseenter="isHover = true"
        @mouseleave="isHover = isMousedown = false"
        @mousemove="dragMousemove"
        v-loading="loading"
        :element-loading-text="$t('marktool.annotate.6emt1s')"
      >
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
        <div
          v-if="currentTagData.tagName && tagVisible"
          class="tag"
          :style="{ background: currentTagData.color }"
        >
          {{ currentTagData.tagName
          }}<template v-if="currentTagData.tagName != currentTagData.name">
            - {{ currentTagData.name }}</template
          >
        </div>
        <div
          class="drag-inner"
          ref="dragElement"
          :style="{ cursor: mouseType, width: '100%' }"
          @mousedown="dragMousedown"
          @mouseup.stop="isMousedown = false"
        >
          <slot>
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
          <span>{{ item }}</span>
        </li>
      </ul>
      <Tab1
        :attributeList="projectDetail.labels"
        :currentImgData="currentImgData"
        @changeAttribute="changeAttribute"
        v-show="rightActive == $t('marktool.annotate.cot77w')"
      />
      <Tab2
        :currentImgId="currentImgData.id"
        :formData="currentImgData.review"
        @submit="submitReview"
        v-show="rightActive == $t('marktool.annotate.7yo6h2')"
      />
    </div>
  </div>
</template>

<script>
import ToolBar from "./components/classify/toolBar";
import colorList from "@/utils/colorList";
import SvgIcon from "@/components/svgIcon";
import Tab1 from "./components/classify/tab1";
import Tab2 from "./components/classify/tab2";
import {
  getDetail,
  getAssign,
  getAssignReview,
  getHistory,
  saveCommit,
  saveReview,
  release,
} from "@/api/annotationPlatform/projectManagement";
export default {
  components: {
    Tab1,
    Tab2,
    ToolBar,
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
        this.$t("marktool.annotate.7yo6h2"),
      ],
      rightActive: this.$t("marktool.annotate.cot77w"),
      attributeList: [], // 所有属性标签
      modeType: "", // 工具栏选择类型
      mouseType: "", // 鼠标样式
      isMousedown: false,
      isHover: false, // 鼠标是否在容器上
      moveStart: {}, // 拖拽坐标
      translate: { x: 0, y: 0 }, // 容器位移样式
      scale: 1, // 初始比例
      scaleZoom: {
        // 缩放范围
        max: 10,
        min: 0.2,
      },
      currentTagData: {}, // 当前属性标签
      tagVisible: true, // 标签是否全部隐藏
      imgStype: {}, // 图片滤镜样式
      issueList: [
        // {
        //   id: "1",
        //   left: 300,
        //   top: 300,
        //   issue: [],
        //   text: "问题1",
        //   time: "2022-11-12 12:00:00",
        //   name: "张健",
        // },
        // {
        //   id: "2",
        //   left: 200,
        //   top: 200,
        //   issue: [],
        //   text: "问题1",
        //   time: "2022-11-12 12:00:00",
        //   name: "张健",
        // },
      ], // 问题列表
      currentIssueId: "", // 当前问题id
      issueStype: {}, // 新问题样式
      timer: null,
    };
  },
  watch: {
    currentImgData: {
      handler(val) {
        this.loading = true;
        let img = new Image();
        img.src = this.$common.handleDataset(val.id);
        img.onload = () => {
          this.$refs.dragElement.style.transform = `scale(1) translate(0px, 0px)`;
          this.loading = false;
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
    // 图片自适应容器尺寸
    const domW = dragWrap.clientWidth;
    let img = new Image();
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
        this.currentPage = this.total;
        this.currentImgData = this.imgList[0];
      } else {
        if (this.total == this.currentPage) {
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
      const data = await getAssign({
        count: 1,
        projectId: this.projectId,
      });
      if (data.data.images.length) {
        this.imgList = data.data.images;
        this.total = Number(data.data.total) + this.imgList.length;
        this.currentImgData = this.imgList[0];
      } else {
        if (this.total == this.currentPage) {
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
        case "REVOKE":
          break;
        case "RECOVER":
          break;
        case "SHOWTAG":
          this.tagVisible = true;
          break;
        case "HIDETAG":
          this.tagVisible = false;
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
        case "DELETETAG":
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
          this.scale += 0.2 * speed;
          this.$refs.dragElement.style.transform = `scale(${this.scale}) translate(${this.translate.x}px, ${this.translate.y}px)`;
        } else if (
          e.wheelDelta < 0 &&
          Math.round(this.scale * 100) / 100 > this.scaleZoom.min
        ) {
          this.scale += 0.2 * speed;
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
    },
    // 改变标签属性
    changeAttribute(obj) {
      console.info(obj);
      this.currentTagData = obj;
    },
    // 改变当前svg
    changeCurrent(val) {
      this.currentSvgVisible = true;
      this.currentSvgData = val;
    },
    // 改变隐藏
    changeVisible(val) {},
    // 改变图片样式
    changeImg(val) {
      this.imgStype = {
        filter: `brightness(${val.imgNum1}%) contrast(${
          val.imgNum2
        }%) saturate(${val.imgNum3}%) opacity(${100 - val.imgNum4}%) `,
      };
    },
    async submit() {
      const annotations = [
        {
          annotationType: 1,
          tagName: this.currentTagData.tagName,
          annotation: {
            label: this.currentTagData.labelTree,
          },
        },
      ];
      const params = {
        projectId: this.projectId,
        commits: [
          {
            imageId: this.currentImgData.id,
            isValid: 1,
            annotations,
          },
        ],
      };
      await saveCommit(params);
      this.$message.success(this.$t("marktool.annotate.mt510c"));
      this.getAssign();
    },
    async submitReview(obj) {
      const reviewAction = obj ? 3 : 7;
      const comment = {
        issue: obj ? obj.issue : "",
        text: obj ? obj.text : "",
      };
      const annotations = [
        {
          annotationType: 1,
          tagName: this.currentTagData.tagName,
          annotation: {
            label: this.currentTagData.labelTree,
          },
        },
      ];
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
      const data = await saveReview(params);
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
    goClassify() {
      this.$router.replace({
        path: "/annotationPlatform/projectManagement/markTool/classifyBatch",
        query: {
          id: this.projectId,
          type: this.$route.query.type,
        },
      });
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

    .hege {
      width: 40px;
      height: 40px;
      font-size: 40px;
      position: absolute;
      right: 20px;
      top: 20px;
      z-index: 40;
    }

    .tag {
      font-size: 13px;
      color: #fff;
      border-radius: 20px;
      padding: 3px 10px;
      position: absolute;
      left: 20px;
      top: 20px;
      z-index: 40;
    }

    .drag-inner {
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      .bg-img {
        max-width: 100%;
        display: block;
      }
    }
  }

  .right-wrap {
    width: 300px;
    background: #fff;

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
