package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.enums.EnglishMonthEnum;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.Channels;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.yihecode.camera.ai.web.dto.AlarmAuditTimeTargetDTO;
import com.yihecode.camera.ai.web.dto.StatisticAlgorithmAuditDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
* Data Count Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
//@SaCheckLogin
@Slf4j
@Api(tags ="Data Count Management")
@CrossOrigin
@Controller
@RequestMapping({"/statistic"})
public class StatisticController {

//
@Autowired
private CameraService cameraService;

//
@Autowired
private AlgorithmService algorithmService;

//
@Autowired
private ReportService reportService;

@Autowired
private CameraGroupService cameraGroupService;
@Autowired
private CameraGroupItemService cameraGroupItemService;

//
@Autowired
private ConfigService configService;

@Autowired
private ReportTargetService reportTargetService;

/**
* by Region Dimension Degree Count Each Day Alarm part Canvas Situation
* @return
*/
@PostMapping("/alarm/area/audit")
@ResponseBody
public JsonResult<List<Map<String, Object>>> alarmAreaAuditStatistic() {
// 1. Get when day Time Range
LocalDateTime startOfDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIDNIGHT);
ZonedDateTime zonedStartOfDay = startOfDay.atZone(ZoneId.systemDefault());
long startMills = zonedStartOfDay.toInstant().toEpochMilli();

LocalDateTime endOfDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX);
ZonedDateTime zonedEndOfDay = endOfDay.atZone(ZoneId.systemDefault());
long endMills = zonedEndOfDay.toInstant().toEpochMilli();

// 2. Query when day Alarm Record
List<Report> reportList = reportService.list(new LambdaQueryWrapper<Report>()
.between(Report::getCreatedMills, startMills, endMills));
if (CollUtil.isEmpty(reportList)) {
return JsonResultUtils.success(Collections.emptyList());
}
// 3. Get Alarm Relate Camera ID List
List<Long> cameraIdList = reportList.stream()
.map(Report::getCameraId)
.distinct()
.collect(Collectors.toList());

// 4. Query Camera Corresponding Group Info
List<CameraGroupItem> itemList = cameraGroupItemService.list(new LambdaQueryWrapper<CameraGroupItem>()
.in(CameraGroupItem::getCameraId, cameraIdList));
if (CollUtil.isEmpty(itemList)) {
return JsonResultUtils.success(Collections.emptyList());
}
// 5. Get Group ID List
// List<Long> groupIdList = itemList.stream()
//.map(CameraGroupItem::getGroupId)
//.distinct()
//.collect(Collectors.toList());

// 6. Query All Group Info (Contain Hierarchy close System)
List<CameraGroup> allGroups = cameraGroupService.list();
if (CollUtil.isEmpty(allGroups)) {
return JsonResultUtils.success(Collections.emptyList());
}
// 7. Build Group ID to Name Mapper
Map<Long, String> groupIdToNameMap = allGroups.stream()
.collect(Collectors.toMap(CameraGroup::getId, CameraGroup::getName));

// 8. Build Group ID to parent ID Mapper
Map<Long, Long> groupIdToParentIdMap = allGroups.stream()
.collect(Collectors.toMap(CameraGroup::getId, CameraGroup::getParentId));
// 9. Build Complete whole Region Hierarchy Tree
Map<Long, List<Long>> parentToChildrenMap = new HashMap<>();
allGroups.forEach(group -> {
long parentId = group.getParentId();
parentToChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>())
.add(group.getId());
});

// 10. find out All Root Region (parentId=0)
List<Long> rootGroupIds = parentToChildrenMap.getOrDefault(0L, Collections.emptyList());

// 11. Standard device Result List
List<Map<String, Object>> resultList = new ArrayList<>();
// 12. deliver belong Process Each Root Region and its child Region
for (Long rootGroupId: rootGroupIds) {
String rootGroupName = groupIdToNameMap.get(rootGroupId);
if (rootGroupName == null) {
continue;
}
// Calculate the Region and its All child Region Alarm Total
Integer totalAlarms = calculateTotalAlarms(rootGroupId, itemList, reportList,
groupIdToParentIdMap, groupIdToNameMap);

if(totalAlarms == 0) {
continue; // like Result the Region not has Alarm, rule Skip
}
// 12.1 Add: like Result Need Display Hierarchy close System, can with deliver belong Process child Region (can select real current
// Add to Result
Map<String, Object> areaStat = new HashMap<>();
areaStat.put("name", rootGroupName);
areaStat.put("conut", totalAlarms);
resultList.add(areaStat);

// deliver belong Process child Region (can select, like Result Need Display All Hierarchy)
// processChildren(rootGroupId, 1, parentToChildrenMap, groupIdToNameMap,
// itemList, reportList, resultList);
}
// 13. Add: by Alarm Total down order Sort
resultList.sort((o1, o2) -> {
int alarms1 = (int) o1.get("conut");
int alarms2 = (int) o2.get("conut");
return Integer.compare(alarms2, alarms1); // down order Row Column
});
// 14. Add: Get front 5
List<Map<String, Object>> top5List = resultList.stream()
.limit(5)
.collect(Collectors.toList());

