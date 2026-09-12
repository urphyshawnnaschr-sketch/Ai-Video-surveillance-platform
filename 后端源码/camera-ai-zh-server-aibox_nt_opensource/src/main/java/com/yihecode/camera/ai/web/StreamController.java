package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.dto.StreamUrlDTO;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.wvp.GBDeviceChannel;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.javacv.DecodeRtspUlr;
import com.yihecode.camera.ai.media.MediaRestfulService;
import com.yihecode.camera.ai.media.MediaService;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.MessageType;
import com.yihecode.camera.ai.netty.data.Response;
import com.yihecode.camera.ai.netty.data.StreamPusherRequest;
import com.yihecode.camera.ai.netty.data.StreamPusherResponse;
import com.yihecode.camera.ai.service.*;

import java.util.*;
import java.util.stream.Collectors;

import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.service.wvp.GBDeviceChannelService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.vo.PlayTypeVo;
import com.yihecode.camera.ai.web.vo.StreamPlayingVo;
import com.yihecode.camera.ai.web.vo.StreamReportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
* Video Stream Play Put Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Video Stream Play Put Management")
@Slf4j
@SaCheckLogin
@Controller
@RequestMapping({"/stream"})
public class StreamController {

    //
@Autowired
private CameraService cameraService;

//
@Autowired
private ConfigService configService;

//
@Autowired
private AlgorithmService algorithmService;

//
@Autowired
private ReportService reportService;

//
@Autowired
private LocationService locationService;

//
@Autowired
private MediaService mediaService;

//
@Autowired
private MediaServerService mediaServerService;

//
@Autowired
private MediaRestfulService mediaRestfulService;

//
@Autowired
private AccountService accountService;

//
@Autowired
private ApDepartService apDepartService;

//
@Autowired
private MessageSenderAndWaiter messageSenderAndWaiter;

@Autowired
private GBDeviceChannelService gbDeviceChannelService;

//
@Autowired
private ProjectConfig projectConfig;

/**
* Save Count Config
* @param ids
* @return
*/
@ApiOperation("Save Count Config")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/config/save"})
@ResponseBody
public JsonResult saveConfig(@RequestBody List<Long> ids) {
if(ids == null) {
return JsonResultUtils.fail("Please select in Need Config Type");
}
//
if(ids.size() > 8) {
return JsonResultUtils.fail("most multi Support Check select 8");
}
// Update Count ID
algorithmService.updateStaticsFlag(ids);

return JsonResultUtils.success();
}

/**
* Count Config - Query Algorithm List
* @return
*/
@ApiOperation("Count Config - Query Algorithm List")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/form/algorithms"})
@ResponseBody
public JsonResult<List<Algorithm>> formAlgorithms() {
List<Algorithm> algorithmList = algorithmService.listUsed();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}

//
for(Algorithm algorithm: algorithmList) {
if(algorithm.getStaticsFlag()!= null && algorithm.getStaticsFlag() == 1) {
algorithm.setStaticsFlagVal("checked");
} else {
algorithm.setStaticsFlagVal("");
}
}
return JsonResultUtils.success(algorithmList);
}

/**
* Query 3 Real-time Alert Data
* @return
*/
@ApiOperation("Video Stream Management - Query Real-time Alert Data List")
@SaCheckPermission(value = {"edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping({"/report/alarms"})
@ResponseBody
public JsonResult<List<StreamReportVo>> listAlarms(@RequestHeader("Lang") String language) {
//
List<Camera> cameras = cameraService.listData();
Map<Long, Camera> cameraMap = cameras.stream().collect(Collectors.toMap(Camera::getId, (s1 -> s1)));
//
Map<Long, String> algorithmNames = new HashMap<>();
Map<Long, String> algorithmEnglishNames = new HashMap<>();
List<Algorithm> algorithmList = this.algorithmService.list();
if (algorithmList!= null) {
for (Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());
algorithmEnglishNames.put(algorithm.getId(), algorithm.getEnglishName());
}
}
//
List<StreamReportVo> dataList = new ArrayList<>();
// Default Query when Day Recent 3 Alert
List<Report> reportList = reportService.listNewly(3);
if(reportList!= null) {
for(Report report: reportList) {
//
String cameraName ="";
String locationName ="";
Camera camera = cameraMap.get(report.getCameraId());
if(camera!= null) {
cameraName = camera.getName();
Location location = locationService.getById(camera.getLocationId());
if(location!= null) {
locationName = location.getName();
}
}

StreamReportVo streamReportVo = new StreamReportVo();
streamReportVo.setId(report.getId());
streamReportVo.setParams(report.getParams());
streamReportVo.setCameraName(cameraName);
streamReportVo.setAlgorithmName(algorithmNames.get(report.getAlgorithmId()));
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
streamReportVo.setAlgorithmName(algorithmEnglishNames.get(report.getAlgorithmId()));
}
streamReportVo.setWareName(locationName);
streamReportVo.setAlarmTime((report.getCreatedAt() == null)?"": DateUtil.format(report.getCreatedAt(),"yyyy/MM/dd HH:mm:ss"));
dataList.add(streamReportVo);
}
}
return JsonResultUtils.success(dataList);
}

