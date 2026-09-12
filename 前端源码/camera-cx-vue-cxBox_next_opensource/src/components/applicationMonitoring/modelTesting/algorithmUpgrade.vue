<template>
  <div>
    <div>
      {{ $t("modeltesting.algorithmupgrade.22gv2v") }}{{ algorithmName }}
    </div>
    <el-card
      class="box-card"
      v-for="(item, index) in platformList"
      :key="index"
    >
      <div slot="header" class="card-header">
        <span>{{ $t("modeltesting.addalgorithm.eg4662") }}{{ item }}</span>
        <div style="display: flex; align-items: center">
          <div class="box-selected">
            <span
              >{{ $t("modeltesting.algorithmupgrade.w8s5zh")
              }}{{ selectedCounts[item]
              }}{{ $t("modeltesting.algorithmupgrade.5uysd5") }}</span
            >
            <span v-if="switchCounts[item] > 0" style="padding-right: 3px">{{
              $t("modeltesting.algorithmupgrade.889k14")
            }}</span>
            <el-popover
              :title="$t('modeltesting.algorithmupgrade.2i3k7x')"
              placement="right"
              width="400"
              trigger="click"
              v-if="switchCounts[item] > 0"
            >
              <div
                v-for="it in selectedBoxs[item]"
                :key="`selected_box_` + it.id"
                class="text item"
                style="display: flex; align-items: center"
              >
                <p style="width: 200px">
                  {{ $t("modeltesting.algorithmupgrade.4a3t15") }}{{ it.name }}
                </p>
                <p>
                  {{ $t("modeltesting.algorithmupgrade.8x6ib6")
                  }}{{ it.process }}
                </p>
              </div>
              <el-link type="primary" slot="reference">{{
                switchCounts[item]
              }}</el-link>
            </el-popover>
            <span v-if="switchCounts[item] > 0" style="padding-left: 3px">{{
              $t("modeltesting.algorithmupgrade.5uysd5")
            }}</span>
          </div>
          <el-button
            v-if="selectedCounts[item] > 0"
            class="box-btn"
            type="text"
            @click="handleClearBoxs(item)"
            :disabled="isSwitching"
            >{{ $t("modeltesting.algorithmupgrade.82j158") }}</el-button
          >
          <el-button
            class="box-btn"
            type="text"
            @click="handleSwitchModel(item)"
            :disabled="isSwitching"
            >{{ $t("modeltesting.algorithmupgrade.575db5") }}</el-button
          >
        </div>
      </div>
      <el-row :gutter="50">
        <el-col :span="8">
          <p class="tit">{{ $t("modeltesting.algorithmupgrade.84obg3") }}</p>
          <el-empty
            v-if="fileLists[item].length == 0"
            :image-size="100"
          ></el-empty>
          <ul class="file-list" v-else>
            <li v-for="fil in fileLists[item]" :key="fil.name">
              <span>{{ fil.name }}</span>
              <div>
                <span class="file-time">{{
                  `${$t("applicationMonitoring.common.updateTime")}：${
                    fil.updated_at
                  }`
                }}</span
                ><el-link
                  :underline="false"
                  :type="fil.name == selectdFiles[item] ? 'success' : 'info'"
                  :class="
                    fil.name == selectdFiles[item]
                      ? 'file-selected'
                      : 'file-unselect'
                  "
                  @click="handleSelectFile(item, fil.name)"
                  ><i class="el-icon-circle-check"></i
                ></el-link>
              </div>
            </li>
          </ul>
        </el-col>
        <el-col :span="16">
          <p style="color: #1A0808; font-size: 13px">
            {{ $t("modeltesting.algorithmupgrade.44c076") }}
          </p>
          <el-table
            :ref="`table-` + item"
            :data="tableDatas[item]"
            style="width: 100%"
            @select="(selection, row) => handleSelection(selection, item)"
            @select-all="(selection) => handleSelection(selection, item)"
          >
            <el-table-column
              type="selection"
              width="55"
              :selectable="getIsSelectable"
            ></el-table-column>
            <el-table-column
              prop="name"
              :label="$t('modeltesting.algorithmupgrade.h825du')"
            >
            </el-table-column>
            <el-table-column
              prop="boxNo"
              :label="$t('modeltesting.algorithmupgrade.e71u15')"
            >
            </el-table-column>
            <el-table-column
              prop="departName"
              :label="$t('modeltesting.algorithmupgrade.4w5qse')"
            >
            </el-table-column>
            <el-table-column
              prop="boxHeartTime"
              :label="$t('applicationMonitoring.boxManagement.c1rxbp')"
            >
              <template slot-scope="scope">
                {{
                  $moment(Number(scope.row.boxHeartTime)).format(
                    "YYYY-MM-DD HH:mm:ss"
                  )
                }}
              </template>
            </el-table-column>
            <el-table-column
              prop="online"
              :label="$t('common.presence')"
              align="center"
            >
              <template slot-scope="scope">{{
                onlineStatusMap[scope.row.online]
              }}</template>
            </el-table-column>
          </el-table>
          <el-pagination
            background
            layout="prev, pager, next"
            :total="tableTotals[item]"
            :page-size="10"
            @current-change="handlePageChange(item, $event)"
          >
          </el-pagination>
        </el-col>
      </el-row>
    </el-card>

    <!-- <el-row :gutter="24">
      <el-col :span="10">
        <el-card class="box-card">
          <div slot="header" class="clear-flex">
            <div>Bind Box</div>
            <div class="alg">{{ algorithmName }}</div>
          </div>
          <el-checkbox-group v-model="formatData.boxIds">
            <el-checkbox
              v-for="(item, index) in boxList"
              :key="item.locationId"
              :label="item.locationId"
              :disabled="item.algorithmVersion ? false : true"
              style="display: block; margin-top: 10px"
              >{{ item.boxName }}
              <span v-if="item.algorithmVersion">{{
                "(" + item.algorithmVersion + ")"
              }}</span>
              <span v-else style="color: red"
                >(No camera uses this algorithm，Please bind first)</span
              >
            </el-checkbox>
          </el-checkbox-group>
        </el-card>
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>Update progress</span>
          </div>
          <ul>
            <li
              style="margin-top: 10px"
              v-for="(item, index) in boxList"
              :key="item.locationId"
            >
              <span
                style="
                  color:  #1A0808;
                  font-size: 13px;
                  float: left;
                  width: 200px;
                "
                >{{ item.boxName }}:</span
              >
              <span style="color:  #1A0808; font-size: 12px">{{
                state(item.boxUpdateStatus)
              }}</span>
            </li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>Local algorithm file</span>
            <el-button
              style="float: right"
              type="primary"
              size="mini"
              :loading="changeLoading"
              @click="changeBoxAlgorithmVersion"
              >Switch Version</el-button
            >
          </div>
          <div class="title">Latest Version</div>
          <div class="radio-sty" v-if="lastVersionFile && lastVersionFile.name">
            <el-radio
              v-model="formatData.fileName"
              :label="lastVersionFile.name"
              >{{ lastVersionFile.name }}</el-radio
            >
            <div style="width: 50%">
              <el-progress
                class="percen-sty"
                :percentage="
                  handleProcess(
                    lastVersionFile.localLength,
                    lastVersionFile.length
                  )
                "
              ></el-progress>
            </div>
          </div>
          <div class="title">Historical Version</div>
          <div class="radio-sty" v-for="(item, index) in fileList">
            <el-radio v-model="formatData.fileName" :label="item.name">{{
              item.name
            }}</el-radio>
            <div style="width: 50%">
              <el-progress
                class="percen-sty"
                :percentage="handleProcess(item.localLength, item.length)"
              ></el-progress>
            </div>
          </div>
        </el-card>
        <el-card class="box-card">
          <div slot="header" class="clear-flex">
            <div>Upload algorithm version file</div>
            <div class="alg">{{ nameEn }}</div>
          </div>
          <ImportAlgorithm
            :platform="platform"
            :nameEn="nameEn"
            @closeImport="closeImport"
          />
        </el-card>
      </el-col>
    </el-row> -->
  </div>
