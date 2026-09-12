<template>
  <el-dialog
    :title="title + $t('common.face')"
    :visible.sync="visible"
    width="50%"
    @update:visible="(val) => $emit('update:visible', val)"
    append-to-body
  >
    <div style="overflow: hidden">
      <el-form
        label-position="right"
        :model="formatData"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
      >
        <el-form-item :label="$t('common.name')" prop="name">
          <el-input :disabled="disabled" v-model="formatData.name"></el-input>
        </el-form-item>
        <el-form-item :label="$t('login.phoneNumber')" prop="tel">
          <el-input :disabled="disabled" v-model="formatData.tel"></el-input>
        </el-form-item>
        <el-form-item :label="$t('common.remark')" prop="remark">
          <el-input :disabled="disabled" v-model="formatData.remark"></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('faceControl.faceRecognition.belongingGroup')"
          prop="groupId"
        >
          <!-- <el-input :disabled="disabled" v-model="formatData.groupId"></el-input> -->
          <el-select
            class="pr10"
            style="width: 291px"
            :disabled="disabled"
            v-model="formatData.groupId"
            :placeholder="$t('common.chooseText')"
          >
            <el-option
              v-for="item in graps"
              :key="item.id"
              v-if="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('faceControl.faceRecognition.picture', { count: '' })"
          prop="nameEn"
        >
          <!-- <input type="file" name="" id="" @change="httpRequest">  -->
          <el-upload
            action="#"
            ref="pictureUpload"
            :disabled="formatData.files.length + fileList.length >= 3"
            :class="
              formatData.files.length + fileList.length >= 3 ? 'hide' : ''
            "
            accept="image/jpeg,image/jpg,image/png"
            :before-upload="beforeUpload"
            list-type="picture-card"
            :file-list="fileList"
            :limit="3"
            :http-request="httpRequest"
          >
            <i slot="default" class="el-icon-plus"></i>

            <div slot="file" slot-scope="{ file }">
              <img
                class="el-upload-list__item-thumbnail"
                :src="file.url"
                alt=""
              />
              <span class="el-upload-list__item-actions">
                <!-- <span
                  class="el-upload-list__item-preview"
                  @click="handlePictureCardPreview(file)"
                >
                  <i class="el-icon-zoom-in"></i>
                </span> -->

                <span
                  v-if="!disabled"
                  class="el-upload-list__item-delete"
                  @click="handleRemove(file)"
                >
                  <i class="el-icon-delete"></i>
                </span>
              </span>
            </div>
          </el-upload>
          <div>{{ $t("faceControl.faceRecognition.pictureDesc") }}</div>
          <div>
            <el-popover
              placement="top-start"
              :title="$t('faceControl.faceRecognition.example')"
              width="400"
              trigger="hover"
              content=""
            >
              <div style="font-size: 12px; color: rgba(0, 0, 0, 0.4)">
                {{ $t("faceControl.faceRecognition.faceRequireOne") }}
                <br />
                {{ $t("faceControl.faceRecognition.faceRequireTwo") }}
              </div>
              <div>
                <img width="100%" src="@/assets/shili.jpg" alt="" />
              </div>
              <el-button
                slot="reference"
                type="text"
                @click="dialogVisible = true"
                >{{ $t("faceControl.faceRecognition.viewExamples") }}</el-button
              >
            </el-popover>
          </div>
          <!-- <div class="iptcontent">
            <el-button
              type="primary"
              :disabled="disabled"
              size="mini"
              @click="handleAddImg"
              >Upload Image</el-button
            >
            <input
              id="upload"
              ref="fileInput"
              class="img-input"
              type="file"
              :multiple="true"
              @change="handleUploadFile"
            />
          </div> -->

          <!-- <div>
            <div v-if="formatData.files" class="flex">
              <div v-for="(item, index) in formatData.files" :key="index">
                <el-image
                  style="width: 150px; height: 100px"
                  :src="getImageUrl(item)"
                  fit="cover"
                >
                </el-image>
              </div>
            </div>
            <div v-else>
              <el-image
                v-for="(item, index) in formatData.faceImages"
                :key="index"
                style="width: 150px; height: 100px"
                :src="
                  $common.handlePublicUrl(
                    `/face/image/avatar?userId=${item.userId}`
                  )
                "
                fit="cover"
              >
              </el-image>
            </div>
          </div> -->
          <!-- <input
            id="upload"
            ref="fileInput"
            class="img-input"
            type="file"
            :multiple="true"
            @change="handleUploadFile"
          /> -->
        </el-form-item>
      </el-form>
      <span slot="footer" class="footer">
        <el-button
          size="mini"
          :disabled="disabled"
          type="primary"
          :loading="addLoading"
          @click="save()"
          >{{ $t("button.sureText", { text: " " }) }}</el-button
        >
        <el-button size="mini" @click="closeDialog">{{
          $t("button.cancelText", { text: " " })
        }}</el-button>
      </span>
    </div>
  </el-dialog>
