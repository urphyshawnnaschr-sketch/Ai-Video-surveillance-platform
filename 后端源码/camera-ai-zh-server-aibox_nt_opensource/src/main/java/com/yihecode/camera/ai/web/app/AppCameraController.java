package com.yihecode.camera.ai.web.app;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.javacv.VideoInfo;
import com.yihecode.camera.ai.netty.MessageSendHandler;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.CameraAddRequest;
import com.yihecode.camera.ai.netty.data.CameraAddResponse;
import com.yihecode.camera.ai.netty.data.CameraDelRequest;
import com.yihecode.camera.ai.netty.data.CameraDelResponse;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
* app End Camera Phase close
* @author Abyss
* @date 2023/12/23 12:49
*/
@Slf4j
@Api(tags = "app End _ Camera Phase close")
@SaCheckLogin
@Controller
@RequestMapping({"/app/camera"})
public class AppCameraController {

    @Autowired
    private CameraService cameraService;
    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;
    @Autowired
    private ReportPeriodService reportPeriodService;

    @Autowired
    private MessageSendHandler messageSendHandler;

    @Value("${dataModelsDir}")
    private String dataModelDir;

    @ApiOperation("Query Camera Pagination Data List")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "page", value = "Page Number"),
            @ApiImplicitParam(name = "limit", value = "Page Size"),
            @ApiImplicitParam(name = "name", value = "Camera Name"),
            @ApiImplicitParam(name = "running", value = "Run Status,0- not Run,1- In Run"),
            @ApiImplicitParam(name = "locationType", value = "Box Type, Fixed Fixed Value:2"),
            @ApiImplicitParam(name = "locationId", value = "Box ID")
    })
    @GetMapping("listPage")
    @ResponseBody
    public PageResult<List<Camera>> listPage(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer limit,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) Integer running,
                                             @RequestParam(required = false) String locationType,
                                             @RequestParam(required = false) Long locationId) {
        IPage<Camera> pageObj = new Page<>(page, limit);
        Camera queryCamera = new Camera();
        queryCamera.setName(name);
        queryCamera.setLocationType(locationType);
        queryCamera.setLocationId(locationId);
        queryCamera.setRunning(running);
        IPage<Camera> pageResult = cameraService.listPage(pageObj, queryCamera);
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
}

return PageResultUtils.success(pageResult.getTotal(), dataList);
}


@ApiOperation("Query Camera Pagination Data List")
@ApiImplicitParam(name ="locationType", value ="Category Type")
@GetMapping("getBanner")
@ResponseBody
public JsonResult<?> listPage(String locationType) {
IPage<Camera> pageObj = new Page<>(1, 5);
Camera queryCamera = new Camera();
queryCamera.setLocationType(locationType);
queryCamera.setRunning(1);
IPage<Camera> pageResult = cameraService.listPage(pageObj, queryCamera);
List<Camera> records = pageResult.getRecords();
return JsonResultUtils.success(records);
}


@ApiOperation("Save Camera Algorithm")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="cameraId", value ="Camera ID"),
@ApiImplicitParam(name ="algorithmvos", value ="Relate Algorithm ID List")
})
@PostMapping("saveCameraAlgorithmBatch")
@ResponseBody
public JsonResult<?> saveCameraAlgorithmBatch(Long cameraId, String algorithmvos) {
if(StrUtil.isBlank(algorithmvos)) {
return JsonResultUtils.fail("not has Select Algorithm");
}

String[] algorithmIds = algorithmvos.split(",");
List<Long> algoIds = new ArrayList<>();
for(String algoId: algorithmIds) {
algoIds.add(Long.parseLong(algoId));
}

Camera camera = cameraService.getById(cameraId);
if(camera == null) {
return JsonResultUtils.fail("not has Select Camera or Deleted");
}

Location location = locationService.getById(camera.getId());
if(location == null) {
return JsonResultUtils.fail("Camera not Relate Box Device");
}

//
List<Long> existAlgoIds = new ArrayList<>();
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(cameraId);
if(cameraAlgorithms!= null) {
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
existAlgoIds.add(cameraAlgorithm.getAlgorithmId());
}
}

