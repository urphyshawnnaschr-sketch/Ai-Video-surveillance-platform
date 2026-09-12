<template>
  <div class="sys-cont">
    <el-form ref="form" :model="form" label-width="100px" size="mini">
      <el-row>
        <el-col :span="6">
          <el-form-item :label="$t('outSystem.systemManagent.fontConfig')">
            <el-input
              v-model="form.typeface"
              @input="typefaceFun"
              style="width: 100px"
              onkeyup="value=value.replace(/[^\d]/g,'')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('outSystem.systemManagent.baseMap')">
            <el-color-picker
              v-model="form.underlay"
              @change="underlayFun"
            ></el-color-picker>
          </el-form-item>
        </el-col>
      </el-row>
      <div class="ai_table">
        <el-table
          :data="listData"
          :span-method="objectSpanMethod"
          border
          style="width: 100%"
        >
          <el-table-column
            prop="name"
            :label="$t('outSystem.systemManagent.moduleName')"
            width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="itemName"
            :label="$t('outSystem.systemManagent.bottomModule')"
            width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="path"
            :label="$t('outSystem.systemManagent.router')"
          ></el-table-column>
        </el-table>
      </div>
    </el-form>
  </div>
</template>
<script>
import Cookies from "js-cookie";
export default {
  data() {
    return {
      form: {
        typeface: JSON.parse(localStorage.getItem("publicStyle")).font
          ? JSON.parse(localStorage.getItem("publicStyle")).font.fontSize.slice(
              0,
              -2
            )
          : "",
        underlay: JSON.parse(localStorage.getItem("publicStyle")).background
          ? JSON.parse(localStorage.getItem("publicStyle")).background
              .backgroundColor
          : "",
      },
      listData: [],
      token: Cookies.get("X-Token"),
      mergingPos: null,
      mergingRows: [],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取数据
    getList() {
      let arr = this.$router.options.routes;
      if (arr.length > 0) {
        let newArr = arr.filter((item) => {
          return (
            item.name === this.$t('outSystem.systemManagent.uyk0vc') ||
            item.name === this.$t('outSystem.systemManagent.mpzbj9') ||
            item.name === this.$t('outSystem.systemManagent.pf866s')
          );
        });
        newArr.map((item, i) => {
          item.children.map((items, ind) => {
            let obj = {
              name: item.name,
              itemName: items.name,
              path: items.path
                ? this.getIP() + items.path + "?token=" + this.token
                : "",
            };
            this.listData.push(obj);
          });
        });
        this.dataPretreatment(this.listData);
      }
    },
    underlayFun() {
      let obj = JSON.parse(localStorage.getItem("publicStyle"));
      localStorage.setItem(
        "publicStyle",
        JSON.stringify(
          Object.assign(obj, {
            background: { backgroundColor: this.form.underlay },
          })
        )
      );
    },
    typefaceFun() {
      let obj = JSON.parse(localStorage.getItem("publicStyle"));
      localStorage.setItem(
        "publicStyle",
        JSON.stringify(
          Object.assign(obj, { font: { fontSize: this.form.typeface + "px" } })
        )
      );
    },
    getIP() {
      let url = window.location.href;
      let index = url.indexOf("#");
      let result = url.substring(0, index + 1);
      return result;
    },
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        //第一列
        const _row = this.mergingRows[rowIndex];
        const _col = _row > 0 ? 1 : 0;
        return {
          rowspan: _row,
          colspan: _col,
        };
      }
    },
    dataPretreatment(list) {
      //表格数据列合并预处理,生成一个与行数相同的数组记录每一行设置的合并数
      // 如果是第一条记录（索引为0），向数组中加入1，并设置索引位置；
      // 如果不是第一条记录，则判断它与前一条记录是否相等，如果相等，
      // 则向中添入元素0，并将前一位元素+1，表示合并行数+1，
      // 以此往复，得到所有行的合并数，0即表示该行不显示。
      if (list.length == 0) {
        return false;
      }
      for (let i = 0; i < list.length; i++) {
        // tabledata 表格数据源
        if (i === 0) {
          this.mergingRows.push(1);
          this.mergingPos = 0;
        } else {
          if (list[i].name === list[i - 1].name) {
            //哪些数据是要合并的 合并的条件是什么
            this.mergingRows[this.mergingPos] += 1;
            this.mergingRows.push(0);
          } else {
            this.mergingRows.push(1);
            this.mergingPos = i;
          }
        }
      }
    },
  },
};
</script>
<style scoped lang="scss">
.sys-cont {
  width: 100%;
  padding: 20px 10px;
  background: #fff;
  .ml-20 {
    margin-left: 20px;
  }
}
::v-deep .el-color-picker--mini .el-color-picker__trigger {
  width: 100px;
}
</style>
