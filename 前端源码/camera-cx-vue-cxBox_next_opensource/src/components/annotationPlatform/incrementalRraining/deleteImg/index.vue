<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('deleteimg.index.w37q30')"
      :visible.sync="dialogVisible"
      width="1200px"
      top="10vh"
      @closed="closed"
    >
      <div>
        <el-form ref="form" :model="params" label-width="80px">
          <el-form-item :label="$t('deleteimg.index.7ggfy7')">
            <el-input-number
              :controls="false"
              v-model="params.trainingBaseVo.interval"
              style="width: 100px"
              @change="getPreview"
            ></el-input-number>{{$t('deleteimg.index.se78jv')}}</el-form-item>
          <el-form-item :label="$t('addproject.step4.t346v6')">
            <span style="margin-right: 10px">{{$t('addproject.step4.qs1f86')}}<el-input-number
                :controls="false"
                v-model="params.trainingBaseVo.begin"
                :placeholder="$t('addproject.step4.qs1f86')"
                style="width: 100px"
                clearable
                @change="getPreview"
              >
              </el-input-number>
            </span>
            <span style="margin-right: 10px">{{$t('addproject.step4.gn1245')}}<el-input-number
                :controls="false"
                v-model="params.trainingBaseVo.end"
                :placeholder="$t('addproject.step4.gn1245')"
                style="width: 100px"
                clearable
                @change="getPreview"
              >
              </el-input-number>
            </span>
          </el-form-item>
          <el-form-item :label="$t('deleteimg.index.gq8err')" prop="name">
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
                  :fileUrl="
                    $common.handlePublicUrl(`/report/stream?id=${item.id}`)
                  "
                  :dataList="handleParams(item.params)"
                  :alarmData="item"
                >
                </AlarmCard>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="deleteImg">{{
          $t("button.deleteText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  getPreview,
  deleteImg,
} from "@/api/annotationPlatform/incrementalRraining";
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
      dialogVisible: true,
      params: {},
      dataList: [],
      total: 0,
    };
  },
  async created() {
    Object.assign(this.params, {
      trainingBaseVo: this.currentParams,
    });
    this.getPreview();
  },
  methods: {
    async deleteImg() {
      const data = await deleteImg(this.params);
      this.$message.success(
        $t("button.deleteText", { text: $t("common.success") })
      );
      this.closed();
    },
    async getPreview() {
      const params = {
        page: 1,
        limit: 99999,
        ...this.currentParams,
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
    // 关闭回调
    closed() {
      this.$emit("close");
    },
  },
};
</script>
<style scoped lang="scss">
.dataList {
  height: 400px;
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
