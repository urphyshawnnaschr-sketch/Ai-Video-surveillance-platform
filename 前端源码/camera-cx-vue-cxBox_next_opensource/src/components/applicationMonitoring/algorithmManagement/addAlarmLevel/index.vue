<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" :rules="rules" label-width="120px">
          <el-form-item :label="$t('addalarmlevel.index.ph7u68')" prop="name">
            <el-input v-model="params.name"></el-input>
          </el-form-item>

          <el-form-item :label="$t('addalarmlevel.index.p8hkqm')" prop="showColor">
            <el-color-picker
              v-model="params.showColor"
              :predefine="predefineColors"
            >
            </el-color-picker>
          </el-form-item>
          <el-form-item :label="$t('addalarmlevel.index.7cmcv1')" prop="nameEn">
            <el-checkbox-group v-model="params.showTypes">
              <el-checkbox
                v-for="item in alarmShowTypes"
                :label="item.code"
                :key="item.code"
              >
                {{ item.text }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveAlarmLevelData">{{
          $t("button.submitText")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  saveAlarmLevel,
  getAlarmLevel,
  getAlarmShowTypeList,
} from "@/api/applicationMonitoring/algorithmManagement";
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
      title: this.currentId
        ? $t("common.edit", { text: $t("common.alarmLevel") })
        : $t("common.add", { text: $t("common.alarmLevel") }),
      dialogVisible: true,
      params: {
        id: this.currentId,
        name: "",
        showColor: "#ff4500",
        showTypes: [],
      },
      alarmShowTypes: [],
      rules: {
        name: [
          {
            required: true,
            message: this.$t('addalarmlevel.index.skqcd7'),
            trigger: "blur",
          },
        ],
      },
      predefineColors: [
        "#ff4500",
        "#ff8c00",
        "#ffd700",
        "#90ee90",
        "#00ced1",
        "#1e90ff",
        "#c71585",
      ],
    };
  },
  async created() {
    await this.listAlarmShowTypes();
    await this.getAlarmLevelInfo();
  },
  methods: {
    //
    async listAlarmShowTypes() {
      const data = await getAlarmShowTypeList({
        id: this.currentId,
      });
      // 只显示 code 为 'sms' 的项
      this.alarmShowTypes = data.data.filter(item => item.code === 'sms');
    },
    //
    async getAlarmLevelInfo() {
      const data = await getAlarmLevel({
        id: this.currentId,
      });
      //
      if (Object.keys(data.data).length > 0) {
        this.params = data.data;
      }
    },
    //
    saveAlarmLevelData() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          await saveAlarmLevel(this.params);
          this.$message.success(
            this.currentId
              ? $t("common.edit", { text: $t("common.success") })
              : $t("common.add", { text: $t("common.success") })
          );
          this.dialogVisible = false;
        } else {
          return false;
        }
      });
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
