<template><div><el-dialog
      :close-on-click-modal="false"
      :title="$t('common.add', {text: $t('common.algorithmCard')})"
      :visible.sync="dialogVisible"
      width="500px"
      append-to-body
      :before-close="handleDialogClose"
    ><el-form ref="form" :model="params" :rules="rules" label-width="100px"><div class="title-sty">{{$t("addpage.addinfo.ym6er3")}}</div><el-form-item :label="$t('common.algorithmName')" prop="name"><el-input v-model="params.name"></el-input></el-form-item><el-form-item :label="$t('common.algorithmNameEn')" prop="englishName"><el-input v-model="params.englishName"></el-input></el-form-item><el-form-item
          :label="$t('modeltesting.addalgorithm.vr50ts')"
          prop="nameEn"
        ><el-input
            v-model="params.nameEn"
            @blur="blurCheck"
            :class="[isExist?'color-red':'']"
          ></el-input><div
            v-if="isExist"
            style="color: #dd383e; font-size: 12px; position: absolute; top: 30px;"
          >{{$t("modeltesting.addalgorithm.8qqb02")}}</div></el-form-item><!--<el-form-item label="Hardware Platform"prop="platform"> <el-select v-model="params.platform"placeholder="Please select Hardware Platform"style="width: 100%"> <el-option v-for="(item, index) in platformList":key="index":label="item.name":value="item.id"></el-option> </el-select> </el-form-item>--><el-form-item
          :label="$t('modeltesting.addalgorithm.932uik')"
          prop="shareMode"
        ><el-select
            v-model="params.shareMode"
            :placeholder="$t('modeltesting.addalgorithm.h0em78')"
            style="width: 100%"
          ><el-option
              v-for="(item, index) in shareModeList"
              :key="index"
              :label="item.name"
              :value="item.id"
            ></el-option></el-select><span class="form-tip">{{$t("addpage.addinfo.434huy")}}</span></el-form-item><el-form-item
          :label="$t('modeltesting.addalgorithm.gwljfz')"
          prop="extras"
        ><el-input v-model="params.extras"></el-input><span class="form-tip"
            >{{$t("addpage.addinfo.55348s")}}<el-popover placement="right" width="400" trigger="click"><p>{{$t("addpage.addinfo.y37j67")}}</p><p style="padding: 5px 0px">{{$t("addpage.addinfo.17t2vg")}}</p><p style="padding: 5px 0px">{{$t("addpage.addinfo.55r8f6")}}</p><p style="padding: 5px 0px">{{$t("addpage.addinfo.r7jd23")}}{"name":"min_detect_frame_num","label":"most small connect Continue Frame","type":"number","value":"1","element":"input"}]</p><el-link
                slot="reference"
                type="primary"
                style="font-size: 12px"
                >{{$t("addpage.addinfo.ue836v")}}</el-link
              ></el-popover></span></el-form-item><div class="title-sty">{{$t("addpage.addinfo.0abe4k")}}</div><el-form-item :label="$t('addpage.addinfo.5bne98')"><el-upload
            ref="upload"
            class="upload-demo"
            action=""
            :on-change="handleChange"
            :on-remove="handleRemove"
            :limit="1"
            :file-list="fileList"
            accept=".png"
            :auto-upload="false"
          ><el-button slot="trigger" size="small" type="primary">{{$t("button.clickToUpload")}}</el-button><div slot="tip" class="el-upload__tip">{{$t("addpage.addinfo.y416gd")}}</div></el-upload></el-form-item><el-form-item :label="$t('addpage.addinfo.gz3t44')"><el-input
            v-model="params.marks"
            type="textarea"
            :rows="3"
            maxlength="30"
            :placeholder="$t('addpage.addinfo.z3qo45')"
          ></el-input></el-form-item></el-form><span slot="footer" class="dialog-footer"><el-button @click="closed" v-if="!btnLoading">{{$t("button.cancelText", {text:""})}}</el-button><el-button type="primary" @click="saveData" :loading="btnLoading">{{$t("addpage.addinfo.21hmw1")}}</el-button></span></el-dialog></div></template>
