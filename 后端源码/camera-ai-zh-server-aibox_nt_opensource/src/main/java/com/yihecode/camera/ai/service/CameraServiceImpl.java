package com.yihecode.camera.ai.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.wvp.GBDeviceChannel;
import com.yihecode.camera.ai.enums.CameraAction;
import com.yihecode.camera.ai.enums.CameraRunningState;
import com.yihecode.camera.ai.enums.CommState;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.CameraMapper;
import com.yihecode.camera.ai.netty.MessageSendHandler;
import com.yihecode.camera.ai.netty.data.CameraAddResponse;
import com.yihecode.camera.ai.service.wvp.GBDeviceChannelService;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.api.aibox.vo.CameraStatusSubVo;
import com.yihecode.camera.ai.web.dto.CameraDTO;
import com.yihecode.camera.ai.web.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
* Camera Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Service
public class CameraServiceImpl extends ServiceImpl<CameraMapper, Camera> implements CameraService {

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private ReportPeriodService reportPeriodService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    @Lazy
    private LocationService locationService;

    @Autowired
    private BoxVersionService boxVersionService;

    @Autowired
    private GBDeviceChannelService gbDeviceChannelService;

    @Autowired
    private MediaServerService mediaServerService;

    @Autowired
    private SocialConfigService socialConfigService;

