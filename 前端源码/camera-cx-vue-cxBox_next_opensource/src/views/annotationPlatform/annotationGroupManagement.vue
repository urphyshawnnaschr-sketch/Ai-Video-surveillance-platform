<template>
  <div class="wrap">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card class="box-card">
          <div
            slot="header"
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
            "
          >
            <span>{{$t('annotationplatform.annotationgroupmanagement.m7rpw5')}}</span>
            <div>
              <el-button type="primary" @click="addGroup">{{$t('annotationplatform.annotationgroupmanagement.6l8mt1')}}</el-button>
            </div>
          </div>
          <el-table
            ref="groupTable"
            :data="groupData"
            border
            v-loading="loading"
            highlight-current-row
            @current-change="handleCurrentChange"
          >
            <el-table-column :label="$t('annotationplatform.annotationgroupmanagement.k78318')" prop="name"></el-table-column>
            <el-table-column
              :label="$t('annotationplatform.annotationgroupmanagement.701177')"
              prop="memberNum"
            ></el-table-column>
            <el-table-column :label="$t('annotationplatform.annotationgroupmanagement.bg3ytg')">
              <template slot-scope="scope">
                {{ $moment(scope.row.createdAt).format("YYYY-MM-DD") }}
              </template>
            </el-table-column>
            <el-table-column :label="$t('common.action', { text: '' })">
              <template slot-scope="scope">
                <el-button type="text" @click.stop="editGroup(scope.row)">{{
                  $t("common.edit", { text: "" })
                }}</el-button>
                <el-button
                  type="text"
                  class="danger"
                  @click="deleteGroup(scope.row)"
                  >{{ $t("button.deleteText", { text: "" }) }}</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card class="box-card">
          <div
            slot="header"
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
            "
          >
            <span>
              <span v-if="currentSelectGroup?.name">
                {{ currentSelectGroup?.name }}：
              </span>{{$t('annotationplatform.annotationgroupmanagement.pgixdt')}}</span>
            <div style="font-size: 0">
              <el-button
                type="primary"
                icon="el-icon-plus"
                style="margin-right: 10px"
                @click="addPersonVisible = true"
                >{{$t('annotationplatform.annotationgroupmanagement.73633q')}}</el-button
              >
              <el-button
                type="danger"
                icon="el-icon-delete"
                style="margin-right: 10px"
                @click="deletePerson(2)"
                >{{$t('annotationplatform.annotationgroupmanagement.h77n4e')}}{{ $t("button.deleteText", { text: "" }) }}</el-button
              >
              <!-- <el-input
                placeholder="Person Name"
                style="width: 200px; margin-right: 10px"
              ></el-input>
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="getGroupPersonList"
                >{{ $t('button.queryText') }}</el-button
              > -->
            </div>
          </div>
          <el-table
            ref="personData"
            :data="personData"
            border
            v-loading="loading"
            @selection-change="handleSelectionChange"
          >
            <el-table-column
              type="selection"
              :label="$t('common.number')"
              width="60"
              align="center"
            ></el-table-column>
            <el-table-column
              :label="$t('common.accountName')"
              prop="name"
            ></el-table-column>
            <!-- <el-table-column label="账号角色"></el-table-column> -->
            <el-table-column :label="$t('common.createTime')">
              <template slot-scope="scope">
                {{ $moment(scope.row.createdAt).format("YYYY-MM-DD HH:mm:ss") }}
              </template>
            </el-table-column>
            <el-table-column :label="$t('common.action', { text: '' })">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  class="danger"
                  @click="deletePerson(1, scope.row)"
                  >{{ $t("button.deleteText", { text: "" }) }}</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <AddGroup
      :currentEditGroup="currentEditGroup"
      v-if="addGroupVisible"
      @close="(addGroupVisible = false), getListData('edit')"
    />
    <AddPerson
      :currentSelectGroup="currentSelectGroup"
      :personData="personData"
      v-if="addPersonVisible"
      @close="(addPersonVisible = false), getGroupPersonList('edit')"
    />
  </div>
</template>
<script>
import {
  getListData,
  deleteData,
  deletePersonData,
  getGroupPersonList,
} from "@/api/annotationPlatform/annotationGroupManagement";
import AddGroup from "@/components/annotationPlatform/annotationGroupManagement/addGroup";
import AddPerson from "@/components/annotationPlatform/annotationGroupManagement/addPerson";
export default {
  components: {
    AddGroup,
    AddPerson,
  },
  data() {
    return {
      loading: false,
      addGroupVisible: false,
      addPersonVisible: false,
      currentEditGroup: null,
      currentSelectGroup: null,
      groupData: [],
      personData: [],
      multipleSelection: [],
    };
  },
  created() {
    this.getListData("init");
  },
  methods: {
    async getListData(source) {
      this.loading = true;
      const data = await getListData();
      this.loading = false;
      this.groupData = data.data;
      // 为时，代表页面新加载，默认选中第一条标注组
      // 为时，代表修改完新加载，默认选中之前的标注组
      if (source == "init" && this.groupData.length) {
        this.currentSelectGroup = this.groupData[0];
      } else {
        const obj = this.groupData.find(
          (i) => i.id == this.currentSelectGroup.id
        );
        this.currentSelectGroup = obj;
      }
      this.$refs.groupTable.setCurrentRow(this.currentSelectGroup);
    },
    async getGroupPersonList() {
      const data = await getGroupPersonList({
        groupId: this.currentSelectGroup?.id,
      });
      this.personData = data.data;
    },
    // 新建分组
    addGroup() {
      this.currentEditGroup = null;
      this.addGroupVisible = true;
    },
    // 编辑分组
    editGroup(item) {
      this.currentEditGroup = item;
      this.addGroupVisible = true;
    },
    // 删除分组
    deleteGroup(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await deleteData({ groupId: item.id });
        if (res.code == 0) {
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
          await this.getListData();
        }
      });
    },
    // 删除人员 为1是单个删除，2是批量删除
    deletePerson(type, item) {
      let message = "";
      let delArr = [];
      if (type == 1) {
        message = $t("modal.deleteSureText");
        delArr = [item.id];
      } else {
        message = this.$t('annotationplatform.annotationgroupmanagement.gm3233');
        delArr = this.multipleSelection;
      }
      if (!delArr.length) {
        return this.$message.error(this.$t('annotationplatform.annotationgroupmanagement.el4d41'));
      }
      this.$confirm(message, $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await deletePersonData(delArr, this.currentSelectGroup.id);
        if (res.code == 0) {
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
          await this.getGroupPersonList();
          this.$refs.personData.clearSelection();
        }
      });
    },
    handleCurrentChange(row) {
      this.currentSelectGroup = row;
      this.getGroupPersonList();
    },
    handleSelectionChange(val) {
      this.multipleSelection = val.map((i) => {
        return i.id;
      });
    },
  },
};
</script>
<style scoped lang="scss">
.wrap {
  min-width: 1200px;
}

::v-deep .el-input {
  margin-bottom: 0 !important;
}
</style>
