/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request';

// 获取位置
export function getTreeData(params) {
  return request({
    url: '/location/listTree2',
    method: 'post',
    params,
  });
}

// 新增编辑区域
export function saveRegion(params) {
  return request({
    url: '/location/save',
    method: 'post',
    params,
  });
}

// 获取区域详情
export function getRegionDetail(data) {
  return request({
    url: '/location/detail',
    method: 'post',
    data,
  });
}

// 删除区域
export function deleteRegion(params) {
  return request({
    url: '/location/delete',
    method: 'post',
    params,
  });
}

// 监测盒子是否在线
export function getHeart(params) {
  return request({
    url: '/location/heart',
    method: 'get',
    params,
  });
}

// 获取摄像头列表
export function getListData(params) {
  return request({
    url: '/camera/listPage',
    method: 'post',
    params,
  });
}

// 获取摄像头详情
export function getListDataDetail(params) {
  return request({
    url: '/camera/detail',
    method: 'post',
    params,
  });
}

// 保存摄像头数据
export function saveCameraDetail(params, data) {
  return request({
    url: '/camera/save',
    method: 'post',
    params,
    data,
  });
}

// 切换视频流类型
export function switchRtspType(params) {
  return request({
    url: '/camera/switchRtspType',
    method: 'post',
    params,
  });
}

// 切换摄像头运行状态
export function switchRunning(params) {
  return request({
    url: '/camera/switchRunning',
    method: 'post',
    params,
  });
}

// 删除摄像头
export function deleteData(params) {
  return request({
    url: '/camera/delete',
    method: 'post',
    params,
  });
}

// 获取算法下拉列表
export function getAlgorithmsListData(params) {
  return request({
    url: '/report/period/algorithms',
    method: 'post',
    params,
  });
}

// 获取配置时段列表
export function getTimeListData(params) {
  return request({
    url: '/report/period/listData',
    method: 'post',
    params,
  });
}

// 删除时段
export function deleteTimeData(params) {
  return request({
    url: '/report/period/delete',
    method: 'post',
    params,
  });
}

// 新增编辑时段
export function saveTimeData(params) {
  return request({
    url: '/report/period/save',
    method: 'post',
    params,
  });
}

// 获取关联算法列表
export function getAlgorithmListData(params) {
  return request({
    url: '/camera/algorithm/listData',
    method: 'post',
    params,
  });
}

// 拍照
export function takePhoto(params) {
  return request({
    url: '/camera/takePhoto',
    method: 'post',
    params,
  });
}

// 保存绘制
export function saveInfo(data) {
  return request({
    url: '/camera/algorithm/save',
    method: 'post',
    data,
  });
}
// 删除绘制
export function deleteInfo(data) {
  return request({
    url: '/camera/algorithm/delete',
    method: 'post',
    data,
  });
}

// 盒⼦算法查询列表
export function getAlgorithmInfo(data) {
  return request({
    url: '/location/algorithm/info',
    method: 'post',
    data,
  });
}

/*********************************************************/
// 新增、编辑摄像头
export function submitCamera(data) {
  return request({
    url: '/camera/submit',
    method: 'post',
    data,
  });
}

// 获取摄像头详情
export function cameraInfo(data) {
  return request({
    url: '/camera/info',
    method: 'post',
    data,
  });
}

// 更新视频编码信息
export function saveVideoCodec(params) {
  return request({
    url: '/camera/saveVideoCodec',
    method: 'post',
    params,
  });
}

// 手动获取流地址
export function getRtspHandler(params) {
  return request({
    url: '/camera/getRtspHandler',
    method: 'post',
    params,
  });
}
