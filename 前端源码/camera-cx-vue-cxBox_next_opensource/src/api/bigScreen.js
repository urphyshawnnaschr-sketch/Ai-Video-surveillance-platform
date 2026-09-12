import request from '@/utils/request'

// 获取告警数据
export function alarmCount(params) {
  return request({
    url: '/statistic/alarm/count/today',
    method: 'post',
    params
  })
}

// 每日报警点位分布情况
export function fetchAlertSituation(params) {
  return request({
    url: '/statistic/alarm/area/audit',
    method: 'post',
    params
  })
}
// 获取本月事件告警统计
export function algorithmAudit(params) {
  return request({
    url: '/statistic/alarm/algorithm/audit',
    method: 'post',
    params
  })
}

// 获取3个月数据
export function alarm3month(data) {
  return request({
    url: '/statistic/alarm/audit/time/3month',
    method: 'post',
    data
  })
}

// 获取4周数据
export function alarm4week(data) {
  return request({
    url: '/statistic/alarm/audit/time/4week',
    method: 'post',
    data
  })
}

// 获取7天数据
export function alarm7day(data) {
  return request({
    url: '/statistic/alarm/audit/time/7day',
    method: 'post',
    data
  })
}