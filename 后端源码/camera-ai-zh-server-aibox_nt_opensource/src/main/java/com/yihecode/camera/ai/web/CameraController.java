package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.enums.CameraAction;
import com.yihecode.camera.ai.enums.CameraRunningState;
import com.yihecode.camera.ai.enums.CommState;
import com.yihecode.camera.ai.javacv.DecodeRtspUlr;
import com.yihecode.camera.ai.javacv.TakePhoto;
import com.yihecode.camera.ai.javacv.VideoInfo;
import com.yihecode.camera.ai.media.MediaRestfulService;
import com.yihecode.camera.ai.media.MediaService;
import com.yihecode.camera.ai.netty.MessageSendHandler;
import com.yihecode.camera.ai.netty.data.*;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.service.map.MapObjectService;
import com.yihecode.camera.ai.utils.*;

import java.util.*;
import java.util.stream.Collectors;

import com.yihecode.camera.ai.web.dto.CameraCassInfoDTO;
import com.yihecode.camera.ai.web.dto.CameraLocationDTO;
import com.yihecode.camera.ai.web.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
* Camera Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Camera Management")
@Slf4j
@SaCheckLogin
@Controller
@RequestMapping({"/camera"})
public class CameraController {

    //
@Autowired
private CameraService cameraService;

//
@Autowired
private AlgorithmService algorithmService;

//
@Autowired
private ReportPeriodService reportPeriodService;

//
@Autowired
private CameraAlgorithmService cameraAlgorithmService;

//
@Autowired
private LocationService locationService;

//
@Autowired
private TakePhoto takePhoto;

//
@Autowired
private ConfigService configService;

//
@Resource
private MediaService mediaService;

//
@Resource
private AccountService accountService;

//
@Resource
private ApDepartService apDepartService;

//
@Resource
private MessageSendHandler messageSendHandler;

//
@Resource
private SocialConfigService socialConfigService;

@Resource
private MapObjectService mapObjectService;

@Autowired
private CameraGroupItemService cameraGroupItemService;

@Autowired
private MediaRestfulService mediaRestfulService;

@Autowired
private ProjectConfig projectConfig;

@Value("${dataModelsDir}")
public String MODEL_DIR;

/**
* Camera Detail
* @param id
* @return
*/
@ApiOperation("Query Camera Detail Data")
@ApiImplicitParam(name ="id", value ="Data ID")
@PostMapping({"/detail"})
@ResponseBody
public JsonResult detail(Long id) {
Camera camera = cameraService.getById(id);
if(camera == null) {
return JsonResultUtils.fail("find not to Data");
}
// Hide Camera Address
camera.setRtspUrl("");
return JsonResultUtils.success(camera);
}

@PostMapping("listData2")
@ResponseBody
public JsonResult<?> listData2() {
List<Camera> cameras = cameraService.listData2();
return JsonResultUtils.success(cameras);
}

/**
* Query Data List
* @return
*/
@ApiOperation("Query Camera List Data")
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping({"/listLessData"})
@ResponseBody
public PageResult<List<CameraCassInfoDTO>> listLessData() {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return PageResultUtils.success(0L, new ArrayList<>());
}
//
List<Long> queryLocationIds = new ArrayList<>();
if(Integer.valueOf(1).equals(account.getIsSuper())) {// super Level Management member
List<Location> locations = locationService.listByType("2");
for(Location location: locations) {
queryLocationIds.add(location.getId());
}
} else {// Non Management member
List<Long> departIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
List<Long> locationIds = locationService.getLocationIdsByDeparts(departIds);
if(locationIds!= null) {
queryLocationIds.addAll(locationIds);
}
}
// not has Corresponding Box Info
if(queryLocationIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>());
}
// By Box ids Query Camera
List<Camera> cameraList = cameraService.listByLocationIds(queryLocationIds);

//
List<CameraCassInfoDTO> dtos = new ArrayList<>();
for (Camera camera: cameraList) {
if(camera.getState() == null || camera.getState()!= 0) {
continue;
}

CameraCassInfoDTO dto = new CameraCassInfoDTO();
dto.setId(camera.getId());
dto.setName(camera.getName());
dto.setType(2);
dtos.add(dto);
}
return PageResultUtils.success(null, dtos);
}

/**
* Query Data List
* @return
*/
@ApiOperation("Query Camera List Data")
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping({"/listData"})
@ResponseBody
public PageResult<?> listData() {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return PageResultUtils.success(0L, new ArrayList<>());
}
//
List<Long> queryLocationIds = new ArrayList<>();
if(Integer.valueOf(1).equals(account.getIsSuper())) {// super Level Management member
List<Location> locations = locationService.listByType("2");
for(Location location: locations) {
queryLocationIds.add(location.getId());
}
} else {// Non Management member
List<Long> departIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
List<Long> locationIds = locationService.getLocationIdsByDeparts(departIds);
if(locationIds!= null) {
queryLocationIds.addAll(locationIds);
}
}
// not has Corresponding Box Info
if(queryLocationIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>());
}
// By Box ids Query Camera
List<Camera> cameraList = cameraService.listByLocationIds(queryLocationIds);

//
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}

//
Map<Long, String> algorithmNames = new HashMap<>();
for(Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());
}

//
List<Camera> dataList = new ArrayList<>();
for (Camera camera: cameraList) {
if(camera.getState() == null || camera.getState()!= 0) {
continue;
}

//
List<String> nameList = new ArrayList<>();
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByCamera(camera.getId());
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
//
String algorithmName = algorithmNames.get(cameraAlgorithm.getAlgorithmId());
if(algorithmName == null) {
continue;
}

//
List<String> periods = new ArrayList<>();
List<ReportPeriod> reportPeriodList = reportPeriodService.listData(camera.getId(), cameraAlgorithm.getAlgorithmId());
for(ReportPeriod reportPeriod: reportPeriodList) {
periods.add(reportPeriod.getStartText() +"-"+ reportPeriod.getEndText());
}

nameList.add(algorithmName +"("+ String.join(",", periods) +")");

// Hide Camera Address
camera.setRtspUrl("");
}
// camera.setAlgorithmNames(String.join("|", nameList));
camera.setAlgorithmNames(nameList.size() +"");

//camera.setAlgorithmNames(this.cameraAlgorithmService.getNames(camera.getId()));
dataList.add(camera);
}
return PageResultUtils.success(null, dataList);
}

/**
* Page Query Data List
* @param page
* @param limit
* @param name
* @param locationId
* @return
*/
@ApiOperation("Query Camera Pagination Data List")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size"),
@ApiImplicitParam(name ="name", value ="Camera Name"),
@ApiImplicitParam(name ="locationId", value ="Region ID"),
@ApiImplicitParam(name ="locationType", value ="Data Source 1 Camera Management 2 Box Management"),
@ApiImplicitParam(name ="departId", value ="belong belong Department ID")
})
@SaCheckPermission(value = {"edgePlatform-boxManagement","box-algorithm-overview"}, mode = SaMode.OR)
@PostMapping("listPage")
@ResponseBody
public PageResult<List<Camera>> listPage(@RequestParam(defaultValue ="1") Integer page,
@RequestParam(defaultValue ="10") Integer limit,
String name,
Long locationId,
String locationType,
Long departId) {
// Determine Whether super Level Management member
List<Long> queryLocationIds = new ArrayList<>();
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null && account.getIsSuper() == 1) {
// super Level Management member, Query All
if(locationId!= null) {// 55555 table show no belong belong Organization
queryLocationIds.add(locationId); // Select Query Box
}
//
if(departId!= null && departId == 9999L) {// Point Click no belong belong Organization Node
List<Long> locationIdsByNonDeparts = locationService.getLocationIdsByNonDeparts();
if(locationIdsByNonDeparts!= null &&!locationIdsByNonDeparts.isEmpty()) {
queryLocationIds.addAll(locationIdsByNonDeparts);
}
} else if (departId!= null) {
List<Long> departIds = apDepartService.getCurrentAndChildIds(departId); // By Select Department Query Current Department and child Department
List<Long> locationIdsByDeparts = locationService.getLocationIdsByDeparts(departIds); // By Department Node Query All Box Node
if(locationIdsByDeparts == null) {// Query
return PageResultUtils.success(0L, new ArrayList<>());
}
//
if(locationId!= null) {
queryLocationIds.add(locationId); // Select Query Box
} else {
queryLocationIds.addAll(locationIdsByDeparts); // Select belong belong Department and child Department All Box
}
}

} else {
Long departIdByAccount = account.getDepartId();
List<Long> departIds = apDepartService.getCurrentAndChildIds(departIdByAccount); // By User belong belong Department Query Current Department and child Department
if(departId!= null &&!departIds.contains(departId)) {
return PageResultUtils.success(0L, new ArrayList<>()); // Select Department not again Current User belong belong Department and child Department
}
if(departId!= null) {
departIds = apDepartService.getCurrentAndChildIds(departId); // By Select belong belong Department Query and child Department
if(departIds == null || departIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>()); // Select Department not again Current User belong belong Department and child Department
}
}
List<Long> locationIdsByDeparts = locationService.getLocationIdsByDeparts(departIds); // By Department Node Query All Box Node
if(locationIdsByDeparts == null) {// Query
return PageResultUtils.success(0L, new ArrayList<>());
}
//
if(locationId!= null &&!locationIdsByDeparts.contains(locationId)) {
return PageResultUtils.success(0L, new ArrayList<>()); // Query Box not In Current User belong belong Department and child Department in
}
//
if(locationId!= null) {
queryLocationIds.add(locationId); // Select Query Box
} else {
queryLocationIds.addAll(locationIdsByDeparts); // Select belong belong Department and child Department All Box
}
}

