<template>
  <div class="sound-cont">
    <div class="mrB10">
      <el-button size="mini" type="primary" @click="addFun()">{{
        $t("common.add", { text: "" })
      }}</el-button>
    </div>
    <div class="table-cont">
      <Tables
        :pagination="pagination"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        @pageChange="pageChange"
        :rowSelection="false"
      >
        <div slot="index" slot-scope="{ $index }">
          {{ $index + 1 }}
        </div>
        <div slot="operate" slot-scope="{ row }">
          <el-button type="text" @click="editData(row)">{{
            $t("common.edit", { text: "" })
          }}</el-button>
          <el-button
            type="text"
            style="color: #dd383e !important"
            @click="delData(row.id)"
            >{{ $t("button.deleteText", { text: "" }) }}</el-button
          >
        </div>
      </Tables>
    </div>
    <!-- 新增、编辑 -->
    <AddInfo
      v-if="addVisible"
      :currentId="currentId"
      @closeAdd="closeAdd"
    ></AddInfo>
  </div>
</template>
<script>
import {
  listData,
  deleteData,
} from "@/api/applicationMonitoring/soundColumnManagement";
import Tables from "@/components/Table/index.vue";
import AddInfo from "./components/addInfo.vue";
export default {
  components: {
    Tables,
    AddInfo,
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
          title: $t("common.number"),
          align: "center",
          slot: "index",
          width: 60,
        },
        {
          key: "sn",
          title: this.$t("soundcolumnmanagement.index.108fkj"),
          align: "center",
        },
        {
          key: "server",
          title: this.$t("components.addinfo.7dpru2"),
          align: "center",
        },
        {
          key: "vol",
          title: this.$t("components.addinfo.5hw38w"),
          align: "center",
          render(h, { value }) {
            const obj = {
              20: this.$t("addcamera.newadd.284165"),
              40: this.$t("addcamera.newadd.sri461"),
              60: this.$t("addcamera.newadd.673fee"),
            };
            return h("span", [obj[value]]);
          },
        },
        {
          key: "Base",
          title: $t("common.action", { text: "" }),
          align: "center",
          slot: "operate",
        },
      ]),
      currentId: "",
      addVisible: false,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listData()
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
    // 新增
    addFun() {
      this.currentId = "";
      this.addVisible = true;
    },
    // 编辑
    editData(row) {
      this.currentId = row.id;
      this.addVisible = true;
    },
    // 关闭新增弹窗
    closeAdd() {
      this.addVisible = false;
      this.currentId = "";
      this.getList();
    },
    // 删除
    delData(id) {
      this.$confirm(this.$t("dingding.index.1253hf"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(() => {
          let formData = new FormData();
          formData.append("id", id);
          deleteData(formData).then((res) => {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            this.getList();
          });
        })
        .catch(() => {});
    },
  },
};
</script>
<style scoped lang="scss">
.sound-cont {
  background: #fff;
  border-radius: 6px;
  padding: 16px;
  .mrB10 {
    margin-bottom: 15px;
  }
}
</style>
