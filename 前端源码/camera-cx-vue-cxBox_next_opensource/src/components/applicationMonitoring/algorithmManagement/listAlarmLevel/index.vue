<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
      style="padding-top: 0px"
    >
      <div style="padding-bottom: 10px; margin-top: -15px">
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="addNewAlarmLevel"
          >{{ $t("common.add", { text: "" }) }}</el-button
        >
      </div>
      <div>
        <el-table :data="tableData" border style="width: 100%">
          <el-table-column
            align="center"
            prop="name"
            :label="$t('addalarmlevel.index.ph7u68')"
           
          >
          </el-table-column>
          <el-table-column
            align="center"
            prop="showColor"
            :label="$t('addalarmlevel.index.p8hkqm')"
            width="100"
          >
            <template slot-scope="scope">
              <span
                class="show-color"
                :style="{ backgroundColor: scope.row.showColor }"
              ></span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="showTypeNames"
            :label="$t('addalarmlevel.index.7cmcv1')"
          >
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.action', { text: '' })"
            width="100"
          >
            <template slot-scope="scope">
              <el-button type="text" @click="editAlarmLevelData(scope.row)">{{
                $t("common.edit", { text: "" })
              }}</el-button>
              <el-button
                type="text"
                class="danger"
                @click="deleteAlarmLevelData(scope.row)"
                >{{ $t("button.deleteText", { text: "" }) }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- <span slot="footer" class="dialog-footer">
				<el-button @click="dialogVisible = false">{{ $t('button.cancelText', { text: '' }) }}</el-button>
			</span> -->
    </el-dialog>

    <AddAlarmLevel
      v-if="addNewAlarmLevelVisible"
      :currentId="currentId"
      @close="(addNewAlarmLevelVisible = false), listAlarmLevel()"
    />
  </div>
</template>
<script>
import {
  getAlarmLevelList,
  deleteAlarmLevel,
} from "@/api/applicationMonitoring/algorithmManagement";
import AddAlarmLevel from "@/components/applicationMonitoring/algorithmManagement/addAlarmLevel";
export default {
  components: {
    AddAlarmLevel,
  },
  data() {
    return {
      loading: false,
      title: this.$t("listalarmlevel.index.7crqr7"),
      dialogVisible: true,
      tableData: [],
      addNewAlarmLevelVisible: false,
      currentId: "",
    };
  },
  async created() {
    await this.listAlarmLevel();
  },
  methods: {
    // 获取行业列表
    async listAlarmLevel() {
      const data = await getAlarmLevelList();
      this.tableData = data.data;
    },
    addNewAlarmLevel() {
      this.currentId = "";
      this.addNewAlarmLevelVisible = true;
    },
    editAlarmLevelData(item) {
      this.currentId = item.id;
      this.addNewAlarmLevelVisible = true;
    },
    async deleteAlarmLevelData(item) {
      this.$confirm(
        this.$t("listalarmlevel.index.m77469"),
        $t("common.prompt"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          type: "warning",
        }
      )
        .then(async () => {
          const res = await deleteAlarmLevel({
            id: item.id,
          });

          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.listAlarmLevel();
          }
        })
        .catch(() => {});
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss">
.show-color {
  padding: 6px 25px;
}
</style>