IPage<Camera> pageObj = new Page<>(page, limit);
Camera queryCamera = new Camera();
queryCamera.setName(name);
//queryCamera.setLocationId(locationId);
queryCamera.setQueryLocationIds(queryLocationIds);
queryCamera.setLocationType(locationType);
IPage<Camera> pageResult = cameraService.listPage(pageObj, queryCamera);

//
List<Camera> records = pageResult.getRecords();
if(records == null) {
records = new ArrayList<>();
}

//
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}

//
Map<Long, String> algorithmNames = new HashMap<>();
for(Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());
}

Set<Long> locationIds = records.stream().map(Camera::getLocationId).collect(Collectors.toSet());
Map<Long, String> locationNames = locationService.getNameByIds(locationIds);

//
List<Camera> dataList = new ArrayList<>();
for (Camera camera: records) {
//
if(camera.getState() == null || camera.getState()!= 0) {
continue;
}

// Data Convert One sub
if(camera.getRtspType() == null) {
camera.setRtspType(0);
}

//
List<String> nameList = new ArrayList<>();
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByCamera(camera.getId());
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
//
String algorithmName = algorithmNames.get(cameraAlgorithm.getAlgorithmId());
if(algorithmName == null) {
continue;
}

//
List<String> periods = new ArrayList<>();
List<ReportPeriod> reportPeriodList = reportPeriodService.listData(camera.getId(), cameraAlgorithm.getAlgorithmId());
for(ReportPeriod reportPeriod: reportPeriodList) {
periods.add(reportPeriod.getStartText() +"-"+ reportPeriod.getEndText());
}

nameList.add(algorithmName +"("+ String.join(",", periods) +")");
}
//camera.setAlgorithmNames(String.join("|", nameList));
camera.setAlgorithmNames(nameList.size() +"");

//camera.setAlgorithmNames(this.cameraAlgorithmService.getNames(camera.getId()));

camera.setLocationName(locationNames.getOrDefault(camera.getLocationId(),"Default"));

dataList.add(camera);

locationIds.add(camera.getLocationId());

// Camera User and Password Encrypt Process
camera.setRtspUrl(DecodeRtspUlr.processSensitive(camera.getRtspUrl()));
}

return PageResultUtils.success(pageResult.getTotal(), dataList);
}

/**
* Page Query Data List
* @param listVo
* @return
*/
@ApiOperation("Query Camera Pagination Data List V2")
@SaCheckPermission(value = {"edgePlatform-boxManagement","box-algorithm-overview"}, mode = SaMode.OR)
@PostMapping("v2/listPage")
@ResponseBody
public PageResult<List<Camera>> listPageV2(@RequestBody CameraListVo listVo) {
// this Two Condition not should the same Hour Exist, departId and locationId Two Condition not can same Hour out current
Long departId = listVo.getDepartId();

// Query Current Account belong belong Department
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return PageResultUtils.success(0L, new ArrayList<>());
}

// Query User belong belong Department & child Department
List<Long> userDepartIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
boolean isSuper = false;
if(account.getIsSuper()!= null && account.getIsSuper() == 1) {
isSuper = true;
}

// has Department ID Condition, rule By Department Query belong belong Box, again by belong belong Box Remove Query Camera
if(departId!= null && departId!= 9999L) {
// Non Management member, Need Determine Current User belong belong Department and child part part Whether Contain the ID
if(!isSuper) {
// not Contain, Back
if(!userDepartIds.contains(departId)) {
return PageResultUtils.success(0L, new ArrayList<>());
}
}

List<Depart> departList = apDepartService.getCurrentAndChild(departId);
List<Long> departIds = departList.stream().map(Depart::getId).collect(Collectors.toList());

List<Location> locationList = locationService.list();

List<Location> locationResults = new ArrayList<>();
for(Location location: locationList) {
if(departIds.contains(location.getDepartId())) {
locationResults.add(location);
}
}

// Current Department down not has Box, Direct connect Back
if(locationResults.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>());
}

// Set Box Query Condition
List<Long> locationIds = locationResults.stream().map(Location::getId).collect(Collectors.toList());
listVo.setLocationIds(locationIds);
}

// has Department Condition, But is for false Department ID, Query no belong belong Organization Camera
if(departId!= null && departId == 9999L) {
// Non Management member, not can Query no belong belong Organization Camera
if(!isSuper) {
return PageResultUtils.success(0L, new ArrayList<>());
}

// Camera belong belong Department for 0
listVo.setLocationId(0L);
}

// Determine User can Query Department
if(departId == null) {
// Non Management member only can Query Current belong belong Department Camera
if(!isSuper) {
List<Location> locationList = locationService.list();

List<Location> locationResults = new ArrayList<>();
for(Location location: locationList) {
if(userDepartIds.contains(location.getDepartId())) {
locationResults.add(location);
}
}

// Current Department down not has Box, Direct connect Back
if(locationResults.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>());
}

// Set Box Query Condition
List<Long> locationIds = locationResults.stream().map(Location::getId).collect(Collectors.toList());
listVo.setLocationIds(locationIds);
}
}

// check find Refer Fixed Camera IDS
if(ObjectUtil.isNotEmpty(listVo.getAlgorithmIds())) {
List<Long> cameraIds = new ArrayList<>();
for(Long algorithmId: listVo.getAlgorithmIds()) {
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithm(algorithmId);
List<Long> _cameraIds = cameraAlgorithmList.stream().map(CameraAlgorithm::getCameraId).collect(Collectors.toList());
cameraIds.addAll(_cameraIds);
}
listVo.setCameraIds(cameraIds);

if(cameraIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>());
}
}

// Page Query
IPage<Camera> pageResult = cameraService.listPageV2(listVo);
List<Camera> records = pageResult.getRecords();
if(ObjectUtil.isEmpty(records)) {
return PageResultUtils.success(0L, new ArrayList<>());
}

// Query Algorithm List
List<Algorithm> algorithmList = algorithmService.list();
Map<Long, String> algorithmMap = algorithmList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Algorithm::getId, Algorithm::getName));

// Query Box List
List<Location> locationList = locationService.list();
Map<Long, String> locationMap = locationList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Location::getId, Location::getName));

//
List<Camera> dataList = new ArrayList<>();
for (Camera camera: records) {
// Execute Status
int status = 0; // 0- not Run,1- Run in,2- Exception
if(camera.getRunning() == 1) {
if(camera.getAiboxExecStatus() == 1000) {
status = 1;
}
if(camera.getAiboxExecStatus() == 3000) {
status = 2;
}
}

camera.setAlgorithmNames(camera.getAlgoCount() +"");
camera.setLocationName(locationMap.getOrDefault(camera.getLocationId(),"Default"));
camera.setExecStatus(status);

// Camera User and Password Encrypt Process
camera.setRtspUrl(DecodeRtspUlr.processSensitive(camera.getRtspUrl()));
dataList.add(camera);
}