/**
* Count Algorithm
* @return
*/
@ApiOperation("Algorithm Count Calculate Result")
@SaCheckPermission("edgePlatform-videoPreview")
@PostMapping({"/statics/algorithms"})
@ResponseBody
public JsonResult<List<Algorithm>> staticsAlgorithms() {
List<Algorithm> algorithmList = algorithmService.listUsed();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}

// Start and End ms Value
long startMills = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
long endMills = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();

//
List<Algorithm> showAlgorithmList = new ArrayList<>();
for(Algorithm algorithm: algorithmList) {
if(algorithm.getStaticsFlag()!= null && algorithm.getStaticsFlag() == 1) {
Integer counter = reportService.getAlgorithmCounter(algorithm.getId(), startMills, endMills);
algorithm.setStaticsFlagVal(""+ counter);
showAlgorithmList.add(algorithm);
}//TODO Person Stream Quantity Tracking Count Data
}
return JsonResultUtils.success(showAlgorithmList);
}

@ApiOperation("Get Edge Box Play Put Address")
@ApiImplicitParams({
@ApiImplicitParam(name ="cameraId", value ="Camera id"),
@ApiImplicitParam(name ="playType", value ="Play Put Type, 0- original start Stream, 1- Real-time Stream, Item front Fixed Fixed for 1"),
@ApiImplicitParam(name ="rtspUrlType", value ="respUrl Type, 1- No One Stream Address, 2- No Two Stream Address")
})
@SaCheckPermission(value = {"edgePlatform-videoPreview","alarmData"}, mode = SaMode.OR)
@PostMapping("/getBoxPlayUrl")
@ResponseBody
public JsonResult<?> getBoxPlayUrl(Long cameraId, Integer playType, Integer rtspUrlType) {
Camera camera = cameraService.getById(cameraId);
if(camera == null) {
return JsonResultUtils.success(StreamUrlDTO.h264(300,"Camera does not exist",""));
}

if(ObjectUtil.equals(rtspUrlType, 1) && StrUtil.isBlank(camera.getRtspUrl())) {
return JsonResultUtils.success(StreamUrlDTO.h264(301,"Camera not has Config Stream Address",""));
}

if(ObjectUtil.equals(rtspUrlType, 2) && StrUtil.isBlank(camera.getRtspUrl2())) {
return JsonResultUtils.success(StreamUrlDTO.h264(301,"Camera not has Config Stream Address 2",""));
}

// Query Box
Location location = locationService.getById(camera.getLocationId());
if(location == null) {
return JsonResultUtils.success(StreamUrlDTO.h264(302,"Camera not Relate Box Device",""));
}

// GB Standard Stream Special Special Process
if(camera.getSourceType()!= null && camera.getSourceType() == 1) {
MediaServer mediaServer = mediaServerService.getById(camera.getMediaServerId());
if(mediaServer == null) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"Media Node does not exist",""));
}

// GB Standard Channel Info
GBDeviceChannel gbDeviceChannel = gbDeviceChannelService.getByAccessCameraId(cameraId);
if(gbDeviceChannel == null) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"GB Standard Channel does not exist",""));
}

// Stream ID
String stream = gbDeviceChannel.getDeviceId() +"_"+ gbDeviceChannel.getChannelId();

