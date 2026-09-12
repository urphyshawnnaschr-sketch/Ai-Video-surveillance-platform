<template>
  <div>
    <Tables
      :pagination="pagination"
      :columns="columns"
      :dataSource="dataSource"
      :loading="loading"
      @pageChange="pageChange"
      :rowSelection="false"
      :selections.sync="selectedRowKeys"
    >
      <div
        slot="header"
        class="head-container"
        style="display: flex; justify-content: space-between; flex-wrap: wrap"
      >
        <div>
          <span class="headitem">
            <span class="headname">{{ $t("components.detail.8b4l8h") }}</span>
            <el-input
              style="width: 100px"
              v-model="formatData.name"
              placeholder=""
            ></el-input>
          </span>
          <span class="headitem">
            <span class="headname">{{ $t("login.phoneNumber") }}</span>
            <el-input
              style="width: 100px"
              v-model="formatData.tel"
              placeholder=""
            ></el-input>
          </span>
          <span class="headitem">
            <span class="headname">{{ $t("facemanagent.index.l66kah") }}</span>
            <el-select
              v-model="formatData.groupId"
              :placeholder="$t('form.tip.chooseGrouping')"
            >
              <el-option
                v-for="item in listGroup"
                v-if="item.id"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              >
              </el-option>
            </el-select>
          </span>

          <el-button class="pr10" type="primary" @click="getTable()">{{
            $t("button.searchText")
          }}</el-button>
          <el-button @click="reset()">{{
            $t("button.resetText", { text: "" })
          }}</el-button>
        </div>
        <!-- <el-form label-position="left">
              <el-form-item :label="$t('common.name')">
                
              </el-form-item>
              <el-form-item :label="$t('login.phoneNumber')">
                
              </el-form-item>
              <el-form-item label="Group">
                
              </el-form-item>
              
          </el-form> -->
        <div>
          <!-- <el-button
            class="pr10"
            type="primary"
            v-if="btnData.includes('face-add')"
            @click="addData()"
            >{{ $t("common.add", { text: $t("common.face") }) }}</el-button
          >
          <el-button
            class="pr10"
            type="primary"
            v-if="btnData.includes('face-group')"
            @click="grapDetailVisible = true"
            >{{ $t("faceControl.faceRecognition.groupManagement") }}</el-button
          >
          <el-button
            class="pr10"
            @click="uploadVisible = true"
            v-if="btnData.includes('face-batch-upload')"
            >{{ $t("faceControl.faceRecognition.batchUpload") }}</el-button
          > -->
          <el-button-group>
            <el-button
              class="pr10"
              type="primary"
              v-if="btnData.includes('face-add')"
              @click="addData()"
              >{{ $t("common.add", { text: $t("common.face") }) }}</el-button
            >
            <el-button
              class="pr10"
              v-if="btnData.includes('face-group')"
              @click="grapDetailVisible = true"
              >{{
                $t("faceControl.faceRecognition.groupManagement")
              }}</el-button
            >
            <el-dropdown style="margin-left: 1px;" @command="handleCommand">
              <el-button >{{$t('applicationMonitoring.alarmmanagement.m120pn')}}<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <!-- <el-dropdown-item command="a">人脸服务配置</el-dropdown-item> -->
                <el-dropdown-item command="b">{{$t('components.facemap.iyxqhk')}}</el-dropdown-item>
                <!-- <el-dropdown-item command="c">同步人脸库</el-dropdown-item> -->
                <el-dropdown-item command="d" v-if="btnData.includes('face-batch-upload')">{{ $t("faceControl.faceRecognition.batchUpload") }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-button-group>
        </div>
      </div>
      <div slot="operate" slot-scope="{ row }">
        <el-button
          type="text"
          v-if="btnData.includes('face-edit')"
          @click="editData(row)"
          >{{ $t("common.edit", { text: "" }) }}</el-button
        >
        <el-button
          type="text"
          v-if="btnData.includes('face-view')"
          @click="seeData(row)"
          >{{ $t("button.viewText", { text: "" }) }}</el-button
        >
        <el-button
          type="text"
          style="color: #dd383e !important"
          v-if="btnData.includes('face-detele')"
          @click="delData(row.id)"
          >{{ $t("button.deleteText", { text: "" }) }}</el-button
        >
      </div>
      <template slot="index" slot-scope="{ $index }">
        {{ $index + 1 }}
      </template>
      <template slot="avatar" slot-scope="{ row }">
        <el-image
          style="height: 100px"
          :src="getFaceAvatarImageUrl(row.id)"
        ></el-image>
      </template>
    </Tables>
    <detail
      v-if="detailVisible"
      :currentItme="currentItme"
      :currentState="currentState"
      :visible.sync="detailVisible"
      @close="close()"
    ></detail>
    <grapDetailVue
      v-if="grapDetailVisible"
      :visible.sync="grapDetailVisible"
      @close="close()"
    ></grapDetailVue>
    <upload
      v-if="uploadVisible"
      :visible.sync="uploadVisible"
      @close="close()"
    ></upload>
    <FaceMap v-if="mapVisible" @close="mapClose" />
  </div>
</template>
<script>
import Cookies from "js-cookie";
import Tables from "@/components/Table/index.vue";
import detail from "./components/detail.vue";
import { listPage, delect, listPageDGroup } from "./api";
import { getMyDate } from "@/utils/common.js";
import grapDetailVue from "./components/grapDetail.vue";
import upload from "./components/upload.vue";
import FaceMap from "../components/faceMap.vue";
export default {
  data() {
    return {
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
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
          key: "avatar",
          title: $t("common.number"),
          align: "center",
          slot: "index",
        },
        {
          key: "avatar",
          title: $t("common.avatar"),
          align: "center",
          slot: "avatar",
        },
        {
          key: "name",
          title: $t("common.name"),
          align: "center",
        },
        {
          key: "tel",
          title: $t("login.phoneNumber"),
          align: "center",
        },
        {
          key: "remark",
          title: $t("common.remark"),
          align: "center",
          render(h, { value }) {
            let str = value;
            if (value == "undefined") {
              str = "";
            }
            return h("span", [str]);
          },
        },
        {
          key: "faceGroup",
          title: $t("faceControl.faceRecognition.belongingGroup"),
          align: "center",
          render(h, { value }) {
            return h("span", [value.name]);
          },
        },
        {
          key: "Base",
          title: $t("common.action", { text: "" }),
          align: "center",
          flex: "right",
          slot: "operate",
        },
      ]),
      formatData: {},
      detailVisible: false,
      grapDetailVisible: false,
      uploadVisible: false,
      currentState: "",
      currentItme: {},
      listGroup: [],
      btnData: [],
      btnObjList: [],
      mapVisible: false,
    };
  },
  components: {
    Tables,
    detail,
    grapDetailVue,
    upload,
    FaceMap,
  },
  created() {
    this.getBtn();
    this.getTable();
    this.listPageDGroup();
  },
  methods: {
    getBtn() {
      this.btnData = [];
      this.btnObjList = [];
      this.isDetail = false;
      const menuArr = JSON.parse(sessionStorage.getItem("menuTree"));
      let newArr = [];
      this.getbtnList(menuArr);
      this.btnObjList.filter((item, index) => {
        newArr.push(item.auth);
      });
      this.btnData = newArr;
    },
    getbtnList(data) {
      let arr = [];
      data.forEach((item) => {
        if (item.path == this.$route.path) {
          arr = item.children.filter((items, ind) => {
            return items.type == 2;
          });
          this.btnObjList = arr;
        } else {
          this.getbtnList(item.children);
        }
      });
    },
    async listPageDGroup() {
      const { data, count } = await listPageDGroup();
      this.listGroup = data;
    },
    pageChange(page, pageSize) {
      this.pagination.currentPage = page;
      this.pagination.pageSize = pageSize;
      this.getTable();
    },
    async getTable() {
      this.formatData = {
        ...this.formatData,
        limit: this.pagination.pageSize,
        page: this.pagination.currentPage,
      };
      this.loading = true;
      const { data, count } = await listPage(this.formatData);
      this.dataSource = data;
      this.pagination.total = parseInt(count);
      this.loading = false;
    },
    reset() {
      this.pagination.currentPage = 1;
      this.formatData = {};
      this.getTable();
    },
    addData() {
      this.currentItme = {};
      this.currentState = "add";
      this.detailVisible = true;
    },
    editData(item) {
      this.currentState = "edit";
      this.currentItme = item;
      this.detailVisible = true;
    },
    seeData(item) {
      this.currentState = "see";
      this.currentItme = item;
      this.detailVisible = true;
    },
    async delData(id) {
      this.$confirm(this.$t("facemanagent.index.2m5f3g"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const { code } = await delect({ id: id });
        if (code == 0) {
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
          this.getTable();
        }
      });
    },
    close() {
      this.getTable();
    },
    // 更多
    handleCommand(e) {
      if (e == "a") {
      }
      if (e == "b") {
        this.mapVisible = true;
      }
      if (e == "c") {
      }
      if(e == 'd'){
        this.uploadVisible = true;
      }
    },
    mapClose() {
      this.mapVisible = false;
    },
    // 人脸头像
    getFaceAvatarImageUrl(id) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/face/image/avatar?userId=${id}&X-Token=${token}`;
    },
  },
};
</script>
<style scoped lang="scss">
.pr10 {
  padding-right: 10px;
}
.mt10 {
  margin-top: 10px;
}
.telemetry {
  padding: 10px;
}
.label {
  font-size: 12px;
  color: #666;
}
.card {
  background: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  font-size: 13px;
  border-radius: 10px;
  overflow: hidden;
  .card-header {
    padding: 10px;
    border-bottom: 1px solid #ebeef5;
    box-sizing: border-box;
  }
  .card-content {
    padding: 10px;
  }
}
::v-deep .el-form-item {
  margin-bottom: 0px;
}
.headname {
  font-size: 13px;
  color: #666;
  padding-right: 10px;
}
.headitem {
  padding-right: 64px;
}
</style>
