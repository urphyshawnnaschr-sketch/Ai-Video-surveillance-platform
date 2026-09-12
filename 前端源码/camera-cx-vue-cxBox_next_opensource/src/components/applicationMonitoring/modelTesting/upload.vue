<template>
  <div class="app-box">
    <div class="headline">
      <!-- <div class="icon"><i class="el-icon-magic-stick"></i></div> -->
      <div class="cont">{{ $t("modeltesting.upload.vpk1v7") }}</div>
    </div>
    <div class="flex-box">
      <div class="left">
        <p class="title">
          {{ $t("modeltesting.upload.8c2l3t")
          }}<span>{{ $t("modeltesting.upload.b8wbz2") }}</span>
        </p>
        <div
          class="display"
          id="objectId"
          :style="{ height: isDefault ? '' : '367px' }"
        >
          <!-- 视频 -->
          <VideoItem
            v-if="params.type == 'mp4' && params.mp4Url"
            :cameraUrl="params.mp4Url"
            :videoWith="videoWith"
            :index="1"
          ></VideoItem>
          <!-- 图片 -->
          <img
            v-if="params.type != 'mp4' && params.file"
            :src="$common.handleImgUrl(params.file)"
            style="width: 100%; max-height: 100%"
          />
        </div>
        <div class="tab-block">
          <div class="upload-block">
            <el-upload
              class="upload-style"
              action=""
              list-type="picture-card"
              :show-file-list="false"
              accept=".jpg,.jpeg,.png,.mp4"
              :http-request="handleUpload"
            >
              <i class="el-icon-plus"></i>
              <div class="tip">{{ $t("modeltesting.upload.r1ue24") }}</div>
            </el-upload>
            <div class="file-list">
              <!-- <transition name="el-fade-in"> -->
              <div
                class="file-item"
                v-for="(item, index) in fileList"
                :key="index"
                @click="upChange(item)"
              >
                <transition name="el-fade-in">
                  <el-image
                    :src="item.url"
                    fit="contain"
                    v-if="item.url"
                  ></el-image>
                </transition>
                <transition name="el-fade-in-linear">
                  <div class="progress-block" v-if="item.animation">
                    <el-progress
                      type="circle"
                      :percentage="item.percentage"
                    ></el-progress>
                  </div>
                </transition>
                <div class="del-sty">
                  <div class="el-icon-delete" @click.stop="delFun(index)"></div>
                </div>
                <div class="mp4" v-if="item.type == 'mp4' && item.url">
                  <img
                    src="@/assets/images/modelTesting/play-icon.png"
                    style="height: 42px; width: 42px"
                  />
                </div>
              </div>
              <!-- </transition> -->
            </div>
          </div>
          <div class="tab-box">
            <div
              class="file-item-de"
              v-for="(item, ind) in tabList"
              :key="ind"
              @click="imgChange(item)"
            >
              <img :src="$common.handleImgUrl(item.file)" style="width: 100%" />
              <div class="mp4" v-if="item.type == 'mp4'">
                <img
                  src="@/assets/images/modelTesting/play-icon.png"
                  style="height: 42px; width: 42px"
                />
              </div>
            </div>
          </div>
        </div>
        <div
          style="font-size: 12px; color: rgba(0, 0, 0, 0.4); line-height: 20px"
        >
          {{ $t("modeltesting.upload.2zx3n1") }}<br />{{
            $t("modeltesting.upload.1fwr84")
          }}
        </div>
      </div>
      <div class="right">
        <p class="title">
          {{ $t("modeltesting.upload.3u2566")
          }}<span>{{ $t("modeltesting.upload.br15wi") }}</span>
        </p>
        <div class="result-sty" :style="{ height: isDefault ? '' : '367px' }">
          <!-- 视频 -->
          <VideoItem
            v-if="videoUrl"
            :cameraUrl="videoUrl"
            :videoWith="videoWith"
            :index="2"
          ></VideoItem>
          <!-- 图片绘制区域以及上传视频第一帧绘制区域 -->
          <MarkDetail
            ref="markDetail"
            v-if="isDraw"
            :fileUrl="$common.handleImgUrl(params.file)"
            :dataList="dataList"
            @dataChange="dataChange"
          />
          <!-- 展示识别后的图片 -->
          <MarkResult
            v-if="isRes"
            :fileUrl="this.$common.handleImgUrl(params.file)"
            :dataList="boxList"
          />
          <!-- 识别时加载的样式 -->
          <div
            class="loading-sty"
            v-if="loading"
            v-loading="loading"
            :element-loading-text="$t('modeltesting.upload.fo7i5d')"
            element-loading-spinner="el-icon-loading"
            element-loading-background="rgba(3, 3, 3, 0.6)"
          ></div>
        </div>

        <!-- 区域坐标展示框 -->
        <div v-if="isDraw || isRes || !params.isDefault">
          <div class="region-flex">
            <div class="txt">{{ $t("modeltesting.upload.44w311") }}</div>
            <div class="btn" v-if="isIdentify" @click="refreshMarks">
              <span class="el-icon-refresh"></span>
              <span>{{
                $t("button.resetText", {
                  text: $t("addcamera.markdetail.1xx58c"),
                })
              }}</span>
            </div>
          </div>
          <div class="region-data">
            <div v-if="marksList.length > 0">
              <div v-for="(items, i) in marksList" :key="i">
                x：{{ items.x }} , y：{{ items.y }}
              </div>
            </div>
          </div>
        </div>
        <!-- 操作按钮 -->
        <div class="btn-all">
          <el-button
            type="primary"
            @click="saveFun"
            v-if="isDraw || isRes || !params.isDefault"
            :disabled="!isDraw"
            icon="el-icon-magic-stick"
            >{{ $t("modeltesting.imgtest.qg1239") }}</el-button
          >
          <el-button
            v-if="isDraw || isRes || !params.isDefault"
            @click="refreshParams"
            :disabled="isDraw"
            >{{ $t("button.resetText", { text: "" }) }}</el-button
          >
          <el-button
            v-if="videoUrl"
            type="primary"
            icon="el-icon-download"
            @click="downloadFun()"
            >{{ $t("modeltesting.upload.w7o2sx") }}</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { upload } from "@/api/common";
