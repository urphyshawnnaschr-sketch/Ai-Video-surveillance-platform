<template>
  <div class="organizational">
    <div class="flex-left">
      <div class="top-title">
        <div class="title">{{ $t("groupview.index.msu2n0") }}</div>
        <el-button
          type="primary"
          v-if="!isShow"
          icon="el-icon-s-tools"
          @click="editFun"
          >{{ $t("faceControl.faceRecognition.groupManagement") }}</el-button
        >
        <el-button type="primary" v-else @click="saveFun">{{
          $t("button.saveText", { text: "" })
        }}</el-button>
      </div>
      <div class="tree-cont">
        <el-input
          :placeholder="$t('groupview.index.b9nt64')"
          v-model="filterText"
          style="margin-bottom: 10px"
          suffix-icon="el-icon-search"
        ></el-input>
        <div
          class="top-title top-hover"
          :style="{ backgroundColor: isDefault ? '#F5F7FA' : '' }"
          @click="allClick()"
        >
          <div>{{ $t("common.allText") }}</div>
          <div
            class="btn-sty"
            style="margin-top: 0px !important"
            v-if="isShow"
            @click="addGroup(0)"
          >
            {{ $t("addproject.step2.459446") }}
          </div>
        </div>
        <el-tree
          class="filter-tree"
          :data="treeData"
          :props="defaultProps"
          default-expand-all
          :filter-node-method="filterNode"
          ref="tree"
        >
          <div
            class="custom-tree-node"
            slot-scope="{ node, data }"
            style="width: 100%; line-height: 26px"
          >
            <div
              @click.stop="handleNodeClick(data)"
              class="label-color"
              style="width: 100%"
            >
              {{ node.label }}
            </div>
            <div
              class="btn-sty"
              style="margin-top: 0px !important"
              v-if="isShow"
            >
              <span class="mr-10" @click.stop="addGroup(1, data)">{{
                $t("addproject.step2.459446")
              }}</span>
              <span class="mr-10" @click.stop="editGroup(data)">{{
                $t("annotationplatform.annotationgroupmanagement.5lbf3q")
              }}</span>
              <span style="color: red" @click.stop="delFun(data)">{{
                $t("annotationplatform.annotationgroupmanagement.5t6l7l")
              }}</span>
            </div>
          </div>
        </el-tree>
        <div
          class="top-title top-hover"
          style="color: #1A0808; font-size: 12px"
          :style="{ backgroundColor: isUnselect ? '#F5F7FA' : '' }"
          @click="unSelectClick()"
        >
          <div>{{ $t("groupview.index.43775b") }}</div>
        </div>
      </div>
    </div>
    <div class="flex-right">
      <div class="flex-item" style="justify-content: space-between">
        <div class="flex-item" v-if="params.groupId">
          <div class="user-title">{{ groupTitle }}</div>
          <el-button
            class="button-contain-icon"
            type="primary"
            @click="gruopFun()"
            ><img
              class="botton-icon"
              src="@/assets/images/iconCamera.png"
              alt=""
            />{{ $t("components.groupmanagement.51q123") }}</el-button
          >
        </div>
        <div class="count-flex">
          <div>
            <span class="count-title">{{
              $t("applicationMonitoring.common.operating")
            }}</span>
            <span class="num"
              >{{ staticsObj.countRun }}/{{ staticsObj.countAll }}</span
            >
          </div>
          <div>
            <span class="count-title">{{
              $t("applicationMonitoring.common.closeRoute")
            }}</span>
            <span class="num"
              >{{ staticsObj.countClose }}/{{ staticsObj.countAll }}</span
            >
          </div>
          <div>
            <span class="count-title">{{
              $t("applicationMonitoring.common.abnormalRoute")
            }}</span>
            <span class="num">{{ staticsObj.countExp }}</span>
          </div>
        </div>
        <div>
          <el-input
            v-model="params.name"
            :placeholder="$t('common.cameraName')"
            clearable
            style="width: 200px; margin-right: 10px"
            @change="getTable"
          ></el-input>
        </div>
      </div>
      <div style="margin-top: 20px">
        <el-table
          :data="tableData"
          border
          style="width: 100%"
          v-loading="loading"
        >
          <el-table-column
            align="center"
            :label="$t('common.liveMap')"
            width="90"
          >
            <template slot-scope="scope" style="padding: 0px !important">
              <el-image
                v-if="scope.row.filename && scope.row.isShowImg"
                style="width: 100%; margin-top: 5px"
                :src="$common.handleCameraImgUrl(scope.row.filename)"
                :preview-src-list="[
                  $common.handleCameraImgUrl(scope.row.filename),
                ]"
                @error="handleImageError(scope.$index)"
              >
              </el-image>
              <img
                v-else
                src="@/assets/images/no-img.png"
                style="width: 100%; margin-top: 5px"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="name"
            :label="$t('groupview.index.30617c')"
          ></el-table-column>
          <el-table-column
            align="center"
            prop="videoCodec"
            :label="$t('groupview.index.x2c1a3')"
          ></el-table-column>
          <el-table-column
            align="center"
            prop="videoWidth"
            :label="$t('groupview.index.x2c1a4')"
          >
            <template slot-scope="scope">
              {{ scope.row.videoWidth }}x{{ scope.row.videoHeight }}
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="boxName"
            :label="$t('groupview.index.t8883x')"
          ></el-table-column>
          <el-table-column
            align="center"
            prop="groupName"
            :label="$t('faceControl.faceRecognition.belongingGroup')"
          ></el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.associationAlgorithm')"
          >
            <template slot-scope="scope">
              <div v-for="(item, index) in scope.row.algorithmNames">
                {{ item }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('applicationMonitoring.incrementalRraining.runState')"
          >
            <template slot-scope="scope">
              <el-tag v-if="scope.row.status == 0" type="info">{{
                $t("applicationMonitoring.common.notRunning")
              }}</el-tag>
              <el-tag v-if="scope.row.status == 1" type="success">{{
                scope.row.execMsg
              }}</el-tag>
              <el-tag v-if="scope.row.status == 2" type="error">{{
                scope.row.execMsg
              }}</el-tag>
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
    <!-- 新增部门 -->
    <AddGroup
      v-if="groupAddVisible"
      :parentId="parentId"
      :currentId="currentId"
      @close="closeGroup"
    />
    <!-- 组内管理 -->
    <GroupManagement
      v-if="groupVisible"
      :groupId="params.groupId"
      @close="closeHandle"
    ></GroupManagement>
  </div>
</template>
<script>
import Tables from "@/components/Table/index.vue";
import AddGroup from "./components/addGroup.vue";
import GroupManagement from "./components/groupManagement.vue";
import { groupTree, groupDelete, groupCameraList, cameraStatics } from "./api";
export default {
  components: {
    Tables,
    AddGroup,
    GroupManagement,
  },
  data() {
    return {
      isUnselect: false,
      groupAddVisible: false,
      filterText: "",
      treeData: [],
      staticsList: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
      isShow: false, //组织按钮是否展示
      isDefault: true, //默认展示全部
      isOpen: false,
      groupList: [],
      parentId: "",
      currentId: "",
      groupTitle: "",
      tableData: [],
      loading: false,
      params: {
        groupId: "",
        limit: 10,
        page: 1,
      },
      total: 0,
      groupId: "",
      groupVisible: false,
      staticsObj: {},
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  created() {
    this.getListTree();
    this.getTable();
    this.getStatics();
  },
  methods: {
    //获取分组树
    getListTree() {
      this.treeData = [];
      groupTree().then((res) => {
        if (res.data && res.data.length > 0) {
          this.treeData = res.data;
          this.groupList = this.getData(res.data);
        }
      });
    },
    getData(data) {
      data.forEach((item) => {
        if (item.children.length < 1) {
          item.children = undefined;
        } else {
          this.getData(item.children);
        }
      });
      return data;
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    // 编辑
    editFun() {
      this.isShow = true;
    },
    // 保存
    saveFun() {
      this.isShow = false;
    },
    // 点击节点
    handleNodeClick(node) {
      console.log(node);
      this.isDefault = false;
      this.isUnselect = false;
      this.params.page = 1;
      this.params.groupId = node.id;
      this.groupTitle = node.name;
      this.getTable();
      this.getStatics();
    },
    // 点击全部
    allClick() {
      this.isDefault = true;
      this.isUnselect = false;
      this.params.page = 1;
      this.params.groupId = "";
      this.groupTitle = "";
      this.getTable();
      this.getStatics();
    },
    // 点击未分组
    unSelectClick() {
      this.isUnselect = true;
      this.isDefault = false;
      this.params.page = 1;
      this.params.groupId = 0;
      this.groupTitle = this.$t("groupview.index.43775b");
      this.getTable();
      this.getStatics();
    },
    // 组织新增
    addGroup(type, data) {
      this.currentId = "";
      if (type == 1) {
        //点击节点
        this.parentId = data.id;
        this.groupAddVisible = true;
      } else {
        //点击全部节点
        this.parentId = "0";
        this.groupAddVisible = true;
      }
    },
    // 组织编辑
    editGroup(data) {
      this.parentId = "";
      this.currentId = data.id;
      this.groupAddVisible = true;
    },
    // 组织新增弹窗关闭
    closeGroup() {
      this.currentId = "";
      this.parentId = "";
      this.groupAddVisible = false;
      this.getListTree();
    },
    // 组织删除
    delFun(data) {
      this.$confirm(
        this.$t("groupview.index.2w4u21"),
        this.$t("groupview.index.5464tg"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          type: "warning",
        }
      )
        .then(async () => {
          const res = await groupDelete({ id: data.id });
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
          this.getListTree();
        })
        .catch(() => {});
    },
    // 获取摄像头
    async getTable() {
      this.loading = true;
      const data = await groupCameraList(this.params);
      this.tableData = data.data;
      if (this.tableData.length > 0) {
        this.tableData.forEach((item) => {
          item.isShowImg = true;
        });
      }
      this.total = Number(data.count);
      this.loading = false;
    },
    async getStatics() {
      // console.log('getStatics', this.params.groupId);
      const res = await cameraStatics(this.params);
      this.staticsObj = res.data;
    },
    // 图片识别失败
    handleImageError(index) {
      this.tableData[index].isShowImg = false;
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getTable();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getTable();
    },
    // 组内管理
    gruopFun() {
      this.groupVisible = true;
    },
    closeHandle() {
      this.groupVisible = false;
      this.getTable();
      this.getStatics();
    },
  },
};
</script>
<style scoped lang="scss">
.organizational {
  display: flex;
  .flex-left {
    width: 280px;
    border-radius: 8px;
    background: #fff;
    padding: 16px;
    margin: 0px 10px 0 0;
    flex-shrink: 0;
    .top-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;
      padding: 5px 10px;
    }
    .btn-sty {
      color: #E53935;
      cursor: pointer;
    }
    .top-hover:hover {
      background-color: #f5f7fa;
    }
    .title {
      font-size: 20px;
      font-weight: bold;
    }
    .tree-cont {
      margin-top: 16px;
    }
    .custom-tree-node {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .mr-10 {
      margin-right: 10px;
    }
  }
  .flex-right {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    flex: 1;
    .flex-item {
      display: flex;
      align-items: center;
    }
    .user-title {
      font-size: 20px;
      color: #1A0808;
      font-weight: bold;
      margin-right: 20px;
    }
    :deep(.button-contain-icon) {
      span {
        display: flex;
        align-items: center;
      }

      .botton-icon {
        width: 14px;
        height: 14px;
        margin-right: 5px;
      }
    }

    .seach-cont {
      padding-top: 16px;
    }
    .item-sty {
      text-align: center;
      color: #1A0808;
      font-size: 12px;
      margin-right: 20px;
      margin-bottom: 10px;
      .num {
        font-size: 20px;
        color: #303133;
        font-weight: bold;
        margin-top: 5px;
      }
    }
    .count-flex {
      display: flex;
      align-items: center;
      width: 40%;
      justify-content: space-between;
    }
    .count-title {
      font-size: 12px;
      color: #1A0808;
      margin-right: 5px;
    }
    .num {
      font-size: 20px;
      color: #303133;
    }
  }
  :deep(.table-container) {
    padding: 0px !important;
  }
}
</style>
