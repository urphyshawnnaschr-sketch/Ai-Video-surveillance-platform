<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('common.add', { text: $t('common.soundColumn') })"
      :visible.sync="dialogVisible"
      width="500px"
      @close="closed"
    >
      <el-form ref="form" :model="params" :rules="rules" label-width="120px">
        <el-form-item :label="$t('components.addinfo.7dpru2')" prop="server">
          <el-input v-model="params.server" placeholder="Format: ip:port"></el-input>
        </el-form-item>
        <el-form-item :label="$t('components.addinfo.u8c4y0')" prop="sn">
          <el-input v-model="params.sn"></el-input>
        </el-form-item>
        <el-form-item :label="$t('components.addinfo.5hw38w')" prop="vol">
          <el-radio-group v-model="params.vol">
            <el-radio :label="20">{{$t('addcamera.newadd.284165')}}</el-radio>
            <el-radio :label="40">{{$t('addcamera.newadd.sri461')}}</el-radio>
            <el-radio :label="60">{{$t('addcamera.newadd.673fee')}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('components.addinfo.ld9hq6')">
          <el-select
            v-model="params.type"
            :placeholder="$t('common.chooseText')"
          >
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('addaccount.index.81e2nd')">
          <el-input v-model="params.userName"></el-input>
        </el-form-item>
        <el-form-item :label="$t('common.password', {text: ''})">
          <el-input v-model="params.password" show-password></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closed">{{ $t("button.cancelText", { text: '' }) }}</el-button>
        <el-button type="primary" @click="saveData" :loading="btnLoading"
          >{{ $t('button.saveText', { text: '' }) }}</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  detailData,
  saveData,
} from "@/api/applicationMonitoring/soundColumnManagement";
export default {
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      options: [
        {
          label: this.$t('components.addinfo.87juu6'),
          value: "yuelang",
        },
      ],
      params: {
        server: "",
        sn: "",
        vol: "",
        type: "",
        userName: "",
        password: "",
      },
      rules: {
        server: [
          { required: true, message: this.$t('components.addinfo.r0z77h'), trigger: "blur" },
        ],
        sn: [{ required: true, message: this.$t('components.addinfo.2inpbu'), trigger: "change" }],
      },
      btnLoading: false,
    };
  },
  created() {
    if (this.currentId) {
      this.getDetail();
    }
  },
  methods: {
    async getDetail() {
      const res = await detailData({ id: this.currentId });
      this.params = res.data;
    },
    closed() {
      this.$emit("closeAdd");
    },
    // 保存
    saveData() {
      this.btnLoading = true;
      this.$refs.form.validate((valid) => {
        if (valid) {
          let formData = new FormData();
          formData.append("server", this.params.server);
          formData.append("sn", this.params.sn);
          formData.append("vol", this.params.vol);
          formData.append("type", this.params.type);
          formData.append("userName", this.params.userName);
          formData.append("password", this.params.password);
          if (this.currentId) {
            formData.append("id", this.params.id);
          }
          saveData(formData)
            .then((res) => {
              this.$message.success($t('button.saveText', { text: $t('common.success') }));
              this.btnLoading = false;
              this.$emit("closeAdd");
            })
            .catch(() => {
              this.btnLoading = false;
            });
        } else {
          this.btnLoading = false;
          return false;
        }
      });
    },
  },
};
</script>
<style lang="scss"></style>