return JsonResultUtils.success(top5List);
}
/**
* deliver belong Calculate Region and its All child Region Alarm Total
*/
private int calculateTotalAlarms(Long groupId,
List<CameraGroupItem> itemList,
List<Report> reportList,
Map<Long, Long> groupIdToParentIdMap,
Map<Long, String> groupIdToNameMap) {
// 1. find out the Region Direct connect Relate Camera produce produce Alarm Number
Integer directAlarms = itemList.stream()
.filter(item -> groupId.equals(item.getGroupId()))
.mapToInt(item -> {
long cameraId = item.getCameraId();
return (int) reportList.stream()
.filter(report -> cameraId == report.getCameraId())
.count();
})
.sum();

// 2. find out All child Region ID
List<Long> childGroupIds = new ArrayList<>();
groupIdToParentIdMap.entrySet().stream()
.filter(entry -> groupId.equals(entry.getValue()))
.forEach(entry -> childGroupIds.add(entry.getKey()));

// 3. deliver belong Calculate child Region Alarm Number
for (Long childId: childGroupIds) {
directAlarms += calculateTotalAlarms(childId, itemList, reportList,
groupIdToParentIdMap, groupIdToNameMap);
}

return directAlarms;
}

/**
* deliver belong Process child Region (can select real current, like Result Need Display All Hierarchy)
*/
private void processChildren(Long parentId,
int level,
Map<Long, List<Long>> parentToChildrenMap,
Map<Long, String> groupIdToNameMap,
List<CameraGroupItem> itemList,
List<Report> reportList,
List<Map<String, Object>> resultList) {

List<Long> children = parentToChildrenMap.getOrDefault(parentId, Collections.emptyList());

for (Long childId: children) {
String childName = groupIdToNameMap.get(childId);
if (childName == null) continue;

int childAlarms = calculateTotalAlarms(childId, itemList, reportList,
null, groupIdToNameMap); // can with Optimize, Avoid Free re reply Calculate

Map<String, Object> childStat = new HashMap<>();
childStat.put("areaName", childName);
childStat.put("totalAlarms", childAlarms);
childStat.put("level", level);
childStat.put("isRoot", false);
resultList.add(childStat);

// deliver belong Process down One Level
processChildren(childId, level + 1, parentToChildrenMap,
groupIdToNameMap, itemList, reportList, resultList);
}
}
/**
*
* @return
*/
@ApiOperation("Query Camera Count")
@PostMapping({"/camera/count"})
@ResponseBody
public JsonResult<Integer> cameraCount() {
int cameraCount = 0;
List<Camera> cameraList = this.cameraService.listData();
if (cameraList!= null) {
cameraCount = cameraList.size();
}
return JsonResultUtils.success(cameraCount);
}

/**
*
* @param startDate
* @param endDate
* @return
*/
@ApiOperation("Query Algorithm Alert Occupy than (Pie image)")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="startDate", value ="Start Time", dataType ="string", example ="yyyy-MM-dd"),
@ApiImplicitParam(name ="endDate", value ="End Time", dataType ="string", example ="yyyy-MM-dd")})
@PostMapping({"/algorithm/ratio"})
@ResponseBody
public JsonResult<List<Map<String, Object>>> listAlgorithmRatio(String startDate, String endDate) {
List<Map<String, Object>> results = this.reportService.findAlgorithmRatio(getStartDate(startDate), getEndDate(endDate));
if (results == null) {
results = new ArrayList<>();
}
Map<Long, String> algorithmMap = this.algorithmService.toMap();
List<Map<String, Object>> dataList = new ArrayList<>();
for (Map<String, Object> result: results) {
Long algorithmId = Convert.toLong(result.get("algorithm_id"), 0L);
Integer count = Convert.toInt(result.get("cnt"), 0);
String algorithmName = algorithmMap.get(algorithmId);
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("name", algorithmName == null?"Unknow": algorithmName);
dataMap.put("value", count);
dataList.add(dataMap);
}
return JsonResultUtils.success(dataList);
}

