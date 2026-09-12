package com.yihecode.camera.ai.web.api.aibox;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yihecode.camera.ai.comm.CommService;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.Record;
import com.yihecode.camera.ai.entity.wvp.GBDeviceChannel;
import com.yihecode.camera.ai.javacv.DecodeRtspUlr;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.wvp.GBDeviceChannelService;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.api.aibox.vo.RecordRegistZlmVo;
import com.yihecode.camera.ai.web.api.aibox.vo.UploadRecordFileV2Vo;
import com.yihecode.camera.ai.web.api.aibox.vo.UploadRecordFileVo;
import com.yihecode.camera.ai.web.api.aibox.vo.UploadRecordDataVo;
import com.yihecode.camera.ai.web.api.comm.AlarmVideoPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
* Recording Management
*
* @author 465769438@qq.com
* @since 2025/3/11
*/
@Slf4j
@SaIgnore
@RestController
@RequestMapping("/aibox/record")
public class AiboxRecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CommService commService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MediaServerService mediaServerService;

    @Autowired
    private GBDeviceChannelService deviceChannelService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private AlarmVideoPushService alarmVideoPushService;

    @Autowired
    private ProjectConfig projectConfig;

    @Value("${recordDir}")
    private String recordDir;

    /**
* Submit Alarm Recording Data, not Contain File
* @param recordDataVo
* @return
*/
    @PostMapping("save")
    public JsonResult<?> save(UploadRecordDataVo recordDataVo) {
        try {
            //Validate key
if(!getLocalKey().equalsIgnoreCase(recordDataVo.getKey())) {
return JsonResultUtils.fail("key Error, Upload failed");
}

// Query Box
Location location = locationService.getBySn(recordDataVo.getSn());
if(location == null) {
return JsonResultUtils.fail("Box does not exist, Upload failed");
}

// Save Record
Record record = new Record();
record.setFlag(0);
record.setApp(recordDataVo.getApp());
record.setStream(recordDataVo.getStream());
record.setMediaServerId(recordDataVo.getMediaServerId());
record.setTimeLen(recordDataVo.getTimeLen());
record.setFileName(recordDataVo.getFileName());
record.setFileSize(recordDataVo.getFileSize());
record.setFileUrl(recordDataVo.getUrl());
record.setStartTime(Long.parseLong(recordDataVo.getStartTime() +"000"));
record.setEndTime(System.currentTimeMillis());
record.setUploadFlag(0);
record.setLocationId(location.getId());
recordService.save(record);
return JsonResultUtils.success();
} catch (Exception e) {
log.error("Recording Save failed, {}", e.getMessage());
}
return JsonResultUtils.fail("Recording Save failed");
}

