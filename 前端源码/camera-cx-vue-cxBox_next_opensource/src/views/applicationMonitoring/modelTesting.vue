<template>
  <div class="model-cont">
    <div class="top-flex">
      <div class="title">
        {{ $t("applicationMonitoring.modelTesting.modelTest") }}
      </div>
      <div>
        <el-radio-group v-model="activeIndex">
          <el-radio-button label="1"
            >{{ $t("faceControl.faceRecognition.picture", { count: "" }) }}
          </el-radio-button>
          <el-radio-button label="2"
            >{{ $t("applicationMonitoring.modelTesting.video") }}
          </el-radio-button>
          <el-radio-button label="3">zip</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    <div v-if="algorithmList.length > 0" class="algorithm-flex">
      <div
        v-for="(item, index) in algorithmList"
        :key="index"
        class="flex-item"
        @click="algorithmClick(item)"
      >
        {{ item.name }}
      </div>
    </div>
    <!-- 图片测试 -->
    <ImgTest
      v-if="activeIndex == '1' && imgVisible"
      :detailObj="detailObj"
      @close="closeHandel"
    />
    <!-- 视频测试 -->
    <VideoTest
      v-if="activeIndex == '2' && videoVisible"
      :detailObj="detailObj"
      @close="closeHandel"
    />
    <!-- zip测试 -->
    <ZipTest
      v-if="activeIndex == '3' && zipVisible"
      :detailObj="detailObj"
      @close="closeHandel"
    />
  </div>
</template>
<script>
import { getListData } from "@/api/applicationMonitoring/modelTesting";
import ImgTest from "@/components/applicationMonitoring/modelTesting/imgTest";
import ZipTest from "@/components/applicationMonitoring/modelTesting/zipTest";
import VideoTest from "@/components/applicationMonitoring/modelTesting/videoTest";
export default {
  components: {
    ImgTest,
    ZipTest,
    VideoTest,
  },
  data() {
    return {
      activeIndex: "1",
      algorithmList: [],
      imgVisible: false,
      zipVisible: false,
      videoVisible: false,
      detailObj: {},
    };
  },
  created() {
    this.getListData();
  },
  methods: {
    // 切换模型
    handleSelect() {},
    // 获取算法列表
    async getListData() {
      const data = await getListData();
      this.algorithmList = data.data;
    },
    // 点击算法
    algorithmClick(item) {
      this.detailObj = { ...item };
      if (this.activeIndex == "1") {
        this.imgVisible = true;
      } else if (this.activeIndex == "2") {
        this.videoVisible = true;
      } else {
        this.zipVisible = true;
      }
    },
    // 弹窗关闭
    closeHandel() {
      if (this.activeIndex == "1") {
        this.imgVisible = false;
      } else if (this.activeIndex == "2") {
        this.videoVisible = false;
      } else {
        this.zipVisible = false;
      }
    },
  },
};
</script>
<style scoped lang="scss">
.model-cont {
  height: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 10px 20px;
  overflow-y: auto;
  .top-flex {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 10px 0px 20px 0px;
  }
  .title {
    font-weight: bold;
    margin-bottom: 10px;
  }
  .algorithm-flex {
    display: grid;
    grid-template-columns: repeat(5, 18%);
    gap: 10px;
    justify-content: space-between;
  }
  .flex-item {
    height: 80px;
    border: 1px solid #ccc;
    cursor: pointer;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    padding: 10px;
  }
  .flex-item:hover {
    box-shadow: 0px 3px 2px #ccc;
  }
}
</style>
