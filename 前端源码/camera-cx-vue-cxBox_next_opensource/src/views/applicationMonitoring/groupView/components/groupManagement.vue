<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="800px"
      @closed="closed"
    >
      <div style="text-align: center">
        <el-transfer
          style="text-align: left; display: inline-block"
          v-model="selectsList"
          filterable
          :titles="[$t('components.groupmanagement.1ky26o'), $t('components.groupmanagement.0d5l39')]"
          :button-texts="[$t('components.groupmanagement.566o1v'), $t('addproject.step2.459446')]"
          :format="{
            noChecked: '${total}',
            hasChecked: '${checked}/${total}',
          }"
          @change="handleChange"
          :data="camerasList"
        >
        </el-transfer>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveFun">{{$t('annotationplatform.annotationgroupmanagement.th446b')}}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { groupCameraSave, camerasInfo } from "../api";
export default {
  props: {
    groupId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      title: this.$t('components.groupmanagement.51q123'),
      camerasList: [],
      selectsList: [],
    };
  },
  created() {
    this.getameras();
  },
  methods: {
    closed() {
      this.$emit("close");
    },
    // 获取摄像头
    async getameras() {
      const res = await camerasInfo({ id: this.groupId });
      let arr = [];

      if (res.data.cameras && res.data.cameras.length > 0) {
        res.data.cameras.forEach((item) => {
          arr.push({
            key: item.id,
            label: item.name,
          });
        });
      }
      this.camerasList = arr;
      this.selectsList = res.data.selects;
    },
    handleChange(value, direction, movedKeys) {
      console.log(this.selectsList);
    },
    // 保存
    async saveFun() {
      // if(this.selectsList.length==0){
      //     this.$message.error("请选择数据")
      //     return
      // }
      let obj = {
        groupId: this.groupId,
        cameraIds: this.selectsList,
      };
      const res = await groupCameraSave(obj);
      this.$message.success(
        $t("button.saveText", { text: $t("common.success") })
      );
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
