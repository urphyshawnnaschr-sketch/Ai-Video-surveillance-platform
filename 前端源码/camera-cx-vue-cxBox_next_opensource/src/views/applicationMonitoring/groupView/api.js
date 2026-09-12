import request from '@/utils/request'

// 分组树
export function groupTree(data) {
    return request({
        url: '/camera/group/tree',
        method: 'post',
        data
    })
}

// 添加分组
export function groupSave(data) {
    return request({
        url: '/camera/group/save',
        method: 'post',
        data
    })
}

// 分组详情
export function groupInfo(data) {
    return request({
        url: '/camera/group/info',
        method: 'post',
        data
    })
}

// 删除分组
export function groupDelete(data) {
    return request({
        url: '/camera/group/delete',
        method: 'post',
        data
    })
}

// 分组关联摄像头
export function groupCameraList(data) {
    return request({
        url: '/camera/group/item/page',
        method: 'post',
        data
    })
}
// 分组新增摄像头
export function groupCameraSave(data) {
    return request({
        url: '/camera/group/item/save',
        method: 'post',
        data
    })
}
// 分组关联摄像头详情
export function camerasInfo(data) {
    return request({
        url: '/camera/group/item/cameras',
        method: 'post',
        data
    })
}

// 根据分组统计摄像头路数
export function cameraStatics(data) {
    return request({
        url: '/camera/statics/by/group',
        method: 'post',
        data
    })
}