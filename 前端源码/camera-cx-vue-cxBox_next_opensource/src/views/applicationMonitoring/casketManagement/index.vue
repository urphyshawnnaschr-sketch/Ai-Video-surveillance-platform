<template>
  <div>
    <div v-if="dataSource.length > 0">
      <Tables
        :pagination="pagination"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        @pageChange="pageChange"
        :rowSelection="false"
      >
        <div slot="header" class="head-container">
          <el-form
            label-position="left"
            :style="{
              display: 'flex',
              alignItems: 'center',
              flexWrap: 'nowrap',
              gap: '15px',
              width: '100%',
              overflow: 'auto',
              padding: '5px 0',
            }"
            label-width="auto"
            size="mini"
          >
            <!-- 名称输入项 -->
            <el-form-item
              :label="$t('common.boxName') + ':'"
              style="min-width: 280px; margin: 0; white-space: nowrap; flex-shrink: 0"
              :label-width="isEnglish ? '70px' : '60px'"
            >
              <el-input
                :style="{ width: '200px' }"
                v-model="formaData.name"
                :placeholder="$t('form.tip.inputBoxName')"
              >
              </el-input>
            </el-form-item>

            <!-- 状态选择项 -->
            <el-form-item
              :label="$t('common.presence') + ':'"
              style="min-width: 280px; margin: 0; white-space: nowrap; flex-shrink: 0"
              :label-width="isEnglish ? '90px' : '60px'"
            >
              <el-select
                class="pr10"
                :style="{ width: '200px' }"
                v-model="formaData.status"
                :placeholder="$t('casketmanagement.index.l5my88')"
              >
                <el-option :label="$t('common.allText')" value=""> </el-option>
                <el-option :label="$t('common.online')" value="1"> </el-option>
                <el-option :label="$t('common.offline')" value="0"> </el-option>
              </el-select>
            </el-form-item>

            <!-- 所属组织选择项 -->
            <el-form-item
              :label="$t('common.caBelongingOrganization')"
              style="min-width: 320px; margin: 0; white-space: nowrap; flex-shrink: 0"
            >
              <el-cascader
                v-model="formaData.departIds"
                :options="depList"
                :props="{ value: 'id', label: 'name', multiple: true }"
                collapse-tags
                clearable
                style="width: 180px"
              >
              </el-cascader>
            </el-form-item>

            <!-- 中文环境-->
            <div
              v-if="!isEnglish"
              :style="{
                display: 'flex',
                justifyContent: 'flex-end',
              }"
            >
              <el-button class="pr10" type="primary" icon="el-icon-search" @click="getTable()">
                {{ $t('casketmanagement.index.pc8sql') }}
              </el-button>

              <el-button icon="el-icon-refresh" @click="reset()" style="white-space: nowrap">
                {{ $t('button.resetText', { text: '' }) }}
              </el-button>
            </div>
          </el-form>
          <!-- 英文环境换行 -->
          <div
            v-if="isEnglish"
            :style="{
              display: isEnglish ? 'flex' : 'inline-flex',
              justifyContent: 'flex-end',
              gap: '10px',
              width: isEnglish ? '100%' : 'auto',
              marginTop: isEnglish ? '5px' : '0',
            }"
          >
            <el-button
              class="pr10"
              type="primary"
              icon="el-icon-search"
              @click="getTable()"
              style="white-space: nowrap"
            >
              {{ $t('casketmanagement.index.pc8sql') }}
            </el-button>

            <el-button icon="el-icon-refresh" @click="reset()" style="white-space: nowrap">
              {{ $t('button.resetText', { text: '' }) }}
            </el-button>
          </div>
        </div>

        <div slot="operate" slot-scope="{ row }">
          <el-button type="text" v-if="btnData.includes('caske-edit')" @click="editData(row)">{{
            $t('common.edit', { text: '' })
          }}</el-button>
          <el-button
            type="text"
            v-if="btnData.includes('caske-restart')"
            @click="restartFun(row)"
            >{{ $t('casketmanagement.index.zcs24z') }}</el-button
          >
          <el-button type="text" v-if="row.activeStatus != 1" @click="activationFun(row)">{{
            $t('casketmanagement.index.e7413i')
          }}</el-button>
          <el-button
            type="text"
            v-if="btnData.includes('caske-delete')"
            @click="delFun(row)"
            style="color: #dd383e !important"
            >{{ $t('button.deleteText', { text: '' }) }}</el-button
          >
        </div>
      </Tables>
    </div>
    <div
      v-else
      style="background: #fff; border-radius: 6px; padding: 16px; height: calc(100vh - 180px)"
    >
      <el-button type="primary" v-if="btnData.includes('caske-add')" @click="addData()">{{
        $t('common.add', { text: $t('common.box') })
      }}</el-button>
      <div style="display: flex; align-items: center; justify-content: center; height: 100%">
        <div style="text-align: center">
          <img src="@/assets/images/empty-icon.png" style="width: 360px" />
          <div style="color: #1A0808; font-size: 13px; margin-top: 16px">
            {{ $t('casketmanagement.index.0nc237') }}
          </div>
        </div>
      </div>
    </div>
    <!-- 编辑 -->
    <el-dialog
      :close-on-click-modal="false"
      width="40%"
      :title="$t('common.edit', { text: '' })"
      :visible.sync="editVisible"
    >
      <el-form
        :model="ruleForm"
        :rules="rules"
        ref="ruleForm"
        :label-width="isEnglish ? '200px' : '80px'"
        size="mini"
      >
        <el-form-item :label="$t('common.boxName')" prop="name">
          <el-input v-model="ruleForm.name"></el-input>
        </el-form-item>
        <el-form-item :label="$t('common.ipAddress')" prop="ipAddr">
          <el-input v-model="ruleForm.ipAddr" :disabled="!isAdd"></el-input>
        </el-form-item>
        <el-form-item :label="$t('common.boxNumber')" prop="sn">
          <el-input
            :placeholder="$t('form.tip.inputBoxNumber')"
            :disabled="!isAdd"
            v-model="ruleForm.sn"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('common.caBelongingOrganization')" prop="departId">
          <el-cascader
            v-model="ruleForm.departId"
            :options="depList"
            :props="{ value: 'id', label: 'name' }"
            :show-all-levels="false"
            clearable
          >
          </el-cascader>
        </el-form-item>
        <el-form-item>
          <div style="text-align: right">
            <el-button @click="resetForm()">{{ $t('button.cancelText', { text: '' }) }}</el-button>
            <el-button type="primary" @click="submitForm('ruleForm')">{{
              $t('casketmanagement.index.m5ks68')
            }}</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- 查看详情 -->
    <DetailInfo v-if="detailVisible" :rowId="rowId" @close="closeHandle" />
    <!-- 资源监控 -->
    <ResourceMonitor v-if="resourceVisible" :rowId="rowId" @close="closeR" />
    <!-- 激活 -->
    <Activation v-if="activationVisible" :rowId="rowId" @close="closeA" />
  </div>