    //
@Resource
private MessageSendHandler messageSendHandler;

@Value("${dataModelsDir}")
public String MODEL_DIR;

/**
*
* @param id
*/
@Override
public void delete(Long id) {
LambdaUpdateWrapper<Camera> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(Camera::getState, CommState.DISABLED.getType())
.set(Camera::getAction, CameraAction.ACTION_DEL.getType())
.eq(Camera::getId, id);
this.getBaseMapper().update(null, updateWrapper);

// Delete Relate Algorithm
cameraAlgorithmService.deleteByCamera(id);
}

/**
*
* @param camera
* @param str
* @param confidencevos
*/
@Override
@Transactional(rollbackFor = Exception.class)
public void saveCamera(Camera camera, String str, String confidencevos, String markpointsvos, Integer updatePoint, String lineMarkPoints) {
// Prevent Coordinate not stop change Dynamic
if(updatePoint == null || updatePoint == 1) {
//
List<List<Map<String, Object>>> apiParams = new ArrayList<>();
if (StrUtil.isNotBlank(camera.getParams()) && StrUtil.isNotBlank(camera.getFileName()) && camera.getScaleRatio()!= null) {
JSONArray points = JSON.parseArray(camera.getParams());
int len = points.size();
for (int i = 0; i < len; i++) {
JSONArray subPoint = points.getJSONArray(i);
int subLen = subPoint.size();
if (subLen >= 3) {
List<Map<String, Object>> subParams = new ArrayList<>();
for (int j = 0; j < subLen; j++) {
JSONObject point = subPoint.getJSONObject(j);
float x = point.getFloatValue("x");
float y = point.getFloatValue("y");
float xNew = x * camera.getScaleRatio();
float yNew = y * camera.getScaleRatio();

Map<String, Object> p = new HashMap<>();
p.put("x", Float.valueOf(xNew).intValue());
p.put("y", Float.valueOf(yNew).intValue());
subParams.add(p);
}
apiParams.add(subParams);
}
}
}

//
if (apiParams.isEmpty()) {
camera.setApiParams("");
} else {
camera.setApiParams(JSON.toJSONString(apiParams));
}
}

// Query Whether Box
Long locationId = camera.getLocationId();
Location location = locationService.getById(locationId);
if(location!= null &&"2".equals(location.getLocationType())) {
camera.setLocationType("2");
}

//
camera.setFrequency(1000);
camera.setAction(CameraAction.ACTION_UPD.getType());
camera.setUpdatedAt(new Date());
this.saveOrUpdate(camera);

//
String[] algorithmIds = str.split(",");
if(algorithmIds == null) {
return;
}

//
String[] confidenceArr = confidencevos.split(",");
if(confidenceArr == null || confidenceArr.length!= algorithmIds.length) {
return;
}

//
String[] markPointsStrArr = null;
String[] imagePointsArr = null; // Convert Coordinate
if(StrUtil.isNotBlank(camera.getFileName()) && StrUtil.isNotBlank(markpointsvos)) {
markPointsStrArr = markpointsvos.split("#");
if(markPointsStrArr!= null) {
int len = markPointsStrArr.length;
imagePointsArr = new String[len];

for(int i = 0; i < len; i++) {
String markPointsStr = markPointsStrArr[i]; // [[{x:0, y:0},....]]
if(StrUtil.isBlank(markPointsStr)) {
imagePointsArr[i] ="";
continue;
}
if("[]".equals(markPointsStr)) {
markPointsStrArr[i] ="";
imagePointsArr[i] ="";
continue;
}

//
JSONArray imagePointsGrp = new JSONArray(); // new Coordinate group Set combine
JSONArray markPointsGrp = JSON.parseArray(markPointsStr);
int grpSize = markPointsGrp.size();
for(int k = 0; k < grpSize; k++) {
JSONArray imagePoints = new JSONArray(); // new Coordinate group
JSONArray markPoints = markPointsGrp.getJSONArray(k);
int mpSize = markPoints.size();
for(int m = 0; m < mpSize; m++) {
JSONObject point = markPoints.getJSONObject(m);
float x = point.getFloatValue("x");
float y = point.getFloatValue("y");
float xNew = x * camera.getScaleRatio();
float yNew = y * camera.getScaleRatio();
//
JSONObject imagePoint = new JSONObject();
imagePoint.put("x", Float.valueOf(xNew).intValue());
imagePoint.put("y", Float.valueOf(yNew).intValue());
imagePoints.add(imagePoint);
}
imagePointsGrp.add(imagePoints);
}
imagePointsArr[i] = imagePointsGrp.toJSONString();
}
}
}
//
cameraAlgorithmService.deleteByCamera(camera.getId());

//
int len = algorithmIds.length;
for(int i = 0; i < len; i++) {
String algorithmId = algorithmIds[i];
Algorithm algorithm = algorithmService.getById(algorithmId);
CameraAlgorithm cameraAlgorithm = new CameraAlgorithm();
cameraAlgorithm.setCameraId(camera.getId());
cameraAlgorithm.setAlgorithmId(Long.parseLong(algorithmId));
cameraAlgorithm.setConfidence(Float.parseFloat(confidenceArr[i]));
cameraAlgorithm.setMarkPoints(markPointsStrArr == null?"": markPointsStrArr[i]);
cameraAlgorithm.setImagePoints(imagePointsArr == null?"": imagePointsArr[i]);
if ("person_tracker".equals(algorithm.getNameEn()) && StrUtil.isNotBlank(lineMarkPoints)) {
cameraAlgorithm.setLineMarkPoints(lineMarkPoints);
cameraAlgorithm.setLineImagePoints(markToImagePoints(lineMarkPoints, camera.getScaleRatio()));
}
// like Result version Is Empty, Get Box down Other Camera Algorithm version
String version ="";
List<CameraAlgorithm> caList = cameraAlgorithmService.listByAlgorithmAndBoxId(cameraAlgorithm.getAlgorithmId(), location.getId());
for (CameraAlgorithm ca: caList) {
if (org.apache.commons.lang3.StringUtils.isNotBlank(ca.getAlgorithmVersion())) {
version = ca.getAlgorithmVersion();
break;
}
}
// like Result according Is Empty, Get Local File most new version
try {
if (org.apache.commons.lang3.StringUtils.isBlank(version)) {
String path = MODEL_DIR + algorithm.getPlatform() +"/"+ algorithm.getNameEn();
List<Path> fileList = Files.list(Paths.get(path)).collect(Collectors.toList());
for (Path file: fileList) {
if (file.getFileName().toString().endsWith(".zip")) {
String[] parts = file.getFileName().toString().split("-");
String v = parts[2].replace(".zip",""); //"1.0"
if (org.apache.commons.lang3.StringUtils.isBlank(version)) {
version = v;
} else if (Double.valueOf(version) < Double.valueOf(v)) {
version = v;
}
}
}
}
} catch (Exception e) {
version ="";
}
// Update version to CameraAlgorithm
cameraAlgorithm.setAlgorithmVersion(version);

cameraAlgorithmService.save(cameraAlgorithm);
}

// Delete Alert Hour Segment Config
List<Long> reportAlgorithmIds = reportPeriodService.listAlgorithmId(camera.getId());
for(Long reportAlgorithmId: reportAlgorithmIds) {
//
for(String algorithmId: algorithmIds) {
if(algorithmId.equals(String.valueOf(reportAlgorithmId))) {
continue;
}
}
//
reportPeriodService.deleteByCameraAndAlgorithm(camera.getId(), reportAlgorithmId);
}

// Add Default Alert Hour Segment
for(String algorithmId: algorithmIds) {
List<ReportPeriod> reportPeriodList = reportPeriodService.listData(camera.getId(), Long.parseLong(algorithmId));
if(reportPeriodList.isEmpty()) {
//
ReportPeriod reportPeriod = new ReportPeriod();
reportPeriod.setCameraId(camera.getId());
reportPeriod.setAlgorithmId(Long.parseLong(algorithmId));
reportPeriod.setStartText("00:00");
reportPeriod.setStartTime(0);
reportPeriod.setEndText("23:59");
reportPeriod.setEndTime(2359);
reportPeriodService.save(reportPeriod);
}
}
//
// // Box Event
// if("2".equals(camera.getLocationType())) {
// Location boxInfo = locationService.findBox(camera.getLocationId());
// if(boxInfo!= null) {
// BoxEvent boxEvent = new BoxEvent();
// boxEvent.setEventType(1);
// boxEvent.setBoxId(boxInfo.getId());
// boxEvent.setCameraId(camera.getId());
// boxEvent.setCreatedAt(new Date());
// boxEventService.save(boxEvent);
//}
//}
}

private String markToImagePoints (String markPointsStr, float scaleRatio) {
JSONArray imagePointsGrp = new JSONArray(); // new Coordinate group Set combine
JSONArray markPointsGrp = JSON.parseArray(markPointsStr);
int grpSize = markPointsGrp.size();
for(int k = 0; k < grpSize; k++) {
JSONArray imagePoints = new JSONArray(); // new Coordinate group
JSONArray markPoints = markPointsGrp.getJSONArray(k);
int mpSize = markPoints.size();
for(int m = 0; m < mpSize; m++) {
JSONObject point = markPoints.getJSONObject(m);
float x = point.getFloatValue("x");
float y = point.getFloatValue("y");
float xNew = x * scaleRatio;
float yNew = y * scaleRatio;
//
JSONObject imagePoint = new JSONObject();
imagePoint.put("x", Float.valueOf(xNew).intValue());
imagePoint.put("y", Float.valueOf(yNew).intValue());
imagePoints.add(imagePoint);
}
imagePointsGrp.add(imagePoints);
}
return imagePointsGrp.toJSONString();
}

/**
* @param cameraId
* @param action
*/
@Override
public void updateActionById(Long cameraId, Integer action) {
LambdaUpdateWrapper<Camera> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(Camera::getAction, action);
updateWrapper.eq(Camera::getId, cameraId);
this.getBaseMapper().update(null, updateWrapper);
}

/**
*
* @param id
* @param running
*/
@Override
public void updateRunning1(Long id, Integer running, String msg) {
Camera camera = new Camera();
camera.setId(id);
if(running!= null) {
camera.setAiboxExecStatus(running == 1? 1000: 3000);
camera.setAiboxExecSend(running == 1? System.currentTimeMillis(): 0L);
camera.setRunning(running);
}
camera.setAiboxExecMsg(msg);
this.updateById(camera);

}

/**
*
* @return
*/
@Override
public Map<Long, String> toMap() {
List<Camera> cameraList = listData();
Map<Long, String> cameraMap = new HashMap<>();
for (Camera camera: cameraList) {
cameraMap.put(camera.getId(), camera.getName());
}
return cameraMap;
}

/**
*
* @return
*/
@Override
public List<Camera> listData() {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getName, Camera::getRunning, Camera::getAiboxExecStatus, Camera::getAiboxExecMsg, Camera::getLocationId, Camera::getFileName, Camera::getVideoCodec, Camera::getVideoWidth, Camera::getVideoHeight);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
List<Camera> cameraList = this.list(queryWrapper);
if(cameraList == null) {
return new ArrayList<>();
}
return cameraList;
}

