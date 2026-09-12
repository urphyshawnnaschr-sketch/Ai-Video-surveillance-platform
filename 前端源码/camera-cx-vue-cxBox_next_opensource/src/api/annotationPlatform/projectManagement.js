/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: 张健，dhq 327650114@qq.com
 * @LastEditTime: 2022-09-26 14:00:27
 */
import request from '@/utils/request';

// 获取项目列表
export function getListData(params) {
  return request({
    url: '/ap/project/listPage',
    method: 'get',
    params
  });
}

// 获取所有项目列表
export function getProjectListData(params) {
  return request({
    url: '/ap/project/list',
    method: 'get',
    params
  });
}

// 获取项目详情
export function getDetail(params) {
  return request({
    url: '/ap/project/getDetail',
    method: 'get',
    params
  });
}

// 删除项目
export function deleteData(params) {
  return request({
    url: '/ap/project/delete',
    method: 'delete',
    params
  });
}

// 获取标注组
export function getGroupListData(params) {
  return request({
    url: '/ap/userGroup/list',
    method: 'get',
    params
  });
}

// 获取下一个id
export function getNextId(params) {
  return request({
    url: '/ap/project/getNextId',
    method: 'get',
    params
  });
}

// 上传数据集
export function uploadDataset(data) {
  return request({
    url: '/ap/file/upload/dataset',
    method: 'post',
    data
  });
}

// 上传标注示例
export function uploadDemo(data) {
  return request({
    url: '/ap/file/upload',
    method: 'post',
    data
  });
}

// 创建项目
export function saveData(data) {
  return request({
    url: '/ap/project/save',
    method: 'post',
    data
  });
}

// 修改项目
export function updateData(data) {
  return request({
    url: '/ap/project/update',
    method: 'post',
    data
  });
}

// 项目详情-总览-标注
export function getOverviewData1(params) {
  return request({
    url: '/ap/project/compute/annotation',
    method: 'get',
    params
  });
}

// 项目详情-总览-质检
export function getOverviewData2(params) {
  return request({
    url: '/ap/project/compute/annotation/review',
    method: 'get',
    params
  });
}

// 项目详情-总览-标签比例
export function getOverviewData3(params) {
  return request({
    url: '/ap/project/compute/lable/annotation',
    method: 'get',
    params
  });
}

// 项目详情-数据集图片列表
export function getImageList(params) {
  return request({
    url: '/ap/image/list',
    method: 'get',
    params
  });
}

// 项目详情-数据集批量释放
export function release(data) {
  return request({
    url: '/ap/image/release',
    method: 'post',
    data
  });
}

// 获取标注任务
export function getAssign(params) {
  return request({
    url: '/ap/image/assign',
    method: 'get',
    params
  });
}

// 获取质检任务
export function getAssignReview(params) {
  return request({
    url: '/ap/image/assign_review',
    method: 'post',
    params
  });
}

// 获取标注历史任务
export function getHistory(params) {
  return request({
    url: '/ap/image/history',
    method: 'get',
    params
  });
}

// 提交标注结果
export function saveCommit(data) {
  return request({
    url: '/ap/image/commit',
    method: 'post',
    data
  });
}
// 提交质检结果
export function saveReview(data) {
  return request({
    url: '/ap/image/review',
    method: 'post',
    data
  });
}

// 下载导出
export function dowloadData(params) {
  return request({
    url: '/ap/export/dowload',
    method: 'get',
    params,
    responseType: 'blob',
  });
}

// 查询导出记录
export function dowloadList(params) {
  return request({
    url: '/ap/export/list',
    method: 'get',
    params
  });
}

// 保存导出
export function saveDownload(data) {
  return request({
    url: '/ap/export/save',
    method: 'post',
    data
  });
}

// 删除导出
export function deleteDownload(params) {
  return request({
    url: '/ap/export/delete',
    method: 'delete',
    params
  });
}

// 项目交付
export function submitProject(params) {
  return request({
    url: '/ap/project/submit',
    method: 'post',
    params
  });
}
