<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('addperson.index.1899gm')"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" :rules="rules" label-width="80px">
          <el-form-item :label="$t('teammanagement.teamdetail.jln5pl')" prop="roleIds">
            <el-select
              style="width: 100%"
              v-model="params.roleIds"
              :placeholder="$t('addperson.index.83n87g')"
              multiple
            >
              <el-option
                v-for="(item, index) in roleOptions"
                :key="index"
                :label="item.nameCh"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('addperson.index.7m637g')" prop="prefix">
            <el-input
              style="width: 100%"
              v-model="params.prefix"
              :placeholder="$t('addperson.index.pu1563')"
            ></el-input>
          </el-form-item>
          <el-form-item :label="$t('projectdetail.overview.7m7byy')" prop="userNum">
            <el-input-number
              style="width: 100%"
              v-model="params.userNum"
              :placeholder="$t('addperson.index.m78f82')"
            ></el-input-number>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveBatchPerson">{{
          $t("button.submitText")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { getRoleList } from "@/api/annotationPlatform/teamManagement";
import { saveBatchPerson } from "@/api/annotationPlatform/teamManagement";
export default {
  data() {
    return {
      dialogVisible: true,
      roleOptions: [],
      params: {
        teamId: this.$route.query.id,
        roleIds: [],
        prefix: "",
        userNum: 1,
      },
      rules: {
        roleIds: [{ required: true, message: this.$t('addperson.index.83n87g'), trigger: "blur" }],
        prefix: [
          { required: true, message: this.$t('addperson.index.33l05z'), trigger: "blur" },
        ],
        userNum: [{ required: true, message: this.$t('addperson.index.sk3nkm'), trigger: "blur" }],
      },
    };
  },
  created() {
    this.getRoleList();
  },
  methods: {
    async getRoleList() {
      const data = await getRoleList();
      this.roleOptions = data.data;
    },
    // 保存账号
    saveBatchPerson() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          await saveBatchPerson(this.params);
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