// Delete Cancel Relate Algorithm
boolean call = false;
List<CameraAlgorithm> deleteCameraAlgorithms = new ArrayList<>();
for(Long algoId: existAlgoIds) {
if(!algoIds.contains(algoId)) {
call = true;
CameraAlgorithm cameraAlgorithm = cameraAlgorithmService.getByCameraAndAlgorithmId(cameraId, algoId);
if(cameraAlgorithm == null) {
continue;
}
deleteCameraAlgorithms.add(cameraAlgorithm);
cameraAlgorithmService.deleteByCameraAndAlgorithm(cameraId, algoId);
//reportPeriodService.deleteByCameraAndAlgorithm(cameraId, algoId);
}
}

// Add new Camera
List<CameraAlgorithm> addCameraAlgorithms = new ArrayList<>();
for(Long algoId: algoIds) {
if(!existAlgoIds.contains(algoId)) {
Algorithm algorithm = algorithmService.getById(algoId);
if(algorithm == null) {
return JsonResultUtils.fail("find not to Algorithm");
}

CameraAlgorithm cameraAlgorithm = new CameraAlgorithm();
cameraAlgorithm.setCameraId(cameraId);
cameraAlgorithm.setAlgorithmId(algoId);
cameraAlgorithm.setConfidence(0.5f);
cameraAlgorithm.setMarkPoints("");
cameraAlgorithm.setImagePoints("");
cameraAlgorithm.setAlgorithmVersion(findAlgoVer(camera.getLocationId(), algorithm));
cameraAlgorithm.setConfidence(0.5f);
cameraAlgorithm.setMarkPoints("");
cameraAlgorithm.setImagePoints("");
cameraAlgorithmService.save(cameraAlgorithm);

addCameraAlgorithms.add(cameraAlgorithm);

this.saveDefaultReportPeriod(cameraId, algorithm.getId());

call = true;
}
}

List<CameraAlgorithm> cameraAlgorithms1 = cameraAlgorithmService.listByCamera(cameraId);
if(cameraAlgorithms1 == null || cameraAlgorithms1.isEmpty()) {
for(CameraAlgorithm cameraAlgorithm: deleteCameraAlgorithms) {
cameraAlgorithmService.save(cameraAlgorithm);
}

return JsonResultUtils.fail("Camera At Least need Relate One Algorithm");
}

// Update Box
if(call && camera.getRunning()!= null && camera.getRunning() == 1) {
CameraAddResponse cameraAddResponse = messageSendHandler.sendAddCamera(location, camera);
if(cameraAddResponse.isStatus()) {
for(CameraAlgorithm cameraAlgorithm: deleteCameraAlgorithms) {
reportPeriodService.deleteByCameraAndAlgorithm(cameraId, cameraAlgorithm.getAlgorithmId());
}
return JsonResultUtils.success();
} else {
for(CameraAlgorithm cameraAlgorithm: deleteCameraAlgorithms) {
cameraAlgorithmService.save(cameraAlgorithm);
}

for(CameraAlgorithm cameraAlgorithm: addCameraAlgorithms) {
cameraAlgorithmService.removeById(cameraAlgorithm.getId());
}

return JsonResultUtils.fail(cameraAddResponse.getMsg());
}
}

return JsonResultUtils.success();
}