/**
* Page Query
*
* @param pageObj
* @return
*/
@Override
public IPage<Camera> listPage(IPage<Camera> pageObj, Camera queryCamera) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
if(StrUtil.isNotBlank(queryCamera.getName())) {
queryWrapper.like(Camera::getName, queryCamera.getName());
}
if(queryCamera.getLocationId()!= null) {
queryWrapper.like(Camera::getLocationIds, queryCamera.getLocationId());
}
if (null!= queryCamera.getRunning() && queryCamera.getRunning() == 0) {
queryWrapper.eq(Camera::getRunning, CameraRunningState.CLOSED.getType());
} else if (null!= queryCamera.getRunning() && queryCamera.getRunning() == 1) {
queryWrapper.eq(Camera::getRunning, CameraRunningState.RUNNING.getType());
}
if("2".equals(queryCamera.getLocationType())){
queryWrapper.eq(Camera::getLocationType,queryCamera.getLocationType());
}else{
queryWrapper.ne(Camera::getLocationType,"2").or().isNull(Camera::getLocationType);
}
if(queryCamera.getQueryLocationIds()!= null && queryCamera.getQueryLocationIds().size() > 0) {
queryWrapper.in(Camera::getLocationId, queryCamera.getQueryLocationIds());
}

queryWrapper.orderByDesc(Camera::getRunning);
return this.page(pageObj, queryWrapper);
}

/**
* By Region Node Delete Camera
*
* @param locationId
*/
@Override
public void removeByLocation(Long locationId) {

}

/**
* Query Work Dynamic Camera
*
* @return
*/
@Override
public List<Camera> listActives() {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getName, Camera::getRtspUrl);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
queryWrapper.eq(Camera::getRunning, CameraRunningState.RUNNING.getType());
queryWrapper.eq(Camera::getRtspType, 0);
queryWrapper.last("limit 0, 10");

//
List<Camera> cameraList = this.list(queryWrapper);
if(cameraList == null) {
return new ArrayList<>();
}
return cameraList;
}

/**
* By Camera Name Query
*
* @param cameraName
* @return
*/
@Override
public Camera getByName(String cameraName) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getName, cameraName);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
queryWrapper.last("limit 0, 1");
return this.getOne(queryWrapper);
}

/**
* By Run Status Count Total
*
* @param runState
* @return
*/
@Override
public Integer getCountByRunState(Integer runState) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, 0);
if(runState >= 0) {
queryWrapper.eq(Camera::getRunning, runState);
}
return this.count(queryWrapper);
}

/**
* Update Camera Play Put Status
*
* @param playCameraIds
*/
@Override
public void updateVideoPlays(List<Long> playCameraIds) {
//
}

