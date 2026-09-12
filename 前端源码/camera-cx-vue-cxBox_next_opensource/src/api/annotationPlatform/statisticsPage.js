/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request';

// 获取项目列表
export function getProjectList(params) {
  return request({
    url: '/ap/project/list',
    method: 'get',
    params
  });
}

// 获取团队列表
export function getTeamList(data) {
  return request({
    url: '/ap/userTeam/listData',
    method: 'post',
    data
  });
}

// 获取项目统计图
export function getProjectData(params) {
  return request({
    url: '/ap/project/compute/project/efficiency',
    method: 'get',
    params
  });
}

// 获取团队统计图
export function getTeamData(params) {
  return request({
    url: '/ap/project/compute/team/efficiency',
    method: 'get',
    params
  });
}

// 获取项目统计列表
export function getProjectPage(params) {
  return request({
    url: '/ap/project/compute/project/statistics',
    method: 'get',
    params
  });
}

// 获取团队统计列表
export function getTeamPage(params) {
  return request({
    url: '/ap/project/compute/team/statistics',
    method: 'get',
    params
  });
}
