import request from '@/utils/request'

// 获取盒子列表_不包含人脸盒子
export function listPage(params) {
  return request({
    url: '/location/listPage',
    method: 'post',
    params
  })
}

// 获取盒子列表_包含人脸盒子
export function listAllPage(params) {
  return request({
    url: '/location/listAllPage',
    method: 'post',
    params
  })
}

// 获取详细信息
export function detail(data) {
  return request({
    url: '/location/detail',
    method: 'post',
    data
  })
}
// 保存
export function save(params) {
  return request({
    url: '/location/save',
    method: 'post',
    params
  })
}
// 盒子列表--重启
export function restart(data) {
  return request({
    url: '/aibox/task/restart',
    method: 'post',
    data
  })
}
// 盒子列表--停止
export function stop(params) {
  return request({
    url: '/location/stop',
    method: 'post',
    params
  })
}
// 盒子列表--同步时间
export function syncTime(params) {
  return request({
    url: '/location/syncTime',
    method: 'post',
    params
  })
}
// 盒子列表--升级
export function upgrade(params) {
  return request({
    url: '/location/upgrade',
    method: 'post',
    params
  })
}
// 获取资源监控信息
export function aiboxStatus(params) {
  return request({
    url: '/aibox/status',
    method: 'post',
    params
  })
}
// 删除盒子
export function basicDelete(data) {
  return request({
    url: '/aibox/basic/delete',
    method: 'post',
    data
  })
}

// 获取机器码
export function getSn(data) {
  return request({
    url: '/location/getSn',
    method: 'post',
    data
  })
}

// 获取激活状态
export function getActived(data) {
  return request({
    url: '/location/getActived',
    method: 'post',
    data
  })
}

// 激活
export function actived(data) {
  return request({
    url: '/location/actived',
    method: 'post',
    data
  })
}