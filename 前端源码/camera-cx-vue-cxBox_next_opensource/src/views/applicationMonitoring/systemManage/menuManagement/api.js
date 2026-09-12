import request from '@/utils/request'

// 获取菜单列表
export function listData(data) {
  return request({
      url: '/ap/menus/tree',
      method: 'post',
      data
  })
}

// 获取菜单列表
export function save(data) {
  return request({
      url: '/ap/menus/save',
      method: 'post',
      data
  })
}

// 删除菜单
export function del(params) {
  return request({
      url: '/ap/menus/delete',
      method: 'post',
      params
  })
}

// 获取菜单详情
export function detail(params) {
  return request({
      url: '/ap/menus/detail',
      method: 'post',
      params
  })
}





