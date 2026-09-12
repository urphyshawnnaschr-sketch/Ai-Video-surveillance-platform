package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraAlgorithm;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.CameraAlgorithmService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Camera and Algorithm Relate Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Camera and Algorithm Relate Management")
@Slf4j
@SaCheckLogin
@Controller
@RequestMapping({"/camera/algorithm"})
public class CameraAlgorithmController {

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CameraService cameraService;

    @Value("${dataModelsDir}")
    public String MODEL_DIR;

    /**
* Query Data List
* @param cameraId
* @return
*/
    @ApiOperation("Query Data List")
    @ApiImplicitParam(name = "cameraId", value = "Camera id")
    @SaCheckPermission(value = {"gb-channel-list", "edgePlatform-boxManagement"}, mode = SaMode.OR)
    @PostMapping({"/listData"})
    @ResponseBody
    public PageResult<?> listData(Long cameraId, Long locationId, @RequestHeader("Lang") String language) throws IOException {
        if(cameraId == null && locationId == null) {
            return PageResultUtils.success(null, new ArrayList<>());
        }

        //Chip Type
String platform ="";

// like Result Camera ID Exist, rule by Camera ID Get Value
if(cameraId!= null) {
Camera camera = cameraService.getById(cameraId);
if(camera == null) {
return PageResultUtils.success(null, new ArrayList<>());
}

Location location = locationService.getById(camera.getLocationId());
if(location == null || StrUtil.isBlank(location.getPlatform())) {
return PageResultUtils.success(null, new ArrayList<>());
}
platform = location.getPlatform();
} else {// Box Device ID
Location location = locationService.getById(locationId);
if(location == null || StrUtil.isBlank(location.getPlatform())) {
return PageResultUtils.success(null, new ArrayList<>());
}
platform = location.getPlatform();
}

// find not to Chip Type
if(StrUtil.isBlank(platform)) {
return PageResultUtils.success(null, new ArrayList<>());
}

// All Algorithm
List<Algorithm> algorithmList = algorithmService.list();
if (algorithmList == null) {
algorithmList = new ArrayList<>();
}

// Bind Algorithm
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByCamera(cameraId);
if (cameraAlgorithmList == null) {
cameraAlgorithmList = new ArrayList<>();
}
Map<Long, CameraAlgorithm> cameraAlgorithmMap = cameraAlgorithmList.stream().collect(Collectors.toMap(CameraAlgorithm::getAlgorithmId, Function.identity()));

// Data whole Reason
List<Map<String, Object>> dataList = new ArrayList<>();
for(Algorithm algorithm: algorithmList) {
// Temp Measure Detection
if("temperatureAlarm".equalsIgnoreCase(algorithm.getNameEn()) ||"haikang".equalsIgnoreCase(algorithm.getNameEn())) {
// Data Organization
CameraAlgorithm cameraAlgorithm = cameraAlgorithmMap.get(algorithm.getId());
int autoPush = cameraAlgorithm == null? algorithm.getPushEnable(): cameraAlgorithm.getAutoPush();
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("id", algorithm.getId());
dataMap.put("name", algorithm.getName());
dataMap.put("nameEn", algorithm.getNameEn());
dataMap.put("confidence", 0.5);
dataMap.put("markPoints","");
dataMap.put("lineMarkPoints","");
dataMap.put("checked", cameraAlgorithm!= null);
dataMap.put("autoPush", autoPush);
dataList.add(dataMap);
}

String dest = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ algorithm.getNameEn());
if (!FileUtil.exist(dest)) {// Directory does not exist, Skip
continue;
}

// find out All File
List<String> fileNames = FileUtil.listFileNames(dest);
if ((fileNames == null || fileNames.isEmpty())) {// not has Algorithm Package
continue;
}

// Determine Whether has Compress Package File
boolean hasFile = false;
for(String fileName: fileNames) {
String ext = FileUtil.extName(fileName);
if("zip".equalsIgnoreCase(ext)) {
hasFile = true;
break;
}
}

// not has Compress Package File
if(!hasFile) {
continue;
}
// Current Config Algorithm Info
CameraAlgorithm cameraAlgorithm = cameraAlgorithmMap.get(algorithm.getId());
//float confidence = cameraAlgorithm == null? 0.5f: ((cameraAlgorithm.getConfidence() == null)? 0.5f: cameraAlgorithm.getConfidence());
String markPoints = cameraAlgorithm == null?"": ((cameraAlgorithm.getImagePoints() == null)?"": cameraAlgorithm.getImagePoints());
String lineMarkPoints = cameraAlgorithm == null?"": ((cameraAlgorithm.getLineMarkPoints() == null)?"": cameraAlgorithm.getLineMarkPoints());
int autoPush = cameraAlgorithm == null? algorithm.getPushEnable(): cameraAlgorithm.getAutoPush();

float confidence = 0.5f;
if(cameraAlgorithm!= null && cameraAlgorithm.getConfidence()!= null) {
confidence = cameraAlgorithm.getConfidence();
} else {
if ("helmet".equalsIgnoreCase(algorithm.getNameEn())) {// Safe all Cap
confidence = 0.88f;
} else if ("object".equalsIgnoreCase(algorithm.getNameEn())) {// Occupy Way
confidence = 0.55f;
} else if ("fire".equalsIgnoreCase(algorithm.getNameEn())) {// Fire Smoke
confidence = 0.75f;
} else if ("dozing".equalsIgnoreCase(algorithm.getNameEn())) {// Sleeping On Duty Detection
confidence = 0.75f;
} else if ("people".equalsIgnoreCase(algorithm.getNameEn())) {// Line Person Detection
confidence = 0.7f;
} else if ("shorts".equalsIgnoreCase(algorithm.getNameEn())) {// short Sleeve short Pants
confidence = 0.75f;
} else if ("car".equalsIgnoreCase(algorithm.getNameEn())) {// Vehicle Detection
confidence = 0.65f;
} else if ("callphone".equalsIgnoreCase(algorithm.getNameEn())) {// Playing Phone Machine Detection
confidence = 0.7f;
} else if ("fall".equalsIgnoreCase(algorithm.getNameEn())) {// Fall Detection
confidence = 0.7f;
} else if ("smoke".equalsIgnoreCase(algorithm.getNameEn())) {// Smoking Detection
confidence = 0.7f;
}
}

// Data Organization
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("id", algorithm.getId());
dataMap.put("name", algorithm.getName());
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
dataMap.put("name", algorithm.getEnglishName());
}
dataMap.put("nameEn", algorithm.getNameEn());
dataMap.put("confidence", confidence);
dataMap.put("markPoints", markPoints);
dataMap.put("lineMarkPoints", lineMarkPoints);
dataMap.put("checked", cameraAlgorithm!= null);
dataMap.put("autoPush", autoPush);
dataList.add(dataMap);
}
return PageResultUtils.success(null, dataList);
}

