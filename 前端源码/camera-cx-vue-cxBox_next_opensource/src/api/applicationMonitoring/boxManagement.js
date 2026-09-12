import request from '@/utils/request'

// 获取盒子列表
export function treeBasic(data) {
    return request({
        url: '/aibox/basic/tree',
        method: 'post',
        data
    })
}

// 新增、修改
export function saveBasic(data) {
    return request({
        url: '/aibox/basic/save',
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

// 根据盒子统计运行路数
export function cameraStatics(data) {
    return request({
        url: '/camera/statics/by/box',
        method: 'post',
        data
    })
}

// 根据盒子统计运行路数
export function cameraStaticsV2(data) {
    return request({
        url: '/camera/statics/by/query',
        method: 'post',
        data
    })
}

// 获取摄像头列表
export function cameraList(data) {
    return request({
        url: '/camera/v2/listPage',
        method: 'post',
        data
    })
}

// 获取算法
export function algorithmListAll(data) {
    return request({
        url: '/algorithm/listAll',
        method: 'post',
        data
    })
}