/**
* Page Query, By Video Play Put ID in Line Sort
*
* @param pageObj
* @return
*/
@Override
public IPage<Camera> listPageAndOrderVideoPlay(IPage<Camera> pageObj) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
queryWrapper.orderByDesc(Camera::getVideoPlay);
return this.page(pageObj, queryWrapper);
}

/**
* Deleted Camera Delete Relate Algorithm, System Init Hour Call One sub
*/
@Override
public void removeDeleted() {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.DISABLED.getType());
//
List<Camera> cameraList = this.list(queryWrapper);
if(cameraList == null) {
return;
}
//
for(Camera camera: cameraList) {
cameraAlgorithmService.deleteByCamera(camera.getId());
}
}

/**
* By Box id Query
*
* @param boxId
* @return
*/
@Override
public List<Camera> listByBoxId(Long boxId) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getLocationId, boxId);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
//
List<Camera> cameras = this.list(queryWrapper);
if(cameras == null) {
return new ArrayList<>();
}
return cameras;
}

@Override
public List<Camera> listLikeName(String cameraName) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.like(Camera::getName, cameraName);
return this.list(queryWrapper);
}

/**
* By Stream Address Query Camera Whether Exist
*
* @param rtspUrl
* @return
*/
@Override
public Camera getByRtspUrl(String rtspUrl) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getRtspUrl, rtspUrl);
queryWrapper.eq(Camera::getState, 0);
queryWrapper.last("limit 0, 1");
return this.getOne(queryWrapper);
}

/**
* Modify Camera Inference Status for Offline
*
* @param date
*/
@Override
public void updateOffline(Date date) {
LambdaUpdateWrapper<Camera> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(Camera::getAiboxExecStatus, 3000);
updateWrapper.set(Camera::getAiboxExecMsg,"Heartbeat Stop");
updateWrapper.lt(Camera::getAiboxExecTime, date);
updateWrapper.eq(Camera::getRunning, 1);
updateWrapper.eq(Camera::getAiboxExecStatus, 1000);
this.update(updateWrapper);
}

/**
* By rtsp Address Update Code Info
*
* @param videoCodecName
* @param rtspUrl
*/
@Override
public void updateVideoCode(String videoCodecName, String rtspUrl) {
LambdaUpdateWrapper<Camera> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(Camera::getVideoCodec, videoCodecName);
updateWrapper.eq(Camera::getRtspUrl, rtspUrl);
this.update(updateWrapper);
}

