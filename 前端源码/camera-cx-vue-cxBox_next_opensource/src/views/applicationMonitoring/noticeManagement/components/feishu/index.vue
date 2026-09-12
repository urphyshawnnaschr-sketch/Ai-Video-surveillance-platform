<template>
  <div>
    <div style="padding: 0 10px">
      <el-button type="primary" @click="formAdd">{{
        $t('common.add', { text: $t('common.pushGroup') })
      }}</el-button>
    </div>
    <div>
      <Tables
        :pagination="pagination"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        @pageChange="pageChange"
        :rowSelection="false"
      >
        <div slot="index" slot-scope="{ $index }">
          {{ $index + 1 }}
        </div>
        <div slot="switchState" slot-scope="{ row }">
          <el-switch
            v-model="row.state"
            @change="(e) => doSwitchState(e, row)"
            :active-value="1"
            :inactive-value="0"
            disabled
          ></el-switch>
        </div>
        <div slot="type" slot-scope="{ row }">
          {{ getTypeLabel(row.type) }}
        </div>

        <div slot="operate" slot-scope="{ row }">
          <el-button type="text" @click="formEdit(row)">{{
            $t('annotationplatform.annotationgroupmanagement.5lbf3q')
          }}</el-button>
          <el-button type="text" style="color: #dd383e !important" @click="delData(row.id)">{{
            $t('button.deleteText', { text: '' })
          }}</el-button>
        </div>
      </Tables>
    </div>

    <!-- 表单 -->
    <Form v-if="formVisible" :currentId="currentId" @closeAdd="formClose"></Form>

    <!-- esb客户参数编辑 -->
    <EsbConfig v-if="esbVisible" @closeEsb="esbClose"></EsbConfig>
  </div>
</template>

<script>
  import { socialHookList, socialHookDel, socialHookSwitch } from '../../api.js';
  import Tables from '@/components/Table/index.vue';
  import Form from './form.vue';
  import EsbConfig from './esb_config.vue';
  export default {
    components: {
      Tables,
      Form,
      EsbConfig,
    },
    data() {
      return {
        pagination: false,
        loading: false,
        dataSource: [],
        columns: Object.freeze([
          {
            key: 'name',
            title: this.$t('dingding.form.j76q5x'),
            align: 'left',
          },
          {
            key: 'type',
            title: this.$t('feishu.form.typeLabel'),
            align: 'left',
            slot: 'type',
          },
          {
            key: 'webhook',
            title: this.$t('dingding.form.rih8yz'),
            align: 'left',
          },
          {
            key: 'state',
            title: this.$t('dingding.index.278wy1'),
            align: 'center',
            width: 100,
            slot: 'switchState',
          },
          {
            key: 'remark',
            title: $t('common.remark'),
            align: 'left',
          },
          {
            key: 'Base',
            title: $t('common.action', { text: '' }),
            align: 'center',
            width: 120,
            slot: 'operate',
          },
        ]),
        currentId: '',
        formVisible: false,
        esbVisible: false,

        typeOptions: [
          { label: 'feishu.form.typeAlarm', value: 0 },
          { label: 'feishu.form.typeBox', value: 1 },
          { label: 'feishu.form.typeCamera', value: 2 },
        ],
      };
    },
    created() {
      this.getSocialHookList();
    },
    methods: {
      pageChange(page) {
        //this.pagination.currentPage = page;
        this.getSocialHookList();
      },
      // 切换状态
      doSwitchState(e, row) {
        socialHookSwitch({ id: row.id })
          .then((res) => {
            this.$message.success($t('common.action', { text: $t('common.success') }));
            this.getSocialHookList();
          })
          .catch((err) => {
            this.getSocialHookList();
          });
      },
      // 删除
      delData(id) {
        this.$confirm(this.$t('dingding.index.1253hf'), $t('common.prompt'), {
          type: 'warning',
        })
          .then(() => {
            let formData = new FormData();
            formData.append('id', id);
            socialHookDel(formData).then((res) => {
              this.$message.success($t('button.deleteText', { text: $t('common.success') }));
              this.getSocialHookList();
            });
          })
          .catch(() => {});
      },
      // 获取列表
      getSocialHookList() {
        this.loading = true;
        socialHookList().then((res) => {
          this.loading = false;
          this.dataSource = res.data;
          // this.pagination.total = res.data.total;
        });
      },
      // 新增表单
      formAdd() {
        this.formVisible = true;
        this.currentId = '';
      },
      // 编辑表单
      formEdit(row) {
        this.formVisible = true;
        this.currentId = row.id;
      },
      // 关闭表单
      formClose() {
        this.formVisible = false;
        this.currentId = '';
        this.getSocialHookList();
      },
      // esb客户参数编辑
      esbEdit() {
        this.esbVisible = true;
      },
      // 关闭表单
      esbClose() {
        this.esbVisible = false;
      },
      getTypeLabel(type) {
        const target = this.typeOptions.find((item) => `${item.value}` === `${type}`);
        return target ? this.$t(target.label) : '--';
      },
    },
  };
</script>
