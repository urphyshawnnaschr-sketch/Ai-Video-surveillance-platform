/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request';

// 获取数据列表
export function getListData(params) {
  return request({
    url: '/ap/training/list',
    method: 'get',
    params
  });
}

// 获取摄像头列表
export function getCameraListData(params) {
  return request({
    url: '/camera/listData',
    method: 'post',
    params
  })
}

// 获取算法列表
export function getAlgorithmListData(params) {
  return request({
    url: '/algorithm/listData',
    method: 'post',
    params
  })
}

// 预览图片
export function getPreview(data) {
  return request({
    url: '/ap/training/preview',
    method: 'post',
    data
  })
}

// 创建项目
export function saveProject(data) {
  return request({
    url: '/ap/training/create',
    method: 'post',
    data
  })
}

// 清除数据
export function deleteImg(data) {
  return request({
    url: '/ap/training/delete',
    method: 'post',
    data
  })
}