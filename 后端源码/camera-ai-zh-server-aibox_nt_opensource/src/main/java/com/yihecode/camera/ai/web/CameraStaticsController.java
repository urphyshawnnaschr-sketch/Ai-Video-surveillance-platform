package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.vo.CameraGroupItemListVo;
import com.yihecode.camera.ai.web.vo.CameraListVo;
import com.yihecode.camera.ai.web.vo.IdVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Camera Data Count
* Count Total, Run Total, not Run Total, Exception Total
*
* @author zhou
* @since 2025.6.18
*/
@RestController
@RequestMapping("camera/statics")
public class CameraStaticsController {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CameraGroupService cameraGroupService;

    @Autowired
    private CameraGroupItemService cameraGroupItemService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private ApDepartService apDepartService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AlgorithmService algorithmService;

    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @PostMapping("by/box")
    public JsonResult<?> byBox(@RequestBody IdVo idVo) {
        Long boxId = (idVo.getId() == null || idVo.getId() == 0) ? null : idVo.getId();
        if(boxId == null) {
            return JsonResultUtils.success(handleAll(new CameraGroupItemListVo()));
        } else {
            int countAll = 0, countRun = 0, countClose = 0, countExp = 0;

            List<Camera> cameraList = cameraService.listByBoxId(boxId);
            for(Camera camera : cameraList) {
                if(camera.getState() == null || camera.getState() != 0) {
                    continue;
                }
                countAll++;

                if(camera.getRunning() != null && camera.getRunning() == 1 && camera.getAiboxExecStatus() != null && camera.getAiboxExecStatus() == 1000) { //Run in
countRun++;
}
if(camera.getRunning()!= null && camera.getRunning() == 0) {// Close in
countClose++;
}
if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 3000) {// Exception in
countExp++;
}
}

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("countAll", countAll);
dataMap.put("countRun", countRun);
dataMap.put("countClose", countClose);
dataMap.put("countExp", countExp);
return JsonResultUtils.success(dataMap);
}
}

/**
* By Group in Line Count
* @return
*/
@SaCheckPermission(value = {"edgePlatform-groupView"}, mode = SaMode.OR)
@PostMapping("by/group")
public JsonResult<?> byGroup(@RequestBody CameraGroupItemListVo listVo) {
Long groupId = listVo.getGroupId();
if(groupId == null) {// All Select
return JsonResultUtils.success(handleAll(listVo));
} else if(groupId == 0) {// not Group Select
return JsonResultUtils.success(handleNotGroup(listVo));
} else {// Refer Fixed Group Select
int countAll = 0, countRun = 0, countClose = 0, countExp = 0;

// Camera List
List<Camera> cameraList = cameraService.list();
Map<Long, Camera> cameraMap = cameraList.stream().collect(Collectors.toMap(Camera::getId, Function.identity()));

// Query Current Node and All child Node
List<CameraGroup> cameraGroupList = cameraGroupService.getCurrentAndChild(listVo.getGroupId());

// Query Each Group Camera Data
for(CameraGroup cameraGroup: cameraGroupList) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(cameraGroup.getId());
if(!cameraGroupItemList.isEmpty()) {
for(CameraGroupItem cameraGroupItem: cameraGroupItemList) {
if(!cameraMap.containsKey(cameraGroupItem.getCameraId())) {
continue;
}

Camera camera = cameraMap.get(cameraGroupItem.getCameraId());
if(camera.getState() == null || camera.getState()!= 0) {
continue;
}

countAll++;

if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 1000) {// Run in
countRun++;
}
if(camera.getRunning()!= null && camera.getRunning() == 0) {// Close in
countClose++;
}
if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 3000) {// Exception in
countExp++;
}
}
}
}

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("countAll", countAll);
dataMap.put("countRun", countRun);
dataMap.put("countClose", countClose);
dataMap.put("countExp", countExp);
return JsonResultUtils.success(dataMap);
}
}