// H265 Process Stream Process, Need Pass ffmpeg turn for hls
if("H265".equalsIgnoreCase(camera.getVideoCodec())) {
String hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, mediaServer, false);
if(hlsUrl == null) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"Get Video Stream Failed",""));
}
return JsonResultUtils.success(StreamUrlDTO.h264(200,"OK", hlsUrl));
}

// H264 Process Stream Process
String webrtcUrl = mediaRestfulService.getGbWebrtcUrl(stream, camera, mediaServer);
if(webrtcUrl == null) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"Get Video Stream Failed",""));
}
return JsonResultUtils.success(StreamUrlDTO.h264(200,"OK", webrtcUrl));
}

// like Result Video Code Format not Set, Request Play Put original start Stream Hour Wait, all by Algorithm Frame Push Stream Incoming Play Put
// H265 send current turn code Push Stream after,webrtc no Method Play Put, The with modify Use turn code after Collect Use hls in Line Play Put
if("H265".equals(camera.getVideoCodec())) {
// H265 Cross net Push Stream
if(projectConfig.isCrossNet()) {
return handleCrossNet265(camera, location, rtspUrlType);
}

// H265, Bureau Domain net Video Stream
return handleLocalNet265(camera, rtspUrlType);
}

// H264, Cross net Push Stream
if(projectConfig.isCrossNet()) {
return handleCrossNet264(camera, location, rtspUrlType);
}

// H264, Bureau Domain net Video Stream
return handleLocalNet264(camera, rtspUrlType);
}

/**
* Process Bureau Domain net inner 264 Video Stream
*
* @param camera Camera
* @param rtspUrlType
* @return
*/
private JsonResult<?> handleLocalNet264(Camera camera, Integer rtspUrlType) {
String app ="live";

// inner net Stream Process Stream Process
List<MediaServer> mediaServerList = mediaServerService.list();
if(ObjectUtil.isEmpty(mediaServerList)) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"not has Play Put Node",""));
}

// Round Query Stream Whether already exists
for(MediaServer mediaServer: mediaServerList) {
String webrtcUrl = mediaRestfulService.getWebrtcUrl(app, camera, mediaServer, true);
if(StrUtil.isNotBlank(webrtcUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h264(200,"OK", webrtcUrl));
}
}

// Random One Play Put Node
int randIndex = new Random().nextInt(mediaServerList.size());
MediaServer mediaServer = mediaServerList.get(randIndex);

// inner net Pull Stream
boolean addOk = mediaRestfulService.addStream(camera, mediaServer);
if (!addOk) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"Get Video Stream Failed",""));
}

// Wait 3 s
try {
Thread.sleep(3000);
} catch (InterruptedException e) {
//
}

// again sub Get Stream Address
String webrtcUrl = mediaRestfulService.getWebrtcUrl(app, camera, mediaServer, true);
if(StrUtil.isNotBlank(webrtcUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h264(200,"OK", webrtcUrl));
}
return JsonResultUtils.success(StreamUrlDTO.h264(303,"Get Video Stream Failed",""));
}

/**
* Process Bureau Domain net inner 265 Video Stream
*
* @param camera
* @param rtspUrlType
* @return
*/
private JsonResult<?> handleLocalNet265(Camera camera, Integer rtspUrlType) {
// inner net Stream Process Stream Process
List<MediaServer> mediaServerList = mediaServerService.list();
if(ObjectUtil.isEmpty(mediaServerList)) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"not has Play Put Node",""));
}

// Round Query Stream Whether already exists
for(MediaServer mediaServer: mediaServerList) {
String hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, mediaServer, true);
if(StrUtil.isNotBlank(hlsUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h265(200,"OK", hlsUrl));
}
}

int randIndex = new Random().nextInt(mediaServerList.size());
MediaServer mediaServer = mediaServerList.get(randIndex);
String rtspUrl = camera.getRtspUrl();
// inner net Pull Stream
boolean addOk = mediaRestfulService.addStreamFFmpeg(camera.getId(), DecodeRtspUlr.processRtspUrl(rtspUrl), mediaServer);
if (!addOk) {
return JsonResultUtils.success(StreamUrlDTO.h265(303,"Get Video Stream Failed",""));
}

// hls Generate Compare slow
for(int i = 0; i < 5; i++) {
// Wait 3 s
try {
Thread.sleep(3000);
} catch (InterruptedException e) {
// throw new RuntimeException(e);
}

// again sub Get Stream Address
String hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, mediaServer, true);
if(StrUtil.isNotBlank(hlsUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h265(200,"OK", hlsUrl));
}
}

