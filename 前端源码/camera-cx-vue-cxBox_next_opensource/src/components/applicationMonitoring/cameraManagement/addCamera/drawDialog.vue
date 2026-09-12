<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      width="800px"
      top="10vh"
      :title="$t('addcamera.drawdialog.62ien4')"
      :visible.sync="innerVisible"
      @closed="closed"
      append-to-body
    >
      <div>
        <MarkDetail
          ref="markDetail"
          v-if="detailObj.fileName"
          :fileUrl="$common.handleCameraImgUrl(detailObj.fileName)"
          :dataList="dataList"
          :nameEn="nameEn"
          :disabled="drawDisabled"
        />
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button v-if="dataList.length > 0" @click="deleteDraw">{{
          $t("button.deleteText", { text: "" })
        }}</el-button>
        <el-button @click="cancelDraw">{{$t('addcamera.drawdialog.2szqc4')}}</el-button>
        <el-button
          type="primary"
          v-if="nameEn != 'person_tracker'"
          @click="newDraw"
          >{{$t('addcamera.drawdialog.6edftx')}}</el-button
        >
        <el-button
          type="primary"
          v-if="nameEn == 'person_tracker'"
          @click="LineDraw"
          >{{$t('addcamera.drawdialog.53cuig')}}</el-button
        >
        <el-button type="primary" @click="saveDraw">{{$t('addcamera.drawdialog.721n87')}}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import MarkDetail from "@/components/markDetail";
import {
  saveInfo,
  deleteInfo,
} from "@/api/applicationMonitoring/cameraManagement";
export default {
  components: {
    MarkDetail,
  },
  props: {
    detailObj: {
      type: Object,
      default: () => {},
    },
    dataList: {
      type: Array,
      default: [],
    },
    algorithmId: {
      type: String,
      default: "",
    },
    nameEn: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      innerVisible: true,
      drawDisabled: false,
    };
  },
  async created() {},
  methods: {
    //删除
    async deleteDraw() {
      let params = {
        cameraId: this.detailObj.id,
        algorithmId: this.algorithmId,
      };
      const data = await deleteInfo(params);
      this.$message.success(
        $t("button.deleteText", { text: $t("common.success") })
      );
      this.dataList = [];
      this.$refs.markDetail.clear();
    },
    //取消绘制
    cancelDraw() {
      this.$emit("close");
    },
    //保存绘制
    async saveDraw() {
      let markPointsArr = [];
      let lineMarkPoints = [];
      let Arr = this.$refs.markDetail.save();
      Arr.forEach((i) => {
        if (this.nameEn == "person_tracker" && i.pointList.length == 2) {
          lineMarkPoints.push(i.pointList);
        } else {
          markPointsArr.push(i.pointList);
        }
      });
      if (markPointsArr.length == 0 || markPointsArr[0].length < 3) {
        this.$message.error(this.$t('addcamera.drawdialog.1334e1'));
        return;
      }
      let params = {
        cameraId: this.detailObj.id,
        algorithmId: this.algorithmId,
        markPoints:
          markPointsArr.length > 0 ? JSON.stringify(markPointsArr) : "",
        lineMarkPoints:
          lineMarkPoints.length > 0 ? JSON.stringify(lineMarkPoints) : "",
      };
      const data = await saveInfo(params);
      this.$message.success(
        $t("button.saveText", { text: $t("common.success") })
      );
      this.innerVisible = false;
      this.$emit("close");
    },
    closed() {
      this.$emit("close");
    },
    newDraw() {
      this.$refs.markDetail.resetting();
    },
    LineDraw() {
      let Arr = this.$refs.markDetail.save();
      console.log(Arr);
      if (Arr.length == 0) {
        this.$message.warning(this.$t('addcamera.drawdialog.5b4171'));
        return;
      }
      this.$refs.markDetail.resetting();
    },
  },
};
</script>
<style scoped lang="scss"></style>
