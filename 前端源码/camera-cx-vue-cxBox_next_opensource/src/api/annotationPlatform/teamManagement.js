/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request'

// 获取团队列表
export function getListData(data) {
  return request({
    url: '/ap/userTeam/listPage',
    method: 'post',
    data
  })
}

// 删除团队
export function deleteData(params) {
  return request({
    url: '/ap/userTeam/delete',
    method: 'delete',
    params
  })
}

// 新增团队
export function saveData(data) {
  return request({
    url: '/ap/userTeam/save',
    method: 'post',
    data
  })
}

// 获取团队成员列表
export function getTeamPerson(params) {
  return request({
    url: '/ap/account/listTeamuser',
    method: 'post',
    params
  })
}

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/ap/role/listData',
    method: 'post',
    params
  })
}

// 批量添加团队成员
export function saveBatchPerson(data) {
  return request({
    url: '/ap/userTeam/saveBatchUsers',
    method: 'post',
    data
  })
}

// 删除团队成员
export function deletePerson(data) {
  return request({
    url: '/ap/userTeam/deleteUser',
    method: 'post',
    data
  })
}

// 导出团队成员
export function exportPerson(params) {
  return request({
    url: '/ap/account/downloadUsers',
    method: 'get',
    params,
    responseType: 'blob',
  })
}