<script>
import { checkNameEn } from "@/api/applicationMonitoring/algorithmManagement";
import { saveOrUpdate } from "@/api/applicationMonitoring/modelTesting";
export default {
  data() {
    return {
      dialogVisible: true,
      platformList: [
        {
          name: this.$t("modeltesting.addalgorithm.qqgf84"),
          id: "chaoxing",
        },
        {
          name: this.$t("modeltesting.addalgorithm.b78692"),
          id: "1684",
        },
        {
          name: this.$t("modeltesting.addalgorithm.rdv8md"),
          id: "1684x",
        },
        {
          name: this.$t("modeltesting.addalgorithm.946l5e"),
          id: "1688",
        },
      ],
      shareModeList: [
        {
          name: this.$t("modeltesting.addalgorithm.932uik"),
          id: 1,
        },
        {
          name: this.$t("modeltesting.addalgorithm.ebg483"),
          id: 0,
        },
      ],
      params: {
        name: "",
        nameEn: "",
        englishName: "",
        platform: "",
        imageFile: "",
        marks: "",
        shareMode: 0,
        extras: "",
      },
      rules: {
        name: [
          {
            required: true,
            message: $t("form.tip.inputAlgorithmName"),
            trigger: "blur",
          },
        ],
        englishName: [
          {
            required: true,
            message: $t("form.tip.inputAlgorithmEnName"),
            trigger: "blur",
          },
        ],
        nameEn: [
          {
            required: true,
            message: this.$t("addpage.addinfo.563pgs"),
            trigger: "blur",
          },
        ],
        // platform: [{ required: true, message: "请选择所属平台", trigger: "change" }],
        shareMode: [
          {
            required: true,
            message: this.$t("modeltesting.addalgorithm.h0em78"),
            trigger: "change",
          },
        ],
      },
      fileList: [],
      btnLoading: false,
      tipText: "",
      isExist: false,
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
      const isLimit = file.size / 1024 / 1024 < 1;
      if (!isLimit) {
        this.$message.error(this.$t("addpage.addinfo.3qhpe7"));
        this.$refs.upload.clearFiles(); // 清除前端显示的文件列表
      } else {
        let Arr = [];
        Arr.push(file);
        this.fileList = Arr;
        this.params.imageFile = file.raw;
      }
    },
    handleRemove(file, fileList) {
      this.params.imageFile = "";
      this.fileList = [];
    },
    // 算法英文名失去焦点时，判断此算法是否存在
    blurCheck() {
      if (!this.params.nameEn) {
        this.isExist = false;
        return;
      }
      let obj = {
        id: this.currentId,
        nameEn: this.params.nameEn,
      };
      checkNameEn(obj).then((res) => {
        this.isExist = res.data;
      });
    },
    // 导入
    saveData() {
      this.btnLoading = true;
      this.$refs.form.validate((valid) => {
        if (valid) {
          let formData = new FormData();
          formData.append("name", this.params.name);
          formData.append("nameEn", this.params.nameEn);
          formData.append("englishName", this.params.englishName);
          formData.append("platform", this.params.platform);
          formData.append("imageFile", this.params.imageFile);
          formData.append("marks", this.params.marks);
          formData.append("shareMode", this.params.shareMode);
          formData.append("extras", this.params.extras || "");
          saveOrUpdate(formData)
            .then((res) => {
              this.btnLoading = false;
              this.$message.success(this.$t("addpage.addinfo.r5n2ns"));
              this.$emit("closeAdd", true);
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
<style scoped lang="scss">
.title-sty {
  font-size: 13px;
  color: #1A0808;
  font-weight: bold;
  margin-bottom: 20px;
}
.form-tip {
  font-size: 12px;
  color: #1A0808;
}
</style>
