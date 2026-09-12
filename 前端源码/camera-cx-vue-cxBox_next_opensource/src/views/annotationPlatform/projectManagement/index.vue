<template>
  <div>
    <div class="head-container">
      <div>
        <el-input
          v-model="params.projectName"
          :placeholder="$t('projectmanagement.index.e313kw')"
          style="width: 200px; margin-right: 10px"
        ></el-input>
        <el-select
          v-model="params.status"
          :placeholder="$t('projectmanagement.index.k86rl1')"
          style="width: 200px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="(item, index) in statusOptions"
            :key="index"
            :label="item.name"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-select
          v-model="params.projectType"
          :placeholder="$t('projectmanagement.index.vo4q4w')"
          style="width: 200px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="(item, index) in projectTypeOptions"
            :key="index"
            :label="item.name"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="getListData">{{
          $t("button.queryText")
        }}</el-button>
      </div>
      <div>
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="addData"
          style="float: right"
          >{{$t('projectmanagement.index.n77twg')}}</el-button
        >
      </div>
    </div>
    <div class="ai_table">
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column align="center" :label="$t('projectmanagement.index.e313kw')">
          <template slot-scope="scope">
            <el-button type="text" @click="clickProjectDetail(scope.row)">{{
              scope.row.projectName
            }}</el-button>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('projectmanagement.index.vo4q4w')">
          <template slot-scope="scope">
            <el-tag
              :type="
                scope.row.projectType == 1 || scope.row.projectType == 3
                  ? 'success'
                  : ''
              "
              >{{ handleProjectType(scope.row.projectType) }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column align="center" prop="itemCount" :label="$t('annotationplatform.incrementalrraining.v4t8uc')">
        </el-table-column>
        <el-table-column
          align="center"
          prop="reviewNum"
          :label="$t('projectmanagement.index.ri63k9')"
        >
          <template slot-scope="scope">
            <template v-if="scope.row.reviewNum == 0">{{$t('projectmanagement.index.c9eh59')}}</template>
            <template v-else>{{ scope.row.noPassImageNum }}</template>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="status" :label="$t('projectmanagement.index.k86rl1')">
          <template slot-scope="scope">
            <template v-if="scope.row.status == 99">{{$t('projectmanagement.index.g2xs15')}}</template>
            <template v-else>{{$t('projectmanagement.index.6w25dt')}}</template>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="nameEn"
          :label="$t('common.createTime')"
        >
          <template slot-scope="scope">
            {{ $moment(scope.row.createdAt).format("YYYY-MM-DD HH:mm:ss") }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('common.action', { text: '' })"
          width="220"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="clickAnnotation(scope.row)"
              :disabled="scope.row.status == 99"
              >{{$t('projectmanagement.index.54r95c')}}</el-button
            >
            <el-button
              type="text"
              @click="clickReview(scope.row)"
              :disabled="
                !Boolean(scope.row.needReview) || scope.row.status == 99
              "
              >{{$t('projectmanagement.index.oq562d')}}</el-button
            >
            <el-button type="text" @click="dowloadData(scope.row)"
              >{{$t('projectmanagement.index.12bp68')}}</el-button
            >
            <el-button
              type="text"
              class="danger"
              @click="deleteData(scope.row)"
              >{{ $t("button.deleteText", { text: "" }) }}</el-button
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
    <AddProject
      v-if="addProjectVisible"
      @close="(addProjectVisible = false), getListData()"
    />
    <DownloadFile
      v-if="downloadFileVisible"
      :currentId="currentId"
      @close="(downloadFileVisible = false), getListData()"
    />
  </div>
</template>
<script>
import {
  getListData,
  deleteData,
  dowloadData,
} from "@/api/annotationPlatform/projectManagement";
import { projectType } from "@/utils/commonData";
import AddProject from "@/components/annotationPlatform/projectManagement/addProject";
import DownloadFile from "@/components/annotationPlatform/projectManagement/downloadFile";
export default {
  components: {
    AddProject,
    DownloadFile,
  },
  data() {
    return {
      addProjectVisible: false,
      downloadFileVisible: false,
      loading: false,
      statusOptions: [
        {
          name: this.$t('projectmanagement.index.4ym8we'),
          value: 1,
        },
        {
          name: this.$t('projectmanagement.index.1f5945'),
          value: 2,
        },
        {
          name: this.$t('projectmanagement.index.6w25dt'),
          value: 11,
        },
        {
          name: this.$t('projectmanagement.index.g2xs15'),
          value: 99,
        },
      ],
      projectTypeOptions: projectType,
      params: {
        projectName: "",
        status: "",
        projectType: "",
        pageNum: 1,
        pageSize: 10,
      },
      tableData: [],
      total: 0,
      currentId: 0,
    };
  },
  async created() {
    this.getListData();
  },
  methods: {
    // 获取项目列表
    async getListData() {
      this.loading = true;
      const data = await getListData(this.params);
      this.tableData = data.data;
      this.total = Number(data.count);
      this.loading = false;
    },
    // 新增项目
    addData() {
      this.addProjectVisible = true;
    },
    // 跳转项目详情
    clickProjectDetail(item) {
      this.$router.push({
        path: "/annotationPlatform/projectManagement/projectDetail",
        query: {
          id: item.id,
        },
      });
    },
    // 进入标注
    clickAnnotation(item) {
      if (item.projectType == 1) {
        this.$router.push({
          path: "/annotationPlatform/projectManagement/markTool/classifyBatch",
          query: {
            id: item.id,
            type: 1,
          },
        });
      } else {
        this.$router.push({
          path: "/annotationPlatform/projectManagement/markTool/annotate",
          query: {
            id: item.id,
            type: 1,
          },
        });
      }
    },
    // 进入质检
    clickReview(item) {
      if (item.projectType == 1) {
        this.$router.push({
          path: "/annotationPlatform/projectManagement/markTool/classify",
          query: {
            id: item.id,
            type: 2,
          },
        });
      } else {
        this.$router.push({
          path: "/annotationPlatform/projectManagement/markTool/annotate",
          query: {
            id: item.id,
            type: 2,
          },
        });
      }
    },
    async dowloadData(item) {
      this.currentId = item.id;
      this.downloadFileVisible = true;
    },
    // 删除项目
    async deleteData(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const res = await deleteData({ projectId: item.id });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getListData();
          }
        })
        .catch(() => {});
    },
    // 处理项目类型
    handleProjectType(val) {
      const data = this.projectTypeOptions.find((i) => i.value == val);
      return data.name;
    },
    // 分页
    handleCurrentChange(val) {
      this.params.pageNum = val;
      this.getListData();
    },
    handleSizeChange(val) {
      this.params.pageSize = val;
      this.params.pageNum = 1;
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