return PageResultUtils.success(pageResult.getTotal(), dataList);
}

/**
* Save Data
* @param camera
* @param algorithmvos
* @return
*/
@ApiOperation("Save Camera Data")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="camera", value ="Camera Entity"),
@ApiImplicitParam(name ="algorithmvos", value ="Relate Algorithm ids(1,2,3)"),
@ApiImplicitParam(name ="confidencevos", value ="Relate Confidence (0.5,0.5,0.5)"),
@ApiImplicitParam(name ="markpointsvos", value ="Region rois"),
@ApiImplicitParam(name ="updatePoint", value ="Whether Update roi(0- No,1- is)"),
@ApiImplicitParam(name ="lineMarkPoints", value ="Person Stream Quantity Exceed Line Draw make Point")
})
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult save(Camera camera, String algorithmvos, String confidencevos, String markpointsvos, Integer updatePoint, String lineMarkPoints) {
if (StrUtil.isBlank(camera.getName())) {
return JsonResultUtils.fail("Please enter Camera Name");
}
Camera c = cameraService.getByName(camera.getName());
if (null!= c &&!c.getId().equals(camera.getId())) {
return JsonResultUtils.fail("Camera Name re reply");
}
if (StrUtil.isBlank(camera.getRtspUrl())) {
return JsonResultUtils.fail("Please enter Camera RTSP Stream");
}
if(StrUtil.isBlank(algorithmvos)) {
return JsonResultUtils.fail("Please At Least Select One Item Algorithm");
}
/*if(StrUtil.isBlank(camera.getLocationType())) {
return JsonResultUtils.fail("locationType cannot be empty");
}*/
if(camera.getIntervalTime() == null || camera.getIntervalTime() <= 0) {
return JsonResultUtils.fail("Please enter Recognition Time Interval (s)");
}
if(camera.getAlarmInterval() == null || camera.getAlarmInterval() <= 0) {
camera.setAlarmInterval(5f);
}
if (camera.getIntervalTime() > camera.getAlarmInterval()) {
return JsonResultUtils.fail("Please Set Recognition Time Interval big at or etc at Alert Time Interval");
}

// Confidence Validate
if(StrUtil.isNotBlank(confidencevos)) {
String[] confidenceArr = confidencevos.split(",");
for(String confidence: confidenceArr) {
if(StrUtil.isBlank(confidence)) {
JsonResultUtils.fail("Please enter Check select Algorithm Confidence");
}
//
try {
double conf = Double.valueOf(confidence);
if(conf <= 0) {
JsonResultUtils.fail("Confidence must be greater than 0");
}
if(conf > 1) {
JsonResultUtils.fail("Confidence must be less than 1.0");
}
} catch (Exception e) {
JsonResultUtils.fail("Confidence Number Char input in Error");
}
}
}


/* remove Relate to Algorithm
if(StrUtil.isNotBlank(camera.getParams())) {
if(StrUtil.isBlank(camera.getFileName())) {
return JsonResultUtils.fail("Please Snapshot according Get image");
}
}
*/
boolean isNew = false;
if (camera.getId() == null) {
camera.setState(CommState.NORMAL.getType());
camera.setRunning(CameraRunningState.RUNNING.getType());
camera.setAction(CameraAction.ACTION_UPD.getType());
camera.setCreatedAt(new Date());

// Region Set
if(camera.getId() == null) {
Location location = locationService.getById(camera.getLocationId());
if(location!= null) {
camera.setLocationIds(location.getParentIds() +"/"+ location.getId());
}
}

// Video Code Format
String videoCodec = takePhoto.getVideoCodec(camera.getRtspUrl());
camera.setVideoCodec(videoCodec);

//
isNew = true;
} else {
//
Camera cameraDb = cameraService.getById(camera.getId());
if(cameraDb == null) {
return JsonResultUtils.fail("Camera Data Not Exist");
}
// Modify Camera Address, or Video Format Code not Set, Again Get Video Code Format
if(!camera.getRtspUrl().equals(cameraDb.getRtspUrl()) || StrUtil.isBlank(cameraDb.getVideoCodec())) {
camera.setVideoCodec(takePhoto.getVideoCodec(camera.getRtspUrl()));
}
// Stop original start Stream
if(!camera.getRtspUrl().equals(cameraDb.getRtspUrl())) {
mediaService.closeStream(cameraDb.getId(),"live");
}
}
camera.setUpdatedAt(new Date());
try {
this.cameraService.saveCamera(camera, algorithmvos, confidencevos, markpointsvos, updatePoint, lineMarkPoints);
// increase Add action_counter Plan Number Value

}catch (Exception e){
log.error("Save Camera Exception", e);
return JsonResultUtils.fail("Save Data Exception");
}
return JsonResultUtils.success();
}