</template>
<script>
  import {
    listPage,
    detail,
    save,
    restart,
    stop,
    syncTime,
    upgrade,
    basicDelete,
  } from '@/api/applicationMonitoring/casketManagement';
  import { getMyDate } from '@/utils/common.js';
  import { saveBasic, listTree } from '@/api/applicationMonitoring/boxManagement';
  import Tables from '@/components/Table/index.vue';
  import store from '@/store';

  import Activation from './components/activation.vue';
  import DetailInfo from './components/detail.vue';
  import ResourceMonitor from './components/resourceMonitor.vue';

  export default {
    data() {
      return {
        activationVisible: false,
        detailVisible: false,
        resourceVisible: false,
        rowId: '',
        editVisible: false,
        ruleForm: {
          id: '',
          name: '',
          ipAddr: '',
          sn: '',
          departId: '',
        },
        rules: {
          name: [
            {
              required: true,
              message: $t('form.tip.inputBoxName'),
              trigger: 'blur',
            },
          ],
          ipAddr: [
            {
              required: true,
              message: this.$t('casketmanagement.index.gphb71'),
              trigger: 'blur',
            },
          ],
          sn: [
            {
              required: true,
              message: this.$t('casketmanagement.index.685848'),
              trigger: 'blur',
            },
          ],
          departId: [
            {
              required: true,
              message: this.$t('casketmanagement.index.ml29wi'),
              trigger: 'change',
            },
          ],
        },
        pagination: {
          currentPage: 1,
          pageSize: 10,
          total: 0,
        },
        loading: false,
        dataSource: [],
        columns: Object.freeze([
          {
            key: 'name',
            title: this.$t('components.detail.fwu578'),
            align: 'center',
          },
          {
            key: 'boxNo',
            title: $t('common.boxNumber'),
            align: 'center',
          },
          {
            key: 'platform',
            title: this.$t('casketmanagement.index.7r8zc6'),
            align: 'center',
          },
          {
            key: 'ipAddr',
            title: this.$t('casketmanagement.index.f021j5'),
            align: 'center',
          },
          {
            key: 'departName',
            title: $t('common.caBelongingOrganization'),
            align: 'center',
          },
          // {
          //     key: "resourceMonitor",
          //     title: "资源监控",
          //     align: "center",
          //     slot:'resourceMonitor'
          // },
          {
            key: 'online',
            title: $t('common.presence'),
            align: 'center',
            render(h, { value }) {
              const str = {
                0: $t('common.offline'),
                1: $t('common.online'),
              }[value];
              return h('span', [str]);
            },
          },
          {
            key: 'activeStatus',
            title: this.$t('casketmanagement.index.klq4mb'),
            align: 'center',
            render(h, { value }) {
              const str = {
                0: $t('applicationMonitoring.casketManagement.notActive'),
                1: $t('applicationMonitoring.casketManagement.active'),
              }[value];
              return h('span', [str]);
            },
          },
          {
            key: 'boxHeartTime',
            title: this.$t('applicationMonitoring.boxManagement.c1rxbp'),
            align: 'center',
            render(h, { value }) {
              return h('span', [getMyDate(Number(value))]);
            },
          },
          {
            key: 'useType',
            title: this.$t('applicationMonitoring.boxManagement.usage'),
            align: 'center',
            render(h, { value }) {
              const str = {
                0: $t('applicationMonitoring.boxManagement.usageInfer'),
                1: $t('applicationMonitoring.boxManagement.usageFace'),
                2: $t('applicationMonitoring.boxManagement.usageAll'),
              }[value];
              return h('span', [str]);
            },
          },
          {
            key: 'Base',
            title: $t('common.action', { text: '' }),
            width: 220,
            align: 'center',
            flex: 'right',
            slot: 'operate',
          },
        ]),
        formaData: {},
        depList: [],
        isAdd: false,
        btnData: [],
        btnObjList: [],
      };
    },
    components: {
      Tables,
      DetailInfo,
      ResourceMonitor,
      Activation,
    },
    computed: {
      isEnglish() {
        const getLanguage = () => {
          const langMap = {
            'zh-CN': 'zh-CN',
            en: 'en-US',
          };
          return langMap[store.state.locale] || 'zh-CN';
        };
        return getLanguage().startsWith('en');
      },
    },
    created() {
      this.getBtn();
      this.getTable();
      this.getTree();
    },
    methods: {
      getBtn() {
        this.btnData = [];
        this.btnObjList = [];
        this.isDetail = false;
        const menuArr = JSON.parse(sessionStorage.getItem('menuTree'));
        let newArr = [];
        this.getbtnList(menuArr);
        this.btnObjList.filter((item, index) => {
          newArr.push(item.auth);
        });
        this.btnData = newArr;
      },
      getbtnList(data) {
        let arr = [];
        data.forEach((item) => {
          if (item.path == this.$route.path) {
            arr = item.children.filter((items, ind) => {
              return items.type == 2;
            });
            this.btnObjList = arr;
          } else {
            this.getbtnList(item.children);
          }
        });
      },
      pageChange(page, pageSize) {
        this.pagination.currentPage = page;
        this.pagination.pageSize = pageSize;
        this.getTable();
      },
      async getTable() {
        let obj = {
          ...this.formaData,
          limit: this.pagination.pageSize,
          page: this.pagination.currentPage,
        };
        let arr = [];
        if (obj.departIds && obj.departIds.length > 0) {
          obj.departIds.forEach((item, ind) => {
            let len = item.length - 1;
            arr.push(item[len]);
          });
        }
        obj.departIds = arr.length > 0 ? arr.join(',') : '';
        this.loading = true;
        const { data, count } = await listPage(obj);
        this.dataSource = data;
        this.pagination.total = parseInt(count);
        this.loading = false;
      },
      reset() {
        this.formaData = {};
        this.pagination.currentPage = 1;
        this.getTable();
      },
      addData() {
        this.isAdd = true;
        ((this.ruleForm = {
          id: '',
          name: '',
          ipAddr: '',
          sn: '',
          departId: '',
        }),
          (this.editVisible = true));
        this.getTree();
      },
      // 编辑
      async editData(row) {
        this.depList = [];
        this.isAdd = false;
        this.getTree();
        const res = await detail({ id: row.id });
        if (res.code == 0) {
          this.ruleForm = {
            id: res.data.id,
            name: res.data.name,
            ipAddr: res.data.ipAddr,
            sn: res.data.boxNo,
            departId: this.getFathersById(res.data.departId, this.depList),
          };
          this.editVisible = true;
        }
      },
      // 保存
      submitForm(formName) {
        console.log(this.ruleForm);
        console.log(this.ruleForm.departId);
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.saveData();
          } else {
            return false;
          }
        });
      },
      async saveData() {
        let obj = {
          id: this.ruleForm.id,
          name: this.ruleForm.name,
          ipAddr: this.ruleForm.ipAddr,
          sn: this.ruleForm.sn,
        };
        if (this.ruleForm.departId && this.ruleForm.departId.length > 0) {
          let len = this.ruleForm.departId.length - 1;
          obj.departId = this.ruleForm.departId ? this.ruleForm.departId[len] : '';
        }
        const res = await saveBasic(obj);
        if (res.code == 0) {
          this.$message.success($t('button.saveText', { text: $t('common.success') }));
          this.editVisible = false;
          this.getTable();
        }
      },
      // 取消
      resetForm() {
        this.editVisible = false;
        this.getTable();
      },
      // 重启
      async restartFun(row) {
        this.$confirm(this.$t('casketmanagement.index.if1132'), $t('common.prompt'), {
          confirmButtonText: $t('button.sureText', { text: '' }),
          cancelButtonText: $t('button.cancelText', { text: '' }),
          type: 'warning',
        })
          .then(() => {
            let formData = new FormData();
            formData.append('id', row.id);
            restart(formData).then((res) => {
              this.$message.success(this.$t('casketmanagement.index.1q56j6'));
              this.getTable();
            });
          })
          .catch(() => {});
      },
      // // 停止
      // async ceaseFun(row){
      //     this.$confirm(`确定停止?`,  $t('common.prompt'), {
      //         confirmButtonText: $t('button.sureText', { text: '' }),
      //         cancelButtonText: $t('button.cancelText', { text: '' }),
      //         type: "warning",
      //     })
      //     .then(async () => {
      //         const res = await stop({id: row.id});
      //         if (res.code == 0) {
      //             this.$message.success($t('common.action', {text: $t('common.success')}));
      //             this.getTable();
      //         }
      //     })
      //     .catch(() => {});
      // },
      // 查看详情
      detailFun(row) {
        this.rowId = row.id;
        this.detailVisible = true;
      },
      // 关闭详情弹窗
      closeHandle() {
        this.getTable();
        this.detailVisible = false;
      },
      //资源监控
      viewFun(row) {
        this.rowId = row.id;
        this.resourceVisible = true;
      },
      // 关闭弹窗
      closeR() {
        this.resourceVisible = false;
        this.getTable();
      },
      // 同步时间
      // async synchronizeFun(row){
      //     this.$confirm(`确定同步时间?`,  $t('common.prompt'), {
      //         confirmButtonText: $t('button.sureText', { text: '' }),
      //         cancelButtonText: $t('button.cancelText', { text: '' }),
      //         type: "warning",
      //     })
      //     .then(async () => {
      //         const res = await syncTime({id: row.id});
      //         if (res.code == 0) {
      //             this.$message.success($t('common.action', {text: $t('common.success')}));
      //             this.getTable();
      //         }
      //     })
      //     .catch(() => {});
      // },
      // // 升级
      // async upgradationFun(row){
      //     this.$confirm(`确定升级?`,  $t('common.prompt'), {
      //         confirmButtonText: $t('button.sureText', { text: '' }),
      //         cancelButtonText: $t('button.cancelText', { text: '' }),
      //         type: "warning",
      //     })
      //     .then(async () => {
      //         const res = await upgrade({id: row.id});
      //         if (res.code == 0) {
      //             this.$message.success($t('common.action', {text: $t('common.success')}));
      //             this.getTable();
      //         }
      //     })
      //     .catch(() => {});
      // },

      // 获取部门树
      getTree() {
        listTree().then((res) => {
          if (res.data && res.data.length > 0) {
            this.depList = this.getData(res.data);
          }
        });
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
      // 删除
      async delFun(row) {
        this.$confirm(
          this.$t('casketmanagement.index.h38xtn'),
          this.$t('casketmanagement.index.s6p859'),
          {
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
            // type: "warning",
          },
        )
          .then(async () => {
            const res = await basicDelete({ id: row.id });
            if (res.code == 0) {
              this.$message.success($t('button.deleteText', { text: $t('common.success') }));
              this.getTable();
            }
          })
          .catch(() => {});
      },
      // 激活
      activationFun(row) {
        this.rowId = row.id;
        this.activationVisible = true;
      },
      closeA() {
        console.log('closeA closeA closeA');
        this.activationVisible = false;
        this.rowId = '';
        this.getTable();
      },
    },
  };
</script>
<style scoped lang="scss">
  .pr10 {
    padding-right: 10px;
  }
  .head-container {
    background: #fff;
    padding: 16px 10px;
    :deep(.el-form-item--mini.el-form-item) {
      margin-bottom: 0px !important;
    }
  }
  .margin15 {
    margin: 0px 15px;
  }
</style>
