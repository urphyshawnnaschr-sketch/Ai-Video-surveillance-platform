/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request'

// 获取统计数据
export function getNewData(params) {
  return request({
    url: '/peopleFace/newdata',
    method: 'post',
    params
  })
}

// 获取陌生人告警记录
export function getNewLy(params) {
  return request({
    url: '/peopleFace/newly',
    method: 'post',
    params
  })
}

// 获取视频流列表
export function getActives(params) {
  return request({
    url: '/camera/shuffle_actives',
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

// 获取人脸管理列表
export function getFaceList(params) {
  return request({
    url: '/peopleFace/listPage',
    method: 'post',
    params
  })
}

// 获取人脸详情
export function getFaceDetail(params) {
  return request({
    url: '/peopleFace/detail',
    method: 'post',
    params
  })
}

// 删除人脸数据
export function deleteFace(params) {
  return request({
    url: '/peopleFace/delete',
    method: 'post',
    params
  })
}

// 保存人脸数据
export function saveFace(params) {
  return request({
    url: '/peopleFace/save',
    method: 'post',
    params
  })
}

export function checkFile(params) {
  return request({
    url: '/peopleFace/checkFile',
    method: 'post',
    params
  })
}
export function checkChunk(params) {
  return request({
    url: '/peopleFace/checkChunk',
    method: 'post',
    params
  })
}
export function chunkUpload(data) {
  return request({
    url: '/peopleFace/chunkUpload',
    method: 'post',
    data
  })
}
export function merge(params) {
  return request({
    url: '/peopleFace/merge',
    method: 'post',
    params
  })
}

// 陌生人列表
export function getCardList(params) {
  return request({
    url: '/peopleFace/list_card_data',
    method: 'get',
    params
  })
}

// 人员已处理
export function processed(params) {
  return request({
    url: '/peopleFace/processed',
    method: 'post',
    params
  })
}

// 人员误报
export function misreport(params) {
  return request({
    url: '/peopleFace/misreport',
    method: 'post',
    params
  })
}