/**
* new Version Save Camera
*
* @param modifyVo
*/
@Transactional(rollbackFor = Exception.class)
@Override
public Long saveSubmit(CameraModifyVo modifyVo, Location location) throws Exception {
boolean updateGb = false;
// Save Basic Info
Camera camera = new Camera();
BeanUtils.copyProperties(modifyVo, camera);
camera.setAlgoCount(modifyVo.getAlgorithms().size());
if(modifyVo.getId() == null) {
camera.setState(CommState.NORMAL.getType());
camera.setRunning(CameraRunningState.CLOSED.getType());
camera.setAction(CameraAction.ACTION_UPD.getType());
camera.setCreatedAt(new Date());
camera.setLocationType("2");
camera.setAiboxExecTime(new Date());
camera.setAiboxExecStatus(3000);
camera.setSoundColumnId(0L);

// Region Set
if(location!= null) {
camera.setLocationIds(location.getParentIds() +"/"+ location.getId());
}

// GB Standard Channel Process
if(modifyVo.getGbId()!= null && modifyVo.getGbId() > 0) {
camera.setSourceType(1);
camera.setGbId(modifyVo.getGbId());
camera.setMediaServerId(modifyVo.getGbMediaServerId());

updateGb = true;

// Set Media Node
Long mediaServerId = this.getMinMediaServer();
camera.setMediaServerId(mediaServerId);
} else {
camera.setSourceType(0);
camera.setGbId(0L);
camera.setMediaServerId(0L);
}
} else {
camera.setLocationType("2");
camera.setUpdatedAt(new Date());
}
this.saveOrUpdate(camera);

// Save Camera and Algorithm Relate Info
cameraAlgorithmService.deleteByCamera(camera.getId());
for(CameraAlgorithmModifyVo cameraAlgorithmModifyVo: modifyVo.getAlgorithms()) {
CameraAlgorithm cameraAlgorithm = new CameraAlgorithm();
cameraAlgorithm.setCameraId(camera.getId());
cameraAlgorithm.setAlgorithmId(cameraAlgorithmModifyVo.getAlgorithmId());
cameraAlgorithm.setConfidence(cameraAlgorithmModifyVo.getAlgorithmConf());
cameraAlgorithm.setMarkPoints((cameraAlgorithmModifyVo.getDrawBoxs() == null || cameraAlgorithmModifyVo.getDrawBoxs().isEmpty())?"": JSON.toJSONString(cameraAlgorithmModifyVo.getDrawBoxs()));
cameraAlgorithm.setImagePoints((cameraAlgorithmModifyVo.getDrawBoxs() == null || cameraAlgorithmModifyVo.getDrawBoxs().isEmpty())?"": JSON.toJSONString(cameraAlgorithmModifyVo.getDrawBoxs()));
//if ("person_tracker".equals(algorithm.getNameEn()) && StrUtil.isNotBlank(lineMarkPoints)) {
cameraAlgorithm.setLineMarkPoints((cameraAlgorithmModifyVo.getDrawLines() == null || cameraAlgorithmModifyVo.getDrawLines().isEmpty())?"": JSON.toJSONString(cameraAlgorithmModifyVo.getDrawLines()));
cameraAlgorithm.setLineImagePoints((cameraAlgorithmModifyVo.getDrawLines() == null || cameraAlgorithmModifyVo.getDrawLines().isEmpty())?"": JSON.toJSONString(cameraAlgorithmModifyVo.getDrawLines()));
//}

// // Algorithm Version
// String version ="";
// List<CameraAlgorithm> caList = cameraAlgorithmService.listByAlgorithmAndBoxId(cameraAlgorithmModifyVo.getAlgorithmId(), camera.getLocationId());
// for (CameraAlgorithm ca: caList) {
// if (StrUtil.isNotBlank(ca.getAlgorithmVersion())) {
// version = ca.getAlgorithmVersion();
// break;
//}
//}
// // like Result according Is Empty, Get Local File most new version
// try {
// if (StrUtil.isBlank(version)) {
// Algorithm algorithm = algorithmService.getById(cameraAlgorithmModifyVo.getAlgorithmId());
// if(algorithm!= null && StrUtil.isNotBlank(algorithm.getPlatform()) && StrUtil.isNotBlank(algorithm.getNameEn())) {
// String path = MODEL_DIR + algorithm.getPlatform() +"/"+ algorithm.getNameEn();
// List<Path> fileList = Files.list(Paths.get(path)).collect(Collectors.toList());
// for (Path file: fileList) {
// if (file.getFileName().toString().endsWith(".zip")) {
// String[] parts = file.getFileName().toString().split("-");
// String v = parts[2].replace(".zip",""); //"1.0"
// if (StrUtil.isBlank(version)) {
// version = v;
//} else if (Double.valueOf(version) < Double.valueOf(v)) {
// version = v;
//}
//}
//}
//}
//}
//} catch (Exception e) {
// version ="";
//}
// cameraAlgorithm.setAlgorithmVersion(version);
cameraAlgorithm.setAlgorithmVersion("0");
cameraAlgorithm.setAutoPush(cameraAlgorithmModifyVo.getAutoPush());
cameraAlgorithmService.save(cameraAlgorithm);

// Management Box Device and Algorithm Version
BoxVersion boxVersion = boxVersionService.getData(modifyVo.getLocationId(), cameraAlgorithmModifyVo.getAlgorithmId());
if(boxVersion == null) {
Algorithm algorithm = algorithmService.getById(cameraAlgorithmModifyVo.getAlgorithmId());
if(algorithm!= null) {
String path = FileUtils.pathTo(MODEL_DIR +"/"+ (location.getPlatform() == null?"a": location.getPlatform()) +"/"+ algorithm.getNameEn() +"/");
List<String> files = FileUtils.listFiles(path, algorithm.getNameEn(), Collections.singletonList("zip"));
if(!files.isEmpty()) {
Collections.sort(files);
String ver = FileUtils.getVersion(files.get(files.size() - 1));

BoxVersion boxVersion1 = new BoxVersion();
boxVersion1.setBoxId(modifyVo.getLocationId());
boxVersion1.setAlgorithmId(cameraAlgorithmModifyVo.getAlgorithmId());
boxVersion1.setVersionNum(ver);
boxVersionService.save(boxVersion1);
}
}
}

// Alert Hour Segment
reportPeriodService.deleteByCameraAndAlgorithm(camera.getId(), cameraAlgorithmModifyVo.getAlgorithmId());
if(cameraAlgorithmModifyVo.getAlarmTimes() == null || cameraAlgorithmModifyVo.getAlarmTimes().isEmpty()) {
ReportPeriod reportPeriod = new ReportPeriod();
reportPeriod.setCameraId(camera.getId());
reportPeriod.setAlgorithmId(cameraAlgorithmModifyVo.getAlgorithmId());
reportPeriod.setStartText("00:00");
reportPeriod.setStartTime(0);
reportPeriod.setEndText("23:59");
reportPeriod.setEndTime(2359);
reportPeriodService.save(reportPeriod);
} else {
for(CameraAlgorithmAlarmTimeModifyVo algorithmAlarmTimeModifyVo: cameraAlgorithmModifyVo.getAlarmTimes()) {
ReportPeriod reportPeriod = new ReportPeriod();
reportPeriod.setCameraId(camera.getId());
reportPeriod.setAlgorithmId(cameraAlgorithmModifyVo.getAlgorithmId());
reportPeriod.setStartText(algorithmAlarmTimeModifyVo.getStartTime());
reportPeriod.setStartTime(algorithmAlarmTimeModifyVo.getStartTime() == null? 0: Integer.parseInt(algorithmAlarmTimeModifyVo.getStartTime().replace(":","")));
reportPeriod.setEndText(algorithmAlarmTimeModifyVo.getEndTime());
reportPeriod.setEndTime(algorithmAlarmTimeModifyVo.getEndTime() == null? 0: Integer.parseInt(algorithmAlarmTimeModifyVo.getEndTime().replace(":","")));
reportPeriodService.save(reportPeriod);
}
}

// Relate Social Platform Push Config
socialConfigService.deleteByCamera(camera.getId(), cameraAlgorithmModifyVo.getAlgorithmId());
List<Long> socials = cameraAlgorithmModifyVo.getSocials();
if(socials!= null &&!socials.isEmpty()) {
for(Long socialId: socials) {
SocialConfig socialConfig = new SocialConfig();
socialConfig.setAlgorithmId(cameraAlgorithmModifyVo.getAlgorithmId());
socialConfig.setCameraId(camera.getId());
socialConfig.setSocialId(socialId);
socialConfigService.save(socialConfig);
}
}
}

// Update GB Standard Channel Corresponding Camera ID
if(updateGb) {
GBDeviceChannel gbDeviceChannel = new GBDeviceChannel();
gbDeviceChannel.setId(modifyVo.getGbId());
gbDeviceChannel.setCameraId(camera.getId());
gbDeviceChannelService.updateById(gbDeviceChannel);
}

/** must move out the Transaction, will import Cause Database Lock table modify by zhou 2025-10-17
// Call Box Service
Camera newCamera = getById(camera.getId());
if(newCamera.getRunning()!= null && newCamera.getRunning() == 1) {
CameraAddResponse response = messageSendHandler.sendAddCamera(location, newCamera);
if(!response.isStatus()) {
throw new BizException(response.getMsg());
} else {
this.updateRunning1(newCamera.getId(), 1,"Run in");
}
}
*/
return camera.getId();
}

