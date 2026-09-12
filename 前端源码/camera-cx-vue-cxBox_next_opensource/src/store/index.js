/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-09-11 12:58:51
 * @LastEditors: 张健，dhq
 * @LastEditTime: 2022-09-21 17:36:05
 */
import Vue from 'vue'
import Vuex from 'vuex'
import Cookies from "js-cookie";

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null,
    cookies: Cookies.get('X-Token') || '',
    menu: null,
    appInfo: {
      appName: '',
      logoUrl: ''
    },
    afterSalesUrl: '',
    algorithmDownload:{},
    tagInfo:'',
    // VocieSwitch:false,
    alarmSwitch:true,
    routerNav:[],
    locale: localStorage.getItem('locale') || 'en'
  },
  getters: {
  },
  mutations: {
    setAfterSalesUrl(state, newUrl) {
      state.afterSalesUrl = newUrl
    },
    setAlgorithmDownload(state,newUrl){
      state.algorithmDownload = newUrl
    },
    setTagInfo(state,newInfo){
      state.tagInfo = newInfo
    },
    // setVocieSwitch(state,newInfo){
    //   state.VocieSwitch = newInfo
    // },
    setAlarmSwitch(state,newInfo){
      state.alarmSwitch = newInfo
    },
    addRouterData(state, routes) {
      const menu = JSON.parse(sessionStorage.getItem('menu'))
      state.routerNav = menu;
    },
    setLocale(state,newLocale){
      state.locale = newLocale;
      localStorage.setItem('locale',newLocale); 
    }
  },
  actions: {
  },
  modules: {
  }
})
