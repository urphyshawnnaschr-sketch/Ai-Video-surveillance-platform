<template>
  <div>
    <el-form ref="form" :model="params" :rules="rules" label-width="80px">
      <el-form-item :label="$t('components.addrole.cxi27s')" prop="nameEn">
        <el-input
          v-model="params.nameEn"
          :disabled="pageType == 'view'"
        ></el-input>
        <div style="font-size: 12px; color: #939393">
          {{ $t("components.addrole.iqd48y") }}
        </div>
      </el-form-item>
      <el-form-item :label="$t('components.addrole.4q1j6p')" prop="nameCh">
        <el-input
          v-model="params.nameCh"
          :disabled="pageType == 'view'"
        ></el-input>
      </el-form-item>
      <el-form-item :label="$t('components.addrole.zwdxe7')" prop="menusIds">
        <el-tree
          ref="tree"
          :data="menuOptions"
          show-checkbox
          node-key="id"
          :default-checked-keys="checkedKeys"
          @check="handleNodeClick"
          :props="defaultProps"
        >
        </el-tree>
      </el-form-item>
      <!-- <el-form-item label="数据管理" prop="locationIds">
              <el-checkbox-group v-model="params.locationIds">
                <div v-for="(item,inx) in locationList" :key="inx">
                  <div style="font-size: 13px;" v-if="item.locationList.length>0">{{ item.name }}</div>
                  <el-checkbox v-for="(items,index) in item.locationList" :label="items.id" :disabled="pageType=='view'">{{ items.name }}</el-checkbox>
                </div>
            </el-checkbox-group>
            </el-form-item> -->
    </el-form>
    <div
      v-if="pageType != 'view'"
      style="display: flex; justify-content: space-between; align-items: center"
    >
      <div>
        <el-button type="primary" @click="saveFun">{{
          $t("button.submitText")
        }}</el-button>
        <el-button @click="closed">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </div>
      <div>
        <el-button type="primary" v-if="currentId" @click="deteleFun">{{
          $t("button.deleteText", { text: "" })
        }}</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { saveData, detailData, deleteData, locationData } from "../api";
import { listData } from "../../menuManagement/api";
export default {
  props: {
    currentId: {
      type: String,
      default: null,
    },
    pageType: {
      type: String,
      default: "view",
    },
  },
  data() {
    return {
      checkedKeys: [], //默认选中的id
      loading: false,
      title: this.currentObj
        ? $t("common.edit", { text: $t("common.role") })
        : $t("common.add", { text: $t("common.role") }),
      defaultProps: {
        children: "children",
        label: "name",
      },
      dialogVisible: true,
      menuOptions: [],
      params: {
        nameEn: "",
        nameCh: "",
        menusIds: [],
        locationIds: [],
      },
      rules: {
        nameEn: [
          {
            required: true,
            message: this.$t("components.addrole.8pt182"),
            trigger: "blur",
          },
          {
            validator: (rule, value, callback) => {
              const regex = /^[a-zA-Z0-9_]{4,26}$/;
              if (!regex.test(value)) {
                callback(new Error(this.$t("components.addrole.h6pnfr")));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
        nameCh: [
          {
            required: true,
            message: this.$t("components.addrole.1l584y"),
            trigger: "blur",
          },
        ],
        menusIds: [
          {
            required: true,
            message: this.$t("components.addrole.w5252k"),
            trigger: "change",
          },
        ],
        // locationIds: [{ required: true, message: "请选择数据", trigger: "change" }],
      },
      locationList: [],
    };
  },
  async created() {
    this.getMenuList();
    //this.getLocation();
    if (this.currentId) {
      //编辑
      this.getDetail();
    }
  },
  methods: {
    // 获取角色区域数据
    async getLocation() {
      let form = new FormData();
      if (this.currentId) {
        form.append("roleId", this.currentId);
      }
      const res = await locationData(form);
      if (res.data && res.data.length > 0) {
        this.locationList = res.data;
        if (this.currentId) {
          let arr = [];
          res.data.forEach((item, ind) => {
            if (item.locationList.length > 0) {
              item.locationList.forEach((items, i) => {
                if (items.checked) {
                  arr.push(items.id);
                }
              });
            }
          });
          this.params.locationIds = arr;
        }
      }
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    // 获取菜单列表
    async getMenuList() {
      let form = new FormData();
      if (this.currentId) {
        form.append("roleId", this.currentId);
      }
      const res = await listData(form);
      if (res.data && res.data.length > 0) {
        this.menuOptions = res.data.filter((item) => {
          return item.name != this.$t("bigscreen.mainleft_old.lkv7t0");
        });
        if (this.currentId) {
          this.checkedKeys = [];
          this.getCheck(res.data);
        }
      }
    },
    // 数据回显
    getCheck(tree) {
      tree.forEach((item) => {
        if (this.pageType == "view") {
          item.disabled = true;
        }
        if (item.checked) {
          this.checkedKeys.push(item.id);
          this.params.menusIds.push(item.id);
        }
        let isChecked = false;
        if (item.children && item.children.length) {
          item.children.forEach((items) => {
            if (!items.checked) {
              this.checkedKeys = this.checkedKeys.filter((i) => {
                console.log();
                return i != item.id;
              });
            }
          });
          this.getCheck(item.children);
        }
      });
    },
    // 获取详情
    getDetail() {
      detailData({ id: this.currentId }).then((res) => {
        Object.assign(this.params, {
          id: res.data.id,
          nameEn: res.data.nameEn,
          nameCh: res.data.nameCh,
        });
      });
    },
    handleNodeClick(data, node, self) {
      let checkedKeys = this.$refs.tree.getCheckedKeys();
      let halfCheckedKeys = this.$refs.tree.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
      this.params.menusIds = checkedKeys;
    },
    // 保存
    saveFun() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          const res = await saveData(this.params);
          this.$message.success(
            $t("button.saveText", { text: $t("common.success") })
          );
          this.$emit("close");
        } else {
          return false;
        }
      });
    },
    // 删除
    deteleFun() {
      this.$confirm(
        this.$t("components.addrole.6442fq"),
        this.$t("components.addrole.g58l73"),
        {
          confirmButtonText: $t("button.deleteText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          type: "warning",
        }
      )
        .then(() => {
          let formData = new FormData();
          formData.append("id", this.params.id);
          deleteData(formData)
            .then((res) => {
              if (res.code == 0) {
                this.$message.success(this.$t("components.addrole.uuvm8o"));
                this.$emit("close");
              }
            })
            .catch((res) => {});
        })
        .catch(() => {});
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss"></style>
