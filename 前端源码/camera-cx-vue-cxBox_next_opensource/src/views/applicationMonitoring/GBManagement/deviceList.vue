<template>
  <div>
    <div class="platform-info">
      <el-button type="primary" icon="el-icon-setting" @click="showPlatform">{{
        $t("components.platforminfo.1l632c")
      }}</el-button>
    </div>
    <div class="channel-cont">
      <Tables
        :pagination="pagination"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        @pageChange="pageChange"
        :rowSelection="false"
      >
        <div slot="onLine" slot-scope="{ row }">
          <el-tag :type="row.onLine == '1' ? 'success' : 'danger'">
            {{ row.onLine == "1" ? $t("common.online") : $t("common.offline") }}
          </el-tag>
        </div>

        <div slot="operate" slot-scope="{ row }">
          <!-- <el-button type="text" class="btn-edit" @click="editForm(row)"
            >Edit</el-button
          > -->
          <el-button
            type="text"
            class="btn-sync"
            @click="startDeviceSync(row)"
            >{{ $t("gbmanagement.devicelist.4vllze") }}</el-button
          >
          <!-- <el-button type="text" class="btn-del" @click="channelFun(row)"
            >{{ $t("button.deleteText", { text: "" }) }}</el-button
          > -->
          <!-- <el-button
            type="text"
            style="color: #67c23a !important"
            @click="startChannelSync(row)"
            >Sync Channel</el-button
          > -->
        </div>
      </Tables>
    </div>

    <!-- 通道 -->
    <ChannelManagement
      v-if="channelVisible"
      :currentId="currentId"
      @close="closeChannel"
    ></ChannelManagement>

    <!-- 配置信息 -->
    <ConfigInfo
      v-if="configVisible"
      :currentId="channelId"
      @close="configVisible = false"
    />

    <!-- 新增、编辑 -->
    <DeviceForm
      v-if="formVisible"
      :currentId="currentId"
      @closeForm="closeForm"
    ></DeviceForm>

    <el-dialog
      :title="$t('gbmanagement.devicelist.51bk39')"
      :visible.sync="syncVisible"
      width="240px"
      :before-close="handleSyncClose"
    >
      <span style="display: flex; justify-content: center">
        <el-progress
          type="circle"
          :percentage="syncNum"
          :status="syncStatus"
          :width="120"
        ></el-progress>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { deviceList, deviceSync, deviceSyncStatus } from "./api";