/**
*
* @param startDate
* @param endDate
* @return
*/
@ApiOperation("Query Algorithm Alert Occupy than (Pie image)")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="startDate", value ="Start Time", dataType ="string", example ="yyyy-MM-dd"),
@ApiImplicitParam(name ="endDate", value ="End Time", dataType ="string", example ="yyyy-MM-dd")})
@PostMapping({"/camera"})
@ResponseBody
public JsonResult<Map<String, Object>> listCamera(String startDate, String endDate) {
List<Map<String, Object>> results = this.reportService.findCamera(getStartDate(startDate), getEndDate(endDate));
if (results == null) {
results = new ArrayList<>();
}
//
Map<Long, Integer> cameraStatistics = new HashMap<>();
for(Map<String, Object> result: results) {
Long cameraId = Convert.toLong(result.get("camera_id"), 0L);
Integer count = Convert.toInt(result.get("cnt"), 0);
cameraStatistics.put(cameraId, count);
}

//
Map<Long, String> cameraMap = this.cameraService.toMap();
List<String> xAxiss = new ArrayList<>();
List<Integer> values = new ArrayList<>();
for (Long cameraId: cameraMap.keySet()) {
xAxiss.add(cameraMap.get(cameraId));

// values
if(cameraStatistics.get(cameraId) == null) {
values.add(0);
} else {
values.add(cameraStatistics.get(cameraId));
}
}
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("xAxiss", xAxiss);
dataMap.put("values", values);
return JsonResultUtils.success(dataMap);
}

/**
*
* @param startDate
* @param endDate
* @return
*/
@ApiOperation("Query Camera Algorithm Alert Occupy than (Column image)")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="startDate", value ="Start Time", dataType ="string", example ="yyyy-MM-dd"),
@ApiImplicitParam(name ="endDate", value ="End Time", dataType ="string", example ="yyyy-MM-dd")})
@PostMapping(value ="/camera2algorithm")
@ResponseBody
public JsonResult<Map<String, Object>> listCameraAlgorithm(String startDate, String endDate) {
Date _startDate = getStartDate(startDate);
Date _endDate = getEndDate(endDate);

//
Map<Long, String> cameraMap = this.cameraService.toMap();
List<String> cameraNames = new ArrayList<>();
for (Long cameraId: cameraMap.keySet()) {
cameraNames.add(cameraMap.get(cameraId));
}

//
Map<Long, String> algorithmMap = this.algorithmService.toMap();
List<String> algorithmNames = new ArrayList<>();
for (Long algorithmId: algorithmMap.keySet()) {
algorithmNames.add(algorithmMap.get(algorithmId));
}

//
List<Map<String, Object>> results = this.reportService.findCameraAlgorithm(_startDate, _endDate);
if (results == null) {
results = new ArrayList<>();
}

// results list to map
Map<String, Integer> resultMap = new HashMap<>();
for(Map<String, Object> result: results) {
Long cameraId = Convert.toLong(result.get("camera_id"), 0L);
Long algorithmId = Convert.toLong(result.get("algorithm_id"), 0L);
Integer count = Convert.toInt(result.get("cnt"), 0);
resultMap.put(cameraId +"@"+ algorithmId, count);
}

//
List<Map<String, Object>> dataList = new ArrayList<>();
for (Long algorithmId: algorithmMap.keySet()) {

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("name", algorithmMap.get(algorithmId));
dataMap.put("type","bar");
dataMap.put("barGap", 0);
Map<String, Object> labelMap = new HashMap<>();
labelMap.put("show", false);
labelMap.put("position","insideBottom");
labelMap.put("distance", 15);
labelMap.put("align","left");
labelMap.put("verticalAlign","middle");
labelMap.put("rotate", 90);
//labelMap.put("formatter","{c} {name|{a}}");
labelMap.put("fontSize", 14);

Map<String, Object> richMap = new HashMap<>();
richMap.put("name", new HashMap<>());
labelMap.put("rich", richMap);
dataMap.put("label", labelMap);

//
Map<String, String> emphasisMap = new HashMap<>();
emphasisMap.put("focus","series");
dataMap.put("emphasis", emphasisMap);

//
List<Integer> values = new ArrayList<>();
for (Long cameraId: cameraMap.keySet()) {
Integer count = resultMap.get(cameraId +"@"+ algorithmId);
values.add(count == null? 0: count);
}
dataMap.put("data", values);
dataList.add(dataMap);
}
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("cameraNames", cameraNames);
dataMap.put("algorithmNames", algorithmNames);
dataMap.put("datas", dataList);

return JsonResultUtils.success(dataMap);
}

/**
*
* @param startDate
* @return
*/
private Date getStartDate(String startDate) {
Date _startDate = null;
try {
_startDate = DateUtil.parse(startDate,"yyyy-MM-dd");
} catch (Exception e) {
//
}
if (_startDate == null) {
_startDate = DateUtil.offsetDay(new Date(), -7);
}
return DateUtil.beginOfDay(_startDate);
}

