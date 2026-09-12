<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      width="60%"
      top="10vh"
      :title="$t('applicationMonitoring.incrementalRraining.detailed')"
      :visible.sync="detailVisible"
      @closed="closed"
      append-to-body
    >
      <el-form
        :model="ruleForm"
        ref="ruleForm"
        label-width="100px"
        size="mini"
        label-position="left"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('common.boxName')">{{
              ruleForm.name
            }}</el-form-item>
            <el-form-item :label="$t('common.ipAddress')">{{
              ruleForm.ipAddr
            }}</el-form-item>
            <el-form-item
              :label="$t('applicationMonitoring.casketManagement.cpuModel')"
              >{{ ruleForm.cpuVersion }}</el-form-item
            >
            <el-form-item
              :label="$t('applicationMonitoring.casketManagement.disk')"
              >{{ ruleForm.diskTotal }}</el-form-item
            >
            <el-form-item
              :label="$t('applicationMonitoring.casketManagement.manufacturer')"
              >{{ ruleForm.makers }}</el-form-item
            >
            <el-form-item
              :label="
                $t('applicationMonitoring.casketManagement.driverVersion')
              "
              >{{ ruleForm.lyndriverVersion }}</el-form-item
            >
            <el-form-item :label="$t('common.presence')">{{
              ruleForm.online == 0
                ? $t("common.offline")
                : ruleForm.online == 1
                ? $t("common.online")
                : ""
            }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('common.boxNumber')">{{
              ruleForm.boxNo
            }}</el-form-item>
            <el-form-item
              :label="
                $t('applicationMonitoring.casketManagement.KernelVersion')
              "
              >{{ ruleForm.kernelVersion }}</el-form-item
            >
            <el-form-item
              :label="
                $t('applicationMonitoring.casketManagement.systemVersion')
              "
              >{{ ruleForm.osVersion }}</el-form-item
            >
            <el-form-item
              :label="
                $t('applicationMonitoring.casketManagement.memoryCapacity')
              "
              >{{ ruleForm.memoryTotal }}</el-form-item
            >
            <el-form-item
              :label="
                $t('applicationMonitoring.casketManagement.equipmentMode')
              "
              >{{ ruleForm.deviceMode }}</el-form-item
            >
            <el-form-item
              :label="$t('applicationMonitoring.casketManagement.SDKVersion')"
              >{{ ruleForm.lynsdkVersion }}</el-form-item
            >
            <el-form-item :label="$t('applicationMonitoring.boxManagement.lastHeartbeat')">{{
              getMyDate(Number(ruleForm.boxHeartTime))
            }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { detail } from "@/api/applicationMonitoring/casketManagement";
import { getMyDate } from "@/utils/common.js";
export default {
  props: {
    rowId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      ruleForm: {},
      detailVisible: true,
      getMyDate: getMyDate,
    };
  },
  created() {
    this.getData();
  },
  methods: {
    // 获取数据
    async getData() {
      const { data } = await detail({ id: this.rowId });
      this.ruleForm = { ...data };
    },
    // 关闭弹窗
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
