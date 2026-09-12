import request from '@/utils/request'


// 获取国标设备列表
export function mediaServerList(data) {
    return request({
        url: '/media/server/listPage',
        method: 'post',
        data
    })
}

// 查询国标设备详情
export function mediaServerInfo(params) {
    return request({
        url: '/media/server/info',
        method: 'get',
        params
    })
}

// 修改国标设备
export function mediaServerSave(data) {
    return request({
        url: '/media/server/save',
        method: 'post',
        data
    })
}

// 删除国标设备
export function mediaServerDel(params) {
    return request({
        url: '/media/server/delete',
        method: 'get',
        params
    })
}

// 同步设备
export function mediaServerCheck(params) {
    return request({
        url: '/media/server/check',
        method: 'post',
        params
    })
}