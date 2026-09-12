<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('common.add', { text: $t('common.phoneNumber') })"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" :rules="rules" label-width="120px">
          <el-form-item :label="$t('login.phoneNumber')" prop="phone">
            <el-input v-model="params.phone"></el-input>
          </el-form-item>
          <el-form-item :label="$t('components.addphone.r1o963')" prop="levelId">
            <el-select
              v-model="params.levelId"
              :placeholder="$t('form.tip.chooseAlarmLevel')"
              style="width: 100%"
            >
              <el-option
                v-for="(item, index) in alarmLevelList"
                :key="index"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: '' })
        }}</el-button>
        <el-button type="primary" @click="saveAlarmLevelData" :loading="loading"
          >{{$t('marktool.annotate.q5fjzf')}}</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { savePhone } from "@/api/applicationMonitoring/voiceManagement";
export default {
  props: {
    alarmLevelList: {
      type: Array,
      default: [],
    },
    detailObj: {
      type: Object,
      default: {},
    },
  },
  data() {
    return {
      dialogVisible: true,
      params: {
        phone: "",
        levelId: "",
      },
      rules: {
        phone: [
          { required: true, message: this.$t('components.addphone.0l6nvu'), trigger: "blur" },
          {
            pattern: /^1[3456789]\d{9}$/,
            message: this.$t('components.addphone.wqx8cq'),
            trigger: "blur",
          },
        ],
        levelId: [
          { required: true, message: this.$t('components.addphone.n8bn4o'), trigger: "blur" },
        ],
      },
      loading: false,
    };
  },
  created() {
    if (this.detailObj.id) {
      this.params = {
        id: this.detailObj.id,
        phone: this.detailObj.phone,
        levelId: this.detailObj.levelId,
      };
    }
  },
  methods: {
    closed() {
      this.$emit("close");
    },
    // 提交
    saveAlarmLevelData() {
      this.loading = true;
      this.$refs.form.validate((valid) => {
        if (valid) {
          savePhone(this.params).then((res) => {
            this.$message.success($t('button.saveText', { text: $t('common.success') }));
            this.loading = false;
            this.$emit("close");
          });
        } else {
          this.loading = false;
          return false;
        }
      });
    },
  },
};
</script>
<style scoped lang="scss"></style>
