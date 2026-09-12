<template>
  <div class="organizational">
    <div class="flex-left">
      <div class="top-title">
        <div class="title">
          {{ $t('organizationalmanagement.index.xc96x5') }}
        </div>
        <el-button type="primary" v-if="!isShow" icon="el-icon-s-tools" @click="editFun">{{
          $t('common.edit', { text: '' })
        }}</el-button>
        <el-button type="primary" v-else @click="saveFun">{{
          $t('button.saveText', { text: '' })
        }}</el-button>
      </div>
      <div class="tree-cont">
        <el-input
          :placeholder="$t('groupview.index.b9nt64')"
          v-model="filterText"
          style="margin-bottom: 10px"
          suffix-icon="el-icon-search"
        ></el-input>
        <div
          class="top-title top-hover"
          :style="{ backgroundColor: isDefault ? '#F5F7FA' : '' }"
          @click="allClick()"
        >
          <div>{{ $t('common.allText') }}</div>
          <div
            class="btn-sty"
            style="margin-top: 0px !important"
            v-if="isShow && btnData.includes('organizational-add')"
            @click="addDep(0)"
          >
            {{ $t('button.addText', { count: '' }) }}
          </div>
        </div>
        <el-tree
          class="filter-tree"
          :data="treeData"
          :props="defaultProps"
          default-expand-all
          :filter-node-method="filterNode"
          ref="tree"
        >
          <div
            class="custom-tree-node"
            slot-scope="{ node, data }"
            style="width: 100%; line-height: 26px"
          >
            <div @click.stop="handleNodeClick(data)" class="label-color" style="width: 100%">
              {{ node.label }}
            </div>
            <div class="btn-sty" style="margin-top: 0px !important" v-if="isShow">
              <span
                class="mr-10"
                v-if="btnData.includes('organizational-add')"
                @click.stop="addDep(1, data)"
                >{{ $t('addproject.step2.459446') }}</span
              >
              <span
                class="mr-10"
                v-if="btnData.includes('organizational-edit')"
                @click.stop="editDep(data)"
                >{{ $t('common.edit', { text: '' }) }}</span
              >
              <span
                style="color: red"
                v-if="btnData.includes('organizational-delete')"
                @click.stop="delFun(data)"
                >{{ $t('annotationplatform.annotationgroupmanagement.5t6l7l') }}</span
              >
            </div>
          </div>
        </el-tree>
      </div>
    </div>
    <div class="flex-right">
      <div class="flex-item">
        <div class="user-title">
          {{ $t('organizationalmanagement.index.14q780') }}
        </div>
        <el-button type="primary" v-if="btnData.includes('user-add')" @click="addUserFun()">{{
          $t('common.add', { text: $t('common.user') })
        }}</el-button>
      </div>
      <Tables
        :pagination="pagination"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        @pageChange="pageChange"
        :rowSelection="rowSelection"
        :selections.sync="selectedRowKeys"
      >
        <div slot="header">
          <div class="seach-cont">
            <el-row>
              <el-col :span="18">
                <el-row>
                  <el-form label-position="right" label-width="80px">
                    <el-row>
                      <el-col :span="8">
                        <el-form-item :label="$t('common.name')">
                          <el-input
                            v-model="formatData.name"
                            :placeholder="$t('common.inputText')"
                          ></el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :span="8">
                        <el-form-item :label="$t('common.accountName')">
                          <el-input
                            v-model="formatData.account"
                            :placeholder="$t('common.inputText')"
                          ></el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :span="8">
                        <el-form-item :label="$t('common.caBelongingOrganization')">
                          <el-cascader
                            v-model="formatData.departIds"
                            :options="depList"
                            :props="{
                              value: 'id',
                              label: 'name',
                              multiple: true,
                              checkStrictly: true,
                            }"
                            :show-all-levels="false"
                            collapse-tags
                            clearable
                          >
                          </el-cascader>
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row v-if="isOpen">
                      <el-col :span="8">
                        <el-form-item :label="$t('login.phoneNumber')">
                          <el-input
                            v-model="formatData.phone"
                            :placeholder="$t('common.inputText')"
                          ></el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :span="8">
                        <el-form-item :label="$t('common.role')">
                          <el-select
                            style="width: 100%"
                            v-model="formatData.roleIds"
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
                      </el-col>
                    </el-row>
                  </el-form>
                </el-row>
              </el-col>
              <el-col
                :span="6"
                style="
                  padding-left: 20px;
                  display: flex;
                  align-items: center;
                  justify-content: space-between;
                "
              >
                <el-button type="primary" icon="el-icon-search" @click="getTable">{{
                  $t('button.queryText')
                }}</el-button>
                <el-button icon="el-icon-refresh" @click="reset">{{
                  $t('button.resetText', { text: '' })
                }}</el-button>
                <div style="cursor: pointer; font-size: 13px; color: #1A0808" @click="openFun()">
                  <span :class="[isOpen ? 'el-icon-arrow-up' : 'el-icon-arrow-down']"></span>
                  <span>{{
                    isOpen
                      ? $t('organizationalmanagement.index.q21v34')
                      : $t('organizationalmanagement.index.4mu22z')
                  }}</span>
                </div>
              </el-col>
            </el-row>
          </div>
          <div class="flex-item">
            <div class="item-sty" v-for="(items, ind) in staticsList" :key="ind">
              <div>{{ items.nameCh }}</div>
              <div class="num">{{ items.accountNum }}</div>
            </div>
          </div>
          <div v-if="selectedRowKeys.length > 0">
            <el-button
              >{{ $t('organizationalmanagement.index.g8265d') }}{{ selectedRowKeys.length
              }}{{ $t('bigscreen.mainleft.8ye8wj') }}</el-button
            >
            <el-button type="primary" v-if="btnData.includes('user-delete')" @click="delAll">{{
              $t('button.deleteText', { text: '' })
            }}</el-button>
          </div>
        </div>
        <div slot="operate" slot-scope="{ row }">
          <el-button
            type="text"
            v-if="btnData.includes('user-edit') && row.account != 'admin'"
            @click="editData(row)"
            >{{ $t('components.editpassword.233cje') }}</el-button
          >
          <el-button
            type="text"
            v-if="btnData.includes('user-edit')&&row.account != 'admin'"
            @click="editPasswordFun(row)"
            >{{ $t('organizationalmanagement.index.sv2dy1') }}</el-button
          >
          <el-button
            type="text"
            v-if="btnData.includes('user-delete') && row.account != 'admin'"
            style="color: #dd383e !important"
            @click="delData(row.id)"
            >{{ $t('button.deleteText', { text: '' }) }}</el-button
          >
        </div>
        <div slot="roles" slot-scope="{ row }">
          <div v-if="row.roles.length > 0">
            <el-tag type="success" v-for="(item, index) in row.roles" :key="index">{{
              item.nameCh
            }}</el-tag>
          </div>
        </div>
      </Tables>
    </div>
    <!-- 新增部门 -->
    <DepartmentAdd
      v-if="depAddVisible"
      :parentId="parentId"
      :currentId="currentId"
      @close="closeDep"
    />
    <!-- 新增用户 -->
    <el-drawer
      :title="title"
      :visible.sync="drawer"
      :wrapperClosable="false"
      :close-on-press-escape="false"
      :direction="direction"
      :before-close="handleClose"
    >
      <UserAdd
        v-if="drawer"
        :currentId="currentUserId"
        :currentdepId="currentdepId"
        @close="handleClose"
      />
    </el-drawer>
    <!-- 修改密码 -->
    <EditPassword
      v-if="editVisible"
      :currentName="currentUserName"
      :currentId="currentUserId"
      @close="passwordClose"
    />
  </div>