import {
  firstPicFromMp4,
  getTestAll,
  detectMp4,
  predict,
} from "@/api/applicationMonitoring/modelTesting";
import VideoItem from "./videoItem";
import MarkDetail from "@/components/applicationMonitoring/modelTesting/markDetail";
import MarkResult from "@/components/markResult";
export default {
  components: {
    VideoItem,
    MarkDetail,
    MarkResult,
  },
  data() {
    return {
      tabList: [],
      VUE_APP_API_BASE_URL,
      videoWith: 0,
      videoHeight: 0,
      params: {},
      videoUrl: "", //上传后的视频路径
      loading: false,
      isDraw: false,
      dataList: [],
      isIdentify: true, //开始识别之前可重新绘制区域
      marks: "",
      marksList: [],
      isRes: false,
      boxList: [],
      fileList: [],
      timer: null,
      defaultVideo: "", //计算后的默认视频
      isDefault: false,
    };
  },
  props: {
    nameEn: {
      type: String,
      default: "",
    },
    id: {
      type: String,
      default: "",
    },
  },
  created() {},
  mounted() {
    this.getTest();
  },
  methods: {
    // 获取默认的图片和视频
    getTest() {
      const element = document.getElementById("objectId");
      this.videoWith = element.offsetWidth;
      this.tabList = [];
      getTestAll({ suanfa: this.nameEn }).then((res) => {
        let arr = res.data;
        if (arr.length > 0) {
          arr.map((item, ind) => {
            if (item.indexOf("_out.mp4") != -1) {
              this.defaultVideo = item;
              this.isDefault = true;
            } else {
              if (item.indexOf(".mp4") != -1) {
                this.getImg(item, 1);
              } else {
                this.tabList.push({
                  file: item,
                  type: "image",
                  isDefault: true,
                });
              }
            }
          });
        }
      });
    },
    // 获取视频第一帧
    async getImg(fileUrl, type) {
      const res = await firstPicFromMp4({ file: fileUrl });
      if (type == 1) {
        this.tabList.unshift({
          file: res.data,
          mp4Url: fileUrl,
          type: "mp4",
          isDefault: true,
        });
        this.params = {
          file: res.data,
          mp4Url: fileUrl,
          type: "mp4",
          isDefault: true,
        };
        if (this.defaultVideo) {
          this.videoUrl = this.defaultVideo;
        } else {
          this.submit(1);
        }

        // this.submit();
      } else {
        this.resetFun();
        this.params = {};
        let item = this.fileList[0];
        // 插入图片
        item["url"] = this.$common.handleImgUrl(res.data);
        item["file"] = res.data;
        item["mp4Url"] = fileUrl;
        item.animation = false;
        setTimeout(() => {
          this.params = {
            file: res.data,
            mp4Url: fileUrl,
            type: "mp4",
            isDefault: false, //是不是默认视频
          };
        }, 200);

        this.isDraw = true;
      }
    },
    resetFun() {
      this.videoUrl = ""; //上传后的视频路径
      this.loading = false;
      this.isDraw = false;
      this.dataList = [];
      this.isIdentify = true; //开始识别之前可重新绘制区域
      this.marks = "";
      this.marksList = [];
      this.isRes = false;
      this.boxList = [];
    },
    // 默认图片视频切换
    imgChange(item) {
      if (this.loading) {
        this.$message.error(this.$t("modeltesting.upload.wm5vw5"));
        return;
      }
      this.resetFun();
      if (item.type == "mp4") {
        //如果是默认视频
        this.params = item;
        this.isDraw = false;
        if (this.defaultVideo) {
          this.videoUrl = this.defaultVideo;
        } else {
          this.submit(1);
        }
      } else {
        this.params = item;
        this.isDraw = true;
      }
    },
    // 上传图片视频切换
    upChange(item) {
      this.params = {};
      this.isRes = false;
      this.videoUrl = "";
      this.params = item;
      this.isDraw = true;
    },
    // 视频识别
    submit(type) {
      this.loading = true;
      let arr = [];
      arr.push(this.id);
      let obj = {
        file: this.params.mp4Url,
        algorithms: JSON.stringify(arr),
        marks: this.marks,
        imgHeight: this.params.isDefault
          ? ""
          : this.$refs.markDetail.$refs.draw_img.offsetHeight,
      };
      detectMp4(obj)
        .then((res) => {
          if (res.code == 0) {
            this.videoUrl = res.data.file;
            this.loading = false;
            this.isDraw = false;
            this.isRes = false;
            this.isIdentify = false;
            if (type == 1) {
              this.defaultVideo = res.data.file;
              this.isDefault = true;
            }
          }
        })
        .catch((res) => {
          this.loading = false;
        });
    },
    // 导出视频
    downloadFun() {
      const url =
        VUE_APP_API_BASE_URL + "/model/test/download?file=" + this.videoUrl;
      const link = document.createElement("a");
      link.href = url;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    // 编辑禁区
    dataChange(val) {
      const arr = [];
      val.forEach((item) => {
        arr.push(item.pointList);
      });
      // this.$set(this.params, 'marks', JSON.stringify(arr))
      this.marks = JSON.stringify(arr);
      this.marksList = arr[0];
    },
    // 重新绘制区域
    refreshMarks() {
      this.dataList = [];
      this.marks = "";
      this.marksList = [];
    },
    // 点击开始识别
    saveFun() {
      if (this.loading) {
        this.$message.error(this.$t("modeltesting.upload.wm5vw5"));
        return;
      }
      if (this.params.type == "image") {
        //图片
        this.submitImg();
      } else {
        //视频
        this.submit();
      }
    },
    // 图片识别
    submitImg() {
      this.loading = true;
      let height = this.$refs.markDetail.$refs.draw_img.offsetHeight;
      let algorithmsArr = [];
      algorithmsArr.push(this.id);
      let obj = {
        imgHeight: height,
        algorithmsArr: algorithmsArr,
        algorithms: JSON.stringify(algorithmsArr),
        file: this.params.file,
        marks: this.marks,
      };
      predict(obj)
        .then((res) => {
          let arr = JSON.parse(res.data.json);
          let newArr = [];
          this.isDraw = false; //隐藏绘制区域
          this.loading = false;
          this.isRes = true; //展示识别结果
          this.isIdentify = false; //隐藏重置区域
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
        })
        .catch((res) => {
          this.loading = false;
        });
    },
    // 重置
    refreshParams() {
      this.boxList = [];
      this.marks = "";
      this.marksList = [];
      this.isDraw = true;
      this.isRes = false;
      this.isIdentify = true;
      this.videoUrl = "";
    },
    // 执行上传
    async handleUpload(data) {
      if (this.loading) {
        this.$message.error(this.$t("modeltesting.upload.wm5vw5"));
        return;
      }
      const file = data.file;
      // 图片
      if (file.type.includes("image")) {
        // 开始动画
        this.fileList.unshift({
          animation: true,
          percentage: 0,
          type: "image",
        });
        let item = this.fileList[0];
        this.handleAnimation(item);
        // setTimeout(async function () {
        // 渲染上传文件
        let formData = new FormData();
        formData.append("name", file.name);
        formData.append("type", file.type);
        formData.append("lastModifiedDate", file.lastModifiedDate);
        formData.append("size", file.size);
        formData.append("file", file);
        const res = await upload(formData);

        // 上传完成停止计时器
        clearInterval(this.timer);
        item.percentage = 100;
        this.isDefault = true;
        setTimeout(
          function () {
            // 插入图片
            item["url"] = this.$common.handleImgUrl(res.data);
            item["file"] = res.data;
            item.animation = false;
            this.resetFun();
            this.params = {
              file: res.data,
              type: "image",
              isDefault: false, //是不是默认图片
            };
            this.isDraw = true;
          }.bind(this),
          500
        );
      } else if (file.type.includes("mp4")) {
        this.handleUploadMP4(file);
      }
    },
    // 上传视频
    async handleUploadMP4(file) {
      const isLimit = file.size / 1024 / 1024 < 30;
      if (!isLimit) {
        this.$message.error(this.$t("modeltesting.upload.ob8270"));
        return;
      }
      // 开始动画
      this.fileList.unshift({
        animation: true,
        percentage: 0,
        type: "mp4",
      });
      let item = this.fileList[0];
      this.handleAnimation(item);
      let formData = new FormData();
      formData.append("file", file);
      const res = await upload(formData);
      this.isDefault = true;
      this.getImg(res.data, 2);
      // 上传完成停止计时器
      clearInterval(this.timer);
      item.percentage = 100;
    },
    // 加载动画
    handleAnimation(item) {
      this.timer = setInterval(() => {
        if (item.percentage < 80) {
          item.percentage += 1;
        } else {
          clearInterval(this.timer);
        }
      }, 500);
    },
    // 删除当前上传的数据
    delFun(index) {
      this.fileList.splice(index, 1);
      this.params = {};
      if (this.fileList.length > 0) {
        setTimeout(() => {
          this.params = this.fileList[0];
        }, 100);
      } else {
        setTimeout(() => {
          this.params = this.tabList[0];
        }, 100);
      }
    },
  },
};
</script>
<style scoped lang="scss">
.app-box {
  // padding: 32px;
  // border-radius: 8px;
  // border: 1px solid #E4E7ED;
  // background: #F5F7FA;
  // margin: 16px 0;

  .headline {
    margin: 30px 0px;
    .cont {
      color: #202b3d;
      font-size: 18px;
      font-style: normal;
      font-weight: bold;
    }
  }

  .flex-box {
    display: flex;
    justify-content: space-between;
    background: #fff;
    padding: 23px 29px;
    > div {
      width: 47%;
      // ;
      // background: #FFF;
      // padding: 16px;
    }

    .title {
      color: #202b3d;
      font-weight: bold;
      margin: 0;
      padding-bottom: 20px;
      font-size: 13px;
      span {
        display: inline-block;
        margin-left: 8px;
        color: #999999;
        font-size: 13px;
        font-weight: 400;
      }
    }
    .display {
      width: 100%;
      // height: 367px;
      border: 1px solid #f3f3f3;
      margin-bottom: 20px;
    }
    .tab-block {
      display: flex;
      overflow-x: scroll;
      margin-bottom: 16px;
      .upload-block {
        display: flex;
        .upload-style {
          width: 152px;
          height: 152px;
          :deep(.el-upload--picture-card) {
            width: 152px;
            height: 152px;
            background-color: #eee;
            border-radius: 0;
            display: flex;
            flex-wrap: wrap;
            align-content: center;
            justify-content: center;
          }
          :deep(.el-upload--picture-card i) {
            font-size: 24px;
            color: rgba(0, 0, 0, 0.9);
          }
          .tip {
            width: 100%;
            color: rgba(0, 0, 0, 0.4);
            text-align: center;
            font-size: 12px;
            font-weight: 400;
            line-height: normal;
            margin-top: 10px;
          }
        }
      }
      .file-list {
        display: flex;
        .file-item {
          width: 152px;
          height: 152px;
          border-radius: 2px;
          border: 1px #f3f3f3 solid;
          display: flex;
          align-content: center;
          justify-content: center;
          position: relative;
          margin-left: 16px;
        }
        .progress-block {
          width: 152px;
          height: 152px;
          position: absolute;
          top: 0;
          left: 0;
          :deep(.el-progress--circle) {
            background-color: #ffffff;
            width: 152px;
            height: 152px;
            display: flex;
            justify-content: center;
            align-items: center;
          }
        }
        .file-item:hover {
          .mp4 {
            display: none;
          }
          .del-sty {
            width: 100%;
            height: 100%;
            position: absolute;
            background: rgba(0, 0, 0, 0.4);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 22px;
          }
        }
        .del-sty {
          display: none;
        }
        .mp4 {
          width: 100%;
          height: 100%;
          position: absolute;
          background: rgba(0, 0, 0, 0.4);
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }

      .tab-box {
        display: flex;
        .file-item-de {
          display: flex; /* Container is layout */
          align-items: center; /* Center horizontally */
          width: 152px;
          height: 152px;
          border: 1px solid #f3f3f3;
          margin-left: 16px;
          position: relative;
          .mp4 {
            width: 100%;
            height: 100%;
            position: absolute;
            background: rgba(0, 0, 0, 0.4);
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
      }
    }
    .result-sty {
      width: 100%;
      // height: 367px;
      border: 1px solid #f3f3f3;
      margin-bottom: 20px;
      position: relative;
      // overflow-y: auto;
      .loading-sty {
        width: 100%;
        height: 100%;
        background: rgba(3, 3, 3, 0.7);
        position: absolute;
        top: 0;
      }
    }
    .region-flex {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      margin-bottom: 15px;
      .btn {
        background: #73767a;
        padding: 5px 12px;
        color: #fff;
        font-size: 12px;
        border-radius: 6px;
        cursor: pointer;
      }
    }
    .region-data {
      width: 100%;
      height: 124px;

      border: 1px solid #dcdcdc;
      padding: 5px 8px;
      overflow-y: auto;
      color: rgba(0, 0, 0, 0.4);
      font-size: 13px;
    }
    .btn-all {
      margin-top: 32px;
      :deep(.el-button--danger) {
        color: #fff;
        background: #ff4949;
        border-color: #ff4949;
      }
      :deep(.el-button--danger:hover) {
        color: #fff !important;
        background: #f78080 !important;
        border-color: #f78080 !important;
      }
    }
  }
}

:deep(.el-button--primary) {
  background: #efa04c !important;
  border: none;
}
</style>
