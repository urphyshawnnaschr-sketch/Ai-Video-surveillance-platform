package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.dto.CameraGroupCameraDTO;
import com.yihecode.camera.ai.web.dto.CameraGroupCameraListDTO;
import com.yihecode.camera.ai.web.vo.CameraGroupItemListVo;
import com.yihecode.camera.ai.web.vo.CameraGroupItemModifyVo;
import com.yihecode.camera.ai.web.vo.IdVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Camera Group
*
* @author zhou
* @since 2025.6.16
*/
@Api(tags = "Camera _ Group and Camera Relate Management")
@Slf4j
@RestController
@RequestMapping("camera/group/item")
public class CameraGroupItemController {

    @Autowired
    private CameraGroupService cameraGroupService;

    @Autowired
    private CameraGroupItemService cameraGroupItemService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    /**
* List Data
* @return
*/
    @ApiOperation(value = "List Data")
    @SaCheckPermission("edgePlatform-groupView")
    @PostMapping("page")
    public PageResult<List<CameraGroupCameraDTO>> page(@RequestBody CameraGroupItemListVo listVo) {
//if(listVo.getGroupId() == null) {
// return PageResultUtils.success(0L, new ArrayList<>());
//}

if(StrUtil.isNotBlank(listVo.getName())) {
List<Long> cameraIds = cameraService.listIdByName(listVo.getName());
if(cameraIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>());
}
listVo.setCameraIds(cameraIds);
}

// Query All
if(listVo.getGroupId() == null) {
return pageAll(listVo);
}

// form Single Process not Group Camera
if(listVo.getGroupId() == 0) {
return pageNotgroup(listVo);
}

List<CameraGroup> currentCameraGroupList = cameraGroupService.getCurrentAndChild(listVo.getGroupId());
List<Long> currentCameraGroupIds = currentCameraGroupList.stream().map(CameraGroup::getId).collect(Collectors.toList());

IPage<CameraGroupItem> pageResult = cameraGroupItemService.listPage(listVo.getPage(), listVo.getLimit(), currentCameraGroupIds, listVo.getCameraIds());
List<CameraGroupItem> cameraGroupItems = pageResult.getRecords();

List<CameraGroupCameraDTO> cameraGroupCameraDTOList = new ArrayList<>();
if(ObjectUtil.isNotEmpty(cameraGroupItems)) {
// All Camera
List<Camera> cameraList = cameraService.listData();
Map<Long, Camera> cameraMap = cameraList.stream().collect(Collectors.toMap(Camera::getId, Function.identity()));

// All Box
List<Location> locationList = locationService.list();
Map<Long, String> locationMap = locationList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Location::getId, Location::getName));

// All Group
List<CameraGroup> cameraGroupList = cameraGroupService.list();
Map<Long, String> cameraGroupMap = cameraGroupList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(CameraGroup::getId, CameraGroup::getName));

// All Algorithm
List<Algorithm> algorithmList = algorithmService.list();
Map<Long, String> algorithmMap = algorithmList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Algorithm::getId, Algorithm::getName));

// Data Process
for(CameraGroupItem cameraGroupItem: cameraGroupItems) {
Camera camera = cameraMap.get(cameraGroupItem.getCameraId());
if(camera == null) {
continue;
}

String boxName = locationMap.get(camera.getLocationId());
String groupName = cameraGroupMap.get(cameraGroupItem.getGroupId());

// Algorithm Name List
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(camera.getId());
List<String> algorithmNames = new ArrayList<>();
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
String algoName = algorithmMap.get(cameraAlgorithm.getAlgorithmId());
if(StrUtil.isNotBlank(algoName)) {
algorithmNames.add(algoName);
}
}

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

CameraGroupCameraDTO cameraGroupCameraDTO = new CameraGroupCameraDTO(camera);
cameraGroupCameraDTO.setFilename(camera.getFileName());
cameraGroupCameraDTO.setBoxName(boxName);
cameraGroupCameraDTO.setGroupName(groupName);
cameraGroupCameraDTO.setAlgorithmNames(algorithmNames);
cameraGroupCameraDTO.setExecMsg(camera.getAiboxExecMsg());
cameraGroupCameraDTO.setStatus(status);
cameraGroupCameraDTO.setVideoWidth(camera.getVideoWidth() == null? 0: camera.getVideoWidth());
cameraGroupCameraDTO.setVideoHeight(camera.getVideoHeight() == null? 0: camera.getVideoHeight());
cameraGroupCameraDTO.setVideoCodec(camera.getVideoCodec() == null?"": camera.getVideoCodec());
cameraGroupCameraDTOList.add(cameraGroupCameraDTO);
}
}
//Integer size = cameraGroupCameraDTOList.size();
return PageResultUtils.success(pageResult.getTotal(), cameraGroupCameraDTOList);
}

