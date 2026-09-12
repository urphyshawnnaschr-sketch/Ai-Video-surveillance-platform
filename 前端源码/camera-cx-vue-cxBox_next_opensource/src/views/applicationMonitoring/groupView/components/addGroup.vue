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
          <el-form-item :label="$t('annotationplatform.annotationgroupmanagement.k78318')" prop="name">
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
import { groupSave, groupInfo } from "../api";
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
      title: this.currentId ? $t("common.edit", { text: this.$t('facemanagent.index.l66kah') }) : this.$t('components.addgroup.2t16qd'),
      params: {
        name: "",
      },
      rules: {
        name: [{ required: true, message: this.$t('addgroup.index.52vjy1'), trigger: "blur" }],
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
      groupInfo({ id: this.currentId }).then((res) => {
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
          const res = await groupSave(obj);
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
