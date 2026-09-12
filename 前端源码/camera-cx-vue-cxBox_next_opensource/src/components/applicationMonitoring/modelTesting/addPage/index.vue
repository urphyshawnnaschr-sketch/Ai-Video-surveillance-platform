<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('common.add', { text: $t('common.algorithm') })"
      :visible.sync="dialogVisible"
      width="500px"
      @close="closed"
    >
      <div>
        <div class="title line32">{{ $t("addpage.index.n6q68d") }}</div>
        <div class="tip">
          <span class="title">{{ $t("addpage.index.82o72m") }}</span
          >{{ $t("addpage.index.z4swe3")
          }}<span class="title">{{ $t("addpage.index.svq779") }}</span
          >”
        </div>
        <div class="tip">
          <span class="title">{{ $t("addpage.index.844x4q") }}</span
          >{{ $t("addpage.index.z4swe3")
          }}<span class="title">{{
            $t("common.add", { text: $t("common.algorithmCard") })
          }}</span
          >”
        </div>
        <div class="btn-sty">
          <el-button type="primary" icon="el-icon-box" @click="addUploadFun">{{
            $t("addpage.index.svq779")
          }}</el-button>
        </div>
        <div class="btn-sty">
          <el-button icon="el-icon-bank-card" @click="addFun">{{
            $t("common.add", { text: $t("common.algorithmCard") })
          }}</el-button>
        </div>
      </div>
    </el-dialog>
    <AddUpload v-if="addUploadVisible" @closeAdd="closeUpload" />
    <AddInfo v-if="addAlgorithmVisible" @closeHandel="closeHandel" />
  </div>
</template>
<script>
import AddUpload from "../addUpload";
import AddInfo from "./addInfo";
export default {
  components: {
    AddUpload,
    AddInfo,
  },
  data() {
    return {
      dialogVisible: true,
      addUploadVisible: false,
      addAlgorithmVisible: false,
    };
  },
  created() {},
  methods: {
    addUploadFun() {
      this.addUploadVisible = true;
    },
    addFun() {
      this.addAlgorithmVisible = true;
    },
    closeUpload(isClose) {
      this.addUploadVisible = false;
      if (isClose) {
        this.$emit("closeAdd");
      }
    },
    closeHandel(isClose) {
      this.addAlgorithmVisible = false;
      if (isClose) {
        this.$emit("closeAdd");
      }
    },
    closed() {
      this.$emit("closeAdd");
    },
  },
};
</script>
<style lang="scss">
.title {
  font-size: 13px;
  color: #1A0808;
  font-weight: bold;
  letter-spacing: 1px;
}
.tip {
  color: #b1b3b8;
  font-size: 13px;
  line-height: 24px;
  letter-spacing: 1px;
}
.btn-sty {
  text-align: center;
  margin-top: 20px;
}
.line32 {
  line-height: 32px;
}
</style>
