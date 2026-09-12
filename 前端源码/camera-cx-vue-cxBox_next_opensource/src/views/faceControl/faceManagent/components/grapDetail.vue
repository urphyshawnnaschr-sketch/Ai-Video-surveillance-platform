<template>
  <el-dialog
    :title="$t('faceControl.faceRecognition.groupManagement')"
    :visible.sync="visible"
    width="30%"
    @update:visible="(val) => $emit('update:visible', val)"
    append-to-body
  >
    <div style="overflow: hidden">
      <div>
        <span> {{ $t("faceControl.faceRecognition.name") }}：</span>
        <el-input
          style="width: 200px; margin: 0 20px"
          maxlength="10"
          :disabled="disabled"
          :placeholder="$t('form.tip.inputName')"
          v-model="formatData.name"
        ></el-input>
        <el-button
          size="mini"
          :disabled="disabled"
          type="primary"
          @click="addgrap()"
          >{{ $t("common.add", { text: "" }) }}</el-button
        >
      </div>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column
          prop="name"
          :label="$t('faceControl.faceRecognition.name')"
          width="180"
        >
        </el-table-column>
        <el-table-column :label="$t('common.action', { text: '' })" width="180">
          <template slot-scope="scope">
            <el-button @click="delGrap(scope.row.id)" type="text">{{
              $t("button.deleteText", { text: "" })
            }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- <span slot="footer" class="footer">
        <el-button size="mini" :disabled="disabled" type="primary" @click="save();closeDialog();">Confirm</el-button>
        <el-button size="mini"  @click="closeDialog">{{ $t("button.cancelText", { text: " " }) }}</el-button>
      </span> -->
    </div>
  </el-dialog>
</template>
<script>
import { listPageDGroup, saveGroup, delectGroup } from "../api";
export default {
  components: {},
  props: {
    visible: Boolean,
    currentItme: Object,
    currentState: String,
  },
  data() {
    return {
      formatData: {},
      tableData: [],
    };
  },
  computed: {},
  created() {
    this.int();
  },
  methods: {
    int() {
      this.getTable();
    },
    async addgrap() {
      if (!this.formatData.name) {
        this.$message.warning($t("form.verify.nameCannot"));
        return;
      }

      if (
        this.tableData.findIndex((item) => item.name == this.formatData.name) >
        -1
      ) {
        this.$message.warning($t("faceControl.faceRecognition.repeatName"));
        return;
      }

      const { data, code } = await saveGroup({
        name: this.formatData.name,
      });
      if (code == 0) {
        this.$message.success($t("common.add", { text: $t("common.success") }));
        this.getTable();
      }
    },
    async delGrap(id) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const { data, code } = await delectGroup({ id: id });
          if (code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            this.getTable();
          }
        })
        .catch(() => {});
    },
    async getTable() {
      const { data, count } = await listPageDGroup();
      const newArr = data.filter((item) => {
        return item.name != $t("faceControl.faceRecognition.total");
      });
      this.tableData = newArr;
    },
    closeDialog() {
      this.$emit("update:visible", false);
      this.$emit("close", false);
    },
    async save() {
      const { code, data } = await saveGroup(this.formatData);
      if (code == 0) {
        this.$message.success(
          $t("button.saveText", { text: $t("common.success") })
        );
        this.closeDialog();
      }
    },
    changes(val) {
      this.formatData.nameEn = val.replace(/[^a-zA-Z]/g, "");
    },
  },
};
</script>
<style scoped>
.bg {
  background: white;
}
.wh {
  width: 100%;
  height: 100%;
}
.footer {
  float: right;
}
</style>
