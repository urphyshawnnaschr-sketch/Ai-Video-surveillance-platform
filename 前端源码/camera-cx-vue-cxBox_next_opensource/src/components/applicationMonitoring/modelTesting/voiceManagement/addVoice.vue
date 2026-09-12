<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('voicemanagement.addvoice.5t82l9')"
    :visible.sync="dialogVisible"
    width="500px"
    append-to-body
    :before-close="handleDialogClose"
  >
    <el-form ref="form" :model="params" :rules="rules" label-width="120px">
      <el-form-item :label="$t('common.associationAlgorithm')" prop="name">
        <el-select v-model="params.id" :placeholder="$t('common.chooseText')">
          <el-option
            v-for="item in algorithmList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('voicemanagement.addvoice.761i3w')" prop="fileName">
        <el-upload
          class="upload-demo"
          action=""
          accept=".mp3"
          :on-change="handleChange"
          :show-file-list="false"
          :auto-upload="false"
        >
          <el-button slot="trigger" size="small" type="primary">{{
            $t("button.clickToUpload")
          }}</el-button>
          <div slot="tip" class="el-upload__tip">
            <div>{{$t('voicemanagement.addvoice.sh14ce')}}</div>
            <div
              v-if="params.fileName"
              style="
                display: flex;
                justify-content: space-between;
                align-items: center;
              "
            >
              <div>{{ params.fileName }}</div>
              <div
                class="el-icon-close"
                style="cursor: pointer"
                @click="handleRemove"
              ></div>
            </div>
          </div>
        </el-upload>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="closed" v-if="!btnLoading">{{
        $t("button.cancelText", { text: "" })
      }}</el-button>
      <el-button type="primary" @click="saveData" :loading="btnLoading">{{
        $t("button.saveText", { text: "" })
      }}</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { uploadSoundFile } from "@/api/applicationMonitoring/modelTesting";
export default {
  props: {
    algorithmList: {
      type: Array,
      default: [],
    },
    currentObj: {
      type: Object,
      default: {},
    },
  },
  data() {
    return {
      dialogVisible: true,
      params: {
        id: "",
        file: "",
        fileName: "",
      },
      rules: {
        id: [{ required: true, message: this.$t('voicemanagement.addvoice.88x724'), trigger: "change" }],
        fileName: [
          { required: true, message: this.$t('voicemanagement.addvoice.fn4vuf'), trigger: "change" },
        ],
      },
      fileList: [],
      btnLoading: false,
      tipText: "",
    };
  },
  created() {
    if (JSON.stringify(this.currentObj) != "{}") {
      console.log(this.currentObj.id);
      this.params.id = this.currentObj.id;
      this.params.fileName = this.currentObj.soundFile;
    }
  },
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
      const isLimit = file.size / 1024 / 1024 < 3;
      if (!isLimit) {
        this.$message.error(this.$t('voicemanagement.addvoice.tq7p7e'));
        return;
      }
      console.log(file);
      let Arr = [];
      Arr.push(file);
      // this.fileList = Arr;
      this.params.fileName = file.name;
      this.params.file = file.raw;
      // this.params.file =
      this.$refs.form.validate();
    },
    handleRemove() {
      this.params.file = "";
      this.params.fileName = "";
      this.$refs.form.validate();
    },
    // 导入
    saveData() {
      this.btnLoading = true;
      this.$refs.form.validate((valid) => {
        if (valid) {
          // if(!this.params.file){
          //     this.$emit('closeAdd');
          //     return;
          // }
          let formData = new FormData();
          formData.append("id", this.params.id);
          if (this.currentObj.id) {
            if (this.params.id != this.currentObj.id || !this.params.file) {
              formData.append("oldId", this.currentObj.id);
            }
          }
          if (this.params.file) {
            formData.append("file", this.params.file);
          }
          uploadSoundFile(formData)
            .then((res) => {
              this.tipText = "";
              this.btnLoading = false;
              if (res.code == 0 && JSON.stringify(res.data) == "{}") {
                this.$message.success(
                  $t("button.saveText", { text: $t("common.success") })
                );
                this.$emit("closeAdd");
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
<style scoped lang="scss"></style>
