<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" :rules="rules" label-width="80px">
          <el-form-item :label="$t('components.departmentadd.2yr1m6')" prop="name">
            <el-input v-model="params.name"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveFun">{{
          $t("button.submitText")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { saveData, detailData } from "../api";
export default {
  props: {
    parentId: {
      type: String,
      default: "",
    },
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogVisible: true,
      title: this.currentId
        ? $t("common.edit", { text: $t("common.department") })
        : $t("common.add", { text: $t("common.department") }),
      params: {
        name: "",
      },
      rules: {
        name: [{ required: true, message: this.$t('components.departmentadd.w3943x'), trigger: "blur" }],
      },
    };
  },
  created() {
    if (this.currentId) {
      this.getDetail();
    }
  },
  methods: {
    closed() {
      this.$emit("close");
    },
    // 获取详情
    getDetail() {
      detailData({ id: this.currentId }).then((res) => {
        Object.assign(this.params, {
          id: res.data.id,
          name: res.data.name,
          parentId: res.data.parentId,
        });
      });
    },
    // 保存
    saveFun() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          let obj = {
            ...this.params,
          };
          if (!this.currentId) {
            obj.parentId = this.parentId == "0" ? 0 : this.parentId;
          }
          const res = await saveData(obj);
          this.$message.success(
            $t("button.saveText", { text: $t("common.success") })
          );
          this.$emit("close");
        } else {
          return false;
        }
      });
    },
  },
};
</script>
<style scoped lang="scss"></style>
