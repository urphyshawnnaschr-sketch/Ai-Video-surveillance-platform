import request from '@/utils/request'

// 新增组织
export function saveData(data) {
    return request({
        url: '/ap/depart/save',
        method: 'post',
        data
    })
}

// 获取组织机构树
export function listTree(data) {
    return request({
        url: '/ap/depart/listTree',
        method: 'post',
        data
    })
}

// 删除
export function deleteData(data) {
    return request({
        url: '/ap/depart/delete',
        method: 'post',
        data
    })
}

// 获取详情
export function detailData(data) {
    return request({
        url: '/ap/depart/detail',
        method: 'post',
        data
    })
}

// 角色-人员统计
export function statics(data) {
    return request({
        url: '/ap/account/statics',
        method: 'post',
        data
    })
}


// 用户新增
export function saveAccount(data) {
    return request({
        url: '/ap/account/save',
        method: 'post',
        data
    })
}
// 用户修改
export function updateAccount(data) {
    return request({
        url: '/ap/account/update',
        method: 'post',
        data
    })
}
// 用户列表
export function accountList(data) {
    return request({
        url: '/ap/account/listPage',
        method: 'post',
        data
    })
}

// 用户详情
export function accountDetail(data) {
    return request({
        url: 'ap/account/detail',
        method: 'post',
        data
    })
}

// 用户删除
export function accountDelete(data) {
    return request({
        url: 'ap/account/delete',
        method: 'post',
        data
    })
}

// 批量删除
export function batchDelete(data) {
    return request({
        url: 'ap/account/batchDelete',
        method: 'post',
        data
    })
}
// 修改密码
export function updatePassword(data) {
    return request({
        url: '/ap/account/updatePassword',
        method: 'post',
        data
    })
}