return JsonResultUtils.success(StreamUrlDTO.h265(303,"Get Video Stream Failed",""));
}

/**
* Process outer net net inner 264 Video Stream
*
* @param camera
* @param rtspUrlType
* @return
*/
private JsonResult<?> handleCrossNet264(Camera camera, Location location, Integer rtspUrlType) {
// outer net Stream Process Stream Process
List<MediaServer> mediaServerList = mediaServerService.list();
if(ObjectUtil.isEmpty(mediaServerList)) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"not has Play Put Node",""));
}

// Round Query Stream Whether already exists
for(MediaServer mediaServer: mediaServerList) {
String webrtcUrl = mediaRestfulService.getWebrtcUrl("cloud", camera, mediaServer, false);
if(StrUtil.isNotBlank(webrtcUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h264(200,"OK", webrtcUrl));
}
}

// not has Get to Video Address, Pull Stream
StreamPusherRequest request = new StreamPusherRequest();
request.setType(MessageType.STREAM_PUSHER.getType());
request.setSn(location.getBoxNo());
request.setRequestId(IdUtil.randomUUID());
request.setCloudStreamPort(projectConfig.getCloudStreamPort());
request.setCameraId(camera.getId());
request.setRtspUrl(DecodeRtspUlr.processRtspUrl(camera.getRtspUrl()));
Response response = messageSenderAndWaiter.sendRequest(request);
if(response == null) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"Box Device Pull Stream Failed, Unknown Error",""));
}

StreamPusherResponse streamPusherResponse = (StreamPusherResponse) response;
if(!streamPusherResponse.isStatus()) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"far Process Push Stream Failed:"+ streamPusherResponse.getMsg(),""));
}

// Wait 5 s
try {
Thread.sleep(5000);
} catch (Exception e) {
//
}

// Round Query Stream Whether already exists
for(MediaServer mediaServer: mediaServerList) {
String webrtcUrl = mediaRestfulService.getWebrtcUrl("cloud", camera, mediaServer, false);
if(StrUtil.isNotBlank(webrtcUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h264(200,"OK", webrtcUrl));
}
}

return JsonResultUtils.success(StreamUrlDTO.h264(303,"Get Video Stream Failed",""));
}

/**
* Process outer net H265 Video Push Stream
*
* @param camera
* @param location
* @param rtspUrlType
* @return
*/
private JsonResult<?> handleCrossNet265(Camera camera, Location location, Integer rtspUrlType) {
// outer net Stream Process Stream Process
List<MediaServer> mediaServerList = mediaServerService.list();
if(ObjectUtil.isEmpty(mediaServerList)) {
return JsonResultUtils.success(StreamUrlDTO.h264(303,"not has Play Put Node",""));
}

// Round Query Stream Whether already exists
MediaServer currentMediaServer = null;
for(MediaServer mediaServer: mediaServerList) {
// Box Whether via Push Stream to Server
boolean hasStream = mediaRestfulService.hasStream("cloud", camera.getId());
if(hasStream) {
currentMediaServer = mediaServer;
break;
}
}

// Stream already exists, Back Play Put Address
if(currentMediaServer!= null) {
String hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, currentMediaServer, false);
if(StrUtil.isNotBlank(hlsUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h265(200,"OK", hlsUrl));
}

// not has turn code, rule in Line turn code
boolean addOk = mediaRestfulService.addStreamFFmpeg(camera.getId(),"rtsp://127.0.0.1/cloud/"+ camera.getId(), currentMediaServer);
if (!addOk) {
return JsonResultUtils.success(StreamUrlDTO.h265(304,"Video turn code Failed",""));
}

// Wait 5 s
try {
Thread.sleep(3000);
} catch (Exception e) {
//
}

// Again Get Address
hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, currentMediaServer, false);
if(StrUtil.isNotBlank(hlsUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h265(200,"OK", hlsUrl));
}
return JsonResultUtils.success(StreamUrlDTO.h265(303,"Get Video Stream Failed",""));
}

