 import request from '@/utils/request'; // 假设已封装实例

// 1. 获取获取区域树形结构数据
export function groupTree(data) {
    return request({
        url: '/camera/group/tree',
        method: 'post',
        data
    })
}

/** 获取区域表格数据（分页+筛选） */
export function getAreaTable(data) {
  return request({
    url: '/groupPusgConfig/page',
    method: 'post',
    data
  });
}

/** 删除区域 */
export function deleteArea(data) {
  return request({
    url: '/groupPusgConfig/batchDelete',
    method: 'post',
    data
  });
}

 /** 新增区域 */
export function addArea(data) {
  return request({
    url: '/groupPusgConfig/add',
    method: 'post',
    data
  });
}

 /** 编辑区域 */
export function editArea(data) {
  return request({
    url: '/groupPusgConfig/update',
    method: 'post',
    data
  });
}

// 获取算法列表
export function getAlgorithmOptions(params) {
  return request({
    url: '/algorithm/listData',
    method: 'post',
    params
  })
}
 
// 获取账号列表
export function getPeopleData(data) {
  return request({
    url: '/ap/account/listPage',
    method: 'post',
    data
  });
}

/** 飞书推送群组 */
export function getSocialHookList(params) {
  return request({
    url: '/social/hook/list',
    method: 'get',
    params
  })
}