/**
* Calculate Frontend show show Use Draw make Data
* @author Abyss
* @date 2023/11/9 19:06
*/
// private String getShowPoints(String markPointsStr) {
// float xRatio = 636F / 2F;
// float yRatio = 357.75F / 2F;
// JSONArray showPointsGrp = new JSONArray(); // new Coordinate group Set combine
// JSONArray markPointsGrp = JSON.parseArray(markPointsStr);
// int grpSize = markPointsGrp.size();
// for(int k = 0; k < grpSize; k++) {
// JSONArray imagePoints = new JSONArray(); // new Coordinate group
// JSONArray markPoints = markPointsGrp.getJSONArray(k);
// int mpSize = markPoints.size();
// for(int m = 0; m < mpSize; m++) {
// JSONObject point = markPoints.getJSONObject(m);
// float x = point.getFloatValue("x");
// float y = point.getFloatValue("y");
// float xNew = x * xRatio;
// float yNew = y * yRatio;
// //
// JSONObject imagePoint = new JSONObject();
// imagePoint.put("x", Float.valueOf(xNew).intValue());
// imagePoint.put("y", Float.valueOf(yNew).intValue());
// imagePoints.add(imagePoint);
//}
// showPointsGrp.add(imagePoints);
//}
// return showPointsGrp.toJSONString();
//}

/**
* Save Camera Algorithm Draw make Data
* @author Abyss
* @date 2023/11/7 23:30
*/
@ApiOperation("Save Camera Algorithm Draw make Data")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="cameraAlgorithm", value ="Camera Algorithm Draw make Entity")
})
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult save(@RequestBody CameraAlgorithm cameraAlgorithm) {
try {
cameraAlgorithmService.saveCameraAlgorithm(cameraAlgorithm);
}catch (Exception e){
return JsonResultUtils.fail(e.getMessage());
}
return JsonResultUtils.success();
}

/**
* Delete Camera Algorithm Draw make Data
* @author Abyss
* @date 2023/11/7 23:30
*/
@ApiOperation("Delete Camera Algorithm Draw make Data")
@ApiImplicitParam(name ="id", value ="Data id")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(@RequestBody CameraAlgorithm cameraAlgorithm) {
try {
cameraAlgorithmService.deleteByCameraAndAlgorithm(cameraAlgorithm.getCameraId(), cameraAlgorithm.getAlgorithmId());
}catch (Exception e) {
return JsonResultUtils.fail(e.getMessage());
}
return JsonResultUtils.success();
}
}