@ApiOperation("Save Camera and Algorithm Relate")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="cameraId", value ="Camera ID"),
@ApiImplicitParam(name ="algorithmId", value ="Algorithm ID"),
@ApiImplicitParam(name ="confidence", value ="Confidence")
})
@PostMapping("saveCameraAlgorithm")
@ResponseBody
public JsonResult<?> saveCameraAlgorithm(Long cameraId, Long algorithmId, Float confidence) {
if(confidence == null || confidence <= 0 || confidence > 1) {
return JsonResultUtils.fail("Confidence Param must In {0-1} of between");
}

Camera camera = cameraService.getById(cameraId);
if(camera == null) {
return JsonResultUtils.fail("find not to Camera or Deleted");
}

Location location = locationService.getById(camera.getLocationId());
if(location == null) {
return JsonResultUtils.fail("not Relate Box Device");
}

boolean call = false;
boolean isAdd = false;
CameraAlgorithm cameraAlgorithm = cameraAlgorithmService.getByCameraAndAlgorithmId(cameraId, algorithmId);
if(cameraAlgorithm == null) {
cameraAlgorithm = new CameraAlgorithm();
cameraAlgorithm.setCameraId(cameraId);
cameraAlgorithm.setAlgorithmId(algorithmId);
cameraAlgorithm.setConfidence(confidence);
cameraAlgorithmService.save(cameraAlgorithm);

this.saveDefaultReportPeriod(cameraId, algorithmId);

call = true;
isAdd = true;
} else {
CameraAlgorithm updateCameraAlgorithm = new CameraAlgorithm();
updateCameraAlgorithm.setId(cameraAlgorithm.getId());
updateCameraAlgorithm.setConfidence(confidence);
cameraAlgorithmService.updateById(updateCameraAlgorithm);

if(!confidence.equals(cameraAlgorithm.getConfidence())) {
call = true;
}
}

if(call && camera.getRunning()!= null && camera.getRunning() == 1) {
CameraDelResponse response = messageSendHandler.sendDelCamera(location, camera);
if(response.isStatus()) {
// Delete Alert Hour Segment
return JsonResultUtils.success();
}

// Rollback
if(isAdd) {
// Delete Insert in Data
cameraAlgorithmService.removeById(cameraAlgorithm.getId());
reportPeriodService.deleteByCameraAndAlgorithm(cameraId, algorithmId);
} else {
// Rollback
CameraAlgorithm updateCameraAlgorithm = new CameraAlgorithm();
updateCameraAlgorithm.setId(cameraAlgorithm.getId());
updateCameraAlgorithm.setConfidence(cameraAlgorithm.getConfidence());
cameraAlgorithmService.updateById(updateCameraAlgorithm);
}
return JsonResultUtils.fail(response.getMsg());
}

return JsonResultUtils.success();
}

@ApiOperation("Delete Camera Relate Algorithm")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="cameraId", value ="Camera ID"),
@ApiImplicitParam(name ="algorithmId", value ="Algorithm ID")
})
@PostMapping("deleteCameraAlgorithm")
@ResponseBody
public JsonResult<?> deleteCameraAlgorithm(Long cameraId, Long algorithmId) {
Camera camera = cameraService.getById(cameraId);
if(camera == null) {
return JsonResultUtils.fail("find not to Camera or Deleted");
}

Location location = locationService.getById(camera.getLocationId());
if(location == null) {
return JsonResultUtils.fail("Camera not Relate Box Device");
}

List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(cameraId);
if(cameraAlgorithms == null || cameraAlgorithms.isEmpty()) {
return JsonResultUtils.fail("not Relate Algorithm");
}

if(cameraAlgorithms.size() == 1) {
return JsonResultUtils.fail("not can Delete Relate, Camera At Least Need Relate One Algorithm");
}

// Query Relate close System
CameraAlgorithm cameraAlgorithm = cameraAlgorithmService.getByCameraAndAlgorithmId(cameraId, algorithmId);
if(cameraAlgorithm == null) {
return JsonResultUtils.fail("not Relate Algorithm");
}

// Delete Relate close System
cameraAlgorithmService.removeById(cameraAlgorithm.getId());

// like Result Box Enable Inference Status
if(camera.getRunning()!= null && camera.getRunning() == 1) {
CameraDelResponse response = messageSendHandler.sendDelCamera(location, camera);
if(response.isStatus()) {
// Delete Alert Hour Segment
reportPeriodService.deleteByCameraAndAlgorithm(cameraId, algorithmId);
return JsonResultUtils.success();
}

// Rollback
cameraAlgorithmService.save(cameraAlgorithm);
return JsonResultUtils.fail(response.getMsg());
} else {
//
reportPeriodService.deleteByCameraAndAlgorithm(cameraId, algorithmId);
}
return JsonResultUtils.success();
}