</template>
<script>
import Cookies from "js-cookie";
import { detail, save, listPageDGroup } from "../api";
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
        files: [],
        remark: "",
        name: "",
        tel: "",
        groupId: "",
      },
      rules: {
        name: [
          { required: true, message: $t("common.inputText"), trigger: "blur" },
        ],
        tel: [
          { required: true, message: $t("common.inputText"), trigger: "blur" },
        ],
        groupId: [
          { required: true, message: $t("common.chooseText"), trigger: "blur" },
        ],
      },
      graps: [],
      dialogVisible: false,
      fileList: [],
      addLoading: false,
    };
  },
  computed: {
    title() {
      if (this.currentState == "see") {
        return $t("button.viewText", { text: "" });
      } else if (this.currentState == "add") {
        return $t("common.add", { text: "" });
      } else {
        return $t("common.edit", { text: "" });
      }
    },
    disabled() {
      if (this.currentState == "see") {
        return true;
      } else {
        return false;
      }
    },
  },
  created() {
    this.int();
  },
  methods: {
    int() {
      this.getTable();
      this.getDetail();
    },

    async getTable() {
      const { data, count } = await listPageDGroup();
      this.graps = data;
    },
    async getDetail() {
      if (this.currentItme.id) {
        const { data, count } = await detail({ id: this.currentItme.id });
        this.formatData = data;
        //fileList
        // this.formatData.files = [{
        //   createdAt :  1705399293000,
        //   deleted :  0,
        //   id :  "1747197386253451266",
        //   url :  "http://39.164.53.248:33021/face/image/avatar?userId=1747197386073096193",
        //   isAvatar :  0,
        //   userId :  "1747197386073096193"
        // }]
        this.formatData.files = [];
        this.fileList = data.faceImages.map((item) => {
          const token = Cookies.get('X-Token');
          let imageUrl = `${VUE_APP_API_BASE_URL}/face/image/getImage?imageId=${item.id}&X-Token=${token}`;


          return {
            ...item,
            url: imageUrl,
          };
        });

        // let obj = {
        //   createdAt :  1705399293000,
        //   deleted :  0,
        //   id :  "1747197386253451266",
        //   url :  "http://39.164.53.248:33021/face/image/avatar?userId=1747197386073096193",
        //   isAvatar :  0,
        //   userId :  "1747197386073096193"
        // }
      }
    },
    closeDialog() {
      this.$emit("update:visible", false);
      this.$emit("close", false);
    },

    validatePhoneNumber(phone) {
      // 定义手机号格式的正则表达式
      var regExp = /^1[3456789]\d{9}$/;

      if (regExp.test(phone)) {
        return true; // 符合手机号格式要求
      } else {
        return false; // 不符合手机号格式要求
      }
    },
    async save() {
      if (!this.validatePhoneNumber(this.formatData.tel)) {
        this.$message.error($t("form.verify.phoneError"));
        return;
      }
      if (!this.formatData.groupId) {
        this.$message.error($t("form.tip.chooseGrouping"));
        return;
      }
      this.addLoading = true;
      const fd = new FormData();
      fd.append("name", this.formatData.name);
      fd.append("tel", this.formatData.tel);
      fd.append("remark", this.formatData.remark);
      fd.append("groupId", this.formatData.groupId);

      if (this.formatData.id) {
        fd.append("id", this.formatData.id);
      }

      // if(this.formatData.files.length >= 3){
      //   fd.append("filesStr", '');
      // } else {
      //   console.log(this.fileList,"raw")

      //   // fd.append("filesStr", this.fileList.map(item=>{
      //   //   return item.imgUrl
      //   // }));

      // }

      this.fileList.forEach((item, index) => {
        if (item) {
          fd.append("filesStr", item.imgUrl);
        }
      });

      if (this.formatData.files) {
        this.formatData.files.forEach((item, index) => {
          if (item) {
            fd.append("files", item);
          }
        });
      }
      //fd.append('files',this.formatData.files)

      //console.log(this.formatData);
      const { code, data } = await save(fd);
      if (code == 0) {
        this.$message.success(
          $t("button.addText", { text: $t("common.success") })
        );
        this.addLoading = false;
        this.closeDialog();
      }
    },
    beforeUpload(file) {
      const isLt = file.size / 1024 / 1024 <= 2;
      if (!isLt) {
        this.$message.error($t('faceControl.faceRecognition.uploadFileLimit'));
        return false;
      }
    },
    changes(val) {
      this.formatData.nameEn = val.replace(/[^a-zA-Z]/g, "");
    },
    httpRequest() {
      console.log(this.$refs.pictureUpload.uploadFiles);

      this.formatData.files = this.$refs.pictureUpload.uploadFiles
        .filter((item) => item.raw)
        .map((item) => {
          return item.raw;
        });
      this.$forceUpdate();

      console.log(this.formatData.files);
    },
    handleRemove(file) {
      console.log(file);
      if (file.raw) {
        const uploadFiles = this.$refs.pictureUpload.uploadFiles;
        console.log(uploadFiles);
        if (uploadFiles.length) {
          for (let i = 0; i < uploadFiles.length; i++) {
            if (uploadFiles[i]["url"] === file.url) {
              uploadFiles.splice(i, 1);
            }
          }
        }
        //this.formatData.files = this.formatData.files.filter(item=> item && item.id != file.raw.id )
        this.httpRequest();
      } else {
        this.fileList = this.fileList.filter((item) => item.id != file.id);
      }
      console.log(this.fileList, "files11");
      console.log(this.formatData.files, "files11");

      this.$forceUpdate();
    },
    handleUploadFile(value) {
      const files = value.target.files || {};
      //const formData = new FormData();
      // Array.from(files).forEach((item: any) => {
      //   formData.append('files', item)
      // })
      //调用后端接口，传入文件参数
      //const { data , code } = await API.fetchImageURl(formData)
      // if(code == 200){
      //   //TODO
      // }
      //console.log(Object.values(files))
      this.formatData.files = Object.values(files);
      console.log(this.fileList, "files22");
      console.log(this.formatData.files, "files22");
      this.$forceUpdate();
    },
    handleAddImg() {
      const input = document.querySelector("#upload");
      input.click();
    },
    getImageUrl(item) {
      return URL.createObjectURL(item);
    },
  },
};
</script>
<style scoped lang="scss">
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
.iptcontent {
  position: relative;
  .img-input {
    opacity: 0;
  }
}
::v-deep .el-form-item {
  margin-bottom: 18px !important;
}
.hide {
  ::v-deep {
    .el-upload--picture-card {
      display: none;
    }

    .el-upload-list__item {
      border: 0;
      border-radius: 0;
      margin: 0 30px 0 0;
    }
  }
}
</style>
