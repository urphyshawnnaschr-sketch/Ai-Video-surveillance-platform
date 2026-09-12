/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request'


// 获取增量列表
export function getListData(params) {
  return request({
    url: '/report/auditListPage',
    method: 'post',
    params
  })
}

// 获取告警详情
export function getListDataDetail(params) {
  return request({
    url: '/report/detail',
    method: 'post',
    params
  })
}

// 获取摄像头列表
export function getCameraListData(params) {
  return request({
    url: '/camera/listData',
    method: 'post',
    params
  })
}

// 获取算法列表
export function getAlgorithmListData(params) {
  return request({
    url: '/algorithm/listData',
    method: 'post',
    params
  })
}

// 获取告警类型列表
export function getTypeListData(params) {
  return request({
    url: '/report/reportTypes',
    method: 'post',
    params
  })
}

// 审核告警
export function auditData(params) {
  return request({
    url: '/report/audit',
    method: 'post',
    params
  })
}

// 导出数据
export function exportData(params) {
  return request({
    url: '/report/audit/export',
    method: 'post',
    params
  })
}