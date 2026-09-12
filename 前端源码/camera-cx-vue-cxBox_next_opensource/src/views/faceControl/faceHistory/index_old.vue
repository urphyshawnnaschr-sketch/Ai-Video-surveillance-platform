<template>
  <div>
    <div class="flex">
      <div class="flex-tree">
        <!-- <el-radio-group v-model="nodeLabel"> -->
        <!-- <span  style="padding-left:23px;display: block;height: 35px;line-height: 35px;"  @click="handleNodeClick('')">
            <el-radio label="0">All</el-radio>
          </span> -->

        <el-tree
          :data="treeData"
          :props="defaultProps"
          :highlight-current="true"
        >
          <div
            class="custom-tree-node"
            slot-scope="{ node, data }"
            @click="handleNodeClick(data.id)"
            style="width: 90%"
          >
            <div style="white-space: pre-line; word-wrap: break-word">
              {{ node.label }}
            </div>
            <!-- <span style="margin-left: 10px;" @click="openTreeMenu(data)">
                <i class="el-icon-setting"></i>
              </span> -->
          </div>
        </el-tree>
        <!-- </el-radio-group> -->
      </div>
      <div class="flex-right">
        <div class="head-container">
          <el-row>
            <el-form label-position="left">
              <el-col :span="4">
                <el-form-item :label="$t('login.phoneNumber')">
                  <el-input
                    style="width: 90px"
                    v-model="formatData.phone"
                    placeholder=""
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :label="$t('common.name')">
                  <el-input
                    style="width: 90px"
                    v-model="formatData.name"
                    placeholder=""
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="9">
                <el-form-item :label="$t('faceControl.faceRecognition.date')">
                  <!-- :picker-options="filerData.pickerOptions" -->
                  <el-date-picker
                    v-model="times"
                    style="width: 330px"
                    :default-time="['00:00:00', '23:59:59']"
                    type="datetimerange"
                    :range-separator="$t('common.pickerDate.to')"
                    format="yyyy-MM-dd HH:mm:ss"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :start-placeholder="$t('common.pickerDate.startDate')"
                    :end-placeholder="$t('common.pickerDate.endDate')"
                  >
                  </el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-button
                  type="primary"
                  icon="el-icon-search"
                  @click="getTable"
                  >{{ $t("button.queryText") }}</el-button
                >
                <el-button icon="el-icon-refresh" @click="reset">{{
                  $t("button.resetText", { text: "" })
                }}</el-button>
              </el-col>
            </el-form>
          </el-row>
          <div style="display: flex; align-items: center">
            <div class="clear-title">
              <el-popover placement="bottom-start" width="300" trigger="hover">
                <div style="font-size: 12px; line-height: 18px">
                  {{ $t("faceControl.faceRecognition.shelfLife") }}
                </div>
                <span class="el-icon-warning-outline" slot="reference"></span>
              </el-popover>
              <span style="padding: 0px 10px 0px 5px"
                >{{ $t("faceControl.faceRecognition.dataPeriod") }}:</span
              >
            </div>
            <el-select
              :placeholder="$t('common.chooseText')"
              v-model="clearReportDay"
              style="width: 100px"
              @change="dayChange()"
            >
              <el-option
                v-for="(item, index) in clearDayList"
                :key="index"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </div>
        </div>
        <div class="ai_table">
          <div style="width: 100%">
            <el-tabs v-model="activeName" @tab-click="changeCamera()">
              <el-tab-pane
                v-for="(item, index) in groupList"
                :key="index"
                :label="item.name"
                :name="item.id"
              ></el-tab-pane>
              <el-tab-pane
                :label="$t('faceControl.faceRecognition.stranger')"
                name="#"
              ></el-tab-pane>
            </el-tabs>
            <div class="flexbg" ref="flexbg" v-if="dataSource.length">
              <div class="flexItem" v-for="item in dataSource" :key="item.id">
                <div>
                  <el-image
                    style="width: 150px; height: 200px"
                    :preview-src-list="[
                      VUE_APP_API_BASE_URL +
                        '/face/report/image?id=' +
                        item.id,
                    ]"
                    :src="
                      VUE_APP_API_BASE_URL +
                      '/face/report/image?id=' +
                      item.id
                    "
                  >
                    <div slot="error" class="image-slot">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
                </div>
                <div class="text" style="width: 100%">
                  <div
                    v-if="item.group?.name"
                    @click="listByUserId(item)"
                    class="pad-10"
                  >
                    <span class="tits">
                      {{ item.faceUser?.name || "--" }}
                    </span>
                    <span class="tits" style="float: right">
                      {{ item.group?.name || "--" }}
                    </span>
                  </div>
                  <div v-else class="pad-10">
                    <span>{{
                      $t("faceControl.faceRecognition.stranger")
                    }}</span>
                    <span
                      style="float: right; color: #E53935"
                      @click="addHei(item)"
                      >{{ $t("faceControl.faceRecognition.faceStore") }}</span
                    >
                  </div>
                </div>
                <div class="text pad-10">
                  <span class="tits" style="width: 130px">{{
                    item.camera?.name
                  }}</span>
                </div>
                <div class="text pad-10">
                  {{ getMyDate(item.createdAt) }}
                </div>
              </div>
              <div
                class="flexItem"
                v-for="i in totals"
                :key="i"
                style="opacity: 0"
              >
                <div>
                  <el-image style="width: 150px; height: 200px"> </el-image>
                </div>
                <div class="text pad-10"></div>
                <div class="text pad-10"></div>
              </div>
            </div>
            <div class="flexbg" v-else>
              <el-empty :description="$t('common.noData')"></el-empty>
            </div>
            <!-- <el-row :gutter="12" v-if="dataSource.length">
              <el-col :span="4" style="margin-top: 10px;" v-for="item in dataSource" :key="item.id">
                <el-card :body-style="{ padding: '0px',border:'0px' }" shadow="hover">
                  
                </el-card>
              </el-col>
            </el-row> -->
            <div class="pagination">
              <el-pagination
                background
                :current-page="params.page"
                :page-size="params.limit"
                layout="total, sizes, prev, pager, next, jumper"
                :total="params.total"
                @current-change="handleCurrentChange"
                @size-change="handleSizeChange"
              ></el-pagination>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-drawer
      :title="groupName"
      :visible.sync="drawer"
      size="50%"
      direction="btt"
    >
      <div>
        <div
          class="flexbg"
          v-if="cameraUser.length"
          style="justify-content: flex-start"
        >
          <div
            class="flexItem"
            v-for="item in cameraUser"
            :key="item.id"
            style="margin-left: 10px"
          >
            <div>
              <el-image
                style="width: 150px; height: 200px"
                :preview-src-list="[
                  VUE_APP_API_BASE_URL +
                    '/face/report/image?id=' +
                    item.id,
                ]"
                :src="
                  VUE_APP_API_BASE_URL +
                  '/face/report/image?id=' +
                  item.id
                "
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </div>
            <div class="text pad-10">
              <span
                class="tits"
                style="width: 130px"
                :title="item.camera.name"
                >{{ item.camera.name || "--" }}</span
              >
            </div>
            <div class="text pad-10">
              {{ getMyDate(item.createdAt) }}
            </div>
          </div>
        </div>
        <el-empty v-else :description="$t('common.noData')"></el-empty>
      </div>
    </el-drawer>

    <detail
      v-if="detailVisible"
      :currentItme="currentItme"
      :visible.sync="detailVisible"
      @close="close()"
    ></detail>
  </div>
