<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('addcamera.newadd.ummyqc')"
      :visible.sync="dialogVisible"
      width="50%"
      @close="closed"
      append-to-body
    >
      <el-row :gutter="20">
        <el-col :span="24" style="padding: 0 20px 10px 20px">
          <el-row>
            <el-col :span="2"
              ><div style="padding: 3px 0">
                {{ $t("components.socialhookselected.hhe7xs") }}
              </div></el-col
            >
            <el-col :span="22">
              <el-radio-group v-model="type" size="mini" @change="changeType">
                <el-radio label="0" border>{{
                  $t("components.socialhookselected.r09n76")
                }}</el-radio>
                <!-- <el-radio label="2" border>钉钉</el-radio>
                <el-radio label="1" border>WeWork</el-radio> -->
              </el-radio-group>
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="24">
          <Tables
            :pagination="pagination"
            :columns="columns"
            :dataSource="dataSource"
            :loading="loading"
            :rowSelection="rowSelection"
            :selections.sync="selectedRowKeys"
            rowKey="id"
          >
            <div slot="index" slot-scope="{ $index }">
              {{ $index + 1 }}
            </div>

            <div slot="switchState" slot-scope="{ row }">
              <el-tag v-if="row.state == 1" type="success">{{
                $t("applicationMonitoring.boxManagement.tkhb19")
              }}</el-tag>
              <el-tag v-if="row.state == 0" type="error">{{
                $t("applicationMonitoring.boxManagement.q827fn")
              }}</el-tag>
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
        </el-col>
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closed">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveData" :loading="btnLoading">{{
          $t("button.saveText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import Tables from "@/components/Table/index.vue";
import { socialHookList } from "../api";
export default {
  components: {
    Tables,
  },
  props: {
    selected: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      dialogVisible: true,
      type: "0",
      btnLoading: false,
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
          key: "remark",
          title: $t("common.remark"),
          align: "left",
        },
        {
          key: "state",
          title: this.$t("components.channel.5x3p13"),
          align: "center",
          width: 100,
          slot: "switchState",
        },
      ]),
      rowSelection: {
        type: "checkbox",
        change: (selectedRowKeys, selectedRows) => {
          this.selectedRowKeys = selectedRowKeys;
          this.selectedRows = selectedRows;

          this.dataSource.forEach((item) => {
            if (selectedRowKeys.includes(item.id)) {
              if (!this.currentSelected.includes(item.id)) {
                this.currentSelected.push(item.id);
              }
            } else {
              let index = this.currentSelected.indexOf(item.id);
              if (index > -1) {
                this.currentSelected.splice(index, 1);
              }
            }
          });
        },
      },
      selectedRows: [],
      selectedRowKeys: [],
      currentSelected: [],
    };
  },
  created() {
    this.currentSelected = this.selected;
    this.getSocailHookInfo();
  },
  methods: {
    // 获取配置列表
    getSocailHookInfo() {
      this.selectedRowKeys = [];
      this.selectedRows = [];

      socialHookList({ type: this.type }).then((res) => {
        this.dataSource = res.data;
        this.dataSource.forEach((item) => {
          if (this.currentSelected.includes(item.id)) {
            this.selectedRowKeys.push(item.id);
            this.selectedRows.push(item);
          }
        });
      });
    },
    // 切换推送方式
    changeType(val) {
      this.type = val;
      this.getSocailHookInfo();
    },
    closed() {
      this.$emit("closeSocial", false);
    },
    // 保存
    saveData() {
      this.$emit("closeSocial", true, this.currentSelected);
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .el-dialog__body {
  padding-top: 10px;
}
::v-deep .el-radio {
  margin-right: 8px;
}
</style>
