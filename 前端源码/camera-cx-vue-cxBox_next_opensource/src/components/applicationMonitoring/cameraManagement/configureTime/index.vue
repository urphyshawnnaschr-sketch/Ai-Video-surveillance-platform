<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('applicationMonitoring.common.configuration')"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <div style="padding-bottom: 20px">
        <el-button type="primary" icon="el-icon-plus" @click="addTimeData">{{
          $t("common.add", { text: $t("common.alarmDuration") })
        }}</el-button>
      </div>
      <div>
        <el-table
          :data="tableData"
          v-loading="loading"
          border
          style="width: 100%"
        >
          <el-table-column
            align="center"
            prop="cameraName"
            :label="$t('common.cameraName')"
          >
          </el-table-column>
          <el-table-column
            align="center"
            prop="algorithmName"
            :label="$t('common.algorithmName')"
          >
          </el-table-column>
          <el-table-column align="center" prop="period" :label="$t('addcamera.newadd.555rc5')">
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.action', { text: '' })"
          >
            <template slot-scope="scope">
              <el-button type="text" @click="editTimeData(scope.row)">{{
                $t("common.edit", { text: "" })
              }}</el-button>
              <el-button
                type="text"
                class="danger"
                @click="deleteTimeData(scope.row)"
                >{{ $t("button.deleteText", { text: "" }) }}</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :close-on-click-modal="false"
      :title="addTimeTitle"
      :visible.sync="addTimeVisible"
      width="400px"
    >
      <el-form :model="currentTimeData">
        <el-form-item :label="$t('common.algorithmName')">
          <el-select
            v-model="currentTimeData.algorithmId"
            :placeholder="$t('configuretime.index.esqh94')"
            clearable
          >
            <el-option
              v-for="(item, index) in algorithmOptions"
              :key="index"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('addcamera.timeinfo.5r5j4x')">
          <el-time-picker
            v-model="currentTimeData.startText"
            format="HH:mm"
            value-format="HH:mm"
            :placeholder="$t('configuretime.index.71adlx')"
          >
          </el-time-picker>
        </el-form-item>
        <el-form-item :label="$t('addcamera.timeinfo.dc2916')">
          <el-time-picker
            v-model="currentTimeData.endText"
            format="HH:mm"
            value-format="HH:mm"
            :placeholder="$t('configuretime.index.jj7937')"
          >
          </el-time-picker>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addTimeVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveTimeData">{{
          $t("button.submitText")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  getAlgorithmsListData,
  getTimeListData,
  deleteTimeData,
  saveTimeData,
} from "@/api/applicationMonitoring/cameraManagement";
export default {
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      loading: false,
      dialogVisible: true,
      addTimeVisible: false,
      params: {
        cameraId: this.currentId,
        limit: 10,
        page: 1,
      },
      tableData: [],
      currentTimeData: {
        id: "",
        cameraId: this.currentId,
        algorithmId: "",
        startText: "",
        endText: "",
      },
      algorithmOptions: [],
    };
  },
  computed: {
    addTimeTitle: function () {
      return this.currentTimeData?.id
        ? $t("common.edit", { text: $t("common.alarmDuration") })
        : $t("common.add", { text: $t("common.alarmDuration") });
    },
  },
  async created() {
    this.getAlgorithmsListData();
    this.getTimeListData();
  },
  methods: {
    // 获取配置时段列表
    async getTimeListData() {
      this.loading = true;
      const data = await getTimeListData(this.params);
      this.loading = false;
      this.tableData = data.data;
    },
    // 获取算法下拉列表
    async getAlgorithmsListData() {
      const data = await getAlgorithmsListData({ cameraId: this.currentId });
      this.algorithmOptions = data.data;
    },
    // 新增时段
    addTimeData() {
      this.currentTimeData = {};
      this.addTimeVisible = true;
    },
    // 编辑时段
    editTimeData(item) {
      Object.assign(this.currentTimeData, {
        id: item.id,
        algorithmId: item.algorithmId,
        startText: item.period.split(" - ")[0],
        endText: item.period.split(" - ")[1],
      });
      this.addTimeVisible = true;
    },
    // 删除时段
    async deleteTimeData(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await deleteTimeData({ id: item.id });
        if (res.code == 0) {
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
          await this.getTimeListData();
        }
      });
    },
    // 保存告警时段
    async saveTimeData() {
      Object.assign(this.currentTimeData, {
        cameraId: this.currentId,
      });
      await saveTimeData(this.currentTimeData);
      this.addTimeVisible = false;
      this.getTimeListData();
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