private Map<String, Object> handleAll(CameraGroupItemListVo listVo) {
int countAll = 0, countRun = 0, countClose = 0, countExp = 0;
List<Camera> cameraList = null; //;cameraService.list();
if(StrUtil.isNotBlank(listVo.getName())) {
cameraList = cameraService.listLikeName(listVo.getName());
} else {
cameraList = cameraService.list();
}

for(Camera camera: cameraList) {
if(camera.getState() == null || camera.getState()!= 0) {
continue;
}
countAll++;

if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 1000) {// Run in
countRun++;
}
if(camera.getRunning()!= null && camera.getRunning() == 0) {// Close in
countClose++;
}
if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 3000) {// Exception in
countExp++;
}
}

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("countAll", countAll);
dataMap.put("countRun", countRun);
dataMap.put("countClose", countClose);
dataMap.put("countExp", countExp);
return dataMap;
}

private Map<String, Object> handleNotGroup(CameraGroupItemListVo listVo) {
int countAll = 0, countRun = 0, countClose = 0, countExp = 0;

List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.list();
List<Long> cameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getCameraId).collect(Collectors.toList());

List<Camera> cameraList = null;
if(ObjectUtil.isEmpty(cameraIds)) {// not has Camera Config, rule Query all table
if(StrUtil.isNotBlank(listVo.getName())) {
cameraList = cameraService.listLikeName(listVo.getName());
} else {
cameraList = cameraService.listData();
}
} else {// By Camera in Line Filter Query
cameraList = cameraService.getCountNotContainCameraId(cameraIds, listVo.getName());
}

for(Camera camera: cameraList) {
countAll++;

if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 1000) {// Run in
countRun++;
}
if(camera.getRunning()!= null && camera.getRunning() == 0) {// Close in
countClose++;
}
if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 3000) {// Exception in
countExp++;
}
}

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("countAll", countAll);
dataMap.put("countRun", countRun);
dataMap.put("countClose", countClose);
dataMap.put("countExp", countExp);
return dataMap;
}

/**
* Point Bit Management -- Camera Query
* @param listVo
* @return
*/
@SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
@PostMapping("by/query")
public JsonResult<Map<String, Object>> byQuery(@RequestBody CameraListVo listVo) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("countAll", 0);
dataMap.put("countRun", 0);
dataMap.put("countClose", 0);
dataMap.put("countExp", 0);

// Query Current Account belong belong Department
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return JsonResultUtils.success(dataMap);
}

// Query User belong belong Department & child Department
List<Long> userDepartIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
boolean isSuper = false;
if(account.getIsSuper()!= null && account.getIsSuper() == 1) {
isSuper = true;
}

// this Two Condition not should the same Hour Exist, departId and locationId Two Condition not can same Hour out current
Long departId = listVo.getDepartId();

// has Department ID Condition, rule By Department Query belong belong Box, again by belong belong Box Remove Query Camera
if(departId!= null && departId!= 9999L) {
// Non Management member, Need Determine Current User belong belong Department and child part part Whether Contain the ID
if(!isSuper) {
// not Contain, Back
if(!userDepartIds.contains(departId)) {
return JsonResultUtils.success(dataMap);
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
return JsonResultUtils.success(dataMap);
}

// Set Box Query Condition
List<Long> locationIds = locationResults.stream().map(Location::getId).collect(Collectors.toList());
listVo.setLocationIds(locationIds);
}

// has Department Condition, But is for false Department ID, Query no belong belong Organization Camera
if(departId!= null && departId == 9999L) {
// Non Management member, not can Query no belong belong Organization Camera
if(!isSuper) {
return JsonResultUtils.success(dataMap);
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
return JsonResultUtils.success(dataMap);
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
return JsonResultUtils.success(dataMap);
}
}

// Page Query
List<Camera> cameraList = cameraService.listAllV2(listVo);

int countAll = 0, countRun = 0, countClose = 0, countExp = 0;
for(Camera camera: cameraList) {
countAll++;

if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 1000) {// Run in
countRun++;
}
if(camera.getRunning()!= null && camera.getRunning() == 0) {// Close in
countClose++;
}
if(camera.getRunning()!= null && camera.getRunning() == 1 && camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 3000) {// Exception in
countExp++;
}
}

dataMap.put("countAll", countAll);
dataMap.put("countRun", countRun);
dataMap.put("countClose", countClose);
dataMap.put("countExp", countExp);
return JsonResultUtils.success(dataMap);
}

}
