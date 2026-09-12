package com.yihecode.camera.ai.web.tracker;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.entity.tracker.TrackerReport;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.service.tracker.TrackerReportService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.tracker.dto.TrackerReportDTO;
import com.yihecode.camera.ai.web.tracker.dto.TrackerReportExcelDTO;
import com.yihecode.camera.ai.web.tracker.dto.TrackerReportTimeLineDTO;
import com.yihecode.camera.ai.web.tracker.dto.TrackerReportTimeLineV2DTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Person Stream Quantity Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Api(tags = "Person Stream Quantity Recognition _ Person Stream Quantity Management")
@SaCheckLogin
//@SaIgnore
@Controller
@RequestMapping({"/tracker/report"})
public class TrackerReportController {

@Resource
private TrackerReportService trackerReportService;

@Resource
private CameraService cameraService;

@Resource
private LocationService locationService;

@Resource
private ApDepartService apDepartService;

@ApiOperation("By Type Count summary Total Count")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="startDate", value ="Start Time (yyyy-MM-dd HH:mm:ss)"),
@ApiImplicitParam(name ="endDate", value ="End Time (yyyy-MM-dd HH:mm:ss)")
})
@SaCheckPermission(value = {"flowDsetection"}, mode = SaMode.OR)
@PostMapping({"/summary/v2"})
@ResponseBody
public JsonResult<?> summaryV2(@DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss") Date startTime,
@DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss") Date endTime) {
// when Day
Date dayStartTime = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH);
Date dayEndTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH);
Map<String, Integer> dayCountData = trackerReportService.countSummary(dayStartTime, dayEndTime);
dayCountData.put("type", 0);

// when Week
Date weekStartTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), -1), DateField.WEEK_OF_MONTH);
Date weekEndTime = DateUtil.truncate(DateUtil.offsetWeek(weekStartTime, 1), DateField.WEEK_OF_MONTH);
Map<String, Integer> weekCountData = trackerReportService.countSummary(weekStartTime, weekEndTime);
weekCountData.put("type", 1);

// when Month
Date monthStartTime = DateUtil.truncate(new Date(), DateField.MONTH);
Date monthEndTime = DateUtil.truncate(DateUtil.offsetMonth(new Date(), 1), DateField.MONTH);
Map<String, Integer> monthCountData = trackerReportService.countSummary(monthStartTime, monthEndTime);
monthCountData.put("type", 2);

// Custom
Map<String, Integer> customCountData = new HashMap<>();
customCountData.put("enterCount", 0);
customCountData.put("leaveCount", 0);
customCountData.put("remainCount", 0);
customCountData.put("type", 3);
if(startTime!= null && endTime!= null) {
customCountData = trackerReportService.countSummary(startTime, endTime);
customCountData.put("type", 3);
}

// input out Data
List<Map<String, Integer>> dataList = new ArrayList<>();
dataList.add(dayCountData);
dataList.add(weekCountData);
dataList.add(monthCountData);
dataList.add(customCountData);
return JsonResultUtils.success(dataList);
}

@ApiOperation("By Type Count summary Total Count")
@ApiImplicitParam(name ="countType", value ="Count Type,0- when Day,1- when Week,2- when Month,9- All")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/summary"})
@ResponseBody
public JsonResult<?> summary(Integer countType) {
//
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("enterCount", 0);
dataMap.put("leaveCount", 0);
dataMap.put("remainCount", 0);

//
Date startTime = null;
Date endTime = null;
if(countType == null || countType == 9) {// Query All
// pass
} else if(countType == 0) {// Query when Day
startTime = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH);
endTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH);
} else if(countType == 1) {// Query when Week
startTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.WEEK_OF_MONTH);
endTime = DateUtil.truncate(DateUtil.offsetWeek(new Date(), 1), DateField.WEEK_OF_MONTH);
} else if(countType == 2) {// Query when Month
startTime = DateUtil.truncate(new Date(), DateField.MONTH);
endTime = DateUtil.truncate(DateUtil.offsetMonth(new Date(), 1), DateField.MONTH);
} else {
return JsonResultUtils.success(dataMap);
}
//
Map<String, Integer> countData = trackerReportService.countSummary(startTime, endTime);
return JsonResultUtils.success(countData);
}

