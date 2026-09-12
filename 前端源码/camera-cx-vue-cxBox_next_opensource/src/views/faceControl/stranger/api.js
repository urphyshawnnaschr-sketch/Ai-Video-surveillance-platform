import request from '@/utils/request'

//上传图片
export function upload(data) {
  return request({
    url: '/face/track/search/upload',
    method: 'post',
    data
  })
}

//保存
export function searchSave(data) {
  return request({
    url: '/face/track/search/save',
    method: 'post',
    data
  })
}

// 删除检索
export function searchDelete(data) {
  return request({
    url: '/face/track/search/delete',
    method: 'post',
    data
  })
}

// 检索结果-列表
export function searchList(data) {
  return request({
    url: '/face/track/search/flow/page',
    method: 'post',
    data
  })
}

// 检索结果-路径
export function flowPath(data) {
  return request({
    url: '/face/track/search/flow/path',
    method: 'post',
    data
  })
}

// 检索结果
export function searchInfo(data) {
  return request({
    url: '/face/track/search/info',
    method: 'post',
    data
  })
}

// 检索历史
export function searchPage(data) {
  return request({
    url: '/face/track/search/page',
    method: 'post',
    data
  })
}

// 分组摄像头
export function groupTree(data) {
  return request({
    url: '/camera/group/tree',
    method: 'post',
    data
  })
}

// 获取摄像头
export function flowCameras(data) {
  return request({
    url: '/face/track/search/flow/cameras',
    method: 'post',
    data
  })
}

// 主地图
export function configPrimary(data) {
  return request({
    url: '/face/track/config/primary',
    method: 'post',
    data
  })
}

// 陌生人检索获取上一条下一条报警
export function searchNearly(data) {
  return request({
    url: '/face/track/search/nearly',
    method: 'post',
    data,
  });
}