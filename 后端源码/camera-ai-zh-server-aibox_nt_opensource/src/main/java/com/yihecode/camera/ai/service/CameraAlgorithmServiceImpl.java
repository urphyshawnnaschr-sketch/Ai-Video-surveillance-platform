package com.yihecode.camera.ai.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.mapper.CameraAlgorithmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
* Camera and Algorithm Relate Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class CameraAlgorithmServiceImpl extends ServiceImpl<CameraAlgorithmMapper, CameraAlgorithm> implements CameraAlgorithmService {

    //
@Autowired
private AlgorithmService algorithmService;

@Autowired
@Lazy
private CameraService cameraService;

@Autowired
private ReportPeriodService reportPeriodService;

/**
*
* @param algorithmId
* @return
*/
@Override
public List<CameraAlgorithm> listByAlgorithm(Long algorithmId) {
LambdaQueryWrapper<CameraAlgorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraAlgorithm::getAlgorithmId, algorithmId);
List<CameraAlgorithm> list = this.list(queryWrapper);
if(list == null) {
return new ArrayList<>();
}
return list;
}

/**
*
* @param cameraId
* @return
*/
@Override
public List<CameraAlgorithm> listByCamera(Long cameraId) {
LambdaQueryWrapper<CameraAlgorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraAlgorithm::getCameraId, cameraId);
List<CameraAlgorithm> list = this.list(queryWrapper);
if(list == null) {
return new ArrayList<>();
}
return list;
}

/**
*
* @param cameraId
*/
@Override
public void deleteByCamera(Long cameraId) {
LambdaQueryWrapper<CameraAlgorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraAlgorithm::getCameraId, cameraId);
this.remove(queryWrapper);
}

/**
*
* @param cameraId
* @return
*/
public String getNames(Long cameraId) {
List<Algorithm> algorithmList = this.algorithmService.list();
if (algorithmList == null) {
algorithmList = new ArrayList<>();
}
Map<Long, String> algorithmMap = new HashMap<>();
for (Algorithm algorithm: algorithmList) {
algorithmMap.put(algorithm.getId(), algorithm.getName());
}
List<CameraAlgorithm> cameraAlgorithmList = listByCamera(cameraId);
List<String> names = new ArrayList<>();
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
String name = algorithmMap.get(cameraAlgorithm.getAlgorithmId());
names.add(name == null?"-": name);
}
if (names.isEmpty()) {
return"No Binds";
}
return String.join("|", names);
}

/**
* By Camera ID and Algorithm ID Query
*
* @param cameraId
* @param algorithmId
* @return
*/
@Override
public CameraAlgorithm getByCameraAndAlgorithmId(Long cameraId, Long algorithmId) {
LambdaQueryWrapper<CameraAlgorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraAlgorithm::getCameraId, cameraId);
queryWrapper.eq(CameraAlgorithm::getAlgorithmId, algorithmId);
return this.getOne(queryWrapper);
}

/**
* By Camera ID and Algorithm ID Delete
*
* @param cameraId
* @param algorithmId
*/
@Override
public void deleteByCameraAndAlgorithm(Long cameraId, Long algorithmId) {
LambdaQueryWrapper<CameraAlgorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraAlgorithm::getCameraId, cameraId);
queryWrapper.eq(CameraAlgorithm::getAlgorithmId, algorithmId);
this.remove(queryWrapper);
}

@Override
@Transactional(rollbackFor = Exception.class)
public void saveCameraAlgorithm(CameraAlgorithm cameraAlgorithm) {
this.deleteByCameraAndAlgorithm(cameraAlgorithm.getCameraId(), cameraAlgorithm.getAlgorithmId());
Camera camera = cameraService.getById(cameraAlgorithm.getCameraId());
// Region points Convert
String markPointsStr = cameraAlgorithm.getMarkPoints();
if(StrUtil.isEmpty(markPointsStr) ||"[]".equals(markPointsStr)) {
cameraAlgorithm.setMarkPoints("");
cameraAlgorithm.setImagePoints("");
} else {
cameraAlgorithm.setImagePoints(markToImagePoints(markPointsStr, camera.getScaleRatio()));
}
// Line Segment points Convert
String lineMarkPointsStr = cameraAlgorithm.getLineMarkPoints();
if(StrUtil.isEmpty(lineMarkPointsStr) ||"[]".equals(lineMarkPointsStr)) {
cameraAlgorithm.setLineMarkPoints("");
cameraAlgorithm.setLineImagePoints("");
} else {
cameraAlgorithm.setLineImagePoints(markToImagePoints(lineMarkPointsStr, camera.getScaleRatio()));
}
this.save(cameraAlgorithm);

// Add Default Alert Hour Segment
List<ReportPeriod> reportPeriodList = reportPeriodService.listData(camera.getId(), cameraAlgorithm.getAlgorithmId());
if(reportPeriodList.isEmpty()) {
//
ReportPeriod reportPeriod = new ReportPeriod();
reportPeriod.setCameraId(camera.getId());
reportPeriod.setAlgorithmId(cameraAlgorithm.getAlgorithmId());
reportPeriod.setStartText("00:00");
reportPeriod.setStartTime(0);
reportPeriod.setEndText("23:59");
reportPeriod.setEndTime(2359);
reportPeriodService.save(reportPeriod);
}
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

@Override
public List<CameraAlgorithm> listByAlgorithmAndBoxId(Long algorithmId, Long boxId) {
List<Camera> cameraList = cameraService.listByBoxId(boxId);
if(cameraList == null || cameraList.isEmpty()) {
return new ArrayList<>();
}

//
List<Long> cameraIds = new ArrayList<>();
for (Camera camera: cameraList) {
cameraIds.add(camera.getId());
}

//
LambdaQueryWrapper<CameraAlgorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraAlgorithm::getAlgorithmId, algorithmId);
queryWrapper.in(CameraAlgorithm::getCameraId, cameraIds);
List<CameraAlgorithm> cameraAlgorithms = list(queryWrapper);
return cameraAlgorithms == null? new ArrayList<>(): cameraAlgorithms;
}

/**
* By Camera id Reset All Algorithm Run Status
*
* @param cameraId
* @param runStatus
*/
@Override
public void updateAlgorithmRunStatus(Long cameraId, Integer runStatus) {
LambdaUpdateWrapper<CameraAlgorithm> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(CameraAlgorithm::getRunStatus, runStatus);
updateWrapper.set(CameraAlgorithm::getRunTime, new Date());
updateWrapper.eq(CameraAlgorithm::getCameraId, cameraId);
this.update(updateWrapper);
}

/**
* Query All Algorithm ids
*
* @return
*/
@Override
public List<Long> listAllAlgorithmIds() {
List<Long> algorithmIds = new ArrayList<>();
List<CameraAlgorithm> cameraAlgorithms = this.list();
if(cameraAlgorithms == null || cameraAlgorithms.isEmpty()) {
return algorithmIds;
}

for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
if(!algorithmIds.contains(cameraAlgorithm.getAlgorithmId())) {
algorithmIds.add(cameraAlgorithm.getAlgorithmId());
}
}
return algorithmIds;
}

/**
* By Algorithm id and Run Status Query
*
* @param algorithmId
* @param runStatus
* @return
*/
@Override
public List<CameraAlgorithm> listRunningByAlgorithm(Long algorithmId, int runStatus) {
LambdaQueryWrapper<CameraAlgorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraAlgorithm::getAlgorithmId, algorithmId);
queryWrapper.in(CameraAlgorithm::getRunStatus, runStatus);
return list(queryWrapper);
}
}
