/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-12-31 23:11:32
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

// 获取算法详情
export function getListDataDetail(params) {
  return request({
    url: '/algorithm/detail',
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

// 删除算法
export function deleteAlgorithm(params) {
  return request({
    url: '/algorithm/delete',
    method: 'post',
    params
  })
}

// 新增编辑算法
export function saveData(data) {
  return request({
    url: '/algorithm/save',
    method: 'post',
    data
  })
}

// 获取算法远程文件列表
export function search(params) {
  return request({
    url: '/test/gitlabDownload/getFileOrigin',
    method: 'get',
    params
  })
}

// 下载
export function download(params) {
  return request({
    url: '/algorithm/download',
    method: 'get',
    params
  })
}

// 获取视频
export function getDemoStream(params) {
  return request({
    url: '/algorithm/demo/stream',
    method: 'get',
    params,
    responseType: 'blob',
  })
}

// 获取告警等级列表数据
export function getAlarmLevelList(params) {
  return request({
    url: '/alarmLevel/listData',
    method: 'post',
    params
  })
}

// 新增/修改告警等级数据
export function saveAlarmLevel(data) {
  return request({
    url: '/alarmLevel/save',
    method: 'post',
    data
  })
}

// 删除告警等级数据
export function deleteAlarmLevel(params) {
  return request({
    url: '/alarmLevel/delete',
    method: 'post',
    params
  })
}

// 查询告警等级详情数据
export function getAlarmLevel(params) {
  return request({
    url: '/alarmLevel/detail',
    method: 'post',
    params
  })
}

// 查询告警展示列表数据
export function getAlarmShowTypeList(params) {
  return request({
    url: '/alarmLevel/listAlarmShowTypes',
    method: 'post',
    params
  })
}

// 输入算法英文名的时候判断算法是否存在
export function checkNameEn(params) {
  return request({
    url: '/algorithm/checkNameEn',
    method: 'post',
    params
  })
}

/*************************************** */
// git
//获取远程仓库文件 
export function getFileOrigin(params) {
  return request({
    url: '/oss/getFileOrigin',
    method: 'get',
    params
  })
}
// //下载 
// export function gitDownload(params) {
//   return request({
//     url: '/test/gitlabDownload/download',
//     method: 'get',
//     params
//   })
// }
// 下载后检查
export function downloadCheck(params) {
  return request({
    url: '/oss/downloadCheck',
    method: 'get',
    params
  })
}
// // 获取文件列表
// export function getFileListBySuanfa(params) {
//   return request({
//     url: '/test/gitlabDownload/getFileListBySuanfa',
//     method: 'get',
//     params
//   })
// }

// export function getFileList(params) {
//   return request({
//     url: '/test/gitlabDownload/getFileList',
//     method: 'get',
//     params
//   })
// }

//算法更新获取盒子列表及算法历史版本
export function getBoxAndHistoryVersion(params) {
  return request({
    url: '/algorithm/getBoxAndHistoryVersion',
    method: 'get',
    params
  })
}

//盒子切换算法版本任务开始
export function changeBoxAlgorithmVersion(params) {
  return request({
    url: '/algorithm/changeBoxAlgorithmVersion',
    method: 'post',
    params
  })
}


export function downloadSingleFile(params) {
  return request({
    url: '/oss/downloadSingleFile',
    method: 'get',
    params
  })
}

// 重新下载
export function reDownloadSingleFile(params) {
  return request({
    url: '/oss/reDownloadSingleFile',
    method: 'get',
    params
  })
}

// 获取算法文件列表
export function getFiles(params) {
  return request({
    url: '/algorithm/getFiles',
    method: 'post',
    params
  })
}

// 获取算法本地文件列表
export function getLocalFiles(params) {
  return request({
    url: '/algorithm/getLocalFiles',
    method: 'post',
    params
  })
}

// 获取配置的平台列表
export function getPlatform(params) {
  return request({
    url: '/algorithm/getPlatform',
    method: 'post',
    params
  })
}

// 盒子设备切换模型 V2
export function switchModel(params) {
  return request({
    url: '/algorithm/switchModel',
    method: 'post',
    params
  })
}