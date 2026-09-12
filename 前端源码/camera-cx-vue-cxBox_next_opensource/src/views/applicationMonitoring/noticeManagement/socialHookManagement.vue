<template><el-tabs type="border-card" v-model="activeName" @tab-click="handleClick"><el-tab-pane :label="$t('noticemanagement.socialhookmanagement.4ya2q6')" name="feishu"><feishu v-if="activeName =='feishu'"></feishu></el-tab-pane><!--<el-tab-pane label="WeWork Group Push"name="wechat"> <wechat v-if="activeName =='wechat'"></wechat> </el-tab-pane> <el-tab-pane label="DingTalk Group Push"name="dingding"> <dingding v-if="activeName =='dingding'"></dingding> </el-tab-pane>--></el-tabs></template>
<script>
import Feishu from "./components/feishu/index.vue";
import Wechat from "./components/wechat/index.vue";
import Dingding from "./components/dingding/index.vue";
export default {
  components: {
    Feishu,
    Wechat,
    Dingding,
  },
  data() {
    return {
      activeName: "feishu",
    };
  },
  methods: {
    handleClick(tab, event) {
      this.activeName = tab.name;
    },
  },
};
</script>