/**
* Query All
* @param listVo
* @return
*/
public PageResult<List<CameraGroupCameraDTO>> pageAll(CameraGroupItemListVo listVo) {
IPage<Camera> cameraIPage = new Page<>(listVo.getPage(), listVo.getLimit());
Camera query = new Camera();
query.setLocationType("2");
if(StrUtil.isNotBlank(listVo.getName())) {
query.setName(listVo.getName());
}
IPage<Camera> pageResult = cameraService.listPage(cameraIPage, query);
List<Camera> cameraList = pageResult.getRecords();
if(ObjectUtil.isEmpty(cameraList)) {
return PageResultUtils.success(0L, new ArrayList<>());
}

// All Box
List<Location> locationList = locationService.list();
Map<Long, String> locationMap = locationList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Location::getId, Location::getName));

// All Group
List<CameraGroup> cameraGroupList = cameraGroupService.list();
Map<Long, String> cameraGroupMap = cameraGroupList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(CameraGroup::getId, CameraGroup::getName));

// All Algorithm
List<Algorithm> algorithmList = algorithmService.list();
Map<Long, String> algorithmMap = algorithmList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Algorithm::getId, Algorithm::getName));

// Data Process
List<Camera> records = pageResult.getRecords();
if(ObjectUtil.isEmpty(records)) {
return PageResultUtils.success(pageResult.getTotal(), new ArrayList<>());
}

//
List<CameraGroupCameraDTO> cameraGroupCameraDTOList = new ArrayList<>();
for(Camera camera: records) {
String boxName = locationMap.get(camera.getLocationId());
String groupName ="not Group";

// Query Group Name Name
CameraGroupItem cameraGroupItem = cameraGroupItemService.getByCamera(camera.getId());
if(cameraGroupItem!= null) {
groupName = cameraGroupMap.getOrDefault(cameraGroupItem.getGroupId(),"not Group");
}

// Algorithm Name List
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(camera.getId());
List<String> algorithmNames = new ArrayList<>();
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
String algoName = algorithmMap.get(cameraAlgorithm.getAlgorithmId());
if(StrUtil.isNotBlank(algoName)) {
algorithmNames.add(algoName);
}
}

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

CameraGroupCameraDTO cameraGroupCameraDTO = new CameraGroupCameraDTO(camera);
cameraGroupCameraDTO.setFilename(camera.getFileName());
cameraGroupCameraDTO.setBoxName(boxName);
cameraGroupCameraDTO.setGroupName(groupName);
cameraGroupCameraDTO.setAlgorithmNames(algorithmNames);
cameraGroupCameraDTO.setExecMsg(camera.getAiboxExecMsg());
cameraGroupCameraDTO.setStatus(status);
cameraGroupCameraDTO.setVideoWidth(camera.getVideoWidth() == null? 0: camera.getVideoWidth());
cameraGroupCameraDTO.setVideoHeight(camera.getVideoHeight() == null? 0: camera.getVideoHeight());
cameraGroupCameraDTO.setVideoCodec(camera.getVideoCodec() == null?"": camera.getVideoCodec());
cameraGroupCameraDTOList.add(cameraGroupCameraDTO);
}
return PageResultUtils.success(pageResult.getTotal(), cameraGroupCameraDTOList);
}

/**
* not Group Page Query
* @return
*/
public PageResult<List<CameraGroupCameraDTO>> pageNotgroup(CameraGroupItemListVo listVo) {
// Group Camera, Need Filter
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.list();
List<Long> cameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getCameraId).collect(Collectors.toList());

IPage<Camera> pageResult = null;
if(ObjectUtil.isEmpty(cameraIds)) {// not has Camera Config, rule Query all table
IPage<Camera> cameraIPage = new Page<>(listVo.getPage(), listVo.getLimit());
Camera query = new Camera();
query.setLocationType("2");
query.setName(listVo.getName());
pageResult = cameraService.listPage(cameraIPage, query);
} else {// By Camera in Line Filter Query
pageResult = cameraService.listPageNotContainCameraId(cameraIds, listVo.getName(), listVo.getPage(), listVo.getLimit());
}

// All Box
List<Location> locationList = locationService.list();
Map<Long, String> locationMap = locationList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Location::getId, Location::getName));

// All Group
List<CameraGroup> cameraGroupList = cameraGroupService.list();
Map<Long, String> cameraGroupMap = cameraGroupList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(CameraGroup::getId, CameraGroup::getName));

// All Algorithm
List<Algorithm> algorithmList = algorithmService.list();
Map<Long, String> algorithmMap = algorithmList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Algorithm::getId, Algorithm::getName));

// Data Process
List<Camera> records = pageResult.getRecords();
if(ObjectUtil.isEmpty(records)) {
return PageResultUtils.success(pageResult.getTotal(), new ArrayList<>());
}