/**
* Send Camera to Box
* @param camera
* @param location
* @return
*/
public String sendCameraToDevice(Camera camera, Location location) {
CameraAddResponse response = messageSendHandler.sendAddCamera(location, camera);
if(!response.isStatus()) {
return response.getMsg();
} else {
this.updateRunning1(camera.getId(), 1,"Run in");
return null;
}
}

/**
* By Box ids Query
*
* @param locationIds
* @return
*/
@Override
public List<Camera> listByLocationIds(List<Long> locationIds) {
if(locationIds == null || locationIds.isEmpty()) {
return new ArrayList<>();
}
//
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(Camera::getLocationId, locationIds);
queryWrapper.eq(Camera::getState, 0);
return this.list(queryWrapper);
}

/**
* By Box ids Query
*
* @param locationIds
* @return
*/
@Override
public List<Camera> listLessByLocationIds(List<Long> locationIds) {
if(locationIds == null || locationIds.isEmpty()) {
return new ArrayList<>();
}
//
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getName, Camera::getLocationType);
queryWrapper.in(Camera::getLocationId, locationIds);
queryWrapper.eq(Camera::getState, 0);
return this.list(queryWrapper);
}

/**
* Page Query, By Box ids Query
*
* @param page
* @param limit
* @param locationIds
* @return
*/
@Override
public IPage<Camera> listPageActivesV2(Integer page, Integer limit, List<Long> locationIds) {
if(locationIds == null || locationIds.isEmpty()) {
return new Page<Camera>(page, limit);
}
//
IPage<Camera> pageObj = new Page<>(page, limit);
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getName, Camera::getRtspUrl);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
queryWrapper.eq(Camera::getRunning, CameraRunningState.RUNNING.getType());
queryWrapper.in(Camera::getLocationId, locationIds);
return this.page(pageObj, queryWrapper);
}

/**
* By Box id Query Current Box Inference Path Number
*
* @param locationId
* @return
*/
@Override
public int getInferNum(Long locationId) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getLocationId, locationId);
queryWrapper.eq(Camera::getRunning, 1);
queryWrapper.eq(Camera::getState, 0);
return this.count(queryWrapper);
}

/**
* Execute Rollback Operation
*
* @param cameraDb
* @param cameraAlgorithmsDb
*/
@Override
public void updateRollback(Camera cameraDb, List<CameraAlgorithm> cameraAlgorithmsDb, List<ReportPeriod> reportPeriodsDb) {
if(cameraDb == null) {
return;
}

// Execute part part Rollback Operation
Camera camera = new Camera();
camera.setId(cameraDb.getId());
camera.setRtspUrl(cameraDb.getRtspUrl());
camera.setIntervalTime(cameraDb.getIntervalTime());
camera.setAlarmInterval(cameraDb.getAlarmInterval());
this.updateById(camera);

// Rollback Bind Algorithm
if(cameraAlgorithmsDb!= null) {
cameraAlgorithmService.deleteByCamera(cameraDb.getId());

for(CameraAlgorithm cameraAlgorithm: cameraAlgorithmsDb) {
cameraAlgorithmService.save(cameraAlgorithm);
}
}

// Rollback Bind Alert Time
if(reportPeriodsDb!= null) {
reportPeriodService.deleteByCamera(cameraDb.getId());

for(ReportPeriod reportPeriod: reportPeriodsDb) {
reportPeriodService.save(reportPeriod);
}
}
}

