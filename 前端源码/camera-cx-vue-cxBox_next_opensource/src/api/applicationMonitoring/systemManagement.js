/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request'

// 获取配置列表
export function getListData(params) {
  return request({
    url: '/config/listData',
    method: 'post',
    params
  })
}

// 获取配置详情
export function getListDataDetail(params) {
  return request({
    url: '/config/detail',
    method: 'post',
    params
  })
}

// 删除配置
export function deleteData(params) {
  return request({
    url: '/config/delete',
    method: 'post',
    params
  })
}

// 新增编辑配置
export function saveData(params) {
  return request({
    url: '/config/save',
    method: 'post',
    params
  })
}

// 获取配置列表
export function getInfo(params) {
  return request({
    url: '/config/info',
    method: 'post',
    params
  })
}

// 保存基本配置
export function saveBasicConfig(params) {
  return request({
    url: '/config/saveBasicConfig',
    method: 'post',
    params
  })
}

// 保存告警配置
export function saveAlarmConfig(params) {
  return request({
    url: '/config/saveAlarmConfig',
    method: 'post',
    params
  })
}

// 保存应用配置
export function saveAppConfig(params) {
  return request({
    url: '/config/saveAppConfig',
    method: 'post',
    params
  })
}

// 上传
export function upload(data) {
  return request({
    url: '/config/upload',
    method: 'post',
    data
  })
}

// 保存售后二维码
export function saveAfterSalesConfig(params) {
  return request({
    url: '/config/saveAfterSalesConfig',
    method: 'post',
    params
  })
}
// 获取售后二维码
export function getAfterSales(params) {
  return request({
    url: '/cache/get',
    method: 'get',
    params
  })
}

// 保存人脸及抽帧应用配置
export function saveFaceFrameConfig(params) {
  return request({
    url: '/config//saveFaceFrameConfig',
    method: 'post',
    params
  })
}
