<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    :close-on-click-modal="false"
    width="400px"
    @close="handleClose"
  >
    <!-- 用销毁重建整个表单，确保禁用元素重新渲染 -->
    <el-form
      v-if="visible"
      :model="form"
      :rules="formRules"
      ref="formRef"
      :label-width="isEnglish ? '160px' : '100px'"
    >
      <!-- 2. 区域等级（必选） -->
      <el-form-item :label="$t('systemManage.alarmPushConfiguration.areaGrade')" prop="groupLevel">
        <el-select style="width: 100%" v-model="form.groupLevel" :disabled="true">
          <el-option
            :label="$t('systemManage.alarmPushConfiguration.levelOne')"
            :value="1"
          ></el-option>
          <el-option
            :label="$t('systemManage.alarmPushConfiguration.levelTwo')"
            :value="2"
          ></el-option>
          <el-option
            :label="$t('systemManage.alarmPushConfiguration.levelThree')"
            :value="3"
          ></el-option>
        </el-select>
      </el-form-item>

      <!-- 3. 区域名称（必选） -->
      <el-form-item :label="$t('systemManage.alarmPushConfiguration.areaName')" prop="name">
        <el-input width="100%" v-model="form.name" :disabled="true"></el-input>
      </el-form-item>
      <!-- 4. 关联算法 -->
      <el-form-item
        :label="$t('systemManage.alarmPushConfiguration.associatedAlgorithm')"
        prop="algorithmIds"
      >
        <el-select
          style="width: 100%"
          v-model="form.algorithmIds"
          multiple
          :placeholder="$t('systemManage.alarmPushConfiguration.pleaseSelect')"
          @change="handleAlgorithmChange"
          filterable
        >
          <el-option
            v-for="item in algorithmOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id.toString()"
          ></el-option>
        </el-select>
        <el-input
          v-show="false"
          v-model="form.algorithmNames"
          :disabled="true"
          class="mt-2"
        ></el-input>
      </el-form-item>

      <!-- 5. 推送接收人 -->
      <el-form-item
        :label="$t('systemManage.alarmPushConfiguration.pushRecipient')"
        prop="responsiblePersonNo"
      >
        <el-select
          style="width: 100%"
          v-model="form.responsiblePersonNo"
          multiple
          :placeholder="$t('systemManage.alarmPushConfiguration.pleaseSelect')"
          @change="handlePeopleChange"
          filterable
        >
          <el-option
            v-for="item in personOptions"
            :key="item.id"
            :label="item.name"
            :value="item.staffNo.toString()"
          ></el-option>
        </el-select>
        <el-input
          v-show="false"
          v-model="form.responsiblePerson"
          :disabled="true"
          class="mt-2"
        ></el-input>
      </el-form-item>

      <!-- 7. 推送群组 -->
      <el-form-item
        v-if="form.groupLevel == 3"
        :label="$t('systemManage.alarmPushConfiguration.pushGroup')"
        prop="socialHookIds"
      >
        <el-select
          style="width: 100%"
          v-model="form.socialHookIds"
          multiple
          :placeholder="$t('systemManage.alarmPushConfiguration.pleaseSelect')"
          filterable
          @change="handleSocialChange"
        >
          <el-option
            v-for="item in socialHookOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id.toString()"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">{{
        $t('systemManage.alarmPushConfiguration.cancel')
      }}</el-button>
      <el-button type="primary" @click="handleFormSubmit">{{
        $t('systemManage.alarmPushConfiguration.confirm')
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import store from '@/store';

  export default {
    name: 'AreaFormDialog',
    props: {
      visible: { type: Boolean, default: false },
      isEditMode: { type: Boolean, default: false }, // 新增模式：为
      initialForm: { type: Object, default: () => ({}) },
      algorithmOptions: { type: Array, default: () => [] },
      personOptions: { type: Array, default: () => [] },
      socialHookOptions: { type: Array, default: () => [] },
    },
    data() {
      return {
        form: {
          id: '',
          groupId: '',
          groupLevel: '',
          name: '',
          algorithmIds: [],
          algorithmNames: '',
          responsiblePerson: '',
          responsiblePersonNo: [],
          socialHookIds: [],
          socialHookNames: '',
        },
      };
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
      title() {
        return this.isEditMode
          ? `${this.$t('systemManage.alarmPushConfiguration.editConfig')}`
          : `${this.$t('systemManage.alarmPushConfiguration.addConfig')}`;
      },
      formRules() {
        return {
          algorithmIds: [
            {
              required: true,
              message: `${this.$t('systemManage.alarmPushConfiguration.pleaseSelectAtAssociatedAlgorithm')}`,
              trigger: 'blur',
            },
          ],
          responsiblePersonNo: [
            {
              required:
                this.form.groupLevel !== 3 ||
                (this.form.groupLevel === 3 && !this.form.socialHookIds?.length),
              message: `${this.$t('systemManage.alarmPushConfiguration.pleaseSelectAtLeastOneRecipient')}`,
              trigger: 'blur',
            },
          ],
          socialHookIds: [
            {
              required: this.form.groupLevel === 3 && !this.form.responsiblePersonNo?.length,
              message: `${this.$t('systemManage.alarmPushConfiguration.pleaseSelectAtLeastOneGroup')}`,
              trigger: 'blur',
            },
          ],
        };
      },
    },
    watch: {
      initialForm: {
        handler(val) {
          this.form = {};
          this.$nextTick(() => {
            const newForm = JSON.parse(JSON.stringify(val));
            if (newForm.algorithmIds && typeof newForm.algorithmIds === 'string') {
              newForm.algorithmIds = newForm.algorithmIds.split(',');
            }
            if (newForm.responsiblePersonNo && typeof newForm.responsiblePersonNo === 'string') {
              newForm.responsiblePersonNo = newForm.responsiblePersonNo.split(',');
            }
            if (newForm.socialHookIds && typeof newForm.socialHookIds === 'string') {
              newForm.socialHookIds = newForm.socialHookIds.split(',');
            }
            Object.keys(newForm).forEach((key) => {
              this.$set(this.form, key, newForm[key]);
            });
          });
        },
        immediate: false,
        deep: true,
      },
    },
    methods: {
      handleAlgorithmChange(selectedIds) {
        const selectedNames = this.algorithmOptions
          .filter((alg) => selectedIds.includes(alg.id.toString()))
          .map((alg) => alg.name);
        this.form.algorithmNames = selectedNames.join(',');
      },
      handlePeopleChange(selectedIds) {
        const selectedNames = this.personOptions
          .filter((item) => selectedIds.includes(item.staffNo.toString()))
          .map((item) => item.name);
        this.form.responsiblePerson = selectedNames.join(',');
        // this.form.socialHookIds = [];
      },
      handleSocialChange() {
        // this.form.responsiblePerson = '';
        // this.form.responsiblePersonNo = [];
      },
      handleFormSubmit() {
        this.$refs.formRef.validate(async (isValid) => {
          if (!isValid) return;
          const submitData = {
            ...this.form,
            algorithmIds: this.form.algorithmIds.join(','),
            responsiblePersonNo: this.form.responsiblePersonNo.join(','),
            responsiblePerson: Array.isArray(this.form.responsiblePerson)
              ? this.form.responsiblePerson.join(',')
              : this.form.responsiblePerson,
            socialHookIds: Array.isArray(this.form.socialHookIds)
              ? this.form.socialHookIds.join(',')
              : this.form.socialHookIds,
          };
          this.$emit('submit', submitData);
        });
      },
      handleClose() {
        this.$emit('close');
        this.form = {
          id: '',
          groupId: '',
          groupLevel: '',
          name: '',
          algorithmIds: [],
          algorithmNames: '',
          responsiblePerson: '',
          responsiblePersonNo: [],
          socialHookIds: [],
          socialHookNames: '',
        };
        if (this.$refs.formRef) {
          this.$refs.formRef.resetFields(); // 重置校验状态
        }
      },
    },
  };
</script>

<style scoped lang="scss">
  ::v-deep .el-dialog {
    .el-dialog__title {
      font-size: 16px;
      font-weight: 500;
      color: #1A0808;
    }
    .el-dialog__body {
      padding: 20px 24px;
      overflow-y: auto;
      max-height: calc(100vh - 200px);
    }
    .el-form-item {
      margin-bottom: 16px;
      .el-form-item__help {
        color: #606266;
        font-size: 12px;
      }
    }
    .el-select {
      width: 100%;
      ::v-deep .el-input__inner {
        padding-left: 12px;
      }
    }
    .dialog-footer {
      display: flex;
      justify-content: flex-end;
      padding-top: 12px;
      border-top: 1px solid #f2f3f5;
    }
  }
</style>