/**
* Save Camera Data - Fit allocate VUE
* @param cameraVo
* @return
*/
// @ApiOperation("Save Camera Data")
// @ApiImplicitParams(value = {
// @ApiImplicitParam(name ="camera", value ="Camera Entity"),
// @ApiImplicitParam(name ="algorithmvos", value ="Relate Algorithm ids(1,2,3)"),
// @ApiImplicitParam(name ="confidencevos", value ="Relate Confidence (0.5,0.5,0.5)"),
// @ApiImplicitParam(name ="markpointsvos", value ="Region rois"),
// @ApiImplicitParam(name ="updatePoint", value ="Whether Update roi(0- No,1- is)")
//})
// @PostMapping({"/saveOrUpdate"})
// @ResponseBody
// public JsonResult saveOrUpdate(@RequestBody CameraVo cameraVo) {
// Camera camera = cameraVo.getCamera();
// if(camera == null) {
// camera = new Camera();
//}
// if (StrUtil.isBlank(camera.getName())) {
// return JsonResultUtils.fail("Please enter Camera Name");
//}
// Camera c = cameraService.getByName(camera.getName());
// if (null!= c) {
// return JsonResultUtils.fail("Camera Name re reply");
//}
// if (StrUtil.isBlank(camera.getRtspUrl())) {
// return JsonResultUtils.fail("Please enter Camera RTSP Stream");
//}
//
// String algorithmvos = cameraVo.getAlgorithmvos();
//// if(StrUtil.isBlank(algorithmvos)) {
//// return JsonResultUtils.fail("Please At Least Select One Item Algorithm");
////}
//
// if(camera.getIntervalTime() == null || camera.getIntervalTime() <= 0) {
// return JsonResultUtils.fail("Please enter Recognition Time Interval (s)");
//}
// if(camera.getAlarmInterval() == null || camera.getAlarmInterval() <= 0) {
// camera.setAlarmInterval(5f);
//}
// if (camera.getIntervalTime() > camera.getAlarmInterval()) {
// return JsonResultUtils.fail("Please Set Recognition Time Interval big at or etc at Alert Time Interval");
//}
//
// // Confidence Validate
// if(StrUtil.isNotBlank(cameraVo.getConfidencevos())) {
// String[] confidenceArr = cameraVo.getConfidencevos().split(",");
// for(String confidence: confidenceArr) {
// if(StrUtil.isBlank(confidence)) {
// JsonResultUtils.fail("Please enter Check select Algorithm Confidence");
//}
// //
// try {
// double conf = Double.valueOf(confidence);
// if(conf <= 0) {
// JsonResultUtils.fail("Confidence must be greater than 0");
//}
// if(conf > 1) {
// JsonResultUtils.fail("Confidence must be less than 1.0");
//}
//} catch (Exception e) {
// JsonResultUtils.fail("Confidence Number Char input in Error");
//}
//}
//}
//
// /* remove Relate to Algorithm
// if(StrUtil.isNotBlank(camera.getParams())) {
// if(StrUtil.isBlank(camera.getFileName())) {
// return JsonResultUtils.fail("Please Snapshot according Get image");
//}
//}
// */
// boolean isNew = false;
// if (camera.getId() == null) {
// camera.setState(CommState.NORMAL.getType());
// camera.setRunning(CameraRunningState.RUNNING.getType());
// camera.setAction(CameraAction.ACTION_UPD.getType());
// camera.setCreatedAt(new Date());
//
// // Region Set
// if(camera.getId() == null) {
// Location location = locationService.getById(camera.getLocationId());
// if(location!= null) {
// camera.setLocationIds(location.getParentIds() +"/"+ location.getId());
//}
//}
// //
// isNew = true;
//}
// camera.setUpdatedAt(new Date());
// this.cameraService.saveCamera(camera, algorithmvos, cameraVo.getConfidencevos(), cameraVo.getMarkpointsvos(), cameraVo.getUpdatePoint(), null);
//
// // Release Person Stream Quantity Task
// //httpTrackerService.addTracker(camera.getId(), isNew? false: true);
// // Release Frame Extract Task
// //httpFrameService.addCamera(camera.getId(), camera.getRtspUrl(), camera.getIntervalTime(), isNew? false: true);
// // Call Edge Box Camera Task
// httpAiBoxService.taskCamera(camera.getId(), isNew? 1000: 6000);
//
// return JsonResultUtils.success();
//}
//
/**
* Delete Camera
* @param id
* @return
*/
@ApiOperation("Delete Camera Data")
@ApiImplicitParam(name ="id", value ="Data id")
@SaCheckPermission(value = {"box-delete"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult<?> delete(Long id) {
Camera camera = cameraService.getById(id);
if(camera == null) {
return JsonResultUtils.fail("Camera does not exist or Deleted");
}
// Status Error or not Run Camera, Direct connect Delete
if(camera.getState() == null || camera.getState()!= 0 || camera.getRunning() == null || camera.getRunning()!= 1) {
cameraService.delete(id);
return JsonResultUtils.success("Operation success");
}

//
Location location = locationService.getById(camera.getLocationId());
if(location == null) {
return JsonResultUtils.fail("Camera not Assign Box Device");
}

// Run in Camera, Call Edge Box Camera Task
CameraDelResponse cameraDelResponse = messageSendHandler.sendDelCamera(location, camera);
if(cameraDelResponse.isStatus()) {
cameraService.delete(id);
return JsonResultUtils.success("Operation success");
}
return JsonResultUtils.fail(cameraDelResponse.getMsg());
}

/**
* Query Run in Camera List
* @return
*/
@ApiOperation("Query Run in Camera List")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/running"})
@ResponseBody
public JsonResult<List<Map<String, Object>>> listRunning() {
List<Camera> cameraList = this.cameraService.list();
if (cameraList == null) {
cameraList = new ArrayList<>();
}

//
List<Map<String, Object>> dataList = new ArrayList<>();
for (Camera camera: cameraList) {
if(camera.getState() == null || camera.getState()!= 0) {
continue;
}

//
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("id", camera.getId());
dataMap.put("running", camera.getRunning());
dataList.add(dataMap);
}
return JsonResultUtils.success(dataList);
}

/**
* Camera Snapshot according Get image
* @return
*/
@ApiOperation("Camera Snapshot according Get image")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="rtspUrl", value ="Camera original start RTSP Address or Add Sensitive Feel RTSP Address", example ="rtsp://******:******@192.168.3.11/Streaming/Channels/101"),
@ApiImplicitParam(name ="locationId", value ="Box id", example ="1"),
@ApiImplicitParam(name ="cameraId", value ="Camera id", example ="1"),
})
@SaCheckPermission(value = {"edgePlatform-boxManagement","gb-channel-list"}, mode = SaMode.OR)
@RequestMapping({"/takePhoto"})
@ResponseBody
public JsonResult<?> takePhoto(String rtspUrl, Long locationId, Long cameraId) {
if(StrUtil.isBlank(rtspUrl)) {
return JsonResultUtils.fail("Please enter Video Stream Address");
}

if(locationId == null) {
return JsonResultUtils.fail("Please First Select Box Device");
}

// like Result transmit Camera ID, that What table show Data already exists, Determine rtsp Address Whether Change
if(cameraId!= null) {
Camera camera = cameraService.getById(cameraId);
if(camera == null) {
return JsonResultUtils.fail("Camera does not exist");
}

// Determine Address Whether Change, like Result no Change, rule Use Database In Surface Address replace instead
String rtspUrlSensitive = DecodeRtspUlr.processSensitive(camera.getRtspUrl());
if(rtspUrlSensitive.equalsIgnoreCase(rtspUrl)) {
rtspUrl = camera.getRtspUrl();
}
}

// Cross net, Camera In customer account Local
if(projectConfig.isCrossNet()) {
Location location = locationService.getById(locationId);
if(location == null) {
return JsonResultUtils.fail("Camera not Relate Box Device");
}

VideoInfo videoInfo = takePhoto.takeCross(rtspUrl, location.getBoxNo());
if(videoInfo == null) {
return JsonResultUtils.fail("Snapshot according Failed, Please Ensure Video Stream Normal and Again Try Try");
}
return JsonResultUtils.success(videoInfo);
}

// Direct connect Get, Network is Mutual via
VideoInfo videoInfo = takePhoto.takeLocal(rtspUrl);
if(videoInfo == null) {
return JsonResultUtils.fail("Snapshot according Failed, Please Ensure Video Stream Normal and Again Try Try");
}
return JsonResultUtils.success(videoInfo);
}

/**
* Cut change Camera Run Status
* @param id
* @return
*/
@ApiOperation("Cut change Camera Run Status")
@ApiImplicitParam(name ="id", value ="Camera id")
@SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
@RequestMapping({"/switchRunning"})
@ResponseBody
public JsonResult<?> switchRunning(Long id) {
Camera camera = cameraService.getById(id);
if (camera == null) {
return JsonResultUtils.fail("Camera does not exist or Deleted");
}

Long aiboxExecSend = camera.getAiboxExecSend();
if(aiboxExecSend!= null) {
long diffInSeconds = System.currentTimeMillis() - aiboxExecSend;
if (diffInSeconds <= 3 * 1000) {
return JsonResultUtils.successMsg("System Processing, Please Do Not Frequent Operation, Please 3 s after Retry");
}
}

// check find belong belong Edge Box
Location location = locationService.getById(camera.getLocationId());
if(location == null) {
return JsonResultUtils.fail("Camera not Assign Box Device");
}

//
Integer running = camera.getRunning();
if(running == null) {
running = 0;
}

// Person Stream Quantity Process
if(running == 0) {
// Query Current Inference Path Number
// int inferNum = cameraService.getInferNum(camera.getLocationId());
// if(inferNum > projectConfig.getMaxInferNum()) {
// JsonResultUtils.fail("Current Box most big Allow same Hour Enable"+ projectConfig.getMaxInferNum() +"Camera");
//}

// Notification Box
CameraAddResponse response = messageSendHandler.sendAddCamera(location, camera);
if(response.isStatus()) {
cameraService.updateRunning1(id, 1,"Run in");
return JsonResultUtils.success("Operation success");
} else {
cameraService.updateRunning1(id, null, response.getMsg());
}
return JsonResultUtils.fail(response.getMsg());
} else {
// Notification Box
CameraDelResponse response = messageSendHandler.sendDelCamera(location, camera);
if(response.isStatus()) {
cameraService.updateRunning1(id, 0,"not Run");
return JsonResultUtils.success("Operation success");
} else {
cameraService.updateRunning1(id, null, response.getMsg());
}
return JsonResultUtils.fail(response.getMsg());
}
}

/**
* Query Work Dynamic Camera
* @return
*/
@ApiOperation("Query Work Dynamic Camera")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/actives"})
@ResponseBody
public JsonResult listActives() {
//
List<Map<String, Object>> dataList = new ArrayList<>();

//
String streamType = configService.getByValTag("streamType");
if(StrUtil.isBlank(streamType) ||"rtsp".equals(streamType) ||"algo".equals(streamType)) {
// java Push Stream
List<Camera> cameraList = cameraService.listActives();
if(cameraList == null) {
cameraList = new ArrayList<>();
}
Collections.shuffle(cameraList);
//
for(Camera camera: cameraList) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("id", camera.getId());
dataList.add(dataMap);
}
}
return JsonResultUtils.success(dataList);
}

