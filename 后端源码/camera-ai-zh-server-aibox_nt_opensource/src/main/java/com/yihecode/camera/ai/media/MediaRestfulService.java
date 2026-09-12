package com.yihecode.camera.ai.media;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.config.MediaNodeConfig;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.MediaServer;
import com.yihecode.camera.ai.javacv.DecodeRtspUlr;
import com.yihecode.camera.ai.service.MediaServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class MediaRestfulService {

    @Autowired
    private MediaNodeConfig mediaNodeConfig;

    @Autowired
    private MediaServerService mediaServerService;

//public void setServerConfig() {
// if(mediaNodeConfig.isConnected()) {
// return;
//}
//
// String serverUrl = String.format("http://%s:%s/media/hook/", mediaNodeConfig.getHookIp(), mediaNodeConfig.getHookPort());
// MediaConfigNode mediaConfigNode = new MediaConfigNode();
// mediaConfigNode.setSecret(mediaNodeConfig.getSecret());
// // Local Test
// //mediaConfigNode.setFfmpegBin("D:\\ffmpeg-7.1-full_build\\ffmpeg-7.1-full_build\\bin\\ffmpeg.exe");
//// mediaConfigNode.setFfmpegBin("/usr/bin/ffmpeg");
// mediaConfigNode.setFfmpegCmd("%s -i %s -an -c:v libx264 -f rtsp %s");
// mediaConfigNode.setHookEnable(1);
// mediaConfigNode.setProtocolEnableHls(1);
// mediaConfigNode.setHookOnPlay(serverUrl +"on_play");
// mediaConfigNode.setHookOnPublish(serverUrl +"on_publish");
// mediaConfigNode.setHookOnRecordMp4(serverUrl +"on_record_mp4");
// mediaConfigNode.setHookOnServerStarted(serverUrl +"on_server_started");
// mediaConfigNode.setHookOnStreamChanged(serverUrl +"on_stream_changed");
// mediaConfigNode.setHookOnStreamNoneReader(serverUrl +"on_stream_none_reader");
// mediaConfigNode.setHookOnStreamNotFound(serverUrl +"on_stream_not_found");
// mediaConfigNode.setHookOnServerKeepalive(serverUrl +"on_server_keepalive");
// mediaConfigNode.setHookAliveInterval(60.0f);
//
// log.info("{}", JSON.toJSONString(mediaConfigNode));
//
// String reqUrl = String.format("http://%s:%s/index/api/setServerConfig", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
//
// try {
// String response = HttpUtil.post(reqUrl, JSON.toJSONString(mediaConfigNode), 5000);
// JSONObject object = JSON.parseObject(response);
// if(object.getIntValue("code") == 0 && object.getIntValue("changed") == 0) {
// log.info("ZLM Set Success");
// mediaNodeConfig.setConnected(true);
//
// // Restart Service
// restartServer();
//} else {
// log.error("ZLM Set Failed, Result {}", response);
//}
//} catch (Exception e) {
// log.error("Call ZLM Set API Exception, ex: {}, media: {}", e.getMessage(), mediaNodeConfig);
//}
//}
//
// /**
// * Restart zlmediakit
// * @return
// */
// private boolean restartServer() {
// try {
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaNodeConfig.getSecret());
// String reqUrl = String.format("http://%s:%s/index/api/restartServer", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
// String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
// JSONObject object = JSON.parseObject(response);
// log.info("Restart ZLM Return Result, resp: {}", response);
// if (object.containsKey("code") && object.getIntValue("code") == 0) {
// return true;
//} else {
// log.error("Call ZLM Restart API Error, resp: {}", response);
//}
//} catch (Exception e) {
// log.error("Call ZLM Restart API Exception, ex: {}", e.getMessage());
//}
// return false;
//}

public List<MediaStreamNode> getMediaList() {
List<MediaStreamNode> mediaStreamNodeList = new ArrayList<>();

String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaNodeConfig.getSecret());

