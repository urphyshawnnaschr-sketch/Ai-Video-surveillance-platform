package com.yihecode.camera.ai.web.api.aibox;


import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.enums.CameraRunningState;
import com.yihecode.camera.ai.enums.CommState;
import com.yihecode.camera.ai.enums.SocialHookType;
import com.yihecode.camera.ai.enums.SocialResultBusinessType;
import com.yihecode.camera.ai.media.MediaService;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.Md5FileUtils;
import com.yihecode.camera.ai.web.api.aibox.vo.*;
import com.yihecode.camera.ai.web.api.comm.AlarmFeishuPushV2Service;
import com.yihecode.camera.ai.web.api.comm.AlarmFeishuPushV3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
* Edge Box Get Camera Resource
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@SaIgnore
@ApiIgnore
@Api(tags = "Edge Box Camera List Management")
@Slf4j
@RestController
@RequestMapping({"/api/aibox/camera"})
public class AiBoxCameraController {

    //
@Resource
private CameraService cameraService;

//
@Resource
private LocationService locationService;

//
@Resource
private AlgorithmService algorithmService;

//
@Resource
private CameraAlgorithmService cameraAlgorithmService;

//
@Resource
private ConfigService configService;

//
@Resource
private MediaService mediaService;

//
@Value("${dataModelsDir}")
public String MODEL_DIR;

@Value("${zlmediakit.http-ip}")
private String zlmHttpIp;

@Value("${zlmediakit.rtsp-port}")
private Integer zlmRtspPort;
@Autowired
private SocialHookService socialHookService;
@Autowired
private SocialResultService socialResultService;
@Autowired
private AlarmFeishuPushV2Service alarmFeishuPushService;
private Map<Long, BigDecimal> map = new HashMap<>();

/**
* Get Camera Resource
* @return
*/
@ApiOperation("Edge Box Pull Get Camera Resource List")
@PostMapping(value = {"","/"})
public JsonResult<List<Map<String, Object>>> listData(@RequestBody CameraPullVo cameraPullVo) {
try {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(cameraPullVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}
// Query Box Info
Location location = locationService.getBoxSnForRemote(cameraPullVo.getSn());
if(location == null) {
return JsonResultUtils.fail("sn find not to Box Info");
}
// By Box ID Query Camera List
List<Camera> cameraList = this.cameraService.listByBoxId(location.getId());
if (cameraList == null) {
return JsonResultUtils.success(new ArrayList<>());
}
//
List<Algorithm> algorithmList = this.algorithmService.list();
if (algorithmList == null) {
return JsonResultUtils.success(new ArrayList<>());
}
//
Map<Long, Algorithm> algorithmMap = new HashMap<>();
for (Algorithm algorithm: algorithmList) {
algorithmMap.put(algorithm.getId(), algorithm);
}

// Algorithm File List
// List<AlgorithmFile> algorithmFiles = algorithmFileService.list();
// if(algorithmFiles == null) {
// algorithmFiles = new ArrayList<>();
//}
// Map<String, String> algorithmFileMap = algorithmFiles.stream().collect(Collectors.toMap(AlgorithmFile::getNameEn, AlgorithmFile::getFileName, (v1, v2) -> v1));
//
// String streamType = configService.getByValTag("streamType"); // Push Stream Mode
// String pushPort = configService.getByValTag("pushPort"); // Push Stream Port
// String pushIp = configService.getByValTag("pushIp"); // Push Stream IP Address
// String mediaServer = configService.getByValTag("mediaServer"); // Stream Media Type zlm/abl
// if(StrUtil.isBlank(pushIp)) {
// pushIp ="127.0.0.1";
//}

// Query Media Server Whether Exist Stream Media
String pushIp = configService.getByValTag("INNER_IP");
if(StrUtil.isNotBlank(zlmHttpIp)) {
pushIp = zlmHttpIp;
}

//
List<Map<String, Object>> dataList = new ArrayList<>();
for (Camera camera: cameraList) {
// Status not Normal Camera
if(camera.getState() == null || camera.getState()!= 0) {
continue;
}
// not has Start Camera
if(camera.getRunning() == null || camera.getRunning()!= 1) {
continue;
}
// not is Box Camera
if(camera.getLocationType() == null ||!"2".equals(camera.getLocationType())) {
continue;
}
//
Map<String, Object> cameraMap = new HashMap<>();
cameraMap.put("camera_id", String.valueOf(camera.getId()));
cameraMap.put("camera_name", camera.getName());
cameraMap.put("rtsp_url", camera.getRtspUrl());
cameraMap.put("state", camera.getState());
cameraMap.put("interval_time", camera.getIntervalTime());
cameraMap.put("alarm_interval", camera.getAlarmInterval() == null? -1.0: camera.getAlarmInterval()); // Negative Value table show Push All Alert
cameraMap.put("action_counter", camera.getActionCounter());
// Push Stream Address
//if(StrUtil.isNotBlank(streamType) &&"algo".equals(streamType)) {
// cameraMap.put("rtmp_url","rtmp://"+ pushIp +":"+ pushPort +"/Media/"+ camera.getId());
// cameraMap.put("rtsp_push_url","rtsp://"+ pushIp +":"+ pushPort +"/Media/"+ camera.getId());
//} else {
// cameraMap.put("rtmp_url","");
// cameraMap.put("rtsp_push_url","");
//}
cameraMap.put("rtmp_url","rtmp://"+ pushIp +":"+ zlmRtspPort +"/livedraw/"+ camera.getId());
cameraMap.put("rtsp_push_url","rtsp://"+ pushIp +":"+ zlmRtspPort +"/livedraw/"+ camera.getId());
// Camera for Valid Status
// when running Status for Stop Status Hour, will will action Set for 2, But is Camera state still is 0, The with Make down Special Special Process, just not Need Algorithm Make Modify
if(camera.getState() == 0) {
// Capture Image Stop
if(camera.getRunning() == null || camera.getRunning() == CameraRunningState.CLOSED.getType()) {
cameraMap.put("state", CommState.DISABLED.getType());
}
}
//
List<CameraAlgorithm> cameraAlgorithmList = this.cameraAlgorithmService.listByCamera(camera.getId());
List<Map<String, Object>> algorithms = new ArrayList<>();
if (cameraAlgorithmList!= null) {
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
if(algorithmMap.containsKey(cameraAlgorithm.getAlgorithmId())) {
Map<String, Object> map = new HashMap<>();
String platform = algorithmMap.get(cameraAlgorithm.getAlgorithmId()).getPlatform();
String nameEn = algorithmMap.get(cameraAlgorithm.getAlgorithmId()).getNameEn();
String version = cameraAlgorithm.getAlgorithmVersion();
if (StringUtils.isBlank(version)) {
// like Result version Is Empty, Get Box down Other Camera Algorithm version
List<CameraAlgorithm> caList = cameraAlgorithmService.listByAlgorithmAndBoxId(cameraAlgorithm.getAlgorithmId(), location.getId());
for (CameraAlgorithm ca: caList) {
if (StringUtils.isNotBlank(ca.getAlgorithmVersion())) {
version = ca.getAlgorithmVersion();
break;
}
}
// like Result according Is Empty, Get Local File most new version
if (StringUtils.isBlank(version)) {
String path = MODEL_DIR + platform +"/"+ nameEn;
List<Path> fileList = Files.list(Paths.get(path))
.collect(Collectors.toList());
for (Path file: fileList) {
if (file.getFileName().toString().endsWith(".zip")) {
String[] parts = file.getFileName().toString().split("-");
String v = parts[2].replace(".zip",""); //"1.0"
if (StringUtils.isBlank(version)) {
version = v;
} else if(Double.valueOf(version) < Double.valueOf(v)) {
version = v;
}
}
}
}
// Update version to CameraAlgorithm
cameraAlgorithm.setAlgorithmVersion(version);
cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
}
map.put("algorithm_id", String.valueOf(cameraAlgorithm.getAlgorithmId()));
map.put("algorithm_name", algorithmMap.get(cameraAlgorithm.getAlgorithmId()).getName());
map.put("algorithm_name_en", nameEn);
map.put("algorithm_confidence", cameraAlgorithm.getConfidence());
map.put("algorithm_rois", cameraAlgorithm.getImagePoints());
map.put("algorithm_lines", cameraAlgorithm.getLineImagePoints());
map.put("algorithm_version", cameraAlgorithm.getAlgorithmVersion());
map.put("zip_file", String.format("%s/%s/%s-%s-%s.zip", platform, nameEn, platform, nameEn, version));
// String fileName = map.get("zip_file").toString();
// String filePath = String.format("%s/%s/%s", platform, nameEn, fileName);
String filePath = map.get("zip_file").toString();
map.put("md5","");
try {
File file = new File(MODEL_DIR + filePath);
String md5 = Md5FileUtils.getMD5(file);
map.put("md5", md5);
} catch (Exception e) {
log.error("Get File md5 Value Exception:{}", MODEL_DIR + filePath);
}
algorithms.add(map);
}
}
}
cameraMap.put("algorithms", algorithms);
// Edge Box Execute Status
cameraMap.put("status", camera.getAiboxExecStatus() == null? 0: camera.getAiboxExecStatus());
dataList.add(cameraMap);
}
return JsonResultUtils.success(dataList);
} catch (Exception e) {
log.error("aibox Call Camera List API Exception", e);
return JsonResultUtils.fail("API Exception @"+ e.getMessage());
}
}

