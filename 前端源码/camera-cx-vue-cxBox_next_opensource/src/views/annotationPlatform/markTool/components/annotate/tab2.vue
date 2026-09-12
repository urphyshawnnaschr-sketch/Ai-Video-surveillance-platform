<template>
  <div class="tab-wrap">
    <ul class="tag-list">
      <li
        v-for="(item, index) in attributeList"
        :key="index"
        :style="{
          background:
            currentTagList.indexOf(item.name) > -1
              ? '#E53935'
              : 'rgba(207, 213, 216, 0.6)',
        }"
        @click="clickTag(item)"
      >
        {{ item.name }}
      </li>
    </ul>
    <el-table
      :data="list"
      :show-header="false"
      highlight-current-row
      @current-change="handleCurrentChange"
      border
      style="margin-top: 20px"
      ref="singleTable"
    >
      <el-table-column width="40" align="center">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="tagName"></el-table-column>
      <el-table-column align="center">
        <template slot-scope="scope">
          <div class="button-list">
            <i
              class="icon-aios_eye"
              v-if="scope.row.svgVisible && scope.row.tagVisible"
              @click.stop="clickVisible(scope.row)"
            ></i>
            <i
              class="icon-aios_eyeclose"
              v-if="!scope.row.svgVisible || !scope.row.tagVisible"
              @click="clickVisible(scope.row)"
            ></i>
            <i class="icon-aios_shanchu" @click="clickDelete(scope.row)"></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  props: {
    dataList: {
      type: Array,
      default: () => [],
    },
    attributeList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      currentTagList: [],
      list: [],
    };
  },
  watch: {
    attributeList: {
      deep: true,
      handler(val) {
        val.forEach((item) => {
          this.currentTagList.push(item.name);
        });
      },
    },
    dataList: {
      deep: true,
      handler(val) {
        console.info(val);
        this.list = val;
      },
    },
  },
  created() {},
  methods: {
    // 点击标签
    clickTag(obj) {
      const i = this.currentTagList.indexOf(obj.name);
      if (i > -1) {
        this.currentTagList.splice(i, 1);
        this.list.forEach((item) => {
          if (obj.name == item.tagName) {
            item.svgVisible = false;
            item.tagVisible = false;
            this.$emit("changeVisible", item);
          }
        });
      } else {
        this.currentTagList.push(obj.name);
        this.list.forEach((item) => {
          if (obj.name == item.tagName) {
            item.svgVisible = true;
            item.tagVisible = true;
            this.$emit("changeVisible", item);
          }
        });
      }
    },
    // 点击显示隐藏
    clickVisible(item) {
      item.svgVisible = !item.svgVisible;
      item.tagVisible = !item.tagVisible;
      this.$emit("changeVisible", item);
    },
    // 点击删除
    clickDelete(item) {
      for (let i = 0; i < this.list.length; i++) {
        if (item.id == this.list[i].id) {
          this.list.splice(i, 1);
          break;
        }
      }
    },
    // 设置当前为当前表格
    setCurrent(row) {
      this.list.forEach((item) => {
        if (item.id == row.id) {
          row.svgVisible = item.svgVisible;
          row.tagVisible = item.tagVisible;
          this.$refs.singleTable.setCurrentRow(row);
        }
      });
    },
    removeCurrent() {
      this.$refs.singleTable.setCurrentRow();
    },
    // 选中表格
    handleCurrentChange(val) {
      if (val) {
        val.svgVisible = true;
        val.tagVisible = true;
        this.$emit("changeCurrent", val);
      }
    },
  },
};
</script>
<style scoped lang="scss">
.tab-wrap {
  padding: 20px;
  .tag-list {
    display: flex;
    flex-wrap: wrap;
    li {
      height: 28px;
      line-height: 28px;
      font-size: 13px;
      color: #fff;
      border-radius: 5px;
      padding: 0 10px;
      margin: 10px 10px 0 0;
      cursor: pointer;
    }
  }
}
.button-list {
  font-size: 13px;
  i {
    cursor: pointer;
    margin: 0 5px;
  }
}
</style>
