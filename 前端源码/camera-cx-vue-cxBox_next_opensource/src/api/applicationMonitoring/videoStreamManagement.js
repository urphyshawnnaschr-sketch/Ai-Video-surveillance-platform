/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-12-28 14:33:22
 */
import request from '@/utils/request'

// 获取配置列表
export function getAlgorithms(params) {
  return request({
    url: '/stream/statics/algorithms',
    method: 'post',
    params
  })
}

// 获取开启的摄像头列表
export function getListPageActives(params) {
  return request({
    url: '/camera/listPageActives',
    method: 'post',
    params
  })
}

// 获取摄像头视频流地址
export function getPlayUrl(params) {
  return request({
    url: '/stream/getPlayUrl',
    method: 'post',
    params
  })
}

// 获取摄像头视频流地址
export function getBoxPlayUrl(params) {
  return request({
    url: '/stream/getBoxPlayUrl',
    method: 'post',
    params
  })
}

export function setPlayType(data) {
  return request({
    url: '/stream/setPlayType',
    method: 'post',
    data
  })
}

// 获取实时告警数据
export function getAlarms(params) {
  return request({
    url: '/stream/report/alarms',
    method: 'post',
    params
  })
}

// 获取弹窗算法列表
export function getAllAlgorithms(params) {
  return request({
    url: '/stream/form/algorithms',
    method: 'post',
    params
  })
}

// 保存弹窗选中算法
export function saveAlgorithms(data) {
  return request({
    url: '/stream/config/save',
    method: 'post',
    data
  })
}

// 获取告警数量
export function getCounter(params) {
  return request({
    url: '/stream/report/counter',
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

// 提交正在播放的摄像头
export function playings(data) {
  return request({
    url: '/stream/playings',
    method: 'post',
    data
  })
}

// 获取边缘盒子播放地址
export function getBoxPlayUrls(data) {
  return request({
    url: '/stream/getBoxPlayUrls',
    method: 'post',
    data
  })
}

export function playCx(data) {
  return request({
    url: '/stream/playCx',
    method: 'post',
    data
  })
}

// 获取组织
export function departTree(data) {
  return request({
    url: '/ap/depart/listTree',
    method: 'post',
    data
  })
}

// 根据所属组织和所属分组查询摄像头列表
export function listData7(data) {
  return request({
    url: '/camera/listData7',
    method: 'post',
    data
  })
}
// 点击查询按钮===根据所属组织和所属分组查询摄像头列表
export function listData8(data) {
  return request({
    url: '/camera/listData8',
    method: 'post',
    data
  })
}

// 点击查询按钮===根据所属组织、所属分组、勾选摄像头查询摄像头分页列表
export function listPage9(data) {
  return request({
    url: '/camera/listPage9',
    method: 'post',
    data
  })
}



// 获取分组摄像机
export function groupTree(data) {
  return request({
    url: '/camera/group/tree',
    method: 'post',
    data
  })
}