@ApiOperation("Modify Camera Alarm Interval and Recognition Interval")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="id", value ="Camera ID"),
@ApiImplicitParam(name ="intervalTime", value ="Recognition Interval"),
@ApiImplicitParam(name ="alarmInterval", value ="Alarm Interval")
})
@PostMapping("save")
@ResponseBody
public JsonResult<?> save(Long id, Float intervalTime, Float alarmInterval) {
Camera camera = cameraService.getById(id);
if(camera == null) {
return JsonResultUtils.fail("not Select Camera or Deleted");
}

if(intervalTime == null || intervalTime <= 0) {
return JsonResultUtils.fail("Calculate Frequency Param Error, Need big at 0");
}

if(alarmInterval == null || alarmInterval <= 0 || alarmInterval < intervalTime) {
return JsonResultUtils.fail("show show Frequency Param Error, Need big at Calculate Frequency");
}

// like Result Data has Change, Need Call Box Notification
boolean hasCall = false;
if (camera.getIntervalTime() == null
|| camera.getAlarmInterval() == null
||!intervalTime.equals(camera.getIntervalTime())
||!alarmInterval.equals(camera.getAlarmInterval())
) {
hasCall = true;
}

Location location = locationService.getById(camera.getLocationId());
if(location == null) {
return JsonResultUtils.fail("Camera not Relate Box Device");
}

if(hasCall && camera.getRunning()!= null && camera.getRunning() == 1) {
//
Camera updateCamera = new Camera();
updateCamera.setId(id);
updateCamera.setIntervalTime(intervalTime);
updateCamera.setAlarmInterval(alarmInterval);
cameraService.updateById(updateCamera);

CameraAddResponse cameraAddResponse = messageSendHandler.sendAddCamera(location, cameraService.getById(id));
if (cameraAddResponse.isStatus()) {
return JsonResultUtils.success();
}

// modify return Remove
updateCamera.setAlarmInterval(camera.getAlarmInterval());
updateCamera.setIntervalTime(camera.getIntervalTime());
cameraService.updateById(updateCamera);

return JsonResultUtils.fail(cameraAddResponse.getMsg());
}
return JsonResultUtils.success();
}

// check find Algorithm Version
private String findAlgoVer(Long locId, Algorithm algorithm) {
List<CameraAlgorithm> caList = cameraAlgorithmService.listByAlgorithmAndBoxId(algorithm.getId(), locId);
if(caList == null || caList.isEmpty()) {
// check find Local File System most big Algorithm Version
String path = FileUtils.pathTo(dataModelDir +"/"+ algorithm.getPlatform() +"/"+ algorithm.getNameEn());
if(!FileUtil.exist(path)) {// not has Download Model File
return"";
}

List<String> fileNames = FileUtil.listFileNames(path);
if(fileNames == null || fileNames.isEmpty()) {
return"";
}

// Filter select All zip File
List<String> zipFileNames = new ArrayList<>();
for(String fileName: fileNames) {
String ext = FileUtil.extName(fileName);
if("zip".equalsIgnoreCase(ext)) {
zipFileNames.add(fileName);
}
}

Collections.sort(zipFileNames);

// most big File Version No
String maxFileName = zipFileNames.get(zipFileNames.size() - 1);
String[] parts = maxFileName.split("-");
return parts[2];
} else {
for(CameraAlgorithm ca: caList) {
if(StrUtil.isNotBlank(ca.getAlgorithmVersion())) {
return ca.getAlgorithmVersion();
}
}
}
return"";
}

// Save Default Alarm Hour Segment
private void saveDefaultReportPeriod(Long cameraId, Long algorithmId) {
ReportPeriod reportPeriod = new ReportPeriod();
reportPeriod.setCameraId(cameraId);
reportPeriod.setAlgorithmId(algorithmId);
reportPeriod.setStartText("00:00");
reportPeriod.setStartTime(0);
reportPeriod.setEndText("23:59");
reportPeriod.setEndTime(2359);
reportPeriodService.save(reportPeriod);
}
}
