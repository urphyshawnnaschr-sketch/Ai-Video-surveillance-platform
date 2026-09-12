<template>
  <div>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane
        :label="$t('faceControl.faceRecognition.faceContrast')"
        name="first"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="home">
              <div
                class="cropper"
                :style="{
                  width: `${imgObj.width}px`,
                  height: `${imgObj.height}px`,
                }"
              >
                <vueCropper
                  ref="cropper"
                  :img="option.img"
                  :outputSize="option.outputSize"
                  :outputType="option.outputType"
                  :canScale="option.canScale"
                  :autoCrop="option.autoCrop"
                  :autoCropWidth="option.autoCropWidth"
                  :autoCropHeight="option.autoCropHeight"
                  :canMoveBox="option.canMoveBox"
                  :canMove="option.canMove"
                  :centerBox="option.centerBox"
                  :info="option.info"
                  :fixedBox="option.fixedBox"
                  @realTime="realTime"
                ></vueCropper>
              </div>
              <!-- <img :src='previewImg'  alt="" class='previewImg' ref="img">
            <el-button type='primary' @click='handleClick'>Button</el-button>
            <img :src="resImg" alt="" v-if="resImg" class='previewImg'> -->
            </div>
          </el-col>
          <el-col :span="12">
            <div class="home">
              <div
                class="cropper"
                :style="{
                  width: `${imgObj.width}px`,
                  height: `${imgObj.height}px`,
                }"
              >
                <vueCropper
                  ref="cropper"
                  :img="option.img"
                  :outputSize="option.outputSize"
                  :outputType="option.outputType"
                  :canScale="option.canScale"
                  :autoCrop="option.autoCrop"
                  :autoCropWidth="option.autoCropWidth"
                  :autoCropHeight="option.autoCropHeight"
                  :canMoveBox="option.canMoveBox"
                  :canMove="option.canMove"
                  :centerBox="option.centerBox"
                  :info="option.info"
                  :fixedBox="option.fixedBox"
                  @realTime="realTime"
                ></vueCropper>
              </div>
              <!-- <img :src='previewImg'  alt="" class='previewImg' ref="img"> -->
              <el-upload
                class="upload-demo"
                action="https://jsonplaceholder.typicode.com/posts/"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :before-remove="beforeRemove"
                multiple
                :limit="3"
                :on-exceed="handleExceed"
                :file-list="fileList"
              >
                <el-button size="small" type="primary">{{
                  $t("button.clickToUpload")
                }}</el-button>
                <div slot="tip" class="el-upload__tip">
                  {{ $t("faceControl.faceRecognition.onlyImgFile") }}
                </div>
              </el-upload>
              <el-button type="primary" @click="handleClick">{{
                $t("faceControl.faceRecognition.btn")
              }}</el-button>
              <!-- <img :src="resImg" alt="" v-if="resImg" class='previewImg'> -->
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane
        :label="$t('faceControl.faceRecognition.faceSearch')"
        name="second"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="home">
              <div
                class="cropper"
                :style="{
                  width: `${imgObj.width}px`,
                  height: `${imgObj.height}px`,
                }"
              >
                <vueCropper
                  ref="cropper"
                  :img="option.img"
                  :outputSize="option.outputSize"
                  :outputType="option.outputType"
                  :canScale="option.canScale"
                  :autoCrop="option.autoCrop"
                  :autoCropWidth="option.autoCropWidth"
                  :autoCropHeight="option.autoCropHeight"
                  :canMoveBox="option.canMoveBox"
                  :canMove="option.canMove"
                  :centerBox="option.centerBox"
                  :info="option.info"
                  :fixedBox="option.fixedBox"
                  @realTime="realTime"
                ></vueCropper>
              </div>
              <!-- <img :src='previewImg'  alt="" class='previewImg' ref="img">
        <el-button type='primary' @click='handleClick'>Button</el-button>
        <img :src="resImg" alt="" v-if="resImg" class='previewImg'> -->
            </div>
          </el-col>
          <el-col :span="12">
            <div class="home">
              <div
                class="cropper"
                :style="{
                  width: `${imgObj.width}px`,
                  height: `${imgObj.height}px`,
                }"
              >
                <vueCropper
                  ref="cropper"
                  :img="option.img"
                  :outputSize="option.outputSize"
                  :outputType="option.outputType"
                  :canScale="option.canScale"
                  :autoCrop="option.autoCrop"
                  :autoCropWidth="option.autoCropWidth"
                  :autoCropHeight="option.autoCropHeight"
                  :canMoveBox="option.canMoveBox"
                  :canMove="option.canMove"
                  :centerBox="option.centerBox"
                  :info="option.info"
                  :fixedBox="option.fixedBox"
                  @realTime="realTime"
                ></vueCropper>
              </div>
              <!-- <img :src='previewImg'  alt="" class='previewImg' ref="img"> -->
              <el-upload
                class="upload-demo"
                action="https://jsonplaceholder.typicode.com/posts/"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :before-remove="beforeRemove"
                multiple
                :limit="3"
                :on-exceed="handleExceed"
                :file-list="fileList"
              >
                <el-button size="small" type="primary">{{
                  $t("button.clickToUpload")
                }}</el-button>
                <div slot="tip" class="el-upload__tip">
                  {{ $t("faceControl.faceRecognition.onlyImgFile") }}
                </div>
              </el-upload>
              <el-button type="primary" @click="handleClick">{{
                $t("faceControl.faceRecognition.btn")
              }}</el-button>
              <!-- <img :src="resImg" alt="" v-if="resImg" class='previewImg'> -->
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import { VueCropper } from "vue-cropper";
export default {
  data() {
    return {
      option: {
        img: require("../../../assets/images/man.png"), // 裁剪图片地址
        outputSize: 1, // 裁剪生成图片质量
        outputType: "jepg", // 裁剪生成图片格式
        canScale: true, // 图片是否允许滚轮播放
        autoCrop: true, // 是否默认生成截图框 false
        info: false, // 是否展示截图框信息
        autoCropWidth: 400, // 生成截图框的宽度
        autoCropHeight: 400, // 生成截图框的高度
        canMoveBox: true, // 截图框是否可以拖动
        fixedBox: true, // 固定截图框的大小
        canMove: false, // 上传图片是否可拖动
        centerBox: true, // 截图框限制在图片里面
      },
      resImg: null, //截图后图片
      previewImg: null, // 预览后的图片
      previewObj: {
        width: 200,
        height: 200,
      },
      imgObj: {
        width: 500,
        height: 500,
      },
    };
  },
  components: {
    VueCropper,
  },
  watch: {
    "option.img": {
      handler: function (val) {
        const that = this;
        const img = new Image();
        img.src = val;
        img.onload = function () {
          // that.imgObj.width = this.width
          // that.imgObj.height = this.height
        };
      },
      immediate: true,
    },
  },
  methods: {
    handleClick() {
      this.$refs.cropper.getCropData((data) => {
        console.log(data);
        this.resImg = data;
        this.handleDownload(data);
      });
    },
    handleDownload(url) {
      var a = document.createElement("a"); // 生成一个a元素
      var event = new MouseEvent("click"); // 创建一个单击事件
      a.download = "photo"; // 设置图片名称, 这里可以自定义，也可以获取图片名称进行修改
      a.href = url; // 将生成的设置为a.属性
      a.dispatchEvent(event); // 触发a的单击事件
    },
    realTime(data) {
      const that = this;
      this.$refs.cropper.getCropBlob((data) => {
        // 这里数据为类型，方法转换成64
        console.log(data);
        this.blobToDataURI(data, function (res) {
          console.log(res);
          that.previewImg = res;
        });
      });
    },
    blobToDataURI(blob, callback) {
      var reader = new FileReader();
      reader.readAsDataURL(blob);
      reader.onload = function (e) {
        callback(e.target.result);
      };
    },
  },
  mounted() {},
};
</script>
<style lang="scss" scoped>
@color: #333;
.home {
  width: 500px;
  height: 500px;
  background-color: #eee;
  .cropper {
    width: 500px;
    height: 500px;
    border: 1px solid orange;
  }
  .previewImg {
    width: 200px;
    height: 200px;
    object-fit: cover;
    border-radius: 50%;
  }
}
</style>