try {
String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0) {
JSONArray array = object.getJSONArray("data");
if(array == null || array.isEmpty()) {
return new ArrayList<>();
}

int len = array.size();
for(int i = 0; i < len; i++) {
JSONObject row = array.getJSONObject(i);

// Only Record make rtsp Protocol and app for live Video
if(!"rtsp".equalsIgnoreCase(row.getString("schema")) &&"live".equalsIgnoreCase(row.getString("app"))) {
continue;
}

MediaStreamNode mediaStreamNode = new MediaStreamNode();
mediaStreamNode.setApp(row.getString("app"));
mediaStreamNode.setStream(row.getString("stream"));
mediaStreamNode.setSchema(row.getString("schema"));
mediaStreamNode.setOriginUrl(row.getString("originUrl"));
mediaStreamNode.setReaderCount(row.getIntValue("readerCount"));
mediaStreamNode.setRecordMp4(row.getBooleanValue("isRecordingMP4"));
mediaStreamNodeList.add(mediaStreamNode);
}
} else {
log.info("Call ZLM Media List API Exception, url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.info("Call ZLM Media List API Exception, ex {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return mediaStreamNodeList;
}

// public boolean addStream(Long cameraId, String rtspUrl) {
// String reqUrl = String.format("http://%s:%s/index/api/addStreamProxy", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
//
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaNodeConfig.getSecret());
// params.put("vhost","__defaultVhost__");
// params.put("app","live");
// params.put("stream", cameraId);
// params.put("url", rtspUrl);
// params.put("enable_hls", false);
//
// try {
// String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
// JSONObject object = JSON.parseObject(response);
// log.info("add stream {}", object);
// if(object.containsKey("code") && object.getIntValue("code") == 0) {
// return true;
//} else {
// log.error("Call ZLM Pull Stream API Exception, Address: {}, Result: {}", reqUrl, response);
//}
//} catch (Exception e) {
// log.info("Call ZLM Pull Stream API Exception, ex: {}, Address:{}, data: {}", e.getMessage(), reqUrl, params);
//}
// return false;
//}

// public boolean addStream(Camera camera) {
// try {
// MediaServer mediaServer = mediaServerService.getById(camera.getMediaServerId());
// if(mediaServer == null) {
// log.error("Camera not has Corresponding zlm Node, not can Pull Stream");
// return false;
//}
//
// String reqUrl = String.format("http://%s:%s/index/api/addStreamProxy", mediaServer.getIp(), mediaServer.getHttpPort());
//
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaServer.getSecret());
// params.put("vhost","__defaultVhost__");
// params.put("app","live");
// params.put("stream", camera.getId());
// params.put("url", camera.getRtspUrl());
// params.put("enable_hls", false);
//
// String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
// JSONObject object = JSON.parseObject(response);
// if(object.containsKey("code") && object.getIntValue("code") == 0) {
// return true;
//} else {
// log.error("Call ZLM Pull Stream API Exception, Address: {}, Result: {}", reqUrl, response);
//}
//} catch (Exception e) {
// log.info("Call ZLM Pull Stream API Exception, ex: {}", e.getMessage());
//}
// return false;
//}

/**
* Pull Stream
*
* @param camera
* @return
*/
public boolean addStream(Camera camera, MediaServer mediaServer) {
try {
String app ="live";
String reqUrl = String.format("http://%s:%s/index/api/addStreamProxy", mediaServer.getIp(), mediaServer.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());
params.put("vhost","__defaultVhost__");
params.put("app", app);
params.put("stream", camera.getId());
params.put("url", DecodeRtspUlr.processRtspUrl(camera.getRtspUrl()));
params.put("enable_hls", false);

String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0) {
return true;
} else {
log.error("Call ZLM Pull Stream API Exception, Address: {}, Result: {}", reqUrl, response);
}
} catch (Exception e) {
log.info("Call ZLM Pull Stream API Exception, ex: {}", e.getMessage());
}
return false;
}

// public boolean addStreamFFmpeg(Long cameraId, String rtspUrl) {
// String reqUrl = String.format("http://%s:%s/index/api/addFFmpegSource", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
//
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaNodeConfig.getSecret());
// params.put("vhost","__defaultVhost__");
// params.put("app","live");
// params.put("stream", cameraId);
// params.put("src_url", rtspUrl);
// params.put("dst_url", String.format("rtsp://127.0.0.1/h265/%s", cameraId));
// params.put("timeout_ms", 15000);
// params.put("enable_hls", 0);
// params.put("enable_mp4", 0);
// log.info("=============== {}", JSON.toJSONString(params));
//
// try {
// String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
// JSONObject object = JSON.parseObject(response);
// log.info("add stream ffmpeg {}", object);
// if(object.containsKey("code") && object.getIntValue("code") == 0) {
// return true;
//} else {
// log.error("Call ZLM ffmpeg Pull Stream API Exception, Address: {}, Result: {}", reqUrl, response);
//}
//} catch (Exception e) {
// log.info("Call ZLM ffmpeg Pull Stream API Exception, ex: {}, Address:{}, data: {}", e.getMessage(), reqUrl, params);
//}
// return false;
//}

