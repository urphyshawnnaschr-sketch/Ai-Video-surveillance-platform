package com.yihecode.camera.ai.media;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
* Function can: Get Play Put Address, Support zlmediakit and ablmediaserver
*
* @author zhoumingxing
* @date 2024/3/19
*/
@Slf4j
@Component
public class MediaService {

    @Autowired
    private ConfigService configService;

    @Value("${zlmediakit.http-ip}")
    private String zlmHttpIp;

    @Value("${zlmediakit.http-port}")
    private Integer zlmHttpPort;

    @Value("${zlmediakit.http-play-port}")
    private Integer zlmHttpPlayPort;

    @Value("${zlmediakit.rtsp-port}")
    private Integer zlmRtspPort;

    @Value("${zlmediakit.secret}")
    private String zlmSecret;

    /**
* Get Play Put Address
* @param cameraId
* @param rtspUrl
* @param playType 0- original start Stream,1- combine Complete Stream
* @return
*/
    public String getPlayUrl(Long cameraId, String rtspUrl, int playType) {
        if(playType == 0) { //original start Stream Play Put
// Notification Box Stop Frame
//httpAiBoxService.stop(cameraId);

//
String mediaTag ="live";
boolean queryOk = this.zlm_query_stream(mediaTag, cameraId);
if(queryOk) {
return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", this.getPlayIp(), this.getPlayPort(), mediaTag, cameraId);
}
// Pull Stream
boolean addOk = this.zlm_add_stream(cameraId, rtspUrl,"live");
if(addOk) {
try {
Thread.sleep(300);
} catch (Exception e) {}
//
return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", this.getPlayIp(), this.getPlayPort(), mediaTag, cameraId);
}
return null;
} else {// combine Complete Stream Play Put, Notification Box Point Play
String mediaTag ="livedraw";
log.info("combine Complete Stream Play Put, Notification Box Push Stream, cameraId: {}", cameraId);

// Notification Box Push Stream
String httpIp = configService.getByValTag("INNER_IP");
if(StrUtil.isNotBlank(zlmHttpIp)) {
httpIp = zlmHttpIp;
}

String rtspPushUrl = String.format("rtsp://%s:%s/%s/%s", httpIp, zlmRtspPort, mediaTag, cameraId);
//
return String.format("http://%s:%s/index/api/webrtc?app=%s&stream=%s&type=play", this.getPlayIp(), this.getPlayPort(), mediaTag, cameraId);
}
}

/**
* Get Play Put Address
* @param cameraId
* @param rtspUrl
* @param playType 0- original start Stream,1- combine Complete Stream
* @return
*/
public String getPlayUrl_FLV(Long cameraId, String rtspUrl, int playType) {
if(playType == 1) {// Play Put combine Complete Stream
boolean queryOk = this.zlm_query_stream("livedraw", cameraId);
if(queryOk) {
return String.format("http://%s:%s/livedraw/%s/live.flv", configService.getOutIp(), this.getPlayPort(), cameraId);
}
}

// Play Put original start Stream
boolean queryOk = this.zlm_query_stream("live", cameraId);
if(queryOk) {
return String.format("http://%s:%s/live/%s/live.flv", configService.getOutIp(), this.getPlayPort(), cameraId);
}

//
boolean addOk = this.zlm_add_stream(cameraId, rtspUrl,"live");
if(addOk) {
try {
Thread.sleep(300);
} catch (Exception e) {}
//
return String.format("http://%s:%s/live/%s/live.flv", configService.getOutIp(), this.getPlayPort(), cameraId);
}
return null;
}

/**
* Close original start Stream
* @param cameraId
*/
public void closeStream(Long cameraId, String mediaTag) {
zlm_close_stream(cameraId, mediaTag);
}

/**
* Splice connect Stream Media Server Access Address
* @return
*/
private String getMediaUrl() {
// Query Media Server Whether Exist Stream Media
String httpIp = configService.getByValTag("INNER_IP");
if(StrUtil.isNotBlank(zlmHttpIp)) {
httpIp = zlmHttpIp;
}

// Stream Media Server Address
return String.format("http://%s:%s", httpIp, zlmHttpPort);
}

/**
* Get Play Put ip
* @return
*/
private String getPlayIp() {
if(StrUtil.isNotBlank(zlmHttpIp)) {
return zlmHttpIp;
}
return configService.getOutIp();
}

/**
* Get Play Put Port
* @return
*/
private Integer getPlayPort() {
if(zlmHttpPlayPort!= null) {
return zlmHttpPlayPort;
}
return zlmHttpPort;
}

/**
* Query Stream
* @param mediaTag
* @param cameraId
* @return
*/
private boolean zlm_query_stream(String mediaTag, Long cameraId) {
String mediaUrl = this.getMediaUrl();
//
Map<String, Object> params = new HashMap();
params.put("secret", zlmSecret);
params.put("app", mediaTag);
params.put("stream", cameraId);
try {
String response = HttpUtil.post(String.format("%s/index/api/getMediaList", mediaUrl), params, 10000);
JSONObject root = JSON.parseObject(response);
if(root.getIntValue("code") == 0) {
log.info("zlm Query Stream Info _ Back json", response);
JSONArray data = root.getJSONArray("data");
if(data!= null && data.size() > 0) {// Exist Stream Info
return true;
}
}
log.error("zlm Query Stream Info _ Back Error, mediaUrl: {}, params: {}, response: {}", mediaUrl, params, response);
} catch (Exception e) {
log.error("zlm Query Stream Info _ Exception, mediaUrl: {}, params: {}, ex: {}", mediaUrl, params, e);
}
return false;
}

/**
* instead Reason Pull Stream
* @param cameraId
* @param rtspUrl
* @return
*/
public boolean zlm_add_stream(Long cameraId, String rtspUrl, String mediaTag) {
String mediaUrl = this.getMediaUrl();
//
Map<String, Object> params = new HashMap();
params.put("secret", zlmSecret);
params.put("vhost","__defaultVhost__");
params.put("app", mediaTag);
params.put("stream", cameraId);
params.put("url", rtspUrl);
params.put("retry_count", 3);
// params.put("enable_hls", false);
// params.put("enable_hls_fmp4", false);
// params.put("enable_mp4", false);
// params.put("enable_ts", false);
// params.put("enable_fmp4", false);
// params.put("hls_demand", false);
// params.put("ts_demand", false);
// params.put("fmp4_demand", false);
// params.put("enable_audio", false);
// params.put("add_mute_audio", false);

try {
String response = HttpUtil.post(String.format("%s/index/api/addStreamProxy", mediaUrl), params, 10000);
JSONObject root = JSON.parseObject(response);
if(root.getIntValue("code") == 0) {
return true;
}
log.error("zlm instead Reason Pull Stream _ Back Error, mediaUrl: {}, params: {}, response: {}", mediaUrl, params, response);
} catch (Exception e) {
log.error("zlm instead Reason Pull Stream _ Back Error, mediaUrl: {}, params: {}, ex: {}", mediaUrl, params, e);
}
return false;
}

/**
* Close Stream
* @param cameraId
*/
private void zlm_close_stream(Long cameraId, String mediaTag) {
String mediaUrl = this.getMediaUrl();
//
Map<String, Object> params = new HashMap();
params.put("secret", zlmSecret);
params.put("app", mediaTag);
params.put("stream", cameraId);
params.put("force", 1);
try {
String response = HttpUtil.post(String.format("%s/index/api/close_streams", mediaUrl), params, 10000);
log.error("zlm Close Refer Fixed Stream _ Back json, mediaUrl: {}, params: {}, response: {}", mediaUrl, params, response);
//JSONObject root = JSON.parseObject(response);
} catch (Exception e) {
log.error("zlm Close Refer Fixed Stream _ Exception: {}", e);
}
}

/**
* Get Play Put Address
* Format: rtsp://192.168.0.121:554/app/Camera01
*
* @param cameraId
* @param rtspUrl
* @param playType 0- original start Stream,1- combine Complete Stream
* @return
*/
public String getPlayUrl_Rtsp(Long cameraId, String rtspUrl, int playType) {
if(playType == 1) {// Play Put combine Complete Stream
boolean queryResult = zlm_query_stream("livedraw", cameraId);
if(queryResult) {
return String.format("rtsp://%s:%s/%s/%s", zlmHttpIp, zlmRtspPort,"livedraw", cameraId);
}
}

// Play Put original start Stream
boolean queryResult = zlm_query_stream("live", cameraId);
if(queryResult) {
return String.format("rtsp://%s:%s/%s/%s", zlmHttpIp, zlmRtspPort,"live", cameraId);
}

boolean addResult = zlm_add_stream(cameraId, rtspUrl,"live");
if(addResult) {
return String.format("rtsp://%s:%s/%s/%s", zlmHttpIp, zlmRtspPort,"live", cameraId);
}
return null;
}
}
