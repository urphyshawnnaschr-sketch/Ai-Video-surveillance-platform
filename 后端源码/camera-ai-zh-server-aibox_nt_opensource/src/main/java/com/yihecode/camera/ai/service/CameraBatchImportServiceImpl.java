package com.yihecode.camera.ai.service;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.enums.CameraAction;
import com.yihecode.camera.ai.enums.CameraImportState;
import com.yihecode.camera.ai.enums.CameraRunningState;
import com.yihecode.camera.ai.enums.CommState;
import com.yihecode.camera.ai.javacv.TakePhoto;
import com.yihecode.camera.ai.mapper.CameraBatchImportMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
* Camera Batch Import Management
* @author Abyss
* @date 2024/3/5 13:31
*/
@Slf4j
@Service
public class CameraBatchImportServiceImpl extends ServiceImpl<CameraBatchImportMapper, CameraBatchImport> implements CameraBatchImportService {

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private ReportPeriodService reportPeriodService;

    @Autowired
    private TakePhoto takePhoto;

    /**
* Storage Data
*
* @param cameraBatchImport
*/
    @Override
    public void saveData(CameraBatchImport cameraBatchImport) {
        CameraBatchImport batchImport = validator(cameraBatchImport);
        batchImport.setCreatedAt(new Date());
        if(StrUtil.isNotBlank(batchImport.getMistake())) { //Exist Error, Direct connect Import failed
batchImport.setImportState(2); // Failed
}
this.save(batchImport);
}

/**
* Delete All
*/
@Override
public void deleteAll() {
LambdaQueryWrapper<CameraBatchImport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.gt(CameraBatchImport::getImportState, 0);
this.remove(queryWrapper);
}

/**
* By Status Query
*
* @param state
* @return
*/
@Override
public List<CameraBatchImport> listByState(Integer state) {
LambdaQueryWrapper<CameraBatchImport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraBatchImport::getImportState, state);
queryWrapper.orderByDesc(CameraBatchImport::getCreatedAt);
List<CameraBatchImport> cameraBatchImports = this.list(queryWrapper);
return cameraBatchImports == null? new ArrayList<>(): cameraBatchImports;
}

/**
* By Import Batch Label and Status Query
*
* @param tag
* @param state
* @return
*/
@Override
public List<CameraBatchImport> listByTag(String tag, Integer state) {
LambdaQueryWrapper<CameraBatchImport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraBatchImport::getTag, tag);
queryWrapper.eq(CameraBatchImport::getImportState, state);
List<CameraBatchImport> cameraBatchImports = this.list(queryWrapper);
return cameraBatchImports == null? new ArrayList<>(): cameraBatchImports;
}

/**
* By Import Batch Label and Status Query Count
*
* @param tag
* @param state
* @return
*/
@Override
public int countByTag(String tag, Integer state) {
LambdaQueryWrapper<CameraBatchImport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraBatchImport::getTag, tag);
if(state!= null) {
queryWrapper.eq(CameraBatchImport::getImportState, state);
}
return this.count(queryWrapper);
}

