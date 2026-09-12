/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request'

// 获取算法列表
export function getListData(params) {
  return request({
    url: '/algorithm/listData',
    method: 'post',
    params
  })
}
// 获取行业列表
export function getTagListData(params) {
  return request({
    url: '/tag/listData',
    method: 'post',
    params
  })
}

// 算法测试
export function predict(data) {
  return request({
    url: '/model/test/detect',
    method: 'post',
    data
  })
}

// 视频算法测试
export function detectMp4(data) {
  return request({
    url: '/model/test/detectMp4',
    method: 'post',
    data
  })
}

// 获取第一帧图片
export function firstPicFromMp4(data) {
  return request({
    url: '/model/test/firstPicFromMp4',
    method: 'post',
    data
  })
}

// zip测试
export function detectZip(data) {
  return request({
    url: '/model/test/detectZip',
    method: 'post',
    data
  })
}

// 获取测试视频
export function getTestVideo(params) {
  return request({
    url: '/model/test/getTestMp4',
    method: 'get',
    params
  })
}

// 获取测试图片
export function getTestImg(params) {
  return request({
    url: '/model/test/getTestImage',
    method: 'get',
    params
  })
}

// 获取算法详情
export function detail(params) {
  return request({
    url: '/algorithm/detail',
    method: 'post',
    params
  })
}

// 获取算法封面图片
export function picStream(params) {
  return request({
    url: '/algorithm/picStream',
    method: 'get',
    params
  })
}

// 获取默认的图片和视频
export function getTestAll(params) {
  return request({
    url: '/model/test/getTestAll',
    method: 'get',
    params
  })
}

// 删除
export function deleteFile(params) {
  return request({
    url: '/algorithm/deleteFile',
    method: 'post',
    params
  })
}
// 检查算法是否有更新
export function checkAlgorithmVersion(params) {
  return request({
    url: '/algorithm/checkAlgorithmVersion',
    method: 'post',
    params
  })
}
// 上传算法卡片信息zip
export function uploadZipSave(data) {
  return request({
    url: '/algorithm/uploadZipSave',
    method: 'post',
    data
  })
}
// 上传算法文件
export function uploadAlgorithmFile(data) {
  return request({
    url: '/algorithm/uploadAlgorithmFile',
    method: 'post',
    data
  })
}

// 上传语音文件
export function uploadSoundFile(data) {
  return request({
    url: '/algorithm/uploadSoundFile',
    method: 'post',
    data
  })
}

// 删除语音文件
export function deleteSoundFile(params) {
  return request({
    url: '/algorithm/deleteSoundFile',
    method: 'post',
    params
  })
}

// 新增算法卡片信息
export function saveOrUpdate(data) {
  return request({
    url: '/algorithm/saveOrUpdate',
    method: 'post',
    data
  })
}

// 
export function summary(params) {
  return request({
    url: '/algorithm/summary',
    method: 'get',
    params
  })
}