</template>
<script>
  import Tables from '@/components/Table/index.vue';

  import { listData } from '../roleManagement/api';
  import DepartmentAdd from './components/departmentAdd.vue';
  import EditPassword from './components/editPassword.vue';
  import UserAdd from './components/userAdd.vue';
  import { listTree, deleteData, accountList, statics, accountDelete, batchDelete } from './api';

  export default {
    components: {
      Tables,
      UserAdd,
      DepartmentAdd,
      EditPassword,
    },
    data() {
      return {
        depAddVisible: false,
        title: $t('common.add', { text: $t('common.user') }),
        filterText: '',
        treeData: [],
        staticsList: [],
        defaultProps: {
          children: 'children',
          label: 'name',
        },
        isShow: false, //组织按钮是否展示
        isDefault: true, //默认展示全部
        isOpen: false,
        pagination: {
          currentPage: 1,
          pageSize: 10,
          total: 0,
        },
        rowSelection: {
          type: 'checkbox',
          change: (selectedRowKeys, selectedRows) => {
            this.selectedRowKeys = selectedRowKeys;
            this.selectedRows = selectedRows;
          },
        },
        selectedRowKeys: [],
        selectedRows: [],
        loading: false,
        dataSource: [],
        columns: Object.freeze([
          {
            key: 'name',
            title: $t('common.name'),
            align: 'center',
          },
          // {
          //   key: 'staffNo',
          //   title: $t('facemanagement.index.staffNo'),
          //   align: 'center',
          // },
          {
            key: 'roles',
            title: $t('common.role'),
            align: 'center',
            slot: 'roles',
          },
          {
            key: 'departNames',
            title: $t('common.caBelongingOrganization'),
            align: 'center',
          },
          {
            key: 'state',
            title: $t('common.state'),
            align: 'center',
            render(h, { value }) {
              let obj = {
                0: this.$t('applicationMonitoring.accountmanagement.fi166w'),
                1: this.$t('applicationMonitoring.accountmanagement.r4bhe1'),
              };
              return h('span', [obj[value]]);
            },
          },
          {
            key: 'phone',
            title: $t('login.phoneNumber'),
            align: 'center',
          },
          {
            key: 'account',
            title: $t('common.accountName'),
            align: 'center',
          },
          {
            key: 'Base',
            title: $t('common.action', { text: '' }),
            align: 'center',
            flex: 'right',
            slot: 'operate',
            width: 200,
          },
        ]),
        formatData: {},
        drawer: false,
        direction: 'rtl',
        currentId: '',
        parentId: '',
        currentUserId: '',
        currentdepId: '',
        depList: [],
        roleOptions: [],
        btnData: [],
        btnObjList: [],
        editVisible: false,
        currentUserName: '',
      };
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      },
    },
    created() {
      this.getBtn();
      this.getListTree();
      this.getTable();
      this.getStatics();
      this.getRole();
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
      // 获取角色
      async getRole() {
        const res = await listData();
        this.roleOptions = res.data;
      },
      //获取组织树
      getListTree() {
        this.treeData = [];
        listTree().then((res) => {
          if (res.data && res.data.length > 0) {
            this.treeData = res.data;
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
      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      // 编辑
      editFun() {
        this.isShow = true;
      },
      // 保存
      saveFun() {
        this.isShow = false;
      },
      // 点击节点
      handleNodeClick(node) {
        this.isDefault = false;
        let arr = [];
        arr.push(node.id);
        this.formatData.departIds = [arr];
        this.getTable();
      },
      // 点击全部
      allClick() {
        this.isDefault = true;
        this.formatData.departIds = [];
        this.getTable();
      },
      // 组织新增
      addDep(type, data) {
        this.currentId = '';
        if (type == 1) {
          //点击节点
          this.parentId = data.id;
          this.depAddVisible = true;
        } else {
          //点击全部节点
          this.parentId = '0';
          this.depAddVisible = true;
        }
      },
      // 组织编辑
      editDep(data) {
        this.parentId = '';
        this.currentId = data.id;
        this.depAddVisible = true;
      },
      // 组织新增弹窗关闭
      closeDep() {
        this.currentId = '';
        this.parentId = '';
        this.depAddVisible = false;
        this.getListTree();
      },
      // 组织删除
      delFun(data) {
        let str =
          data.children && data.children.length > 0
            ? this.$t('organizationalmanagement.index.w49j88')
            : $t('modal.deleteSureText');
        this.$confirm(str, $t('common.prompt'), {
          confirmButtonText: $t('button.sureText', { text: '' }),
          cancelButtonText: $t('button.cancelText', { text: '' }),
          type: 'warning',
        })
          .then(async () => {
            const res = await deleteData({ id: data.id });
            this.$message.success($t('button.deleteText', { text: $t('common.success') }));
            this.getListTree();
          })
          .catch(() => {});
      },
      // 获取角色人员统计
      async getStatics() {
        const res = await statics();
        this.staticsList = res.data;
      },
      // 获取用户表格数据
      async getTable() {
        let obj = {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
          ...this.formatData,
        };
        let arr = [];
        if (obj.departIds && obj.departIds.length > 0) {
          obj.departIds.forEach((item, index) => {
            let len = item.length - 1;
            arr.push(item[len]);
          });
        }
        obj.departIds = arr;
        this.loading = true;
        const res = await accountList(obj);
        this.dataSource = res.data;
        this.pagination.total = parseInt(res.count);
        this.loading = false;
      },
      // 重置
      reset() {
        this.pagination.currentPage = 1;
        this.formatData = {};
        this.getTable();
      },
      pageChange(page, pageSize) {
        this.pagination.currentPage = page;
        this.pagination.pageSize = pageSize;
        this.getTable();
      },
      // 查询条件展开收起
      openFun() {
        this.isOpen = !this.isOpen;
      },
      /******************************/
      // 新增
      addUserFun() {
        this.title = $t('common.add', { text: $t('common.user') });
        this.currentUserId = '';
        this.drawer = true;
      },
      // 修改用户信息
      editData(row) {
        this.title = $t('common.edit', {
          text: this.$t('organizationalmanagement.index.xm31p1'),
        });
        this.currentUserId = row.id;
        this.currentdepId = row.departId;
        this.drawer = true;
      },
      // 修改密码
      editPasswordFun(row) {
        this.currentUserId = row.id;
        this.currentUserName = row.account;
        this.editVisible = true;
      },
      passwordClose() {
        this.editVisible = false;
        this.currentUserId = '';
        this.currentUserName = '';
        this.getTable();
      },
      // 单个删除
      delData(id) {
        this.$confirm(
          this.$t('organizationalmanagement.index.4sbzt6'),
          this.$t('organizationalmanagement.index.zm7sr1'),
          {
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
            type: 'warning',
          },
        )
          .then(() => {
            let formData = new FormData();
            formData.append('accountId', id);
            accountDelete(formData)
              .then((res) => {
                if (res.code == 0) {
                  this.$message.success(this.$t('organizationalmanagement.index.23bkq4'));
                  this.getTable();
                }
              })
              .catch((res) => {});
          })
          .catch(() => {});
      },
      // 批量删除
      delAll() {
        let len = this.selectedRowKeys.length;
        this.$confirm(
          this.$t('organizationalmanagement.index.4sbzt6'),
          this.$t('organizationalmanagement.index.d10ik2') +
            len +
            this.$t('organizationalmanagement.index.39956p'),
          {
            confirmButtonText: $t('button.sureText', { text: '' }),
            cancelButtonText: $t('button.cancelText', { text: '' }),
            type: 'warning',
          },
        )
          .then(() => {
            let Arr = [];
            let isDelete = true;
            this.selectedRows.forEach((item) => {
              Arr.push(item.id);
              if (item.account == 'admin') {
                isDelete = false;
              }
            });
            if (!isDelete) {
              this.$message.error(this.$t('organizationalmanagement.index.6g6054'));
              return;
            }
            let obj = {
              accountIds: Arr,
            };
            batchDelete(obj)
              .then((res) => {
                this.$message.success(this.$t('organizationalmanagement.index.23bkq4'));
                this.getTable();
                this.selectedRowKeys = [];
                this.selectedRows = [];
              })
              .catch((res) => {});
          })
          .catch(() => {});
      },
      handleClose() {
        this.currentUserId = '';
        this.currentdepId = '';
        this.drawer = false;
        this.getTable();
      },
    },
  };
</script>
<style scoped lang="scss">
  .organizational {
    display: flex;
    .flex-left {
      width: 280px;
      border-radius: 8px;
      background: #fff;
      padding: 16px;
      margin: 0px 10px 0 0;
      flex-shrink: 0;
      .top-title {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 13px;
        padding: 5px 10px;
      }
      .btn-sty {
        color: #E53935;
        cursor: pointer;
      }
      .top-hover:hover {
        background-color: #f5f7fa;
      }
      .title {
        font-size: 20px;
        font-weight: bold;
      }
      .tree-cont {
        margin-top: 16px;
      }
      .custom-tree-node {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
      }
      .mr-10 {
        margin-right: 10px;
      }
    }
    .flex-right {
      background: #fff;
      border-radius: 8px;
      padding: 16px;
      flex: 1;
      .flex-item {
        display: flex;
        align-items: center;
      }
      .user-title {
        font-size: 20px;
        color: #1A0808;
        font-weight: bold;
        margin-right: 20px;
      }
      .seach-cont {
        padding-top: 16px;
      }
      .item-sty {
        text-align: center;
        color: #1A0808;
        font-size: 12px;
        margin-right: 20px;
        margin-bottom: 10px;
        .num {
          font-size: 20px;
          color: #303133;
          font-weight: bold;
          margin-top: 5px;
        }
      }
    }
    :deep(.table-container) {
      padding: 0px !important;
    }
  }
</style>
