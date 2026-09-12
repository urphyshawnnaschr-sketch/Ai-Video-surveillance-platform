<template>
  <div>
    <div class="channel-cont">
      <Tables
        :pagination="pagination"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        @pageChange="pageChange"
        :rowSelection="false"
      >
        <div slot="header-btn" style="margin-bottom: 16px">
          <el-button type="primary" @click="editForm">{{
            $t("common.add", { text: $t("common.nodes") })
          }}</el-button>
        </div>
        <div slot="onLine" slot-scope="{ row }">
          <el-tag :type="row.onLine == '1' ? 'success' : 'danger'">
            {{ row.onLine == "1" ? $t("common.online") : $t("common.offline") }}
          </el-tag>
        </div>

        <div slot="operate" slot-scope="{ row }">
          <el-button type="text" class="btn-edit" @click="editForm(row)">{{
            $t("common.edit", { text: "" })
          }}</el-button>
          <el-button
            type="text"
            class="btn-sync"
            @click="checkMediaServer(row)"
            >{{ $t("common.test") }}</el-button
          >
          <el-button type="text" class="btn-del" @click="delData(row)">{{
            $t("button.deleteText", { text: "" })
          }}</el-button>
        </div>
      </Tables>
    </div>

    <!-- 新增、编辑 -->
    <FormModal
      v-if="formVisible"
      :currentId="currentId"
      @closeForm="closeForm"
    />
  </div>
</template>
<script>
import { mediaServerList, mediaServerDel, mediaServerCheck } from "./api";
import Tables from "@/components/Table/index.vue";
import FormModal from "./components/form.vue";
export default {
  components: {
    Tables,
    FormModal,
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
          title: this.$t("components.form.nqv7b4"),
          align: "left",
        },
        {
          key: "ip",
          title: this.$t("casketmanagement.index.f021j5"),
          align: "left",
          width: 180,
        },
        {
          key: "httpPort",
          title: this.$t("components.form.y5j625"),
          align: "left",
        },
        {
          key: "rtspPort",
          title: this.$t("components.form.zs45z4"),
          align: "left",
        },
        {
          key: "rtcPort",
          title: this.$t("components.form.76g178"),
          align: "left",
        },
        {
          key: "rtpPortRange",
          title: this.$t("components.form.q51n33"),
          align: "left",
        },
        {
          key: "sendRtpPortRange",
          title: this.$t("components.form.6tpmss"),
          align: "left",
        },
        {
          key: "secret",
          title: $t("common.password", { text: "" }),
          align: "left",
        },
        // {
        //   key: "nodeType",
        //   title: $t("components.form.y572uc", { text: "" }),
        //   align: "left",
        //   render(h, { value, row }) {
        //     console.log(row);
        //     let cc = row.cameraCount || 0;
        //     const str = { 
        //       0: `${$t("components.form.y572u1")} (${cc})`,
        //       1: `${$t("components.form.y572u2")} (${cc})`,
        //       2: `${$t("components.form.y572u3")} (${cc})`,
        //     }[value];
        //     return h("span", [str]);
        //   },
        // },
        {
          key: "Base",
          title: $t("common.action", { text: "" }),
          align: "center",
          slot: "operate",
        },
      ]),
      currentId: "",
      formVisible: false,
      currentId: null,
    };
  },
  created() {
    this.getMediaServerList();
  },
  methods: {
    // 获取数据
    getMediaServerList() {
      this.loading = true;
      let formData = new FormData();
      formData.append("page", this.pagination.currentPage);
      formData.append("limit", this.pagination.pageSize);
      mediaServerList(formData)
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
      this.getMediaServerList();
    },
    // 验证
    checkMediaServer(row) {
      this.loading = true;
      mediaServerCheck({ id: row.id })
        .then((res) => {
          this.loading = false;
          if (res.data) {
            this.$message.success(this.$t("mediaserver.index.234ld5"));
          } else {
            this.$message.error(this.$t("mediaserver.index.4q9u4b"));
          }
        })
        .catch((err) => {
          this.loading = false;
        });
    },
    // 关闭通道弹窗
    closeChannel() {
      this.channelVisible = false;
      this.currentId = "";
      this.getMediaServerList();
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
      this.getMediaServerList();
    },
    // 删除数据
    delData(row) {
      this.$confirm(this.$t("mediaserver.index.6p7abc"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await mediaServerDel({ id: row.id });
        if (res.code == 0) {
          this.$message.success(
            $t("common.action", { text: $t("common.success") })
          );
          this.getMediaServerList();
        }
      });
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