/**
* Query Camera
*
* @return
*/
@Override
public List<Camera> listData2() {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getName, Camera::getRunning);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
List<Camera> datas = this.list(queryWrapper);
return datas == null? new ArrayList<>(): datas;
}

/**
* Query Camera and Box close System List
*
* @return
*/
@Override
public List<Camera> listCameraAndBox() {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getLocationId);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
List<Camera> datas = this.list(queryWrapper);
return datas == null? new ArrayList<>(): datas;
}

/**
* Query most small Load Media Node
*
* @param mediaServerId
* @return
*/
@Override
public int countByMediaServer(Long mediaServerId) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
queryWrapper.eq(Camera::getMediaServerId, mediaServerId);
return this.count(queryWrapper);
}

public Long getMinMediaServer() {
// add by zhou 2025-07-22, Media Node Temp Hour no Use, Direct connect Back 0 Immediate can
//if(true) {
// return 0L;
//}
List<MediaServer> mediaServers = mediaServerService.list();
if(mediaServers == null || mediaServers.isEmpty()) {
return 0L;
}

Map<Long, Integer> countMap = new HashMap<>();
for(MediaServer mediaServer: mediaServers) {
countMap.put(mediaServer.getId(), 0);
}

//
QueryWrapper<Camera> queryWrapper = new QueryWrapper<Camera>();
queryWrapper.select("media_server_id, count(1) as cnt");
queryWrapper.eq("state", 0);
queryWrapper.gt("media_server_id", 0);
queryWrapper.groupBy("media_server_id");
queryWrapper.orderByAsc("cnt");
List<Map<String, Object>> datas = this.listMaps(queryWrapper);
if(!ArrayUtil.isEmpty(datas)) {
for(Map<String, Object> data: datas) {
Long id = Convert.toLong(data.get("media_server_id"));
Integer cnt = Convert.toInt(data.get("cnt"));
countMap.put(id, cnt);
}
}
return countMap.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0L);
}

/**
* By Media Node Query Camera List
*
* @param mediaServerId
*/
@Override
public List<Camera> listByMediaServerId(Long mediaServerId) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
queryWrapper.eq(Camera::getRunning, 1);
queryWrapper.eq(Camera::getMediaServerId, mediaServerId);
queryWrapper.eq(Camera::getSourceType, 0);
List<Camera> datas = this.list(queryWrapper);
return datas == null? new ArrayList<>(): datas;
}

/**
* By Box ID Query List
*
* @param locationIds
* @param cameraName
* @return
*/
@Override
public List<Camera> listData5(List<Long> locationIds, String cameraName) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getName, Camera::getLocationId);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
if(ObjectUtil.isNotEmpty(locationIds)) {
queryWrapper.in(Camera::getLocationId, locationIds);
}
if(StrUtil.isNotBlank(cameraName)) {
queryWrapper.like(Camera::getName, cameraName);
}
List<Camera> cameraList = this.list(queryWrapper);
return cameraList == null? new ArrayList<>(): cameraList;
}

/**
* By Camera Name Blur Query
*
* @param name
* @return
*/
@Override
public List<Long> listIdByName(String name) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.like(Camera::getName, name);
List<Camera> cameraList = this.list(queryWrapper);
return cameraList.stream().map(Camera::getId).collect(Collectors.toList());
}

/**
* Page Query
*
* @param listVo
* @return
*/
@Override
public IPage<Camera> listPageV2(CameraListVo listVo) {
IPage<Camera> cameraIPage = new Page<>(listVo.getPage(), listVo.getLimit());
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());

// By Name Blur Query Camera
if(StrUtil.isNotBlank(listVo.getName())) {
queryWrapper.like(Camera::getName, listVo.getName());
}

// By Refer Fixed Box ID Query Camera
if(listVo.getLocationId()!= null) {
queryWrapper.eq(Camera::getLocationId, listVo.getLocationId());
}

// By multi Box ID Query Camera
if(ObjectUtil.isNotEmpty(listVo.getLocationIds())) {
queryWrapper.in(Camera::getLocationId, listVo.getLocationIds());
}

// By Refer Fixed Camera Query Camera
if(ObjectUtil.isNotEmpty(listVo.getCameraIds())) {
queryWrapper.in(Camera::getId, listVo.getCameraIds());
}

if(ObjectUtil.isEmpty(listVo.getSortBys())) {
queryWrapper.orderByDesc(Camera::getRunning);
} else {
for(SortByVo byVo: listVo.getSortBys()) {
if("algoCount".equalsIgnoreCase(byVo.getSortBy())) {
if("asc".equalsIgnoreCase(byVo.getSortOrder())) {
queryWrapper.orderByAsc(Camera::getAlgoCount);
} else {
queryWrapper.orderByDesc(Camera::getAlgoCount);
}
}

if("heart".equalsIgnoreCase(byVo.getSortBy())) {
if("asc".equalsIgnoreCase(byVo.getSortOrder())) {
queryWrapper.orderByAsc(Camera::getAiboxExecTime);
} else {
queryWrapper.orderByDesc(Camera::getAiboxExecTime);
}
}
}
}
return this.page(cameraIPage, queryWrapper);
}

