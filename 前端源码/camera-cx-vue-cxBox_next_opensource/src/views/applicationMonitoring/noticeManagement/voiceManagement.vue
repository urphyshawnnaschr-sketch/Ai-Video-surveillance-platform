<template>
  <div>
    <el-form ref="params" :model="params" label-width="180px">
      <el-form-item :label="$t('noticemanagement.voicemanagement.21mydf')" prop="voiceEnable">
        <el-radio v-model="params.voiceEnable" label="false">{{$t('noticemanagement.apimanagement.i3y411')}}</el-radio>
        <el-radio v-model="params.voiceEnable" label="true">{{$t('noticemanagement.apimanagement.7b96e0')}}</el-radio>
      </el-form-item>
      <el-form-item label="AccessKey ID">
        <el-input v-model="params.voiceAppId"></el-input>
        <div class="tip-text">{{$t('noticemanagement.voicemanagement.m8v1s6')}}</div>
      </el-form-item>
      <el-form-item label="AccessKey Secret">
        <el-input v-model="params.voiceAppSecret" type="textarea"></el-input>
        <div class="tip-text">{{$t('noticemanagement.voicemanagement.d0bl92')}}</div>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.voicemanagement.bqg368')">
        <el-input v-model="params.voiceTemplateId"></el-input>
        <div class="tip-text">{{$t('noticemanagement.voicemanagement.658fm3')}}</div>
      </el-form-item>
      <el-divider></el-divider>
      <el-form-item :label="$t('noticemanagement.voicemanagement.spqm11')">
        <div style="margin-bottom: 10px">
          <el-button @click.prevent="addPhone()">{{
            $t("common.add", { text: "" })
          }}</el-button>
        </div>
        <el-table
          :data="tableData"
          style="width: 500px"
          border
          v-loading="loading"
        >
          <el-table-column
            prop="phone"
            align="center"
            :label="$t('common.phoneNumber')"
          >
          </el-table-column>
          <el-table-column align="center" :label="$t('components.addphone.r1o963')" width="120">
            <template slot-scope="scope">
              <el-tag
                v-if="scope.row.name"
                size="medium"
                :style="{
                  backgroundColor: scope.row.showColorAlpha,
                  color: scope.row.showColor,
                  borderColor: scope.row.showColor,
                }"
                >{{ scope.row.name }}</el-tag
              >
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.action', { text: '' })"
            width="120"
          >
            <template slot-scope="scope">
              <el-button type="text" @click="addPhone(scope.row)">{{
                $t("common.edit", { text: "" })
              }}</el-button>
              <el-button
                type="text"
                style="color: red !important"
                @click="deletePhone(scope.row)"
                >{{ $t("button.deleteText", { text: "" }) }}</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <div class="tip-text">{{$t('noticemanagement.voicemanagement.454512')}}</div>
        <el-form-item>
          <el-button
            type="primary"
            @click="onSubmitForm('params')"
            style="margin-top: 16px"
            >{{ $t("button.submitText") }}</el-button
          >
          <div class="tip-text">{{$t('noticemanagement.voicemanagement.8e482l')}}</div>
        </el-form-item>
      </el-form-item>
    </el-form>
    <!-- 新增手机号 -->
    <AddPhone
      v-if="isAddPhone"
      :alarmLevelList="alarmLevelList"
      :detailObj="detailObj"
      @close="close"
    />
  </div>
</template>
<script>
import {
  saveAlarmVoiceConfig,
  queryAlarmVoiceConfig,
  listData,
  deletePhone,
} from "@/api/applicationMonitoring/voiceManagement";
import { getAlarmLevelList } from "@/api/applicationMonitoring/algorithmManagement";
import AddPhone from "./components/addPhone.vue";
export default {
  components: {
    AddPhone,
  },
  data() {
    return {
      tableData: [],
      params: {
        voiceEnable: "false",
        voiceAppId: "",
        voiceAppSecret: "",
        voiceTemplateId: "",
      },
      isAddPhone: false,
      loading: false,
      alarmLevelList: [],
      detailObj: {},
    };
  },
  async created() {
    await this.listAlarmLevelData();
    await this.getInfo();
    await this.getPhoneList();
  },
  methods: {
    async listAlarmLevelData() {
      const data = await getAlarmLevelList();
      this.alarmLevelList = data.data;
    },
    async getInfo() {
      const res = await queryAlarmVoiceConfig();
      this.params = res.data || {};
    },
    // 获取电话号码列表
    async getPhoneList() {
      this.loading = true;
      const data = await listData();
      if (data.data && data.data.length > 0) {
        data.data.forEach((item) => {
          this.alarmLevelList.forEach((items) => {
            if (item.levelId == items.id) {
              item.name = items.name;
              item.showColor = items.showColor;
              item.showColorAlpha = items.showColorAlpha;
            }
          });
        });
      }
      this.tableData = data.data;
      this.loading = false;
    },
    addPhone(row) {
      this.detailObj = {};
      if (row) {
        this.detailObj = row;
      }
      this.isAddPhone = true;
    },
    close() {
      this.isAddPhone = false;
      this.getPhoneList();
    },
    async deletePhone(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      })
        .then(async () => {
          const res = await deletePhone({ id: item.id });
          if (res.code == 0) {
            this.$message.success(
              $t("button.deleteText", { text: $t("common.success") })
            );
            await this.getPhoneList();
          }
        })
        .catch(() => {});
    },
    onSubmitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          const data = await saveAlarmVoiceConfig(this.params);
          this.$message.success(
            $t("button.saveText", { text: $t("common.success") })
          );
        } else {
          return false;
        }
      });
    },
  },
};
</script>
<style scoped lang="scss">
.tip-text {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.6);
}
</style>