// Box not has Push Stream to Server, Notification Box Pull Stream
StreamPusherRequest request = new StreamPusherRequest();
request.setType(MessageType.STREAM_PUSHER.getType());
request.setSn(location.getBoxNo());
request.setRequestId(IdUtil.randomUUID());
request.setCloudStreamPort(projectConfig.getCloudStreamPort());
request.setCameraId(camera.getId());
request.setRtspUrl(DecodeRtspUlr.processRtspUrl(camera.getRtspUrl()));
Response response = messageSenderAndWaiter.sendRequest(request);
if(response == null) {
return JsonResultUtils.success(StreamUrlDTO.h265(303,"Box Device Push Stream Failed, Unknown Error",""));
}

// Box Back Message
StreamPusherResponse streamPusherResponse = (StreamPusherResponse) response;
if(!streamPusherResponse.isStatus()) {
return JsonResultUtils.success(StreamUrlDTO.h265(303, streamPusherResponse.getMsg(),""));
}

// Wait 5 s
try {
Thread.sleep(5000);
} catch (Exception e) {
//
}

// select Fixed Stream Media, Here Exist BUG
MediaServer mediaServer = mediaServerList.get(0);

// Confirm Stream via Push to Server
boolean checkStream = mediaRestfulService.hasStream("cloud", camera.getId(), mediaServer);
if(!checkStream) {
return JsonResultUtils.success(StreamUrlDTO.h265(303,"Box Device Push Stream Failed",""));
}

// Again Get Play Put Stream
String hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, mediaServer, false);
if(StrUtil.isNotBlank(hlsUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h265(200,"OK", hlsUrl));
}

// not has turn code, rule in Line turn code
boolean addOk = mediaRestfulService.addStreamFFmpeg(camera.getId(),"rtsp://127.0.0.1/cloud/"+ camera.getId(), mediaServer);
if (!addOk) {
return JsonResultUtils.success(StreamUrlDTO.h265(304,"Video turn code Failed",""));
}

// hls Generate Compare slow
for(int i = 0; i < 5; i++) {
// Wait 3 s
try {
Thread.sleep(3000);
} catch (InterruptedException e) {
// throw new RuntimeException(e);
}

// again sub Get Stream Address
hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, mediaServer, false);
if(StrUtil.isNotBlank(hlsUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h265(200,"OK", hlsUrl));
}
}
return JsonResultUtils.success(StreamUrlDTO.h265(303,"Get Video Stream Failed",""));
}

/**
* Process GB Standard 265 Video Stream
*
* @param camera Camera
* @param stream GB Standard Stream ID,deviceId_channelId
* @param mediaServer Refer Fixed Stream Media
* @return
*/
private JsonResult<?> handleGb265(Camera camera, String stream, MediaServer mediaServer) {
String hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, mediaServer, false);
if(StrUtil.isNotBlank(hlsUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h265(200,"OK", hlsUrl));
}

// inner net Pull Stream
boolean addOk = mediaRestfulService.addStreamFFmpeg(camera.getId(), DecodeRtspUlr.processRtspUrl(camera.getRtspUrl()), mediaServer);
if (!addOk) {
return JsonResultUtils.success(StreamUrlDTO.h265(303,"Get Video Stream Failed",""));
}

// hls Generate Compare slow
for(int i = 0; i < 5; i++) {
// Wait 3 s
try {
Thread.sleep(3000);
} catch (InterruptedException e) {
// throw new RuntimeException(e);
}

// again sub Get Stream Address
hlsUrl = mediaRestfulService.getHlsUrl("h265", camera, mediaServer, false);
if(StrUtil.isNotBlank(hlsUrl)) {
return JsonResultUtils.success(StreamUrlDTO.h265(200,"OK", hlsUrl));
}
}

return JsonResultUtils.success(StreamUrlDTO.h265(303,"Get Video Stream Failed",""));
}

