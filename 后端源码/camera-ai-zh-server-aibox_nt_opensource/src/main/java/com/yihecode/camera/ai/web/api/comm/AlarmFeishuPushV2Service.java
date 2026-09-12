package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.entity.SocialHook;
import com.yihecode.camera.ai.entity.SocialResult;
import com.yihecode.camera.ai.enums.SocialResultBusinessType;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.ReportService;
import com.yihecode.camera.ai.service.SocialResultService;
import com.yihecode.camera.ai.web.api.aibox.PaintRectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Alarm Feishu Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmFeishuPushV2Service {

    @Autowired
    private SocialResultService socialResultService;

    @Autowired
    private ReportService reportService;

    //Feishu token
private final Map<Long, String> feishuAccessTokenMap = new ConcurrentHashMap<>();

// Feishu token Get Time
private final Map<Long, Long> feishuAccessGetMap = new ConcurrentHashMap<>();

@Autowired
@Qualifier("asyncTaskExecutor")
private ThreadPoolTaskExecutor executor;


public String MODEL_DIR ="/home/yihecode";

public void send(SocialHook socialHook, String filepath, String boxJsons, String title,
String content, String pageUrl, String imgUrl, String cameraName,
String algorithmName, Long reportId, Date createdAt, Integer type) {

CompletableFuture.runAsync(() -> {
AlarmPushResult alarmPushResult = reqSend(socialHook, filepath, boxJsons, title, content, pageUrl);
log.info("Async Send Feishu Alarm Result:"+ alarmPushResult.isSuccess() +","+ alarmPushResult.getErrorDetail());
try {
// like Result Send Feishu Success, rule Mark Alarm Push Status for 1
if(ObjectUtil.equals(SocialResultBusinessType.REPORT.getType(), type)) {
Report report = new Report();
report.setId(reportId);
report.setCreatedAt(createdAt);
if (alarmPushResult.isSuccess()) {
log.info("Push Success");
// Push Result
reportService.updatePushResult(report, 1,"Push Success");
} else {
log.info("Push Failed");
// Push Result
reportService.updatePushResult(report, 2, alarmPushResult.getError());
}
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
socialResult.setBusinessType(type);
socialResultService.save(socialResult);
log.info("Record Send Log");
} catch (Exception e) {
//e.printStackTrace();
}
}, executor);
}

/**
* Sync Send
* @param socialHook
* @param filepath
* @param boxJsons
* @param title
* @param content
* @param pageUrl
* @param imgUrl
* @param cameraName
* @param algorithmName
* @param reportId
*/
public AlarmPushResult sendSync(SocialHook socialHook, String filepath, String boxJsons, String title,
String content, String pageUrl, String imgUrl, String cameraName,
String algorithmName, Long reportId) {
AlarmPushResult alarmPushResult = reqSend(socialHook, filepath, boxJsons, title, content, pageUrl);
log.info("Sync Send Feishu Alarm Result:"+ alarmPushResult.isSuccess() +","+ alarmPushResult.getErrorDetail());
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

// // like Result Send Feishu Success, rule Mark Alarm Push Status for 1
// if(alarmPushResult.isSuccess()) {
// // Push Result
// reportService.updatePushResult(reportId, 1,"Push Success");
//} else {
// // Push Result
// reportService.updatePushResult(reportId, 2, alarmPushResult.getError());
//}
return alarmPushResult;
}

/**
* Sync Send
* @param socialHook
* @param report
* @param title
* @param content
* @param pageUrl
*/
@Transactional(rollbackFor = Exception.class)
public void resendSync(SocialHook socialHook, Report report, String title,
String content, String pageUrl, SocialResult socialResult) {
if (ObjectUtil.equals(report.getPushed(), 1)) {
SocialResult updateSocialResult = new SocialResult();
updateSocialResult.setId(socialResult.getId());
updateSocialResult.setState(1);
updateSocialResult.setErrorDetail("");
socialResultService.updateById(updateSocialResult);
return;
}
AlarmPushResult alarmPushResult = reqSend(socialHook, report.getFileName(), report.getParams(), title, content, pageUrl);
log.info("Sync Send Feishu Alarm Result:"+ alarmPushResult.isSuccess() +","+ alarmPushResult.getErrorDetail());
if (alarmPushResult.isSuccess()) {
Report updateReport = new Report();
updateReport.setId(report.getId());
updateReport.setCreatedAt(report.getCreatedAt());
reportService.updatePushResult(updateReport, 1,"Push Success");
}
// Record Send Log
SocialResult updateSocialResult = new SocialResult();
updateSocialResult.setId(socialResult.getId());
updateSocialResult.setState(alarmPushResult.isSuccess()? 0: 1);
updateSocialResult.setErrorDetail(StringUtils.isEmpty(alarmPushResult.getErrorDetail())?"Again Push Success": alarmPushResult.getErrorDetail());
updateSocialResult.setResendNum(socialResult.getResendNum() + 1);
socialResultService.updateById(updateSocialResult);
}

/**
* Sync Send
* @param socialHook
* @param report
* @param title
* @param content
* @param pageUrl
*/
@Transactional(rollbackFor = Exception.class)
public AlarmPushResult resendSyncHik(SocialHook socialHook, Report report, String title,
String content, String pageUrl) throws BizException{
AlarmPushResult alarmPushResult = reqSendHik(socialHook, report.getFileName(), title, content, pageUrl);
log.info("resendSyncHik Sync Send Feishu Alarm Result:"+ alarmPushResult.isSuccess() +","+ alarmPushResult.getErrorDetail());
if(alarmPushResult.isSuccess()){
Report updateReport = new Report();
updateReport.setId(report.getId());
updateReport.setCreatedAt(report.getCreatedAt());
reportService.updatePushResult(updateReport, 1,"Push Success");
}
// Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(socialHook.getId());
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl(report.getFileName());
socialResult.setState(alarmPushResult.isSuccess()? 0: 1);
socialResult.setErrorDetail(alarmPushResult.getErrorDetail());
socialResult.setSendText(title +"###"+ content);
socialResult.setCameraName(report.getCameraName());
socialResult.setAlgorithmName("Consume Prevent Fire Alarm");
socialResult.setReportId(report.getId());
socialResultService.save(socialResult);

return alarmPushResult;
}

/**
* Send
* @param socialHook
* @param filepath
* @param title
* @param pageUrl
* @return
*/
public AlarmPushResult reqSendToUser(SocialHook socialHook, String filepath, String boxJsons, String title,
String content, String pageUrl, String staffNo) {
if(StringUtils.isEmpty(staffNo)) {
return AlarmPushResult.builder().success(false).error("Send Message Non Method").errorDetail("no Person member work No").build();
}
log.info("------------------ Test Feishu Push Person staffNo: {}", staffNo);

try {
String imageKey ="";
Map<String, Object> imgMap = new HashMap<>();
List<Map<String, Object>> groupObject = new ArrayList<>();
List<Map<String, Object>> groupObject1 = new ArrayList<>();
List< List<Map<String, Object>>> groupObject2 = new ArrayList<>();
if(StringUtils.isNotBlank(filepath)) {
AlarmPushResult alarmPushResult = this.reqUpload(socialHook, filepath, boxJsons);
log.info("------------------ Test Feishu Push Person alarmPushResult: {}", alarmPushResult);
imageKey = alarmPushResult.getImageKey();
if (!alarmPushResult.isSuccess()) {
return alarmPushResult;
}
imgMap.put("tag","img");
imgMap.put("image_key", imageKey);
groupObject1.add(imgMap);
}
AlarmPushResult alarmPushResult = getToken(socialHook);
log.info("------------------ Test Feishu Push Person getToken: {}", alarmPushResult);

Map<String, Object> aMap = new HashMap<>();
aMap.put("tag","a");
aMap.put("text","check View");
aMap.put("href", pageUrl);
Map<String, Object> textMap = new HashMap<>();
textMap.put("tag","text");
textMap.put("text", content);

groupObject.add(aMap);
groupObject.add(textMap);
groupObject2.add(groupObject);
groupObject2.add(groupObject1);

Map<String, Object> zhCnMap3 = new HashMap<>();
zhCnMap3.put("title", title);
zhCnMap3.put("content", groupObject2);
Map<String, Object> contentMap = new HashMap<>();
contentMap.put("content", groupObject2);
Map<String, Object> zhCnMap = new HashMap<>();
zhCnMap.put("zh_cn", zhCnMap3);
Map<String, Object> dataMap = new HashMap<>();

dataMap.put("msg_type","post");
dataMap.put("content", JSONUtil.toJsonStr(zhCnMap));
dataMap.put("receive_id", staffNo);
dataMap.put("receive_id_type","user_id");


// Map<String, String> signMap = GenSign(socialHook.getSignature());
// if(signMap!= null) {
// dataMap.put("timestamp", signMap.get("timestamp"));
// dataMap.put("sign", signMap.get("sign"));
//}

// instead Reason
// Proxy proxy = getProxy(socialHook.getProxyAddr());
String url = socialHook.getWebhook();
if(StrUtil.isNotBlank(socialHook.getProxyAddr())) {
url = url.replace("https://open.feishu.cn", socialHook.getProxyAddr());
}

log.info("Feishu Send Person Address: {},data:{}", url, JSON.toJSONString(dataMap));
String response = HttpRequest.post(url)
.header("Content-Type","application/json")
.header("Authorization","Bearer"+ alarmPushResult.getToken())
.body(JSON.toJSONString(dataMap))
.timeout(20000) // Timeout Time, form Bit ms
//.setProxy(proxy)
.execute()
.body();

//String response = HttpUtil.post(socialHook.getWebhook(), JSON.toJSONString(dataMap), 5000);
// {"StatusCode":0,"StatusMessage":"success","code":0,"data":{},"msg":"success"}
log.info("Feishu Send Message Back: {}", response);
JSONObject object = JSON.parseObject(response);
if(ObjectUtil.equals(object.get("code"), 0)) {
log.info("Feishu Send Message Success");
return AlarmPushResult.builder().success(true).build();
} else {
log.info("Feishu Send Message Failed");
return AlarmPushResult.builder().success(false).error("Send Message Exception").errorDetail(response).build();
}
} catch (Exception e) {
log.error("Feishu Send Message Failed", e);
return AlarmPushResult.builder().success(false).error("Send Message Failed").errorDetail(e.getMessage()).build();
}
}

/**
* Send
* @param socialHook
* @param filepath
* @param title
* @param pageUrl
* @return
*/
private AlarmPushResult reqSend(SocialHook socialHook, String filepath, String boxJsons, String title,
String content, String pageUrl) {
Map<String, Object> imgMap = new HashMap<>();
List<Map<String, Object>> groupObject = new ArrayList<>();
if(StringUtils.isNotBlank(filepath)) {
AlarmPushResult alarmPushResult = this.reqUpload(socialHook, filepath, boxJsons);
if (!alarmPushResult.isSuccess()) {
return alarmPushResult;
}
imgMap.put("tag","img");
imgMap.put("image_key", alarmPushResult.getImageKey());
groupObject.add(imgMap);
}
try {
Map<String, Object> aMap = new HashMap<>();
aMap.put("tag","a");
aMap.put("text","check View");
aMap.put("href", pageUrl);
Map<String, Object> textMap = new HashMap<>();
textMap.put("tag","text");
textMap.put("text", content);

groupObject.add(textMap);
groupObject.add(aMap);

List<List<Map<String, Object>>> groupList = new ArrayList<>();
groupList.add(groupObject);

Map<String, Object> zhCnMap = new HashMap<>();
zhCnMap.put("title", title);
zhCnMap.put("content", groupList);

Map<String, Object> zhCnMap2 = new HashMap<>();
zhCnMap2.put("zh_cn", zhCnMap);

Map<String, Object> postMap = new HashMap<>();
postMap.put("post", zhCnMap2);

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("msg_type","post");
dataMap.put("content", postMap);

Map<String, String> signMap = GenSign(socialHook.getSignature());
if(signMap!= null) {
dataMap.put("timestamp", signMap.get("timestamp"));
dataMap.put("sign", signMap.get("sign"));
}

// instead Reason
//Proxy proxy = getProxy(socialHook.getProxyAddr());
String url = socialHook.getWebhook();
if(StrUtil.isNotBlank(socialHook.getProxyAddr())) {
url = url.replace("https://open.feishu.cn", socialHook.getProxyAddr());
}
log.info("Feishu Send Group Address: {},data:{}", url, JSON.toJSONString(dataMap));

String response = HttpRequest.post(url)
.header("Content-Type","application/json")
.body(JSON.toJSONString(dataMap))
.timeout(20000) // Timeout Time, form Bit ms
//.setProxy(proxy)
.execute()
.body();

//String response = HttpUtil.post(socialHook.getWebhook(), JSON.toJSONString(dataMap), 5000);
// {"StatusCode":0,"StatusMessage":"success","code":0,"data":{},"msg":"success"}
JSONObject object = JSON.parseObject(response);
log.info("Feishu Send Message Back: {}, Determine Result:{}", object.toJSONString(), object.containsKey("StatusCode") && object.getIntValue("StatusCode") == 0);
if(object.containsKey("StatusCode") && object.getIntValue("StatusCode") == 0) {
log.info("Feishu Send Message Success");
return AlarmPushResult.builder().success(true).build();
} else {
log.info("Feishu Send Message Failed");
return AlarmPushResult.builder().success(false).error("Send Message Exception").errorDetail(response).build();
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
private AlarmPushResult reqUpload(SocialHook socialHook, String filepath, String boxJsons) {
try {
AlarmPushResult alarmPushResult = getToken(socialHook);
log.info("Feishu Get token->reqUpload: {}", alarmPushResult);
if(!alarmPushResult.isSuccess()) {
return alarmPushResult;
}
String newFile = filepath;
log.info("Feishu Get token -> boxJsons:{}", boxJsons);
if(StringUtils.isNotBlank(boxJsons)) {
PaintRectHandler paintRectHandler = new PaintRectHandler();
newFile = paintRectHandler.paintRect2File(filepath, boxJsons);
log.info("Feishu Get token Again Draw make Image -> newFile:{}", newFile);

}
// instead Reason
//Proxy proxy = getProxy(socialHook.getProxyAddr());

String url ="https://open.feishu.cn/open-apis/im/v1/images";
if(StrUtil.isNotBlank(socialHook.getProxyAddr())) {
url = url.replace("https://open.feishu.cn", socialHook.getProxyAddr());
}
log.info("Feishu Upload Image Address: {}", url);

String result = HttpRequest.post(url)
.form("image", new File(newFile))
.form("image_type","message")
.header("Authorization","Bearer"+ alarmPushResult.getToken())
//.setProxy(proxy)
.execute()
.body();
JSONObject object = JSON.parseObject(result);
if(object.containsKey("code") && object.getIntValue("code") == 0) {
JSONObject data = object.getJSONObject("data");
return AlarmPushResult.builder().success(true).imageKey(data.getString("image_key")).build();
} else {
return AlarmPushResult.builder().success(false).error("Image Upload failed").errorDetail(result).build();
}
// {"code":0,"data":{"image_key":"img_v3_02mi_2c776a5c-f775-4285-8768-062546630afg"},"msg":"success"}
} catch (Exception e) {
log.error("Feishu File Upload Exception", e);
return AlarmPushResult.builder().success(false).error("Image Upload failed").errorDetail(e.getMessage()).build();
}
}

/**
* Send (Hikvision Alert)
* @param socialHook
* @param filepath
* @param title
* @param pageUrl
* @return
*/
public AlarmPushResult reqSendHik(SocialHook socialHook, String filepath, String title,
String content, String pageUrl) throws BizException {
AlarmPushResult alarmPushResult = this.reqUploadHik(socialHook, filepath);
if (!alarmPushResult.isSuccess()) {
return alarmPushResult;
}

try {
Map<String, Object> aMap = new HashMap<>();
aMap.put("tag","a");
aMap.put("text","check View");
aMap.put("href", pageUrl);
Map<String, Object> imgMap = new HashMap<>();
imgMap.put("tag","img");
imgMap.put("image_key", alarmPushResult.getImageKey());
Map<String, Object> textMap = new HashMap<>();
textMap.put("tag","text");
textMap.put("text", content);

List<Map<String, Object>> groupObject = new ArrayList<>();
groupObject.add(textMap);
groupObject.add(aMap);
groupObject.add(imgMap);

List<List<Map<String, Object>>> groupList = new ArrayList<>();
groupList.add(groupObject);

Map<String, Object> zhCnMap = new HashMap<>();
zhCnMap.put("title", title);
zhCnMap.put("content", groupList);

Map<String, Object> zhCnMap2 = new HashMap<>();
zhCnMap2.put("zh_cn", zhCnMap);

Map<String, Object> postMap = new HashMap<>();
postMap.put("post", zhCnMap2);

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("msg_type","post");
dataMap.put("content", postMap);

Map<String, String> signMap = GenSign(socialHook.getSignature());
if (signMap!= null) {
dataMap.put("timestamp", signMap.get("timestamp"));
dataMap.put("sign", signMap.get("sign"));
}

// instead Reason
//Proxy proxy = getProxy(socialHook.getProxyAddr());
String url = socialHook.getWebhook();
if (StrUtil.isNotBlank(socialHook.getProxyAddr())) {
url = url.replace("https://open.feishu.cn", socialHook.getProxyAddr());
}
log.info("Hik Feishu Send Group Address: {},data:{}", url, JSON.toJSONString(dataMap));

String response = HttpRequest.post(url)
.header("Content-Type","application/json")
.body(JSON.toJSONString(dataMap))
.timeout(20000) // Timeout Time, form Bit ms
//.setProxy(proxy)
.execute()
.body();

//String response = HttpUtil.post(socialHook.getWebhook(), JSON.toJSONString(dataMap), 5000);
// {"StatusCode":0,"StatusMessage":"success","code":0,"data":{},"msg":"success"}
JSONObject object = JSON.parseObject(response);
log.info("Hik Feishu Send Message Back: {}, Determine Result:{}", object.toJSONString(), object.containsKey("StatusCode") && object.getIntValue("StatusCode") == 0);
if (object.containsKey("StatusCode") && object.getIntValue("StatusCode") == 0) {
log.info("Hik Feishu Send Message Success");
return AlarmPushResult.builder().success(true).build();
} else {
log.info("Hik Feishu Send Message Failed");
return AlarmPushResult.builder().success(false).error("Send Message Exception").errorDetail(response).build();
}
} catch (Exception e) {
log.error("Hik Feishu Send Message Failed,{}", e.getMessage(), e);
throw new BizException("Hik Feishu Send Message Failed");
// return AlarmPushResult.builder().success(false).error("Send Message Failed").errorDetail(e.getMessage()).build();
}
}

/**
* Upload File (Hikvision)
*
* @param socialHook
* @param filepath
* @return
*/
private AlarmPushResult reqUploadHik(SocialHook socialHook, String filepath) throws BizException {
try {
AlarmPushResult alarmPushResult = getToken(socialHook);
if (!alarmPushResult.isSuccess()) {
return alarmPushResult;
}
//
// PaintRectHandler paintRectHandler = new PaintRectHandler();
// String newFile = paintRectHandler.paintRect2File(filepath, boxJsons);

// instead Reason
//Proxy proxy = getProxy(socialHook.getProxyAddr());

String url ="https://open.feishu.cn/open-apis/im/v1/images";
if (StrUtil.isNotBlank(socialHook.getProxyAddr())) {
url = url.replace("https://open.feishu.cn", socialHook.getProxyAddr());
}
log.info("Hik Feishu Upload Image Address: {}", url);

// String newFilePath = Paths.get(MODEL_DIR, filepath).toString();
// File file = new File(newFilePath);
// if (!file.exists() ||!file.isFile()) {
// log.error("File does not exist or not is Valid File: {}", newFilePath);
// return AlarmPushResult.builder().success(false).error("File does not exist").build();
//}
String result = HttpRequest.post(url)
.form("image", new File(filepath))
.form("image_type","message")
.header("Authorization","Bearer"+ alarmPushResult.getToken())
//.setProxy(proxy)
.execute()
.body();
log.info("Hik Feishu Upload Image Back: {}", result);
JSONObject object = JSON.parseObject(result);
if (object.containsKey("code") && object.getIntValue("code") == 0) {
JSONObject data = object.getJSONObject("data");
return AlarmPushResult.builder().success(true).imageKey(data.getString("image_key")).build();
} else {
return AlarmPushResult.builder().success(false).error("Image Upload failed").errorDetail(result).build();
}
// {"code":0,"data":{"image_key":"img_v3_02mi_2c776a5c-f775-4285-8768-062546630afg"},"msg":"success"}
} catch (Exception e) {
log.error("Hik Feishu File Upload Exception", e);
throw new BizException("Hik Feishu File Upload Exception");
// return AlarmPushResult.builder().success(false).error("Image Upload failed").errorDetail(e.getMessage()).build();
}
}


/**
* Get TOKEN
* @param socialHook
* @return
*/
private AlarmPushResult reqToken(SocialHook socialHook) {
try {
String url ="https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
if(StrUtil.isNotBlank(socialHook.getProxyAddr())) {
url = url.replace("https://open.feishu.cn", socialHook.getProxyAddr());
}
log.info("Feishu token Request URL: {}", url);

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("app_id", socialHook.getAppId());
dataMap.put("app_secret", socialHook.getAppSecret());
// String response = HttpUtil.post(url, JSON.toJSONString(dataMap), 5000);

// instead Reason
//Proxy proxy = getProxy(socialHook.getProxyAddr());

String response = HttpRequest.post(url)
.header("Content-Type","application/json")
.body(JSON.toJSONString(dataMap))
.timeout(5000) // Timeout Time, form Bit ms
//.setProxy(proxy)
.execute()
.body();

JSONObject object = JSON.parseObject(response);
if (object.containsKey("code") && object.getIntValue("code") == 0) {
int expire = object.getIntValue("expire");
String token = object.getString("tenant_access_token");

feishuAccessGetMap.put(socialHook.getId(), System.currentTimeMillis() + (expire - 1500) * 1000L);
feishuAccessTokenMap.put(socialHook.getId(), token);
return AlarmPushResult.builder().success(true).token(token).build();
} else {
return AlarmPushResult.builder().success(false).error("Get token Exception").errorDetail(response).build();
}
// {"code":0,"expire":7200,"msg":"ok","tenant_access_token":"t-g1045ng8M5MZUMBGCJ6K6IBRPW2YOCNDCLVYSPR2"}
} catch (Exception e) {
log.error("Get Feishu token Exception", e);
return AlarmPushResult.builder().success(false).error("Get token Exception").errorDetail(e.getMessage()).build();
}
}

/**
* Get TOKEN
*
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

/**
* Convert instead Reason Address
* @param proxyAddr
* @return
*/
private static Proxy getProxy(String proxyAddr) {
if(StrUtil.isBlank(proxyAddr)) {
return null;
}

// Correct rule Match allocate ip:port Format (Ignore Protocol and Path)
String regex ="(?:https?://)?(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";
Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(proxyAddr);

if (matcher.find()) {
String ip = matcher.group(1);
String port = matcher.group(2);
// System.out.println("IP:"+ ip +", Port:"+ port);
return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, Integer.parseInt(port)));
}
return null;
}



// Create Button Action Aux assist Method
private List<Map<String, Object>> createButtonActions(Long reportId) {
List<Map<String, Object>> buttonList = new ArrayList<>();

// Process Button
Map<String, Object> processAction = new HashMap<>();
processAction.put("key","process_"+ reportId);
processAction.put("text","Process");
processAction.put("type","primary");
Map<String, Object> processValue = new HashMap<>();
processValue.put("action","process");
processValue.put("reportId", reportId);
processAction.put("value", JSON.toJSONString(processValue));
buttonList.add(processAction);

// receive to Button
Map<String, Object> receiveAction = new HashMap<>();
receiveAction.put("key","receive_"+ reportId);
receiveAction.put("text","receive to");
receiveAction.put("type","default");
Map<String, Object> receiveValue = new HashMap<>();
receiveValue.put("action","receive");
receiveValue.put("reportId", reportId);
receiveAction.put("value", JSON.toJSONString(receiveValue));
buttonList.add(receiveAction);

// Close Button
Map<String, Object> closeAction = new HashMap<>();
closeAction.put("key","close_"+ reportId);
closeAction.put("text","Close");
closeAction.put("type","danger");
Map<String, Object> closeValue = new HashMap<>();
closeValue.put("action","close");
closeValue.put("reportId", reportId);
closeAction.put("value", JSON.toJSONString(closeValue));
buttonList.add(closeAction);

return buttonList;
}


public AlarmPushResult reqSendToUserWithButtons(SocialHook socialHook, String filepath, String boxJsons,
String title, String content, String pageUrl, String staffNo,
Long reportId) {
if (StringUtils.isEmpty(staffNo)) {
return AlarmPushResult.builder().success(false).error("Send Message Non Method").errorDetail("no Person member work No").build();
}

try {
// 1. Build Card Content
Map<String, Object> card = buildCardContent(socialHook, filepath, boxJsons, title, content, pageUrl, reportId);

// 2. Create Card Template Get Card ID
String cardId = createCardTemplate(socialHook, card);
if (cardId == null) {
return AlarmPushResult.builder()
.success(false)
.error("Create Card Template Failed")
.build();
}

// 3. make Use Card ID Send Message
return sendMessageWithCardId(socialHook, staffNo, cardId);
} catch (Exception e) {
log.error("Feishu Send Belt Button Message Failed", e);
return AlarmPushResult.builder()
.success(false)
.error("Send Message Failed")
.errorDetail(e.getMessage())
.build();
}
}

private Map<String, Object> buildCardContent(SocialHook socialHook, String filepath, String boxJsons,
String title, String content, String pageUrl, Long reportId) {
Map<String, Object> card = new HashMap<>();

// 1. Add schema Version (must)
card.put("schema","2.0");

// 2. Head part
Map<String, Object> header = new HashMap<>();
header.put("template","blue");
Map<String, Object> headerTitle = new HashMap<>();
headerTitle.put("tag","plain_text");
headerTitle.put("content", title);
header.put("title", headerTitle);
card.put("header", header);

// 3. Config
Map<String, Object> config = new HashMap<>();
config.put("wide_screen_mode", true);
config.put("enable_forward", true);
card.put("config", config);

// 4. body Capacity Device
Map<String, Object> body = new HashMap<>();
List<Map<String, Object>> elements = new ArrayList<>();

// Text Content
if (StringUtils.isNotBlank(content)) {
Map<String, Object> contentElement = new HashMap<>();
contentElement.put("tag","markdown");
contentElement.put("content", content);
elements.add(contentElement);
}

// Image (Need First Upload)
if (StringUtils.isNotBlank(filepath)) {
AlarmPushResult uploadResult = this.reqUpload(socialHook, filepath, boxJsons);
if (uploadResult.isSuccess()) {
Map<String, Object> imgElement = new HashMap<>();
imgElement.put("tag","img");
imgElement.put("img_key", uploadResult.getImageKey());

Map<String, Object> altText = createPlainText("Alert Image");
imgElement.put("alt", altText);

elements.add(imgElement);
}
}

// Chain connect
if (StringUtils.isNotBlank(pageUrl)) {
Map<String, Object> linkElement = new HashMap<>();
linkElement.put("tag","a");
linkElement.put("text","View Detail");
linkElement.put("href", pageUrl);
elements.add(linkElement);
}

// Button (make Use column_set real current Horizontal Row Column)
if (reportId!= null) {
// Create Button Capacity Device (Horizontal Row Column)
Map<String, Object> buttonContainer = new HashMap<>();
buttonContainer.put("tag","column_set");

List<Map<String, Object>> columns = new ArrayList<>();

// receive to Button
columns.add(createButtonColumn("receive to","default","receive_"+ reportId));

// Processing Button
columns.add(createButtonColumn("Processing","primary","process_"+ reportId));

// Close Button
columns.add(createButtonColumn("Close","danger","close_"+ reportId));

buttonContainer.put("columns", columns);
elements.add(buttonContainer);
}

// will Element Add to body
body.put("elements", elements);
card.put("body", body);

return card;
}

// Create Button Column Aux assist Method
private Map<String, Object> createButtonColumn(String text, String type, String value) {
Map<String, Object> column = new HashMap<>();
column.put("tag","column");
column.put("width","weighted");
column.put("weight", 1);

Map<String, Object> button = new HashMap<>();
button.put("tag","button");
button.put("text", createPlainText(text));
button.put("type", type);
button.put("value", value);

List<Map<String, Object>> elements = new ArrayList<>();
elements.add(button);
column.put("elements", elements);

return column;
}

private Map<String, Object> createPlainText(String content) {
Map<String, Object> text = new HashMap<>();
text.put("tag","plain_text");
text.put("content", content);
return text;
}



private String createCardTemplate(SocialHook socialHook, Map<String, Object> cardContent) {
try {
AlarmPushResult tokenResult = getToken(socialHook);
if (!tokenResult.isSuccess()) {
log.error("Get Feishu token Failed");
return null;
}

String url ="https://open.feishu.cn/open-apis/cardkit/v1/cards";
if (StrUtil.isNotBlank(socialHook.getProxyAddr())) {
url = url.replace("https://open.feishu.cn", socialHook.getProxyAddr());
}

// Build Complete whole Request Body - Contain type and data Field
Map<String, Object> requestBody = new HashMap<>();
requestBody.put("type","card_json"); // Refer Fixed Card Type for JSON Card
// will Card Content Convert for JSON String Make for data Field Value
requestBody.put("data", JSON.toJSONString(cardContent));

String jsonPayload = JSON.toJSONString(requestBody);
log.info("Create Card Template Request: {}", jsonPayload);

String response = HttpRequest.post(url)
.header("Content-Type","application/json")
.header("Authorization","Bearer"+ tokenResult.getToken())
.body(jsonPayload)
.timeout(20000)
.execute()
.body();

log.info("Create Card Template Back: {}", response);
JSONObject jsonResponse = JSON.parseObject(response);

// Process Response
if (jsonResponse!= null && jsonResponse.getInteger("code") == 0) {
JSONObject data = jsonResponse.getJSONObject("data");
if (data!= null) {
return data.getString("card_id");
}
}

log.error("Create Card Template Failed: {}", response);
return null;
} catch (Exception e) {
log.error("Create Card Template Exception", e);
return null;
}
}

private AlarmPushResult sendMessageWithCardId(SocialHook socialHook, String staffNo, String cardId) {
try {
AlarmPushResult tokenResult = getToken(socialHook);
if (!tokenResult.isSuccess()) {
log.error("Get Feishu token Failed");
return AlarmPushResult.builder().success(false).error("Get Feishu token Failed").build();
}

String url = socialHook.getWebhook();
if (StrUtil.isNotBlank(socialHook.getProxyAddr())) {
url = url.replace("https://open.feishu.cn", socialHook.getProxyAddr());
}



Map<String, Object> requestBody = new HashMap<>();
requestBody.put("receive_id", staffNo);
requestBody.put("msg_type","interactive");

// According to Official Method Document Build Correct content result structure
Map<String, Object> contentData = new HashMap<>();
contentData.put("card_id", cardId);

Map<String, Object> contentObj = new HashMap<>();
contentObj.put("type","card");
contentObj.put("data", contentData);

// will contentObj Convert for JSON String
String contentStr = JSON.toJSONString(contentObj);
requestBody.put("content", contentStr);

String jsonPayload = JSON.toJSONString(requestBody);
log.info("Send Card Message Request: {}", jsonPayload);

String response = HttpRequest.post(url)
.header("Content-Type","application/json")
.header("Authorization","Bearer"+ tokenResult.getToken())
.body(jsonPayload)
.timeout(20000)
.execute()
.body();

log.info("Send Card Message Back: {}", response);
JSONObject jsonResponse = JSON.parseObject(response);
if (jsonResponse!= null && jsonResponse.getInteger("code") == 0) {
return AlarmPushResult.builder().success(true).build();
} else {
log.error("Send Card Message Failed: {}", response);
return AlarmPushResult.builder()
.success(false)
.error("Send Card Message Failed")
.errorDetail(response)
.build();
}
} catch (Exception e) {
log.error("Send Card Message Exception", e);
return AlarmPushResult.builder()
.success(false)
.error("Send Card Message Exception")
.errorDetail(e.getMessage())
.build();
}
}

}