/**
* Submit Alarm Recording Data, not Contain File
* @param recordDataVo
* @return
*/
@PostMapping("v2/save")
public JsonResult<?> saveV2(UploadRecordDataVo recordDataVo) {
try {
// Validate key
if(!getLocalKey().equalsIgnoreCase(recordDataVo.getKey())) {
return JsonResultUtils.fail("key Error, Upload failed");
}

// Parse Camera ID
Long cameraId = null;
String stream = recordDataVo.getStream();
if(stream.contains("_")) {// GB Standard Type
String[] device2channel = stream.split("_");
GBDeviceChannel gbDeviceChannel = deviceChannelService.getByDeviceIdAndChannelId(device2channel[0], device2channel[1]);
if(gbDeviceChannel!= null && gbDeviceChannel.getCameraId()!= null && gbDeviceChannel.getCameraId()!= 0) {
cameraId = gbDeviceChannel.getCameraId();
}
} else {
cameraId = Long.parseLong(stream);
}

// Camera find not to
if(cameraId == null) {
return JsonResultUtils.fail("find not to Corresponding Camera, no Need Upload Recording");
}

Camera camera = cameraService.getById(cameraId);
if(camera == null) {
return JsonResultUtils.fail("find not to Corresponding Camera, no Need Upload Recording");
}

// Recording Hour long
String recordTimes = configService.getByValTag("recordTimes");
int times = 15; // Default 15 s
if(StrUtil.isNotBlank(recordTimes)) {
try {
times = Integer.parseInt(recordTimes);
} catch (Exception e) {
//
}
}

// Recording Start and End Time
long startTime = Long.parseLong(recordDataVo.getStartTime() +"000");
long endTime = startTime + recordDataVo.getTimeLen() * 1000L; // Start Time Stamp + Recording Hour long * 1000 + Difference Value (supplement Even) + 1000(supplement Even)

// Query Alarm List
List<Report> reportList = reportService.listByCameraAndTimeBetween(cameraId, startTime - 3000, endTime);
if(reportList == null || reportList.isEmpty()) {
// Save Record, Keep As for Problem Fixed Bit Query
// Record record = new Record();
// record.setFlag(0);
// record.setApp(recordDataVo.getApp());
// record.setStream(recordDataVo.getStream());
// record.setMediaServerId(recordDataVo.getMediaServerId());
// record.setTimeLen(recordDataVo.getTimeLen());
// record.setFileName(recordDataVo.getFileName());
// record.setFileSize(recordDataVo.getFileSize());
// record.setFileUrl(recordDataVo.getUrl());
// record.setStartTime(startTime);
// record.setEndTime(endTime);
// record.setUploadFlag(0);
// record.setLocationId(camera.getLocationId());
// recordService.save(record);

return JsonResultUtils.fail("not has Alarm, no Need Upload Recording");
}

// Save Record
Record record = new Record();
record.setFlag(1);
record.setApp(recordDataVo.getApp());
record.setStream(recordDataVo.getStream());
record.setMediaServerId(recordDataVo.getMediaServerId());
record.setTimeLen(recordDataVo.getTimeLen());
record.setFileName(recordDataVo.getFileName());
record.setFileSize(recordDataVo.getFileSize());
record.setFileUrl(recordDataVo.getUrl());
record.setStartTime(startTime);
record.setEndTime(endTime);
record.setUploadFlag(0);
record.setLocationId(camera.getLocationId());
recordService.save(record);

// Modify Alarm Relate Recording ID
for(Report report: reportList) {
Report report1 = new Report();
report1.setId(report.getId());
report1.setRecordId(record.getId());
reportService.updateById(report1);
}
return JsonResultUtils.success(record.getId());
} catch (Exception e) {
log.error("Recording Save failed, {}", e.getMessage());
}
return JsonResultUtils.fail("Recording Save failed");
}

/**
* Upload Recording File
* @param recordFileV2Vo
* @return
*/
@PostMapping("v2/upload")
public JsonResult<?> uploadV2(UploadRecordFileV2Vo recordFileV2Vo) {
try {
if(StrUtil.isBlank(recordFileV2Vo.getPath())) {
return JsonResultUtils.fail("Recording File Is Empty, Upload failed");
}

if(recordFileV2Vo.getRecordId() == null) {
return JsonResultUtils.fail("Recording ID Is Empty, Upload failed");
}

if(!getLocalKey().equalsIgnoreCase(recordFileV2Vo.getKey())) {
return JsonResultUtils.fail("key Error, Upload failed");
}

Record record = recordService.getById(recordFileV2Vo.getRecordId());
if(record == null) {
return JsonResultUtils.fail("Recording Data Is Empty, Upload failed");
}

Record record1 = new Record();
record1.setId(recordFileV2Vo.getRecordId());
record1.setRecordPath(recordFileV2Vo.getPath());
record1.setUploadFlag(1);
recordService.updateById(record1);

// Video Push to Third Party Platform
if(StrUtil.isNotBlank(projectConfig.getThirdPushVideoUrl())) {
List<Long> reportIds = reportService.getIdsByRecordId(record1.getId());
alarmVideoPushService.sendVideo(record, reportIds);
}

return JsonResultUtils.success();
} catch (Exception e) {
log.error("Recording Save failed, {}", e.getMessage());
}
return JsonResultUtils.fail("Recording Save failed");
}