@ApiOperation("Person Stream Quantity Page Query _ Deprecated")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Pagination Number"),
@ApiImplicitParam(name ="cameraId", value ="Camera ID"),
@ApiImplicitParam(name ="startTime", value ="Start Time (yyyy-MM-dd HH:mm:ss)"),
@ApiImplicitParam(name ="endTime", value ="End Time (yyyy-MM-dd HH:mm:ss)")
})
@SaCheckPermission(value = {"flowDsetection"}, mode = SaMode.OR)
@PostMapping({"/listPage"})
@ResponseBody
public PageResult<List<TrackerReportDTO>> listPage(@RequestParam(defaultValue ="1") Integer page,
@RequestParam(defaultValue ="9") Integer limit,
@RequestParam(defaultValue ="") String cameraIds,
@RequestParam(defaultValue ="") String departIds,
@DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss") Date startTime,
@DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss") Date endTime) {
List<Long> queryCameraIds = new ArrayList<>();
if(StrUtil.isNotBlank(cameraIds)) {
List<Long> ids = Arrays.stream(cameraIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
queryCameraIds.addAll(ids);
}

List<Long> queryDepartIds = new ArrayList<>();
if(StrUtil.isNotBlank(departIds)) {
List<Long> ids = Arrays.stream(departIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
queryDepartIds.addAll(ids);
}

IPage<TrackerReport> pageResult = trackerReportService.listPage(page, limit, queryCameraIds, queryDepartIds, startTime, endTime);
List<TrackerReport> trackerReports = pageResult.getRecords();
if(trackerReports == null) {
trackerReports = new ArrayList<>();
}
//
List<Location> boxes = locationService.listDataByType("2");
Map<Long, Location> boxMap = boxes.stream().collect(Collectors.toMap(Location::getId, Function.identity(), (s1, s2) -> s1));
List<TrackerReportDTO> trackerReportDTOS = new ArrayList<>();
for(TrackerReport trackerReport: trackerReports) {
Camera camera = cameraService.getById(trackerReport.getCameraId());
String boxIp ="/";
String boxName ="/";
String departName ="/";
if(camera!= null) {
Location box = boxMap.get(camera.getLocationId());
if(box!= null) {
boxIp = box.getIpAddr();
boxName = box.getName();

//
Depart depart = apDepartService.getById(box.getDepartId());
if(depart!= null) {
departName = depart.getName();
}
}
}

//
TrackerReportDTO trackerReportDTO = new TrackerReportDTO();
trackerReportDTO.setCameraId(trackerReport.getCameraId());
trackerReportDTO.setCameraName(camera == null?"/": camera.getName());
trackerReportDTO.setEnterCount(trackerReport.getEnterCount());
trackerReportDTO.setLeaveCount(trackerReport.getLeaveCount());
trackerReportDTO.setRemainCount(Math.max(0, (trackerReport.getEnterCount() - trackerReport.getLeaveCount())));
trackerReportDTO.setDepartName(departName);
trackerReportDTO.setBoxIp(boxIp);
trackerReportDTO.setBoxName(boxName);
trackerReportDTOS.add(trackerReportDTO);
}
return PageResultUtils.success(pageResult.getTotal(), trackerReportDTOS);
}

@ApiOperation("Time Axis _ Deprecated")
@ApiImplicitParams({
@ApiImplicitParam(name ="hours", value ="Display Week long, form Bit: h")
})
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping("timeline")
@ResponseBody
public JsonResult<List<TrackerReportTimeLineDTO>> timeline(@RequestParam(defaultValue ="10") Integer hours) {
Date startTime = DateUtil.truncate((DateUtil.truncate(DateUtil.offsetHour(new Date(), hours * -1), DateField.HOUR_OF_DAY)), DateField.SECOND);
List<TrackerReportTimeLineDTO> trackerReportTimeLineDTOS = new ArrayList<>();
while(true) {
if((startTime.getTime() - System.currentTimeMillis()) > 0) {
break;
}
String label = DateUtil.format(startTime,"HH:mm");
Date endTime = DateUtil.offsetMinute(startTime, 30);
Map<String, Integer> countData = trackerReportService.countSummary(startTime, endTime);

TrackerReportTimeLineDTO trackerReportTimeLineDTO = new TrackerReportTimeLineDTO();
trackerReportTimeLineDTO.setLabel(label);
trackerReportTimeLineDTO.setEnterCount(countData.get("enterCount"));
trackerReportTimeLineDTO.setLeaveCount(countData.get("leaveCount"));
trackerReportTimeLineDTO.setRemainCount(countData.get("remainCount"));
trackerReportTimeLineDTOS.add(trackerReportTimeLineDTO);

//
startTime = endTime;
}
return JsonResultUtils.success(trackerReportTimeLineDTOS);
}

@ApiOperation("Time Axis")
@ApiImplicitParams({
@ApiImplicitParam(name ="type", value ="0- when Day,1- when Week,2- when Month,3- Custom"),
@ApiImplicitParam(name ="startTime", value ="Start Time (yyyy-MM-dd HH:mm:ss)"),
@ApiImplicitParam(name ="endTime", value ="End Time (yyyy-MM-dd HH:mm:ss)")
})
@SaCheckPermission(value = {"flowDsetection"}, mode = SaMode.OR)
@PostMapping("timeline/v2")
@ResponseBody
public JsonResult<TrackerReportTimeLineV2DTO> timelineV2(@RequestParam(defaultValue ="0") Integer type,
@DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss") Date startTime,
@DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss") Date endTime) {
// when Day
if(type == 0) {
Date qStartTime = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH);
Date qEndTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH);

List<Map<String, Object>> dataList = trackerReportService.groupByHour(qStartTime, qEndTime);
if(dataList == null) {
dataList = new ArrayList<>();
}
Map<String, Map<String, Object>> dataMap = dataList.stream().collect(Collectors.toMap(each -> Objects.toString(each.get("time_type"),""), each -> each, (key1, key2) -> key1));
// Time Label, in in Headcount, Away open Headcount, Keep Headcount
List<String> labels = new ArrayList<>();
List<Integer> enterCounts = new ArrayList<>();
List<Integer> leaveCounts = new ArrayList<>();
List<Integer> remainCounts = new ArrayList<>();
for(int i = 0; i < 24; i++) {
String prefix = i < 10? ("0"+ i): String.valueOf(i);
labels.add(prefix +":00");
if(dataMap.containsKey(prefix)) {
Map<String, Object> data = dataMap.get(prefix);
int enterCount = Convert.toInt(data.get("enter_count"), 0);
int leaveCount = Convert.toInt(data.get("leave_count"), 0);
int remainCount = enterCount - leaveCount;
enterCounts.add(enterCount);
leaveCounts.add(leaveCount);
remainCounts.add(Math.max(0, remainCount));
} else {
enterCounts.add(0);
leaveCounts.add(0);
remainCounts.add(0);
}
}
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(labels);
v2DTO.setEnterCounts(enterCounts);
v2DTO.setLeaveCounts(leaveCounts);
v2DTO.setRemainCounts(remainCounts);
return JsonResultUtils.success(v2DTO);
}

// when Week
if(type == 1) {
Date qStartTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), -1), DateField.WEEK_OF_MONTH);
Date qEndTime = DateUtil.truncate(DateUtil.offsetWeek(qStartTime, 1), DateField.WEEK_OF_MONTH);