/**
* ffmpeg instead Reason Pull Stream
* @param cameraId
* @param rtspUrl
* @param mediaServer
* @return
*/
public boolean addStreamFFmpeg(Long cameraId, String rtspUrl, MediaServer mediaServer) {
String reqUrl = String.format("http://%s:%s/index/api/addFFmpegSource", mediaServer.getIp(), mediaServer.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());
params.put("vhost","__defaultVhost__");
params.put("app","live");
params.put("stream", cameraId);
params.put("src_url", rtspUrl);
params.put("dst_url", String.format("rtsp://127.0.0.1/h265/%s", cameraId));
params.put("timeout_ms", 15000);
params.put("enable_hls", 0);
params.put("enable_mp4", 0);

try {
String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0) {
return true;
} else {
log.error("Call ZLM ffmpeg Pull Stream API Exception, Address: {}, Result: {}", reqUrl, response);
}
} catch (Exception e) {
log.info("Call ZLM ffmpeg Pull Stream API Exception, ex: {}, Address:{}, data: {}", e.getMessage(), reqUrl, params);
}
return false;
}

public void closeStream(String app, String stream) {
List<MediaServer> mediaServers = mediaServerService.list();

for(MediaServer mediaServer: mediaServers) {
try {
String reqUrl = String.format("http://%s:%s/index/api/close_streams", mediaServer.getIp(), mediaServer.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());
params.put("app", app);
params.put("stream", stream);
params.put("force", 1);
params.put("vhost","__defaultVhost__");

String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
JSONObject object = JSON.parseObject(response);
log.info("ZLM Close Pull Stream Result {}", object);
} catch (Exception e) {
log.info("Call ZLM Close Stream API Exception, ex: {}, media: {}", e.getMessage(), mediaNodeConfig);
}
}
}

// public void closeAllStream() {
// try {
// String reqUrl = String.format("http://%s:%s/index/api/close_streams", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
//
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaNodeConfig.getSecret());
// params.put("force", 1);
//
// String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
// JSONObject object = JSON.parseObject(response);
// log.info("ZLM Close All Pull Stream Result {}", object);
//} catch (Exception e) {
// log.info("Call ZLM Close All Stream API Exception, ex: {}, media: {}", e.getMessage(), mediaNodeConfig);
//}
//}

// public void startRecord(String app, String stream, Integer secs) {
// try {
// String reqUrl = String.format("http://%s:%s/index/api/startRecord", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
//
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaNodeConfig.getSecret());
// params.put("app", app);
// params.put("stream", stream);
// params.put("type", 1);
// params.put("max_second", secs);
// params.put("vhost","__defaultVhost__");
//
// String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
// JSONObject object = JSON.parseObject(response);
// log.info("ZLM Start Record make Notification Result {}", object);
//} catch (Exception e) {
// log.info("Call ZLM Start Record make API Exception, ex: {}, media: {}", e.getMessage(), mediaNodeConfig);
//}
//}

// public void stopRecord(String app, String stream) {
// try {
// String reqUrl = String.format("http://%s:%s/index/api/stopRecord", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
//
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaNodeConfig.getSecret());
// params.put("app", app);
// params.put("stream", stream);
// params.put("type", 1);
// params.put("vhost","__defaultVhost__");
//
// String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
// JSONObject object = JSON.parseObject(response);
// log.info("ZLM Close Record make Notification Result {}", object);
//} catch (Exception e) {
// log.info("Call ZLM Close Record make API Exception, ex: {}, media: {}", e.getMessage(), mediaNodeConfig);
//}
//}

/**
* Get Play Put Address
* @param app
* @param cameraId
* @return
*/
public String getWebrtcUrl(String app, Long cameraId) {
String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaNodeConfig.getSecret());
params.put("app", app);
params.put("stream", cameraId);
params.put("schema","rtsp");

