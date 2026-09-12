<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="box-card">
          <div
            slot="header"
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
            "
          >
            <span>{{ $t("projectdetail.overview.12lbr6") }}</span>
            <el-radio-group v-model="overviewRadio" @input="getOverviewData1">
              <el-radio-button label="1">{{
                $t("projectdetail.overview.n71i25")
              }}</el-radio-button>
              <el-radio-button label="2">{{
                $t("projectdetail.overview.6f2b26")
              }}</el-radio-button>
            </el-radio-group>
          </div>
          <el-row>
            <el-col :span="6">
              <div class="overview-item">
                <p class="count">
                  {{ annotationData.annCount || 0 }}/{{
                    annotationData.annInfoCount || 0
                  }}
                </p>
                <span class="text">{{
                  $t("projectdetail.overview.oged77")
                }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="overview-item">
                <p class="count color2">{{ annotationData.noAnnoCount }}</p>
                <span class="text">{{
                  $t("projectdetail.overview.fl328q")
                }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="overview-item">
                <p class="count color3">{{ annotationData.backCount }}</p>
                <span class="text">{{
                  $t("projectdetail.overview.8924s2")
                }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="overview-item">
                <p class="count">{{ annotationData.finshRatio }}%</p>
                <span class="text">{{
                  $t("projectdetail.overview.155wj5")
                }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div
            slot="header"
            style="
              height: 32px;
              display: flex;
              align-items: center;
              justify-content: space-between;
            "
          >
            <span>{{ $t("projectdetail.overview.6hex4z") }}</span>
          </div>
          <el-row>
            <el-col :span="6">
              <div class="overview-item">
                <p class="count">
                  {{ inspectionData.count }}/{{ inspectionData.noReviewCount }}
                </p>
                <span class="text">{{
                  $t("projectdetail.overview.7373iy")
                }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="overview-item">
                <p class="count color1">{{ inspectionData.reviewCount }}</p>
                <span class="text">{{ $t("marktool.annotate.523uwu") }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="overview-item">
                <p class="count color2">{{ inspectionData.backCount }}</p>
                <span class="text">{{
                  $t("projectdetail.overview.k445yj")
                }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="overview-item">
                <p class="count">{{ inspectionData.finshRatio }}%</p>
                <span class="text">{{
                  $t("projectdetail.overview.155wj5")
                }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    <el-table :data="tableData" border style="width: 100%; margin-top: 10px;">
      <el-table-column
        :label="$t('projectdetail.overview.ikfk71')"
        prop="label"
      ></el-table-column>
      <el-table-column
        :label="$t('projectdetail.overview.7m7byy')"
        prop="annCount"
      ></el-table-column>
      <el-table-column :label="$t('projectdetail.overview.07rnxn')">
        <template slot-scope="scope">
          <el-progress :percentage="scope.row.annRatio"></el-progress>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import {
  getOverviewData1,
  getOverviewData2,
  getOverviewData3,
} from "@/api/annotationPlatform/projectManagement";
export default {
  data() {
    return {
      projectId: this.$route.query.id,
      overviewRadio: "1",
      annotationData: {},
      inspectionData: {},
      tableData: [],
    };
  },
  async created() {
    this.getOverviewData1();
    this.getOverviewData2();
    this.getOverviewData3();
  },
  methods: {
    async getOverviewData1() {
      const data = await getOverviewData1({
        projectId: this.projectId,
        type: this.overviewRadio,
      });
      this.annotationData = data.data;
    },
    async getOverviewData2() {
      const data = await getOverviewData2({
        projectId: this.projectId,
      });
      this.inspectionData = data.data;
    },
    async getOverviewData3() {
      const data = await getOverviewData3({
        projectId: this.projectId,
      });
      this.tableData = data.data;
    },
  },
};
</script>
<style scoped lang="scss">
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
    font-size: 12px;
  }
}
</style>
