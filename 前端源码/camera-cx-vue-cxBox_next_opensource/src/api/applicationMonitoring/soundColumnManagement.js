import request from '@/utils/request'

// 获取音柱列表
export function listData(params) {
    return request({
        url: '/soundColumn/listData',
        method: 'post',
        params
    })
}

// 保存音柱
export function saveData(data) {
    return request({
        url: '/soundColumn/save',
        method: 'post',
        data
    })
}

// 获取音柱详情
export function detailData(params) {
    return request({
        url: '/soundColumn/detail',
        method: 'get',
        params
    })
}

// 删除音柱
export function deleteData(data) {
    return request({
        url: '/soundColumn/delete',
        method: 'post',
        data
    })
}