/**
* Query Work Dynamic Camera - Page Query
* @return
*/
@ApiOperation("Query Work Dynamic Camera")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size")
})
@SaCheckPermission(value = {"edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping("listPageActives")
@ResponseBody
public PageResult<List<Map<String, Object>>> listPageActives(@RequestParam(defaultValue ="1") Integer page, @RequestParam(defaultValue ="10") Integer limit) {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return PageResultUtils.success(0L, new ArrayList<>());
}
//
List<Long> queryLocationIds = new ArrayList<>();
if(Integer.valueOf(1).equals(account.getIsSuper())) {// super Level Management member
List<Location> locations = locationService.listByType("2");
for(Location location: locations) {
queryLocationIds.add(location.getId());
}
} else {// Non Management member
List<Long> departIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
List<Long> locationIds = locationService.getLocationIdsByDeparts(departIds);
if(locationIds!= null) {
queryLocationIds.addAll(locationIds);
}
}
// not has Corresponding Box Info
if(queryLocationIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>());
}

IPage<Camera> cameraIPage = cameraService.listPageActivesV2(page, limit, queryLocationIds);
List<Camera> records = cameraIPage.getRecords();
if(records == null) {
records = new ArrayList<>();
}
//
List<Map<String, Object>> dataList = new ArrayList<>();
for(Camera record: records) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("id", record.getId());
dataList.add(dataMap);
}
return PageResultUtils.success(cameraIPage.getTotal(), dataList);
}

/**
* Query Work Dynamic Camera
* @return
*/
@ApiOperation("Query Work Dynamic Camera (Random Sort)")
@SaCheckPermission(value = {"faceControl-faceRecognition"}, mode = SaMode.OR)
@PostMapping({"/shuffle_actives"})
@ResponseBody
public JsonResult shuffleActives() {
List<Camera> cameraList = cameraService.listActives();
if (cameraList == null) {
cameraList = new ArrayList<>();
}
Collections.shuffle(cameraList);

for(Camera camera: cameraList) {
// Camera User and Password Encrypt
camera.setRtspUrl(DecodeRtspUlr.processSensitive(camera.getRtspUrl()));
}

return JsonResultUtils.success(cameraList);
}

/**
* Each Separate 5 s Report In Progress Play Put Camera
* @param cameraIds
* @return
*/
@ApiIgnore
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/refreshVideoPlay"})
@ResponseBody
public JsonResult<?> refreshVideoPlay(String cameraIds) {
if(StrUtil.isBlank(cameraIds)) {
return JsonResultUtils.success();
}
String[] uCameraIds = cameraIds.split(",");

//
List<Long> playCameraIds = new ArrayList<>();
for(String cameraId: uCameraIds) {
playCameraIds.add(Long.parseLong(cameraId));
}
//
cameraService.updateVideoPlays(playCameraIds);
return JsonResultUtils.success();
}

@ApiOperation("new Version Save Camera Data")
@SaCheckPermission(value = {"box-add","box-edit"}, mode = SaMode.OR)
@PostMapping({"/submit"})
@ResponseBody
public JsonResult<?> submit(@RequestBody CameraModifyVo modifyVo) {
if(StrUtil.isBlank(modifyVo.getName())) {
return JsonResultUtils.fail("Camera Name must Fill");
}
if(StrUtil.isBlank(modifyVo.getRtspUrl())) {
return JsonResultUtils.fail("Camera Stream Address must Fill");
}
if(modifyVo.getIntervalTime() == null) {
return JsonResultUtils.fail("Recognition Interval must Fill");
}
if(modifyVo.getAlarmInterval() == null) {
return JsonResultUtils.fail("Alert Interval must Fill");
}
if(modifyVo.getIntervalTime() <= 0) {
return JsonResultUtils.fail("Recognition Interval must be greater than 0");
}
if(modifyVo.getAlarmInterval() <= 0) {
return JsonResultUtils.fail("Alert Interval must be greater than 0");
}
if(modifyVo.getAlarmInterval() < modifyVo.getIntervalTime()) {
return JsonResultUtils.fail("Alert Interval must be greater than Recognition Interval");
}
if(modifyVo.getAlgorithms() == null || modifyVo.getAlgorithms().isEmpty()) {
return JsonResultUtils.fail("must Select One kind Algorithm");
}
for(CameraAlgorithmModifyVo cameraAlgorithmModifyVo: modifyVo.getAlgorithms()) {
if(cameraAlgorithmModifyVo.getAlgorithmId() == null) {
return JsonResultUtils.fail("Algorithm not Select");
}
if(cameraAlgorithmModifyVo.getAlgorithmConf() == null || cameraAlgorithmModifyVo.getAlgorithmConf() <= 0) {
return JsonResultUtils.fail("Algorithm Confidence must be greater than 0");
}
}

// Add Name Unique One Property Validate
Camera existingCamera = cameraService.getByName(modifyVo.getName());
if(existingCamera!= null &&!existingCamera.getId().equals(modifyVo.getId())) {
return JsonResultUtils.fail("Camera Name Exist, Please make Use Other Name");
}

// Determine Box Device Whether Exist
Location location = locationService.getById(modifyVo.getLocationId());
if(location == null) {
return JsonResultUtils.fail("not Select Box Device");
}

if(StrUtil.isBlank(location.getPlatform())) {
return JsonResultUtils.fail("Box Device lack Missing Chip Param");
}

// Speaker Pole ID Process
if(modifyVo.getSoundColumnId() == null) {
modifyVo.setSoundColumnId(0L);
}

//
try {
// Whether Create new Camera
boolean isAdd = (modifyVo.getId() == null);

// Whether Close far End Box Push Stream
boolean isCloseRemoteStream = false;

// Query Old Camera
Camera cameraDb = cameraService.getById(modifyVo.getId());
if(!isAdd) {
if(cameraDb == null) {
return JsonResultUtils.fail("Camera does not exist or Deleted, no Method Modify");
} else {
// Process rtsp Address change more, like Result Desensitize Address and Desensitize Database Address Consistent, rule Collect Use Database Address
String sensitiveRtspUrl = DecodeRtspUlr.processSensitive(cameraDb.getRtspUrl());
String sensitiveRtspUrl2 = DecodeRtspUlr.processSensitive(cameraDb.getRtspUrl2());
if(sensitiveRtspUrl2.equalsIgnoreCase(modifyVo.getRtspUrl2())) {
modifyVo.setRtspUrl2(cameraDb.getRtspUrl2());
}
if(sensitiveRtspUrl.equalsIgnoreCase(modifyVo.getRtspUrl())) {
modifyVo.setRtspUrl(cameraDb.getRtspUrl());
} else {
// log.info("rtsp Address change more, Close Stream Media");
mediaRestfulService.closeStream("live", cameraDb.getId() +"");
mediaRestfulService.closeStream("cloud", cameraDb.getId() +"");

if(projectConfig.isCrossNet()) {
isCloseRemoteStream = true;
}
}

Long aiboxExecSend = cameraDb.getAiboxExecSend();
if(aiboxExecSend!= null) {
long diffInSeconds = System.currentTimeMillis() - aiboxExecSend;
if (diffInSeconds <= 3 * 1000) {
return JsonResultUtils.successMsg("System Processing, Please Do Not Frequent Operation, Please 3 s after Retry");
}
}
}
}

// Save Data
Long cameraId = cameraService.saveSubmit(modifyVo, location);

// Send Camera to Box Device
Camera cameraData = cameraService.getById(cameraId);
if(cameraData.getRunning()!= null && cameraData.getRunning() == 1) {
String errorMsg = cameraService.sendCameraToDevice(cameraData, location);
if(errorMsg!= null) {
return JsonResultUtils.fail(errorMsg);
}

if(isCloseRemoteStream && projectConfig.isCrossNet()) {// Stream Address Change, and for Cross net Mode
messageSendHandler.sendStreamClose(location, cameraData.getId(), projectConfig.getCloudStreamPort());
}
}


// Add Camera Camera, Direct connect Back
// if(isAdd) {
// return JsonResultUtils.success("Operation success");
//}

// Stream Address Change, Notification Stream Media Close Video Stream
// if(!modifyVo.getRtspUrl().equals(cameraDb.getRtspUrl())) {
// mediaService.closeStream(modifyVo.getId(),"live");
//}

// like Result Modify Camera, and for Enabled
// if(cameraDb.getRunning()!= null && cameraDb.getRunning() == 1) {
// // Query History, like Result Call Algorithm Failed, Need Manual Rollback Data
// List<CameraAlgorithm> cameraAlgorithmsDb = cameraAlgorithmService.listByCamera(modifyVo.getId());
// List<ReportPeriod> reportPeriodsDb = reportPeriodService.listByCamera(modifyVo.getId());
//
// // Query most new Camera
// Camera cameraNew = cameraService.getById(cameraId);
// CameraAddResponse response = messageSendHandler.sendAddCamera(location, cameraNew);
// if(response.isStatus()) {
// cameraService.updateRunning1(cameraId, 1);
// return JsonResultUtils.success("Operation success");
//}
//
// // Execute Rollback Operation
// cameraService.updateRollback(cameraDb, cameraAlgorithmsDb, reportPeriodsDb);
// return JsonResultUtils.fail(response.getMsg());
//}
return JsonResultUtils.success("Operation success");
}catch (Exception e){
log.error("Camera Add / Modify Exception", e);
return JsonResultUtils.fail(String.format("Operation failed, Error Message [%s]", e.getMessage()));
}
}

