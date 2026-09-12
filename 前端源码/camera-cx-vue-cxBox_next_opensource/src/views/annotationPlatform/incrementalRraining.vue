<template>
  <div>
    <div class="head-container">
      <div>
        <el-select
          v-model="params.algorithmId"
          :placeholder="$t('annotationplatform.incrementalrraining.y55jh6')"
          style="width: 200px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="(item, index) in algorithmOptions"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
        <el-select
          v-model="params.cameraId"
          :placeholder="$t('common.cameraName')"
          style="width: 200px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="(item, index) in cameraOptions"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="getListData">{{
          $t("button.queryText")
        }}</el-button>
      </div>
    </div>
    <div class="ai_table">
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column align="center" prop="algorithmName" :label="$t('annotationplatform.incrementalrraining.y55jh6')">
        </el-table-column>
        <el-table-column align="center" prop="imageNum" :label="$t('annotationplatform.incrementalrraining.v4t8uc')">
        </el-table-column>
        <el-table-column
          align="center"
          prop="cameraName"
          :label="$t('common.cameraName')"
        >
        </el-table-column>
        <el-table-column align="center" :label="$t('applicationMonitoring.common.updateTime')">
          <template slot-scope="scope">
            {{ $moment(scope.row.updatedAt).format("YYYY-MM-DD HH:mm:ss") }}
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('annotationplatform.incrementalrraining.ypb6nl')">
          <template slot-scope="scope">
            <el-button type="text" @click="clickPreview(scope.row)"
              >{{$t('annotationplatform.incrementalrraining.ynsby4')}}</el-button
            >
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('common.action', { text: '' })"
          width="220"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="clickAdd(scope.row)"
              >{{$t('annotationplatform.incrementalrraining.taf817')}}</el-button
            >
            <el-button
              type="text"
              class="danger"
              @click="clickDelete(scope.row)"
              >{{$t('annotationplatform.incrementalrraining.5ms4b7')}}</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        ></el-pagination>
      </div>
    </div>
    <PreviewImg
      v-if="previewImgVisible"
      :currentParams="currentParams"
      @close="(previewImgVisible = false), getListData()"
    />
    <AddProject
      v-if="addProjectVisible"
      :currentParams="currentParams"
      @close="(addProjectVisible = false), getListData()"
    />
  </div>
</template>
<script>
import {
  getListData,
  getCameraListData,
  getAlgorithmListData,
  deleteImg,
} from "@/api/annotationPlatform/incrementalRraining";
import PreviewImg from "@/components/annotationPlatform/incrementalRraining/previewImg";
import AddProject from "@/components/annotationPlatform/incrementalRraining/addProject";
export default {
  components: {
    PreviewImg,
    AddProject,
  },
  data() {
    return {
      previewImgVisible: false,
      addProjectVisible: false,
      loading: false,
      algorithmOptions: [],
      cameraOptions: [],
      params: {
        algorithmId: "",
        cameraId: "",
        page: 1,
        limit: 10,
      },
      currentParams: {},
      tableData: [],
      total: 0,
    };
  },
  async created() {
    await this.getOptions();
    this.getListData();
  },
  methods: {
    // 获取下拉
    async getOptions() {
      const data1 = await getCameraListData();
      this.cameraOptions = data1.data;
      const data2 = await getAlgorithmListData();
      this.algorithmOptions = data2.data;
    },
    // 获取数据列表
    async getListData() {
      this.loading = true;
      const data = await getListData(this.params);
      this.tableData = data.data;
      this.total = Number(data.count);
      this.loading = false;
    },
    // 预览图片
    clickPreview(item) {
      this.currentParams = {
        algorithmId: item.algorithmId,
        cameraId: item.cameraId,
        begin: 1,
        end: 10,
        interval: 10,
      };
      this.previewImgVisible = true;
    },
    // 创建项目
    clickAdd(item) {
      this.currentParams = {
        algorithmId: item.algorithmId,
        cameraId: item.cameraId,
        begin: 1,
        end: 10,
        interval: 10,
      };
      this.addProjectVisible = true;
    },
    clickDelete(item) {
      this.$confirm(this.$t('annotationplatform.incrementalrraining.vv181m'), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await deleteImg({
          trainingBaseVo: {
            algorithmId: item.algorithmId,
            cameraId: item.cameraId,
            begin: 1,
            end: Number(item.imageNum),
            interval: 10,
          },
        });
        if (res.code == 0) {
          this.$message.success(this.$t('annotationplatform.incrementalrraining.265r58'));
          this.params.page = 1;
          await this.getListData();
        }
      });
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getListData();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getListData();
    },
  },
};
</script>
<style scoped lang="scss">
.head-container {
  display: flex;
  justify-content: space-between;
}
.ai_table {
  margin-top: 10px;
}
</style>
