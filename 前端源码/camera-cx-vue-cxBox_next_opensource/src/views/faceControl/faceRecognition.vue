<template>
  <div class="wrap">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <el-row>
            <el-col :span="24" style="margin-bottom: 15px">
              <span class="card-title">{{
                $t("faceControl.faceRecognition.alarmOverview")
              }}</span>
              <!-- <span class="card-title-sub"
                >Only display from early morning today0to midnight24full day data from early morning</span
              > -->
            </el-col>
            <el-col :span="6" v-for="(item, index) in groups" :key="index">
              <el-statistic :title="item.name" style="height: 70px">
                <template slot="formatter">
                  <span
                    @click="getTabs(item.id, item.name)"
                    style="
                      padding: 10px 0;
                      color: #1e9fff;
                      cursor: pointer;
                      float: left;
                    "
                    >{{ item.userCount }}</span
                  >
                  <br />
                  <!-- <el-button type="text" @click="clickFaceManagement(index+1)">管理</el-button> -->
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic :title="$t('faceControl.faceRecognition.stranger')">
                <template slot="formatter">
                  <span
                    @click="
                      getTabs('', $t('faceControl.faceRecognition.stranger'))
                    "
                    style="padding: 10px 0; color: #1e9fff; cursor: pointer"
                    >{{ count }}</span
                  >
                  <!-- <el-button type="text" @click="clickStranger">查询当日</el-button> -->
                </template>
              </el-statistic>
            </el-col>

            <!-- <el-col :span="6">
              <el-statistic title="Employee">
                <template slot="formatter">
                  <span style="padding: 10px 0; color: #1e9fff">{{
                    statisticsDetail.yg
                  }}</span>
                  <br />
                  <el-button type="text" @click="clickFaceManagement(1)">Manage</el-button>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="Guest">
                <template slot="formatter">
                  <span style="padding: 10px 0; color: #32c682">{{
                    statisticsDetail.zk
                  }}</span>
                  <br />
                  <el-button type="text" @click="clickFaceManagement(2)">Manage</el-button>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="Visitor">
                <template slot="formatter">
                  <span style="padding: 10px 0; color: #a71d5d">{{
                    statisticsDetail.fk
                  }}</span>
                  <br />
                  <el-button type="text" @click="clickFaceManagement(3)">Manage</el-button>
                </template>
              </el-statistic>
            </el-col>
            <el-col :span="6">
              <el-statistic title="Stranger">
                <template slot="formatter">
                  <span style="padding: 10px 0; color: #f43838">{{
                    statisticsDetail.msr
                  }}</span>
                  <br />
                  <el-button type="text" @click="clickStranger">Query Today</el-button>
                </template>
              </el-statistic>
            </el-col> -->
          </el-row>
        </el-card>
        <el-card class="box-card" style="margin-top: 20px">
          <el-row>
            <el-col :span="24">
              <div style="margin-bottom: 15px">
                <span class="card-title">{{
                  $t("faceControl.faceRecognition.alarmLog")
                }}</span>
                <span class="card-title-sub">{{
                  $t("faceControl.faceRecognition.identificationRecord")
                }}</span>
              </div>
              <el-table :data="tableData" border>
                <el-table-column
                  :label="$t('common.number')"
                  type="index"
                ></el-table-column>
                <el-table-column
                  :label="$t('faceControl.faceRecognition.capturePictures')"
                >
                  <template slot-scope="scope">
                    <el-image
                      class="img"
                      :src="
                        $common.handlePublicUrl(
                          `/face/report/image?id=${scope.row.id}`
                        )
                      "
                      :preview-src-list="[
                        $common.handlePublicUrl(
                          `/face/report/image?id=${scope.row.id}`
                        ),
                      ]"
                      fit="cover"
                    >
                    </el-image>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('common.cameraName')">
                  <template slot-scope="scope">
                    <span v-if="scope.row && scope.row.camera">
                      {{ scope.row.camera.name }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.alarmTime')"
                  prop="alarmTime"
                >
                  <template slot-scope="scope">
                    <span>
                      {{ getMyDate(scope.row.createdAt) }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('faceControl.faceRecognition.alarmObject')"
                  prop="alarmTime"
                >
                  <template slot-scope="scope">
                    <span>
                      <span v-if="scope.row.group?.name">
                        {{ scope.row.faceUser?.name }}
                      </span>
                      <span v-else>{{
                        $t("faceControl.faceRecognition.stranger")
                      }}</span>
                    </span>
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('faceControl.faceRecognition.belongingGroup')"
                  prop="alarmTime"
                >
                  <template slot-scope="scope">
                    <span>
                      {{ scope.row.group?.name || "--" }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </el-col>
            <!-- <el-col :span="6">
              <div class="card-title">Real-time capture record</div>
              <el-row :gutter="10">
                <el-col
                  :span="12"
                  v-for="(item, index) in tableData"
                  :key="index"
                >
                  <el-image
                    class="img"
                    :src="$common.handleStream(item.id)"
                    fit="cover"
                    :preview-src-list="[$common.handleStream(item.id)]"
                  >
                    <div slot="error" class="image-slot">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
                </el-col>
              </el-row>
            </el-col> -->
          </el-row>
        </el-card>
      </el-col>
      <!-- <el-col :span="12">
        <el-card class="box-card">
          <div class="card-title">
            <span>Video Cruise</span>
          </div>
          <div class="video-box">
            <div
              class="video-item"
            >
              <div style="width: 100%;height: 100%;" v-if="cameraId">
                <VideoBox :Id="cameraId" :index="1" ref="video1"></VideoBox>
              </div>
            </div>
            <div class="dropdown">
              <el-select
                :placeholder="$t('common.camera')"
                v-model="cameraId"
                class="head-container-input"
                @change="changeCameraId()"
                style="margin-left: 10px;"
              >
                <el-option
                  v-for="(subItem, subIndex) in activeList"
                  :key="subIndex"
                  :label="subItem.name"
                  :value="subItem.id"
                ></el-option>
              </el-select>
            </div>
          </div>
        </el-card>
      </el-col> -->
    </el-row>
    <FaceManagement
      :currentFaceType="currentFaceType"
      v-if="faceManagementVisible"
      @close="faceManagementVisible = false"
    />
    <StrangerList v-if="strangerVisible" @close="strangerVisible = false" />
  </div>
</template>
<script>
import { getMyDate } from "@/utils/common.js";
import Player from "xgplayer";
import FlvPlugin from "xgplayer-flv";
import "xgplayer/dist/index.min.css";
import {
  getNewData,
  getNewLy,
  getActives,
  getPlayUrl,
} from "@/api/faceControl/faceRecognition";
import { listPageDGroup } from "./faceManagent/api";
import { reportImage, listPage } from "./faceHistory/api";
import FaceManagement from "@/components/faceControl/faceRecognition/faceManagement";
import StrangerList from "@/components/faceControl/faceRecognition/strangerList";
import VideoBox from "@/components/faceControl/video";
export default {
  components: {
    FaceManagement,
    StrangerList,
    VideoBox,
  },
  data() {
    return {
      getMyDate: getMyDate,
      faceManagementVisible: false,
      strangerVisible: false,
      currentFaceType: "1",
      imgUrl: "",
      statisticsDetail: {},
      tableData: [],
      activeList: [],
      videoCount: 4,
      bigPoster: "",
      groups: [],
      timeObj: {},
      groupId: "",
      hasStranger: 0,
      count: null,
      cameraId: "",
    };
  },
  created() {
    // this.getNewData();

    let that = this;

    this.timeObj = setInterval(function () {
      that.getNewLy();
      that.getGroup();
    }, 2000);
    that.getNewLy();

    this.getActives();
    this.getGroup();
    // this.getNumber();
  },
  destroyed() {
    clearInterval(this.timeObj);
    this.timeObj = null;
  },
  beforeDestroy() {
    clearInterval(this.timeObj);
    this.timeObj = null;
  },
  methods: {
    getTabs(id, name) {
      this.groupId = id;
      this.hasStranger =
        name == $t("faceControl.faceRecognition.total")
          ? 0
          : name == $t("faceControl.faceRecognition.stranger")
          ? 1
          : 2;
      this.getNewLy();
    },
    // // 获取统计数据
    // async getNewData() {
    //   const data = await getNewData();
    //   this.statisticsDetail = data.data;
    // },
    // 获取陌生人告警记录
    async getNewLy() {
      const data = await listPage({
        limit: 10,
        page: 1,
        groupId: this.groupId || "",
        hasStranger: this.hasStranger,
      });
      this.tableData = data.data;
      // console.log(this.tableData, data.data);
    },

    // async getNumber() {
    //   const data = await listPage({
    //     limit: 10,
    //     page: 1,
    //     hasStranger: 1,
    //   });
    //   this.count = data.count;
    // },

    // 获取视频流并播放
    async getActives() {
      const data = await getActives();
      this.activeList = data.data;
      this.cameraId = this.activeList.length > 0 ? this.activeList[0].id : "";
      // this.activeList.forEach(async (item) => {
      //   const res = await getPlayUrl({ cameraId: item.id });
      //   const playUrl = res.data;
      //   let player = new Player({
      //     id: "video" + item.id,
      //     isLive: true,
      //     playsinline: true,
      //     url: playUrl,
      //     autoplay: true,
      //     fluid: true,
      //     controls: true,
      //     plugins: [FlvPlugin],
      //   });
      // });
    },
    changeCameraId() {
      this.$refs.video1.cameraId = this.cameraId;
      this.$refs.video1.getPlayUrl();
    },
    // 人脸管理
    clickFaceManagement(type) {
      this.currentFaceType = type + "";
      this.faceManagementVisible = true;
    },
    // 查询陌生人
    clickStranger() {
      this.strangerVisible = true;
    },
    async getGroup() {
      const { data, count } = await listPageDGroup();
      this.groups = data;
      if (this.groups.length > 0) {
        this.groups.forEach((item, index) => {
          if (item.name == $t("faceControl.faceRecognition.total")) {
            this.result(item);
          }
        });
      }
    },
    result(items) {
      let otherItemsValuesSum = this.groups
        .filter((item) => item.id !== items.id)
        .reduce((sum, item) => sum + item.userCount, 0);
      this.count = items.userCount - otherItemsValuesSum;
    },
  },
};
</script>
<style scoped lang="scss">
.wrap {
  min-width: 1200px;
  .card-title {
    font-weight: bold;
    padding-bottom: 0px;
    font-family: PingFang SC;
    font-size: 20px;
    font-weight: 400;
    line-height: 22px;
    letter-spacing: 0em;
    margin-bottom: 10px;
  }
  .card-title-sub {
    //styleName: Body/Medium;
    font-family: PingFang SC;
    font-size: 13px;
    font-weight: 400;
    line-height: 22px;
    letter-spacing: 0em;
    text-align: left;
    color: #00000066;
    padding-left: 5px;
  }
  .img {
    // width: 100px;
    height: 50px;
  }
  .video-box {
    width: 100%;
    // height: 530px;
    // background: rgba(47, 64, 86, 0.7);
    display: grid;
    overflow: hidden;
    position: relative;
    .video-item {
      // border: 1px #2f4056 solid;
      display: flex;
      align-items: center;
      position: relative;
      margin-bottom: 5px;
      // margin-left: 5px;
      .video-item-name {
        position: absolute;
        top: 5px;
        left: 5px;
      }
    }
  }
  .video4 {
    grid-template-columns: 50% 50%;
    grid-template-rows: 50% 50%;
  }
}

::v-deep {
  .el-table {
    .cell {
      // display: flex;
    }
    .el-button--text {
      color: #E53935;

      &.is-disabled {
        color: #c0c4cc;
      }
    }
  }
}

::v-deep .el-table thead th {
  //background-color: #FFF !important;
}

.common-table-header {
  margin-bottom: 10px;
}

.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  * {
    display: inline;
  }
}

.table-container {
  position: relative;

  //.table-loading {
  //  position: absolute;
  //  display: flex;
  //  align-items: center;
  //  justify-content: center;
  //  top: 0;
  //  left: 0;
  //  right: 0;
  //  bottom: 0;
  //  z-index: 9999;
  //  background-color: rgba(255, 255, 255, 0.7);
  //
  //  i {
  //    color: rgba(230, 162, 60, 0.5);
  //    font-size: 30px;
  //    font-weight: bolder;
  //  }
  //}
}
.dropdown {
  position: absolute;
  right: 10px;
  top: 10px;
  z-index: 10;
}
</style>