@ApiOperation("new Version Query Camera Detail Data")
@ApiImplicitParam(name ="id", value ="Data ID")
@SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
@PostMapping({"/info"})
@ResponseBody
public JsonResult<CameraModifyVo> info(Long id) {
Camera camera = cameraService.getById(id);
if(camera == null) {
return JsonResultUtils.fail("find not to Data");
}
//
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}
Map<Long, String> algorithmMap = algorithmList.stream().collect(Collectors.toMap(Algorithm::getId, Algorithm::getNameEn, (s1, s2) -> s1));
//
List<CameraAlgorithmModifyVo> cameraAlgorithmModifyVos = new ArrayList<>();
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(camera.getId());
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
CameraAlgorithmModifyVo cameraAlgorithmModifyVo = new CameraAlgorithmModifyVo();
cameraAlgorithmModifyVo.setAlgorithmId(cameraAlgorithm.getAlgorithmId());
cameraAlgorithmModifyVo.setAlgorithmNameEn(algorithmMap.get(cameraAlgorithm.getAlgorithmId()));
cameraAlgorithmModifyVo.setAlgorithmConf(cameraAlgorithm.getConfidence());
cameraAlgorithmModifyVo.setDrawBoxs(new ArrayList<>());
cameraAlgorithmModifyVo.setDrawLines(new ArrayList<>());
cameraAlgorithmModifyVo.setAlarmTimes(new ArrayList<>());

// roi Coordinate
String drawBoxJson = cameraAlgorithm.getImagePoints();
if(StrUtil.isNotBlank(drawBoxJson)) {
List<List<CameraAlgorithmDrawBoxModifyVo>> cameraAlgorithmDrawBoxModifyVos = new ArrayList<>();
JSONArray jsonArray = JSON.parseArray(drawBoxJson);
for(int i = 0; i < jsonArray.size(); i++) {
List<CameraAlgorithmDrawBoxModifyVo> cameraAlgorithmDrawBoxModifyVo = JSONObject.parseArray(jsonArray.getJSONArray(i).toJSONString(), CameraAlgorithmDrawBoxModifyVo.class);
cameraAlgorithmDrawBoxModifyVos.add(cameraAlgorithmDrawBoxModifyVo);
}
cameraAlgorithmModifyVo.setDrawBoxs(cameraAlgorithmDrawBoxModifyVos);
}

// line Coordinate
String drawLineJson = cameraAlgorithm.getLineImagePoints();
if(StrUtil.isNotBlank(drawLineJson)) {
List<List<CameraAlgorithmDrawBoxModifyVo>> cameraAlgorithmDrawLineModifyVos = new ArrayList<>();
JSONArray jsonArray = JSON.parseArray(drawLineJson);
for(int i = 0; i < jsonArray.size(); i++) {
List<CameraAlgorithmDrawBoxModifyVo> cameraAlgorithmDrawLineModifyVo = JSONObject.parseArray(jsonArray.getJSONArray(i).toJSONString(), CameraAlgorithmDrawBoxModifyVo.class);
cameraAlgorithmDrawLineModifyVos.add(cameraAlgorithmDrawLineModifyVo);
}
cameraAlgorithmModifyVo.setDrawLines(cameraAlgorithmDrawLineModifyVos);
}

// Alarm Area between
List<ReportPeriod> reportPeriods = reportPeriodService.listData(camera.getId(), cameraAlgorithm.getAlgorithmId());
if(reportPeriods!= null &&!reportPeriods.isEmpty()) {
List<CameraAlgorithmAlarmTimeModifyVo> algorithmAlarmTimeModifyVos = new ArrayList<>();
for(ReportPeriod reportPeriod: reportPeriods) {
CameraAlgorithmAlarmTimeModifyVo algorithmAlarmTimeModifyVo = new CameraAlgorithmAlarmTimeModifyVo();
algorithmAlarmTimeModifyVo.setStartTime(reportPeriod.getStartText());
algorithmAlarmTimeModifyVo.setEndTime(reportPeriod.getEndText());
algorithmAlarmTimeModifyVos.add(algorithmAlarmTimeModifyVo);
}
cameraAlgorithmModifyVo.setAlarmTimes(algorithmAlarmTimeModifyVos);
}

// Relate Social Push
List<SocialConfig> socialConfigList = socialConfigService.listData(cameraAlgorithm.getCameraId(), cameraAlgorithm.getAlgorithmId());
List<Long> socials = socialConfigList.stream().map(SocialConfig::getSocialId).collect(Collectors.toList());
cameraAlgorithmModifyVo.setSocials(socials);

//
cameraAlgorithmModifyVos.add(cameraAlgorithmModifyVo);
}

//
CameraModifyVo cameraModifyVo = new CameraModifyVo();
BeanUtils.copyProperties(camera, cameraModifyVo);
cameraModifyVo.setAlgorithms(cameraAlgorithmModifyVos);

// Camera User and Password Encrypt
cameraModifyVo.setRtspUrl(DecodeRtspUlr.processSensitive(camera.getRtspUrl()));
cameraModifyVo.setRtspUrl2(DecodeRtspUlr.processSensitive(camera.getRtspUrl2()));

// Speaker Pole ID Process
if(cameraModifyVo.getSoundColumnId()!= null && cameraModifyVo.getSoundColumnId() == 0) {
cameraModifyVo.setSoundColumnId(null);
}
return JsonResultUtils.success(cameraModifyVo);
}

