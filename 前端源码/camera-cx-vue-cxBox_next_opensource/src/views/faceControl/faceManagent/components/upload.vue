<template>
  <el-dialog
    :title="$t('faceControl.faceRecognition.batchUpload')"
    :visible.sync="visible"
    width="40%"
    @update:visible="(val) => $emit('update:visible', val)"
    append-to-body
  >
    <div style="overflow: hidden">
      <el-form label-position="right" label-width="100px">
        <el-form-item
          :label="$t('faceControl.faceRecognition.caBelongingGroup')"
        >
          <el-select
            class="pr10"
            style="width: 291px"
            v-model="formatData.faceGroupId"
            :placeholder="$t('common.chooseText')"
          >
            <el-option
              v-for="item in tableData"
              :key="item.id"
              v-if="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('faceControl.faceRecognition.uploadFiles')">
          <el-upload
            :action="VUE_APP_API_BASE_URL + '/face/user/upload'"
            :data="{
              faceGroupId: this.formatData.faceGroupId,
            }"
            :headers="{
              'X-Token': token,
            }"
            :before-upload="beforeupload"
            accept=".zip"
            :on-success="onSuccess"
            :limit="1"
            :disabled="isDisabled"
          >
            <el-button size="small" type="primary">{{
              $t("button.clickToUpload")
            }}</el-button>
            <div slot="tip" class="el-upload__tip">
              {{ $t("faceControl.faceRecognition.onlyUploadFile") }}
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item :label="$t('faceControl.faceRecognition.zipExample')">
          <div style="font-size: 12px; color: #000">
            <div style="line-height: 22px">
              {{ $t("faceControl.faceRecognition.folderFace", { count: 1 })
              }}<br />
              + {{ $t("faceControl.faceRecognition.picture", { count: 1 }) }}<br />
              + {{ $t("faceControl.faceRecognition.picture", { count: 2 }) }}<br />
            </div>
            <div style="line-height: 22px; margin-top: 15px">
              {{ $t("faceControl.faceRecognition.folderFace", { count: 2 })
              }}<br />
              + {{ $t("faceControl.faceRecognition.picture", { count: 1 })
              }}<br />
              + {{ $t("faceControl.faceRecognition.picture", { count: 2 }) }}
            </div>
            <div style="line-height: 22px; margin-top: 15px">
              {{ $t("faceControl.faceRecognition.folderFace", { count: 3 })
              }}<br />
              + {{ $t("faceControl.faceRecognition.picture", { count: 1 })
              }}<br />
              + {{ $t("faceControl.faceRecognition.picture", { count: 2 })
              }}<br />
              + {{ $t("faceControl.faceRecognition.picture", { count: 3 }) }}
            </div>
            <div style="line-height: 22px; margin-top: 15px">
              ...{{ $t("faceControl.faceRecognition.moreFace") }}
            </div>
            <div style="color: #1A0808; line-height: 22px; margin-top: 15px">
              {{ $t("faceControl.faceRecognition.folderDesc") }}
            </div>
          </div>
        </el-form-item>
      </el-form>

      <!-- <span slot="footer" class="footer">
        <el-button size="mini" :disabled="disabled" type="primary" @click="save();closeDialog();">Confirm</el-button>
        <el-button size="mini"  @click="closeDialog">{{ $t("button.cancelText", { text: " " }) }}</el-button>
      </span> -->
    </div>
  </el-dialog>
</template>
<script>
import { listPageDGroup } from "../api";
import Cookies from "js-cookie";
export default {
  components: {},
  props: {
    visible: Boolean,
    currentItme: Object,
    currentState: String,
  },
  data() {
    return {
      formatData: {
        faceGroupId: "",
      },
      tableData: [],
      VUE_APP_API_BASE_URL,
      token: Cookies.get("X-Token"),
      isDisabled: false,
    };
  },
  computed: {},
  created() {
    this.int();
  },
  methods: {
    int() {
      this.getTable();
    },

    async getTable() {
      const { data, count } = await listPageDGroup();
      this.tableData = data;
    },
    closeDialog() {
      this.$emit("update:visible", false);
      this.$emit("close", false);
    },
    beforeupload(file) {
      const isLt2M = file.size / 1024 / 1024 < 100;
      const faceGroupId = this.formatData.faceGroupId;

      if (!faceGroupId) {
        this.$message.error($t("form.tip.chooseBelongingGroupUpload") + "!");
        return false;
      }
      if (!isLt2M) {
        this.$message.error($t("form.verify.cannotExceed") + "!");
        return false;
      }
      this.isDisabled = true;
    },
    onSuccess(data) {
      if (data.code == 0) {
        this.isDisabled = false;
        this.$message.success($t("faceControl.faceRecognition.importSuccess"));
        this.closeDialog();
      } else {
        this.isDisabled = false;
        this.$message.error(data.msg);
      }
    },
  },
};
</script>
<style scoped>
.bg {
  background: white;
}
.wh {
  width: 100%;
  height: 100%;
}
.footer {
  float: right;
}
.upload-demo {
  width: 290px;
}
::v-deep .el-form-item {
  margin-bottom: 18px !important;
}
</style>
