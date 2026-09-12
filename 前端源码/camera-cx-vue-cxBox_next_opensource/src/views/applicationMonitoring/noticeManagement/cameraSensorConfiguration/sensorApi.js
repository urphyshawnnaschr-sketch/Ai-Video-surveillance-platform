import request from '@/utils/request'

export const getSensorPage = (params) => {
  return request({
    url: `/sensor/page`,
    method: 'post',
    data: params
  });
};

 
export const addSensorConfig = (data) => {
  return request({
    url: `/sensor/add`,
    method: 'post',
    data
  });
};

 
export const updateSensorConfig = (data) => {
  return request({
    url: `/sensor/update`,
    method: 'post',
    data
  });
};

 
export const batchDeleteSensor = (ids) => {
  return request({
    url: `/sensor/batchDelete`,
    method: 'post',
    data: ids
  });
};

// 下载导入模板
export const downloadSensorTemplate = () => {
  return request({
    url: '/sensor/download', 
    method: 'get',
    responseType: 'blob' 
  });
};

// 导入传感器数据
export const importSensorData = (formData) => {
  return request({
    url: '/sensor/importExcel', 
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};