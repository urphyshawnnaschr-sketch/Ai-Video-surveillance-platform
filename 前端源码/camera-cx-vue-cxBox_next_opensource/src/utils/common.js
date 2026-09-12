/*
 * @Description:
 * @Autor: 张健，dhq
 * @Date: 2022-08-18 13:57:20
 * @LastEditors: fengshiqiang-xphl fengshiqiang-xphl@gome.inc
 * @LastEditTime: 2023-10-30 14:00:35
 */

import Cookies from "js-cookie";

export function handlePublicUrl(str) {
  return VUE_APP_API_BASE_URL + str;
}

export function handleImgUrl(str) {
  return VUE_APP_API_BASE_URL + '/model/test/stream?file=' + str;
}

export function handleCameraImgUrl(str) {
  const token = Cookies.get('X-Token');
  // return VUE_APP_API_BASE_URL + '/image/stream?fileName=' + str;
  return `${VUE_APP_API_BASE_URL}/image/camera/stream?fileName=${str}&X-Token=${token}`;
}

export function handleStream(str) {
  const token = Cookies.get('X-Token');
  return `${VUE_APP_API_BASE_URL}/report/stream?id=${str}&X-Token=${token}`;
}

export function handleFace(str) {
  return VUE_APP_API_BASE_URL + '/peopleFace/avatar?id=' + str;
}

export function handleFile(str) {
  return VUE_APP_API_BASE_URL + '/ap/file/stream?id=' + str;
}

export function handleDataset(str) {
  return VUE_APP_API_BASE_URL + '/ap/image/stream?id=' + str;
}

export function hexToRgba(hex, opacity) {
  if (!hex) hex = '#ededed';
  let rgba =
    'rgba(' +
    parseInt('0x' + hex.slice(1, 3)) +
    ',' +
    parseInt('0x' + hex.slice(3, 5)) +
    ',' +
    parseInt('0x' + hex.slice(5, 7)) +
    ',' +
    (opacity || '1') +
    ')';
  return rgba;
}

export function translateTreeToData(data) {
  const result = [];
  data.forEach(item => {
    const loop = data => {
      result.push(data);
      const child = data.children;
      if (child) {
        for (let i = 0; i < child.length; i++) {
          loop(child[i]);
        }
      }
    };
    loop(item);
  });
  return result;
}

export function checkIsJSON(obj) {
  var isjson =
    typeof obj == 'object' && Object.prototype.toString.call(obj).toLowerCase() == '[object object]' && !obj.length;
  return isjson;
}

//时间戳转化年月日 时分秒
export function getMyDate(str) {
  if(!str) return '--'
  let oDate = new Date(str),
  oYear = oDate.getFullYear(),
  oMonth = oDate.getMonth()+1,
  oDay = oDate.getDate(),
  oHour = oDate.getHours(),
  oMin = oDate.getMinutes(),
  oSen = oDate.getSeconds(),
  oTime = oYear +'-'+ addZero(oMonth) +'-'+ addZero(oDay) +' '+ addZero(oHour) +':'+
addZero(oMin) +':'+addZero(oSen);
  return oTime;
}
//补零操作
function addZero(num){
  if(parseInt(num) < 10){
      num = '0'+num;
  }
  return num;
}
