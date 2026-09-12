package com.yihecode.camera.ai.web.map;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraGroup;
import com.yihecode.camera.ai.entity.CameraGroupItem;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.entity.map.MapObject;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.service.CameraGroupItemService;
import com.yihecode.camera.ai.service.CameraGroupService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.service.map.MapObjectService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.map.dto.CameraGroupItemDTO;
import com.yihecode.camera.ai.web.map.dto.MapObjectDTO;
import com.yihecode.camera.ai.web.map.vo.CameraGroupItemListVO;
import com.yihecode.camera.ai.web.map.vo.CameraGroupUnselectVO;
import com.yihecode.camera.ai.web.map.vo.MapObjectListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Ground image or image Layer and Camera or Box Config Management
*/
@Api(tags = "System Management _ Base image Insert Point Config Management")
@Slf4j
@RestController
@RequestMapping("map/object")
public class MapObjectController {

    @Autowired
    private MapObjectService mapObjectService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ApDepartService apDepartService;

    @Autowired
    private CameraGroupService cameraGroupService;

    @Autowired
    private CameraGroupItemService cameraGroupItemService;

    @ApiOperation("Group part Level List Data")
    @SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
    @PostMapping("camera/group")
    public JsonResult<List<Map<String, Object>>> cameraGroup(@RequestHeader("Lang") String language) {
        List<Map<String, Object>> levelList = new ArrayList<>();
        int maxLevel = cameraGroupService.getMaxLevel();
        for(int i = 1; i <= maxLevel; i++) {
            Map<String, Object> levelMap = new HashMap<>();
            if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)) {
                levelMap.put("name", "Level" + i);
            }else{
                levelMap.put("name", i + "Level");
            }
            levelMap.put("value", i);
            levelList.add(levelMap);
        }
        return JsonResultUtils.success(levelList);
    }

    @SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
    @PostMapping("list")
    public PageResult<?> list(@RequestBody MapObjectListVO listVO) {
        List<MapObjectDTO> mapObjectDTOS = new ArrayList<>();

        List<MapObject> mapObjectList = mapObjectService.listData(listVO.getMapId(), listVO.getType());
        if(mapObjectList == null || mapObjectList.isEmpty()) {
            return PageResultUtils.success(null, mapObjectDTOS);
        }

        //Ground image, Corresponding Box
// if(listVO.getType() == 0) {
// List<Location> locationList = locationService.list();
// if(locationList == null) {
// locationList = new ArrayList<>();
//}
//
// Map<Long, String> locationNameMap = locationList.stream().filter(loc -> StrUtil.isNotBlank(loc.getName())).collect(Collectors.toMap(Location::getId, Location::getName));
// Map<Long, Long> locationDepartMap = locationList.stream().filter(loc -> loc.getDepartId()!= null).collect(Collectors.toMap(Location::getId, Location::getDepartId));
//
// List<Depart> departList = apDepartService.list();
// if(departList == null) {
// departList = new ArrayList<>();
//}
// Map<Long, String> departMap = departList.stream().filter(dpt -> StrUtil.isNotBlank(dpt.getName()))
//.collect(Collectors.toMap(Depart::getId, Depart::getName));
//
// // Frontend Check select Department ID
// List<Long> selectedDepartIds = listVO.getObjectIds();
// if(selectedDepartIds == null) {
// selectedDepartIds = new ArrayList<>();
//}
//
// for(MapObject mapObject: mapObjectList) {
// // like Result Frontend Check select Department Condition, Judge Fixed Whether Filter
// if(!selectedDepartIds.isEmpty()) {
// Long locationDepartID = locationDepartMap.get(mapObject.getObjectId());
// if(locationDepartID == null ||!selectedDepartIds.contains(locationDepartID)) {
// continue;
//}
//}
//
// // like Result Frontend transmit Name, Judge Fixed Filter
// String locationName = locationNameMap.get(mapObject.getObjectId());
// if(StrUtil.isNotBlank(listVO.getObjectName())) {
// if(locationName == null ||!locationName.contains(listVO.getObjectName())) {
// continue;
//}
//}
//
// MapObjectDTO mapObjectDTO = new MapObjectDTO();
// BeanUtils.copyProperties(mapObject, mapObjectDTO);
// mapObjectDTO.setTypeName("box");
// mapObjectDTO.setObjectName(locationNameMap.get(mapObject.getObjectId()));
// mapObjectDTO.setRelName(departMap.get(locationDepartMap.get(mapObject.getObjectId())));
// mapObjectDTOS.add(mapObjectDTO);
//}
//}

// Ground image, Corresponding Camera Group
if(listVO.getType() == 0) {
// Query All Group
List<CameraGroup> cameraGroupList = cameraGroupService.list();
Map<Long, CameraGroup> cameraGroupMap = cameraGroupList.stream().collect(Collectors.toMap(CameraGroup::getId, Function.identity()));

//
List<Long> containGroupIds = new ArrayList<>();
if(StrUtil.isNotBlank(listVO.getObjectName())) {
// By Camera Name in Line Blur Query
List<Long> cameraIds = cameraService.listIdByName(listVO.getObjectName());
if(cameraIds.isEmpty()) {
return PageResultUtils.success(0L, Collections.emptyList());
}

// By Camera IDs Get Corresponding Group
List<Long> groupIds = cameraGroupItemService.listIdByCamera(cameraIds);
if(groupIds.isEmpty()) {
return PageResultUtils.success(0L, Collections.emptyList());
}

containGroupIds.addAll(groupIds);
}


for(MapObject mapObject: mapObjectList) {
// By Group in Line Filter, like Result not Contain, Skip
if(!containGroupIds.isEmpty() &&!containGroupIds.contains(mapObject.getObjectId())) {
continue;
}

CameraGroup cameraGroup = cameraGroupMap.get(mapObject.getObjectId());
if(listVO.getLevel()!= null) {
// Group Object does not exist, Skip
if(cameraGroup == null) {
continue;
}

// like Result Group Hierarchy Param not Match allocate, Skip
if(!listVO.getLevel().equals(cameraGroup.getLevel())) {
continue;
}
}

int count = 0;
if(cameraGroup!= null) {
count = cameraGroupItemService.countByGroup(cameraGroup.getId());
}

// Query Current and All parent Node Name
String chainName = cameraGroupService.getCurrentAndParentNames(mapObject.getObjectId(), cameraGroupList);

MapObjectDTO mapObjectDTO = new MapObjectDTO();
mapObjectDTO.setId(mapObject.getId());
mapObjectDTO.setMapId(mapObject.getMapId());
mapObjectDTO.setObjectId(mapObject.getObjectId());
mapObjectDTO.setObjectName(cameraGroup == null?"": cameraGroup.getName());
// mapObjectDTO.setRelName(formatLevel(cameraGroup == null? 1: cameraGroup.getLevel()));
mapObjectDTO.setRelName(chainName);
mapObjectDTO.setCameraCount(count +"Path");
mapObjectDTO.setPosition(mapObject.getPosition());
mapObjectDTO.setTypeName("group");
mapObjectDTOS.add(mapObjectDTO);
}
}

// image Layer, Corresponding Camera
if(listVO.getType() == 1) {
List<Camera> cameraList = cameraService.list();
if(cameraList == null) {
cameraList = new ArrayList<>();
}

Map<Long, String> cameraNameMap = cameraList.stream().filter(cam -> StrUtil.isNotBlank(cam.getName())).collect(Collectors.toMap(Camera::getId, Camera::getName));
Map<Long, Long> cameraLocationMap = cameraList.stream().filter(cam -> cam.getLocationId()!= null).collect(Collectors.toMap(Camera::getId, Camera::getLocationId));

List<Location> locationList = locationService.list();
if(locationList == null) {
locationList = new ArrayList<>();
}

Map<Long, String> locationNameMap = locationList.stream().filter(loc -> StrUtil.isNotBlank(loc.getName())).collect(Collectors.toMap(Location::getId, Location::getName));

// Frontend Check select Box ID
List<Long> selectedBoxIds = listVO.getObjectIds();
if(selectedBoxIds == null) {
selectedBoxIds = new ArrayList<>();
}

for(MapObject mapObject: mapObjectList) {
// like Result Frontend Check select Box Condition, Judge Fixed Whether Filter
if(!selectedBoxIds.isEmpty()) {
Long cameraBoxID = cameraLocationMap.get(mapObject.getObjectId());
if(cameraBoxID == null ||!selectedBoxIds.contains(cameraBoxID)) {
continue;
}
}

// like Result Frontend transmit Name, Judge Fixed Filter
String cameraName = cameraNameMap.get(mapObject.getObjectId());
if(StrUtil.isNotBlank(listVO.getObjectName())) {
if(cameraName == null ||!cameraName.contains(listVO.getObjectName())) {
continue;
}
}

MapObjectDTO mapObjectDTO = new MapObjectDTO();
BeanUtils.copyProperties(mapObject, mapObjectDTO);
mapObjectDTO.setTypeName("camera");
mapObjectDTO.setObjectName(cameraNameMap.get(mapObject.getObjectId()));
mapObjectDTO.setRelName(locationNameMap.get(cameraLocationMap.get(mapObject.getObjectId())));
mapObjectDTOS.add(mapObjectDTO);
}
}
return PageResultUtils.success(null, mapObjectDTOS);
}

