/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request'


// 获取电话号码列表
export function getListData(params) {
  return request({
    url: '/smsphone/listData',
    method: 'post',
    params
  })
}

// 新增电话号码列表
export function saveData(params) {
  return request({
    url: '/smsphone/save',
    method: 'post',
    params
  })
}

// 删除电话号码
export function deleteData(params) {
  return request({
    url: '/smsphone/delete',
    method: 'post',
    params
  })
}

