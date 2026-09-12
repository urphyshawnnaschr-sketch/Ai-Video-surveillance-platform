/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-10-31 13:13:02
 */
import request from '@/utils/request'

//人员列表
export function listPage(params) {
  return request({
    url: '/face/user/listPage',
    method: 'post',
    params
  })
}
//删除人员
export function delect(params) {
  return request({
    url: '/face/user/delete',
    method: 'post',
    params
  })
}

//新增保存人员
export function save(data) {
  return request({
    url: '/face/user/save',
    method: 'post',
    data
  })
}
//人员详情
export function detail(params) {
  return request({
    url: '/face/user/detail',
    method: 'get',
    params
  })
}

//新增保存分组
export function saveGroup(data) {
  return request({
    url: '/face/group/save',
    method: 'post',
    data
  })
}

//分组列表
export function listPageDGroup(params) {
  return request({
    url: '/face/group/listData',
    method: 'post',
    params
  })
}

//删除分组
export function delectGroup(params) {
  return request({
    url: '/face/group/delete',
    method: 'post',
    params
  })
}

/****************************人脸地图************************/

// 查看地图配置列表
export function configList(data) {
  return request({
    url: '/face/track/config/list',
    method: 'post',
    data
  })
}

// 新增或编辑地图
export function configSave(data) {
  return request({
    url: '/face/track/config/save',
    method: 'post',
    data
  })
}

// 删除地图
export function configDelete(data) {
  return request({
    url: '/face/track/config/delete',
    method: 'post',
    data
  })
}

// 上传地图
export function upload(data) {
  return request({
    url: '/face/track/config/upload',
    method: 'post',
    data
  })
}

// 查看地图详情
export function configInfo(data) {
  return request({
    url: '/face/track/config/info',
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

// 摄像头标记
export function cameraSave(data) {
  return request({
    url: '/face/track/camera/save',
    method: 'post',
    data
  })
}

// 删除摄像头标记
export function cameraDelete(data) {
  return request({
    url: '/face/track/camera/delete',
    method: 'post',
    data
  })
}

// 摄像头列表
export function cameraList(data) {
  return request({
    url: '/face/track/camera/list',
    method: 'post',
    data
  })
}
