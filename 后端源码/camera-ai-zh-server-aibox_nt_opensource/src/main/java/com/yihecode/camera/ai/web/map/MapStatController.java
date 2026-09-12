package com.yihecode.camera.ai.web.map;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.entity.map.MapConfig;
import com.yihecode.camera.ai.entity.map.MapObject;
import com.yihecode.camera.ai.entity.map.MapRule;
import com.yihecode.camera.ai.entity.map.MapRuleConfig;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.service.map.MapConfigService;
import com.yihecode.camera.ai.service.map.MapObjectService;
import com.yihecode.camera.ai.service.map.MapRuleService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.map.dto.MapObjectStatDTO;
import com.yihecode.camera.ai.web.map.dto.ReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
* Count Data
*/
@Slf4j
@RestController
@RequestMapping("map/stat")
public class MapStatController {

    @Autowired
    private MapConfigService mapConfigService;

    @Autowired
    private MapObjectService mapObjectService;

    @Autowired
    private MapRuleService mapRuleService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ApDepartService apDepartService;

    @Autowired
    private CameraGroupService cameraGroupService;

    @Autowired
    private CameraGroupItemService cameraGroupItemService;
    @Autowired
    private AlgorithmService algorithmService;
    private static final List<Long> algorithmIds = Arrays.asList(1697225577508438018L, 1925823843802386434L);

