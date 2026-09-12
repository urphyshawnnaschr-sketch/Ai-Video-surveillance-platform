import request from '@/utils/request'

// 获取角色列表
export function listData(data) {
    return request({
        url: '/ap/role/listData',
        method: 'post',
        data
    })
}

// 新增角色
export function saveData(data) {
    return request({
        url: '/ap/role/save',
        method: 'post',
        data
    })
}

// 删除角色
export function deleteData(data) {
    return request({
        url: '/ap/role/delete',
        method: 'post',
        data
    })
}

// 获取角色详情
export function detailData(params) {
    return request({
        url: '/ap/role/detail',
        method: 'post',
        params
    })
}
// 查询角色区域数据
export function locationData(data) {
    return request({
        url: '/ap/role/location/listData',
        method: 'post',
        data
    })
}