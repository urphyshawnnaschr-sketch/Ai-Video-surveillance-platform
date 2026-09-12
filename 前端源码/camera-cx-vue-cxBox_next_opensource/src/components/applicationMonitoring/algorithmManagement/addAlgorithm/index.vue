<template><div><el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="600px"
      @close="closed"
    ><div><el-form ref="form" :model="params" :rules="rules" label-width="80px"><el-form-item :label="$t('common.algorithmName')" prop="name"><el-input v-model="params.name"></el-input></el-form-item><el-form-item
            :label="$t('applicationMonitoring.algorithmManagement.algorithmEnglish')"
            prop="nameEn"
          ><el-input
              v-model="params.nameEn"
              @blur="blurCheck"
              :class="[isExist?'color-red':'']"
            ></el-input><div
              v-if="isExist"
              style="color: #dd383e; font-size: 12px; position: absolute; top: 30px;"
            >{{$t("addalgorithm.index.60jjn5")}}</div></el-form-item><el-form-item
            :label="$t('addalgorithm.index.2yghsk')"
            prop="platform"
          ><el-select
              v-model="params.platform"
              :placeholder="$t('addalgorithm.index.710vv4')"
              style="width: 100%"
            ><el-option
                v-for="(item, index) in platformList"
                :key="index"
                :label="item.name"
                :value="item.id"
              ></el-option></el-select></el-form-item><el-form-item :label="$t('common.alarmLevel')" prop="alarmLevelId"><el-select
              v-model="params.alarmLevelId"
              :placeholder="$t('form.tip.chooseAlarmLevel')"
              style="width: 100%"
            ><el-option
                v-for="(item, index) in alarmLevelList"
                :key="index"
                :label="item.name"
                :value="item.id"
              ></el-option></el-select></el-form-item><el-form-item
            :label="$t('applicationMonitoring.common.applicationScenarios')"
            prop="tagIds"
          ><el-select
              v-model="params.tagIds"
              :placeholder="$t('addalgorithm.index.1d4b4z')"
              style="width: 100%"
              multiple
              clearable
            ><el-option
                v-for="(item, index) in tagList"
                :key="index"
                :label="item.name"
                :value="item.id"
              ></el-option></el-select></el-form-item></el-form></div><div><!--<el-button type="primary"@click="search"> Search </el-button> <el-button type="primary":disabled="!tableData.length":loading="loading"@click="download"> Save to Local </el-button >--><el-button type="primary" @click="searchFun" :disabled="isExist">{{$t("button.searchText")}}</el-button></div><div style="padding: 10px 0">{{$t("addalgorithm.index.11v9n8")}}</div><div><el-table :data="tableData" border style="width: 100%"><el-table-column
            align="center"
            prop="name"
            :label="$t('addalgorithm.index.3l8678')"
          ></el-table-column><el-table-column
            align="center"
            prop="md5Str"
            :label="$t('addalgorithm.index.90t7k1')"
          ></el-table-column><el-table-column
            align="center"
            prop="size"
            :label="$t('addalgorithm.index.fj69u4')"
          ></el-table-column><el-table-column
            align="center"
            :label="$t('addalgorithm.index.683z41')"
          ><template slot-scope="scope"><el-progress :percentage="scope.row.process"></el-progress></template>
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.action', { text: '' })"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="downs(scope.row.name, scope.$index)"
                >{{ $t("modeltesting.modeltesting.39e71x") }}</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveData" :disabled="isExist">{{
          $t("marktool.annotate.q5fjzf")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import {
  getTagListData,
  getListDataDetail,
  search,
  saveData,
  getAlarmLevelList,
  downloadCheck,
  downloadSingleFile,
  checkNameEn,
} from "@/api/applicationMonitoring/algorithmManagement";
export default {
  props: {
    currentId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      platformList: [
        {
          name: this.$t("addalgorithm.index.868d2k"),
          id: "lynxi",
        },
      ],
      loading: false,
      title: this.currentId
        ? $t("common.edit", { text: $t("common.algorithm") })
        : $t("common.add", { text: $t("common.algorithm") }),
      dialogVisible: true,
      params: {
        id: this.currentId,
        name: "",
        nameEn: "",
        tagIds: [],
        alarmLevelId: "",
        platform: "",
      },
      rules: {
        name: [
          {
            required: true,
            message: $t("form.tip.inputAlgorithmName"),
            trigger: "blur",
          },
        ],
        nameEn: [
          {
            required: true,
            message: $t("form.tip.inputAlgorithmEnglish"),
            trigger: "blur",
          },
        ],
        platform: [
          {
            required: true,
            message: this.$t("addalgorithm.index.710vv4"),
            trigger: "change",
          },
        ],
      },
      tagList: [],
      tableData: [],
      alarmLevelList: [],
      listData: [],
      timer: null,
      isExist: false,
    };
  },
  async created() {
    await this.getTagListData();
    await this.listAlarmLevelData();
    if (this.currentId) {
      await this.getListDataDetail();
      await this.searchFun();
    }
  },
  beforeDestroy() {
    // 在组件销毁前清除定时器
    this.tableData.map((item, ind) => {
      clearInterval(item.timerObj);
      item.timerObj = "";
    });
  },
  methods: {
    // 获取行业列表
    async getTagListData() {
      const data = await getTagListData({ type: 1 });
      this.tagList = data.data;
    },
    // 获取算法详情
    async getListDataDetail() {
      const data = await getListDataDetail({ id: this.currentId });
      Object.assign(this.params, {
        name: data.data.name,
        nameEn: data.data.nameEn,
        tagIds: data.data.tagIds,
        alarmLevelId:
          data.data.alarmLevelId && data.data.alarmLevelId != "0"
            ? data.data.alarmLevelId
            : "",
        platform: data.data.platform,
      });
    },
    //
    // async search() {
    //   this.$refs.form.validate(async (valid) => {
    //     if (valid) {
    //       const params = {
    //         suanfa: this.params.nameEn,
    //         page: 1,
    //         limit: 10,
    //       };
    //       const data = await search(params);
    //       this.tableData = data.data;
    //       let newArr = this.tableData.filter((item) => {
    //         return Number(item.process)!=100
    //       });
    //       if(newArr.length==0){
    //         clearInterval(this.timer);
    //       }

    //     } else {
    //       return false;
    //     }
    //   });
    // },
    // async timerFun(ind){
    //   let that = this;
    //   this.timer = await setInterval(function () {
    //       that.search();
    //   }, 5000);
    // },
    // 保存算法
    saveData() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          await saveData(this.params);
          this.$message.success(
            this.currentId
              ? $t("common.edit", { text: $t("common.success") })
              : $t("common.add", { text: $t("common.success") })
          );
          this.dialogVisible = false;
        } else {
          return false;
        }
      });
    },
    // 下载
    // async download() {
    //   this.loading = true;
    //   let newArr = this.tableData.filter((item) => {
    //     return Number(item.process)!=100
    //   });
    //   if(newArr.length!=0){
    //     this.timerFun()
    //   }
    //   await download({
    //     suanfa: this.params.nameEn,
    //   });
    //   this.loading = false;
    //   this.$message.success("下载成功");
    // },

    // 搜索
    async searchFun(funType) {
      clearInterval(this.timer);
      this.timer = null;
      if (!this.params.nameEn) {
        this.$message.error($t("form.tip.inputAlgorithmEnglish"));
        return;
      }
      if (!this.params.platform) {
        this.$message.error(this.$t("addalgorithm.index.710vv4"));
        return;
      }
      const obj = {
        suanfa: this.params.nameEn,
        platform: this.params.platform,
      };
      const res = await search(obj);
      res.data.forEach((item, index) => {
        clearInterval(item.timerObj);
        item.timerObj = "";
        item.process = this.handleProcess(item.localLength, item.length);
        if (
          item.md5Str == this.$t("modeltesting.modeltesting.f3vkgb") &&
          funType != 1
        ) {
          let that = this;
          item.timerObj = setInterval(function () {
            that.inspectFun(item.name, index);
          }, 3000);
        }
      });
      this.tableData = res.data;
      console.log(this.tableData);
    },
    downs(name, index) {
      downloadSingleFile({
        suanfa: this.params.nameEn,
        platform: this.params.platform,
        fileName: name,
      })
        .then((res) => {
          //console.log(1111);
          if (res.code == 0) {
            this.inspectFun(name, index);
            let that = this;
            this.tableData[index].timerObj = setInterval(function () {
              that.inspectFun(name, index);
            }, 3000);
          }
        })
        .catch((res) => {})
        .finally((res) => {});
    },
    // 检查
    inspectFun(fileName, index) {
      downloadCheck({ suanfa: fileName })
        .then((res) => {
          if (res.data.msg != this.$t("modeltesting.modeltesting.f3vkgb")) {
            clearInterval(this.tableData[index].timerObj);
            this.tableData[index].timerObj = "";
            this.searchFun(1);
          } else {
            let tatal = this.tableData[index].length;
            this.tableData[index].process = this.handleProcess(
              res.data.length,
              tatal
            );
          }
        })
        .catch((res) => {
          if (
            res.msg != this.$t("modeltesting.modeltesting.f3vkgb") ||
            res != this.$t("modeltesting.modeltesting.f3vkgb")
          ) {
            clearInterval(this.tableData[index].timerObj);
            this.tableData[index].timerObj = "";
            this.searchFun(1);
          }
        });
    },
    // 关闭回调
    closed() {
      // clearInterval(this.timer);
      // this.timer=null;
      this.tableData.map((item, ind) => {
        clearInterval(item.timerObj);
        item.timerObj = "";
      });
      console.log(this.tableData);

      this.$emit("close");
    },
    // 处理进度条
    handleProcess(local, total) {
      if (local && total) {
        return Number(((Number(local) / Number(total)) * 100).toFixed(2));
      } else {
        return 0;
      }
    },
    //
    async listAlarmLevelData() {
      const data = await getAlarmLevelList();
      this.alarmLevelList = data.data;
    },
    // 算法英文名失去焦点时，判断此算法是否存在
    blurCheck() {
      if (!this.params.nameEn) {
        this.isExist = false;
        return;
      }
      let obj = {
        id: this.currentId,
        nameEn: this.params.nameEn,
      };
      checkNameEn(obj).then((res) => {
        this.isExist = res.data;
      });
    },
  },
};
</script>
<style lang="scss">
.color-red {
  .el-input__inner {
    border-color: #dd383e;
  }
}
</style>
