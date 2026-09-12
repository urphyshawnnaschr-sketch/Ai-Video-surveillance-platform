<template>
  <div>
    <div class="flex">
      <div class="flex-tree">
        <el-tree :data="treeData" :props="defaultProps">
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <span @click="handleNodeClick(data)">{{ node.label }}</span>
            <span style="margin-left: 10px" @click="openTreeMenu(data)">
              <i class="el-icon-setting"></i>
            </span>
          </span>
        </el-tree>
      </div>
      <div class="flex-right">
        <div class="head-container">
          <el-input
            v-model="params.name"
            :placeholder="$t('common.cameraName')"
            style="width: 200px; margin-right: 10px"
          ></el-input>
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="getListData"
            >{{ $t("button.queryText") }}</el-button
          >
          <el-button icon="el-icon-refresh" @click="refreshData">{{
            $t("button.resetText", { text: "" })
          }}</el-button>
        </div>
        <div class="ai_table">
          <el-table
            :data="tableData"
            border
            style="width: 100%"
            v-loading="loading"
          >
            <el-table-column
              align="center"
              :label="$t('common.liveMap')"
              width="110"
            >
              <template slot-scope="scope">
                <el-image
                  style="width: 80px; margin-top: 5px"
                  :src="$common.handleCameraImgUrl(scope.row.fileName)"
                  :preview-src-list="[
                    $common.handleCameraImgUrl(scope.row.fileName),
                  ]"
                >
                </el-image>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="name"
              :label="$t('common.cameraName')"
            >
            </el-table-column>
            <el-table-column
              align="center"
              prop="locationName"
              :label="
                $t('applicationMonitoring.incrementalRraining.belongingRegion')
              "
            >
            </el-table-column>
            <!-- <el-table-column align="center" prop="intervalTime" label="流类型">
              <template slot-scope="scope">
                <el-dropdown style="margin-left: 10px">
                  <span class="el-dropdown-link">
                    <span v-if="scope.row.rtspType == 0">Real-time</span>
                    <span v-if="scope.row.rtspType == 1">Backup</span>
                    <span v-if="scope.row.rtspType == 2">Image</span>
                    <i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      @click.native="switchRtspType(0, scope.row)"
                      >Real-time video URL</el-dropdown-item
                    >
                    <el-dropdown-item
                      @click.native="switchRtspType(1, scope.row)"
                      >Backup video URL</el-dropdown-item
                    >
                    <el-dropdown-item
                      @click.native="switchRtspType(2, scope.row)"
                      >Image URL</el-dropdown-item
                    >
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column> -->
            <!-- <el-table-column
              align="center"
              prop="alarmInterval"
              :label="$t('common.alarmInterval')"
            >
            </el-table-column> -->
            <el-table-column
              align="center"
              prop="intervalTime"
              :label="$t('applicationMonitoring.incrementalRraining.inference')"
            >
            </el-table-column>
            <el-table-column
              align="center"
              prop="algorithmNames"
              :label="$t('common.associationAlgorithm')"
            >
            </el-table-column>
            <el-table-column
              align="center"
              :label="$t('applicationMonitoring.common.configuration')"
            >
              <template slot-scope="scope">
                <el-button type="text" @click="configure(scope.row)">{{
                  $t("common.config")
                }}</el-button>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              :label="$t('applicationMonitoring.incrementalRraining.runState')"
            >
              <template slot-scope="scope">
                <el-switch
                  v-model="scope.row.running"
                  @change="switchRunning(scope.row)"
                  :active-value="1"
                  :inactive-value="0"
                ></el-switch>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              :label="$t('common.action', { text: '' })"
              width="160"
            >
              <template slot-scope="scope">
                <el-button type="text" @click="editData(scope.row)">{{
                  $t("common.edit", { text: "" })
                }}</el-button>
                <el-button
                  type="text"
                  class="danger"
                  @click="deleteData(scope.row)"
                  >{{ $t("button.deleteText", { text: "" }) }}</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination">
            <el-pagination
              background
              :current-page="params.page"
              :page-size="params.limit"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @current-change="handleCurrentChange"
              @size-change="handleSizeChange"
            ></el-pagination>
          </div>
        </div>
      </div>
    </div>
    <div v-show="showTreeMenu" class="treeMenu">
      <div @click="addRegion">
        {{ $t("common.edit", { text: $t("common.subRegions") }) }}
      </div>
      <div v-if="isDelete" @click="deleteRegion">
        {{ $t("applicationMonitoring.common.deleteArea") }}
      </div>
      <div @click="editRegion">
        {{ $t("common.edit", { text: $t("common.thisRegions") }) }}
      </div>
      <div @click="addCamera">
        {{ $t("applicationMonitoring.incrementalRraining.createCamera") }}
      </div>
    </div>
    <ConfigureTime
      :currentId="currentId"
      v-if="configureTimeVisible"
      @close="(configureTimeVisible = false), getListData()"
    />
    <AddCamera
      :currentId="currentId"
      :currentData="currentNode"
      v-if="addCameraVisible"
      @close="(addCameraVisible = false), getListData()"
    />
    <AddRegion
      :currentData="currentNode"
      :type="addRegionType"
      v-if="addRegionVisible"
      @success="(addRegionVisible = false), getTreeData()"
      @close="addRegionVisible = false"
    />
  </div>