/**
* Import
*
* @param cameraBatchImport
*/
@Transactional(rollbackFor = Exception.class)
@Override
public void saveImport(CameraBatchImport cameraBatchImport) {
if (cameraBatchImport.getImportState() == null || cameraBatchImport.getImportState()!= 1) {
return;
}
// Get image
Map<String, String> retMap = takePhoto.take(cameraBatchImport.getRtspUrl());
if (retMap == null) {
Map<String, String> mistakeMap = new HashMap<>();
mistakeMap.put("rtspUrl","Camera Network not via");
cameraBatchImport.setMistake(JSON.toJSONString(mistakeMap));
//
String mistakeDesc = mistakeMap.values().stream().collect(Collectors.joining(","));
cameraBatchImport.setMistakeDesc(mistakeDesc);
cameraBatchImport.setImportState(2);
this.updateById(cameraBatchImport);
return;
}

// Import Camera
String algorithm_ids = cameraBatchImport.getAlgorithmIds();
String[] algorithms_ids = algorithm_ids.split(",");
List<Long> algorithmIds = new ArrayList<>();
for (String algorithm_id: algorithms_ids) {
algorithmIds.add(Long.parseLong(algorithm_id));
}
// By Stream Address Query Camera Whether Exist
Camera camera = cameraService.getByRtspUrl(cameraBatchImport.getRtspUrl());
// Camera does not exist, Create Camera, Create Algorithm Relate, Create Alert Hour Segment, Relate Box
if (camera == null) {
// Add Camera
camera = new Camera();
camera.setName(cameraBatchImport.getName());
camera.setIntervalTime(cameraBatchImport.getIntervalTime());
camera.setAlarmInterval(cameraBatchImport.getAlarmInterval());
camera.setRtspUrl(cameraBatchImport.getRtspUrl());
camera.setState(CommState.NORMAL.getType());
camera.setRunning(CameraRunningState.CLOSED.getType());
camera.setAction(CameraAction.ACTION_UPD.getType());
camera.setCreatedAt(new Date());
camera.setLocationType(cameraBatchImport.getLocationType());
camera.setLocationId(cameraBatchImport.getLocationId());
camera.setLocationIds(cameraBatchImport.getLocationIds());
camera.setScaleRatio(3.0F);
camera.setApiParams("");
camera.setFrequency(1000);
camera.setUpdatedAt(new Date());
camera.setFileName(retMap.get("fileName"));
camera.setVideoCodec(retMap.get("videoCodecName"));
camera.setAiboxExecStatus(3000);
camera.setAiboxExecTime(new Date());
cameraService.saveOrUpdate(camera);

// Bind Algorithm
for (Long algorithmId: algorithmIds) {
// Add Camera Algorithm Relate
CameraAlgorithm cameraAlgorithm = new CameraAlgorithm();
cameraAlgorithm.setCameraId(camera.getId());
cameraAlgorithm.setAlgorithmId(algorithmId);
cameraAlgorithm.setConfidence(0.5F);
cameraAlgorithm.setMarkPoints("");
cameraAlgorithm.setImagePoints("");
cameraAlgorithmService.save(cameraAlgorithm);

// Add Default Alert Hour Segment
ReportPeriod reportPeriod = new ReportPeriod();
reportPeriod.setCameraId(camera.getId());
reportPeriod.setAlgorithmId(algorithmId);
reportPeriod.setStartText("00:00");
reportPeriod.setStartTime(0);
reportPeriod.setEndText("23:59");
reportPeriod.setEndTime(2359);
reportPeriodService.save(reportPeriod);
}

// Box Event
// if ("2".equals(cameraBatchImport.getLocationType())) {
// // Call Edge Box Camera Task
// httpAiBoxService.taskCamera(camera.getId(), 1000, 0);
//}

// Modify Status
cameraBatchImport.setImportState(CameraImportState.IMPORTSUCCESS.getType());
this.updateById(cameraBatchImport);
} else {
boolean flag = false;
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(camera.getId());
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
// Algorithm not has Bind, Add Bind
if (!algorithmIds.contains(cameraAlgorithm.getAlgorithmId())) {
// Add Camera Algorithm Relate
CameraAlgorithm newCameraAlgorithm = new CameraAlgorithm();
newCameraAlgorithm.setCameraId(camera.getId());
newCameraAlgorithm.setAlgorithmId(cameraAlgorithm.getAlgorithmId());
newCameraAlgorithm.setConfidence(0.5F);
newCameraAlgorithm.setMarkPoints("");
newCameraAlgorithm.setImagePoints("");
cameraAlgorithmService.save(newCameraAlgorithm);

// Add Default Alert Hour Segment
ReportPeriod reportPeriod = new ReportPeriod();
reportPeriod.setCameraId(camera.getId());
reportPeriod.setAlgorithmId(cameraAlgorithm.getAlgorithmId());
reportPeriod.setStartText("00:00");
reportPeriod.setStartTime(0);
reportPeriod.setEndText("23:59");
reportPeriod.setEndTime(2359);
reportPeriodService.save(reportPeriod);

//
flag = true;
}
}

// Box Event
//if ("2".equals(cameraBatchImport.getLocationType()) && flag) {
// // Call Edge Box Camera Task
// httpAiBoxService.taskCamera(camera.getId(), 6000, 0);
//}

// Modify Status
cameraBatchImport.setImportState(CameraImportState.IMPORTSUCCESS.getType());
this.updateById(cameraBatchImport);
}
}

/**
* Query most after One Import Batch Label
*
* @return
*/
@Override
public String getLastImportTag() {
LambdaQueryWrapper<CameraBatchImport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.orderByDesc(CameraBatchImport::getCreatedAt);
queryWrapper.last("limit 0, 1");
CameraBatchImport cameraBatchImport = this.getOne(queryWrapper);
if(cameraBatchImport == null) {
return null;
}
return cameraBatchImport.getTag();
}