/**
*
* @param endDate
* @return
*/
private Date getEndDate(String endDate) {
Date _endDate = null;
try {
_endDate = DateUtil.parse(endDate,"yyyy-MM-dd");
} catch (Exception e) {
//
}
if (_endDate == null) {
_endDate = new Date();
}
return DateUtil.beginOfDay(DateUtil.offsetDay(_endDate, 1));
}


/**
* Real-time Camera Detection Data: Camera Total, Camera Run Total
* @return
*/
@ApiIgnore
@CrossOrigin
@RequestMapping("/countCamera")
@ResponseBody
public JsonResult countCamera() {
//
Integer totalCount = cameraService.getCountByRunState(-1);
Integer runingCount = cameraService.getCountByRunState(1);
//
Map<String, Object> resMap = new HashMap<>();
resMap.put("total", totalCount);
resMap.put("runing", runingCount);
//
return JsonResultUtils.success(resMap);
}

/**
* System Management: Count Server Count, Data Set Total, Today Day Push Data Set Total, Annotation Data Set Total, Task Total, Model Training Total, Model Deploy Count, Model Mirror Count
* @return
*/
@ApiIgnore
@CrossOrigin
@RequestMapping("/countData")
@ResponseBody
public JsonResult countData() {
Map<String, Object> resMap = new HashMap<>();
resMap.put("server_count", 2);
resMap.put("dataset_count", 0);
resMap.put("today_dataset_count", 0);
resMap.put("mark_dataset_count", 0);
resMap.put("task_count", 10);
resMap.put("model_train_count", 20);
resMap.put("model_deploy_count", 15);
resMap.put("model_image_count", 20);
//
Integer datasetCount = reportService.getCount(null, null, null, null, null);
Integer todayDatasetCount = reportService.getCount(DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime(), DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime(), null, null, null);
Integer markDatasetCount = reportService.getMarkCount(null, null, null, null);

//
resMap.put("dataset_count", datasetCount);
resMap.put("today_dataset_count", todayDatasetCount);
resMap.put("mark_dataset_count", markDatasetCount);
//
return JsonResultUtils.success(resMap);
}

/**
* This Day Cumulative pre Alarm sub Number: This Day Cumulative pre Alarm sub Number, Algorithm Alert sub Number Count
* @return
*/
@ApiIgnore
@CrossOrigin
@RequestMapping("/countAlgorithm1Day")
@ResponseBody
public JsonResult countAlgorithm1Day() {
Map<String, Object> resMap = new HashMap<>();

//
Integer totalCount = reportService.getCount(DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime(), DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime(), null, null, null);
List<Map<String, Object>> algorithmStaticsData = reportService.getCountByAlgorithmReList(DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime(), DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime());

//
Map<Long, String> algorithmNames = algorithmService.toMap();
//
List<Map<String, Object>> chartDatas = new ArrayList<>();
for (Map<String, Object> data: algorithmStaticsData) {
Long algorithmId = Convert.toLong(data.get("algorithm_id"), 0l);
Integer count = Convert.toInt(data.get("ct"), 0);
Map<String, Object> chartData = new HashMap<>();
chartData.put("name", algorithmNames.get(algorithmId));
chartData.put("count", count == null? 0: count);
chartDatas.add(chartData);
algorithmNames.remove(algorithmId);
}
Iterator<Long> iter = algorithmNames.keySet().iterator();
while(iter.hasNext()) {
Long algorithmId = iter.next();
String algorithmName = algorithmNames.get(algorithmId);
//
Map<String, Object> chartData = new HashMap<>();
chartData.put("name", algorithmName);
chartData.put("count", 0);
chartDatas.add(chartData);
}
//
resMap.put("total", totalCount);
resMap.put("datas", chartDatas);
return JsonResultUtils.success(resMap);
}

