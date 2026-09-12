import request from '@/utils/request';


// 获取摄像头列表
export function upload(data) {
  return request({
    url: '/map/file/image/upload',
    method: 'post',
    data
  })
}

export function uploadSvg(data) {
  return request({
    url: '/map/file/svg/upload',
    method: 'post',
    data
  })
}

export function configList(params) {
  return request({
    url: '/map/config/list',
    method: 'get',
    params
  })
}

export function configSave(data) {
  return request({
    url: '/map/config/save',
    method: 'post',
    data
  })
}

export function configDelete(params) {
  return request({
    url: '/map/config/delete',
    method: 'get',
    params
  })
}

export function configInfo(params) {
  return request({
    url: '/map/config/info',
    method: 'get',
    params
  })
}

//盒子 摄像头的创建
export function objectSave(data) {
  return request({
    url: '/map/object/save',
    method: 'post',
    data
  })
}


//盒子摄像头列表
export function objectList(data) {
  return request({
    url: '/map/object/list',
    method: 'post',
    data
  })
}

//删除盒子摄像头
export function objectDelete(params) {
  return request({
    url: '/map/object/delete',
    method: 'get',
    params
  })
}

//图标的创建
export function ruleSave(data) {
  return request({
    url: '/map/rule/save',
    method: 'post',
    data
  })
}

//图标列表
export function ruleInfo(params) {
  return request({
    url: '/map/rule/info',
    method: 'get',
    params
  })
}


//盒子列表
export function locationList(data) {
  return request({
    url: '/location/listData5',
    method: 'post',
    data
  })
}

//摄像头列表
export function cameraList(data) {
  return request({
    url: '/camera/listData5',
    method: 'post',
    data
  })
}

//统计接口
export function stat(params) {
  return request({
    url: '/map/stat',
    method: 'get',
    params
  })
}

//部门接口
export function departList(params) {
  return request({
    url: '/ap/depart/list',
    method: 'get',
    params
  })
}

// 分组层级列表
export function cameraGroup(data) {
  return request({
    url: '/map/object/camera/group',
    method: 'post',
    data
  })
}

// 未添加分组
export function unselectGroup(data) {
  return request({
    url: '/map/object/camera/group/unselect',
    method: 'post',
    data
  })
}

export function groupCameraList(data) {
    return request({
        url: '/camera/group/item/page',
        method: 'post',
        data
    })
}

 