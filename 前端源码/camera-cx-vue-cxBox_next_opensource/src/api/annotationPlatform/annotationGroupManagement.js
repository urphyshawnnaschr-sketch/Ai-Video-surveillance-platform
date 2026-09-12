/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request';

// 获取所有标签组
export function getListData(params) {
  return request({
    url: '/ap/userGroup/list',
    method: 'get',
    params
  });
}

// 保存标注组
export function saveData(data) {
  return request({
    url: '/ap/userGroup/save',
    method: 'post',
    data
  });
}

// 修改标注组
export function updateData(data) {
  return request({
    url: '/ap/userGroup/update',
    method: 'post',
    data
  });
}

// 删除标注组
export function deleteData(params) {
  return request({
    url: '/ap/userGroup/delete',
    method: 'delete',
    params
  });
}

// 根据标注组查询所有成员
export function getGroupPersonList(params) {
  return request({
    url: '/ap/account/listGroupUser',
    method: 'post',
    params
  });
}

// 根据创建者查询所创建团队的所有成员
export function getTeamPersonList(params) {
  return request({
    url: '/ap/account/listAllTeamUser',
    method: 'post',
    params
  });
}

// 添加成员
export function savePersonData(data, groupId) {
  return request({
    url: `/ap/userGroup/addUser/${groupId}`,
    method: 'post',
    data:data.userIds
  });
}

// 删除成员
export function deletePersonData(data, groupId) {
  return request({
    url: `/ap/userGroup/deleteUser/${groupId}`,
    method: 'post',
    data
  });
}