</template>
<script>
import Tables from "@/components/Table/index.vue";
import {
  listPage,
  cameraListData,
  groupListData,
  listByUserId,
  saveClearFaceReportDayConfig,
} from "./api";
import { getAfterSales } from "@/api/applicationMonitoring/systemManagement";
import { getMyDate } from "@/utils/common.js";
import detail from "./components/detail.vue";

export default {
  data() {
    return {
      filerData: {
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > Date.now() - 8.64e6;
          },
        },
      },
      params: {
        page: 1,
        limit: 50,
        total: 0,
      },
      getMyDate: getMyDate,
      rowSelection: {
        type: "checkbox",
        change: (selectedRowKeys, selectedRows) => {
          this.selectedRowKeys = selectedRowKeys;
          this.selectedRows = selectedRows;
        },
      },
      selectedRowKeys: [],
      selectedRows: [],
      loading: false,
      dataSource: [],
      columns: Object.freeze([
        {
          key: "createdAt",
          title: $t("faceControl.faceRecognition.snapTime"),
          align: "center",
          render(h, { value }) {
            return h("span", [getMyDate(createdAt)]);
          },
        },
        {
          key: "filePath",
          title: $t("faceControl.faceRecognition.capturePictures"),
          align: "center",
          slot: "img",
        },

        {
          key: "hasStranger",
          title: $t("faceControl.faceRecognition.containStranger"),
          align: "center",
          render(h, { value }) {
            let str = value ? $t("common.yesText") : $t("common.noText");
            return h("span", [str]);
          },
        },
        {
          key: "camera",
          title: $t("common.cameraName"),
          align: "center",
          render(h, { value }) {
            return h("span", [value.name]);
          },
        },
      ]),
      formatData: {
        name: "",
        phone: "",
      },
      cameras: [],
      cameraUser: [],
      times: [],
      treeData: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
      groupList: [],
      VUE_APP_API_BASE_URL,
      activeName: "",
      totals: 0,
      groupName: "",
      detailVisible: false,
      drawer: false,
      nodeLabel: "0",
      timeObj: null,
      clearDayList: [
        {
          id: "30",
          name: $t("faceControl.faceRecognition.nearDate", { count: 30 }),
        },
        {
          id: "20",
          name: $t("faceControl.faceRecognition.nearDate", { count: 20 }),
        },
        {
          id: "14",
          name: $t("faceControl.faceRecognition.nearDate", { count: 14 }),
        },
        {
          id: "10",
          name: $t("faceControl.faceRecognition.nearDate", { count: 10 }),
        },
        {
          id: "7",
          name: $t("faceControl.faceRecognition.nearDate", { count: 7 }),
        },
        {
          id: "5",
          name: $t("faceControl.faceRecognition.nearDate", { count: 5 }),
        },
        {
          id: "3",
          name: $t("faceControl.faceRecognition.nearDate", { count: 3 }),
        },
        {
          id: "1",
          name: $t("faceControl.faceRecognition.nearDate", { count: 1 }),
        },
        {
          id: "0",
          name: $t("faceControl.faceRecognition.today"),
        },
      ],
      clearReportDay: "30",
      oldDay: "30",
    };
  },
  components: {
    Tables,
    detail,
  },
  created() {
    this.getDay();
    this.getTable();
    this.cameraListPage();
  },
  destroyed() {
    clearInterval(this.timeObj);
    this.timeObj = null;
  },
  methods: {
    addHei(item) {
      this.detailVisible = true;
      this.currentItme = item;
    },
    mans() {
      this.$nextTick(() => {
        let total = this.dataSource.length;
        let limit = Math.floor(this.$refs.flexbg.clientWidth / 150);
        if (total % limit) {
          this.totals = Number(limit - (total % limit));
        } else {
          this.totals = 0;
        }
      });
    },
    async listByUserId(item) {
      this.groupName = item.faceUser.name + " " + item.group.name;
      const { data } = await listByUserId({ userId: item.userId });
      this.cameraUser = data || [];
      this.drawer = true;
    },
    // 点击节点
    handleNodeClick(id) {
      // Object.assign(this.formatData, {
      //   cameraId: node.cameraId,
      // });
      this.formatData.cameraId = id;
      this.params.page = 1;
      this.getTable();
    },
    handleCurrentChange(val) {
      this.params.page = val;
      this.getTable();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getTable();
    },
    async getTable() {
      if (this.times.length) {
        this.formatData.startDate = encodeURIComponent(this.times[0]);
        this.formatData.endDate = encodeURIComponent(this.times[1]);
      }
      this.formatData = {
        ...this.formatData,
        limit: this.params.limit,
        page: this.params.page,
        hasStranger: 0,
      };

      if (this.activeName == "#") {
        this.formatData.hasStranger = 1;
        this.formatData.groupId = "";
      }
      this.loading = true;
      const { data, count } = await listPage(this.formatData);
      this.dataSource = data;
      this.params.total = parseInt(count);
      this.loading = false;
      this.mans();
      if (this.timeObj) {
        clearInterval(this.timeObj);
        this.timeObj = null;
      }
      let that = this;
      this.timeObj = setInterval(function () {
        that.getTable();
      }, 2000);
    },
    reset() {
      this.times = [];
      this.formatData = {};
      this.params.page = 1;
      this.getTable();
    },
    async cameraListPage() {
      const { data } = await cameraListData();

      this.treeData = [
        { name: $t("faceControl.faceRecognition.total"), id: "" },
        ...data,
      ];
      // if (this.treeData.length) {
      //   //this.formatData.cameraId = this.cameras[0].id;
      //   this.changeCamera();
      // }
      this.groupListData();
    },
    async groupListData() {
      const { data } = await groupListData();
      data.map((item, ind) => {
        if (item.name == $t("faceControl.faceRecognition.total")) {
          item.id = item.id.toString();
        }
      });
      this.groupList = data;
      this.changeCamera();
    },

    changeCamera() {
      this.formatData.groupId = this.activeName == 0 ? "" : this.activeName;
      this.params.page = 1;
      this.getTable();
    },
    // 切换定时任务清除告警信息天数
    dayChange() {
      let str = "";
      this.clearDayList.forEach((item) => {
        if (this.clearReportDay == item.id) {
          str = item.name;
        }
      });
      this.$confirm(
        `${$t(
          "faceControl.faceRecognition.confirmText"
        )}“${str}”?<div style="color: red;">(${$t(
          "faceControl.faceRecognition.confirmSaveSure",
          { str }
        )}。)</div>`,
        $t("common.prompt"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          dangerouslyUseHTMLString: true,
          type: "warning",
        }
      )
        .then(() => {
          saveClearFaceReportDayConfig({
            clearFaceReportDay: this.clearReportDay,
          }).then((res) => {
            this.$message({
              message: $t("button.saveText", { text: $t("common.success") }),
              type: "success",
              duration: 500,
            });
            this.oldDay = this.clearReportDay;
          });
        })
        .catch(() => {
          this.clearReportDay = this.oldDay;
        });
    },
    //获取清除告警信息天数
    async getDay() {
      const res = await getAfterSales({ tag: "clearFaceReportDay" });
      this.clearReportDay = res.data ? res.data : "30";
      this.oldDay = res.data ? res.data : "30";
    },
  },
};
</script>
<style scoped lang="scss">
.search_box {
  padding-bottom: 20px;
}
.flex {
  display: flex;
  .flex-tree {
    width: 240px;
    border-radius: 8px;
    background: #fff;
    padding: 10px;
    margin: 10px 10px 0 0;
    flex-shrink: 0;
    ::v-deep .el-tree-node__content {
      height: auto;
    }
    .custom-tree-node {
      font-size: 13px;
    }
  }
  .flex-right {
    flex: 1;
    width: calc(100% - 300px);
  }
}
.el-dropdown-link {
  color: #E53935;
}
.treeMenu {
  position: fixed;
  z-index: 99999;
  top: 50%;
  left: 50%;
  background-color: white;
  overflow: hidden;
  border-radius: 5px;
  border: 1px solid #e6ebf5;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
  div {
    padding: 3px 20px;
    box-sizing: border-box;
    //width: 50px;
    text-align: center;
  }
  div:hover {
    background-color: #eee;
    cursor: pointer;
  }
}
.text {
  font-size: 12px;
  color: #1A0808;
  height: 22px;
  // padding: 0 10px;
  cursor: pointer;
}
.pad-10 {
  padding: 0px 10px;
}
.ai_table {
  width: 100%;
  overflow: hidden;
}
.flexbg {
  width: 100%;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  flex-wrap: wrap;
}
.title {
  margin: 10px;
  margin-left: 0;
}
.tits {
  display: inline-block;
  width: 60px;
  height: 20px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.flexItem {
  display: inline-flex;
  //padding-bottom: var(--Space-Size-small, 8px);
  margin-bottom: 8px;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  border-radius: 6px;
  background: #fff;

  /* 卡片阴影 */
  box-shadow: 0px 12px 32px 0px rgba(0, 0, 0, 0.04),
    0px 8px 20px 0px rgba(0, 0, 0, 0.08);
}
</style>
