<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('alarmdetail.index.3r91n2')"
      :visible.sync="dialogVisible"
      width="1000px"
      @closed="closed"
    >
      <MarkResult
        v-if="detail.report?.id"
        :fileUrl="$common.handleStream(detail.report.id)"
        :dataList="boxList"
      />
      <div class="name">
        {{ `${$t("common.algorithmName")}：${detail.algorithm?.name}` }}
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="auditData(1)">{{$t('applicationMonitoring.incrementalRraining.6lrx3t')}}</el-button>
        <el-button type="danger" @click="auditData(2)">{{$t('applicationMonitoring.incrementalRraining.ox0d1x')}}</el-button>
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import MarkResult from "@/components/markResult";
import {
  getListDataDetail,
  auditData,
} from "@/api/applicationMonitoring/incrementalRraining";
export default {
  components: {
    MarkResult,
  },
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      detail: {},
      boxList: [],
    };
  },
  async created() {
    this.getListDataDetail();
  },
  methods: {
    // 获取摄像头详情
    async getListDataDetail() {
      const data = await getListDataDetail({ id: this.currentId });
      this.detail = data.data;
      this.boxList = JSON.parse(data.data.report.params);
    },
    async auditData(val) {
      const params = {
        id: this.detail.report.id,
        result: val,
      };
      await auditData(params);
      this.$message.success(this.$t('alarmdetail.index.7m2583'));
      this.dialogVisible = false;
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss">
.image {
  width: 100%;
  height: auto;
  display: block;
}
.name {
  padding: 20px 0;
}
</style>
