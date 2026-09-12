import request from '@/utils/request'

// 上传文件
export function upload(data) {
    return request({
      url: '/camera/batch/import/upload',
      method: 'post',
      data
    })
}
// 清除上传记录
export function deleteAll(data) {
    return request({
      url: '/camera/batch/import/deleteAll',
      method: 'post',
      data
    })
}
// 根据校验状态查询列表数据
export function listDataByCheckState(params) {
    return request({
      url: '/camera/batch/import/listData',
      method: 'post',
      params
    })
}
// 获取整体进度
export function getState(params) {
  return request({
    url: '/camera/batch/import/status',
    method: 'get',
    params
  })
}
// 校验之后删除数据
export function deleteById(params) {
  return request({
    url: '/camera/batch/import/deleteById',
    method: 'post',
    params
  })
}
// 上传多文件
export function uploads(data) {
  return request({
    url: '/camera/batch/import/uploads',
    method: 'post',
    data
  })
}
// 修改校验之后的错误数据
export function update(data) {
  return request({
    // url: '/cameraImport/update',
    url: '/camera/batch/import/update',
    method: 'post',
    data
  })
}

// 获取上传进度
export function statusByTag(params) {
  return request({
    url: '/camera/batch/import/statusByTag',
    method: 'get',
    params
  })
}

// 导出错误文件
export function exportData(params) {
  return request({
    url: 'camera/batch/import/export',
    method: 'get',
    params,
    responseType: 'blob',
  })
}