/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2021-06-08 13:19:25
 * @LastEditors: 张健，dhq
 * @LastEditTime: 2022-09-19 18:01:18
 */
import router from '@/router';
import axios from 'axios';
import Cookies from "js-cookie";
import { Message, MessageBox } from 'element-ui';

import store from "@/store";

const getLanguage = ()=>{
  const langMap = {
    'zh-CN': 'zh-CN',
    'en': 'en-US'
  }
  return langMap[store.state.locale] || 'zh-CN'
}
const service = axios.create({
  // baseURL: process.env.VUE_APP_BASE_PUBLICPATH,
  baseURL: VUE_APP_API_BASE_URL,
  timeout: 3000000
});
service.interceptors.request.use(
  config => {
    if (Cookies.get('X-Token') || router.history.current.query.token) {
      config.headers['X-Token'] = Cookies.get('X-Token') || router.history.current.query.token;
    }
    config.headers['lang'] = getLanguage();
    return config;
  },
  error => {
    Promise.reject(error);
  }
);

service.interceptors.response.use(
  res => {
    if (res.config.responseType === 'blob') {
      return res;
    }
    if (res.data.code == 0 || res.data.code == undefined) {
      return res.data;
    } else if (res.data.code == 403) {
      Cookies.remove('X-Token')
      router.push('/login')
      Message.error(res.data.msg);
      return Promise.reject(res.data.msg);
    } else {
      Message.error(res.data.msg);
      return Promise.reject(res.data.msg);
    }
  },
  error => {
    return Promise.reject(error);
  }
);

export default service;
