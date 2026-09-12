/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-11-06 22:38:36
 */
import request from '@/utils/request'
//第三方
export function saveAlarmPushConfig(params) {
  return request({
    url: '/config/saveAlarmPushConfig',
    method: 'post',
    params
  })
}
//短信
export function saveAlarmSmsConfig(params) {
  return request({
    url: '/config/saveAlarmSmsConfig',
    method: 'post',
    params
  })
}
//微信

export function saveAlarmWeworkConfig(params) {
  return request({
    url: '/config/saveAlarmWeworkConfig',
    method: 'post',
    params
  })
}

// IP音柱
export function saveSoundColumnConfig(params) {
  return request({
    url: '/config/saveSoundColumnConfig',
    method: 'post',
    params
  })
}


//
export function socialHookList(params) {
  return request({
    url: '/social/hook/list',
    method: 'get',
    params
  })
}

//
export function socialHookSave(data) {
  return request({
    url: '/social/hook/save',
    method: 'post',
    data
  })
}


//
export function socialHookSwitch(params) {
  return request({
    url: '/social/hook/switch',
    method: 'post',
    params
  })
}


//
export function socialHookInfo(params) {
  return request({
    url: '/social/hook/info',
    method: 'get',
    params
  })
}

//
export function socialHookDel(data) {
  return request({
    url: '/social/hook/delete',
    method: 'post',
    data
  })
}

//
export function esbInfo() {
  return request({
    url: '/social/hook/esb/config',
    method: 'get',
  })
}

//
export function esbSave(data) {
  return request({
    url: '/social/hook/esb/config',
    method: 'post',
    data
  })
}
