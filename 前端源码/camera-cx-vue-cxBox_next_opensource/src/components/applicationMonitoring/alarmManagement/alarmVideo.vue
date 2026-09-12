<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('alarmmanagement.alarmvideo.581178')"
      :visible.sync="dialogVisible"
      width="900px"
      :append-to-body="true"
      @closed="closed"
    >
      <video
        :src="videoUrl"
        controls="controls"
        autoplay
        muted
        width="100%"
        height="100%"
      ></video>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{$t('applicationMonitoring.boxManagement.q827fn')}}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>


export default {
  props: {
    videoUrl: {
      type: String,
      default: () => [],
    },
  },
  data() {
    return {
      dialogVisible: true,
    };
  },
  methods: {
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
  