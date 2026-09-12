<template>
  <div class="equipment-cont">
    <div>
      <el-form :inline="true" :model="params">
        <el-form-item :label="$t('gbmanagement.channellist.1xidkr')">
          <el-select
            :placeholder="$t('common.chooseText')"
            clearable
            filterable
            v-model="params.boxStatus"
            style="width: 150px"
          >
            <el-option value="0" :label="$t('gbmanagement.channellist.m4l2uq')"></el-option>
            <el-option value="1" :label="$t('gbmanagement.channellist.xm4x1o')"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('gbmanagement.channellist.4gff70')">
          <el-select
            :placeholder="$t('common.chooseText')"
            clearable
            filterable
            v-model="params.cameraStatus"
            style="width: 150px"
          >
            <el-option value="0" :label="$t('videobox.index.96pi56')"></el-option>
            <el-option value="1" :label="$t('gbmanagement.channellist.10t445')"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">{{
            $t("button.queryText")
          }}</el-button>
        </el-form-item>
      </el-form>
    </div>
    <Tables
      :pagination="pagination"
      :columns="columns"
      :dataSource="dataSource"
      :loading="loading"
      @pageChange="pageChange"
      :rowSelection="rowSelection"
      :selections.sync="selectedRowKeys"
      rowKey="id"
    >
      <div slot="header-btn" style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAssignVisible"
          >{{$t('gbmanagement.channellist.3wo8k8')}}</el-button
        >
      </div>

      <div slot="status" slot-scope="{ row }">
        <el-tag :type="row.status == '1' ? 'success' : 'danger'">
          {{ row.status == "1" ? $t('common.online') : $t('common.offline') }}
        </el-tag>
      </div>
      <div slot="hasAccess" slot-scope="{ row }">
        <el-tag :type="row.cameraId == 0 ? 'info' : 'success'">
          {{ row.cameraId == 0 ? $t("videobox.index.96pi56") : $t("gbmanagement.channellist.10t445") }}
        </el-tag>
      </div>
      <div slot="hasBox" slot-scope="{ row }">
        <el-tag :type="row.locationId == 0 ? 'info' : 'success'">
          {{ row.locationId == 0 ? $t("gbmanagement.channellist.m4l2uq") : $t("gbmanagement.channellist.xm4x1o") }}
        </el-tag>
      </div>
      <div slot="operate" slot-scope="{ row }">
        <!-- <el-button type="text">播放</el-button> -->
        <template v-if="row.cameraId == 0">
          <el-button type="text" @click="insertFun(row)">{{$t('gbmanagement.channellist.85i3q2')}}</el-button>
          <el-button type="text" @click="channelStreamCheck(row)"
            >{{$t('gbmanagement.channellist.uq66xy')}}</el-button
          >
        </template>
        <template v-else>
          <el-button type="text" @click="channelStreamCheck(row)"
            >{{$t('gbmanagement.channellist.uq66xy')}}</el-button
          >
        </template>
      </div>
    </Tables>
    <!-- 接入 -->
    <AddCamera
      v-if="addVisible"
      :currentData="boxObj"
      :gbId="gbId"
      :gbName="gbName"
      :gbUrl="gbUrl"
      :gbMediaServerId="gbMediaServerId"
      pageType="channel"
      @close="(addVisible = false), getList()"
    />

    <!-- 分配盒子/服务器 -->
    <AssignForm
      v-if="assignVisible"
      :channelIds="channelIds"
      @closeAssign="closeAssign"
    />

    <!-- 分配盒子/服务器 -->
    <ChannelStreamCheck
      v-if="channelStreamCheckVisible"
      :currentId="checkChannelId"
      @closeChannelStreamCheck="closeChannelStreamCheck"
    />
  </div>
