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
        <el-form ref="form" :model="params" :rules="rules" label-width="80px">
          <el-form-item :label="$t('addsystem.index.516b52')" prop="name">
            <el-input v-model="params.name"></el-input>
          </el-form-item>
          <el-form-item :label="$t('addsystem.index.5kp136')" prop="tag">
            <el-input v-model="params.tag"></el-input>
          </el-form-item>
          <el-form-item :label="$t('addsystem.index.f722po')" prop="val">
            <el-input v-model="params.val"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveData">{{
          $t("button.submitText")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  getListDataDetail,
  saveData,
} from "@/api/applicationMonitoring/systemManagement";
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
        ? $t("common.edit", { text: $t("common.config") })
        : $t("common.add", { text: $t("common.config") }),
      dialogVisible: true,
      params: {
        id: this.currentId,
        name: "",
        tag: "",
        val: "",
      },
      rules: {
        name: [{ required: true, message: this.$t('addsystem.index.o3leu1'), trigger: "blur" }],
        tag: [{ required: true, message: this.$t('addsystem.index.0x30l1'), trigger: "blur" }],
        val: [{ required: true, message: this.$t('addsystem.index.0x30l1'), trigger: "blur" }],
      },
      tableData: [],
    };
  },
  async created() {
    if (this.currentId) {
      await this.getListDataDetail();
    }
  },
  methods: {
    // 获取配置详情
    async getListDataDetail() {
      const data = await getListDataDetail({ id: this.currentId });
      Object.assign(this.params, {
        name: data.data.name,
        tag: data.data.tag,
        val: data.data.val,
      });
    },
    //
    // 保存配置
    saveData() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          await saveData(this.params);
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