try {
String response = HttpUtil.post(reqUrl, params, 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
JSONArray array = object.getJSONArray("data");
if(!array.isEmpty()) {
return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort(), app, cameraId);
}
} else {
log.error("Get Webrtc Stream Address Exception,url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.error("Get Webrtc Stream Address Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return null;
}

/**
* Get Play Put Address
* @param app
* @param cameraId
* @return
*/
// public String getWebrtcUrl(String app, Long cameraId, MediaServer mediaServer) {
// String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaServer.getIp(), mediaServer.getHttpPort());
//
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaServer.getSecret());
// params.put("app", app);
// params.put("stream", cameraId);
// params.put("schema","rtsp");
//
// try {
// String response = HttpUtil.post(reqUrl, params, 5000);
// JSONObject object = JSON.parseObject(response);
// if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
// JSONArray array = object.getJSONArray("data");
// if(!array.isEmpty()) {
// return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", mediaServer.getIp(), mediaServer.getHttpPort(), app, cameraId);
//}
//} else {
// log.error("Get Webrtc Stream Address Exception,url: {}, data: {}", reqUrl, params);
//}
//} catch (Exception e) {
// log.error("Get Webrtc Stream Address Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
//}
// return null;
//}

/**
* Get Play Put Address
* @param app
* @param camera
* @return
*/
public String getWebrtcUrl(String app, Camera camera) {
MediaServer mediaServer = mediaServerService.getById(camera.getMediaServerId());
if(mediaServer == null) {
log.error("Camera not has Corresponding zlm Node, not can Get Play Put Address");
return null;
}

String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaServer.getIp(), mediaServer.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());
params.put("app", app);
params.put("stream", camera.getId());
params.put("schema","rtsp");

