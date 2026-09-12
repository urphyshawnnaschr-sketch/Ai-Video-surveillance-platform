<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="visible"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="110px" label-position="right">
      <el-form-item
        :label="$t('noticemanagement.cameraSensorConfiguration.sensorCode')"
        prop="sensorCode"
      >
        <el-input
          v-model="form.sensorCode"
          :disabled="isEdit"
          :placeholder="$t('noticemanagement.cameraSensorConfiguration.pleaseEnterSensorCode')"
        ></el-input>
      </el-form-item>
      <el-form-item
        :label="$t('noticemanagement.cameraSensorConfiguration.sensorName')"
        prop="sensorName"
      >
        <el-input
          v-model="form.sensorName"
          :placeholder="$t('noticemanagement.cameraSensorConfiguration.pleaseEnterSensorName')"
        ></el-input>
      </el-form-item>
      <el-form-item :label="$t('noticemanagement.cameraSensorConfiguration.cameraName')">
        <el-select
          style="width: 100%"
          v-model="form.cameraId"
          :placeholder="$t('noticemanagement.cameraSensorConfiguration.pleaseEnterCameraId')"
          filterable
          clearable
          @change="handleCameraChange"
        >
          <el-option
            v-for="item in cameraListOptions"
            :key="item.cameraId"
            :label="item.cameraName"
            :value="item.cameraId"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="handleSubmit"
        >{{ $t('noticemanagement.cameraSensorConfiguration.confirm') }}
      </el-button>
      <el-button @click="handleClose"
        >{{ $t('noticemanagement.cameraSensorConfiguration.cancel') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
  export default {
    name: 'SensorFormDialog',
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      isEdit: {
        type: Boolean,
        default: false,
      },
      formData: {
        type: Object,
        default: () => ({
          sensorCode: '',
          sensorName: '',
          cameraId: null,
          cameraName: null,
        }),
      },
      cameraListOptions: {
        type: Array,
        default: [],
      },
    },
    data() {
      return {
        form: {
          sensorCode: '',
          sensorName: '',
          cameraId: null,
          cameraName: null,
        },
        rules: {
          sensorCode: [
            {
              required: true,
              message: `${$t('noticemanagement.cameraSensorConfiguration.pleaseEnterSensorCode')}`,
              trigger: 'blur',
            },
          ],
          sensorName: [
            {
              required: true,
              message: `${$t('noticemanagement.cameraSensorConfiguration.pleaseEnterSensorName')}`,
              trigger: 'blur',
            },
          ],
        },
      };
    },
    computed: {
      dialogTitle() {
        return this.isEdit
          ? $t('noticemanagement.cameraSensorConfiguration.editSensorConfiguration')
          : $t('noticemanagement.cameraSensorConfiguration.addSensorConfiguration');
      },
    },
    watch: {
      formData: {
        handler(val) {
          this.form = { ...val };
        },
        immediate: true,
      },
    },
    methods: {
      handleCameraChange(val) {
        if (!val) {
          this.form.cameraName = null;
          return;
        }

        const cameraName = this.cameraListOptions.filter((item) => item.cameraId === val)[0]
          .cameraName;
        this.form.cameraName = cameraName;
      },
      handleClose() {
        this.$refs.form.clearValidate();
        this.$emit('close');
      },

      handleSubmit() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.$emit('submit', { ...this.form });
          }
        });
      },
    },
  };
</script>

<style scoped>
  .dialog-footer {
    text-align: right;
  }
</style>