/**
* This Month pre Alarm Event front 10 Count
* @return
*/
@ApiIgnore
@CrossOrigin
@RequestMapping("/countAlgorithm30Day")
@ResponseBody
public JsonResult countAlgorithm30Day() {
//
List<Map<String, Object>> algorithmStaticsData = reportService.getCountByAlgorithmReList(DateUtil.truncate(DateUtil.offsetDay(new Date(), -30), DateField.DAY_OF_MONTH).getTime(), DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime());

//
Map<Long, String> algorithmNames = algorithmService.toMap();
int num = 0;
List<Map<String, Object>> chartDatas = new ArrayList<>();
for (Map<String, Object> data: algorithmStaticsData) {
Long algorithmId = Convert.toLong(data.get("algorithm_id"), 0l);
Integer count = Convert.toInt(data.get("ct"), 0);
Map<String, Object> chartData = new HashMap<>();
chartData.put("name", algorithmNames.get(algorithmId));
chartData.put("count", count == null? 0: count);
chartDatas.add(chartData);
algorithmNames.remove(algorithmId);
num++;
if(num >= 10) {
break;
}
}
// Fill Charge full Ten Bit
Iterator<Long> iter = algorithmNames.keySet().iterator();
while(iter.hasNext() && num < 10) {
Long algorithmId = iter.next();
String algorithmName = algorithmNames.get(algorithmId);
Map<String, Object> chartData = new HashMap<>();
chartData.put("name", algorithmName);
chartData.put("count", 0);
chartDatas.add(chartData);
num++;
}
//
return JsonResultUtils.success(chartDatas);
}

/**
* Work Dynamic Camera List
* @return
*/
@ApiIgnore
@CrossOrigin
@SaCheckPermission("XXXXXX")
@RequestMapping("/activeCameras")
@ResponseBody
public JsonResult activeCameras() {
//
List<Camera> cameraList = cameraService.listActives();
//
List<Map<String, Object>> dataList = new ArrayList<>();
//
for(Camera camera: cameraList) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("name", camera.getName());
dataMap.put("rtsp_url", camera.getRtspUrl());
dataList.add(dataMap);
}

return JsonResultUtils.success(dataList);
}

/**
* This Week Cumulative pre Alarm: Alert Total, Each Day Alert sub Number, by Date Curve Line image
* @return
*/
@ApiIgnore
@CrossOrigin
@RequestMapping("/countAlgorithmCount7Day")
@ResponseBody
public JsonResult countAlgorithmCount7Day() {
Map<String, Object> resMap = new HashMap<>();
//
Date startDate = DateUtil.truncate(DateUtil.offsetDay(new Date(), -6), DateField.DAY_OF_MONTH);
Date endDate = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH); // Contain Today

// Total
Integer total = reportService.getCount(startDate.getTime(), endDate.getTime(), null, null, null);
//
int end = Integer.parseInt(DateUtil.format(new Date(),"yyyyMMdd"));
List<Map<String, Object>> dataList = new ArrayList<>();
while (true) {
int today = Integer.parseInt(DateUtil.format(startDate,"yyyyMMdd"));
if(today > end) {
break;
}
//
Date nextDate = DateUtil.truncate(DateUtil.offsetDay(startDate, 1), DateField.DAY_OF_MONTH);
Integer count = reportService.getCount(startDate.getTime(), nextDate.getTime(), null, null, null);
//
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("name", DateUtil.format(startDate,"MM-dd"));
dataMap.put("count", count);
dataList.add(dataMap);

//
Calendar calendar = Calendar.getInstance();
calendar.setTime(startDate);
calendar.add(Calendar.DAY_OF_MONTH, 1);
startDate = calendar.getTime();
}
//
resMap.put("total", total);
resMap.put("datas", dataList);
return JsonResultUtils.success(resMap);
}

/**
* This 7 day pre Alarm: Alert Total, Each Day Alert sub Number, Pie image
* @return
*/
@ApiIgnore
@CrossOrigin
@RequestMapping("/countAlgorithm7Day")
@ResponseBody
public JsonResult countAlgorithm7Day() {
//
Date startDate = DateUtil.truncate(DateUtil.offsetDay(new Date(), -6), DateField.DAY_OF_MONTH);
Date endDate = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH); // Contain Today

// by Algorithm Query near 7 day Alert Count Data
Map<Long, Integer> countByAlgorithmMap = reportService.getCountByAlgorithm(startDate.getTime(), endDate.getTime());
//
Map<Long, String> algorithmNames = algorithmService.toMap();
//
List<Map<String, Object>> dataList = new ArrayList<>();
for (Long algorithmId: algorithmNames.keySet()) {
String algorithmName = algorithmNames.get(algorithmId);
//
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("name", algorithmName);
dataMap.put("count", countByAlgorithmMap.get(algorithmId) == null? 0: countByAlgorithmMap.get(algorithmId));
dataList.add(dataMap);
}
return JsonResultUtils.success(dataList);
}

/**
* Config Param
* @return
*/
@ApiIgnore
@CrossOrigin
@RequestMapping("/countConfig")
@ResponseBody
public JsonResult countConfig() {
String wsUrl = configService.getByValTag("wsUrl");
String streamUrl = configService.getByValTag("streamUrl");
String webUrl = configService.getByValTag("webUrl");
//
Map<String, String> resMap = new HashMap<>();
resMap.put("wsUrl", wsUrl);
resMap.put("streamUrl", streamUrl);
resMap.put("webUrl", webUrl);
return JsonResultUtils.success(resMap);
}

