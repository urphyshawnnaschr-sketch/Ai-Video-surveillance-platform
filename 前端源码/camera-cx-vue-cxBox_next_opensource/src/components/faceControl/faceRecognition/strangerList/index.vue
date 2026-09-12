<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('strangerlist.index.q3gwz6')"
      :visible.sync="dialogVisible"
      width="1200px"
      @closed="closed"
    >
      <div class="head-container">
        <el-select
          :placeholder="$t('common.camera')"
          clearable
          v-model="params.cameraId"
          class="head-container-input"
        >
          <el-option
            v-for="(item, index) in cameraOptions"
            :key="index"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
        <el-date-picker
          v-model="date"
          type="daterange"
          :range-separator="$t('common.pickerDate.to')"
          :start-placeholder="$t('common.pickerDate.startDate')"
          :end-placeholder="$t('common.pickerDate.endDate')"
          value-format="yyyy-MM-dd"
          format="yyyy-MM-dd"
        >
        </el-date-picker>
        <el-button type="primary" icon="el-icon-search" @click="getListData">{{
          $t("button.queryText")
        }}</el-button>
        <el-button icon="el-icon-refresh" @click="refreshData">{{
          $t("button.resetText", { text: "" })
        }}</el-button>
      </div>
      <el-row :gutter="20" v-if="cardList.length">
        <el-col :span="6" v-for="(item, index) in cardList" :key="index">
          <div class="item">
            <MarkResult
              :fileUrl="$common.handlePublicUrl(item.image)"
              :dataList="JSON.parse(item.params)"
            />
            <el-descriptions
              :colon="false"
              :column="1"
              style="margin-top: 10px"
            >
              <el-descriptions-item>{{ item.cameraName }}</el-descriptions-item>
              <el-descriptions-item>{{ item.wareName }}</el-descriptions-item>
              <el-descriptions-item>{{ item.alarmTime }}</el-descriptions-item>
              <el-descriptions-item>
                <div
                  style="
                    display: flex;
                    justify-content: space-between;
                    width: 250px;
                  "
                >
                  <el-button type="text" @click="clickProcessed(item)">{{$t('strangerdetail.index.w65635')}}</el-button>
                  <el-button
                    type="text"
                    class="danger"
                    @click="clickMisreport(item)"
                  >{{$t('strangerdetail.index.1qqcv1')}}</el-button>
                  <el-button
                    type="text"
                    class="info"
                    @click="clickDetail(item)"
                  >{{$t('components.detail.bz9jg2')}}</el-button>
                </div>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>
      </el-row>
      <div class="noData" v-else>{{ $t("common.noData") }}</div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
    <StrangerDetail
      :currentData="currentData"
      v-if="strangerDetailVisible"
      @close="(strangerDetailVisible = false), getListData()"
    />
  </div>
</template>
<script>
import { getCameraListData } from "@/api/applicationMonitoring/alarmManagement";
import {
  getCardList,
  processed,
  misreport,
} from "@/api/faceControl/faceRecognition";
import MarkResult from "@/components/markResult";
import StrangerDetail from "@/components/faceControl/faceRecognition/strangerDetail";
export default {
  components: {
    MarkResult,
    StrangerDetail,
  },
  data() {
    return {
      dialogVisible: true,
      strangerDetailVisible: false,
      date: [
        this.$moment(new Date()).format("YYYY-MM-DD"),
        this.$moment(new Date()).format("YYYY-MM-DD"),
      ],
      params: {
        page: 1,
        limit: 8,
        cameraId: "",
        startDate: "",
        endDate: "",
      },
      total: 0,
      cameraOptions: [],
      cardList: [],
      currentData: {},
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
    },
    // 获取陌生人列表
    async getListData() {
      this.params.startDate = this.date[0];
      this.params.endDate = this.date[1];
      const data = await getCardList(this.params);
      this.cardList = data.data;
    },
    // 重置
    refreshData() {
      this.date = [
        this.$moment(new Date()).format("YYYY-MM-DD"),
        this.$moment(new Date()).format("YYYY-MM-DD"),
      ];
      Object.assign(this.params, {
        page: 1,
        limit: 8,
        cameraId: "",
        startDate: "",
        endDate: "",
      });
      this.getListData();
    },
    // 人员已处理
    clickProcessed(item) {
      this.$confirm(this.$t('strangerdetail.index.wl5qfs'), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await processed({ id: item.id });
        if (res.code == 0) {
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
          await this.getListData();
        }
      });
    },
    // 人员误报
    clickMisreport(item) {
      this.$confirm(this.$t('strangerdetail.index.s8opoh'), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await misreport({ id: item.id });
        if (res.code == 0) {
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
          await this.getListData();
        }
      });
    },
    // 详情
    clickDetail(item) {
      this.currentData = item;
      this.strangerDetailVisible = true;
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getFaceList();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getFaceList();
    },
  },
};
</script>
<style scoped lang="scss">
.item {
  border: 1px #e8e8e8 solid;
}
.img {
  width: 100%;
  display: block;
}
.noData {
  line-height: 120px;
  text-align: center;
}
::v-deep .el-range-editor.el-input__inner {
  padding: 0 10px;
  margin-right: 10px;
}
::v-deep .el-range-separator {
  line-height: 30px !important;
}
</style>