try {
String response = HttpUtil.post(reqUrl, params, 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
JSONArray array = object.getJSONArray("data");
if(!array.isEmpty()) {
return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", mediaServer.getIp(), mediaServer.getHttpPort(), app, camera.getId());
}
} else {
log.error("Get Webrtc Stream Address Exception,url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.error("Get Webrtc Stream Address Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return null;
}

/**
* Get Play Put Address
* @param app
* @param camera
* @param mediaServer
* @param checkSameUrl For Local rtsp Pull Stream, Exist more change Stream Address Operation, Need Judge Fixed Pull Stream Address and Current Address Whether Consistent Problem
* @return
*/
public String getWebrtcUrl(String app, Camera camera, MediaServer mediaServer, boolean checkSameUrl) {
String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaServer.getIp(), mediaServer.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());
params.put("app", app);
params.put("stream", camera.getId());
params.put("schema","rtsp");

try {
String response = HttpUtil.post(reqUrl, params, 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
JSONArray array = object.getJSONArray("data");
if(!array.isEmpty()) {
if(checkSameUrl) {
JSONObject mediaRow = array.getJSONObject(0);
String originUrl = mediaRow.getString("originUrl");
String processRtspUrl = DecodeRtspUlr.processRtspUrl(camera.getRtspUrl());
if(!originUrl.equalsIgnoreCase(processRtspUrl)) {
closeStream(app, String.valueOf(camera.getId()));
} else {
return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", mediaServer.getIp(), mediaServer.getHttpPort(), app, camera.getId());
}
} else {
return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", mediaServer.getIp(), mediaServer.getHttpPort(), app, camera.getId());
}
}
} else {
log.error("Get Webrtc Stream Address Exception,url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.error("Get Webrtc Stream Address Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return null;
}

/**
* Get Play Put Address
* @param stream
* @param camera
* @return
*/
public String getGbWebrtcUrl(String stream, Camera camera, MediaServer mediaServer) {
String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaServer.getIp(), mediaServer.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());
params.put("app","rtp");
params.put("stream", stream);
params.put("schema","rtsp");

try {
String response = HttpUtil.post(reqUrl, params, 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
JSONArray array = object.getJSONArray("data");
log.info("Back data: {}", array.toJSONString());
if(!array.isEmpty()) {
return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", mediaServer.getIp(), mediaServer.getHttpPort(),"rtp", stream);
}
} else {
log.error("Get Webrtc Stream Address Exception,url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.error("Get Webrtc Stream Address Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return null;
}

/**
* Get Play Put Address
* @param app
* @param cameraId
* @return
*/
// public String getHlsUrl(String app, Long cameraId) {
// String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
//
// Map<String, Object> params = new HashMap<>();
// params.put("secret", mediaNodeConfig.getSecret());
// params.put("app", app);
// params.put("stream", cameraId);
// params.put("schema","hls");
//
// try {
// String response = HttpUtil.post(reqUrl, params, 5000);
// JSONObject object = JSON.parseObject(response);
// if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
// JSONArray array = object.getJSONArray("data");
// if(!array.isEmpty()) {
// return String.format("http://%s:%s/%s/%s/hls.m3u8", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort(), app, cameraId);
//}
//} else {
// log.error("Get HLS Stream Address Exception,url: {}, data: {}", reqUrl, params);
//}
//} catch (Exception e) {
// log.error("Get HLS Stream Address Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
//}
// return null;
//}

/**
* Get Play Put Address
* @param app
* @param camera
* @return
*/
public String getHlsUrl(String app, Camera camera, MediaServer mediaServer, boolean checkSameUrl) {
String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaServer.getIp(), mediaServer.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());
params.put("app", app);
params.put("stream", camera.getId());
params.put("schema","hls");

try {
String response = HttpUtil.post(reqUrl, params, 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
JSONArray array = object.getJSONArray("data");
if(!array.isEmpty()) {
if(checkSameUrl) {
JSONObject mediaRow = array.getJSONObject(0);
String originUrl = mediaRow.getString("originUrl");
String processRtspUrl = DecodeRtspUlr.processRtspUrl(camera.getRtspUrl());
if(!originUrl.equalsIgnoreCase(processRtspUrl)) {
closeStream(app, String.valueOf(camera.getId()));
} else {
return String.format("http://%s:%s/%s/%s/hls.m3u8", mediaServer.getIp(), mediaServer.getHttpPort(), app, camera.getId());
}
} else {
return String.format("http://%s:%s/%s/%s/hls.m3u8", mediaServer.getIp(), mediaServer.getHttpPort(), app, camera.getId());
}
}
} else {
log.error("Get HLS Stream Address Exception,url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.error("Get HLS Stream Address Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return null;
}

/**
* Get Play Put Address _rtsp Format
* @param app
* @param cameraId
* @return
*/
public String getRtspUrl(String app, Long cameraId) {
String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaNodeConfig.getSecret());
params.put("app", app);
params.put("stream", cameraId);
params.put("schema","rtsp");

try {
String response = HttpUtil.post(reqUrl, params, 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
JSONArray array = object.getJSONArray("data");
if(!array.isEmpty()) {
return String.format("rtsp://%s:%s/%s/%s", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getRtspPort(), app, cameraId);
}
} else {
log.error("Get Rtsp Stream Address Exception,url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.error("Get Rtsp Stream Address Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return null;
}

/**
* Query Rtsp Stream Whether Exist _rtsp Format
* @param app
* @param cameraId
* @return
*/
public boolean hasStream(String app, Long cameraId) {
String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaNodeConfig.getSecret());
params.put("app", app);
params.put("stream", cameraId);
params.put("schema","rtsp");

try {
String response = HttpUtil.post(reqUrl, params, 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
JSONArray array = object.getJSONArray("data");
if(!array.isEmpty()) {
return true;
}
} else {
log.error("Query Rtsp Stream Whether Exist Exception,url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.error("Query Rtsp Stream Whether Exist Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return false;
}

/**
* Query Rtsp Stream Whether Exist _rtsp Format
* @param app
* @param cameraId
* @return
*/
public boolean hasStream(String app, Long cameraId, MediaServer mediaServer) {
String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaServer.getIp(), mediaServer.getHttpPort());

Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());
params.put("app", app);
params.put("stream", cameraId);
params.put("schema","rtsp");

try {
String response = HttpUtil.post(reqUrl, params, 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("code") && object.getIntValue("code") == 0 && object.containsKey("data")) {
JSONArray array = object.getJSONArray("data");
if(!array.isEmpty()) {
return true;
}
} else {
log.error("Query Rtsp Stream Whether Exist Exception,url: {}, data: {}", reqUrl, params);
}
} catch (Exception e) {
log.error("Query Rtsp Stream Whether Exist Exception, ex: {}, url: {}, data: {}", e.getMessage(), reqUrl, params);
}
return false;
}

/**
* Verify Media Node Whether Normal
* @param mediaServer
* @return
*/
public boolean check(MediaServer mediaServer) {
String reqUrl = String.format("http://%s:%s/index/api/getMediaList", mediaServer.getIp(), mediaServer.getHttpPort());
try {
Map<String, Object> params = new HashMap<>();
params.put("secret", mediaServer.getSecret());

String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
JSONObject object = JSON.parseObject(response);
return object.containsKey("code") && object.getIntValue("code") == 0;
} catch (Exception e) {
return false;
}
}
}
