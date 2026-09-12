/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-09 11:19:54
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2024-01-23 22:42:26
 */
import request from '@/utils/request'

//列表
export function listPage(params) {
  return request({
    url: '/face/report/listPage2',
    method: 'post',
    params
  })
}

//列表
export function groupListData(params) {
  return request({
    url: '/face/group/listData',
    method: 'post',
    params
  })
}



//摄像头列表
export function cameraListData(params) {
  return request({
    url: '/camera/listData6',
    method: 'post',
    params
  })
}
//算法列表
export function reportImage(params) {
  return request({
    url: '/face/report/image',
    method: 'get',
    responseType:'arraybuffer',
    params
  })
}

export function listByUserId(params) {
  return request({
    url: '/face/report/listByUserId',
    method: 'post',
    params
  })
}



export function save(params) {
  return request({
    url: '/face/user/saveFromStranger',
    method: 'post',
    //responseType:'arraybuffer',
    params
  })
}

// 保存定时任务清除告警信息天数
export function saveClearFaceReportDayConfig(params) {
  return request({
    url: '/config/saveClearFaceReportDayConfig',
    method: 'post',
    params
  })
}

/**********************新接口***********************************/
// 获取列表数据
export function faceList(data) {
  return request({
    url: '/face/report/v2/page',
    method: 'post',
    data
  })
}

// 获取人脸详情
export function faceInfo(data) {
  return request({
    url: '/face/report/info',
    method: 'post',
    data
  })
}

// 获取上一条、下一条
export function faceNearly(data) {
  return request({
    url: '/face/report/nearly',
    method: 'post',
    data
  })
}

// 下载图片
export function dowloadImage(params) {
  return request({
    url: '/face/report/download/image',
    method: 'get',
    params,
    responseType: 'blob',
  });
}

// 去重设置
export function duplicate(data) {
  return request({
    url: '/face/report/setting/duplicate',
    method: 'post',
    data,
  });
}

// 调整已入库人脸
export function addImageToExist(data) {
  return request({
    url: '/face/user/addImageToExist',
    method: 'post',
    data,
  });
}

// 获取人名
export function userList(params) {
  return request({
    url: '/face/user/all',
    method: 'get',
    params,
  });
}

// 去重回显
export function duplicateInfo(params) {
  return request({
    url: '/face/report/setting/duplicate',
    method: 'get',
    params,
  });
}

// 获取摄像头
export function camerasList(data) {
  return request({
    url: '/face/report/v2/cameras',
    method: 'post',
    data,
  });
}

// 获取分组摄像头
export function groupTree(data) {
  return request({
    url: '/camera/group/tree',
    method: 'post',
    data,
  });
}


// 相似度设置
export function saveFaceSimiliarity(data) {
  return request({
    url: '/config/saveFaceSimiliarity',
    method: 'post',
    data,
  });
}

// 获取相似度
export function getFaceSimiliarity(data) {
  return request({
    url: '/config/getFaceSimiliarity',
    method: 'post',
    data,
  });
}

// 根据人员获取识别详情
export function listUserInfo(data) {
  return request({
    url: '/face/report/listUserInfo',
    method: 'post',
    data,
  });
}

// 根据人员获取识别列表
export function listUserPage(data) {
  return request({
    url: '/face/report/listUserPage',
    method: 'post',
    data,
  });
}

// 获取摄像头列表
export function listData2(data) {
  return request({
    url: '/camera/listData2',
    method: 'post',
    data,
  });
}