    /**
* List Data
* @return
*/
    //@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@GetMapping(value = {"","/"})
public JsonResult<?> data(Long mapId, String isShowStatus) {
MapConfig mapConfig = mapConfigService.getById(mapId);
if(mapConfig == null) {
return JsonResultUtils.success(new ArrayList<>());
}

List<MapObject> mapObjectList = mapObjectService.listData(mapId, null);
if(mapObjectList == null || mapObjectList.isEmpty()) {
return JsonResultUtils.success(new ArrayList<>());
}

// Box Rule rule
MapRule mapRuleBox = mapRuleService.getLatest(0);
long endMillsBox = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();
long startMillsBox = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
if(mapRuleBox!= null && mapRuleBox.getCalcDay()!= null) {
int setHour = mapRuleBox.getCalcDay();
int curHour = DateUtil.hour(new Date(), true);
Date todayAt = DateUtil.beginOfHour(DateUtil.offsetHour(new Date(), setHour - DateUtil.hour(new Date(), true)));
if(curHour < setHour) {// from Yesterday Start Count
Date beforeAt = DateUtil.offsetDay(todayAt, -1);
startMillsBox = beforeAt.getTime();
endMillsBox = todayAt.getTime();

//log.info("Ground image Count Data, Box, Start {}, End {}", beforeAt, todayAt);
} else {// from Current Start Count
Date nextAt = DateUtil.offsetDay(todayAt, 1);
startMillsBox = todayAt.getTime();
endMillsBox = nextAt.getTime();
//log.info("Ground image Count Data, Box, Start {}, End {}", todayAt, nextAt);
}
}

// Camera Rule rule
MapRule mapRuleCamera = mapRuleService.getLatest(1);
long endMillsCamera = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();
long startMillsCamera = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
if(mapRuleCamera!= null && mapRuleCamera.getCalcDay()!= null) {
int setHour = mapRuleCamera.getCalcDay();
int curHour = DateUtil.hour(new Date(), true);
Date todayAt = DateUtil.beginOfHour(DateUtil.offsetHour(new Date(), setHour - DateUtil.hour(new Date(), true)));
if(curHour < setHour) {// from Yesterday Start Count
Date beforeAt = DateUtil.offsetDay(todayAt, -1);
startMillsCamera = beforeAt.getTime();
endMillsCamera = todayAt.getTime();

//log.info("Ground image Count Data, Camera, Start {}, End {}", beforeAt, todayAt);
} else {// from Current Start Count
Date nextAt = DateUtil.offsetDay(todayAt, 1);
startMillsCamera = todayAt.getTime();
endMillsCamera = nextAt.getTime();
//log.info("Ground image Count Data, Camera, Start {}, End {}", todayAt, nextAt);
}
}

// Return Data
List<MapObjectStatDTO> mapObjectStatDTOList = new ArrayList<>();

// Department
List<Depart> departList = apDepartService.list();
Map<Long, String> departMap = departList.stream().filter(dpt -> StrUtil.isNotBlank(dpt.getName())).collect(Collectors.toMap(Depart::getId, Depart::getName));

// Box
List<Location> locationList = locationService.list();
Map<Long, String> locationNameMap = locationList.stream().filter(loc -> StrUtil.isNotBlank(loc.getName())).collect(Collectors.toMap(Location::getId, Location::getName));
Map<Long, Long> locationDepartMap = locationList.stream().filter(loc -> loc.getDepartId()!= null).collect(Collectors.toMap(Location::getId, Location::getDepartId));

// Camera
List<Camera> cameraList = cameraService.listData5(null, null);
Map<Long, String> cameraNameMap = cameraList.stream().filter(cam -> StrUtil.isNotBlank(cam.getName())).collect(Collectors.toMap(Camera::getId, Camera::getName));
Map<Long, Long> cameraLocationMap = cameraList.stream().filter(cam -> cam.getLocationId()!= null).collect(Collectors.toMap(Camera::getId, Camera::getLocationId));

// Query Group
List<CameraGroup> cameraGroupList = cameraGroupService.list();
Map<Long, String> cameraGroupMap = cameraGroupList.stream().collect(Collectors.toMap(CameraGroup::getId, CameraGroup::getName));

// Algorithm
List<Algorithm> algorithmList = algorithmService.list();
Map<Long, String> algorithmListMap = algorithmList.stream().collect(Collectors.toMap(Algorithm::getId, Algorithm::getName));
//
for(MapObject mapObject: mapObjectList) {
// Group Name Name
if(mapObject.getType()!= null && mapObject.getType() == 0) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(mapObject.getObjectId());
int num = 0;
MapObjectStatDTO mapObjectStatDTO = new MapObjectStatDTO();
int redNum = 0;
if(ObjectUtil.isNotEmpty(cameraGroupItemList)) {
List<Long> cameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getCameraId).collect(Collectors.toList());
num = reportService.countByBatchCameras(cameraIds, startMillsBox, endMillsBox, null, isShowStatus);
if(num <= 0) {
continue;
}
redNum = reportService.countByBatchCameras(cameraIds, startMillsBox, endMillsBox, algorithmIds, isShowStatus);
List<ReportDTO> reportList = reportService.selectByBatchCameras(cameraIds, startMillsBox, endMillsBox, isShowStatus);
reportList.forEach(report -> {
report.setAlgorithmName(algorithmListMap.get(report.getAlgorithmId()));
report.setCameraName(cameraGroupMap.getOrDefault(mapObject.getObjectId(),"Unknown"));
});
//log.info("Count Data, Camera Group {}, Alarm Details {}, Start Time {}, End Time {}, Red Alarm Count {}",
// cameraIds, JSONUtil.toJsonStr(reportList), startMillsBox, endMillsBox, redNum);
mapObjectStatDTO.setReportList(reportList);
}
if (num <= 0){
continue;
}

Map<String, String> ruleMap = getColor(mapRuleBox, num);

mapObjectStatDTO.setObjectId(mapObject.getObjectId());
mapObjectStatDTO.setObjectName(cameraGroupMap.getOrDefault(mapObject.getObjectId(),"Unknown"));
mapObjectStatDTO.setRelName("");
mapObjectStatDTO.setType("box");
mapObjectStatDTO.setNum(num);
mapObjectStatDTO.setPosition(mapObject.getPosition());
mapObjectStatDTO.setColor(ruleMap.get("color"));
if(redNum > 0){
mapObjectStatDTO.setColor("#EE1B0C");
}
mapObjectStatDTO.setSvgName(ruleMap.get("svgName"));
mapObjectStatDTO.setRuleName(ruleMap.get("name"));
mapObjectStatDTO.setMinMax(ruleMap.get("minMax"));
mapObjectStatDTOList.add(mapObjectStatDTO);
}

// Camera
if(mapObject.getType()!= null && mapObject.getType() == 1) {
int num = reportService.countByCamera(mapObject.getObjectId(), startMillsCamera, endMillsCamera, null, isShowStatus);
if(num <= 0) {
continue;
}
int redNum = reportService.countByCamera(mapObject.getObjectId(), startMillsBox, endMillsBox, algorithmIds, isShowStatus);
Map<String, String> ruleMap = getColor(mapRuleCamera, num);
List<ReportDTO> reportList = reportService.selectByBatchCameras(Collections.singletonList(mapObject.getObjectId()), startMillsBox, endMillsBox, isShowStatus);
reportList.forEach(report -> {
report.setAlgorithmName(algorithmListMap.get(report.getAlgorithmId()));
report.setCameraName(cameraNameMap.get(mapObject.getObjectId()));
});
//log.info("Count Data, Camera Group {}, Alarm Details {}, Start Time {}, End Time {}, Red Alarm Count {}",
// mapObject.getObjectId(), JSONUtil.toJsonStr(reportList), startMillsBox, endMillsBox, redNum);
MapObjectStatDTO mapObjectStatDTO = new MapObjectStatDTO();
mapObjectStatDTO.setReportList(reportList);
mapObjectStatDTO.setObjectId(mapObject.getObjectId());
mapObjectStatDTO.setObjectName(cameraNameMap.get(mapObject.getObjectId()));
mapObjectStatDTO.setRelName(locationNameMap.get(cameraLocationMap.get(mapObject.getObjectId())));
mapObjectStatDTO.setType("camera");
mapObjectStatDTO.setNum(num);
mapObjectStatDTO.setPosition(mapObject.getPosition());
mapObjectStatDTO.setColor(ruleMap.get("color"));
if(redNum > 0){
mapObjectStatDTO.setColor("#EE1B0C");
}
mapObjectStatDTO.setSvgName(ruleMap.get("svgName"));
mapObjectStatDTO.setRuleName(ruleMap.get("name"));
mapObjectStatDTO.setMinMax(ruleMap.get("minMax"));
mapObjectStatDTOList.add(mapObjectStatDTO);
}
}
return JsonResultUtils.success(mapObjectStatDTOList);
}

/**
* Get Color
* @param mapRule
* @param num
* @return
*/
private Map<String, String> getColor(MapRule mapRule, int num) {
Map<String, String> ruleMap = new HashMap<>();
ruleMap.put("svgName",""); // svg Name
ruleMap.put("color","#EE1B0C"); // Color
ruleMap.put("name","Unknown"); // One Level Alarm
ruleMap.put("minMax",""); // 0-100

if(mapRule == null || mapRule.getRules() == null || mapRule.getRules().isEmpty()) {
return ruleMap;
}

List<MapRuleConfig> mapRuleConfigList = mapRule.getRules();
for(MapRuleConfig mapRuleConfig: mapRuleConfigList) {
if(mapRuleConfig.getMin() <= num && num < mapRuleConfig.getMax()) {
String color = mapRuleConfig.getColor();
String name = mapRuleConfig.getName();
String svgName = mapRule.getSvgName();
ruleMap.put("svgName", svgName);
ruleMap.put("color", color);
ruleMap.put("name", name);
ruleMap.put("minMax", mapRuleConfig.getMin() +"-"+ mapRuleConfig.getMax());
}
}
return ruleMap;
}
}
