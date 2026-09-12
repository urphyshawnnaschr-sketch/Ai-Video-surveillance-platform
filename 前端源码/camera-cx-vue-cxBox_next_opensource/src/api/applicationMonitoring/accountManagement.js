/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request';

// 获取账号列表
export function getListData(data) {
  return request({
    url: '/ap/account/listPage',
    method: 'post',
    data
  });
}

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/ap/role/listData',
    method: 'post',
    params
  });
}

// 保存账号
export function saveData(data) {
  return request({
    url: '/ap/account/save',
    method: 'post',
    data
  });
}

// 修改账号
export function updateData(data) {
  return request({
    url: '/ap/account/update',
    method: 'post',
    data
  });
}

// 重置密码
export function resetPassword(params) {
  return request({
    url: '/ap/account/resetPassword',
    method: 'post',
    params
  });
}

// 删除账号
export function deleteData(params) {
  return request({
    url: '/ap/account/delete',
    method: 'post',
    params
  });
}
