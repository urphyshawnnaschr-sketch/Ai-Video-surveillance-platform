<template>
  <div>
    <div class="head-container">
    
      <el-input
        v-model="params.name"
        :placeholder="$t('teammanagement.teamdetail.8y333y')"
        style="width: 200px; margin-right: 10px"
      ></el-input>
      <el-button type="primary" icon="el-icon-search" @click="getTeamPerson">{{
        $t("button.queryText")
      }}</el-button>

      <el-button
        type="default"
        icon="el-icon-back"
        @click="goBack"
        >{{$t('projectmanagement.index.1fa83m')}}</el-button
      >

      <el-button
        type="primary"
        icon="el-icon-upload2"
        @click="exportData"
        style="float: right"
        >{{$t('projectmanagement.index.12bp68')}}</el-button
      >
      <el-button
        type="primary"
        icon="el-icon-plus"
        @click="addData"
        style="float: right"
        >{{$t('teammanagement.teamdetail.j547l4')}}</el-button
      >
    </div>
    <div class="ai_table">
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column align="center" :label="$t('teammanagement.teamdetail.i8e5fb')">
          <template slot-scope="scope">
            {{ scope.row.teams[0].name }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="name"
          :label="$t('common.accountName')"
        >
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('common.password', { text: '' })"
        >
          <template slot-scope="scope">
            {{ scope.row.password || $t("teammanagement.teamdetail.r6qsj8") }}
          </template>
        </el-table-column>
        <el-table-column align="center" prop="userNum" :label="$t('teammanagement.teamdetail.jln5pl')">
          <template slot-scope="scope">
            {{ handleRoleData(scope.row.roles) }}
          </template>
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
            <el-button
              type="text"
              class="danger"
              @click="deletePerson(scope.row)"
              >{{ $t("button.deleteText", { text: "" }) }}</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
    <AddPerson
      v-if="addPersonVisible"
      @close="(addPersonVisible = false), getTeamPerson()"
    />
  </div>
</template>
<script>
import {
  getTeamPerson,
  getRoleList,
  deletePerson,
  exportPerson,
} from "@/api/annotationPlatform/teamManagement";
import AddPerson from "@/components/annotationPlatform/teamManagement/addPerson";
export default {
  components: {
    AddPerson,
  },
  data() {
    return {
      addPersonVisible: false,
      loading: false,
      params: {
        teamId: this.$route.query.id,
        name: "",
      },
      roleOptions: [],
      tableData: [],
    };
  },
  async created() {
    await this.getRoleList();
    this.getTeamPerson();
  },
  methods: {
    async getRoleList() {
      const data = await getRoleList();
      this.roleOptions = data.data;
    },
    // 获取团队成员列表
    async getTeamPerson() {
      this.loading = true;
      const data = await getTeamPerson(this.params);
      this.tableData = data.data;
      this.loading = false;
    },
    // 新增团队
    addData() {
      this.addPersonVisible = true;
    },
    // 导出
    async exportData() {
      let form = new FormData();
      form.append("teamId", this.params.teamId);
      const data = await exportPerson({
        teamId: this.params.teamId,
      });
      const href = window.URL.createObjectURL(data.data); //转成格式
      window.location.href = href;
    },
    // 删除团队成员
    async deletePerson(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const res = await deletePerson({
            teamId: this.params.teamId,
            userId: item.id,
          });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getTeamPerson();
          }
        })
        .catch(() => {});
    },
    handleRoleData(roles) {
      const list = [];
      roles.forEach((item) => {
        const obj = this.roleOptions.find((i) => i.id == item.id);
        if (obj) {
          list.push(obj.nameCh);
        }
      });
      return list.join(",") || "--";
    },
    close() {
      this.addPersonVisible = false;
      this.getTeamPerson();
    },
    goBack() {
      this.$router.push("/annotationPlatform/teamManagement");
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