List<Map<String, Object>> dataList = trackerReportService.groupByWeek(qStartTime, qEndTime);
if(dataList == null) {
dataList = new ArrayList<>();
}
Map<String, Map<String, Object>> dataMap = dataList.stream().collect(Collectors.toMap(each -> Objects.toString(each.get("time_type"),""), each -> each, (key1, key2) -> key1));
// Time Label, in in Headcount, Away open Headcount, Keep Headcount
List<String> labels = new ArrayList<>();
List<Integer> enterCounts = new ArrayList<>();
List<Integer> leaveCounts = new ArrayList<>();
List<Integer> remainCounts = new ArrayList<>();
for(int i = 1; i < 8; i++) {
String label ="";
switch (i) {
case 1:
label ="Week One";
break;
case 2:
label ="Week Two";
break;
case 3:
label ="Week Three";
break;
case 4:
label ="Week Four";
break;
case 5:
label ="Week Five";
break;
case 6:
label ="Week Six";
break;
case 7:
label ="Week Day";
break;
}
labels.add(label);
if(dataMap.containsKey(String.valueOf(i))) {
Map<String, Object> data = dataMap.get(String.valueOf(i));
int enterCount = Convert.toInt(data.get("enter_count"), 0);
int leaveCount = Convert.toInt(data.get("leave_count"), 0);
int remainCount = enterCount - leaveCount;
enterCounts.add(enterCount);
leaveCounts.add(leaveCount);
remainCounts.add(Math.max(0, remainCount));
} else {
enterCounts.add(0);
leaveCounts.add(0);
remainCounts.add(0);
}
}
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(labels);
v2DTO.setEnterCounts(enterCounts);
v2DTO.setLeaveCounts(leaveCounts);
v2DTO.setRemainCounts(remainCounts);
return JsonResultUtils.success(v2DTO);
}

