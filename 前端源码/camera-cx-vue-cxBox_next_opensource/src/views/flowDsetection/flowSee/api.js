/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-10-31 11:02:05
 */
import request from '@/utils/request'

//算法列表
export function listPage(params) {
  return request({
    url: '/tracker/report/listPage',
    method: 'post',
    params
  })
}
//删除算法
export function delect(params) {
  return request({
    url: '/algorithm/delete',
    method: 'post',
    params
  })
}

//新增保存算法
export function save(data) {
  return request({
    url: '/algorithm/save',
    method: 'post',
    data
  })
}
//算法详情
export function detail(params) {
  return request({
    url: '/algorithm/detail',
    method: 'post',
    params
  })
}

//编辑算法
export function saveOrUpdate(params) {
  return request({
    url: '/camera/saveOrUpdate',
    method: 'post',
    params
  })
}

//摄像头列表
export function cameraListData(params) {
  return request({
    url: '/camera/listData',
    method: 'post',
    params
  })
}

//算法列表
export function reportImage(params) {
  return request({
    url: '/tracker/report/image',
    method: 'get',
    params
  })
}



