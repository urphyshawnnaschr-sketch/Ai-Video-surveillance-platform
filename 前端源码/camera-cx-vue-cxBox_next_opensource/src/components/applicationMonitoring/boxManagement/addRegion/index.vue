<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="500px"
      @closed="closed"
    >
      <el-form label-width="120px">
        <el-form-item :label="$t('addbox.index.s92bmf')">
          {{ detail.parentName }}
        </el-form-item>
        <el-form-item :label="$t('common.areaName')">
          <el-input
            :placeholder="$t('form.tip.inputAreaName')"
            v-model="detail.name"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('addregion.index.x7a83i')">
          <el-input
            :placeholder="$t('addregion.index.5gto3t')"
            v-model="detail.longitude"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('addregion.index.z2nw8y')">
          <el-input
            :placeholder="$t('addregion.index.3s1377')"
            v-model="detail.latitude"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('addbox.index.11t97c')">
          <el-input :placeholder="$t('addbox.index.0s77rk')" v-model="detail.ipAddr"></el-input>
        </el-form-item>
      </el-form>
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
  getRegionDetail,
  saveRegion,
} from "@/api/applicationMonitoring/cameraManagement";
export default {
  props: {
    type: {
      type: String,
      default: "add",
    },
    currentData: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      loading: false,
      dialogVisible: true,
      title:
        this.type == "add"
          ? $t("common.add", { text: $t("common.regionalNodes") })
          : $t("common.edit", { text: $t("common.regionalNodes") }),
      detail: {},
    };
  },
  async created() {
    if (this.type == "edit") {
      this.getRegionDetail();
    } else {
      this.detail = {
        parentName: this.currentData.text,
        parentId: this.currentData.meId,
      };
    }
  },
  methods: {
    // 获取区域详情
    async getRegionDetail() {
      const data = await getRegionDetail({ id: this.currentData.meId });
      this.detail = data.data;
    },
    // 保存数据
    async saveData() {
      const params = {
        id: this.detail.id,
        parentId: this.detail.parentId,
        name: this.detail.name,
        longitude: this.detail.longitude,
        latitude: this.detail.latitude,
        ipAddr: this.detail.ipAddr,
        locationType: 2,
        type: 1,
      };
      await saveRegion(params);
      this.$message.success(
        $t("button.saveText", { text: $t("common.success") })
      );
      this.dialogVisible = false;
      this.$emit("success");
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
.btn-list {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
}
</style>