</template>
<script>
import {
  getTreeData,
  deleteRegion,
  getListData,
  switchRtspType,
  switchRunning,
  deleteData,
} from "@/api/applicationMonitoring/cameraManagement";
import ConfigureTime from "@/components/applicationMonitoring/cameraManagement/configureTime";
import AddCamera from "@/components/applicationMonitoring/cameraManagement/addCamera";
import AddRegion from "@/components/applicationMonitoring/cameraManagement/addRegion";
export default {
  components: {
    ConfigureTime,
    AddCamera,
    AddRegion,
  },
  data() {
    return {
      configureTimeVisible: false,
      addCameraVisible: false,
      addRegionVisible: false,
      loading: false,
      showTreeMenu: false,
      isDelete: false,
      treeData: [],
      tableData: [],
      currentId: "",
      currentNode: {},
      addRegionType: "add",
      params: {
        locationType: 1,
        name: "",
        locationId: "",
        limit: 10,
        page: 1,
      },
      total: 0,
      defaultProps: {
        children: "children",
        label: "text",
      },
    };
  },
  created() {
    this.getTreeData();
    this.getListData();
  },
  methods: {
    // 获取位置
    async getTreeData() {
      const data = await getTreeData({ locationType: 1 });
      this.treeData = data.data;
    },
    // 获取摄像头列表
    async getListData() {
      this.loading = true;
      const data = await getListData(this.params);
      this.tableData = data.data;
      this.total = Number(data.count);
      this.loading = false;
    },
    // 重置
    refreshData() {
      this.currentNode = {};
      Object.assign(this.params, {
        locationType: 1,
        name: "",
        locationId: "",
        limit: 10,
        page: 1,
      });
      this.getListData();
    },
    // 新增区域
    addRegion() {
      this.addRegionType = "add";
      this.addRegionVisible = true;
    },
    // 编辑区域
    editRegion() {
      this.addRegionType = "edit";
      this.addRegionVisible = true;
    },
    // 删除区域
    deleteRegion() {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const res = await deleteRegion({ id: this.currentNode.meId });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getTreeData();
          }
        })
        .catch(() => {});
    },
    // 新增摄像头
    addCamera() {
      this.currentId = "";
      this.addCameraVisible = true;
    },
    // 编辑摄像头
    editData(item) {
      this.currentId = item.id;
      this.addCameraVisible = true;
    },
    // 切换流类型
    async switchRtspType(val, item) {
      const params = {
        id: item.id,
        rtspType: val,
      };
      await switchRtspType(params);
      this.getListData();
    },
    // 配置
    configure(item) {
      this.currentId = item.id;
      this.configureTimeVisible = item;
    },
    // 切换运行状态
    async switchRunning(item) {
      await switchRunning({ id: item.id });
      this.loading = true;
      setTimeout(() => {
        this.getListData();
      }, 500);
    },
    // 删除算法
    async deleteData(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const res = await deleteData({ id: item.id });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getListData();
          }
        })
        .catch(() => {});
    },
    // 点击节点
    handleNodeClick(node) {
      Object.assign(this.params, {
        locationId: node.meId,
      });
      this.getListData();
    },
    openTreeMenu(data) {
      this.showTreeMenu = true; // 显示菜单
      this.currentNode = data; // 存储数据
      if (data.parent == "#") {
        this.isDelete = false;
      } else {
        this.isDelete = true;
      }
      document
        .querySelector(".treeMenu")
        .setAttribute(
          "style",
          `top:${event.clientY}px;left:${event.clientX}px;`
        );
      document.addEventListener("click", this.closeTreeMenu);
      document.addEventListener("contextmenu", this.closeTreeMenu);
    },
    closeTreeMenu() {
      this.showTreeMenu = false; // 关闭菜单
      document.removeEventListener("click", this.closeTreeMenu);
      document.removeEventListener("contextmenu", this.closeTreeMenu);
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getListData();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getListData();
    },
    // 关闭弹窗回调
    close() {
      this.configureTimeVisible = false;
      this.addCameraVisible = false;
      this.getListData();
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
    .custom-tree-node {
      font-size: 13px;
    }
  }
  .flex-right {
    flex: 1;
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
</style>
