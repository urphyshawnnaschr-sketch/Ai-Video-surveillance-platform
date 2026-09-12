/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request';
import qs from 'qs';

// 获取告警列表
export function getListData(params) {
  return request({
    url: '/report/listPage',
    method: 'post',
    params,
    paramsSerializer: (params) => qs.stringify(params, { arrayFormat: 'repeat' }),
  });
}

// 获取告警详情
export function getListDataDetail(params) {
  return request({
    url: '/report/detail',
    method: 'post',
    params,
  });
}

// 获取摄像头列表
export function getCameraListData(params) {
  return request({
    url: '/camera/listLessData',
    method: 'post',
    params,
  });
}

// 获取算法列表
export function getAlgorithmListData(params) {
  return request({
    url: '/algorithm/listData',
    method: 'post',
    params,
  });
}

// 获取告警类型列表
export function getTypeListData(params) {
  return request({
    url: '/report/reportTypes',
    method: 'post',
    params,
  });
}

// 推送告警
export function pushData(params) {
  return request({
    url: '/report/pushData',
    method: 'post',
    params,
  });
}

// 获取有告警的算法
export function listTabs(data) {
  return request({
    url: 'report/listTabs',
    method: 'post',
    data,
  });
}

// 删除告警
export function deleteAlarm(data) {
  return request({
    url: '/report/delete',
    method: 'post',
    data,
  });
}

// 导出
export function exportAlarm(params) {
  return request({
    url: '/report/export',
    method: 'get',
    params,
    responseType: 'blob',
    paramsSerializer: (params) => qs.stringify(params, { arrayFormat: 'repeat' }),
  });
}

// 保存定时任务清除告警信息天数
export function saveclearReportDayConfig(data) {
  return request({
    url: '/config/saveClearReportDayConfig',
    method: 'post',
    data,
  });
}

// 获取定时任务清除告警信息天数
export function getclearReportDayConfig() {
  return request({
    url: '/config/getClearReportDayConfig',
    method: 'post',
  });
}

// 导出告警数据
export function exportData(params) {
  return request({
    url: '/report/exportData',
    method: 'get',
    params,
    responseType: 'blob',
  });
}

// 保存数据采集配置
export function saveAlarmCollect(data) {
  return request({
    url: '/algorithm/saveAlarmCollect',
    method: 'post',
    data,
  });
}

// 查询数据采集配置
export function getAlarmCollect() {
  return request({
    url: '/algorithm/getAlarmCollect',
    method: 'post',
  });
}

// 导出采集告警图片
export function exportCollectData(data) {
  return request({
    url: '/report/exportCollect',
    method: 'post',
    data,
    responseType: 'blob',
  });
}

// 删除采集数据
export function deleteAlarmCollect() {
  return request({
    url: '/report/removeCollect',
    method: 'post',
  });
}

// 查询采集数据数量
export function getAlarmCollectCount() {
  return request({
    url: '/report/getCollectCount',
    method: 'get',
  });
}

// 处理报警并推送
export function saveAuditAndPush(params) {
  return request({
    url: '/report/saveAuditAndPush',
    method: 'post',
    params,
  });
}

// 处理报警
export function saveAuditOk(params) {
  return request({
    url: '/report/saveAuditOk',
    method: 'post',
    params,
  });
}

// 推送数据
export function saveDataPush(params) {
  return request({
    url: '/report/saveDataPush',
    method: 'post',
    params,
  });
}

// 关闭报警
export function saveAuditClose(params) {
  return request({
    url: '/report/saveAuditClose',
    method: 'post',
    params,
  });
}

// 获取报警处理状态和推送状态
export function getReportMust(params) {
  return request({
    url: '/report/getReportMust',
    method: 'post',
    params,
  });
}

// 获取告警详情---新
export function DetailInfo(params) {
  return request({
    url: '/report/info',
    method: 'get',
    params,
  });
}

// 切换详情数据
export function nearlyData(data) {
  return request({
    url: '/report/nearly',
    method: 'post',
    data,
  });
}

// 获取告警情况汇总
export function alarmList(data) {
  return request({
    url: '/report/stat/list',
    method: 'post',
    data,
  });
}

// 获取告警趋势
export function alarmTrend(data) {
  return request({
    url: '/report/stat/audit/result',
    method: 'post',
    data,
  });
}

// 获取处理趋势
export function dealingTrend(data) {
  return request({
    url: '/report/stat/audit/time',
    method: 'post',
    data,
  });
}

// 获取盒子接口
export function boxList(data) {
  return request({
    url: '/location/listData5',
    method: 'post',
    data,
  });
}

// 获取事件处理目标值列表
export function targetList(data) {
  return request({
    url: '/report/target/list',
    method: 'post',
    data,
  });
}

// 事件处理目标值保存
export function targetSave(data) {
  return request({
    url: '/report/target/save',
    method: 'post',
    data,
  });
}

// 处理报警并推送
export function qryResponsiblePerson(data) {
  return request({
    url: '/groupPusgConfig/qryResponsiblePerson',
    method: 'post',
    data,
  });
}

/** 根据摄像头和算法查询各级责任人数据 */
export function sendGroupPushConfigMessage(data) {
  return request({
    url: '/groupPusgConfig/sendGroupPushConfigMessage',
    method: 'post',
    data,
  });
}

/** 正报/误报 */
export function markReportSave(data) {
  return request({
    url: '/report/markReport',
    method: 'post',
    data,
  });
}

// 告警汇总
export function summanyReport(data) {
  return request({
    url: '/report/summary',
    method: 'post',
    data,
  });
}