// /**
// * Add Camera, Send Operation Message to Box
// * @param location
// * @param camera
// */
// private CameraAddResponse sendAddRequest(Location location, Camera camera) {
// List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByCamera(camera.getId());
// if(cameraAlgorithmList == null || cameraAlgorithmList.isEmpty()) {
// return CameraAddResponse.builder().status(false).msg("not has Config Algorithm").cameraId(camera.getId()).build();
//}
//
// List<CameraAddRequest.Algo> algos = new ArrayList<>();
// for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
// Algorithm algorithm = algorithmService.getById(cameraAlgorithm.getAlgorithmId());
// if(algorithm == null) {
// continue;
//}
//
// String platform = algorithm.getPlatform();
// String nameEn = algorithm.getNameEn();
// Integer shareMode = algorithm.getShareMode() == null? 0: algorithm.getShareMode();
//
// String version = cameraAlgorithm.getAlgorithmVersion();
// if (StringUtils.isBlank(version)) {
// // like Result version Is Empty, Get Box down Other Camera Algorithm version
// List<CameraAlgorithm> caList = cameraAlgorithmService.listByAlgorithmAndBoxId(cameraAlgorithm.getAlgorithmId(), location.getId());
// for (CameraAlgorithm ca: caList) {
// if (StringUtils.isNotBlank(ca.getAlgorithmVersion())) {
// version = ca.getAlgorithmVersion();
// break;
//}
//}
//}
//
// // like Result according Is Empty, Get Local File most new version
// if (StrUtil.isBlank(version)) {
// double maxVer = 0d;
// String path = MODEL_DIR + platform +"/"+ nameEn;
// if(!FileUtil.exist(path)) {
// FileUtil.mkdir(path);
//}
// List<String> fileNamesList = FileUtil.listFileNames(path);
// for (String fileName: fileNamesList) {
// String mainName = FileUtil.mainName(fileName);
// String extName = FileUtil.extName(fileName);
// if(StrUtil.isNotBlank(mainName) &&"zip".equalsIgnoreCase(extName)) {
// String[] parts = mainName.split("-");
// if(parts.length!= 3) {
// continue;
//}
//
// // find most big Version
// String v = parts[2]; // Version No
// double currVer = Double.parseDouble(v);
// if(maxVer < currVer) {
// maxVer = currVer;
// version = v;
//}
//}
//}
//}
//
// // Update version to CameraAlgorithm
// cameraAlgorithm.setAlgorithmVersion(version);
// cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
//
// CameraAddRequest.Algo algo = new CameraAddRequest.Algo();
// algo.setAlgoId(algorithm.getId());
// algo.setAlgoName(algorithm.getName());
// algo.setAlgoNameEn(algorithm.getNameEn());
// algo.setAlgoConfidence(cameraAlgorithm.getConfidence());
// algo.setAlgoVer(version);
// algo.setAlgoRois(cameraAlgorithm.getImagePoints());
// algo.setAlgoLines(cameraAlgorithm.getLineImagePoints());
// algo.setZipFile(String.format("/%s/%s/%s-%s-%s.zip", platform, nameEn, platform, nameEn, version));
// algo.setZipName(String.format("%s-%s-%s.zip", platform, nameEn, version));
// algo.setMd5(DigestUtil.md5Hex(FileUtil.newFile(FileUtils.pathTo(MODEL_DIR + algo.getZipFile()))));
// algo.setShareMode(shareMode);
// algos.add(algo);
//}
//
// CameraAddRequest cameraAddRequest = new CameraAddRequest();
// cameraAddRequest.setType(MessageType.ADD_CAMERA.getType());
// cameraAddRequest.setSn(location.getBoxNo());
// cameraAddRequest.setRequestId(IdUtil.randomUUID());
// cameraAddRequest.setCameraId(camera.getId());
// cameraAddRequest.setIntervalTime(camera.getIntervalTime());
// cameraAddRequest.setRtspUrl(camera.getRtspUrl());
// cameraAddRequest.setVideoFps(camera.getVideoFps());
// cameraAddRequest.setAlgorithms(algos);
//
// Response response = messageSenderAndWaiter.sendRequest(cameraAddRequest);
// if(response == null) {
// return CameraAddResponse.builder().status(false).msg("Unknown Error").cameraId(camera.getId()).build();
//}
// return (CameraAddResponse) response;
//}
//
// /**
// * Delete Camera, Send Operation Message to Box
// * @param location
// * @param camera
// */
// private CameraDelResponse sendDelRequest(Location location, Camera camera) {
// CameraDelRequest cameraDelRequest = new CameraDelRequest();
// cameraDelRequest.setType(MessageType.DEL_CAMERA.getType());
// cameraDelRequest.setSn(location.getBoxNo());
// cameraDelRequest.setRequestId(IdUtil.randomUUID());
// cameraDelRequest.setCameraId(camera.getId());
// Response response = messageSenderAndWaiter.sendRequest(cameraDelRequest);
// if(response == null) {
// return CameraDelResponse.builder().status(false).msg("Unknown Error").cameraId(camera.getId()).build();
//}
// return (CameraDelResponse) response;
//}


/**
* Camera Snapshot according Get image
* @return
*/
@ApiOperation("Sync Update Camera Code")
@ApiImplicitParam(name ="id", value ="Camera ID")
@SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
@RequestMapping({"/saveVideoCodec"})
@ResponseBody
public JsonResult<?> saveVideoCodec(Long id) {
Camera camera = cameraService.getById(id);
if(camera == null) {
return JsonResultUtils.fail("Camera does not exist");
}

if(StrUtil.isBlank(camera.getRtspUrl())) {
return JsonResultUtils.fail("Camera Stream Address not Config");
}

// Cross net, Camera In customer account Local
if(projectConfig.isCrossNet()) {
Location location = locationService.getById(camera.getLocationId());
if(location == null) {
return JsonResultUtils.fail("Camera not Relate Box Device");
}

VideoInfo videoInfo = takePhoto.takeCross(camera.getRtspUrl(), location.getBoxNo());
if(videoInfo == null) {
return JsonResultUtils.fail("Sync Failed, Please Ensure Video Stream Normal and Again Try Try");
}

// Update Code Info
Camera modifyCamera = new Camera();
modifyCamera.setId(camera.getId());
modifyCamera.setVideoCodec(videoInfo.getVideoCodec());
modifyCamera.setVideoFps(videoInfo.getVideoFps());
cameraService.updateById(modifyCamera);

return JsonResultUtils.success(videoInfo);
}

// Direct connect Get, Network is Mutual via
VideoInfo videoInfo = takePhoto.takeLocal(camera.getRtspUrl());
if(videoInfo == null) {
return JsonResultUtils.fail("Sync Failed, Please Ensure Video Stream Normal and Again Try Try");
}

// Update Code Info
Camera modifyCamera = new Camera();
modifyCamera.setId(camera.getId());
modifyCamera.setVideoCodec(videoInfo.getVideoCodec());
modifyCamera.setVideoFps(videoInfo.getVideoFps());
modifyCamera.setVideoWidth(videoInfo.getVideoWidth());
modifyCamera.setVideoHeight(videoInfo.getVideoHeight());
cameraService.updateById(modifyCamera);
return JsonResultUtils.success(videoInfo);
}

// @SaIgnore
// @GetMapping("/nodes")
// @ResponseBody
// public JsonResult<?> nodes() {
// return JsonResultUtils.success(cameraService.getMinMediaServer());
//}

