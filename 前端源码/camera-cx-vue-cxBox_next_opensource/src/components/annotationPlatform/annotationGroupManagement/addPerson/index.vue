<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('annotationplatform.annotationgroupmanagement.73633q')"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" :rules="rules" label-width="80px">
          <el-form-item :label="$t('addperson.index.n5y15j')" prop="userIds">
            <el-select
              v-model="params.userIds"
              multiple
              :placeholder="$t('addperson.index.lxu378')"
              style="width: 100%"
            >
              <el-option
                v-for="(item, index) in personOptions"
                :label="item.name"
                :value="item.id"
                :key="index"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveData">{{
          $t("button.submitText")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  getTeamPersonList,
  savePersonData,
} from "@/api/annotationPlatform/annotationGroupManagement";
export default {
  props: {
    personData: Array,
    currentSelectGroup: Object,
  },
  data() {
    return {
      loading: false,
      dialogVisible: true,
      personOptions: [],
      params: {
        userIds: [],
      },
      rules: {
        userIds: [{ required: true, message: this.$t('addperson.index.lxu378'), trigger: "change" }],
      },
    };
  },
  async created() {
    this.getTeamPersonList();
  },
  methods: {
    async getTeamPersonList() {
      const data = await getTeamPersonList();
      this.personOptions = data.data;
    },
    // 保存账号
    saveData() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          let status = false;
          this.params.userIds.forEach((ids) => {
            this.personData.forEach((item) => {
              if (ids == item.id) {
                status = true;
              }
            });
          });
          if (status) {
            return this.$message.error(this.$t('addperson.index.om1l41'));
          }
          await savePersonData(this.params, this.currentSelectGroup.id);
          this.$message.success(
            $t("common.add", { text: $t("common.success") })
          );
          this.dialogVisible = false;
        } else {
          return false;
        }
      });
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