/**
* Query All
*
* @param listVo
* @return
*/
@Override
public List<Camera> listAllV2(CameraListVo listVo) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());

// By Name Blur Query Camera
if(StrUtil.isNotBlank(listVo.getName())) {
queryWrapper.like(Camera::getName, listVo.getName());
}

// By Refer Fixed Box ID Query Camera
if(listVo.getLocationId()!= null) {
queryWrapper.eq(Camera::getLocationId, listVo.getLocationId());
}

// By multi Box ID Query Camera
if(ObjectUtil.isNotEmpty(listVo.getLocationIds())) {
queryWrapper.in(Camera::getLocationId, listVo.getLocationIds());
}

// By Refer Fixed Camera Query Camera
if(ObjectUtil.isNotEmpty(listVo.getCameraIds())) {
queryWrapper.in(Camera::getId, listVo.getCameraIds());
}

if(ObjectUtil.isEmpty(listVo.getSortBys())) {
queryWrapper.orderByDesc(Camera::getRunning);
} else {
for(SortByVo byVo: listVo.getSortBys()) {
if("algoCount".equalsIgnoreCase(byVo.getSortBy())) {
if("asc".equalsIgnoreCase(byVo.getSortOrder())) {
queryWrapper.orderByAsc(Camera::getAlgoCount);
} else {
queryWrapper.orderByDesc(Camera::getAlgoCount);
}
}

if("heart".equalsIgnoreCase(byVo.getSortBy())) {
if("asc".equalsIgnoreCase(byVo.getSortOrder())) {
queryWrapper.orderByAsc(Camera::getAiboxExecTime);
} else {
queryWrapper.orderByDesc(Camera::getAiboxExecTime);
}
}
}
}
return this.list(queryWrapper);
}

/**
* Query not Contain Refer Fixed Camera ID Data
*
* @param cameraIds
* @param page
* @param limit
* @return
*/
@Override
public IPage<Camera> listPageNotContainCameraId(List<Long> cameraIds, String name, Integer page, Integer limit) {
IPage<Camera> cameraIPage = new Page<>(page, limit);

LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
if(StrUtil.isNotBlank(name)) {
queryWrapper.like(Camera::getName, name);
}
queryWrapper.notIn(Camera::getId, cameraIds);
return this.page(cameraIPage, queryWrapper);
}

/**
* Query not Contain Refer Fixed Camera ID Data
*
* @param cameraIds
* @return
*/
@Override
public List<Camera> getCountNotContainCameraId(List<Long> cameraIds, String name) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
if(StrUtil.isNotBlank(name)) {
queryWrapper.like(Camera::getName, name);
}
queryWrapper.notIn(Camera::getId, cameraIds);
return this.list(queryWrapper);
}

/**
* By The belong Organization and The belong Group Query Camera List
*
* @param locationIds
* @param cameraIds
* @param running
* @return
*/
@Override
public List<Camera> listData7(List<Long> locationIds, List<Long> cameraIds, Integer running) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getName);
queryWrapper.eq(Camera::getState, CommState.NORMAL.getType());
if(running!= null && running == 1) {
queryWrapper.eq(Camera::getRunning, 1);
}
if(ObjectUtil.isNotEmpty(locationIds)) {
queryWrapper.in(Camera::getLocationId, locationIds);
}
if(ObjectUtil.isNotEmpty(cameraIds)) {
queryWrapper.in(Camera::getId, cameraIds);
}
return this.list(queryWrapper);
}

/**
* By The belong Organization, The belong Group, Check select Camera Query Camera Page List
*
* @param page
* @param limit
* @param locationIds
* @param cameraIds
* @param running
* @return
*/
@Override
public IPage<Camera> listPage9(Integer page, Integer limit, List<Long> locationIds, List<Long> cameraIds, Integer running) {
List<Long> defaultCameraIds = Arrays.asList(
1924851696229519362L,
1924853804974575618L,
1924855496851951618L,
1949727346606051329L,
1949728508008828930L,
1949728952265314306L,
1949729101322489857L,
1949730242433224705L,
1950745996410589186L
);

IPage<Camera> iPage = new Page<>(page, limit);
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Camera::getId, Camera::getName)
.eq(Camera::getState, CommState.NORMAL.getType());
CameraDTO cameraDto = new CameraDTO();
cameraDto.setRunning(running);
cameraDto.setLocationIdList(locationIds);
cameraDto.setIdList(cameraIds);
cameraDto.setDefaultIdList(defaultCameraIds);
return baseMapper.defaultPage(iPage, cameraDto);
}

/**
* By IP Address Query Camera
*
* @param ipAddress
* @return
*/
@Override
public Camera getByIp(String ipAddress) {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.like(Camera::getRtspUrl, ipAddress);
queryWrapper.eq(Camera::getState, 0);
return this.getOne(queryWrapper, false);
}

/**
* Query Status Normal and Enable, But is Execute Exception Camera
*
* @return
*/
@Override
public List<Camera> listException() {
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Camera::getState, 0);
queryWrapper.eq(Camera::getRunning, 1);
queryWrapper.eq(Camera::getAiboxExecStatus, 3000);
return this.list(queryWrapper);
}
}
