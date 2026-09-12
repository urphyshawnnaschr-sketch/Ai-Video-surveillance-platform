package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.service.SocialResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
* Alarm DingTalk Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmDingDingPushV2Service {

    @Autowired
    private SocialResultService socialResultService;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    public void send(SocialHook socialHook, String title, String content, String pageUrl, String imgUrl, String cameraName, String algorithmName, Long reportId) {
        CompletableFuture.runAsync(() -> {
            AlarmPushResult alarmPushResult = this.send(socialHook, title, content, pageUrl, imgUrl);

            //Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(socialHook.getId());
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl(imgUrl);
socialResult.setState(alarmPushResult.isSuccess()? 0: 1);
socialResult.setErrorDetail(alarmPushResult.getErrorDetail());
socialResult.setSendText(title +"###"+ content);
socialResult.setCameraName(cameraName);
socialResult.setAlgorithmName(algorithmName);
socialResult.setReportId(reportId);
socialResultService.save(socialResult);
}, executor);
}

private AlarmPushResult send(SocialHook socialHook, String title, String content, String pageUrl, String imgUrl) {
try {
String webhook = socialHook.getWebhook();
// Add Sign
if(StrUtil.isNotBlank(socialHook.getSignature())) {
Long timestamp = System.currentTimeMillis();
String stringToSign = timestamp +"\n"+ socialHook.getSignature();
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(socialHook.getSignature().getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
String signbase64 = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
webhook +="&timestamp="+ timestamp;
webhook +="&sign="+ signbase64;
}

// Request Param
Map<String, Object> linkMap = new HashMap<>();
linkMap.put("text", content);
linkMap.put("title", title);
linkMap.put("picUrl", imgUrl);
linkMap.put("messageUrl", pageUrl);
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("msgtype","link");
dataMap.put("link", linkMap);

// Send
String response = HttpUtil.post(webhook, JSON.toJSONString(dataMap), 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("errcode") && object.getIntValue("errcode") == 0) {
return AlarmPushResult.builder().success(true).build();
} else {
return AlarmPushResult.builder().success(false).error("DingTalk Send Message Failed").errorDetail(response).build();
}
} catch (Exception e) {
log.error("DingTalk Message Send Exception: {}", e.getMessage());
return AlarmPushResult.builder().success(false).error("DingTalk Message Send Exception").errorDetail(e.getMessage()).build();
}
}
}