// when Month
if(type == 2) {
Date qStartTime = DateUtil.truncate(new Date(), DateField.MONTH);
Date qEndTime = DateUtil.truncate(DateUtil.offsetMonth(new Date(), 1), DateField.MONTH);

List<Map<String, Object>> dataList = trackerReportService.groupByMonth(qStartTime, qEndTime);
if(dataList == null) {
dataList = new ArrayList<>();
}
Map<String, Map<String, Object>> dataMap = dataList.stream().collect(Collectors.toMap(each -> Objects.toString(each.get("time_type"),""), each -> each, (key1, key2) -> key1));
// Time Label, in in Headcount, Away open Headcount, Keep Headcount
List<String> labels = new ArrayList<>();
List<Integer> enterCounts = new ArrayList<>();
List<Integer> leaveCounts = new ArrayList<>();
List<Integer> remainCounts = new ArrayList<>();
YearMonth yearMonth = YearMonth.now();
int maxDays = yearMonth.lengthOfMonth();
for(int i = 1; i <= maxDays; i++) {
labels.add(String.valueOf(i));
if(dataMap.containsKey(String.valueOf(i))) {
Map<String, Object> data = dataMap.get(String.valueOf(i));
int enterCount = Convert.toInt(data.get("enter_count"), 0);
int leaveCount = Convert.toInt(data.get("leave_count"), 0);
int remainCount = enterCount - leaveCount;
enterCounts.add(enterCount);
leaveCounts.add(leaveCount);
remainCounts.add(Math.max(0, remainCount));
} else {
enterCounts.add(0);
leaveCounts.add(0);
remainCounts.add(0);
}
}
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(labels);
v2DTO.setEnterCounts(enterCounts);
v2DTO.setLeaveCounts(leaveCounts);
v2DTO.setRemainCounts(remainCounts);
return JsonResultUtils.success(v2DTO);
}