/**
* Validate
* @param cameraBatchImport
* @return
*/
private CameraBatchImport validator(CameraBatchImport cameraBatchImport) {
Map<String, String> mistake = new HashMap<>();
if (StringUtils.isBlank(cameraBatchImport.getBrand())) {
mistake.put("brand","Brand Is Empty");
}
if(StringUtils.isNotBlank(cameraBatchImport.getBrand()) &&!("Hikvision".equals(cameraBatchImport.getBrand()) ||"big Hua".equals(cameraBatchImport.getBrand()) ||"Other".equals(cameraBatchImport.getBrand()))) {
mistake.put("brand","Brand Only Support Hikvision, big Hua, Other Three kind Type");
}
if (StringUtils.isBlank(cameraBatchImport.getName())) {
mistake.put("name","Camera Name Is Empty");
}
if (StringUtils.isBlank(cameraBatchImport.getIpHost())) {
mistake.put("ipHost","IP Address Is Empty");
}
if (StringUtils.isNotBlank(cameraBatchImport.getIpHost()) &&!Validator.isIpv4(cameraBatchImport.getIpHost())) {
mistake.put("ipHost","IP Address Format Error");
}
if (StringUtils.isNotBlank(cameraBatchImport.getPort()) &&!Validator.isNumber(cameraBatchImport.getPort())) {
mistake.put("port","Port Only Support Number Char");
}
if (StringUtils.isBlank(cameraBatchImport.getAccount())) {
mistake.put("account","Account Is Empty");
}
if (StringUtils.isNotBlank(cameraBatchImport.getAccount()) && Validator.isChinese(cameraBatchImport.getAccount())) {
mistake.put("account","Account Contain in Text");
}
// Remove front after empty Grid
cameraBatchImport.setPassword(cameraBatchImport.getPassword() == null?"": cameraBatchImport.getPassword().trim());
if (StringUtils.isBlank(cameraBatchImport.getPassword())) {
mistake.put("password","Password Is Empty");
}
if (StringUtils.isNotBlank(cameraBatchImport.getPassword()) && cameraBatchImport.getPassword().length() < 6) {
mistake.put("password","Password Few at 6 Bit");
}
if (StringUtils.isNotBlank(cameraBatchImport.getPassword()) && Validator.isChinese(cameraBatchImport.getPassword())) {
mistake.put("password","Password Contain in Text");
}
if (StrUtil.isNotBlank(cameraBatchImport.getChannel()) &&!Validator.isNumber(cameraBatchImport.getChannel())) {
mistake.put("channel","Channel No Only Support Number Char");
}
// Algorithm Validate
if (StringUtils.isBlank(cameraBatchImport.getAlgorithms())) {
mistake.put("algorithm","Algorithm Is Empty");
} else {
String algorithmMistake ="";
// Query Algorithm
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}
Map<String, Long> algorithmMap = algorithmList.stream().collect(Collectors.toMap(Algorithm::getName, Algorithm::getId, (s1, s2) -> s1));
//
List<String> algorithmIds = new ArrayList<>();
List<String> algorithmNameList = Arrays.asList(cameraBatchImport.getAlgorithms().split(","));
for (String algorithmName: algorithmNameList) {
Long algorithmId = algorithmMap.get(algorithmName);
if (null == algorithmId) {
algorithmMistake += StringUtils.isBlank(algorithmMistake)? algorithmName:","+ algorithmName;
} else {
algorithmIds.add(algorithmId +"");
}
}
//
if (StringUtils.isNotBlank(algorithmMistake)) {
algorithmMistake +="not find to Corresponding Algorithm";
mistake.put("algorithm", algorithmMistake);
} else {
cameraBatchImport.setAlgorithmIds(String.join(",", algorithmIds));
}
}
// Region Validate
if(StringUtils.isBlank(cameraBatchImport.getLocationType()) ||"2".equals(cameraBatchImport.getLocationType())) {
// Box Version, Box only has One Root Node, that What Here Direct connect Determine Whether Exist Immediate can
if (StringUtils.isBlank(cameraBatchImport.getLocation())) {
mistake.put("location","Point Bit belong belong Is Empty");
} else {
Location location = locationService.getByName(cameraBatchImport.getLocation());
if (null == location) {
mistake.put("location","not find to Point Bit belong belong");
} else {
cameraBatchImport.setLocationId(location.getId());
cameraBatchImport.setLocationIds(location.getParentIds() +","+ location.getId());
}
}
} else {
// Server Version, like Result Here with / Slash Do Hierarchy Management, Need Create
if (StringUtils.isBlank(cameraBatchImport.getLocation())) {
mistake.put("location","Point Bit belong belong Is Empty");
} else {
String[] nodes = cameraBatchImport.getLocation().split("/");
Location location = locationService.saveNodes(nodes);
if(location == null) {
mistake.put("location","Point Bit belong belong Format Error");
} else {
cameraBatchImport.setLocationId(location.getId());
cameraBatchImport.setLocationIds(location.getParentIds());
}
}
}
//
if (null == cameraBatchImport.getIntervalTime()) {
mistake.put("intervalTime","Recognition Interval Is Empty");
} else if(cameraBatchImport.getIntervalTime() <= 0) {
mistake.put("intervalTime","Recognition Interval must be greater than 0");
}
if (null == cameraBatchImport.getAlarmInterval()) {
mistake.put("alarmInterval","Alert Interval Is Empty");
} else if(cameraBatchImport.getAlarmInterval() <= 0) {
mistake.put("alarmInterval","Alert Interval must be greater than 0");
}
if (null!= cameraBatchImport.getIntervalTime() && null!= cameraBatchImport.getAlarmInterval()
&& cameraBatchImport.getAlarmInterval() < cameraBatchImport.getIntervalTime()) {
mistake.put("alarmInterval","Alert Interval must be greater than Recognition Interval");
}
// rtsp Address Validate
if(StringUtils.isBlank(cameraBatchImport.getRtspUrl())) {
if (StringUtils.isNotBlank(cameraBatchImport.getBrand())
&& StringUtils.isNotBlank(cameraBatchImport.getIpHost())
&& StringUtils.isNotBlank(cameraBatchImport.getAccount())
&& StringUtils.isNotBlank(cameraBatchImport.getPassword())
) {
String rtspUrl = formatRtsp(cameraBatchImport);
if (rtspUrl == null) {
mistake.put("rtspUrl","not Support Camera, Please Manual Fill Stream Address");
} else {
cameraBatchImport.setRtspUrl(rtspUrl);
}
}
}

