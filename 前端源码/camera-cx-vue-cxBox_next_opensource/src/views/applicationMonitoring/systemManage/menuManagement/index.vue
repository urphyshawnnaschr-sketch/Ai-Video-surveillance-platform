<template>
  <div>
    <div class="bg">
      <!-- <el-button
            type="primary"
            icon="el-icon-plus"
            @click="addData"
            style="margin-bottom: 10px;"
            >Add Menu</el-button
          > -->
      <el-table
        :data="dataSource"
        row-key="id"
        border
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column
          :label="$t('common.number')"
          prop="index"
          align="center"
          width="60"
        ></el-table-column>
        <el-table-column
          prop="name"
          align="center"
          :label="$t('components.grapdetail.8124u4')"
        ></el-table-column>
        <el-table-column
          prop="path"
          align="center"
          :label="$t('components.detail.qvoh18')"
        ></el-table-column>
        <el-table-column
          prop="filePath"
          align="center"
          :label="$t('components.detail.p0d682')"
        ></el-table-column>
        <el-table-column
          prop="type"
          align="center"
          :label="$t('components.detail.4187k4')"
          width="120"
        >
          <template slot-scope="scope">
            {{ getType(scope.row.type) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="auth"
          align="center"
          :label="$t('components.detail.f5b513')"
        ></el-table-column>
        <!-- <el-table-column :label="$t('common.action', {text: ''})" align="center" width="100">
          <template slot-scope="scope">
            <el-button type="text"  @click.stop="edit(scope.row)">Edit</el-button>
            <el-button type="text"  style="color: #DD383E !important" @click.stop="del(scope.row)">{{ $t("button.deleteText", { text: "" }) }}</el-button>
          </template>
        </el-table-column> -->
      </el-table>
    </div>
    <AddAccount
      :currentAccount="currentAccount"
      v-if="addAccountVisible"
      @close="(addAccountVisible = false), getTable()"
    />
  </div>
</template>
<script>
import Tables from "@/components/Table/index.vue";
import AddAccount from "./components/detail.vue";
import { listData, del } from "./api";
import { getMyDate } from "@/utils/common.js";
export default {
  data() {
    return {
      loading: false,
      dataSource: [],
      formatData: {},
      addAccountVisible: false,
    };
  },
  components: {
    Tables,
    AddAccount,
  },
  created() {
    this.getTable();
  },
  methods: {
    async getTable() {
      this.formatData = {
        ...this.formatData,
      };
      this.loading = true;
      const { data, count } = await listData(this.formatData);
      if (data && data.length > 0) {
        data.forEach((item, ind) => {
          item.index = ind + 1;
        });
      }
      this.dataSource = data;
      this.loading = false;
    },
    reset() {
      this.formatData = {};
      this.getTable();
    },
    // 获取类型
    getType(str) {
      let obj = {
        0: this.$t("components.detail.25c948"),
        1: this.$t("components.detail.fx5d38"),
        2: this.$t("facecompare.index copy.sdewnd"),
      };
      return obj[str];
    },
    // 新增账号
    addData() {
      this.currentAccount = null;
      this.addAccountVisible = true;
    },
    edit(row) {
      this.currentAccount = row;
      this.addAccountVisible = true;
    },
    del(row) {
      this.$confirm(
        this.$t("menumanagement.index.351238"),
        $t("common.prompt"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          type: "warning",
        }
      )
        .then(() => {
          del({ id: row.id })
            .then((res) => {
              if (res.code == 0) {
                this.$message.success(
                  $t("button.deleteText", { text: $t("common.success") })
                );
                this.getTable();
              }
            })
            .catch((res) => {});
        })
        .catch(() => {});
    },
  },
};
</script>
<style scoped lang="scss">
.bg {
  background: #fff;
  padding: 10px 16px;
  border-radius: 6px;
}
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
  margin-bottom: 20px;
}
</style>
