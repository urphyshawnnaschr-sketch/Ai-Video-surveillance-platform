import request from '@/utils/request'

// 总数量统计
export function summary(data) {
    return request({
        url: '/tracker/report/summary',
        method: 'post',
        data
    })
}

// 分页查询
export function listPage(data) {
    return request({
        url: '/tracker/report/listPage',
        method: 'post',
        data
    })
}

// 统计图
export function timeline(data) {
    return request({
        url: '/tracker/report/timeline',
        method: 'post',
        data
    })
}

// 数据导出
export function exportData(data) {
    return request({
        url: '/tracker/report/export',
        method: 'post',
        data,
        responseType: 'blob',
    })
}


/************************************* */
// 获取总统计
export function summaryV2(data) {
    return request({
        url: '/tracker/report/summary/v2',
        method: 'post',
        data
    })
}

// 获取统计图数据
export function timelineV2(data) {
    return request({
        url: '/tracker/report/timeline/v2',
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

// 获取摄像头树
export function treeForCass(params) {
    return request({
        url: '/location/treeForCass',
        method: 'get',
        params
    })
}