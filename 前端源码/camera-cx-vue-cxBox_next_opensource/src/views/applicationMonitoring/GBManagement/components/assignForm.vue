<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('gbmanagement.channellist.3wo8k8')"
    :visible.sync="dialogVisible"
    width="50%"
    @close="closed"
    append-to-body
  >
    <div class="config-cont">
      <Tables
        :pagination="false"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        :rowSelection="false"
      >
        <div slot="operate" slot-scope="{ row }">
          <el-button type="text" @click="handleAssign(row)">{{$t('components.assignform.3f6978')}}</el-button>
        </div>
      </Tables>
    </div>
  </el-dialog>
</template>
<script>
import { boxAndChannelCount, channelAssign } from "../api";
import Tables from "@/components/Table/index.vue";
export default {
  components: {
    Tables,
  },
  props: {
    channelIds: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {
      dialogVisible: true,
      params: {},
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      loading: false,
      dataSource: [],
      columns: [
        {
          key: "name",
          title: this.$t('components.detail.fwu578'),
          align: "left",
        },
        {
          key: "channelCount",
          title: this.$t('components.assignform.hdovy3'),
          align: "center",
        },
        {
          key: "Base",
          title: $t("common.action", { text: "" }),
          align: "center",
          slot: "operate",
        },
      ],
    };
  },
  created() {
    this.getBoxAndChannelCount();
  },
  methods: {
    // 获取数据
    getBoxAndChannelCount() {
      this.loading = true;
      boxAndChannelCount()
        .then((res) => {
          this.dataSource = res.data;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    closed() {
      this.$emit("closeAssign");
    },
    // 分配盒子/服务器
    handleAssign(row) {
      this.$confirm(this.$t('components.assignform.k61g0c'), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const params = {
          channelIds: this.channelIds,
          boxId: row.id,
        };
        const res = await channelAssign(params);
        if (res.code == 0) {
          this.$message.success(
            $t("common.action", { text: $t("common.success") })
          );
          this.closed();
        }
      });
    },
  },
};
</script>
<style scoped lang="scss">
.bot-btn {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
