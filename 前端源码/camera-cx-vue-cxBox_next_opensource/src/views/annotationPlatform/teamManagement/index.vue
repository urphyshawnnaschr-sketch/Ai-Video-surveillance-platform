<template>
  <div>
    <div class="head-container">
      <el-input
        v-model="params.name"
        :placeholder="$t('teammanagement.index.88ppqq')"
        style="width: 200px; margin-right: 10px"
      ></el-input>
      <el-button type="primary" icon="el-icon-search" @click="getListData">{{
        $t("button.queryText")
      }}</el-button>
      <el-button
        type="primary"
        icon="el-icon-plus"
        @click="addData"
        style="float: right"
        >{{ $t("common.add", { text: $t("common.team") }) }}</el-button
      >
    </div>
    <div class="ai_table">
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column align="center" prop="name" :label="$t('teammanagement.index.hf347w')">
        </el-table-column>
        <el-table-column align="center" prop="description" :label="$t('teammanagement.index.358hi5')">
        </el-table-column>
        <el-table-column align="center" prop="createdName" :label="$t('teammanagement.index.98yv38')">
        </el-table-column>
        <el-table-column align="center" prop="userNum" :label="$t('teammanagement.index.4c4m7e')">
        </el-table-column>
        <el-table-column align="center" :label="$t('common.createTime')">
          <template slot-scope="scope">
            {{ $moment(scope.row.updatedAt).format("YYYY-MM-DD HH:mm:ss") }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('common.action', { text: '' })"
          width="160"
        >
          <template slot-scope="scope">
            <el-button type="text" @click="clickTeamDetail(scope.row)"
              >{{$t('teammanagement.index.7ln155')}}</el-button
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
    </div>
    <AddTeam
      v-if="addTeamVisible"
      @close="(addTeamVisible = false), getListData()"
    />
  </div>
</template>
<script>
import {
  getListData,
  deleteData,
} from "@/api/annotationPlatform/teamManagement";
import AddTeam from "@/components/annotationPlatform/teamManagement/addTeam";
export default {
  components: {
    AddTeam,
  },
  data() {
    return {
      addTeamVisible: false,
      loading: false,
      params: {
        pageNum: 1,
        pageSize: 10,
        name: "",
      },
      tableData: [],
    };
  },
  created() {
    this.getListData();
  },
  methods: {
    // 获取团队列表
    async getListData() {
      this.loading = true;
      const data = await getListData(this.params);
      this.tableData = data.data;
      this.loading = false;
    },
    // 新增团队
    addData() {
      this.addTeamVisible = true;
    },
    // 删除团队
    async deleteData(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const res = await deleteData({ teamId: item.id });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getListData();
          }
        })
        .catch(() => {});
    },
    // 跳转团队详情
    clickTeamDetail(item) {
      this.$router.push({
        path: "/annotationPlatform/teamManagement/teamDetail",
        query: {
          id: item.id,
        },
      });
    },
    close() {
      this.addTeamVisible = false;
      this.getListData();
    },
  },
};
</script>
<style scoped lang="scss">
.search_box {
  padding-bottom: 20px;
}
.ai_table {
  margin-top: 10px;
}
</style>