//
cameraBatchImport.setMistake(mistake.isEmpty()?"": JSON.toJSONString(mistake));
//
String mistakeDesc = mistake.values().stream().collect(Collectors.joining(","));
cameraBatchImport.setMistakeDesc(mistakeDesc);
return cameraBatchImport;
}

/**
* Format Change Stream Address
* @param cameraBatchImport
* @return
*/
private String formatRtsp(CameraBatchImport cameraBatchImport) {
try {
// Hikvision Camera, Stream Address Format:rtsp://[username]:[password]@[address]:[port]/Streaming/Channels/[id]?transportmode=[type]
if (StrUtil.isNotBlank(cameraBatchImport.getBrand()) && cameraBatchImport.getBrand().equals("Hikvision")) {
return String.format("rtsp://%s:%s@%s/Streaming/Channels/%s",
cameraBatchImport.getAccount(),
URLEncoder.encode(cameraBatchImport.getPassword(),"utf-8"),
cameraBatchImport.getIpHost(),
StringUtils.isNotBlank(cameraBatchImport.getChannel())? cameraBatchImport.getChannel():"101");
}
// big Hua Camera, Stream Address Format: rtsp://[username]:[password]@[address]:[port]/cam/realmonitor?channel=[id]&subtype=[type]
if (StrUtil.isNotBlank(cameraBatchImport.getBrand()) && cameraBatchImport.getBrand().equals("big Hua")) {
return String.format("rtsp://%s:%s@%s/cam/realmonitor?channel=%s&subtype=0",
cameraBatchImport.getAccount(),
URLEncoder.encode(cameraBatchImport.getPassword(),"utf-8"),
cameraBatchImport.getIpHost(),
StringUtils.isNotBlank(cameraBatchImport.getChannel())? cameraBatchImport.getChannel():"1");
}
// Other, can with supplement Charge
return null;
} catch (UnsupportedEncodingException e) {
log.error("Camera Batch Import Splice connect rtsp Address Exception, not Support Code Format", e);
}
return null;
}

}
