<template>
  <div class="step4">
    <el-form
      ref="step4Form"
      :model="step4Form"
      label-width="80px"
      style="width: 600px; margin: 0 auto"
    >
      <el-form-item :label="$t('addproject.step4.t346v6')">
        <div>
          <span style="margin-right: 10px"
            >{{ $t("addproject.step4.qs1f86")
            }}<el-input-number
              :controls="false"
              v-model="step4Form.begin"
              :placeholder="$t('addproject.step4.qs1f86')"
              style="width: 100px"
              clearable
              @change="getPreview"
            >
            </el-input-number>
          </span>
          <span style="margin-right: 10px"
            >{{ $t("addproject.step4.gn1245")
            }}<el-input-number
              :controls="false"
              v-model="step4Form.end"
              :placeholder="$t('addproject.step4.gn1245')"
              style="width: 100px"
              clearable
              @change="getPreview"
            >
            </el-input-number>
          </span>
        </div>
        <div style="color: #E53935">{{ $t("addproject.step4.70ju1h") }}</div>
      </el-form-item>
      <el-form-item :label="$t('addproject.step4.w34wt4')">
        <div>
          <el-input-number
            :controls="false"
            v-model="step4Form.interval"
            style="width: 100px"
            @change="getPreview"
          ></el-input-number
          >{{ $t("addproject.step4.qiurr2") }}
        </div>
        <div style="color: #E53935">{{ $t("addproject.step4.yo1ieo") }}</div>
      </el-form-item>
      <el-form-item :label="$t('addproject.step4.3s7bmj')" prop="name">
        {{ total }}
      </el-form-item>
    </el-form>
    <div class="dataList">
      <el-row :gutter="20">
        <el-col
          :span="48"
          v-for="(item, index) in dataList"
          :key="index"
          class="item"
        >
          <el-card :body-style="{ padding: '0px' }" shadow="hover">
            <AlarmCard
              :fileUrl="$common.handlePublicUrl(`/report/stream?id=${item.id}`)"
              :dataList="handleParams(item.params)"
              :alarmData="item"
            >
            </AlarmCard>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { getPreview } from "@/api/annotationPlatform/incrementalRraining";
import AlarmCard from "@/components/applicationMonitoring/alarmManagement/alarmCard";
export default {
  components: {
    AlarmCard,
  },
  props: {
    currentParams: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      step4Form: {
        interval: "",
        begin: "",
        end: "",
      },
      submitData: {},
      dataList: [],
      total: 0,
    };
  },
  async created() {
    Object.assign(this.step4Form, this.currentParams);
    this.getPreview();
  },
  mounted() {},
  methods: {
    async getPreview() {
      const params = {
        page: 1,
        limit: 99999,
        ...this.step4Form,
      };
      const data = await getPreview({ trainingBaseVo: params });
      this.dataList = data.data;
      this.total = Number(data.count);
    },
    handleParams(params) {
      try {
        return JSON.parse(params);
      } catch (err) {
        return [];
      }
    },
    async submit() {
      this.submitData = this.step4Form;
      return true;
    },
  },
};
</script>
<style scoped lang="scss">
.step4 {
  padding: 20px 0;
  margin: 0 auto;

  .demo {
    img {
      width: 100%;
      display: block;
      margin: 10px 0;
    }

    p {
      line-height: 20px;
      margin: 0;

      &.first {
        margin-top: 20px;
      }
    }
  }
}

.dataList {
  height: 320px;
  overflow-x: hidden;
  overflow-y: auto;
}

.item {
  margin-bottom: 20px;
}

::v-deep .el-col-48 {
  width: 20%;
}
</style>
