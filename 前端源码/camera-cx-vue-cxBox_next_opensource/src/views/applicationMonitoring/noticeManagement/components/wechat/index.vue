<template>
  <div>
    <div style="padding: 0 10px">
      <el-button type="primary" @click="formAdd">{{
        $t("common.add", { text: $t("common.pushGroup") })
      }}</el-button>
    </div>
    <div>
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
        <div slot="switchState" slot-scope="{ row }">
          <el-switch
            v-model="row.state"
            @change="(e) => doSwitchState(e, row)"
            :active-value="1"
            :inactive-value="0"
            active-color="#13ce66"
            inactive-color="#ff4949"
          ></el-switch>
        </div>

        <div slot="operate" slot-scope="{ row }">
          <el-button type="text" @click="formEdit(row)">{{
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

    <!-- 表单 -->
    <Form
      v-if="formVisible"
      :currentId="currentId"
      @closeAdd="formClose"
    ></Form>
  </div>
</template>

<script>
import { socialHookList, socialHookDel, socialHookSwitch } from "../../api.js";
import Tables from "@/components/Table/index.vue";
import Form from "./form.vue";
export default {
  components: {
    Tables,
    Form,
  },
  data() {
    return {
      pagination: false,
      loading: false,
      dataSource: [],
      columns: Object.freeze([
        {
          key: "name",
          title: this.$t("dingding.form.j76q5x"),
          align: "left",
        },
        {
          key: "webhook",
          title: this.$t("dingding.form.rih8yz"),
          align: "left",
        },
        {
          key: "state",
          title: this.$t("dingding.index.278wy1"),
          align: "center",
          width: 100,
          slot: "switchState",
        },
        {
          key: "remark",
          title: $t("common.remark"),
          align: "left",
        },
        {
          key: "Base",
          title: $t("common.action", { text: "" }),
          align: "center",
          width: 120,
          slot: "operate",
        },
      ]),
      currentId: "",
      formVisible: false,
    };
  },
  created() {
    this.getSocialHookList();
  },
  methods: {
    pageChange(page) {
      //this.pagination.currentPage = page;
      this.getSocialHookList();
    },
    // 切换状态
    doSwitchState(e, row) {
      socialHookSwitch({ id: row.id })
        .then((res) => {
          this.$message.success(
            $t("common.action", { text: $t("common.success") })
          );
          this.getSocialHookList();
        })
        .catch((err) => {
          this.getSocialHookList();
        });
    },
    // 删除
    delData(id) {
      this.$confirm(this.$t("dingding.index.1253hf"), $t("common.prompt"), {
        type: "warning",
      })
        .then(() => {
          let formData = new FormData();
          formData.append("id", id);
          socialHookDel(formData).then((res) => {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            this.getSocialHookList();
          });
        })
        .catch(() => {});
    },
    // 获取列表
    getSocialHookList() {
      this.loading = true;
      socialHookList({ type: 1 }).then((res) => {
        this.loading = false;
        this.dataSource = res.data;
      });
    },
    // 新增表单
    formAdd() {
      this.formVisible = true;
      this.currentId = "";
    },
    // 编辑表单
    formEdit(row) {
      this.formVisible = true;
      this.currentId = row.id;
    },
    // 关闭表单
    formClose() {
      this.formVisible = false;
      this.currentId = "";
      this.getSocialHookList();
    },
  },
};
</script>