/**
* Upload Recording File
* @param recordFileVo
* @return
*/
@PostMapping("upload")
public JsonResult<?> upload(UploadRecordFileVo recordFileVo) {
try {
if(recordFileVo.getFile() == null) {
return JsonResultUtils.fail("Recording File Is Empty, Upload failed");
}

if(recordFileVo.getRecordId() == null) {
return JsonResultUtils.fail("Recording ID Is Empty, Upload failed");
}

if(!getLocalKey().equalsIgnoreCase(recordFileVo.getKey())) {
return JsonResultUtils.fail("key Error, Upload failed");
}

Record record = recordService.getById(recordFileVo.getRecordId());
if(record == null) {
return JsonResultUtils.fail("Recording Data Is Empty, Upload failed");
}

// Create Storage Path
String url = record.getFileUrl();
int idx = url.lastIndexOf("/");
String path = FileUtils.pathTo(recordDir +"/"+ url.substring(0, idx));
FileUtil.mkdir(path);

String filepath = FileUtils.pathTo(path +"/"+ record.getFileName());
if(!FileUtil.exist(filepath)) {
// Save File
recordFileVo.getFile().transferTo(FileUtil.newFile(filepath));
}

// Save Record
Record updateRecord = new Record();
updateRecord.setId(record.getId());
updateRecord.setUploadFlag(1);
recordService.updateById(updateRecord);
return JsonResultUtils.success();
} catch (Exception e) {
log.error("Recording Save failed, {}", e.getMessage());
}
return JsonResultUtils.fail("Recording Save failed");
}

/**
* Query not Upload Recording
* @param sn
* @return
*/
@PostMapping("files")
public JsonResult<?> listRecordFiles(String sn, String key) {
// Validate key
if(!getLocalKey().equalsIgnoreCase(key)) {
return JsonResultUtils.fail("key Error, Upload failed");
}

// Query Box
Location location = locationService.getBySn(sn);
if(location == null) {
return JsonResultUtils.fail("Box does not exist, Upload failed");
}

// Recent 30 min? Query Recent Bind Alarm, But not has Upload Recording File Record
long mills = DateUtil.offsetMinute(new Date(), -30).getTime();
List<Record> recordList = recordService.listByUploadFlag(location.getId(), 0, mills);
if(recordList == null) {
recordList = new ArrayList<>();
}

List<Map<String, Object>> dataList = new ArrayList<>();
for(Record record: recordList) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("recordId", record.getId());
dataMap.put("url", record.getFileUrl());
dataList.add(dataMap);
}
return JsonResultUtils.success(dataList);
}

/**
* Back Enable Inference Camera
* @return
*/
@GetMapping("urls")
public JsonResult<?> getRtspUrls(String sn, String key) {
List<Map<String, Object>> rtspUrls = new ArrayList<>();

if(!getLocalKey().equals(key)) {
return JsonResultUtils.fail("key Value Error");
}

// Recording not Enable
if(!commService.isRecordEnable()) {
log.info("Recording Function can not Enable isRecordEnable {}", commService.isRecordEnable());
return JsonResultUtils.success(rtspUrls);
}

Location location = locationService.getBySn(sn);
if(location == null) {
log.info("Recording not Enable, check find not to Box Device sn {}", sn);
return JsonResultUtils.success(rtspUrls);
}

List<Camera> cameras = cameraService.listByBoxId(location.getId());
for(Camera camera: cameras) {
if(camera.getRunning()!= null && camera.getRunning() == 1) {
Map<String, Object> data = new HashMap<>();
data.put("camera_id", camera.getId());
data.put("camera_url", DecodeRtspUlr.processRtspUrl(camera.getRtspUrl()));
rtspUrls.add(data);
}
}
return JsonResultUtils.success(rtspUrls);
}

@GetMapping("cameras")
public JsonResult<?> getCameras(String mediaServerId, String sn, @RequestParam(defaultValue ="0") Integer crossNet, String key) {
// Compare key Value
if(!getLocalKey().equals(key)) {
return JsonResultUtils.fail("key Value Error");
}

// Return Result
List<Map<String, Object>> results = new ArrayList<>();

//
if(crossNet == 0) {// Local Record make
// Query Media Node
MediaServer mediaServer = mediaServerService.getByName(mediaServerId);
if(mediaServer == null) {
return JsonResultUtils.fail("not has Corresponding Media Node");
}

// By Media Node Query Camera List
List<Camera> cameraList = cameraService.listByMediaServerId(mediaServer.getId());
if(cameraList.isEmpty()) {
return JsonResultUtils.success(results);
}

// Data Process
for(Camera camera: cameraList) {
Map<String, Object> data = new HashMap<>();
data.put("camera_id", camera.getId());
data.put("camera_url", camera.getRtspUrl());
results.add(data);
}
} else {// Cross net Record make
Location location = locationService.getBySn(sn);
if(location!= null) {
List<Camera> cameraList = cameraService.listByBoxId(location.getId());
// Data Process
for(Camera camera: cameraList) {
Map<String, Object> data = new HashMap<>();
data.put("camera_id", camera.getId());
data.put("camera_url", DecodeRtspUlr.processRtspUrl(camera.getRtspUrl()));
results.add(data);
}
}
}
return JsonResultUtils.success(results);
}

