<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('modeltesting.drawarea.d8t7w1')"
      :visible.sync="isDrawArea"
      width="800px"
      :append-to-body="true"
      @closed="closed"
    >
      <div>
        <MarkDetail
          ref="markDetail"
          v-if="file"
          :fileUrl="$common.handleImgUrl(file)"
          :dataList="dataList"
          @dataChange="dataChange"
        />
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resettingFun">{{
          $t("button.resetText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveDraw">{{
          $t("button.saveText", { text: '' })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import MarkDetail from "@/components/markDetail";
import { firstPicFromMp4 } from "@/api/applicationMonitoring/modelTesting";
export default {
  components: {
    MarkDetail,
  },
  props: {
    fileName: {
      type: String,
      default: "",
    },
    dataList: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {
      isDrawArea: true,
      marks: "",
      file: "",
    };
  },
  created() {
    this.getImg();
  },
  mounted() {
    if (this.dataList.length > 0) {
      this.marks = JSON.stringify(this.dataList);
    }
  },
  methods: {
    // 获取第一帧图片
    async getImg() {
      const res = await firstPicFromMp4({ file: this.fileName });
      this.file = res.data;
    },
    // 关闭回调
    closed() {
      this.$emit("close", this.marks);
    },
    //绘制
    saveDraw() {
      console.log();
      let obj = {
        marks: this.marks,
        imgHeight: this.$refs.markDetail.$refs.draw_img.offsetHeight,
      };
      this.$emit("close", obj);
    },
    // 重置
    resettingFun() {
      this.dataList = [];
      this.marks = "";
    },
    // 编辑禁区
    dataChange(val) {
      const arr = [];
      val.forEach((item) => {
        arr.push(item.pointList);
      });
      this.marks = JSON.stringify(arr);
    },
  },
};
</script>
<style scoped lang="scss"></style>
