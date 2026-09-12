<template>
  <div>
    <div class="head-container">
      <div>
        <el-select
          v-model="params.labelStatus"
          :placeholder="$t('projectdetail.dataset.0gqv1k')"
          style="margin-right: 10px"
          clearable
        >
          <el-option
            v-for="(item, index) in labelStatusOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <!-- <el-select
          v-model="params.labelUserId"
          placeholder="Annotator"
          style="margin-right: 10px"
          clearable
        >
        </el-select>
        <el-select
          v-model="params.label"
          placeholder="Tag"
          style="margin-right: 10px"
          clearable
        >
        </el-select> -->
        <el-button type="primary" icon="el-icon-search" @click="getData(1)">{{
          $t("button.queryText")
        }}</el-button>
        <el-button icon="el-icon-refresh" @click="refreshData">{{
          $t("button.resetText", { text: "" })
        }}</el-button>
      </div>
      <div style="width: 180px; flex-shrink: 0">
        <el-checkbox
          :label="$t('marktool.classifybatch.6id9sg')"
          style="margin-right: 10px"
          :indeterminate="isIndeterminate"
          v-model="checkAll"
          @change="handleCheckAllChange"
        ></el-checkbox>
        <el-button type="primary" style="margin-right: 10px" @click="release"
          >{{$t('projectdetail.dataset.h6ot7t')}}</el-button
        >
      </div>
    </div>
    <el-checkbox-group v-model="checkedList" @change="handleCheckedImgChange">
      <el-row :gutter="20">
        <el-col :span="48" v-for="(item, index) in imgList" :key="index">
          <div class="dataset-item">
            <el-checkbox class="checkbox" :label="item.id">
              <br />
            </el-checkbox>
            <div style="display: inline-block; height: 200px">
              <ClassifyDetail
                v-if="projectDetail.projectType == 1"
                :currentImgData="item"
              />
              <AnnotateDetail
                v-if="
                  projectDetail.projectType == 2 ||
                  projectDetail.projectType == 4
                "
                :currentImgData="item"
              />
              <div
                class="invalid"
                v-if="item.commit && item.commit.isValid == 0"
              >
                <img src="@/assets/images/markTool/invalid.png" />
              </div>
              <div class="hege" v-if="item.status == 15">
                <SvgIcon icon-name="icon-accepted" />
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-checkbox-group>
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
</template>
<script>
import SvgIcon from "@/components/svgIcon";
import ClassifyDetail from "@/components/classifyDetail";
import AnnotateDetail from "@/components/annotateDetail";
import {
  getDetail,
  getImageList,
  release,
} from "@/api/annotationPlatform/projectManagement";
import { annotationType } from "@/utils/commonData";
import { color } from "echarts";
import colorList from "@/utils/colorList";
export default {
  components: {
    SvgIcon,
    ClassifyDetail,
    AnnotateDetail,
  },
  data() {
    return {
      labelStatusOptions: [
        {
          label: this.$t('projectdetail.dataset.x3n2fg'),
          value: "1",
        },
        {
          label: this.$t('projectmanagement.index.6w25dt'),
          value: "2",
        },
        {
          label: this.$t('projectdetail.dataset.d825bp'),
          value: "3",
        },
      ],
      labelUserOptions: [],
      labelOptions: [],
      params: {
        projectId: this.$route.query.id,
        page: 1,
        limit: 10,
        labelStatus: "",
        labelUserId: "",
        label: "",
      },
      projectDetail: {},
      imgList: [],
      total: 0,
      checkAll: false,
      isIndeterminate: false,
      checkedList: [],
    };
  },
  async created() {
    await this.getDetail();
    this.getData();
  },
  methods: {
    async getDetail() {
      const data = await getDetail({ id: this.params.projectId });
      this.projectDetail = data.data;
    },
    // 获取图片列表
    async getData() {
      const data = await getImageList(this.params);
      const res = new Map();
      const tagList = data.data.filter((item) => {
        if (item.commit.annotations && item.commit.annotations.length) {
          return (
            !res.has(item.commit.annotations[0].tagName) &&
            res.set(item.commit.annotations[0].tagName, 1)
          );
        } else {
          return "";
        }
      });
      tagList.forEach((item, index) => {
        data.data.forEach((i) => {
          if (
            i.commit.annotations &&
            i.commit.annotations.length &&
            i.commit.annotations[0].tagName ==
              item.commit.annotations[0].tagName
          ) {
            this.$set(i, "color", colorList[index]);
          }
        });
      });
      this.imgList = data.data;
      this.total = Number(data.count);
    },
    // 重置
    refreshData() {
      this.checkAll = false;
      this.isIndeterminate = false;
      this.checkedList = [];
      Object.assign(this.params, {
        page: 1,
        limit: 10,
        labelStatus: "",
        labelUserId: "",
        label: "",
      });
      this.getData();
    },
    // 批量释放
    async release() {
      this.$confirm(this.$t('projectdetail.dataset.3xq944'), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await release({
          projectId: this.params.projectId,
          imageIds: this.checkedList,
        });
        if (res.code == 0) {
          this.$message.success(this.$t('projectdetail.dataset.eg91qx'));
          this.params.page = 1;
          await this.getData();
        }
      });
    },
    clickItem(item) {
      console.info(item);
    },
    handleCheckAllChange(val) {
      const list = this.imgList.map((i) => {
        return i.id;
      });
      this.checkedList = val ? list : [];
      this.isIndeterminate = false;
    },
    handleCheckedImgChange(value) {
      const list = this.imgList.map((i) => {
        return i.id;
      });
      let checkedCount = value.length;
      this.checkAll = checkedCount === list.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < list.length;
    },
    // 分页
    handleCurrentChange(val) {
      this.checkAll = false;
      this.isIndeterminate = false;
      this.checkedList = [];
      this.params.page = val;
      this.getData();
    },
    handleSizeChange(val) {
      this.checkAll = false;
      this.isIndeterminate = false;
      this.checkedList = [];
      this.params.limit = val;
      this.params.page = 1;
      this.getData();
    },
  },
};
</script>
<style scoped lang="scss">
.head-container {
  padding: 10px 0;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

::v-deep .el-col-48 {
  width: 20%;
}

.dataset-item {
  width: 100%;
  height: 200px;
  border: 1px solid rgba(0, 0, 0, 0.06);
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
</style>
