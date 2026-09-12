package com.yihecode.camera.ai.notify.sms;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.*;

/**
* Description: SMS Send Utils
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
public class SendSmsUtil {

    private static final String ENCODING = "UTF-8";
    //
private static final String apikey ="acc";
//
private static final String tplid ="";


/**
* Send
* @param mobiles
* @param cameraName
* @param algorithmName
*/
public static void send(String mobiles, String cameraName, String algorithmName, String smsAppkey, String smsTplId) {
if(StrUtil.isBlank(mobiles)) {
return;
}

//
try {
Map<String, Object> params = new HashMap<>();
params.put("apikey", smsAppkey);
params.put("mobile", mobiles);
params.put("tpl_id", smsTplId);
params.put("tpl_value", URLEncoder.encode("#camera#", ENCODING) +"="+ URLEncoder.encode(cameraName, ENCODING) +"&"+ URLEncoder.encode("#algorithm#", ENCODING) +"="+ URLEncoder.encode(algorithmName, ENCODING) +"&"+ URLEncoder.encode("#time#", ENCODING) +"="+ DateUtil.format(new Date(),"MM/dd HH:mm"));
String response = HttpUtil.post("https://sms.yunpian.com/v2/sms/tpl_batch_send.json", params, 5000);
// log.info("Yunpian SMS Send Result:{}", response);
} catch (Exception e) {
log.error("Yunpian SMS Send Exception:{}", e.getMessage());
}
}

/**
* Send form SMS
* @author Abyss
* @date 2023/11/29 00:54
* @param mobiles Phone
* @param text SMS Content
*/
public static void singleSend(String appkey, String mobiles, String text) {
if(StrUtil.isBlank(mobiles)) {
return;
}
try {
Map<String, Object> params = new HashMap<>();
// params.put("apikey", appkey);
if (StringUtils.isNotBlank(appkey)) {
params.put("apikey", appkey);
} else {
params.put("apikey", apikey);
}

params.put("mobile", mobiles);
params.put("text", text);
String result = HttpUtil.post("https://sms.yunpian.com/v2/sms/single_send.json", params);
// System.out.println(result);
// log.info("SMS Send Result, result: {}", result);
} catch (Exception e) {
log.error("SMS Send Exception", e);
}
}
}
