package com.yihecode.camera.ai.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.comm.CommService;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.Record;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.media.MediaRestfulService;
import com.yihecode.camera.ai.media.MediaStreamNode;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.MessageType;
import com.yihecode.camera.ai.netty.data.StreamRecordRequest;
import com.yihecode.camera.ai.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class StreamRecordJob {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private MediaRestfulService mediaRestfulService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    @Autowired
    private CommService commService;

    @Autowired
    private ProjectConfig projectConfig;

    @Value("${recordDir:}")
    private String recordDir;

    //Cross net Recording Sync Process
private boolean crossNetHandling = false;

public void runJob() {
// try {
// // Camera Config List
// List<Camera> cameraList = cameraService.listData();
// if (cameraList == null || cameraList.isEmpty()) {
// return;
//}
//
// // Cross net, Need Push Message to Local Box
// if(projectConfig.isCrossNet()) {
// runCrossNet();
// return;
//}
//
// // Stream Media Stream List
// List<MediaStreamNode> mediaStreamNodes = mediaRestfulService.getMediaList();
//
// // not Enable Record make, or not In Record make Hour Segment, or not In Record make Date
// if(!commService.isRecordEnable()) {
// for(MediaStreamNode mediaStreamNode: mediaStreamNodes) {
// if(mediaStreamNode.getReaderCount() == 0) {
// // no Person View, Direct connect Close
// //mediaRestfulService.closeStream(mediaStreamNode.getApp(), mediaStreamNode.getStream());
//} else if(mediaStreamNode.isRecordMp4()) {
// // Close Recording
// mediaRestfulService.stopRecord(mediaStreamNode.getApp(), mediaStreamNode.getStream());
//
//}
//}
// return;
//}
//
// //
// for(Camera camera: cameraList) {
// String cameraId = String.valueOf(camera.getId());
//
// // like Result Camera Status Error, Direct connect Close
// if(camera.getState() == null || camera.getState()!= 0) {
// for(MediaStreamNode mediaStreamNode: mediaStreamNodes) {
// if(mediaStreamNode.getStream().equalsIgnoreCase(cameraId)) {
// mediaRestfulService.closeStream(mediaStreamNode.getApp(), mediaStreamNode.getStream());
//}
//}
// continue;
//}
//
// // like Result Camera Normal, But is not Enable, Close Recording
// if(camera.getRunning() == null || camera.getRunning()!= 1) {
// for(MediaStreamNode mediaStreamNode: mediaStreamNodes) {
// if(mediaStreamNode.getStream().equalsIgnoreCase(cameraId)) {
// if(mediaStreamNode.getReaderCount() == 0) {
// // no Person View, Direct connect Close
// //mediaRestfulService.closeStream(mediaStreamNode.getApp(), mediaStreamNode.getStream());
//} else if(mediaStreamNode.isRecordMp4()) {
// // Close Recording
// mediaRestfulService.stopRecord(mediaStreamNode.getApp(), mediaStreamNode.getStream());
//}
//}
//}
// continue;
//}
//
// // like Result Camera Normal, Enable Inference, Open Recording
// boolean exists = false;
// for(MediaStreamNode mediaStreamNode: mediaStreamNodes) {
// if(mediaStreamNode.getStream().equalsIgnoreCase(cameraId)) {
// exists = true;
//
// if(!mediaStreamNode.isRecordMp4()) {
// // Start Recording
// int secs = 0;
// String recordSecs = configService.getByValTag("recordSecs");
// if(StrUtil.isNotBlank(recordSecs)) {
// try {
// secs = Integer.parseInt(recordSecs);
//} catch (Exception e) {
// log.error("Recording Hour long Error: {}", recordSecs);
//}
//}
// log.info("Start Recording");
// mediaRestfulService.startRecord(mediaStreamNode.getApp(), mediaStreamNode.getStream(), secs);
//}
//}
//}
//
// // Stream does not exist, Pull Stream
// if(!exists) {
// mediaRestfulService.addStream(camera.getId(), camera.getRtspUrl());
//}
//}
//} catch (Exception e) {
// log.error("JOB Execute -> MediaRecordJob Exception", e);
//}
}

// Cross net Recording Process
public void runCrossNet() {
if(crossNetHandling) {
return;
}
crossNetHandling = true;

try {
List<Location> locationList = locationService.list();
if (locationList == null || locationList.isEmpty()) {
return;
}

if (!commService.isRecordEnable()) {
// Close Recording
for(Location location: locationList) {
// Reset Recording Enabled
StreamRecordState.getInst().setRecordState(location.getId(), -1L);

// up One sub Operation via Execute over, not again Execute
if(!StreamRecordState.getInst().getCloseState(location.getId())) {
continue;
}

// Set via Execute over Close Operation
StreamRecordState.getInst().setCloseState(location.getId(), 1);

// Call Box Close Record make
List<Camera> cameraList = cameraService.listByBoxId(location.getId());
if(cameraList == null || cameraList.isEmpty()) {
continue;
}

List<Map<String, Object>> cameraRequestList = new ArrayList<>();
for(Camera camera: cameraList) {
Map<String, Object> cameraMap = new HashMap<>();
cameraMap.put("camera_id", camera.getId());
cameraMap.put("rtsp_url", camera.getRtspUrl());
cameraRequestList.add(cameraMap);
}

StreamRecordRequest request = new StreamRecordRequest();
request.setType(MessageType.STREAM_RECORD.getType());
request.setSn(location.getBoxNo());
request.setRequestId(IdUtil.randomUUID());
request.setCameras(cameraRequestList);
request.setRecordType(0);
messageSenderAndWaiter.sendRequest(request);
}
} else {
// Enable Recording
for(Location location: locationList) {
// Reset Recording Disabled
StreamRecordState.getInst().setCloseState(location.getId(), 0);

// Distance up sub Operation not super over 10 min
if(!StreamRecordState.getInst().getRecordState(location.getId())) {
continue;
}

// Query Camera
List<Camera> cameraList = cameraService.listByBoxId(location.getId());
if(cameraList == null || cameraList.isEmpty()) {
continue;
}

// Send Enable Recording Request
List<Map<String, Object>> cameraRequestList = new ArrayList<>();
for(Camera camera: cameraList) {
Map<String, Object> cameraMap = new HashMap<>();
cameraMap.put("camera_id", camera.getId());
cameraMap.put("rtsp_url", camera.getRtspUrl());
cameraRequestList.add(cameraMap);
}

StreamRecordRequest request = new StreamRecordRequest();
request.setType(MessageType.STREAM_RECORD.getType());
request.setSn(location.getBoxNo());
request.setRequestId(IdUtil.randomUUID());
request.setCameras(cameraRequestList);
request.setRecordType(1);
messageSenderAndWaiter.sendRequest(request);

// Reset Recording Disabled
StreamRecordState.getInst().setRecordState(location.getId(), System.currentTimeMillis());
}
}
} catch (Exception e) {
log.error("Cloud End Recording Notification Exception, ex: {}", e.getMessage());
} finally {
crossNetHandling = false;
}
}

public void runConnect() {
//mediaRestfulService.setServerConfig();
}

public void runAddFlag() {
long mills = DateUtil.offsetMinute(new Date(), -3).getTime();
List<Report> reportList = reportService.listRecentByMills(mills);
if(reportList == null || reportList.isEmpty()) {
return;
}

for(Report report: reportList) {
long createdMills = report.getCreatedMills();
String cameraId = String.valueOf(report.getCameraId());

Record record = recordService.getRecordId("live", cameraId, createdMills);
if(record == null) {
continue;
}

//
Report report1 = new Report();
report1.setId(report.getId());
report1.setRecordId(record.getId());
reportService.updateById(report1);

//
if(record.getFlag() == null || record.getFlag() == 0) {
Record record1 = new Record();
record1.setId(record.getId());

// Non Cross Network Environment
if(!projectConfig.isCrossNet()) {
record1.setUploadFlag(1);
}

record1.setFlag(1);
recordService.updateById(record1);
}
}
}

public void runDelFlag() {
long mills = DateUtil.offsetMinute(new Date(), -10).getTime();
List<Record> records = recordService.listEarlyByFlag(0, mills);
if(records == null || records.isEmpty()) {
return;
}

for(Record record: records) {
recordService.removeById(record.getId());

// Local Mode, Delete Recording File
if(!projectConfig.isCrossNet()) {
String path = recordDir + record.getFileUrl();
FileUtil.del(path);
}
}
}
}
