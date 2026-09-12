<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('export.index.cmqqrj')"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <el-form label-width="120px">
        <el-form-item :label="$t('common.camera')">
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
        </el-form-item>
        <el-form-item :label="$t('applicationMonitoring.incrementalRraining.zzeg52')">
          <el-select
            :placeholder="$t('applicationMonitoring.incrementalRraining.zzeg52')"
            clearable
            v-model="params.algorithmId"
            class="head-container-input"
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
          <el-date-picker
            v-model="params.startText"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            type="date"
            :placeholder="$t('export.index.k66763')"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item :label="$t('addcamera.timeinfo.dc2916')">
          <el-date-picker
            v-model="params.endText"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            :placeholder="$t('export.index.k66763')"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item
          :label="$t('applicationMonitoring.incrementalRraining.auditStatus')"
        >
          <el-radio-group v-model="params.auditState">
            <el-radio-button label="0">{{ $t('common.allText') }}</el-radio-button>
            <el-radio-button label="1">{{ $t('applicationMonitoring.incrementalRraining.audited') }}</el-radio-button>
            <el-radio-button label="2">{{ $t('applicationMonitoring.incrementalRraining.pendingApproval') }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          ><span style="color: red"
            >{{$t('export.index.5d2y21')}}</span
          ></el-form-item
        >
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="exportData">{{$t('projectmanagement.index.12bp68')}}</el-button>
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  getCameraListData,
  getAlgorithmListData,
  exportData,
} from "@/api/applicationMonitoring/incrementalRraining";
export default {
  data() {
    return {
      dialogVisible: true,
      cameraOptions: [],
      algorithmOptions: [],
      params: {
        cameraId: "",
        algorithmId: "",
        startText: "",
        endText: "",
        auditState: 0,
      },
    };
  },
  created() {
    this.getOptions();
  },
  methods: {
    // 获取下拉
    async getOptions() {
      const data1 = await getCameraListData();
      this.cameraOptions = data1.data;
      const data2 = await getAlgorithmListData();
      this.algorithmOptions = data2.data;
    },
    async exportData() {
      await exportData(this.params);
      this.$message.success(this.$t('export.index.0l74vk'));
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
