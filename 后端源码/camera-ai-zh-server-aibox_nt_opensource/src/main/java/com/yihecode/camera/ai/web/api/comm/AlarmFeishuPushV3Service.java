package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.entity.SocialHook;
import com.yihecode.camera.ai.entity.SocialResult;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.ReportService;
import com.yihecode.camera.ai.service.SocialResultService;
import com.yihecode.camera.ai.web.api.aibox.PaintRectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
* Alarm Feishu Push, Pass customer account API in Line Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmFeishuPushV3Service {

    @Autowired
    private SocialResultService socialResultService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ConfigService configService;

    //Feishu token
private final Map<Long, String> feishuAccessTokenMap = new ConcurrentHashMap<>();

// Feishu token Get Time
private final Map<Long, Long> feishuAccessGetMap = new ConcurrentHashMap<>();

@Autowired
@Qualifier("asyncTaskExecutor")
private ThreadPoolTaskExecutor executor;

public void send(SocialHook socialHook, String filepath, String boxJsons, String title, String content,
String pageUrl, String imgUrl, String cameraName, String algorithmName, Long reportId) {
CompletableFuture.runAsync(() -> {
AlarmPushResult alarmPushResult = reqSend(socialHook, filepath, boxJsons, cameraName, algorithmName);

try {
// like Result Send Feishu Success, rule Mark Alarm Push Status for 1
if(alarmPushResult.isSuccess()) {
Report report = new Report();
report.setId(reportId);
report.setPushed(1);
reportService.updateById(report);
}

// Record Send Log
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
} catch (Exception e) {
//e.printStackTrace();
}
}, executor);
}

/**
* Send
* @param socialHook
* @param filepath
* @return
*/
private AlarmPushResult reqSend(SocialHook socialHook, String filepath, String boxJsons,
String camereName, String algorithmName) {
// get access token
AlarmPushResult tokenResult = getToken(socialHook);
if(!tokenResult.isSuccess()) {
return tokenResult;
}

// upload image
AlarmPushResult imageResult = this.reqUpload(socialHook, filepath, boxJsons, tokenResult.getToken());
if(!imageResult.isSuccess()) {
return imageResult;
}

try {
String baseUrl = configService.getByValTag("esbBaseUrl");
String sendUrl = configService.getByValTag("esbSendUrl");
String appKey = configService.getByValTag("esbAppKey");
String ak = configService.getByValTag("esbAk");
String sk = configService.getByValTag("esbSk");
String fsTemplatId = configService.getByValTag("esbFsTemplatId");
String fsClientId = configService.getByValTag("esbFsClientId");

Map<String, Object> templatVars = new HashMap<>();
templatVars.put("ai_img", imageResult.getImageKey());
templatVars.put("type", algorithmName);
templatVars.put("channel", camereName);

Map<String, Object> paraMap = new HashMap<>();
paraMap.put("appKey", ak);
paraMap.put("appSecret", sk);
paraMap.put("receiveIdType","CHAT_ID"); // socialHook.getWebhook());
paraMap.put("receiveType","CHAT_ID");
paraMap.put("receiverIds", Collections.singletonList(socialHook.getWebhook()));
paraMap.put("templateId", fsTemplatId);
paraMap.put("templateVar", templatVars);
paraMap.put("larkAppId", fsClientId);

String result = HttpRequest.post(baseUrl + sendUrl)
.body(JSON.toJSONString(paraMap))
.header("access-token", tokenResult.getToken())
.header("appKey", appKey)
.execute()
.body();
JSONObject object = JSON.parseObject(result);
if(object.containsKey("code") && object.getIntValue("code") == 200) {
return AlarmPushResult.builder().success(true).build();
} else {
return AlarmPushResult.builder().success(false).error("Send Message Exception").errorDetail(result).build();
}
} catch (Exception e) {
log.error("Feishu Send Message Failed", e);
return AlarmPushResult.builder().success(false).error("Send Message Failed").errorDetail(e.getMessage()).build();
}
}

/**
* Upload File
* @param socialHook
* @param filepath
* @return
*/
private AlarmPushResult reqUpload(SocialHook socialHook, String filepath, String boxJsons, String token) {
try {
PaintRectHandler paintRectHandler = new PaintRectHandler();
String newFile = paintRectHandler.paintRect2File(filepath, boxJsons);

String baseUrl = configService.getByValTag("esbBaseUrl");
String uploadUrl = configService.getByValTag("esbUploadUrl");
String appKey = configService.getByValTag("esbAppKey");

String result = HttpRequest.post(baseUrl + uploadUrl)
.form("image", new File(newFile))
.form("imageType","message")
.header("access-token", token)
.header("appKey", appKey)
.execute()
.body();
JSONObject object = JSON.parseObject(result);
if(object.containsKey("code")
&& object.getIntValue("code") == 200
&& object.containsKey("success")
&& object.getBooleanValue("success")) {
JSONObject data = object.getJSONObject("data");
return AlarmPushResult.builder().success(true).imageKey(data.getString("imageKey")).build();
} else {
return AlarmPushResult.builder().success(false).error("Image Upload failed").errorDetail(result).build();
}
} catch (Exception e) {
log.error("Feishu File Upload Exception", e);
return AlarmPushResult.builder().success(false).error("Image Upload failed").errorDetail(e.getMessage()).build();
}
}

/**
* Get TOKEN
* @param socialHook
* @return
*/
private AlarmPushResult reqToken(SocialHook socialHook) {
try {
String baseUrl = configService.getByValTag("esbBaseUrl");
String tokenUrl = configService.getByValTag("esbTokenUrl");
String appKey = configService.getByValTag("esbAppKey");
String ak = configService.getByValTag("esbAk");
String sk = configService.getByValTag("esbSk");

Map<String, Object> paramMap = new HashMap<>();
paramMap.put("ak", ak);
paramMap.put("sk", sk);
String result = HttpRequest.post(baseUrl + tokenUrl)
.body(JSON.toJSONString(paramMap))
.header("appKey", appKey)
.execute()
.body();

JSONObject object = JSON.parseObject(result);
if (object.containsKey("code")
&& object.getIntValue("code") == 200
&& object.containsKey("success")
&& object.getBooleanValue("success")) {
JSONObject data = object.getJSONObject("data");
String token = data.getString("accessToken");
int expire = data.getIntValue("expire");
feishuAccessGetMap.put(socialHook.getId(), System.currentTimeMillis() + (expire - 1500) * 1000L);
feishuAccessTokenMap.put(socialHook.getId(), token);
return AlarmPushResult.builder().success(true).token(token).build();
} else {
return AlarmPushResult.builder().success(false).error("Get token Exception").errorDetail(result).build();
}
} catch (Exception e) {
log.error("Get Feishu token Exception", e);
return AlarmPushResult.builder().success(false).error("Get token Exception").errorDetail(e.getMessage()).build();
}
}

/**
* Get TOKEN
* @param socialHook
* @return
*/
private AlarmPushResult getToken(SocialHook socialHook) {
Long expireTime = feishuAccessGetMap.get(socialHook.getId());
if(expireTime == null || System.currentTimeMillis() > expireTime) {
return reqToken(socialHook);
} else {
String token = feishuAccessTokenMap.get(socialHook.getId());
return AlarmPushResult.builder().success(true).token(token).build();
}
}

private static Map<String, String> GenSign(String signature) throws NoSuchAlgorithmException, InvalidKeyException {
if(StrUtil.isBlank(signature)) {
return null;
}

long timestamp = System.currentTimeMillis() / 1000;
// timestamp+"\n"+ Secret Key when Make Signature String
String stringToSign = timestamp +"\n"+ signature;
// make Use HmacSHA256 Algorithm Calculate Signature
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
byte[] signData = mac.doFinal(new byte[]{});
String sign = new String(Base64.encodeBase64(signData));

Map<String, String> result = new HashMap<>();
result.put("timestamp", timestamp +"");
result.put("sign", sign);
return result;
}



}
