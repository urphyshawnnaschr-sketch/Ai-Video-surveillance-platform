<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('addpage.index.svq779')"
      :visible.sync="dialogVisible"
      width="500px"
      append-to-body
      :before-close="handleDialogClose"
    >
      <el-form ref="form" :model="params" :rules="rules" label-width="120px">
        <el-form-item :label="$t('common.algorithmName')" prop="name">
          <el-input v-model="params.name"></el-input>
        </el-form-item>
        <el-form-item :label="$t('modeltesting.addupload.bdb667')" prop="file">
          <el-upload
            class="upload-demo"
            action=""
            :on-change="handleChange"
            :on-remove="handleRemove"
            :file-list="fileList"
            :auto-upload="false"
          >
            <el-button slot="trigger" size="small" type="primary">{{
              $t("button.clickToUpload")
            }}</el-button>
            <div slot="tip" class="el-upload__tip">
              <div v-if="tipText" style="color: red">{{ tipText }}</div>
              <div v-else>{{$t('modeltesting.addupload.btlqhr')}}</div>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closed" v-if="!btnLoading">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveData" :loading="btnLoading"
          >{{$t('batchupload.upload.s63286')}}</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { uploadZipSave } from "@/api/applicationMonitoring/modelTesting";
export default {
  data() {
    return {
      dialogVisible: true,
      params: {
        name: "",
        file: "",
      },
      rules: {
        name: [{ required: true, message: $t('form.tip.inputAlgorithmName'), trigger: "blur" }],
        file: [
          { required: true, message: this.$t('modeltesting.addupload.wrjul1'), trigger: "change" },
        ],
      },
      fileList: [],
      btnLoading: false,
      tipText: "",
    };
  },
  created() {},
  methods: {
    handleDialogClose() {
      if (!this.btnLoading) {
        this.$emit("closeAdd");
      }
    },
    closed() {
      this.$emit("closeAdd");
    },
    handleChange(file, fileList) {
      let Arr = [];
      Arr.push(file);
      this.fileList = Arr;
      this.params.file = file.raw;
      this.$refs.form.validate();
    },
    handleRemove(file, fileList) {
      this.params.file = "";
      this.fileList = [];
      this.$refs.form.validate();
    },
    // 导入
    saveData() {
      this.btnLoading = true;
      this.$refs.form.validate((valid) => {
        if (valid) {
          let formData = new FormData();
          formData.append("name", this.params.name);
          formData.append("file", this.params.file);
          uploadZipSave(formData)
            .then((res) => {
              this.tipText = "";
              this.btnLoading = false;
              if (res.code == 0 && JSON.stringify(res.data) == "{}") {
                this.$message.success(
                  $t("faceControl.faceRecognition.importSuccess")
                );
                this.$emit("closeAdd", true);
              } else {
                this.tipText = res.data;
              }
            })
            .catch(() => {
              this.btnLoading = false;
            });
        } else {
          this.btnLoading = false;
          return false;
        }
      });
    },
  },
};
</script>
<style lang="scss"></style>
