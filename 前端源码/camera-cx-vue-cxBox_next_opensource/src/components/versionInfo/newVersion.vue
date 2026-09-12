<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="title"
    :visible.sync="dialogVisible"
    width="800px"
    @close="closed"
  >
    <div class="version-cont">
      <div class="tip-txt">
        {{ $t("versioninfo.newversion.nuv379") }}{{ versionObj.version
        }}{{ $t("versioninfo.newversion.v44517") }}
      </div>
      <div style="margin: 16px 0px">
        <el-table
          :data="tableData"
          border
          style="width: 100%"
          v-loading="loading"
        >
          <el-table-column
            v-for="(item, index) in titleList"
            :label="item"
            :prop="'temp' + index"
            :width="index == 0 ? '60' : index == 1 ? '150' : ''"
          ></el-table-column>
        </el-table>
      </div>
      <span
        style="cursor: pointer; color: #E53935; font-size: 13px"
        @click="historicalFun()"
        >{{ $t("versioninfo.newversion.2w5641") }}</span
      >
    </div>
    <!-- 查看历史版本 -->
    <HistoricalVersion v-if="isHistorical" @closeHistory="closeHistory" />
  </el-dialog>
</template>
<script>
import HistoricalVersion from "./historicalVersion.vue";
export default {
  components: {
    HistoricalVersion,
  },
  props: {
    title: {
      type: String,
      default: "",
    },
    versionObj: {
      type: Object,
      default: {},
    },
  },
  data() {
    return {
      dialogVisible: true,
      titleList: [],
      tableData: [],
      loading: false,
      isHistorical: false,
    };
  },
  watch: {
    versionObj: {
      immediate: true,
      deep: true,
      handler(val) {
        this.loading = true;
        if (val.notes && val.notes.length > 0) {
          let notesArr = JSON.parse(JSON.stringify(val.notes));
          this.titleList = notesArr[0];
          let Arr = notesArr.splice(1, notesArr.length);
          let newArr = [];
          if (Arr.length > 0) {
            Arr.forEach((item) => {
              let obj = { ...item };
              let newData = {};
              for (let key in obj) {
                newData["temp" + key] = obj[key];
              }
              newArr.push(newData);
            });
            this.tableData = newArr;
            console.log(newArr);
          } else {
            this.tableData = [];
          }
          this.loading = false;
        } else {
          this.tableData = [];
          this.titleList = [];
          this.loading = false;
        }
      },
    },
  },
  created() {},
  methods: {
    closed() {
      this.$emit("closeNewVersion");
    },
    historicalFun() {
      this.isHistorical = true;
    },
    closeHistory() {
      this.isHistorical = false;
    },
  },
};
</script>
<style scoped lang="scss">
.version-cont {
  .tip-txt {
    font-size: 13px;
    color: #000;
  }
  :deep(.el-table td.el-table__cell div) {
    white-space: pre-line;
  }
}
</style>
