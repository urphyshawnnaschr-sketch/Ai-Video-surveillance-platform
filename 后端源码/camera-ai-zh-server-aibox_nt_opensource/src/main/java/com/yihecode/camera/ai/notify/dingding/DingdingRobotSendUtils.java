package com.yihecode.camera.ai.notify.dingding;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
* Function can: DingTalk Group Bot Message Send
*
* @author zhoumingxing
* @date 2024/3/8
*/
@Slf4j
public class DingdingRobotSendUtils {

    /**
* Send
* @param webhookUrl webhook Address
* @param sign Add Sign, like Result Create Group Bot Hour Wait Check select, the Value must transmit, No rule can with not transmit
* @param title Title
* @param text Content
* @param picUrl Image url
* @param jumpUrl Point Click Jump turn Address
*/
    public static void send(String webhookUrl, String sign, String title, String text, String picUrl, String jumpUrl) {
//POST https://oapi.dingtalk.com/robot/send?access_token=XXXXXX&timestamp=XXX&sign=XXX
// {
//"msgtype":"link",
//"link": {
//"text":"this Immediate will Release new Version, create start Person xx Name It for Mangrove. And In this Before, Each when Surface Temp re big Upgrade, produce Product via Reason They all will Get One should scene instead No, this One sub, for What is Mangrove",
//"title":"Hour instead Fire Car to front open",
//"picUrl":"",
//"messageUrl":"https://www.dingtalk.com/s?__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI"
//}
//}

try {
// Add Sign
if(StrUtil.isNotBlank(sign)) {
Long timestamp = System.currentTimeMillis();
String stringToSign = timestamp +"\n"+ sign;
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(sign.getBytes("UTF-8"),"HmacSHA256"));
byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
String signbase64 = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
webhookUrl +="&timestamp="+ timestamp;
webhookUrl +="&sign="+ signbase64;
}

// Request Param
Map<String, Object> linkMap = new HashMap<>();
linkMap.put("text", text);
linkMap.put("title", title);
linkMap.put("picUrl", picUrl);
linkMap.put("messageUrl", jumpUrl);
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("msgtype","link");
dataMap.put("link", linkMap);

// Send
String response = HttpUtil.post(webhookUrl, JSON.toJSONString(dataMap), 5000);
JSONObject root = JSON.parseObject(response);
if(root.getIntValue("errcode")!= 0) {
log.error("DingTalk Message Send Failed: {}", response);
} else {
log.info("DingTalk Message Send Success: {}", response);
}
} catch (Exception e) {
log.error("DingTalk Message Send Exception: {}", e.getMessage());
}
}
}
