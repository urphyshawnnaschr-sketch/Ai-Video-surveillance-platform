/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-12-31 16:25:54
 */
import request from '@/utils/request'

//盒子列表
export function getBoxAndVersion(params) {
  return request({
    url: '/location/getBoxAndVersion',
    method: 'get',
    params
  })
}



//盒子列表
export function changeBoxVersion(params) {
  return request({
    url: '/location/changeBoxVersion',
    method: 'post',
    params
  })
}

export function getBoxAndHistoryVersion(params) {
  return request({
    url: '/algorithm/getBoxAndHistoryVersion',
    method: 'post',
    params
  })
}

export function boxFileUpload(data) {
  return request({
    url: '/location/boxFileUpload',
    method: 'post',
    data
  })
}






