<template>
  <el-dialog
    :close-on-click-modal="false"
    width="1200px"
    top="5vh"
    :title="$t('modeltesting.imgtest.xynrr6')"
    :visible.sync="imgVisible"
    @closed="closed"
    append-to-body
  >
    <div class="imgTest-cont">
      <el-card class="left-card">
        <el-form label-width="80px">
          <el-form-item :label="$t('components.detail.26y57g')">
            <div id="drop-area" class="drop-area">
              <MarkDetail
                v-if="params.file"
                :fileUrl="this.$common.handleImgUrl(params.file)"
                :dataList="dataList"
                @dataChange="dataChange"
              />
              <div v-else>{{$t('modeltesting.imgtest.68ivil')}}</div>
            </div>
            <div class="imgBox" v-if="imgList.length > 0">
              <div class="selShow">
                <div v-for="url in imgList">
                  <img
                    :src="$common.handleImgUrl(url)"
                    :alt="$t('components.detail.26y57g')"
                    @click="imgChange(url)"
                  />
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item :label="$t('modeltesting.imgtest.25pg1s')">
            <el-input
              :placeholder="$t('form.tip.inputContent')"
              v-model="params.marks"
              readonly
            >
              <el-button type="primary" slot="append" @click="refreshMarks">{{
                $t("button.resetText", { text: "" })
              }}</el-button>
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('common.camera')">
            <el-input
              :placeholder="$t('modeltesting.imgtest.ipq2f8')"
              v-model="params.cameraId"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <div style="display: flex">
              <el-button type="primary" @click="submit">{{$t('modeltesting.imgtest.qg1239')}}</el-button>
              <el-button @click="refreshParams">{{
                $t("button.resetText", { text: "" })
              }}</el-button>
              <el-upload
                action=""
                :http-request="handleUploadImg"
                :show-file-list="false"
                accept=".jpg,.jpeg,.png,.gif"
              >
                <el-button type="primary" style="margin-left: 10px"
                  >{{$t('modeltesting.imgtest.dsk58q')}}</el-button
                >
              </el-upload>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
      <el-card class="right-card">
        <div slot="header">
          <span>{{$t('modeltesting.imgtest.58ogz8')}}</span>
        </div>
        <div>
          <MarkResult
            v-if="params.file"
            :fileUrl="this.$common.handleImgUrl(params.file)"
            :dataList="boxList"
          />
          <!-- <div
                    class="text"
                    v-html="
                        testResult.json
                        ?.split('\n')
                        .join('<br/>')
                        .split('\t')
                        .join('&nbsp;&nbsp;&nbsp;&nbsp;')
                    "
                    ></div> -->
        </div>
      </el-card>
    </div>
  </el-dialog>
</template>
<script>
import { getTestImg, predict } from "@/api/applicationMonitoring/modelTesting";
import { upload } from "@/api/common";
import MarkDetail from "@/components/applicationMonitoring/modelTesting/markDetail";
import MarkResult from "@/components/markResult";
export default {
  components: {
    MarkDetail,
    MarkResult,
  },
  data() {
    return {
      imgVisible: true,
      params: {
        file: "",
        marks: "",
        cameraId: "",
      },
      boxList: [],
      dataList: [],
      imgList: [],
      testResult: {},
      dropActive: false,
      VUE_APP_API_BASE_URL,
    };
  },
  props: {
    detailObj: {
      type: Object,
      default: {},
    },
  },
  created() {
    this.getTestImg();
  },
  mounted() {
    this.$nextTick(function () {
      let dropArea = document.getElementById("drop-area");
      dropArea.addEventListener("drop", this.dropEvent, false);
      dropArea.addEventListener("dragleave", (e) => {
        e.stopPropagation();
        e.preventDefault();
        this.dropActive = false;
      });
      dropArea.addEventListener("dragenter", (e) => {
        e.stopPropagation();
        e.preventDefault();
        this.dropActive = true;
      });
      dropArea.addEventListener("dragover", (e) => {
        e.stopPropagation();
        e.preventDefault();
        this.dropActive = true;
      });
    });
  },
  methods: {
    // 获取测试图片
    getTestImg() {
      getTestImg({ suanfa: this.detailObj.nameEn }).then((res) => {
        this.$set(this.params, "file", res.data[0]);
        this.imgList = [...res.data];
      });
    },
    // 关闭弹窗
    closed() {
      this.$emit("close");
    },
    // 重置
    refreshParams() {
      this.params = {
        file: "",
        marks: "",
        cameraId: "",
      };
      this.testResult = {};
      this.boxList = [];
      this.imgList = [];
      this.dataList = [];
    },
    // 编辑禁区
    dataChange(val) {
      const arr = [];
      val.forEach((item) => {
        arr.push(item.pointList);
      });
      this.$set(this.params, "marks", JSON.stringify(arr));
    },
    // 重置禁区
    refreshMarks() {
      this.dataList = [];
      this.params.marks = "";
    },
    dropEvent(e) {
      this.dropActive = false;
      e.stopPropagation();
      e.preventDefault();
      this.handleUploadImg(e.dataTransfer.files);
    },
    // 切换图片
    imgChange(url) {
      this.params = {
        file: "",
        marks: "",
        cameraId: "",
      };
      this.testResult = {};
      this.boxList = [];
      this.dataList = [];
      this.$set(this.params, "file", url);
    },
    // 上传
    async handleUploadImg(files) {
      this.refreshParams();
      const file = files.file ? files.file : files[0];
      // 渲染上传文件
      let formData = new FormData();
      formData.append("name", file.name);
      formData.append("type", file.type);
      formData.append("lastModifiedDate", file.lastModifiedDate);
      formData.append("size", file.size);
      formData.append("file", file);
      this.params.file = "";
      const data = await upload(formData);
      this.params.file = data.data;
      let img = new Image();
      img.src = this.$common.handleImgUrl(this.params.file);
      // img.onload = () => {
      //   this.params.imgHeight = img.height;
      // };
    },
    // 识别
    async submit() {
      let height = document.getElementById("drop-area").offsetHeight;
      let algorithmsArr = [];
      algorithmsArr.push(this.detailObj.id);
      let obj = {
        imgHeight: height,
        algorithmsArr: algorithmsArr,
        algorithms: JSON.stringify(algorithmsArr),
        ...this.params,
      };
      const data = await predict(obj);
      this.testResult = data.data;
      let arr = JSON.parse(data.data.json);
      let newArr = [];
      if (arr.length > 0) {
        arr.map((item, i) => {
          if (item.data.length > 0) {
            item.data.map((items, ind) => {
              items.confidence =
                items.confidence == 1 ? "1.0" : items.confidence;
              newArr.push(items);
            });
          }
        });
        this.boxList = newArr;
      }
    },
  },
};
</script>
<style scoped lang="scss">
.imgTest-cont {
  display: flex;
  justify-content: space-between;
  .left-card {
    width: 60%;
  }
  .right-card {
    width: 39%;
  }
  .drop-area {
    width: 550px;
    min-height: 360px;
    text-align: center;
    background: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    .upload {
      width: 100%;
      height: 100%;
      position: absolute;
      opacity: 0;
    }
    img {
      max-width: 100%;
      display: block;
    }
  }
  .imgBox {
    .selShow {
      width: 550px;
      display: flex;
      // justify-content: space-around;
      background-color: #eaeaea;
      padding: 8px;
      div {
        height: 46px;
        margin-right: 10px;
        cursor: pointer;
        img {
          height: 46px;
        }
      }
      div:last-child {
        margin-right: 0;
      }
    }
  }
}
</style>
