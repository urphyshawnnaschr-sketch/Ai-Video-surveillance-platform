package com.yihecode.camera.ai.web.app.push;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;

public class AppPushUtils {

    //private static String url ="https://fc-mp-b86d0661-1b23-4318-9e5d-59252e9801b1.next.bspapp.com/send";
private static String url ="http://pushmsg.yihecode.cn/send";

// {
//"cids":"43223939402e28811969a94605b42468",
//"title":"1111",
//"content":"11900099099",
//"options": {
//"HW": {
//"/message/android/target_user_type": 1
//},
//"VV": {
//"/pushMode": 1
//}
//},
//"data":{
//"data1":1,
//"data2":2
//},
//"request_id":"212320028909901111"
//}
public static void push(String cids, String title, String content, Map<String, Object> data) {
Map<String, Object> params = new HashMap<>();
Map<String, Object> options = new HashMap<>();
Map<String, Object> HW = new HashMap<>();
HW.put("/message/android/target_user_type", 1);
Map<String, Object> VV = new HashMap<>();
VV.put("/pushMode", 1);
options.put("HW", HW);
options.put("VV", VV);
params.put("options", options);
params.put("cids", cids);
params.put("title", title);
params.put("content", content);
if (null!= data) {
params.put("data", data);
}
params.put("request_id", IdUtil.randomUUID());
String json = HttpUtil.post(url, JSON.toJSONString(params), 5000);
System.out.println(json);
}

public static void push(String[] cids, String title, String content, Map<String, Object> data) {
Map<String, Object> params = new HashMap<>();
Map<String, Object> options = new HashMap<>();
Map<String, Object> HW = new HashMap<>();
HW.put("/message/android/target_user_type", 1);
Map<String, Object> VV = new HashMap<>();
VV.put("/pushMode", 1);
Map<String, Object> XM = new HashMap<>();
VV.put("/extra.channel id","Default");
Map<String, Object> XMG = new HashMap<>();
VV.put("/extra.channel id","Default");
options.put("HW", HW);
options.put("VV", VV);
options.put("XM", XM);
options.put("XMG", XMG);
params.put("options", options);
params.put("cids", cids);
params.put("title", title);
params.put("content", content);
if (null!= data) {
params.put("data", data);
}
params.put("request_id", IdUtil.randomUUID());
String json = HttpUtil.post(url, JSON.toJSONString(params), 5000);
System.out.println(json);
}

}
