<template>
  <div>
    <div class="head-container">
      <div>
        {{ $t("projectmanagement.projectdetail.875s3l")
        }}<el-tag v-if="projectDetail.status == 11">{{
          $t("projectmanagement.index.6w25dt")
        }}</el-tag>
        <el-tag v-if="projectDetail.status == 99">{{
          $t("projectmanagement.index.g2xs15")
        }}</el-tag>
      </div>
      <div v-if="projectDetail.status == 11">
        <el-button type="primary" @click="clickAnnotation">{{
          $t("projectmanagement.projectdetail.16c4c8")
        }}</el-button>
        <el-button type="primary" @click="clickSubmit">{{
          $t("projectmanagement.projectdetail.qy48s6")
        }}</el-button>
      </div>
    </div>
    <div class="ai_table">
      <el-tabs v-model="active">
        <el-tab-pane
          :label="$t('projectmanagement.projectdetail.tyq3sb')"
          name="1"
        >
          <Overview v-if="active == 1" />
        </el-tab-pane>
        <el-tab-pane
          :label="$t('projectmanagement.projectdetail.8nj3ny')"
          name="2"
        >
          <Dataset v-if="active == 2" />
        </el-tab-pane>
        <el-tab-pane
          :label="$t('projectmanagement.projectdetail.3t1u5u')"
          name="3"
        >
          <Document v-if="active == 3" />
        </el-tab-pane>
        <el-tab-pane
          :label="$t('projectmanagement.projectdetail.2g5lee')"
          name="4"
        >
          <Setup v-if="active == 4" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>
<script>
import Overview from "@/components/annotationPlatform/projectManagement/projectDetail/overview";
import Dataset from "@/components/annotationPlatform/projectManagement/projectDetail/dataset";
import Document from "@/components/annotationPlatform/projectManagement/projectDetail/document";
import Setup from "@/components/annotationPlatform/projectManagement/projectDetail/setup";
import {
  getDetail,
  submitProject,
} from "@/api/annotationPlatform/projectManagement";
export default {
  components: {
    Overview,
    Dataset,
    Document,
    Setup,
  },
  data() {
    return {
      active: "1",
      projectId: this.$route.query.id,
      projectDetail: {},
    };
  },
  async created() {
    this.getDetail();
  },
  methods: {
    async getDetail() {
      const data = await getDetail({ id: this.projectId });
      this.projectDetail = data.data;
    },
    clickAnnotation() {
      if (this.projectDetail.projectType == 1) {
        this.$router.push({
          path: "/annotationPlatform/projectManagement/markTool/classifyBatch",
          query: {
            id: this.projectId,
            type: 1,
          },
        });
      } else {
        this.$router.push({
          path: "/annotationPlatform/projectManagement/markTool/annotate",
          query: {
            id: this.projectId,
            type: 1,
          },
        });
      }
    },
    async clickSubmit() {
      await submitProject({ projectId: this.projectId });
      this.$message.success(this.$t("projectmanagement.projectdetail.f6kd8o"));
    },
  },
};
</script>
<style scoped lang="scss">
.head-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.overview-item {
  text-align: center;

  .count {
    font-size: 20px;
    margin: 10px 0;

    &.color1 {
      color: #E53935;
    }

    &.color2 {
      color: #dd383e;
    }

    &.color3 {
      color: #e6a23c;
    }
  }

  .text {
    font-size: 13px;
  }

  .ai_table {
    margin-top: 10px;
  }
}
</style>
