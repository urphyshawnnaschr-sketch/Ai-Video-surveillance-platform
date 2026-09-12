<template>
  <div class="wrap">
    <ToolBarBatch ref="toolBarBatch" @setMode="setMode" />
    <div class="content">
      <div class="pagination">
        <el-button type="primary" @click="goClassify"
          >{{ $t("marktool.classifybatch.17fgt4")
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
          :page-size="20"
          :pager-count="5"
          @current-change="handleCurrentChange"
          :total="total"
        />
        <span>
          <el-button type="primary" @click="back">{{
            $t("marktool.annotate.13o4yx")
          }}</el-button>
          <el-button type="primary" @click="submit">{{
            $t("button.submitText")
          }}</el-button>
        </span>
      </div>
      <div class="list">
        <div style="padding-top: 20px">
          <el-checkbox
            :label="$t('marktool.classifybatch.6id9sg')"
            style="margin-right: 10px"
            :indeterminate="isIndeterminate"
            v-model="checkAll"
            @change="handleCheckAllChange"
          ></el-checkbox>
        </div>
        <el-checkbox-group
          v-model="checkedList"
          @change="handleCheckedImgChange"
        >
          <el-row :gutter="20" class="checkbox-list">
            <el-col :span="48" v-for="(item, index) in imgList" :key="index">
              <el-checkbox class="checkbox" :label="item.id">
                <div class="list-item">
                  <div
                    style="width: 100%; display: inline-block; height: 200px"
                  >
                    <ClassifyDetail
                      v-if="projectDetail.projectType == 1"
                      :currentImgData="item"
                    />
                    <div class="hege" v-if="item.status == 15">
                      <SvgIcon icon-name="icon-accepted" />
                    </div>
                  </div>
                </div>
              </el-checkbox>
            </el-col>
          </el-row>
        </el-checkbox-group>
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
        ref="tab1"
        :attributeList="projectDetail.labels"
        @changeAttribute="changeAttribute"
        v-show="rightActive == $t('marktool.annotate.cot77w')"
      />
    </div>
  </div>
</template>

<script>
import ToolBarBatch from "./components/classify/toolBarBatch";
import SvgIcon from "@/components/svgIcon";
import Tab1 from "./components/classify/tab1";
import ClassifyDetail from "@/components/classifyDetail";
import {
  getDetail,
  getAssign,
  getAssignReview,
  getHistory,
  saveCommit,
  release,
} from "@/api/annotationPlatform/projectManagement";
export default {
  components: {
    Tab1,
    ToolBarBatch,
    SvgIcon,
    ClassifyDetail,
  },
  data() {
    return {
      loading: false,
      projectId: this.$route.query.id,
      projectDetail: {},
      imgList: [], // 图片列表
      currentPage: 1, // 当前页
      total: 0, // 总数
      rightTabList: [this.$t("marktool.annotate.cot77w")],
      rightActive: this.$t("marktool.annotate.cot77w"),
      attributeList: [], // 所有属性标签
      modeType: "", // 工具栏选择类型
      currentTagData: {}, // 当前属性标签
      tagVisible: true, // 标签是否全部隐藏
      checkAll: false,
      isIndeterminate: false,
      checkedList: [],
      timer: null,
    };
  },
  async created() {
    await this.getDetail();
    this.getAssign();
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
                  const list = [];
                  for (let i = 0; i < this.imgList.length; i++) {
                    list.push(this.imgList[i].id);
                  }
                  const res = await release({
                    projectId: this.projectId,
                    imageIds: list,
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
    async getAssign() {
      const data = await getAssign({
        count: 20,
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
      const data = await getHistory({
        projectId: this.projectId,
        page: this.currentPage,
        limit: 20,
        type: 0,
      });
      this.imgList = data.data;
      if (data.data.length) {
        this.imgList = data.data;
      } else {
        this.$message.error(this.$t("marktool.classifybatch.8s6h34"));
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

        case "DELETETAG":
          break;
        default:
          this.modeType = "";
          this.mouseType = "auto";
          break;
      }
    },
    // 改变标签属性
    changeAttribute(obj) {
      if (!this.checkedList.length) {
        this.$refs.tab1.name = "";
        return this.$message.error(this.$t("marktool.classifybatch.4oc555"));
      }
      this.currentTagData = obj;
      this.checkedList.forEach((item) => {
        this.imgList.forEach((i) => {
          if (item == i.id) {
            this.$set(i.commit, "annotations", [
              {
                annotationType: 1,
                tagName: this.currentTagData.tagName,
                annotation: {
                  label: this.currentTagData.labelTree,
                },
              },
            ]);
            this.$set(i, "color", this.currentTagData.color);
          }
        });
      });
      this.checkAll = false;
      this.isIndeterminate = false;
      this.checkedList = [];
    },
    handleCheckAllChange(val) {
      const list = this.imgList.map((i) => {
        return i.id;
      });
      this.checkedList = val ? list : [];
      this.isIndeterminate = false;
    },
    handleCheckedImgChange(value) {
      console.info(value);
      const list = this.imgList.map((i) => {
        return i.id;
      });
      let checkedCount = value.length;
      this.checkAll = checkedCount === list.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < list.length;
    },
    async submit() {
      const commits = [];
      let count = 0;
      for (let i = 0; i < this.imgList.length; i++) {
        const item = this.imgList[i];
        if (!item.commit.annotations?.length) {
          count++;
        }
        commits.push({
          imageId: item.id,
          isValid: item.commit.isValid || 1,
          annotations: item.commit.annotations,
        });
      }
      const params = {
        projectId: this.projectId,
        commits,
      };
      console.info(count);
      if (count) {
        return this.$message.error(
          this.$t("marktool.classifybatch.rdl617", [count])
        );
      }
      const data = await saveCommit(params);
      this.$message.success(this.$t("marktool.annotate.mt510c"));
      this.getAssign();
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
      const list = [];
      for (let i = 0; i < this.imgList.length; i++) {
        list.push(this.imgList[i].id);
      }
      const res = await release({
        projectId: this.projectId,
        imageIds: list,
      });
      this.$router.push({
        path: "/annotationPlatform/projectManagement",
      });
    },
    // 分页
    handleCurrentChange(val) {
      this.checkAll = false;
      this.isIndeterminate = false;
      this.checkedList = [];
      this.currentPage = val;
      if (this.imgList.length && this.total == this.currentPage) {
        this.getAssign();
      } else {
        this.getHistory();
      }
    },
    goClassify() {
      this.$router.replace({
        path: "/annotationPlatform/projectManagement/markTool/classify",
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
::v-deep .el-col-48 {
  width: 20%;
}

::v-deep .checkbox-list {
  .checkbox {
    width: 100%;
    height: 100%;
    position: relative !important;
    top: 0 !important;
    right: 0 !important;
  }

  .el-checkbox__input {
    display: none;
  }

  .el-checkbox__label {
    width: 100%;
    padding: 0;
    display: block;
  }

  .is-checked {
    .list-item {
      border: 1px #E53935 solid !important;
    }
  }
}

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

  .list {
    height: calc(100vh - 138px);
    background: #f5f8fb;
    padding: 0 20px;
    overflow: auto;

    .list-item {
      width: 100%;
      height: 200px;
      border: 1px solid rgba(0, 0, 0, 0.06);
      border-radius: 5px;
      margin: 10px 0;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      .checkbox {
        position: absolute;
        top: 10px;
        right: 10px;
        z-index: 10;
      }

      .img {
        max-width: 100%;
        max-height: 100%;
        display: block;
      }

      .invalid {
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.7);
        position: absolute;
        left: 0;
        top: 0;
        z-index: 9;
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          width: 80px;
        }
      }

      .hege {
        width: 20px;
        height: 20px;
        position: absolute;
        left: 10px;
        top: 10px;
        z-index: 8;
        font-size: 20px !important;
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
