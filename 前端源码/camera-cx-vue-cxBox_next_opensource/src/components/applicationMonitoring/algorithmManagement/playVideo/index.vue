<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('applicationMonitoring.algorithmManagement.dwyg7y')"
      :visible.sync="dialogVisible"
      width="1000px"
      @closed="closed"
    >
      <video :src="videoUrl" class="video" controls autoplay></video>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { getDemoStream } from "@/api/applicationMonitoring/algorithmManagement";
export default {
  props: {
    currentName: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      videoUrl: "",
    };
  },
  async mounted() {
    this.getDemoStream();
  },
  methods: {
    async getDemoStream() {
      const data = await getDemoStream({ suanfa: this.currentName });
      let blob = new Blob([data.data]);
      let str = window.URL.createObjectURL(blob);
      this.videoUrl = str;
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss">
.video {
  width: 100%;
}
</style>