/**
* Recent 20 Alert Record
* @return
*/
@ApiIgnore
@CrossOrigin
@RequestMapping("/countNewly")
@ResponseBody
public JsonResult countNewly() {
//
String webUrl = configService.getByValTag("webUrl");
Map<Long, String> cameraNames = cameraService.toMap();
Map<Long, String> algorithmNames = algorithmService.toMap();

// Default Query when Day Recent 3 Alert
List<Map<String, Object>> dataList = new ArrayList<>();
List<Report> reportList = reportService.listNewly(20);
if(reportList!= null) {
for(Report report: reportList) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("id", report.getId());
dataMap.put("params", report.getParams());
dataMap.put("cameraName", cameraNames.get(report.getCameraId()));
dataMap.put("algorithmName", algorithmNames.get(report.getAlgorithmId()));
dataMap.put("wareName","");
dataMap.put("alarmTime", (report.getCreatedAt() == null)?"": DateUtil.format(report.getCreatedAt(),"MM/dd HH:mm"));
dataMap.put("webUrl", webUrl);
dataList.add(dataMap);
}
}
return JsonResultUtils.success(dataList);
}

/**
* Recent 20 Alert Record - Filter Drop Phase same Camera
* @return
*/
@ApiIgnore
@CrossOrigin
@GetMapping("/countNewlyByCamera")
@ResponseBody
public JsonResult countNewlyByCamera() {
//
String webUrl = configService.getByValTag("webUrl");
Map<Long, String> cameraNames = cameraService.toMap();
Map<Long, String> cameraMap = new HashMap<>();
Map<Long, String> algorithmNames = algorithmService.toMap();

// Default Query when Day Recent 3 Alert
List<Map<String, Object>> dataList = new ArrayList<>();
List<Report> reportList = reportService.listNewly(20);
if(reportList!= null) {
for(Report report: reportList) {
if (!cameraMap.containsKey(report.getCameraId())) {
cameraMap.put(report.getCameraId(), cameraNames.get(report.getCameraId()));
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("id", report.getId());
dataMap.put("params", report.getParams());
dataMap.put("cameraName", cameraNames.get(report.getCameraId()));
dataMap.put("cameraId", report.getCameraId());
dataMap.put("algorithmName", algorithmNames.get(report.getAlgorithmId()));
dataMap.put("fileName", report.getFileName());
dataMap.put("wareName","");
dataMap.put("alarmTime", (report.getCreatedAt() == null)?"": DateUtil.format(report.getCreatedAt(),"MM/dd HH:mm"));
dataMap.put("webUrl", webUrl);
dataList.add(dataMap);
}
}
}
return JsonResultUtils.success(dataList);
}

@ApiIgnore
@CrossOrigin
@GetMapping({"/stream"})
public void getImageAsByteArray(@RequestParam(defaultValue ="0") Long id, HttpServletResponse response) {
Report report = reportService.getById(id);
if(report!= null && StrUtil.isNotBlank(report.getFileName())) {
//
File file = new File(report.getFileName());
if(!file.exists()) {
System.out.println("Read Alert Image Exception: File does not exist");
return;
}
//
try (FileInputStream fis = new FileInputStream(file)) {
response.setContentType("image/jpeg");
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
} catch (Exception e) {
System.out.println("Read Alert Image Exception:"+ e.getMessage());
}
}

}

/**
* when Day Alert Count Data
* @return
*/
@PostMapping("alarm/count/today")
@ResponseBody
public JsonResult<?> alarmCountTodayStatics() {
// Only Query when Day
long startMills = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
long endMills = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();

// Pending
int unhandleCount = reportService.countByAudit(startMills, endMills, 0);

// Process
int handledCount = reportService.countByAudit(startMills, endMills, 1);

// self Dynamic Process
int autoHandledCount = reportService.countByAudit(startMills, endMills, 3);

// Closed
int closedCount = reportService.countByAudit(startMills, endMills, 2);

// Return Data
Map<String, Object> resultMap = new HashMap<>();
resultMap.put("totalCount", unhandleCount + handledCount + autoHandledCount + closedCount); // Alarm Total
resultMap.put("unhandleCount", unhandleCount); // Pending Number
resultMap.put("handledCount", handledCount + autoHandledCount + closedCount); // Process Number
return JsonResultUtils.success(resultMap);
}