@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@PostMapping("save")
public JsonResult<?> save(@RequestBody MapObject mapObject) {
if(mapObject.getMapId() == null || mapObject.getObjectId() == null) {
return JsonResultUtils.fail("Please select Device");
}

// ID Process One down
if(mapObject.getId()!= null && mapObject.getId() == 0) {
mapObject.setId(null);
}

MapObject mapObjectDb = mapObjectService.getData(mapObject.getMapId(), mapObject.getObjectId());
if(mapObjectDb == null) {
mapObjectService.save(mapObject);
} else {
mapObject.setId(mapObjectDb.getId());
mapObjectService.updateById(mapObject);
}
return JsonResultUtils.success();
}

@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@GetMapping("delete")
public JsonResult<?> delete(Long id) {
mapObjectService.removeById(id);
return JsonResultUtils.success();
}


/**
* not Select Camera Group
* @return
*/
@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@PostMapping("camera/group/unselect")
public JsonResult<?> cameraGroupUnselect(@RequestBody CameraGroupUnselectVO unselectVO) {
// Query All Group
List<CameraGroup> cameraGroupList = cameraGroupService.list();

// By Camera Name Query Group
List<Long> containGroupIds = new ArrayList<>();
if(StrUtil.isNotBlank(unselectVO.getObjectName())) {
List<Long> cameraIds = cameraService.listIdByName(unselectVO.getObjectName());
if(cameraIds.isEmpty()) {
return JsonResultUtils.success(new ArrayList<>());
}

List<Long> groupIds = cameraGroupItemService.listIdByCamera(cameraIds);
if(groupIds.isEmpty()) {
return JsonResultUtils.success(new ArrayList<>());
}
containGroupIds.addAll(groupIds);
}


// Query Config Group
List<Long> mapObjectList = mapObjectService.listObjectIDByType(0);

// Data Process
List<MapObjectDTO> mapObjectDTOList = new ArrayList<>();
for(CameraGroup cameraGroup: cameraGroupList) {
// Group Config, Skip
if(mapObjectList.contains(cameraGroup.getId())) {
continue;
}

if(unselectVO.getLevel()!= null) {
// has Hierarchy Condition, not Satisfy, Skip
if(!unselectVO.getLevel().equals(cameraGroup.getLevel())) {
continue;
}
}

// By Camera Name Brush select Group not Contain Current Group, Skip
if(!containGroupIds.isEmpty() &&!containGroupIds.contains(cameraGroup.getId())) {
continue;
}

int count = cameraGroupItemService.countByGroup(cameraGroup.getId());
if(count == 0) {
continue;
}

// Query Current and All parent Node Name
String chainName = cameraGroupService.getCurrentAndParentNames(cameraGroup.getId(), cameraGroupList);

MapObjectDTO mapObjectDTO = new MapObjectDTO();
mapObjectDTO.setId(null);
mapObjectDTO.setMapId(null);
mapObjectDTO.setObjectId(cameraGroup.getId());
mapObjectDTO.setObjectName(cameraGroup.getName());
// mapObjectDTO.setRelName(formatLevel(cameraGroup.getLevel()));
mapObjectDTO.setRelName(chainName);
mapObjectDTO.setCameraCount(count +"Path");
mapObjectDTO.setTypeName("group");
mapObjectDTOList.add(mapObjectDTO);
}
return JsonResultUtils.success(mapObjectDTOList);
}

