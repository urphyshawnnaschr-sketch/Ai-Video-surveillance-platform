<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('annotationplatform.incrementalrraining.ypb6nl')"
      :visible.sync="dialogVisible"
      width="1200px"
      @closed="closed"
    >
      <el-form label-width="80px" style="width: 600px; margin: 0 auto">
        <el-form-item :label="$t('addproject.step4.t346v6')">
          <div>
            <span style="margin-right: 10px"
              >{{ $t("addproject.step4.qs1f86")
              }}<el-input-number
                :controls="false"
                v-model="params.trainingBaseVo.begin"
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
                v-model="params.trainingBaseVo.end"
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
              v-model="params.trainingBaseVo.interval"
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
              :ratio="1"
            >
            </AlarmCard>
          </el-card>
        </el-col>
      </el-row>
      <div class="pagination">
        <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        ></el-pagination>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
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
      dialogVisible: true,
      params: {
        page: 1,
        limit: 10,
      },
      dataList: [],
      total: 0,
    };
  },
  created() {
    Object.assign(this.params, {
      trainingBaseVo: this.currentParams,
    });
    this.getPreview();
  },
  methods: {
    async getPreview() {
      const data = await getPreview(this.params);
      this.dataList = data.data;
      this.total = Number(data.count);
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getPreview();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getPreview();
    },
    handleParams(params) {
      try {
        return JSON.parse(params);
      } catch (err) {
        return [];
      }
    },
  },
};
</script>
<style scoped lang="scss">
.item {
  margin-bottom: 20px;
}

::v-deep .el-col-48 {
  width: 20%;
}
</style>