if(type == 3) {
if(startTime == null || endTime == null) {
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(new ArrayList<>());
v2DTO.setEnterCounts(new ArrayList<>());
v2DTO.setLeaveCounts(new ArrayList<>());
v2DTO.setRemainCounts(new ArrayList<>());
return JsonResultUtils.success(v2DTO);
}

long hourBetween = Duration.between(startTime.toInstant(), endTime.toInstant()).toHours();
// if(hourBetween <= 0) {
// TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
// v2DTO.setLabels(new ArrayList<>());
// v2DTO.setEnterCounts(new ArrayList<>());
// v2DTO.setLeaveCounts(new ArrayList<>());
// v2DTO.setRemainCounts(new ArrayList<>());
// return JsonResultUtils.success(v2DTO);
//}

// small at 8 h, half h Interval
if(hourBetween <= 8) {
List<String> labels = new ArrayList<>();
List<Integer> enterCounts = new ArrayList<>();
List<Integer> leaveCounts = new ArrayList<>();
List<Integer> remainCounts = new ArrayList<>();
while(true) {
int result = startTime.compareTo(endTime);
if(result > 0) {
break;
}

Date qEndTime = DateUtil.offsetMinute(startTime, 30);
String label = DateUtil.format(startTime,"MM-dd HH:mm");
Map<String, Integer> countData = trackerReportService.countSummary(startTime, qEndTime);
labels.add(label);
enterCounts.add(countData.get("enterCount"));
leaveCounts.add(countData.get("leaveCount"));
remainCounts.add(countData.get("remainCount"));
//
startTime = qEndTime;
}
//
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(labels);
v2DTO.setEnterCounts(enterCounts);
v2DTO.setLeaveCounts(leaveCounts);
v2DTO.setRemainCounts(remainCounts);
return JsonResultUtils.success(v2DTO);
}

// small at 24 h,1 h Interval
if(hourBetween <= 24) {
List<String> labels = new ArrayList<>();
List<Integer> enterCounts = new ArrayList<>();
List<Integer> leaveCounts = new ArrayList<>();
List<Integer> remainCounts = new ArrayList<>();
while(true) {
int result = startTime.compareTo(endTime);
if(result > 0) {
break;
}

Date qEndTime = DateUtil.offsetMinute(startTime, 60);
String label = DateUtil.format(startTime,"MM-dd HH:mm");
Map<String, Integer> countData = trackerReportService.countSummary(startTime, qEndTime);
labels.add(label);
enterCounts.add(countData.get("enterCount"));
leaveCounts.add(countData.get("leaveCount"));
remainCounts.add(countData.get("remainCount"));
//
startTime = qEndTime;
}
//
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(labels);
v2DTO.setEnterCounts(enterCounts);
v2DTO.setLeaveCounts(leaveCounts);
v2DTO.setRemainCounts(remainCounts);
return JsonResultUtils.success(v2DTO);
}

// small at 3 day,4 h Interval
if(hourBetween <= 3 * 24) {
List<String> labels = new ArrayList<>();
List<Integer> enterCounts = new ArrayList<>();
List<Integer> leaveCounts = new ArrayList<>();
List<Integer> remainCounts = new ArrayList<>();
while(true) {
int result = startTime.compareTo(endTime);
if(result > 0) {
break;
}

Date qEndTime = DateUtil.offsetHour(startTime, 4);
String label = DateUtil.format(startTime,"MM-dd HH:mm");
Map<String, Integer> countData = trackerReportService.countSummary(startTime, qEndTime);
labels.add(label);
enterCounts.add(countData.get("enterCount"));
leaveCounts.add(countData.get("leaveCount"));
remainCounts.add(countData.get("remainCount"));
//
startTime = qEndTime;
}
//
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(labels);
v2DTO.setEnterCounts(enterCounts);
v2DTO.setLeaveCounts(leaveCounts);
v2DTO.setRemainCounts(remainCounts);
return JsonResultUtils.success(v2DTO);
}

// small at 5 day,8 h Interval
if(hourBetween <= 5 * 24) {
List<String> labels = new ArrayList<>();
List<Integer> enterCounts = new ArrayList<>();
List<Integer> leaveCounts = new ArrayList<>();
List<Integer> remainCounts = new ArrayList<>();
while(true) {
int result = startTime.compareTo(endTime);
if(result > 0) {
break;
}

Date qEndTime = DateUtil.offsetHour(startTime, 8);
String label = DateUtil.format(startTime,"MM-dd HH:mm");
Map<String, Integer> countData = trackerReportService.countSummary(startTime, qEndTime);
labels.add(label);
enterCounts.add(countData.get("enterCount"));
leaveCounts.add(countData.get("leaveCount"));
remainCounts.add(countData.get("remainCount"));
//
startTime = qEndTime;
}
//
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(labels);
v2DTO.setEnterCounts(enterCounts);
v2DTO.setLeaveCounts(leaveCounts);
v2DTO.setRemainCounts(remainCounts);
return JsonResultUtils.success(v2DTO);
}

// super over 5 day,24 h Interval
if(hourBetween > 5 * 24) {
List<String> labels = new ArrayList<>();
List<Integer> enterCounts = new ArrayList<>();
List<Integer> leaveCounts = new ArrayList<>();
List<Integer> remainCounts = new ArrayList<>();
while(true) {
int result = startTime.compareTo(endTime);
if(result > 0) {
break;
}

Date qEndTime = DateUtil.offsetHour(startTime, 24);
String label = DateUtil.format(startTime,"MM-dd HH:mm");
Map<String, Integer> countData = trackerReportService.countSummary(startTime, qEndTime);
labels.add(label);
enterCounts.add(countData.get("enterCount"));
leaveCounts.add(countData.get("leaveCount"));
remainCounts.add(countData.get("remainCount"));
//
startTime = qEndTime;
}
//
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(labels);
v2DTO.setEnterCounts(enterCounts);
v2DTO.setLeaveCounts(leaveCounts);
v2DTO.setRemainCounts(remainCounts);
return JsonResultUtils.success(v2DTO);
}
}

// Other
TrackerReportTimeLineV2DTO v2DTO = new TrackerReportTimeLineV2DTO();
v2DTO.setLabels(new ArrayList<>());
v2DTO.setEnterCounts(new ArrayList<>());
v2DTO.setLeaveCounts(new ArrayList<>());
v2DTO.setRemainCounts(new ArrayList<>());
return JsonResultUtils.success(v2DTO);
}

