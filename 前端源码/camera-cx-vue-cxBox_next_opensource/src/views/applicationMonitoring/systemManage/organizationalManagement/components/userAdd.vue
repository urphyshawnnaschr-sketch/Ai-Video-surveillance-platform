<template>
  <div class="user-cont">
    <el-form ref="form" :model="params" :rules="rules" label-width="100px">
      <div class="title">{{ $t('components.useradd.js8r65') }}</div>
      <el-form-item :label="$t('common.name')" prop="name">
        <el-input v-model="params.name"></el-input>
      </el-form-item>
      <el-form-item :label="$t('facemanagement.index.staffNo')" prop="staffNo">
        <el-input v-model="params.staffNo"></el-input>
      </el-form-item>
      <el-form-item :label="$t('login.phoneNumber')" prop="phone">
        <el-input v-model="params.phone"></el-input>
      </el-form-item>
      <el-form-item :label="$t('common.caBelongingOrganization')" prop="departId">
        <el-cascader
          v-model="params.departId"
          :options="depList"
          :props="{ value: 'id', label: 'name', checkStrictly: true }"
          :show-all-levels="false"
          clearable
        >
        </el-cascader>
      </el-form-item>
      <el-form-item :label="$t('components.useradd.5jj5ks')" prop="roleIds">
        <el-select
          style="width: 100%"
          v-model="params.roleIds"
          :placeholder="$t('common.chooseText')"
          clearable
        >
          <el-option
            v-for="(item, index) in roleOptions"
            :key="index"
            :label="item.nameCh"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <div class="title">{{ $t('common.password', { text: $t('addaccount.index.81e2nd') }) }}</div>
      <el-form-item :label="$t('common.accountName')" prop="account">
        <el-input v-model="params.account"></el-input>
      </el-form-item>
      <el-form-item
        :label="$t('common.password', { text: $t('addaccount.index.81e2nd') })"
        prop="password"
        v-if="!currentId"
      >
        <el-input v-model="params.password"></el-input>
        <div style="font-size: 12px; color: #939393">{{
          $t('components.editpassword.p05m5w')
        }}</div>
      </el-form-item>
      <el-form-item :label="$t('components.useradd.ed168w')" prop="state">
        <el-radio-group v-model="params.state">
          <el-radio :label="0">{{ $t('components.useradd.467187') }}</el-radio>
          <el-radio :label="1">{{ $t('facehistory.index.74wtn2') }}</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div>
      <el-button type="primary" @click="saveData">{{
        $t('button.saveText', { text: '' })
      }}</el-button>
      <el-button @click="cancelFun">{{ $t('button.cancelText', { text: '' }) }}</el-button>
    </div>
  </div>
</template>
<script>
  import { listData } from '../../roleManagement/api';
  import { listTree, saveAccount, updateAccount, accountDetail } from '../api';

  export default {
    props: {
      currentId: {
        type: String,
        default: '',
      },
    },
    data() {
      return {
        roleOptions: [],
        depList: [],
        params: {
          name: '',
          staffNo: '',
          phone: '',
          account: '',
          password: '',
          state: null,
          departId: [],
          roleIds: '',
        },
        rules: {
          name: [
            { required: true, message: this.$t('facemanagement.index.j66758'), trigger: 'blur' },
          ],
          staffNo: [
            {
              required: true,
              message: this.$t('facemanagement.index.staffNoTip'),
              trigger: 'blur',
            },
          ],
          phone: [
            { required: true, message: this.$t('components.addphone.0l6nvu'), trigger: 'blur' },
          ],
          account: [
            { required: true, message: this.$t('components.useradd.2gqqoo'), trigger: 'blur' },
          ],
          password: [
            {
              required: true,
              message: this.$t('form.tip.inputPassword'),
              trigger: 'blur',
            },
            {
              validator: (rule, value, callback) => {
                const regex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[\W_]).{8,26}$/;
                if (!regex.test(value)) {
                  callback(new Error(this.$t('components.editpassword.vl95j6')));
                } else {
                  callback();
                }
              },
              trigger: 'blur',
            },
          ],
          state: [
            { required: true, message: this.$t('components.useradd.u845k1'), trigger: 'change' },
          ],
          departId: [
            { required: true, message: this.$t('components.useradd.96jbo7'), trigger: 'change' },
          ],
          roleIds: [
            { required: true, message: this.$t('components.useradd.4348pn'), trigger: 'change' },
          ],
        },
      };
    },
    async created() {
      await this.getRole();
      await this.getTree();
      if (this.currentId) {
        await this.getDetail();
      }
    },
    methods: {
      // 获取详情
      async getDetail() {
        let formData = new FormData();
        formData.append('accountId', this.currentId);
        const res = await accountDetail(formData);
        let str = '';
        if (res.data.roles && res.data.roles.length > 0) {
          res.data.roles.forEach((item) => {
            str = item.id;
          });
        }
        Object.assign(this.params, {
          id: res.data.id,
          name: res.data.name,
          phone: res.data.phone,
          account: res.data.account,
          staffNo: res.data.staffNo,
          state: res.data.state,
          departId: this.getFathersById(res.data.departId, this.depList),
          roleIds: str,
        });
      },
      // 上级部门回显
      getFathersById(id, data, prop = 'id') {
        var arrRes = [];
        const rev = (data, nodeId) => {
          for (var i = 0, length = data.length; i < length; i++) {
            const node = data[i];
            if (node[prop] === nodeId) {
              arrRes.unshift(node[prop]);
              return true;
            } else {
              if (node.children && node.children.length) {
                if (rev(node.children, nodeId)) {
                  arrRes.unshift(node[prop]);
                  return true;
                }
              }
            }
          }
          return false;
        };
        rev(data, id);
        return arrRes;
      },
      // 获取角色
      async getRole() {
        const res = await listData();
        this.roleOptions = res.data;
      },
      // 获取部门树
      async getTree() {
        const res = await listTree();
        if (res.data && res.data.length > 0) {
          this.depList = this.getData(res.data);
        }
      },
      getData(data) {
        data.forEach((item) => {
          if (item.children.length < 1) {
            item.children = undefined;
          } else {
            this.getData(item.children);
          }
        });
        return data;
      },
      // 新增
      saveData() {
        this.$refs.form.validate(async (valid) => {
          if (valid) {
            let len = this.params.departId.length - 1;
            let obj = {
              name: this.params.name,
              staffNo: this.params.staffNo,
              phone: this.params.phone,
              account: this.params.account,
              password: this.params.password,
              state: this.params.state,
              departId: this.params.departId ? this.params.departId[len] : '',
              roleIds: this.params.roleIds ? this.params.roleIds.split(',') : '',
            };
            if (this.currentId) {
              obj.id = this.params.id;
              const res = await updateAccount(obj);
            } else {
              const res = await saveAccount(obj);
            }
            this.$message.success(this.$t('button.saveText', { text: this.$t('common.success') }));
            this.$emit('close');
          } else {
            return false;
          }
        });
      },
      // 取消
      cancelFun() {
        this.$emit('close');
      },
    },
  };
</script>
<style scoped lang="scss">
  .user-cont {
    padding: 16px;
    .title {
      font-size: 16px;
      font-weight: bold;
      padding: 16px 0px;
    }
  }
</style>
