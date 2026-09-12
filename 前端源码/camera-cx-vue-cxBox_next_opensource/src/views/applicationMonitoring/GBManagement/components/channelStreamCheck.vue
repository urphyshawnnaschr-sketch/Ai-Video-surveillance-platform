<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('components.channelstreamcheck.ng9qyk')"
    :visible.sync="dialogVisible"
    width="400px"
    @close="closed"
    append-to-body
  >
    <div class="config-cont">
      <div v-if="checking" class="text-tip">
        <i class="el-icon-loading" />{{
          $t("components.channelstreamcheck.53pc2v")
        }}
      </div>
      <div v-if="!checking && !checkSuccess" class="err-tip">
        {{ $t("components.channelstreamcheck.nmy36g") }}
      </div>
      <div v-if="!checking && checkSuccess">
        <div class="vcode">
          {{ $t("components.channelstreamcheck.u37u37") }}{{ videoCodec }}
        </div>
        <img :src="imgUrl" class="img" />
      </div>
      <div class="bot-btn" v-if="!checking && !checkSuccess">
        <el-link
          type="danger"
          :underline="true"
          @click="getChannelCheckStream"
          >{{ $t("components.channelstreamcheck.h822s3") }}</el-link
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { channelCheckStream } from "../api";
export default {
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      imgUrl: "",
      videoCodec: "",
      checking: true,
      checkSuccess: false,
    };
  },
  created() {
    this.getChannelCheckStream();
  },
  methods: {
    // 获取数据
    getChannelCheckStream() {
      this.checking = true;
      channelCheckStream({ id: this.currentId })
        .then((res) => {
          this.checking = false;
          this.checkSuccess = true;

          if (res.data.success) {
            this.checkSuccess = true;
            this.videoCodec = res.data.videoCodecName;
            this.imgUrl = this.$common.handleCameraImgUrl(res.data.fileName);
          } else {
            this.checkSuccess = false;
          }
        })
        .catch((e) => {
          this.checking = false;
          this.checkSuccess = false;
        });
    },
    closed() {
      this.$emit("closeChannelStreamCheck");
    },
  },
};
</script>
<style scoped lang="scss">
.bot-btn {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
.text-tip {
  text-align: center;
  font-size: 16px;
  color: #999;
  padding: 40px 0 40px 0;
  width: 100%;
}
.err-tip {
  text-align: center;
  font-size: 16px;
  color: #dd383e;
  color: #999;
  padding: 40px 0 40px 0;
  width: 100%;
}
.vcode {
  padding-bottom: 10px;
}
.img {
  width: 100%;
  height: 100%;
}
::v-deep .el-dialog__body {
  padding: 0 20px 20px 20px;
}
</style>
