/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-07-09 10:33:16
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2024-05-06 22:26:50
 */
import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import routers from "./router/permission";
import store from "./store";
import * as echarts from "echarts";
import * as common from "@/utils/common";

import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import moment from "moment/moment";

// import './assets/styles/index.scss';
import "./theme/index.css";
// 暖色系品牌主题覆盖（必须在 index.css 之后）
import "./theme/warm.css";

import "./utils/rem.js";
import "./assets/styles/font.css";
import "./assets/styles/icomoon.css";

import i18n from './i18n.js';

Vue.prototype.$echarts = echarts;
Vue.prototype.$baseUrl = process.env.VUE_APP_IMGURL;
Vue.prototype.$common = common;
Vue.prototype.$moment = moment;

Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value)
});
Vue.prototype.$ELEMENT = { size: "small" };
window.$t = (key, params) => i18n.t(key, params)

new Vue({
  router,
  routers,
  store,
  i18n,
  render: (h) => h(App),
}).$mount("#app");