</template>
<script>
import Cookies from "js-cookie";
import {
  getBoxAndHistoryVersion,
  changeBoxAlgorithmVersion,
  getLocalFiles,
  switchModel,
} from "@/api/applicationMonitoring/algorithmManagement";
import { listPage } from "@/api/applicationMonitoring/casketManagement";
import { getPlatform } from "@/api/applicationMonitoring/algorithmManagement";

import ImportAlgorithm from "@/components/applicationMonitoring/modelTesting/importAlgorithm";
export default {
  components: {
    ImportAlgorithm,
  },
  data() {
    return {
      formatData: {
        boxIds: [],
        fileName: "",
      },
      boxList: [],
      fileList: [],
      boxCheckList: "",
      fileCheckList: "",
      VUE_APP_API_BASE_URL,
      token: Cookies.get("X-Token"),
      file: "",
      // id:"",
      timer: null,
      lastVersionFile: {},
      changeLoading: false,
      // algorithmName:'',
      // 芯片类型，例如：platformList: ['chaoxing', '1684x']
      platformList: [],
      // 选择的算法文件，例如: selectdFiles: {"chaoxing": 'chaoxing-people-1.0.zip', "1684x": ""}
      selectdFiles: {},
      // 盒子设备数量数据，例如: tableTotals: {"chaoxing": 10, "1684x": 20}
      tableTotals: {},
      // 算法文件数据，例如: fileLists: {"chaoxing":[{'name': 'xxx-xxx-1.0.zip', 'updated_at': '2025-04-14 12:12:15'}], "1684x": []}
      fileLists: {},
      // 表格数据，例如: tableDatas: {"chaoxing":[], "1684x": []}
      tableDatas: {},
      // 选择的盒子,例如: selectedBoxs: {"chaoxing":['1', '2'], "1684x": []}
      selectedBoxs: {},
      // 选中的盒子数量,,例如: selectedCounts: {"chaoxing": 5, "1684x": 0}
      selectedCounts: {},
      // 是否可选
      isSelectable: true,
      // 是否正在执行切换
      isSwitching: false,
      // 盒子查询参数, ,例如: queryParams: {"chaoxing": { page: 1, limit 10 }, "1684x": {}}
      queryParams: {},
      // 处理盒子数量,,例如: switchCounts: {"chaoxing": 5, "1684x": 0}
      switchCounts: {},
      // 在线状态
      onlineStatusMap: {
        0: $t("common.offline"),
        1: $t("common.online"),
      },
    };
  },
  props: {
    id: {
      type: String,
      default: "",
    },
    algorithmName: {
      type: String,
      default: "",
    },
    platform: {
      type: String,
      default: "",
    },
    nameEn: {
      type: String,
      default: "",
    },
  },
  created() {
    this.getBoxAndHistoryVersion();

    this.fetchPlatform();
  },
  destroyed() {
    clearInterval(this.timer);
  },
  methods: {
    state(s) {
      let obj = {
        0: $t("applicationMonitoring.algorithmArrangeUpdata.noUpdateTask"),
        1: $t("applicationMonitoring.algorithmArrangeUpdata.waitingUpdates"),
        2: $t("applicationMonitoring.algorithmArrangeUpdata.updating"),
        3: $t("applicationMonitoring.algorithmArrangeUpdata.updateCompleted"),
        4: $t(
          "applicationMonitoring.algorithmArrangeUpdata.algorithmFileDownload"
        ),
      };
      return obj[s];
    },
    async getBoxAndHistoryVersion() {
      let { data } = await getBoxAndHistoryVersion({ id: this.id });
      this.boxList = data.boxList;
      if (data.fileList.length > 0) {
        let newArr = data.fileList.filter((item) => {
          return item.name != data.lastVersionFile.name;
        });
        this.fileList = newArr;
      }
      this.lastVersionFile = data.lastVersionFile;
      let Arr = data.boxList.filter((item) => {
        return item.boxUpdateStatus != 0;
      });
      if (Arr.length > 0) {
        if (this.timer) {
          clearInterval(this.timer);
        }
        let that = this;
        this.timer = setInterval(function () {
          that.getBoxAndHistoryVersion();
        }, 3000);
      } else {
        clearInterval(this.timer);
      }
    },
    changeBoxAlgorithmVersion() {
      //this.formatData.locationId = this.boxCheckList
      if (this.timer) {
        clearInterval(this.timer);
      }
      let that = this;
      this.timer = setInterval(function () {
        that.getBoxAndHistoryVersion();
      }, 3000);
      this.changeLoading = true;
      let obj = {
        boxIds: this.formatData.boxIds + "",
        fileName: this.formatData.fileName,
        algorithmId: this.id,
      };
      changeBoxAlgorithmVersion(obj)
        .then((res) => {
          this.changeLoading = false;
          if (res.code == 0) {
            this.$message.success(
              $t("applicationMonitoring.common.updateSuccess")
            );
            this.formatData.fileName = "";
            this.formatData.boxIds = [];
            // this.getBoxAndHistoryVersion();
          }
        })
        .catch(() => {
          this.changeLoading = false;
        });
      // let { code } = await changeBoxAlgorithmVersion({
      // boxIds:this.formatData.boxIds+"",
      // fileName:this.formatData.fileName,
      // algorithmId:this.id
      // });
      // if(code == 0){
      //   this.$message.success('更新成功')
      //   this.formatData.fileName = "";
      //   this.formatData.boxIds = [];
      //   this.getBoxAndHistoryVersion();
      // }
    },
    // 处理进度条
    handleProcess(local, total) {
      if (local && total) {
        let num = Number(((Number(local) / Number(total)) * 100).toFixed(2));
        return num > 100 ? Number("100.00") : num;
      } else {
        return 0;
      }
    },
    closeImport() {
      this.getBoxAndHistoryVersion();
    },
    // 选择文件
    handleSelectFile(platform, file_name) {
      this.$set(this.selectdFiles, platform, file_name);
      this.selectdFiles = { ...this.selectdFiles, [platform]: file_name };
    },
    // 是否可选
    getIsSelectable() {
      return this.isSelectable; // 返回 false 禁用所有复选框
    },
    // 执行模型切换
    handleSwitchModel(item) {
      this.isSwitching = true;
      // 判断是否选中盒子
      const selected = this.selectedBoxs[item].length;
      if (selected == 0) {
        this.isSwitching = false;
        this.$message.error(this.$t("modeltesting.algorithmupgrade.4hl2b4"));
        return;
      }
      // 判断是否选中算法文件
      if (this.selectdFiles[item] == "") {
        this.isSwitching = false;
        this.$message.error(this.$t("modeltesting.algorithmupgrade.1l77p8"));
        return;
      }
      // 弹窗确认操作
      this.$confirm(
        this.$t("modeltesting.algorithmupgrade.if7e8o"),
        $t("common.prompt"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          type: "warning",
        }
      )
        .then(() => {
          this.isSelectable = false;
          this.doSwitchModel(item);
        })
        .catch((err) => {
          this.isSelectable = true;
          this.isSwitching = false;
        });
    },
    async doSwitchModel(platform) {
      const datas = this.selectedBoxs[platform];
      this.switchCounts = { ...this.switchCounts, [platform]: 0 };
      for (const row of datas) {
        const swCount = this.switchCounts[platform];
        this.switchCounts = { ...this.switchCounts, [platform]: swCount + 1 };
        row["process"] = this.$t("modeltesting.algorithmupgrade.o4xt03");
        let params = {
          boxId: row.id,
          fileName: this.selectdFiles[platform],
          platform: platform,
          nameEn: this.nameEn,
        };
        const res = await switchModel(params);
        row["process"] = res.data.message;
      }

      this.isSelectable = true;
      this.isSwitching = false;
      this.$message.success(this.$t("modeltesting.algorithmupgrade.71787p"));
    },
    // 页面全部勾选
    handleSelection(selection, platform) {
      // 删除本页的选择
      const tableData = this.tableDatas[platform];
      tableData.forEach((item) => {
        let idx = this.selectedBoxs[platform].findIndex((it) => {
          return item.id == it.id;
        });

        if (idx > -1) {
          this.selectedBoxs[platform].splice(idx, 1);
        }
      });

      // 选中用户的勾选
      if (selection.length > 0) {
        selection.forEach((item) => {
          let idx = this.selectedBoxs[platform].findIndex((it) => {
            return item.id == it.id;
          });

          if (idx == -1) {
            this.selectedBoxs[platform].push({
              id: item.id,
              name: item.name,
              process: this.$t("modeltesting.algorithmupgrade.9ott7c"),
            });
          }
        });
      }

      // 更新选中盒子数量
      this.selectedCounts = {
        ...this.selectedCounts,
        [platform]: this.selectedBoxs[platform].length,
      };
    },
    // 查询盒子列表
    async fetchBoxs(platform) {
      const res = await listPage(this.queryParams[platform]);
      if (res.code == 0) {
        this.$set(this.tableTotals, platform, Number(res.count));
        this.tableDatas = { ...this.tableDatas, [platform]: res.data };

        const refName = `table-${platform}`;
        const refObj = this.$refs[refName];

        this.$nextTick(() => {
          this.tableDatas[platform].forEach((row) => {
            let idx = this.selectedBoxs[platform].findIndex((item) => {
              return item.id == row.id;
            });
            if (idx > -1) {
              refObj[0].toggleRowSelection(row, true); // 设置选中
            }
          });
        });
      }
    },
    async fetchLocalFiles(platform) {
      const res = await getLocalFiles({
        nameEn: this.nameEn,
        platform: platform,
      });
      if (res.code == 0) {
        this.fileLists[platform] = res.data;
      }
    },
    // 获取硬件平台列表
    fetchPlatform() {
      getPlatform().then((res) => {
        this.platformList = res.data;
        if (this.platformList && this.platformList.length > 0) {
          this.platformList.forEach((item) => {
            this.selectdFiles[item] = "";
            this.tableTotals[item] = 0;
            this.fileLists[item] = [];
            this.tableDatas[item] = [];
            this.selectedBoxs[item] = [];
            this.selectedCounts[item] = 0;
            this.switchCounts[item] = 0;
            this.queryParams[item] = { page: 1, limit: 10, platform: item };

            // 获取文件列表
            this.fetchLocalFiles(item);

            // 获取盒子设备列表
            this.fetchBoxs(item);
          });
        }
      });
    },
    // 分页处理
    handlePageChange(platform, event) {
      this.queryParams[platform]["page"] = event;
      this.fetchBoxs(platform);
    },
    // 清空选择的盒子设备
    handleClearBoxs(platform) {
      this.selectedBoxs = { ...this.selectedBoxs, [platform]: [] };
      this.selectedCounts[platform] = 0;
      this.switchCounts = { ...this.switchCounts, [platform]: 0 };

      const refName = `table-${platform}`;
      const refObj = this.$refs[refName];

      this.$nextTick(() => {
        this.tableDatas[platform].forEach((row) => {
          refObj[0].toggleRowSelection(row, false);
        });
      });
    },
  },
};
</script>
<style scoped>
.item {
  padding: 10px 0;
  font-size: 13px;
  color: #666;
}
.title {
  font-size: 13px;
  font-weight: bold;
  margin-bottom: 10px;
}
.radio-sty {
  display: flex;
  margin-bottom: 5px;
}
.clear-flex {
  display: flex;
  justify-content: space-between;
}
.alg {
  flex: 1;
  padding-left: 20px;
  text-align: right;
}
.box-card {
  margin-top: 10px;
}
</style>
<style lang="scss">
.percen-sty {
  .el-progress-bar {
    width: 92% !important;
    margin-right: -75px !important;
  }
  .el-progress__text {
    margin-left: 30px !important;
  }
}

::v-deep.el-dialog__body {
  padding-top: 0 !important;
}

.card-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.box-selected {
  margin: 0 15px;
}
.box-btn {
  float: right;
  padding: 3px 0;
  font-size: 13px;
}
.tit {
  color: #1A0808;
  font-size: 13px;
}
.file-list {
  margin: 0;
  padding: 0;
}
.file-list li {
  padding: 10px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.file-list li:last-child {
  border: 0;
}
.file-time {
  margin: 0 15px;
  color: #c0c4cc;
  font-size: 13px;
}
.file-selected {
  color: #67c23a;
}
.file-unselect {
  color: #c0c4cc;
}
</style>
