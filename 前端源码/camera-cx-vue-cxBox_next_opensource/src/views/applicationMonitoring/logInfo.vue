<template>
  <div class="log-info">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane
        :label="$t('applicationMonitoring.logInfo.javaLog')"
        name="first"
      >
        <div v-for="(item, index) in logList" :key="index">
          {{ item }}
        </div>
      </el-tab-pane>
      <el-tab-pane
        :label="$t('applicationMonitoring.logInfo.boxLog')"
        name="second"
      >
        <div v-for="(item, index) in logList" :key="index">
          {{ item }}
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import { javaLog, aiboxLog } from "@/api/common";
export default {
  data() {
    return {
      logList: [],
      activeName: "first",
    };
  },
  created() {
    this.getLog();
    let that = this;
    this.timer = setInterval(function () {
      that.getLog();
    }, 10000);
  },
  destroyed() {
    clearInterval(this.timer);
    this.timer = "";
  },
  methods: {
    async getLog() {
      const res = await javaLog();
      this.logList = res.data;
    },
    async getBoxLog() {
      const res = await aiboxLog();
      this.logList = res.data;
    },
    handleClick(tab) {
      clearInterval(this.timer);
      this.timer = "";
      this.logList = [];
      if (tab.name == "first") {
        this.getLog();
        let that = this;
        this.timer = setInterval(function () {
          that.getLog();
        }, 10000);
      } else {
        this.getBoxLog();
        let that = this;
        this.timer = setInterval(function () {
          that.getBoxLog();
        }, 10000);
      }
    },
  },
};
</script>
<style scoped lang="scss">
.log-info {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  font-size: 13px;
  div {
    line-height: 20px;
    word-break: break-all;
    white-space: pre-wrap;
  }
}
</style>
