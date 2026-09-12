import request from '@/utils/request'

// 保存语音告警配置
export function saveAlarmVoiceConfig(data) {
  return request({
    url: '/voicePhone/saveAlarmVoiceConfig',
    method: 'post',
    data
  })
}

// 查询语音告警配置
export function queryAlarmVoiceConfig(params) {
  return request({
    url: '/voicePhone/queryAlarmVoiceConfig',
    method: 'get',
    params
  })
}

// 添加语音通知手机号
export function savePhone(data) {
  return request({
    url: '/voicePhone/savePhone',
    method: 'post',
    data
  })
}

// 查询手机号列表
export function listData(params) {
    return request({
        url: 'voicePhone/listData',
        method: 'get',
        params
    })
}

// 删除手机号
export function deletePhone(data) {
    return request({
      url: '/voicePhone/delete',
      method: 'post',
      data
    })
}