/**
* by Algorithm Dimension Degree Count Alarm Process Result
* @return
*/
@PostMapping("alarm/algorithm/audit")
@ResponseBody
public JsonResult<?> alarmAlgorithmAuditStatistic(@RequestHeader("Lang") String language) {
long startMills = DateUtil.truncate(new Date(), DateField.MONTH).getTime();
long endMills = DateUtil.truncate(DateUtil.offsetMonth(new Date(), 1), DateField.MONTH).getTime();

// Count Data
List<Map<String, Object>> dataList = reportService.countAlgorithmAuditGroupBy(startMills, endMills);

//
List<StatisticAlgorithmAuditDTO> statisticAlgorithmAuditDTOList = new ArrayList<>();

List<Algorithm> algorithmList = algorithmService.list();
for(Algorithm algorithm: algorithmList) {
String name = algorithm.getName();
if(StringUtils.contains(name,"Immediate will Go Live")){
continue;
}
int unhandled = 0, handled = 0; // not Process Count, Process Count
for(Map<String, Object> dataMap: dataList) {
Long algoId = Convert.toLong(dataMap.get("algorithm_id"), 0L);
if(algoId.equals(algorithm.getId())) {
Integer auditResult = Convert.toInt(dataMap.get("audit_result"), -1);
if(auditResult == 0) {// not Process
Integer cnt = Convert.toInt(dataMap.get("cnt"), 0);
unhandled += cnt;
} else if(auditResult == 1 || auditResult == 2 || auditResult == 3) {// Process, Closed, self Dynamic Process
Integer cnt = Convert.toInt(dataMap.get("cnt"), 0);
handled += cnt;
}
}
}

//
StatisticAlgorithmAuditDTO statisticAlgorithmAuditDTO = new StatisticAlgorithmAuditDTO();
statisticAlgorithmAuditDTO.setAlgorithmId(algorithm.getId());
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
statisticAlgorithmAuditDTO.setAlgorithmName(algorithm.getEnglishName());
}else {
statisticAlgorithmAuditDTO.setAlgorithmName(algorithm.getName());
}
statisticAlgorithmAuditDTO.setUnhandleCount(unhandled);
statisticAlgorithmAuditDTO.setHandledCount(handled);
statisticAlgorithmAuditDTO.setTotalCount(unhandled + handled);
statisticAlgorithmAuditDTOList.add(statisticAlgorithmAuditDTO);
}

// for Data According to total Quantity in Line Sort
statisticAlgorithmAuditDTOList.sort(new Comparator<StatisticAlgorithmAuditDTO>() {
@Override
public int compare(StatisticAlgorithmAuditDTO o1, StatisticAlgorithmAuditDTO o2) {
return o2.getTotalCount() - o1.getTotalCount();
}
});
statisticAlgorithmAuditDTOList = statisticAlgorithmAuditDTOList.stream().limit(10).collect(Collectors.toList());
return JsonResultUtils.success(statisticAlgorithmAuditDTOList);
}

/**
* by Month Count Process Rate
* @return
*/
@PostMapping("alarm/audit/time/3month")
@ResponseBody
public JsonResult<?> alarmAuditTime3Month(@RequestHeader("Lang") String language) {
Map<Integer, Integer> reportTargetMap = reportTargetService.getDataMap();

List<AlarmAuditTimeTargetDTO> alarmAuditTimeTargetDTOList = new ArrayList<>();

for (int i = -2; i <= 0; i++) {
Date date = DateUtil.offsetMonth(DateUtil.date(), i);
Date startOfMonth = DateUtil.beginOfMonth(date);
Date endOfMonth = DateUtil.endOfMonth(date);

long startMills = DateUtil.truncate(startOfMonth, DateField.DAY_OF_MONTH).getTime();
long endMills = DateUtil.truncate(endOfMonth, DateField.DAY_OF_MONTH).getTime();

// Current Month copy
int month = DateUtil.month(startOfMonth) + 1;
String monthDate = DateUtil.format(startOfMonth,"MM Month");
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
monthDate = EnglishMonthEnum.getText(month);
}

// summary total Query
List<Map<String, Object>> results = reportService.sumAndCountAlarmAuditTime(startMills, endMills);

// Data Process
Map<String, Object> resultMap = results.get(0);
int cnt = Convert.toInt(resultMap.getOrDefault("cnt", 0));
double timeVal = 0;
if(cnt > 0) {
int times = Convert.toInt(resultMap.getOrDefault("times", 0));
timeVal = Double.valueOf(times * 1d / cnt).intValue();
}

Integer targetVal = reportTargetMap.get(month);
if(targetVal == null) {
targetVal = 0;
}

AlarmAuditTimeTargetDTO alarmAuditTimeTargetDTO = new AlarmAuditTimeTargetDTO();
alarmAuditTimeTargetDTO.setDate(monthDate);
alarmAuditTimeTargetDTO.setTargetVal(targetVal);
alarmAuditTimeTargetDTO.setTimeVal(timeVal);
alarmAuditTimeTargetDTOList.add(alarmAuditTimeTargetDTO);
}
return JsonResultUtils.success(alarmAuditTimeTargetDTOList);
}

