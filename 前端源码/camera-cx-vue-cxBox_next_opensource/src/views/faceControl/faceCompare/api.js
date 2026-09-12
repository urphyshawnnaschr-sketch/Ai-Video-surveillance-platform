/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-12-25 17:31:23
 */
import request from '@/utils/request'

//列表
export function faceCompare(data) {
  return request({
    url: '/face/image/compare',
    method: 'post',
    data
  })
}

export function faceRecognize(data) {
  return request({
    url: '/face/image/recognize',
    method: 'post',
    data
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