/**
* not Select Camera Group
* @return
*/
@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@PostMapping("camera/group/items")
public JsonResult<List<CameraGroupItemDTO>> cameraGroupItems(@RequestBody CameraGroupItemListVO listVO) {
List<Camera> cameraList = cameraService.list();
Map<Long, Camera> cameraMap = cameraList.stream().collect(Collectors.toMap(Camera::getId, Function.identity()));

List<Location> locationList = locationService.list();
Map<Long, Location> locationMap = locationList.stream().collect(Collectors.toMap(Location::getId, Function.identity()));

List<CameraGroupItemDTO> cameraGroupItemDTOList = new ArrayList<>();

// By Group ID Query Config Camera
List<CameraGroupItem> cameraGroupItems = cameraGroupItemService.listByGroup(listVO.getGroupId());
for(CameraGroupItem cameraGroupItem: cameraGroupItems) {
Camera camera = cameraMap.get(cameraGroupItem.getCameraId());
if(camera == null) {
continue;
}

Location location = locationMap.get(camera.getLocationId());

CameraGroupItemDTO cameraGroupItemDTO = new CameraGroupItemDTO();
cameraGroupItemDTO.setId(camera.getId());
cameraGroupItemDTO.setName(camera.getName());
cameraGroupItemDTO.setFilename(camera.getFileName());
cameraGroupItemDTO.setLocationId(location == null? 0L: location.getId());
cameraGroupItemDTO.setLocationName(location == null?"": location.getName());
cameraGroupItemDTOList.add(cameraGroupItemDTO);
}
return JsonResultUtils.success(cameraGroupItemDTOList);
}

/**
* Format Change Display Camera Group Hierarchy
* @param maxLevel
* @return
*/
private String formatLevel(Integer maxLevel) {
if(maxLevel == null) {
return"";
}

List<String> x = new ArrayList<>();
for(int i = 1; i <= maxLevel; i++) {
x.add(i +"Level");
}
return String.join("-", x);
}
}
