/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-11-07 16:15:16
 */
import request from '@/utils/request'

// 登录
export function login(params) {
  return request({
    url: '/login',
    method: 'post',
    params
  })
}

// 上传
export function upload(data) {
  return request({
    url: '/model/test/upload',
    method: 'post',
    data
  })
}

// 通过获取图片
export function getStream(params) {
  return request({
    url: '/report/stream',
    method: 'get',
    params,
    responseType: 'blob',
  })
}

// 获取信息
export function getAppInfo(data) {
  return request({
    url: '/config/appInfo',
    method: 'post',
    data
  })
}

// 获取验证码
export function getRegisterSms(params) {
  return request({
    url: '/getRegisterSms',
    method: 'post',
    params
  })
}
// 重置密码--获取验证码
export function getResetPswSms(params) {
  return request({
    url: '/getResetPswSms',
    method: 'post',
    params
  })
}

// 注册信息
export function register(params) {
  return request({
    url: '/register',
    method: 'post',
    params
  })
}

// 重置密码
export function resetPsw(params) {
  return request({
    url: '/resetPsw',
    method: 'post',
    params
  })
}

// 获取日志
export function javaLog(params) {
  return request({
    url: '/logger/reader/java?lines=300',
    method: 'get',
    params
  })
}
// 获取盒子日志
export function aiboxLog(params) {
  return request({
    url: '/logger/reader/aibox?limit=300',
    method: 'post',
    params
  })
}
//获取最新版本更新
export function getLastFileOrigin(params){
  return request({
    url:'/releaseNotes/getLastFileOrigin',
    method:'get',
    params
  })
}
//获取历史版本记录
export function getFileOrigin(params){
  return request({
    url:'/releaseNotes/getFileOrigin',
    method:'get',
    params
  })
}

// 获取菜单
export function userTree(data) {
  return request({
    url: '/ap/menus/user/tree',
    method: 'post',
    data
  })
}