</template>
<script>
import { channelList, channelStreamUrl } from "./api";
import Tables from "@/components/Table/index.vue";
import AddCamera from "@/components/applicationMonitoring/boxManagement/addCamera/newAdd.vue";
import AssignForm from "./components/assignForm.vue";
import ChannelStreamCheck from "./components/channelStreamCheck.vue";
export default {
  components: {
    Tables,
    AddCamera,
    AssignForm,
    ChannelStreamCheck,
  },
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      loading: false,
      dataSource: [],
      columns: [
        {
          key: "deviceName",
          title: this.$t('gbmanagement.channellist.u74zh2'),
          align: "left",
        },
        {
          key: "deviceId",
          title: this.$t('gbmanagement.channellist.u74zh2'),
          align: "left",
        },
        {
          key: "channelId",
          title: this.$t('gbmanagement.channellist.hc6244'),
          align: "left",
        },
        {
          key: "name",
          title: this.$t('gbmanagement.channellist.h1tkc5'),
          align: "left",
        },
        {
          key: "manufacture",
          title: this.$t('gbmanagement.channellist.x87s32'),
          align: "left",
        },
        {
          key: "status",
          title: $t('common.presence'),
          align: "center",
          slot: "status",
        },
        {
          key: "hasAccess",
          title: this.$t('gbmanagement.channellist.4gff70'),
          align: "center",
          slot: "hasAccess",
        },
        {
          key: "hasBox",
          title: this.$t('gbmanagement.channellist.1xidkr'),
          align: "center",
          slot: "hasBox",
        },
        {
          key: "Base",
          title: $t("common.action", { text: "" }),
          align: "center",
          slot: "operate",
        },
      ],
      status: "",
      addVisible: false,
      channelObj: {},
      channelId: "",
      syncVisible: false,
      syncNum: 0,
      syncStatus: "",
      syncCount: 0,
      cameraOptions: [],
      params: {
        cameraStatus: "",
        boxStatus: "",
      },
      rowSelection: {
        type: "checkbox",
        change: (selectedRowKeys, selectedRows) => {
          this.selectedRowKeys = selectedRowKeys;
          this.selectedRows = selectedRows;
        },
      },
      selectedRows: [],
      selectedRowKeys: [],
      assignVisible: false,
      channelIds: [],
      boxObj: {},
      gbId: "",
      gbName: "",
      gbUrl: "",
      gbMediaServerId: "",
      channelStreamCheckVisible: false,
      checkChannelId: "",
    };
  },
  created() {
    this.getList();
  },
  beforeDestroy() {
    if (this.syncTimer) {
      clearInterval(this.syncTimer);
      this.syncTimer = null;
    }
  },
  methods: {
    // 获取数据
    getList() {
      this.loading = true;
      let formData = new FormData();
      formData.append("page", this.pagination.currentPage);
      formData.append("limit", this.pagination.pageSize);
      formData.append("cameraStatus", this.params.cameraStatus);
      formData.append("boxStatus", this.params.boxStatus);
      channelList(formData)
        .then((res) => {
          this.dataSource = res.data;
          this.pagination.total = parseInt(res.count);
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    // 分页查询
    pageChange(page, pageSize) {
      this.pagination.currentPage = page;
      this.pagination.pageSize = pageSize;
      this.getList();
    },
    // 查询
    search() {
      this.pagination.currentPage = 1;
      this.getList();
    },
    // 接入
    async insertFun(row) {
      if (row.locationId == 0) {
        this.$message.error(this.$t('gbmanagement.channellist.04ug65'));
        return;
      }

      const res = await channelStreamUrl({
        id: row.id,
      });
      const streamUrl = res.data.streamUrl || "";
      const mediaServerId = res.data.mediaServerId || "0";
      if (streamUrl == "") {
        this.$message.error(this.$t('gbmanagement.channellist.4ts966'));
        return;
      }

      this.boxObj = {
        id: row.locationId,
      };
      this.gbId = row.id;
      this.gbName = row.name + "@" + row.channelId;
      this.gbUrl = streamUrl;
      this.gbMediaServerId = mediaServerId;
      this.addVisible = true;
    },
    closed() {
      this.$emit("close");
    },
    // 分配盒子/服务器
    handleAssignVisible() {
      if (this.selectedRowKeys.length == 0) {
        this.$message.error(this.$t('gbmanagement.channellist.s4yi7q'));
        return;
      }
      this.channelIds = this.selectedRowKeys;
      this.assignVisible = true;
    },
    // 关闭分配盒子/服务器弹窗
    closeAssign() {
      this.assignVisible = false;
      this.selectedRowKeys = [];
      this.selectedRows = [];
      this.getList();
    },
    // 验证
    channelStreamCheck(row) {
      // if (row.locationId == 0) {
      //   this.$message.error("未分配盒子设备/服务器");
      //   return;
      // }
      this.checkChannelId = row.id;
      this.channelStreamCheckVisible = true;
    },
    // 关闭通道流验证弹窗
    closeChannelStreamCheck() {
      this.channelStreamCheckVisible = false;
      this.checkChannelId = "";
    },
  },
};
</script>
<style scoped lang="scss">
.head-container {
  background: #fff;
  padding: 16px 10px;
  :deep(.el-form-item--mini.el-form-item) {
    margin-bottom: 0px !important;
  }
}
</style>
