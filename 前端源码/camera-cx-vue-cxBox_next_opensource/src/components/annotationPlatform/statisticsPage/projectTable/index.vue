<template>
  <div>
    <el-table :data="newTableData" border style="margin-top: 20px">
      <el-table-column :label="$t('projecttable.index.mq4p10')" prop="userName"></el-table-column>
      <el-table-column :label="$t('applicationMonitoring.accountmanagement.vktp69')" prop="roleName"></el-table-column>
      <el-table-column :label="$t('projecttable.index.4017n8')" prop="annoNum"></el-table-column>
      <el-table-column :label="$t('projecttable.index.5g01qt')" prop="annoDetailNum"></el-table-column>
      <el-table-column :label="$t('projecttable.index.82rnrq')" prop="invalidNum"></el-table-column>
      <el-table-column :label="$t('projecttable.index.140svw')" prop="reviewNum"></el-table-column>
      <el-table-column :label="$t('projecttable.index.23lxf4')" prop="backNum"></el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  props: {
    tableData: Array,
  },
  data() {
    return {
      newTableData: [],
    };
  },
  watch: {
    tableData: {
      immediate: true,
      deep: true,
      handler(val) {
        this.newTableData = val;
      },
    },
  },
  methods: {},
};
</script>
<style scoped lang="scss">
.chart {
  width: 100%;
  height: 348px;
}
</style>