/**
* by Week Count Process Rate
* @return
*/
@PostMapping("alarm/audit/time/4week")
@ResponseBody
public JsonResult<?> alarmAuditTime4Week() {
Map<Integer, Integer> reportTargetMap = reportTargetService.getDataMap();

List<AlarmAuditTimeTargetDTO> alarmAuditTimeTargetDTOList = new ArrayList<>();

for (int i = -3; i <= 0; i++) {
// Start and End Time
Date mondayOfThisWeek = DateUtil.beginOfWeek(DateUtil.offsetWeek(new Date(), i));
Date sundayOfThisWeek = DateUtil.endOfWeek(DateUtil.offsetWeek(new Date(), i));

long startMills = DateUtil.truncate(mondayOfThisWeek, DateField.DAY_OF_MONTH).getTime();
long endMills = DateUtil.truncate(sundayOfThisWeek, DateField.DAY_OF_MONTH).getTime();

// Current Month copy
int month = DateUtil.month(mondayOfThisWeek) + 1;

// Current Week
int week = DateUtil.weekOfYear(mondayOfThisWeek);

// summary total Query
List<Map<String, Object>> results = reportService.sumAndCountAlarmAuditTime(startMills, endMills);

// Data Process
Map<String, Object> resultMap = results.get(0);
int cnt = Convert.toInt(resultMap.getOrDefault("cnt", 0));
double timeVal = 0;
if(cnt > 0) {
int times = Convert.toInt(resultMap.getOrDefault("times", 0));
timeVal = Double.valueOf(times * 1d / cnt).intValue();
}

Integer targetVal = reportTargetMap.get(month);
if(targetVal == null) {
targetVal = 0;
}

AlarmAuditTimeTargetDTO alarmAuditTimeTargetDTO = new AlarmAuditTimeTargetDTO();
alarmAuditTimeTargetDTO.setDate("WK"+ week);
alarmAuditTimeTargetDTO.setTargetVal(targetVal);
alarmAuditTimeTargetDTO.setTimeVal(timeVal);
alarmAuditTimeTargetDTOList.add(alarmAuditTimeTargetDTO);
}
return JsonResultUtils.success(alarmAuditTimeTargetDTOList);
}

/**
* by Week Count Process Rate
* @return
*/
@PostMapping("alarm/audit/time/7day")
@ResponseBody
public JsonResult<?> alarmAuditTime7Day(@RequestHeader("Lang") String language) {
Map<Integer, Integer> reportTargetMap = reportTargetService.getDataMap();

List<AlarmAuditTimeTargetDTO> alarmAuditTimeTargetDTOList = new ArrayList<>();
for(int i = 0; i < 7; i++) {
// Start and End Date
Date date = DateUtil.offsetDay(new Date(), i - 6);
long startMills = DateUtil.truncate(date, DateField.DAY_OF_MONTH).getTime();
long endMills = DateUtil.truncate(DateUtil.offsetDay(date, 1), DateField.DAY_OF_MONTH).getTime();

// Current Month copy
int month = DateUtil.month(date) + 1;

// summary total Query
List<Map<String, Object>> results = reportService.sumAndCountAlarmAuditTime(startMills, endMills);

// Data Process
Map<String, Object> resultMap = results.get(0);
int cnt = Convert.toInt(resultMap.getOrDefault("cnt", 0));
double timeVal = 0;
if(cnt > 0) {
int times = Convert.toInt(resultMap.getOrDefault("times", 0));
timeVal = Double.valueOf(times * 1d / cnt).intValue();
}

Integer targetVal = reportTargetMap.get(month);
if(targetVal == null) {
targetVal = 0;
}

AlarmAuditTimeTargetDTO alarmAuditTimeTargetDTO = new AlarmAuditTimeTargetDTO();
alarmAuditTimeTargetDTO.setDate(DateUtil.format(date,"MM Month dd Day"));
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
alarmAuditTimeTargetDTO.setDate(EnglishMonthEnum.getText(month) +""+ DateUtil.dayOfMonth(date));
}
alarmAuditTimeTargetDTO.setTargetVal(targetVal);
alarmAuditTimeTargetDTO.setTimeVal(timeVal);
alarmAuditTimeTargetDTOList.add(alarmAuditTimeTargetDTO);
}
return JsonResultUtils.success(alarmAuditTimeTargetDTOList);
}
}