import Tables from "@/components/Table/index.vue";
import ChannelManagement from "./components/channel.vue";
import ConfigInfo from "./components/platformInfo.vue";
import DeviceForm from "./components/deviceForm.vue";
export default {
  components: {
    Tables,
    ChannelManagement,
    ConfigInfo,
    DeviceForm,
  },
  data() {
    return {
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      loading: false,
      dataSource: [],
      columns: Object.freeze([
        {
          key: "name",
          title: this.$t("components.grapdetail.8124u4"),
          align: "left",
        },
        {
          key: "deviceId",
          title: this.$t("gbmanagement.channellist.u74zh2"),
          align: "left",
          width: 180,
        },
        {
          key: "hostAddress",
          title: this.$t("addbox.index.11t97c"),
          align: "left",
        },
        {
          key: "manufacturer",
          title: this.$t("components.channel.402q56"),
          align: "left",
        },
        {
          key: "transport",
          title: this.$t("gbmanagement.devicelist.87hjqv"),
          align: "left",
        },
        {
          key: "streamMode",
          title: this.$t("gbmanagement.devicelist.133c11"),
          align: "left",
        },
        {
          key: "channelNums",
          title: this.$t("gbmanagement.devicelist.1ndx4b"),
          align: "center",
        },
        {
          key: "onLine",
          title: this.$t("components.channel.5x3p13"),
          align: "center",
          slot: "onLine",
        },
        {
          key: "registerTime",
          title: this.$t("gbmanagement.devicelist.tnm7i6"),
          align: "left",
        },
        {
          key: "keepaliveTime",
          title: this.$t("applicationMonitoring.boxManagement.c1rxbp"),
          align: "left",
        },
        {
          key: "Base",
          title: $t("common.action", { text: "" }),
          align: "center",
          slot: "operate",
        },
      ]),
      currentId: "",
      channelVisible: false,
      configVisible: false,
      syncTimer: null,
      syncVisible: false,
      syncNum: 0,
      syncStatus: "", // exception
      formVisible: false,
      currentId: null,
    };
  },
  created() {
    this.getList();
  },
  beforeDestroy() {
    if (this.syncTimer != null) {
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
      deviceList(formData)
        .then((res) => {
          this.dataSource = res.data;
          this.pagination.total = parseInt(res.count);
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    pageChange(page, pageSize) {
      this.pagination.currentPage = page;
      this.pagination.pageSize = pageSize;
      this.getList();
    },
    // 点击通道
    channelFun(row) {
      this.currentId = row.deviceId;
      this.channelVisible = true;
    },
    // 关闭通道弹窗
    closeChannel() {
      this.channelVisible = false;
      this.currentId = "";
      this.getList();
    },
    // 展示国标平台信息
    showPlatform() {
      this.channelId = "";
      this.configVisible = true;
    },
    // 通道同步
    startDeviceSync(row) {
      // 发送国标同步请求
      let formdata = new FormData();
      formdata.append("id", row.id);
      deviceSync(formdata)
        .then((res) => {
          this.syncVisible = true;
          this.syncStatus = "success";
          this.syncNum = 0;
          this.syncTimer = setInterval(() => {
            // 获取通道同步状态
            deviceSyncStatus(formdata).then((res) => {
              if (res.code == 0) {
                let data = res.data;
                if (data.code == 0) {
                  if (data.data.syncIng) {
                    if (this.syncNum + 10 < 100) {
                      this.syncNum = this.syncNum + 10;
                    }
                  } else {
                    this.syncNum = 100;
                    this.syncStatus = "success";

                    if (data.data.total > 0) {
                      this.$message.success(
                        this.$t("gbmanagement.devicelist.wq1c7l", [
                          data.data.total,
                        ])
                      );
                    } else {
                      this.syncStatus = "exception";
                      this.$message.error(data.data.errorMsg);
                    }

                    setTimeout(() => {
                      this.syncVisible = false;
                      this.syncNum = 0;
                      clearInterval(this.syncTimer);
                      this.syncTimer = null;
                    }, 1000);
                  }
                } else {
                  this.$message.error(data.msg);
                  this.syncNum = 100;
                  this.syncStatus = "exception";
                  setTimeout(() => {
                    this.syncVisible = false;
                    this.syncNum = 0;
                    clearInterval(this.syncTimer);
                    this.syncTimer = null;
                  }, 1000);
                }
              }
            });
          }, 3000);
          // this.syncVisible.close();
        })
        .catch(() => {
          this.syncVisible = false;
          this.syncNum = 0;
          clearInterval(this.syncTimer);
          this.syncTimer = null;
        });
    },
    handleSyncClose() {
      this.syncVisible = false;
      this.syncNum = 0;
      clearInterval(this.syncTimer);
      this.syncTimer = null;
    },
    // 编辑设备信息
    editForm(row) {
      this.currentId = row.id;
      this.formVisible = true;
    },
    // 关闭配置弹窗
    closeForm() {
      this.formVisible = false;
      this.currentId = null;
      this.getList();
    },
  },
};
</script>
<style scoped lang="scss">
.platform-info {
  margin-bottom: 10px;
}
.equipment-cont {
  background: #fff;
  border-radius: 6px;
  padding: 16px;
}
.btn-edit {
  color: #E53935 !important;
}
.btn-sync {
  color: #67c23a !important;
}
.btn-del {
  color: #dd383e !important;
}
</style>
