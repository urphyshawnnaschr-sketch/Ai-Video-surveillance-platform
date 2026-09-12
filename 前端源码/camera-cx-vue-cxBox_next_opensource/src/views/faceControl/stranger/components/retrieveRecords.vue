<template>
  <div class="app-container">
    <div class="subheading">
      <div class="text">{{$t('components.retrieverecords.8fvffu')}}</div>
      <el-button type="primary" size="small" @click="handleRetrieve">Search</el-button>
    </div>
    <el-table :data="searchData" style="width: 100%">
      <el-table-column :label="$t('components.retrieverecords.gq7174')" align="center" prop="img">
        <template slot-scope="scope">
          <el-image style="width: 80px; height: 80px" :src="getFaceTrackSearchImage(scope.row.filename)"></el-image>
        </template>
      </el-table-column>
      <el-table-column :label="$t('components.retrieverecords.6d3qu6')" align="center" sortable prop="createdAt"></el-table-column>
      <el-table-column :label="$t('common.action', { text: '' })" align="center" width="50">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">{{$t('casketmanagement.index.g6ttgh')}}</el-button>
        </template>
      </el-table-column>

      <div class="table-empty" slot="empty">
        <img src="@/assets/images/no-data.png" :alt="$t('components.detail.26y57g')">
        <p>{{$t('components.retrieverecords.1499rp')}}</p>
      </div>
    </el-table>

    <!-- 检索表单 -->
    <retrieveForm ref="retrieveForm"></retrieveForm>
  </div>
</template>
<script>
import Cookies from "js-cookie";
import retrieveForm from './retrieveForm.vue';
import { searchPage } from '../api'
export default {
  data() {
    return {
      // 搜索记录数据
      searchData: [],
      VUE_APP_API_BASE_URL,
    };
  },
  created() {
    this.getTable();
  },
  methods: {
    // 处理检索按钮点击
    handleRetrieve() {
      this.$refs.retrieveForm.openDialog();
    },
    // 处理查看按钮点击
    handleView(row) {
      this.$emit('getInfo',row)
    },
    async getTable(){
      const res = await searchPage({page:1,limit:10});
      this.searchData = res.data;
    },
    // 获取图片
    getFaceTrackSearchImage(filename) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/face/track/search/image?filename=${filename}&X-Token=${token}`;
    },
  },
  components: {
    retrieveForm,
  },
};
</script>
<style scoped lang="scss">
::v-deep .el-table__empty-block {
  .el-table__empty-text {
    width: 100%;
    line-height: normal;

    img {
      width: 100%;
      margin-top: 20px;
      margin-bottom: 16px;
    }

    p {
      height: max-content;
      margin-bottom: 20px;
    }
  }
}
</style>
