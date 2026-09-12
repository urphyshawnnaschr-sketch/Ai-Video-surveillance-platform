/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request'

// 获取摄像头列表
export function getListData(params) {
  return request({
    url: '/camera/listPage',
    method: 'post',
    params
  })
}

// 获取数据
export function getChart1Data(params) {
  return request({
    url: '/statistic/algorithm/ratio',
    method: 'post',
    params
  })
}

// 获取数据
export function getChart2Data(params) {
  return request({
    url: '/statistic/camera',
    method: 'post',
    params
  })
}

// 获取数据
export function getChart3Data(params) {
  return request({
    url: '/statistic/camera2algorithm',
    method: 'post',
    params
  })
}