//
List<CameraGroupCameraDTO> cameraGroupCameraDTOList = new ArrayList<>();
for(Camera camera: records) {
String boxName = locationMap.get(camera.getLocationId());
String groupName ="not Group";

// Algorithm Name List
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(camera.getId());
List<String> algorithmNames = new ArrayList<>();
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
String algoName = algorithmMap.get(cameraAlgorithm.getAlgorithmId());
if(StrUtil.isNotBlank(algoName)) {
algorithmNames.add(algoName);
}
}

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

CameraGroupCameraDTO cameraGroupCameraDTO = new CameraGroupCameraDTO(camera);
cameraGroupCameraDTO.setFilename(camera.getFileName());
cameraGroupCameraDTO.setBoxName(boxName);
cameraGroupCameraDTO.setGroupName(groupName);
cameraGroupCameraDTO.setAlgorithmNames(algorithmNames);
cameraGroupCameraDTO.setExecMsg(camera.getAiboxExecMsg());
cameraGroupCameraDTO.setStatus(status);
cameraGroupCameraDTO.setVideoWidth(camera.getVideoWidth() == null? 0: camera.getVideoWidth());
cameraGroupCameraDTO.setVideoHeight(camera.getVideoHeight() == null? 0: camera.getVideoHeight());
cameraGroupCameraDTO.setVideoCodec(camera.getVideoCodec() == null?"": camera.getVideoCodec());
cameraGroupCameraDTOList.add(cameraGroupCameraDTO);
}
return PageResultUtils.success(pageResult.getTotal(), cameraGroupCameraDTOList);
}

@ApiOperation(value ="Add / Edit")
@SaCheckPermission("edgePlatform-groupView")
@PostMapping("save")
public JsonResult<Long> save(@RequestBody CameraGroupItemModifyVo modifyVo) {
if(modifyVo.getGroupId() == null) {
return JsonResultUtils.fail("Group not Select");
}

cameraGroupItemService.saveData(modifyVo);
return JsonResultUtils.success();
}

// @SaCheckPermission("camera:group:mgr")
// @PostMapping("delete")
// public JsonResult<Long> delete(@RequestBody IdVo idVo) {
// List<CameraGroup> cameraGroupList = cameraGroupService.list();
//
// // Pending Delete ID
// List<Long> removeIds = new ArrayList<>();
// removeIds.add(idVo.getId());
//
// // parent Level ID
// List<Long> parentIds = new ArrayList<>();
// parentIds.add(idVo.getId());
//
// while(true) {
// List<Long> subIds = new ArrayList<>();
// for (CameraGroup cameraGroup: cameraGroupList) {
// if (cameraGroup.getParentId()!= null && parentIds.contains(cameraGroup.getParentId())) {
// subIds.add(cameraGroup.getId());
// removeIds.add(cameraGroup.getId());
//}
//}
// if(subIds.isEmpty()) {
// break;
//}
// parentIds.clear();
// parentIds.addAll(subIds);
//}
//
// cameraGroupService.removeByIds(removeIds);
// return JsonResultUtils.success(idVo.getId());
//}

// @SaCheckPermission("camera:group:mgr")
// @PostMapping("info")
// public JsonResult<CameraGroup> info(@RequestBody IdVo idVo) {
// CameraGroup cameraGroup = cameraGroupService.getById(idVo.getId());
// return JsonResultUtils.success(cameraGroup);
//}

@ApiOperation(value ="Camera not Annotation and Annotation List")
@SaCheckPermission("edgePlatform-groupView")
@PostMapping("cameras")
public JsonResult<CameraGroupCameraListDTO> cameras(@RequestBody IdVo idVo) {
List<CameraGroupCameraDTO> cameras = new ArrayList<>();
List<Long> selects = new ArrayList<>();

// Camera Info
List<Camera> cameraList = cameraService.listData();

// Config Camera
List<CameraGroupItem> cameraGroupItems = cameraGroupItemService.list();
List<Long> cameraIds = cameraGroupItems.stream().map(CameraGroupItem::getCameraId).collect(Collectors.toList());

if(idVo.getId() == null || idVo.getId() == 0) {// new build Group
for(Camera camera: cameraList) {
if(cameraIds.contains(camera.getId())) {// Contain Filter Drop
continue;
}
cameras.add(new CameraGroupCameraDTO(camera));
}
} else {
List<CameraGroupItem> currentGroupItems = cameraGroupItemService.listByGroup(idVo.getId());
List<Long> currentCameraIds = currentGroupItems.stream().map(CameraGroupItem::getCameraId).collect(Collectors.toList());

for(Camera camera: cameraList) {
if(cameraIds.contains(camera.getId()) &&!currentCameraIds.contains(camera.getId())) {// Contain Filter Drop
continue;
}
cameras.add(new CameraGroupCameraDTO(camera));
}

selects.addAll(currentCameraIds);
}

//
CameraGroupCameraListDTO cameraGroupCameraListDTO = new CameraGroupCameraListDTO();
cameraGroupCameraListDTO.setCameras(cameras);
cameraGroupCameraListDTO.setSelects(selects);
return JsonResultUtils.success(cameraGroupCameraListDTO);
}
}
