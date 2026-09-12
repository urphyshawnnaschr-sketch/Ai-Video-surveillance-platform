<template>
  <div>
    <div class="head-container" :style="publicStyle.background">
      <el-select
        :placeholder="$t('applicationMonitoring.common.applicationScenarios')"
        clearable
        v-model="params.tagId"
        class="head-container-input"
      >
        <el-option
          v-for="(item, index) in tagList"
          :key="index"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="getListData">{{
        $t("button.queryText")
      }}</el-button>
      <el-button
        type="primary"
        icon="el-icon-plus"
        @click="addAlarmLevelData"
        style="float: right"
        >{{ $t("common.add", { text: $t("common.alarmLevel") }) }}</el-button
      >
      <el-button
        type="primary"
        icon="el-icon-plus"
        @click="addData"
        style="float: right"
        >{{ $t("common.add", { text: $t("common.algorithm") }) }}
      </el-button>
    </div>
    <div class="ai_table" :style="publicStyle.background">
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        :header-row-style="publicStyle.font"
        v-loading="loading"
      >
        <el-table-column
          align="center"
          prop="name"
          :label="$t('common.algorithmName')"
        >
        </el-table-column>
        <el-table-column
          align="center"
          prop="nameEn"
          :label="
            $t('applicationMonitoring.algorithmManagement.algorithmEnglish')
          "
        >
        </el-table-column>
        <el-table-column align="center" :label="$t('common.alarmLevel')">
          <template slot-scope="scope">
            <el-tag
              size="medium"
              :style="{
                backgroundColor: scope.row.alarmLevel.showColorAlpha,
                color: scope.row.alarmLevel.showColor,
                borderColor: scope.row.alarmLevel.showColor,
              }"
              >{{ scope.row.alarmLevel.name }}</el-tag
            >
          </template>
        </el-table-column>
        <!-- <el-table-column align="center" label="算法归属">
          <template slot-scope="scope">
            {{ handleTag(scope.row.tagIds) }}
          </template>
        </el-table-column> -->
        <el-table-column
          align="center"
          :label="$t('applicationMonitoring.common.updateTime')"
        >
          <template slot-scope="scope">
            {{ $moment(scope.row.updatedAt).format("YYYY-MM-DD HH:mm:ss") }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('common.action', { text: '' })"
          width="260"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="playVideo(scope.row)"
              :disabled="!Boolean(scope.row.videoUri)"
              >{{ $t("applicationMonitoring.algorithmManagement.domVideo") }}
            </el-button>
            <el-button type="text" @click="editData(scope.row)">{{
              $t("common.edit", { text: "" })
            }}</el-button>
            <el-button type="text" @click="upData(scope.row)"
              >{{ $t('applicationMonitoring.boxManagement.algorithmUpgrade') }}</el-button
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
    <AddAlgorithm
      :currentId="currentId"
      v-if="addAlgorithmVisible"
      @close="(addAlgorithmVisible = false), getListData()"
    />
    <playVideo
      :currentName="currentVideoName"
      v-if="playVideoVisible"
      @close="playVideoVisible = false"
    />
    <ListAlarmLevel
      v-if="listAlarmLevelVisible"
      @close="listAlarmLevelVisible = false"
    />
  </div>
</template>
<script>
import {
  getListData,
  getTagListData,
  deleteData,
} from "@/api/applicationMonitoring/algorithmManagement";
import AddAlgorithm from "@/components/applicationMonitoring/algorithmManagement/addAlgorithm";
import PlayVideo from "@/components/applicationMonitoring/algorithmManagement/playVideo";
import ListAlarmLevel from "@/components/applicationMonitoring/algorithmManagement/listAlarmLevel";
export default {
  components: {
    AddAlgorithm,
    PlayVideo,
    ListAlarmLevel,
  },
  data() {
    return {
      addAlgorithmVisible: false,
      playVideoVisible: false,
      listAlarmLevelVisible: false,
      loading: false,
      params: {
        tagId: "",
      },
      tagList: [],
      tableData: [],
      currentId: "",
      currentVideoName: "",
      publicStyle: "",
    };
  },
  async created() {
    await this.getTagListData();
    this.getListData();
    this.publicStyle = JSON.parse(localStorage.getItem("publicStyle"));
  },
  methods: {
    // 获取行业列表
    async getTagListData() {
      const data = await getTagListData({ type: 1 });
      this.tagList = data.data;
    },
    // 获取算法列表
    async getListData() {
      this.loading = true;
      const data = await getListData(this.params);
      this.tableData = data.data;
      this.loading = false;
    },
    // 新增算法
    addData() {
      this.currentId = "";
      this.addAlgorithmVisible = true;
    },
    // 编辑算法
    editData(item) {
      this.currentId = item.id;
      this.addAlgorithmVisible = true;
    },
    upData(item) {
      this.$router.push({
        path: "/algorithmManagement/algorithmArrangeUpdata",
        query: {
          id: item.id,
          name: item.name,
        },
      });
    },

    // 删除算法
    async deleteData(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const res = await deleteData({ id: item.id });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getListData();
          }
        })
        .catch(() => {});
    },
    // 播放视频
    playVideo(item) {
      if (item.videoUri) {
        this.currentVideoName = item.nameEn;
        this.playVideoVisible = true;
      } else {
        this.$message.error($t('applicationMonitoring.algorithmManagement.notVideo'));
      }
    },
    handleTag(tags) {
      const list = [];
      tags.forEach((item) => {
        const obj = this.tagList.find((i) => i.id == item);
        list.push(obj.name);
      });
      return list.join(",");
    },
    close() {
      this.addAlgorithmVisible = false;
      this.getListData();
    },
    addAlarmLevelData() {
      this.listAlarmLevelVisible = true;
    },
  },
};
</script>
<style scoped lang="scss">
.search_box {
  padding-bottom: 20px;
}
</style>
