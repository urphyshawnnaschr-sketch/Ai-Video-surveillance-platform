<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      width="60%"
      top="10vh"
      :title="$t('applicationMonitoring.casketManagement.activation')"
      :visible.sync="detailVisible"
      @closed="closed"
      append-to-body
    >
      <el-form
        :model="ruleForm"
        ref="ruleForm"
        label-width="100px"
        size="mini"
        label-position="left"
      >
        <el-form-item
          :label="$t('applicationMonitoring.casketManagement.machineCode')"
        >
          <div style="display: flex; align-items: center">
            <div v-if="sn">
              <span>{{ sn }}</span>
              <span
                style="margin-left: 20px; cursor: pointer; color: #eb3a2f"
                @click="copyFun"
                >{{ $t("button.copyText") }}</span
              >
            </div>
            <div v-else>
              <i class="el-icon-loading" style="font-size: 20px" />
              <span style="margin-left: 5px"
                >{{
                  $t("applicationMonitoring.casketManagement.readMachineCode")
                }}
              </span>
            </div>
          </div>
        </el-form-item>
        <el-form-item
          :label="$t('applicationMonitoring.casketManagement.authorizeCode')"
          v-if="activeType != 1"
        >
          <div style="display: flex">
            <div style="flex: 1">
              <el-input
                :placeholder="$t('form.tip.inputContent')"
                type="textarea"
                :rows="2"
                v-model="activeCode"
                clearable
              ></el-input>
            </div>
            <div
              v-if="activeCode"
              style="margin-left: 20px; cursor: pointer; color: #eb3a2f"
              @click="activeFun"
            >
              {{ $t("applicationMonitoring.casketManagement.activation") }}
            </div>
          </div>
        </el-form-item>
        <el-form-item
          :label="$t('applicationMonitoring.casketManagement.curState')"
        >
          {{
            activeType == 1
              ? $t("applicationMonitoring.casketManagement.active")
              : $t("applicationMonitoring.casketManagement.notActive")
          }}
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {
  getSn,
  getActived,
  actived,
} from "@/api/applicationMonitoring/casketManagement";
export default {
  props: {
    rowId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      ruleForm: {},
      detailVisible: true,
      sn: "",
      activeCode: "",
      activeType: "",
    };
  },
  async created() {
    // await this.getSn();
    await this.getActive();
  },
  methods: {
    // 获取机器码
    async getSn() {
      let formData = new FormData();
      formData.append("boxId", this.rowId);
      getSn(formData).then((res) => {
        console.log("getsn", res);
        this.sn = res.data;
      });
    },
    // 复制
    copyFun() {
      // navigator.clipboard.writeText(this.sn)
      const textarea = document.createElement("textarea");
      textarea.value = this.sn;
      document.body.appendChild(textarea);
      textarea.select();
      document.execCommand("copy");
      document.body.removeChild(textarea);
    },
    // 获取激活状态
    async getActive() {
      let form = new FormData();
      form.append("boxId", this.rowId);
      getActived(form)
        .then((res) => {
          console.log("getactived", res);
          this.activeType = res.data;
          if (this.activeType == 0) {
            this.getSn();
          } else {
            this.$message.success(
              $t("applicationMonitoring.casketManagement.boxActive")
            );
            this.$emit("close");
          }
        })
        .catch((error) => {
          //this.$emit('close');
        });
    },
    // 激活
    async activeFun() {
      let form = new FormData();
      form.append("boxId", this.rowId);
      form.append("activeCode", this.activeCode);
      actived(form).then((res) => {
        if (res.code == 0) {
          this.activeType = res.data;
          if (this.activeType == 0) {
            this.getSn();
          } else {
            this.$message.success($t('applicationMonitoring.casketManagement.boxActive'));
            this.$emit("close");
          }
        }
      });
    },
    // 关闭弹窗
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