/**
* Get Camera Resource
* @return
*/
@ApiOperation("Edge Box Sync Camera and Relate Algorithm Run Status")
@PostMapping("/status")
public JsonResult<Void> status(@RequestBody CameraStatusVo cameraStatusVo) {
//log.info("Edge Box Sync Camera and Relate Algorithm Run Status, {}", cameraStatusVo.toString());
try {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(cameraStatusVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

// By Box ID Query Camera List
Location location = this.locationService.getBoxSnForRemote(cameraStatusVo.getSn());
if (location == null) {
return JsonResultUtils.fail("find not to Box Info");
}

// Param List Verify
List<CameraStatusSubVo> cameraStatusSubVos = cameraStatusVo.getCameras();
if(cameraStatusSubVos == null || cameraStatusSubVos.isEmpty()) {
// log.error("cameraStatusSubVos, Request Param Is Empty: {}", cameraStatusSubVos);
return JsonResultUtils.success();
}

// By Box id Query All Camera
List<Camera> cameras = this.cameraService.listByBoxId(location.getId());
if(cameras == null) {
//log.error("cameras, Data s Is Empty: {}", cameras);
return JsonResultUtils.success();
}

// will Camera Relate Algorithm Run Status All Set for 0
for(Camera camera: cameras) {
cameraAlgorithmService.updateAlgorithmRunStatus(camera.getId(), 0);
}

// Camera List turn map
Map<Long, String> cameraMap = cameras.stream().collect(Collectors.toMap(Camera::getId, Camera::getName));

// Modify Camera most after Inference Status and Time, Modify Relate Algorithm most after Execute Status and Time
for(CameraStatusSubVo cameraStatusSubVo: cameraStatusSubVos) {
// Camera id Error
if(cameraStatusSubVo.getCameraId() == null) {
log.error("Camera Param Is Empty, {}", cameraStatusSubVo);
continue;
}
//
if(!cameraMap.containsKey(cameraStatusSubVo.getCameraId())) {
//log.error("Camera does not exist, {}", cameraStatusSubVo.getCameraId());
}

// @modify by zhou 2025.6.16 modify for new Data Mode
// Data result structure: [{cameraId:'xx', algorithmIds: ['model_id1','model_id2']}, {cameraId:'xx', algorithmIds: ['cap_fail']}]
// Normal Situation,eg: [{cameraId:'xx', algorithmIds: ['model_id1','model_id2']}]
// Camera Connection not up Situation,eg: [{cameraId:'xx', algorithmIds: ['cap_fail']}]
// Inference Exception not up Situation,eg: [{cameraId:'xx', algorithmIds: ['infer_fail']}]
// remark: that What not again Determine Camera Offline Status #JobCron.java@cameraOffline
List<String> algoIds = cameraStatusSubVo.getAlgorithmIds();
if(algoIds == null || algoIds.isEmpty()) {
continue;
}

int execStatus = 1000; // 1000- In Progress Inference,3000- not Inference
String execMsg ="Run in";
String algoId = algoIds.get(0);
Boolean isOffline = false;
if("cap_fail".equalsIgnoreCase(algoId)) {// Camera Collect Error
execStatus = 3000;
execMsg ="Camera Collect Exception";
isOffline = true;
} else if("infer_fail".equalsIgnoreCase(algoId)) {// Inference Exception
execStatus = 3000;
execMsg ="Inference Exception";
isOffline = true;
}
Long cameraId = cameraStatusSubVo.getCameraId();
// Modify Camera Inference Status
Camera camera1 = new Camera();
camera1.setId(cameraId);
camera1.setAiboxExecStatus(execStatus);
camera1.setAiboxExecMsg(execMsg);
camera1.setAiboxExecTime(new Date());
cameraService.updateById(camera1);

// Inference Normal, Modify Camera Relate Algorithm Run Status
if(execStatus == 1000) {
this.cameraAlgorithmService.updateAlgorithmRunStatus(cameraId, 1);
}
if(isOffline){
// Camera Offline, Send Feishu
BigDecimal count = map.get(cameraId);
if(ObjectUtil.isNotNull(count)) {
map.put(cameraId, count.add(BigDecimal.ONE));
// log.info("Camera Offline:{}, Offline sub Number:{}", cameraId, map.get(cameraId));
// log.info("sub Number Determine:{}, Send Feishu", count.remainder(new BigDecimal(100)).compareTo(BigDecimal.ZERO) == 0);
if(count.remainder(new BigDecimal(100)).compareTo(BigDecimal.ZERO) == 0){
log.info("Camera Offline, Send Feishu");
this.sendFeishu(location, cameraMap, cameraStatusSubVo);
}
}else{
map.put(cameraId, BigDecimal.ONE);
// log.info("Camera Offline 1:{}, Offline sub Number:{}", cameraId, map.get(cameraId));
}
}
// log.info("Edge Box Sync Camera and Relate Algorithm Run Status, Success");
}
return JsonResultUtils.success();
} catch (Exception e) {
log.error("aibox Call Camera Status API Exception", e);
return JsonResultUtils.fail("API Exception @"+ e.getMessage());
}
}
private void sendFeishu(Location location, Map<Long, String> cameraMap, CameraStatusSubVo cameraStatusSubVo) {
List<SocialHook> socialHookList = socialHookService.listData(SocialHookType.CAMERA.getType());
if(socialHookList.isEmpty()) {
// Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(0L);
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl("");
socialResult.setState(0);
socialResult.setErrorDetail("not has Send Group Config, Please Check Handle public Push");
socialResult.setSendText("");
socialResult.setCameraName("");
socialResult.setReportId(location.getId());
socialResult.setBusinessType(SocialResultBusinessType.CAMERA.getType());
socialResultService.save(socialResult);
}else {
String content ="Camera Offline, Please Check Network Connection Whether Normal.";
for (SocialHook socialHook: socialHookList) {
Long cameraId = cameraStatusSubVo.getCameraId();
String cameraName = cameraMap.get(cameraId);
String title ="Camera Name:"+ cameraName;
alarmFeishuPushService.send(socialHook, null, null, title, content, null,
null, cameraName, null, cameraId, null, SocialResultBusinessType.CAMERA.getType());
}
}
}
@ApiOperation("Edge Box Sync Algorithm Update Status")
@PostMapping("/algorithmStatus")
public JsonResult<List<Map<String, Object>>> algorithmStatus(@RequestBody CameraAlgorithmlVo cameraAlgorithmlVo) {
try {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(cameraAlgorithmlVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}
// Query Box Info
Location location = locationService.getBoxSnForRemote(cameraAlgorithmlVo.getSn());
if(location == null) {
return JsonResultUtils.fail("sn find not to Box Info");
}
//
if (cameraAlgorithmlVo.getStatus() == null) {
return JsonResultUtils.fail("status Status Is Empty");
}
Algorithm algorithm = algorithmService.getByNameEn(cameraAlgorithmlVo.getName());
if(location == null) {
return JsonResultUtils.fail("name find not to Algorithm Info");
}
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithmAndBoxId(algorithm.getId(), location.getId());
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
cameraAlgorithm.setBoxUpdateStatus(cameraAlgorithmlVo.getStatus());
cameraAlgorithm.setAlgorithmVersion(cameraAlgorithmlVo.getVersion());
cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
}

return JsonResultUtils.success();
} catch (Exception e) {
log.error("aibox Call Algorithm Update Status Status API Exception", e);
return JsonResultUtils.fail("API Exception @"+ e.getMessage());
}
}

