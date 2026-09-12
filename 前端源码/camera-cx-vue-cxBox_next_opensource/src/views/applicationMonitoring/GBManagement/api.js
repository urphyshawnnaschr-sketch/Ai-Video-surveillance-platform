import request from '@/utils/request'


// 获取国标设备列表
export function deviceList(data) {
    return request({
        url: '/wvp/device/listPage',
        method: 'post',
        data
    })
}

// 查询国标设备详情
export function deviceInfo(params) {
    return request({
        url: '/wvp/device/info',
        method: 'get',
        params
    })
}

// 修改国标设备
export function deviceSave(data) {
    return request({
        url: '/wvp/device/save',
        method: 'post',
        data
    })
}

// 删除国标设备
export function deviceDelete(params) {
    return request({
        url: '/wvp/device/delete',
        method: 'get',
        params
    })
}

// 同步设备
export function deviceSync(data) {
    return request({
        url: '/wvp/device/sync',
        method: 'post',
        data
    })
}

// 获取同步结果
export function deviceSyncStatus(data) {
    return request({
        url: '/wvp/device/syncStatus',
        method: 'post',
        data
    })
}

// 获取国标设备通道列表
export function channelList(data) {
    return request({
        url: '/wvp/channel/listPage',
        method: 'post',
        data
    })
}

// 查询国标配置信息
export function getPlatformInfo() {
    return request({
        url: '/wvp/platform/info',
        method: 'get'
    })
}

// 验证通道流是否正常
export function channelCheckStream(params) {
    return request({
        url: '/wvp/channel/stream/check',
        method: 'get',
        params
    })
}

// 获取通道流验证结果
export function channelCheckStreamMsg(data) {
    return request({
        url: '/wvp/channel/getErrMsg',
        method: 'post',
        data
    })
}

// 查询盒子/服务器列表及摄像头数量
export function boxAndChannelCount() {
    return request({
        url: '/wvp/channel/box/channel/count',
        method: 'get',
    })
}

// 分配盒子/服务器
export function channelAssign(data) {
    return request({
        url: '/wvp/channel/assign',
        method: 'post',
        data
    })
}

// 拼接国标流地址
export function channelStreamUrl(params) {
    return request({
        url: '/wvp/channel/stream/url',
        method: 'get',
        params
    })
}