@ApiOperation("Export Data")
@ApiImplicitParams({
@ApiImplicitParam(name ="countType", value ="Export Type:0- when Day,1- when Week,2- when Month,9- Custom"),
@ApiImplicitParam(name ="startTime", value ="Start Time, Custom Export Type must transmit"),
@ApiImplicitParam(name ="endTime", value ="End Time, Custom Export Type must transmit")
})
@SaCheckPermission(value = {"flowDsetection"}, mode = SaMode.OR)
@PostMapping("export")
@ResponseBody
public void export(@RequestParam(defaultValue ="9") Integer countType,
@DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss") Date startTime,
@DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss") Date endTime,
HttpServletResponse response) throws Exception {
boolean isQuery = true;
if(countType == null) {// Query All
isQuery = false;
} else if(countType == 0) {// Query when Day
startTime = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH);
endTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH);
} else if(countType == 1) {// Query when Week
startTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.WEEK_OF_MONTH);
endTime = DateUtil.truncate(DateUtil.offsetWeek(new Date(), 1), DateField.WEEK_OF_MONTH);
} else if(countType == 2) {// Query when Month
startTime = DateUtil.truncate(new Date(), DateField.MONTH);
endTime = DateUtil.truncate(DateUtil.offsetMonth(new Date(), 1), DateField.MONTH);
} else if(countType == 9) {// Custom Query
if(startTime == null || endTime == null) {
isQuery = false;
}
}

if(!isQuery) {
// Reset response
response.reset();
response.setContentType("application/json");
response.setCharacterEncoding("utf-8");
Map<String, Object> map = new HashMap<>();
map.put("code", 500);
map.put("msg","Param Error");
response.getWriter().println(JSON.toJSONString(map));
} else {

// Here Note has same Learn reverse should make Use swagger will import Cause each kind Problem, Please Direct connect Use Browser or Use postman
try {
List<TrackerReport> trackerReports = trackerReportService.countGroupbyCamera(startTime, endTime);
if (trackerReports == null) {
trackerReports = new ArrayList<>();
}
//
List<Camera> cameraList = cameraService.list();
if(cameraList == null) {
cameraList = new ArrayList<>();
}
Map<Long, String> cameraMap = cameraList.stream().collect(Collectors.toMap(Camera::getId, Camera::getName, (s1, s2) -> s1));
Map<Long, Long> boxIdMap = cameraList.stream().collect(Collectors.toMap(Camera::getId, Camera::getLocationId, (s1, s2) -> s1));
//
List<Location> boxList = locationService.listDataByType("2");
Map<Long, Location> boxMap = boxList.stream().collect(Collectors.toMap(Location::getId, Function.identity(), (s1, s2) -> s1));

//
List<TrackerReportExcelDTO> trackerReportExcelDTOS = new ArrayList<>();
for(TrackerReport trackerReport: trackerReports) {
String cameraName ="/";
String boxIp ="/";
String boxName ="/";
if(cameraMap.containsKey(trackerReport.getCameraId())) {
cameraName = cameraMap.get(trackerReport.getCameraId());
}
if(boxIdMap.containsKey(trackerReport.getCameraId())) {
Long boxId = boxIdMap.get(trackerReport.getCameraId());
Location boxData = boxMap.get(boxId);
if(boxData!= null) {
boxIp = boxData.getIpAddr();
boxName = boxData.getName();
}
}
Integer enterCount = trackerReport.getEnterCount();
Integer leaveCount = trackerReport.getLeaveCount();
int remainCount = enterCount - leaveCount;
TrackerReportExcelDTO trackerReportExcelDTO = new TrackerReportExcelDTO();
trackerReportExcelDTO.setBoxIp(boxIp);
trackerReportExcelDTO.setBoxName(boxName);
trackerReportExcelDTO.setCameraName(cameraName);
trackerReportExcelDTO.setEnterCount(enterCount);
trackerReportExcelDTO.setLeaveCount(leaveCount);
trackerReportExcelDTO.setRemainCount(Math.max(0, remainCount));
trackerReportExcelDTOS.add(trackerReportExcelDTO);
}
//
response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
response.setCharacterEncoding("utf-8");
String fileName = URLEncoder.encode("Export _ Download","UTF-8").replaceAll("\\+","%20");
response.setHeader("Content-disposition","attachment;filename*=utf-8''"+ fileName +".xlsx");
EasyExcel.write(response.getOutputStream(), TrackerReportExcelDTO.class).autoCloseStream(Boolean.FALSE).sheet("Data").doWrite(trackerReportExcelDTOS);
} catch (Exception e) {
// Reset response
response.reset();
response.setContentType("application/json");
response.setCharacterEncoding("utf-8");
Map<String, Object> map = new HashMap<>();
map.put("code", 500);
map.put("msg","Export Error");
response.getWriter().println(JSON.toJSONString(map));
}
}
}
}