@ApiOperation("Edge Box Close Stream Media")
@PostMapping("/close/livedraw")
public JsonResult<Void> closeLiveDraw(@RequestBody CameraCloseLiveDrawVo cameraCloseLiveDrawVo) {
//log.info("Edge Box Close Stream Media, params: {}", cameraCloseLiveDrawVo);
if(cameraCloseLiveDrawVo.getCameraId() == null) {
return JsonResultUtils.fail("Camera id Param Is Empty");
}
//
mediaService.closeStream(cameraCloseLiveDrawVo.getCameraId(),"livedraw");
return JsonResultUtils.success();
}

/**
* Get Camera Resource
* @return
*/
@ApiOperation("Edge Box Pull Get Camera Invalid Resource List")
@PostMapping("invalid")
public JsonResult<List<Long>> listInvalidData(@RequestBody CameraPullVo cameraPullVo) {
try {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(cameraPullVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}
// Query Box Info
Location location = locationService.getBoxSnForRemote(cameraPullVo.getSn());
if(location == null) {
return JsonResultUtils.fail("sn find not to Box Info");
}
// By Box ID Query Camera List
List<Camera> cameraList = this.cameraService.listByBoxId(location.getId());
if (cameraList == null) {
return JsonResultUtils.success(new ArrayList<>());
}

//
List<Long> invalidCameraIds = new ArrayList<>();
for(Camera camera: cameraList) {
if(camera.getState() == null || camera.getState()!= 0 || camera.getRunning() == null || camera.getRunning()!= 1) {
invalidCameraIds.add(camera.getId());
}
}
return JsonResultUtils.success(invalidCameraIds);
} catch (Exception e) {
log.error("aibox Call Camera List API Exception", e);
return JsonResultUtils.fail("API Exception @"+ e.getMessage());
}
}

/**
* Camera Enabled Report
* increase Add Algorithm For Camera Enable Hour, Camera no Method Connection, Camera no Method Get Frame, Model Load failed, Load success etc Situation
* @return
*/
@ApiOperation("Camera Enabled Report")
@PostMapping("report")
public JsonResult<?> reportStatus(@RequestBody CameraReportVo reportVo) {
try {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(reportVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

Camera modifyCamera = new Camera();
modifyCamera.setId(reportVo.getCameraId());
modifyCamera.setAiboxExecStatus(reportVo.getStatus() == null || reportVo.getStatus() == 1? 3000: 1000);
modifyCamera.setAiboxExecMsg(reportVo.getMsg());
cameraService.updateById(modifyCamera);
return JsonResultUtils.success();
} catch (Exception e) {
log.error("aibox Report Camera Enabled Exception", e);
return JsonResultUtils.fail("API Exception @"+ e.getMessage());
}
}
}