@ApiOperation("Query All Camera")
@SaCheckPermission(value = {"map:mgr","edgePlatform-groupView","faceControl-faceHistory","edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping({"/listData5"})
@ResponseBody
public PageResult<?> listData5(@RequestBody CameraListData5Vo dataVo) {
List<CameraLocationDTO> cameraLocationDTOS = new ArrayList<>();

List<Camera> cameraList = cameraService.listData5(dataVo.getObjectIds(), dataVo.getObjectName());
if(cameraList == null || cameraList.isEmpty()) {
return PageResultUtils.success(null, new ArrayList<>());
}

//
List<Location> locationList = locationService.list();
if(locationList == null) {
locationList = new ArrayList<>();
}
Map<Long, String> locationMap = locationList.stream().collect(Collectors.toMap(Location::getId, Location::getName));

List<Long> mapObjectIDList = mapObjectService.listObjectIDByType(1);

for(Camera camera: cameraList) {
if(mapObjectIDList.contains(camera.getId())) {
continue;
}

CameraLocationDTO cameraLocationDTO = new CameraLocationDTO();
cameraLocationDTO.setCameraId(camera.getId());
cameraLocationDTO.setLocationId(camera.getLocationId());
cameraLocationDTO.setCameraName(camera.getName());
cameraLocationDTO.setLocationName(locationMap.get(camera.getLocationId()));
cameraLocationDTOS.add(cameraLocationDTO);
}

return PageResultUtils.success(null, cameraLocationDTOS);
}

@ApiOperation("Query Config Face Algorithm Camera")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@RequestMapping("listData6")
@ResponseBody
public PageResult<?> listData6() {
List<CameraCassInfoDTO> cameraCassInfoDTOS = new ArrayList<>();

Algorithm algorithm = algorithmService.getByNameEn("face_recognize");
if(algorithm == null) {
return PageResultUtils.success(0L, cameraCassInfoDTOS);
}

List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
if(cameraAlgorithms == null || cameraAlgorithms.isEmpty()) {
return PageResultUtils.success(0L, cameraCassInfoDTOS);
}
List<Long> cameraIds = cameraAlgorithms.stream().map(CameraAlgorithm::getCameraId).collect(Collectors.toList());

List<Camera> cameraList = cameraService.listData();
for(Camera camera: cameraList) {
if(!cameraIds.contains(camera.getId())) {
continue;
}
CameraCassInfoDTO cameraCassInfoDTO = new CameraCassInfoDTO();
cameraCassInfoDTO.setId(camera.getId());
cameraCassInfoDTO.setName(camera.getName());
cameraCassInfoDTOS.add(cameraCassInfoDTO);
}
return PageResultUtils.success(null, cameraCassInfoDTOS);
}

@ApiOperation("By The belong Organization and The belong Group Query Camera List")
@SaCheckPermission(value = {"edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping("listData7")
@ResponseBody
public JsonResult<?> listData7(@RequestBody CameraListData7Vo dataVo) {
// By Department Query All Box ID
List<Long> locationIds = listUserLocationIds(dataVo.getDepartIds());
if(locationIds == null) {
return JsonResultUtils.success(Collections.emptyList());
}

// By Group ID Query All Camera ID
List<Long> cameraIds = new ArrayList<>();
if(ObjectUtil.isNotEmpty(dataVo.getCameraGroupIds())) {
List<Long> cameraGroupIds = dataVo.getCameraGroupIds();
for(Long cameraGroupId: cameraGroupIds) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(cameraGroupId);
if(ObjectUtil.isNotEmpty(cameraGroupItemList)) {
List<Long> currentCameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getId).collect(Collectors.toList());
cameraIds.addAll(currentCameraIds);
}
}

if(ObjectUtil.isEmpty(cameraIds)) {
return JsonResultUtils.success(Collections.emptyList());
}
}

// Query Camera
List<Camera> cameraList = cameraService.listData7(locationIds, cameraIds, dataVo.getRunning());

//
List<CameraCassInfoDTO> cameraCassInfoDTOS = new ArrayList<>();
for(Camera camera: cameraList) {
CameraCassInfoDTO cameraCassInfoDTO = new CameraCassInfoDTO();
cameraCassInfoDTO.setId(camera.getId());
cameraCassInfoDTO.setName(camera.getName());
cameraCassInfoDTOS.add(cameraCassInfoDTO);
}
return JsonResultUtils.success(cameraCassInfoDTOS);
}

@ApiOperation("By The belong Organization and The belong Group Query Camera List")
@SaCheckPermission(value = {"edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping("listData8")
@ResponseBody
public JsonResult<?> listData8(@RequestBody CameraListData7Vo dataVo) {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return JsonResultUtils.success(Collections.emptyList());
}

// By Department Query All Box ID
List<Long> locationIds = listUserLocationIds(dataVo.getDepartIds());
if(locationIds == null) {
return JsonResultUtils.success(Collections.emptyList());
}

// By Group ID Query All Camera ID
List<Long> cameraIds = new ArrayList<>();
if(ObjectUtil.isNotEmpty(dataVo.getCameraGroupIds()) && ObjectUtil.isEmpty(dataVo.getCameraIds())) {
List<Long> cameraGroupIds = dataVo.getCameraGroupIds();
for(Long cameraGroupId: cameraGroupIds) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(cameraGroupId);
if(ObjectUtil.isNotEmpty(cameraGroupItemList)) {
List<Long> currentCameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getId).collect(Collectors.toList());
cameraIds.addAll(currentCameraIds);
}
}

if(ObjectUtil.isEmpty(cameraIds)) {
return JsonResultUtils.success(Collections.emptyList());
}
}

// like Result via Check select Camera, rule Direct connect Fill Charge Check select Camera
if(ObjectUtil.isNotEmpty(dataVo.getCameraIds())) {
cameraIds.addAll(dataVo.getCameraIds());
}

// Query Camera
List<Camera> cameraList = cameraService.listData7(locationIds, cameraIds, dataVo.getRunning());

//
List<CameraCassInfoDTO> cameraCassInfoDTOS = new ArrayList<>();
for(Camera camera: cameraList) {
CameraCassInfoDTO cameraCassInfoDTO = new CameraCassInfoDTO();
cameraCassInfoDTO.setId(camera.getId());
cameraCassInfoDTO.setName(camera.getName());
cameraCassInfoDTOS.add(cameraCassInfoDTO);
}
return JsonResultUtils.success(cameraCassInfoDTOS);
}

@ApiOperation("By The belong Organization, The belong Group, Check select Camera Query Camera Page List")
@SaCheckPermission(value = {"edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping("listPage9")
@ResponseBody
public PageResult<?> listPage9(@RequestBody CameraListData7Vo dataVo) {
// By Department Query All Box ID
List<Long> locationIds = listUserLocationIds(dataVo.getDepartIds());
if(locationIds == null) {
return PageResultUtils.success(0L, Collections.emptyList());
}

// By Group ID Query All Camera ID
List<Long> cameraIds = new ArrayList<>();
if(ObjectUtil.isNotEmpty(dataVo.getCameraGroupIds()) && ObjectUtil.isEmpty(dataVo.getCameraIds())) {
List<Long> cameraGroupIds = dataVo.getCameraGroupIds();
for(Long cameraGroupId: cameraGroupIds) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(cameraGroupId);
if(ObjectUtil.isNotEmpty(cameraGroupItemList)) {
List<Long> currentCameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getId).collect(Collectors.toList());
cameraIds.addAll(currentCameraIds);
}
}

if(ObjectUtil.isEmpty(cameraIds)) {
return PageResultUtils.success(0L, Collections.emptyList());
}
}

// like Result via Check select Camera, rule Direct connect Fill Charge Check select Camera
if(ObjectUtil.isNotEmpty(dataVo.getCameraIds())) {
cameraIds.addAll(dataVo.getCameraIds());
}

// Page Query Camera
IPage<Camera> pageResult = cameraService.listPage9(dataVo.getPage(), dataVo.getLimit(), locationIds, cameraIds, dataVo.getRunning());
List<Camera> cameraList = pageResult.getRecords();
if(ObjectUtil.isEmpty(cameraList)) {
return PageResultUtils.success(0L, Collections.emptyList());
}

// Data Back
List<CameraCassInfoDTO> cameraCassInfoDTOS = new ArrayList<>();
for(Camera camera: cameraList) {
CameraCassInfoDTO cameraCassInfoDTO = new CameraCassInfoDTO();
cameraCassInfoDTO.setId(camera.getId());
cameraCassInfoDTO.setName(camera.getName());
cameraCassInfoDTOS.add(cameraCassInfoDTO);
}
return PageResultUtils.success(pageResult.getTotal(), cameraCassInfoDTOS);
}

/**
* Get User can Query Box List
* Return Data,null- table show not need again Query,empty- Continue Query
* @param departIds
* @return
*/
private List<Long> listUserLocationIds(List<Long> departIds) {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return null;
}

// Query All Box List
List<Location> locationList = locationService.listInferBox();

// By Department Query All Box ID
List<Long> locationIds = new ArrayList<>();

// Frontend Query Department Condition
if(ObjectUtil.isNotEmpty(departIds)) {
// Non Management member, Query User belong belong Department Visible Box, after will Frontend Filter Condition again Limit make Query
if(account.getIsSuper() == null || account.getIsSuper()!= 1) {
List<Long> userDepartIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
if(ObjectUtil.isEmpty(userDepartIds)) {
return null;
} else {
for(Location location: locationList) {
if(userDepartIds.contains(location.getDepartId()) && departIds.contains(location.getDepartId())) {
locationIds.add(location.getId());
}
}

if(ObjectUtil.isEmpty(locationIds)) {
return null;
}
}
} else {
for(Location location: locationList) {
if(departIds.contains(location.getDepartId())) {
locationIds.add(location.getId());
}
}

if(ObjectUtil.isEmpty(locationIds)) {
return null;
}
}
} else {
// Non Management member, Query User belong belong Department Visible Box
if(account.getIsSuper() == null || account.getIsSuper()!= 1) {
List<Long> userDepartIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
if(ObjectUtil.isEmpty(userDepartIds)) {
return Collections.emptyList();
} else {
for(Location location: locationList) {
if(userDepartIds.contains(location.getDepartId())) {
locationIds.add(location.getId());
}
}

if(ObjectUtil.isEmpty(locationIds)) {
return null;
}
}
}
}
return locationIds;
}
}