/**
* Get when Day Alert Count
* @return
*/
@ApiOperation("Get when Day Alert Count")
@SaCheckPermission(value = {"edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping("/report/counter")
@ResponseBody
public JsonResult<Integer> getReportCounter() {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return JsonResultUtils.success(0);
}

// Non Management member
List<Long> departIds = new ArrayList<>();
if(account.getIsSuper() == null || account.getIsSuper()!= 1) {
List<Long> currentAndChildIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
if(ObjectUtil.isEmpty(currentAndChildIds)) {
return JsonResultUtils.success(0);
}
departIds.addAll(currentAndChildIds);
}

// Start and End ms Value
long startMills = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
long endMills = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();
int counter = reportService.getCounter(startMills, endMills, departIds);
return JsonResultUtils.success(counter);
}

@ApiOperation("Query Current Play Put Type")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/getPlayType"})
@ResponseBody
public JsonResult<Integer> getPlayType() {
String streamType = configService.getByValTag("streamType"); // Push Stream Type
if(StrUtil.isBlank(streamType) ||"rtsp".equals(streamType)) {
return JsonResultUtils.success(0);
}
// algo Algorithm Push Stream
return JsonResultUtils.success(1);
}

@ApiOperation("Query Current Play Put Type")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/setPlayType"})
@ResponseBody
public JsonResult<Void> setPlayType(@RequestBody PlayTypeVo playTypeVo) {
String streamType = configService.getByValTag("streamType");
if(((StrUtil.isBlank(streamType) ||"rtsp".equals(streamType))) && playTypeVo.getPlayType() == 0) {// no Need change more
return JsonResultUtils.success();
}
if("algo".equals(streamType) && playTypeVo.getPlayType() == 1) {// no Need change more
return JsonResultUtils.success();
}
// Update Play Put Type
configService.saveData("Play Put Type","streamType", playTypeVo.getPlayType() == 0?"rtsp":"algo");
configService.evictByTag("streamType");

// Notification Box Stop Play Put
// if("algo".equals(streamType)) {
// List<VideoPlay> videoPlays = videoPlayService.list();
// if(videoPlays!= null) {
// for(VideoPlay videoPlay: videoPlays) {
// httpAiBoxService.stop(videoPlay.getCameraId());
//}
//}
//}
// videoPlayService.removeAll();
return JsonResultUtils.success();
}

@ApiOperation("Get Play Put Address, Fit allocate super Star Video Play Put")
@ApiImplicitParams({
@ApiImplicitParam(name ="boxId", value ="Box ID"),
})
@SaCheckPermission("XXXXXXX")
@PostMapping("/getPlayUrlByBox")
@ResponseBody
public JsonResult<String> getPlayUrlByBox(Long boxId) {
Location location = locationService.getById(boxId);
if(location == null) {
return JsonResultUtils.fail("no Method Play Put, Box Info does not exist");
}
if(StrUtil.isBlank(location.getIpAddr())) {
return JsonResultUtils.fail("no Method Play Put, Box ip Address does not exist");
}
return JsonResultUtils.success(String.format("http://%s:8889/mix_video/", location.getIpAddr()));
}

@ApiOperation("Refer Fixed Play Put Camera, Fit allocate super Star Video Play Put")
@ApiImplicitParams({
@ApiImplicitParam(name ="cameraIds", value ="Camera Ids"),
@ApiImplicitParam(name ="boxId", value ="Box ID"),
})
@SaCheckPermission("XXXXXXX")
@PostMapping("/changePlays")
@ResponseBody
public JsonResult<?> changePlays(Long boxId, String cameraIds) {
Location location = locationService.getById(boxId);
if(location == null) {
return JsonResultUtils.fail("no Method Cut change Play Put, Box Info does not exist");
}
if(StrUtil.isBlank(location.getIpAddr())) {
return JsonResultUtils.fail("no Method Cut change Play Put, Box ip Address does not exist");
}
//
if(StrUtil.isBlank(cameraIds)) {
return JsonResultUtils.fail("no Method Cut change Play Put, not has Camera Config");
}
//
try {
String[] cameraIdArr = cameraIds.split(",");
List<String> cameraIdList = Arrays.asList(cameraIdArr);
Map<String, Object> jsonData = new HashMap<>();
jsonData.put("camera_list", cameraIdList);
String response = HttpUtil.post(String.format("http://%s:36896/task/change/plays", location.getIpAddr()), JSON.toJSONString(jsonData), 3000);
JSONObject resultJson = JSON.parseObject(response);
if (resultJson.getIntValue("code") == 200) {
return JsonResultUtils.success("Cut change Success, In Progress Again Load, Please Later etc");
} else {
return JsonResultUtils.fail("Cut change Failed,"+ resultJson.getString("msg"));
}
} catch (Exception e) {
return JsonResultUtils.fail("no Method Cut change Play Put, via Letter Exception");
}
}
}