<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('components.platforminfo.1l632c')"
    :visible.sync="dialogVisible"
    width="80%"
    @close="closed"
    append-to-body
  >
    <div class="config-cont">
      <el-form ref="form" :model="params" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item :label="$t('components.platforminfo.fhb5jj')">
              {{ params.sipId }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('components.platforminfo.4haw0y')">
              {{ params.sipDomain }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('components.platforminfo.h5h4a8')">
              {{ params.sipIp }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('components.platforminfo.264147')">
              {{ params.sipPort }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="$t('common.password', {text: $t('components.platforminfo.6y20p5')}) + ':'">
              {{ params.sipPassword }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="bot-btn"><el-button type="primary" @click="getPlatformInfo">{{$t('components.platforminfo.j1d51k')}}</el-button></div>
    </div>
  </el-dialog>
</template>
<script>
import { getPlatformInfo } from "../api";
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
      params: {},
    };
  },
  created() {
    this.getPlatformInfo();
  },
  methods: {
    // 获取数据
    getPlatformInfo() {
      this.loading = true;
      getPlatformInfo()
        .then((res) => {
          console.log(res);
          this.params = res.data;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss">
.bot-btn {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>