/**
* Get Camera List,V2 Version By Box SN Get Current Box up Config Camera
* @param mediaServerId
* @param sn
* @param key
* @return
*/
@GetMapping("v2/cameras")
public JsonResult<?> getCamerasV2(String mediaServerId, String sn, String key) {
// Compare key Value
if(!getLocalKey().equals(key)) {
return JsonResultUtils.fail("key Value Error");
}

Location location = locationService.getBySn(sn);
if(location == null) {
return JsonResultUtils.fail("Box not find to");
}

List<Camera> cameraList = cameraService.listByBoxId(location.getId());
if(ObjectUtil.isEmpty(cameraList)) {
return JsonResultUtils.fail("not has Need Record make Camera");
}

// Return Result
List<Map<String, Object>> results = new ArrayList<>();

// Data Process
for(Camera camera: cameraList) {
// Filter Status not Normal, Filter not Enable Inference
if(camera.getState() == null || camera.getState()!= 0 || camera.getRunning() == null || camera.getRunning()!= 1) {
continue;
}
Map<String, Object> data = new HashMap<>();
data.put("camera_id", camera.getId());
data.put("camera_url", DecodeRtspUlr.processRtspUrl(camera.getRtspUrl()));
results.add(data);
}
return JsonResultUtils.success(results);
}

/**
* Query Recording Enabled
*/
@GetMapping("enable")
public JsonResult<?> getEnable(String sn, String key) {
if(!getLocalKey().equals(key)) {
return JsonResultUtils.fail("key Value Error");
}

// Recording Hour long
String recordSecs = configService.getByValTag("recordSecs");

Map<String, Object> result = new HashMap<>();
result.put("enable", commService.isRecordEnable());
result.put("secs", StrUtil.isBlank(recordSecs)? 15: Integer.parseInt(recordSecs));
return JsonResultUtils.success(result);
}

/**
* Record make Node Register, will Record make Node Corresponding zlm Phase close Info in Line Register
* @param vo
* @return
*/
// @PostMapping("regist_zlm")
// public JsonResult<?> registZlm(@RequestBody RecordRegistZlmVo vo) {
// // Compare key Value
// if(!getLocalKey().equals(vo.getKey())) {
// return JsonResultUtils.fail("key Value Error");
//}
//
// if(StrUtil.isBlank(vo.getIp())) {
// return JsonResultUtils.fail("ip Param Is Empty");
//}
// if(StrUtil.isBlank(vo.getName())) {
// return JsonResultUtils.fail("name Param Is Empty");
//}
// if(vo.getHttpPort() == null) {
// return JsonResultUtils.fail("http.port Param Is Empty");
//}
//
// //
// MediaServer mediaServer = mediaServerService.getByName(vo.getName());
// if(mediaServer!= null) {
// return JsonResultUtils.success();
//}
//
// mediaServer = new MediaServer();
// mediaServer.setNodeType(vo.getNodeType());
// mediaServer.setName(vo.getName());
// mediaServer.setSecret(vo.getSecret());
// mediaServer.setIp(vo.getIp());
// mediaServer.setHttpPort(vo.getHttpPort());
// mediaServer.setRtspPort(vo.getRtspPort());
// mediaServer.setRtcPort(vo.getRtcPort());
// mediaServer.setRtpPortRange(vo.getRtpPortRange());
// mediaServer.setSendRtpPortRange(vo.getSendRtpPortRange());
// mediaServerService.save(mediaServer);
// return JsonResultUtils.success();
//}

/**
* Local key Value
* @return
*/
private String getLocalKey() {
String aiboxKey = configService.getByValTag("aiboxKey");
return SecureUtil.md5(StrUtil.isBlank(aiboxKey)?"-1024-": aiboxKey);
}
}
