package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.*;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.dto.ReportSummaryTaskDTO;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.enums.ReportType;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.utils.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.yihecode.camera.ai.vo.ReportMessage;
import com.yihecode.camera.ai.web.api.ReportPushService;
import com.yihecode.camera.ai.web.api.comm.AlarmPushResult;
import com.yihecode.camera.ai.web.api.comm.AlarmPushServcie;
import com.yihecode.camera.ai.web.app.push.AppPushController;
import com.yihecode.camera.ai.web.dto.ReportAuditResultStaticsDTO;
import com.yihecode.camera.ai.web.dto.ReportMustDTO;
import com.yihecode.camera.ai.web.dto.ReportSummaryDTO;
import com.yihecode.camera.ai.web.map.dto.ReportDTO;
import com.yihecode.camera.ai.web.vo.*;
import com.yihecode.camera.ai.websocket.ReportWebsocket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
* Alarm Management, Query / show show / Review etc
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Alarm Management")
@SaCheckLogin
@Slf4j
@Controller
@RequestMapping({"/report"})
public class ReportController {

    //
@Autowired
private ReportService reportService;

//
@Autowired
private CameraService cameraService;

//
@Autowired
private AlgorithmService algorithmService;

//
@Autowired
private CameraAlgorithmService cameraAlgorithmService;

//
@Autowired
private ConfigService configService;

//
@Autowired
private ReportPushService reportPushService;

@Autowired
private ReportWebsocket reportWebsocket;

@Autowired
private AlarmLevelService alarmLevelService;

@Autowired
private AppPushController appPushController;

@Autowired
private AccountService accountService;

@Autowired
private ApDepartService apDepartService;

@Autowired
private LocationService locationService;

@Autowired
private AlarmPushServcie alarmPushServcie;

@Autowired
private CameraGroupService cameraGroupService;

@Autowired
private CameraGroupItemService cameraGroupItemService;

@Autowired
private ReportSummaryTaskService reportSummaryTaskService;

@Autowired
private ReportSummaryService reportSummaryService;

@Autowired
private ProjectConfig projectConfig;

@Value("${uploadDir}")
private String uploadDir;


/**
* Clear Wash Alarm Record
* @param reportDTO
* @return
*/
@ApiOperation(("Annotation Alert"))
@PostMapping("/markReport")
@ResponseBody
public JsonResult markReport(@RequestBody ReportDTO reportDTO){
reportService.markReport(reportDTO);
return JsonResultUtils.success();
}

/**
* Clear Wash Alarm Record
* @param reportDTO
* @return
*/
@ApiOperation(("Save Alert"))
@PostMapping("/deleteReport")
@ResponseBody
public JsonResult deleteReport(@RequestBody ReportDTO reportDTO){
reportService.deleteReport(reportDTO);
return JsonResultUtils.success();
}

@ApiOperation(("Save Alert part Analyze Temp Hour table"))
@PostMapping("/saveReportSummaryTask")
@ResponseBody
public JsonResult saveReportSummaryTask(@RequestBody ReportSummaryTaskDTO reportSummaryTaskDTO) {
List<ReportSummaryTask> tasks = new ArrayList<>();

String startDate = reportSummaryTaskDTO.getStartDate();
String endDate = reportSummaryTaskDTO.getEndDate();

LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd"));;
LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd"));;
// Iterate Date Range
for (LocalDate date = startLocalDate;!date.isAfter(endLocalDate); date = date.plusDays(1)) {
int year = date.getYear();
int month = date.getMonthValue();
int day = date.getDayOfMonth();
int fullDate = year * 10000 + month * 100 + day;

ReportSummaryTask task = new ReportSummaryTask();
task.setTaskYear(year);
task.setTaskMonth(month);
task.setTaskDay(day);
task.setTaskDate(fullDate);

tasks.add(task);
}

reportSummaryTaskService.saveBatch(tasks);
return JsonResultUtils.success();
}
@ApiOperation(("Save Alert"))
@PostMapping("/createReport")
@ResponseBody
public JsonResult createReport(@RequestParam(defaultValue ="1") Integer page,
@RequestParam(defaultValue ="10") Integer limit,
Integer count, Integer year, Integer month, Integer day,
Integer hour, Integer minute, Integer offSetMonth, Integer offSetDay) {
Report reportQuery = new Report();
//
IPage<Report> pageResult = this.reportService.listPage(new Page<>(page, limit), reportQuery);
// List<Report> cameraList = this.reportService.listPageGroupByCamera(new Page<>(page, limit), reportQuery);
List<Report> reportList = pageResult.getRecords();
log.info("------------------------------ Query to Report Report List ------------------------reportList: {}",
JSON.toJSON(reportList));
if (CollUtil.isEmpty(reportList)) {
reportList = new ArrayList<>();
}
List<Report> saveReportList = new ArrayList<>();
List<Date> dateList = this.generateRandomDates(count, year, month, day, hour, minute, offSetMonth, offSetDay);
log.info("Generate Random Date List: {},dateListSize:{}, reportListSize:{}",
JSON.toJSON(dateList), dateList.size(), reportList.size());
// Ensure Random Select Report Report Count not super over Report Report List real International Count
int randomReportCount = Math.min(dateList.size(), reportList.size());
// randomReportCount = Math.min(randomReportCount, cameraList.size());
log.info("Random Select Report Report Count: {}", randomReportCount);
if (randomReportCount > 0) {
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// Random Select Report Report and Date
Random random = new Random();
for (Date date: dateList) {
int index = RandomUtil.randomInt(randomReportCount - 1);
Report randomReport = reportList.get(index);
// Report cameraReport = cameraList.get(index);
// Copy Property and Set new Create Time
Report saveReport = new Report();
BeanUtils.copyProperties(randomReport, saveReport);
saveReport.setId(null);
saveReport.setCreatedAt(date);
saveReport.setCameraId(randomReport.getCameraId());
double probability = random.nextDouble();
if (probability < 0.15) {
saveReport.setAuditResult(2);
}else{
saveReport.setAuditResult(1);
}
Calendar calendar = Calendar.getInstance();
calendar.setTime(date);
int randomSeconds = 5 + new Random().nextInt(6); // Generate 5-10 Random Number
calendar.add(Calendar.SECOND, randomSeconds);
Date auditDate = calendar.getTime();

saveReport.setAuditAt(auditDate);
saveReport.setAuditState(1);
String dateString = sdf.format(date);
long time = DateUtil.parse(dateString,"yyyy-MM-dd HH:mm:ss").getTime();
saveReport.setCreatedMills(time);
saveReportList.add(saveReport);
}
}
log.info("Standard device Save Report Report List: {}", JSON.toJSON(saveReportList));
return JsonResultUtils.success(reportService.saveBatch(saveReportList));
}


public List<Date> generateRandomDates(Integer count, Integer year, Integer month,
Integer day, Integer hour, Integer minute,
Integer offSetMonth, Integer offSetDay) {
List<Date> dates = new ArrayList<>();
log.info("Generate Random Date List, Param: count={}, year={}, month={}, day={}, hour={}, minute={}", count, year, month, day, hour, minute);
// Start start Time
LocalDateTime startTime = LocalDateTime.of(year, month, day, hour, minute);
log.info("Start start Time: {}", startTime);
LocalDateTime endTime = null;
if(ObjectUtil.isNotNull(offSetMonth)) {
endTime = startTime.plusMonths(offSetMonth);
} else if (ObjectUtil.isNotNull(offSetDay)) {
endTime = startTime.plusDays(offSetDay);
}
log.info("End Time: {}", endTime);
// Calculate total Time Cross Degree (s)
long totalSeconds = ChronoUnit.SECONDS.between(startTime, endTime);
log.info("total Time Cross Degree (s): {}", totalSeconds);
// like Result Time Cross Degree not Enough with Generate count Date, rule Throw Exception
if (totalSeconds < count) {
throw new IllegalArgumentException("Time Cross Degree not Enough with Generate Refer Fixed Date Count");
}

// Calculate Each Date of between Time increase Quantity (s)
long incrementSeconds = totalSeconds / count;
log.info("Each Date of between Time increase Quantity (s): {}", incrementSeconds);
// Generate count Average Even deliver increase Date
for (int i = 0; i < count; i++) {
LocalDateTime currentTime = startTime.plusSeconds(i * incrementSeconds);
// Format Change Time and Convert for Date Object
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String formattedTime = currentTime.format(formatter);
Date date = DateUtil.parse(formattedTime);
dates.add(date);
}
log.info("Generate Date List: {}", JSON.toJSON(dates));
return dates;
}
/**
*
* @return
*/
@ApiOperation(("Query Alert Type List"))
@SaCheckRole(value = {"Data Annotation member"}, mode = SaMode.OR)
@PostMapping({"/reportTypes"})
@ResponseBody
public JsonResult reportTypeList() {
return JsonResultUtils.success(ReportType.toList());
}

/**
*
* @param page
* @param limit
* @param cameraId
* @param algorithmId
* @param type
* @return
*/
@ApiOperation("Query Alert Data List")
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping({"/listPage"})
@ResponseBody
public PageResult listPage(@RequestParam(defaultValue ="1") Integer page,
@RequestParam(defaultValue ="10") Integer limit,
Long cameraId,
Long algorithmId,
Integer type,
@RequestParam(required = false) List<Integer> markList,
String startDate,
String endDate,
Long alarmLevelId,
String departIds,
String auditResult,
@RequestParam(defaultValue ="0") Integer display,
@RequestParam(defaultValue ="0") Integer isHistory,
@RequestHeader("Lang") String language) {
// Export Department Condition
List<Long> exportDepartIds = new ArrayList<>();
if(StrUtil.isNotBlank(departIds)) {
String[] sps = departIds.split(",");
try {
for(String sp: sps) {
exportDepartIds.add(Long.valueOf(sp));
}
} catch (Exception e) {
return PageResultUtils.fail("Department Param Error");
}
}
//
// Determine Whether super Level Management member
List<Long> queryDepartIds = new ArrayList<>();
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return PageResultUtils.success(0L, new ArrayList<>());
}

// Management Query
if(account.getIsSuper()!= null && account.getIsSuper() == 1) {// super Level Management member
//log.info("is supper");
if(!exportDepartIds.isEmpty()) {
// List<Long> locationIdsByDeparts = locationService.getLocationIdsByDeparts(exportDepartIds);
// if(locationIdsByDeparts.isEmpty()) {
// throw new BizException("not has Phase close Data Need Export");
//}
// queryLocationIds.addAll(locationIdsByDeparts);
queryDepartIds.addAll(exportDepartIds);
}
} else {
//log.info("not super");
List<Long> departIdsByAccount = apDepartService.getCurrentAndChildIds(account.getDepartId()); // By User belong belong Department Query Current Department and child Department
//log.info("departIdsByAccount {}", departIdsByAccount);
if(!exportDepartIds.isEmpty()) {
//log.info("exportDepartIds {}", exportDepartIds);
List<Long> includeDepartIds = new ArrayList<>();
for(Long departId: exportDepartIds) {
if(departIdsByAccount.contains(departId)) {
includeDepartIds.add(departId); // Determine Select Department Whether In User belong belong Department and child Department
}
}
//
if(includeDepartIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>());
}
//
// List<Long> locationIdsByDeparts = locationService.getLocationIdsByDeparts(includeDepartIds); // Query belong belong Department and child Department Corresponding Box
// if(locationIdsByDeparts.isEmpty()) {
// throw new BizException("not has Phase close Data Need Export");
//}
// queryLocationIds.addAll(locationIdsByDeparts);
queryDepartIds.addAll(includeDepartIds);
} else {
// List<Long> locationIdsByDeparts = locationService.getLocationIdsByDeparts(departIdsByAccount); // Query belong belong Department and child Department Corresponding Box
// if(locationIdsByDeparts.isEmpty()) {
// throw new BizException("not has Phase close Data Need Export");
//}
// queryLocationIds.addAll(locationIdsByDeparts);
queryDepartIds.addAll(departIdsByAccount);
}
}

//log.info("departIds {}", queryDepartIds);

long startMills = 0L, endMills = 0L; // Start Time and End Time ms Value
if(StrUtil.isBlank(startDate) && StrUtil.isBlank(endDate)) {// Only Query when day
return PageResultUtils.fail("Please select Start Time and End Time");
} else if(StrUtil.isNotBlank(startDate) && StrUtil.isBlank(endDate)) {// Only Query with Start Log for when day
return PageResultUtils.fail("Please select Start Time");
} else if(StrUtil.isNotBlank(startDate) && StrUtil.isBlank(endDate)) {// Only Query with End Log for when day
return PageResultUtils.fail("Please select End Time");
} else {// with Start Date and End Date Query
Date date1 = DateUtil.parse(startDate,"yyyy-MM-dd HH:mm:ss");
// startMills = date1.getTime();
Date date2 = DateUtil.parse(endDate,"yyyy-MM-dd HH:mm:ss");
// endMills = date2.getTime();
if(!ObjectUtil.equals(1, isHistory)) {
// Calculate Three Month front Date
Date threeMonthsAgo = DateUtil.offsetMonth(new Date(), -3);
// like Result Start Time early at Three Month front, rule self Dynamic Adjust whole for Three Month front
if (date1.before(threeMonthsAgo)) {
date1 = threeMonthsAgo;
}
}
startMills = date1.getTime();
endMills = date2.getTime();
}

Report reportQuery = new Report();
reportQuery.setCameraId(cameraId);
reportQuery.setAlgorithmId(algorithmId);
reportQuery.setType(type);

List<Integer> auditResults = new ArrayList<>();
if(StrUtil.isNotBlank(auditResult)) {
String[] ars = auditResult.split(",");
auditResults = Arrays.stream(ars).map(Integer::valueOf).collect(Collectors.toList());
}
if(ObjectUtil.equals(1, isHistory)){
log.info("isHistory:{}", isHistory);
display = null;
}
log.info("Alert Data Query Account account Department id->queryDepartIds;{}", queryDepartIds);
IPage<Report> pageResult = this.reportService.listByPage(new Page<>(page, limit), cameraId, algorithmId, type, startMills, endMills, alarmLevelId, queryDepartIds, display, auditResults, markList);
List<Report> reportList = pageResult.getRecords();
if (reportList == null) {
reportList = new ArrayList<>();
}
Map<Long, String> cameraNames = new HashMap<>();
List<Camera> cameraList = this.cameraService.list();
if (cameraList!= null) {
for (Camera camera: cameraList) {
cameraNames.put(camera.getId(), camera.getName());
}
}

// Query Alert Level List
Map<Long, AlarmLevel> alarmLevelMap = alarmLevelService.getDataMap();

//
Map<Long, String> algorithmNames = new HashMap<>();
Map<Long, String> algorithmEnglishNames = new HashMap<>();
Map<Long, AlarmLevel> algorithmAlarmLevels = new HashMap<>();
List<Algorithm> algorithmList = this.algorithmService.list();
if (algorithmList!= null) {
for (Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());
algorithmEnglishNames.put(algorithm.getId(), algorithm.getEnglishName());

// Algorithm Corresponding Alert Level Name
algorithmAlarmLevels.put(algorithm.getId(), alarmLevelMap.get(algorithm.getAlarmLevelId()));
}
}

// Push Address
String reportPushUrl = configService.getByValTag("reportPushUrl");

// Data Process
for (Report report: reportList) {
String cameraName = cameraNames.get(report.getCameraId());
String algorithmName = algorithmNames.get(report.getAlgorithmId());
String algorithmEnglishName = algorithmEnglishNames.get(report.getAlgorithmId());
report.setCameraName(cameraName == null?"": cameraName);
report.setAlgorithmName(algorithmName == null?"": algorithmName);
report.setCreatedStr(report.getCreatedAt()!= null? DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"):"");
String typeName ="UNKNOW";
if (ReportType.AI.getType() == report.getType()) {
typeName = ReportType.AI.getText();
} else if (ReportType.STREAM.getType() == report.getType()) {
typeName = ReportType.STREAM.getText();
}
report.setTypeName(typeName);
report.setAlarmLevel(algorithmAlarmLevels.get(report.getAlgorithmId()));

// Whether Need Manual Review Process
report.setMustAudit((report.getAuditResult() == null || report.getAuditResult() == 0));

// Whether Need Push
report.setMustPush(StrUtil.isNotBlank(reportPushUrl) && (report.getPushed() == null || report.getPushed() == 0));

// Calculate Process Hour long
report.setAuditTimeLen(formatDuration(report.getCreatedAt(), report.getAuditAt()));

if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
report.setAlgorithmName(algorithmEnglishName == null?"": algorithmEnglishName);
}
}
return PageResultUtils.success(pageResult.getTotal(), reportList);
}

/**
*
* @param id
* @param modelMap
* @return
*/
@ApiIgnore
@SaCheckPermission("XXXXXXX")
@GetMapping({"/detail"})
public String detail(Long id, ModelMap modelMap) {
Report report = reportService.getById(id);
if(report!= null && report.getCreatedAt()!= null) {
report.setCreatedStr(DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
}
modelMap.addAttribute("report", report);

//
int width = 1;
int height = 1;
try {
File file = new File(report.getFileName());
if (file.exists()) {
BufferedImage bi = ImgUtil.read(file);
width = bi.getWidth();
height = bi.getHeight();
}
} catch (Exception e) {
//
}
modelMap.addAttribute("width", width <= 0? 1: width);
modelMap.addAttribute("height", height <= 0? 1: height);

//
Camera camera = cameraService.getById(report == null? 0L: report.getCameraId());
modelMap.addAttribute("camera", camera);

//
Algorithm algorithm = algorithmService.getById(report == null? 0L: report.getAlgorithmId());

//
if(report.getAlgorithmId() == 0L && report.getType() == ReportType.STREAM.getType()) {
algorithm = new Algorithm();
algorithm.setName(ReportType.STREAM.getText());
}
modelMap.addAttribute("algorithm", algorithm);

//
modelMap.addAttribute("webUrl", configService.getByValTag("webUrl"));

return"report/detail";
}

/**
*
* @param id
* @return
*/
@ApiOperation("Query Alert Detail Data")
@ApiImplicitParam(name ="id", value ="Data id")
@SaCheckPermission("apmgr-train")
@PostMapping({"/detail"})
@ResponseBody
public JsonResult detailInfo(Long id) {
Map<String, Object> retMap = new HashMap<>();

//
Report report = reportService.getById(id);
if(report!= null && report.getCreatedAt()!= null) {
report.setCreatedStr(DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
}
retMap.put("report", report);

//
int width = 1;
int height = 1;
try {
File file = new File(report.getFileName());
if (file.exists()) {
BufferedImage bi = ImgUtil.read(file);
width = bi.getWidth();
height = bi.getHeight();
}
} catch (Exception e) {
//
}
retMap.put("width", width <= 0? 1: width);
retMap.put("height", height <= 0? 1: height);

//
Camera camera = cameraService.getById(report == null? 0L: report.getCameraId());
retMap.put("camera", camera);

//
Algorithm algorithm = algorithmService.getById(report == null? 0L: report.getAlgorithmId());

//
if(report.getAlgorithmId() == 0L && report.getType() == ReportType.STREAM.getType()) {
algorithm = new Algorithm();
algorithm.setName(ReportType.STREAM.getText());
}
retMap.put("algorithm", algorithm);

//
String rois ="";
String lines ="";
CameraAlgorithm cameraAlgorithm = cameraAlgorithmService.getByCameraAndAlgorithmId(report.getCameraId(), report.getAlgorithmId());
if(cameraAlgorithm!= null) {
if(cameraAlgorithm.getMarkPoints()!= null) {
rois = cameraAlgorithm.getMarkPoints();
}
if(cameraAlgorithm.getLineMarkPoints()!= null) {
rois = cameraAlgorithm.getLineMarkPoints();
}
}
retMap.put("rois", rois);
retMap.put("lines", lines);

return JsonResultUtils.success(retMap);
}

/**
* For External Access API
* @param id
* @return
*/
@SaIgnore
@ApiOperation("Query Alert Detail Data")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="id", value ="Alarm id"),
@ApiImplicitParam(name ="key", value ="Access key"),
@ApiImplicitParam(name ="t", value ="Timestamp")
})
@GetMapping({"/ext/detail"})
public String extDetail(Long id, String key, @RequestParam("t") Long timestamp, ModelMap modelMap) {
boolean ok = ExternalAccessUtils.decrypt(id, timestamp, key);
if(!ok) {
log.info("External Access Alarm Detail, Validation failed");
return"report/detail";
}

Report report = reportService.getById(id);
if(report == null) {
return"report/detail";
}

if(report.getCreatedAt()!= null) {
report.setCreatedStr(DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
}
modelMap.addAttribute("report", report);

//
int width = 1;
int height = 1;
try {
File file = new File(report.getFileName());
if (file.exists()) {
BufferedImage bi = ImgUtil.read(file);
width = bi.getWidth();
height = bi.getHeight();
}
} catch (Exception e) {
//
}
modelMap.addAttribute("width", width <= 0? 1: width);
modelMap.addAttribute("height", height <= 0? 1: height);

//
Camera camera = cameraService.getById(report.getCameraId());
modelMap.addAttribute("camera", camera);

//
Algorithm algorithm = algorithmService.getById(report.getAlgorithmId());

//
if(report.getAlgorithmId() == 0L && Objects.equals(report.getType(), ReportType.STREAM.getType())) {
algorithm = new Algorithm();
algorithm.setName(ReportType.STREAM.getText());
}
modelMap.addAttribute("algorithm", algorithm);

//
modelMap.addAttribute("webUrl", configService.getByValTag("webUrl"));

//
modelMap.addAttribute("timestamp", timestamp);
modelMap.addAttribute("key", key);

return"report/detail";
}

/**
*
* @param id
* @param response
* @throws Exception
*/
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@GetMapping({"/stream"})
public void getImageAsByteArray(@RequestParam(defaultValue ="0") Long id, HttpServletResponse response) {
Report report = reportService.getById(id);
if(report!= null && StrUtil.isNotBlank(report.getFileName())) {
//
File file = new File(report.getFileName());
if(!file.exists()) {
// System.out.println("Read Alert Image Exception: File does not exist");
return;
}
//
try (FileInputStream fis = new FileInputStream(file)) {
response.setContentType("image/jpeg");
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
} catch (Exception e) {
// System.out.println("Read Alert Image Exception:"+ e.getMessage());
}
}
}

/**
* check View Alarm Image
* @param id
* @param response
* @throws Exception
*/
@SaIgnore
@ApiOperation("Query Alarm Image")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="id", value ="Alarm id"),
@ApiImplicitParam(name ="key", value ="Access key"),
@ApiImplicitParam(name ="t", value ="Timestamp")
})
@GetMapping({"/ext/stream"})
public void extImage(@RequestParam(defaultValue ="0") Long id, String key, @RequestParam("t") Long timestamp, HttpServletResponse response) {
boolean ok = ExternalAccessUtils.decrypt(id, timestamp, key);
if(!ok) {
return;
}

Report report = reportService.getById(id);
if(report!= null && StrUtil.isNotBlank(report.getFileName())) {
File file = new File(report.getFileName());
if(!file.exists()) {
//System.out.println("Read Alert Image Exception: File does not exist");
return;
}
//
try (FileInputStream fis = new FileInputStream(file)) {
response.setContentType("image/jpeg");
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
} catch (Exception e) {
// System.out.println("Read Alert Image Exception:"+ e.getMessage());
}
}
}

/**
* Get Image Stream Zoom Slightly image
* @author Abyss
* @date 2023/12/5 16:30
* @param id
*/
@ApiIgnore
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@GetMapping({"/streamThumb"})
public void getImageThumbAsByteArray(@RequestParam(defaultValue ="0") Long id, HttpServletResponse response) {
Report report = reportService.getById(id);
if(report!= null && StrUtil.isNotBlank(report.getFileName())) {
File file = new File(report.getFileName());
if(!file.exists()) {
return;
}
try {
byte[] bytes = ImageUtils.compress2Byte(file, 0.5);
response.setContentType("image/jpeg");
response.getOutputStream().write(bytes);
response.getOutputStream().flush();
response.getOutputStream().close();
} catch (Exception e) {
// System.out.println("Read Alert Image Exception:"+ e.getMessage());
}
}
}

/**
*
* @param ids
* @return
*/
@ApiOperation("Batch Delete Data")
@ApiImplicitParam(name ="ids", value ="Data ids", example ="1,2,3,4")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/batchRemove"})
@ResponseBody
public JsonResult batchRemove(String ids) {
if(StrUtil.isBlank(ids)) {
return JsonResultUtils.fail("not has select in Data");
}

String[] idArr = ids.split(",");
if(idArr == null || idArr.length == 0) {
return JsonResultUtils.fail("not has select in Data");
}

for(String id: idArr) {
try {
Long idLng = Long.parseLong(id);
reportService.updateDisplay(idLng, 1); // not Display
} catch (Exception e) {
//
}
}


return JsonResultUtils.success();
}

/**
*
* @param page
* @param limit
* @param cameraId
* @param algorithmId
* @param auditState
* @return
*/
@ApiOperation("Query Review Data List")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size"),
@ApiImplicitParam(name ="cameraId", value ="Camera id"),
@ApiImplicitParam(name ="algorithmId", value ="Algorithm id"),
@ApiImplicitParam(name ="auditState", value ="Review Status (0,1)"),
@ApiImplicitParam(name ="auditResult", value ="Review Result Status (0,1,2)"),
})
@SaCheckPermission("XXXXXXX")
@PostMapping({"/auditListPage"})
@ResponseBody
public PageResult auditListPage(@RequestParam(defaultValue ="1") Integer page, @RequestParam(defaultValue ="10") Integer limit, Long cameraId, Long algorithmId, Integer auditState, Integer auditResult) {
Report reportQuery = new Report();
reportQuery.setCameraId(cameraId);
reportQuery.setAlgorithmId(algorithmId);
reportQuery.setType(0);
reportQuery.setAuditState(auditState);
reportQuery.setAuditResult(auditResult);

//
IPage<Report> pageResult = this.reportService.listPage(new Page<>(page, limit), reportQuery);
List<Report> reportList = pageResult.getRecords();
if (reportList == null) {
reportList = new ArrayList<>();
}
Map<Long, String> cameraNames = new HashMap<>();
List<Camera> cameraList = this.cameraService.list();
if (cameraList!= null) {
for (Camera camera: cameraList) {
cameraNames.put(camera.getId(), camera.getName());
}
}
Map<Long, String> algorithmNames = new HashMap<>();
List<Algorithm> algorithmList = this.algorithmService.list();
if (algorithmList!= null) {
for (Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());
}
}
for (Report report: reportList) {
String cameraName = cameraNames.get(report.getCameraId());
String algorithmName = algorithmNames.get(report.getAlgorithmId());
report.setCameraName(cameraName == null?"": cameraName);
report.setAlgorithmName(algorithmName == null?"": algorithmName);
report.setCreatedStr(report.getCreatedAt()!= null? DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"):"");
report.setTypeName("Monitor Alert");
try {
JSONArray root = JSON.parseArray(report.getParams());
JSONObject first = root.getJSONObject(0);
report.setConf(first.getString("confidence"));
} catch (Exception e) {
report.setConf("Error");
}
}
return PageResultUtils.success(pageResult.getTotal(), reportList);
}

/**
* Review
* @param id
* @param result
* @return
*/
@SaCheckPermission("XXXXXXX")
@PostMapping("/audit")
@ResponseBody
public JsonResult doAudit(Long id, Integer result) {
reportService.updateAudit(id, result);
return JsonResultUtils.success();
}

@ApiOperation("Alarm Data Export")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="cameraId", value ="Camera id"),
@ApiImplicitParam(name ="algorithmId", value ="Algorithm id"),
@ApiImplicitParam(name ="startDate", value ="Start Time (yyyy-MM-dd HH:mm:ss)"),
@ApiImplicitParam(name ="endDate", value ="End Time (yyyy-MM-dd HH:mm:ss)"),
@ApiImplicitParam(name ="state", value ="Review Status (1- Normal Alarm, 2- wrong Report Alarm)"),
@ApiImplicitParam(name ="markList", value ="Mark Status (0- Pending Fixed, 1- Normal Alarm, 2- wrong Report Alarm)")
})
@SaCheckPermission(value = {"alarm-export-image"}, mode = SaMode.OR)
@GetMapping("/export")
public void export(Long algorithmId, Long cameraId, String startDate, String endDate, Integer state, @RequestParam(required = false) List<Integer> markList, HttpServletResponse response) throws Exception {
long startMills = 0L, endMills = 0L; // Start Time and End Time ms Value
if(StrUtil.isBlank(startDate) || StrUtil.isBlank(endDate)) {
throw new BizException("Please select Start Time and End Time");
}
// Time Convert
try {
startMills = DateUtil.parse(startDate,"yyyy-MM-dd HH:mm:ss").getTime();
endMills = DateUtil.parse(endDate,"yyyy-MM-dd HH:mm:ss").getTime();
if (endMills <= startMills) {
throw new BizException("Start Time not Get big at End Time");
}
} catch (Exception e) {
throw new BizException("Start Time or End Time Format Error");
}
// Detection Export Root Directory Whether Exist
File tarRoot = new File(uploadDir);
if(!tarRoot.exists()) {
throw new BizException("Export Directory not Set");
}
// Create Export child Directory
File tarSub = new File(uploadDir + File.separator +"report_exports"+ File.separator);
if(!tarSub.exists()) {
tarSub.mkdirs();
}
// Query Export Data
List<Report> reportList = reportService.listExport(algorithmId, cameraId, startMills, endMills, state, markList);
if(reportList.isEmpty()) {
throw new BizException("not has Need Export Data");
}
// Create Export Directory
String randomString = DateUtil.format(new Date(),"yyyyMMdd_HHmmss") +"_"+ RandomUtil.randomString(4);
File tar = new File(tarSub + File.separator + randomString + File.separator);
if(!tar.exists()) {
tar.mkdirs();
}
// Copy Image File to Export Directory
for(Report report: reportList) {
try {
FileUtil.copy(new File(report.getFileName()), tar, true);
} catch (Exception e) {
//
}
}
// Type Package zip
File zipfile = ZipUtil.zip(tar, StandardCharsets.UTF_8);
// Delete File
FileUtil.del(tar);
// Execute Export
try (FileInputStream fis = new FileInputStream(zipfile)) {
String chName ="Alarm Data Export _ Download.zip";
response.setContentType("application/octet-stream");
response.setContentLengthLong(zipfile.length());
response.addHeader("Content-Disposition","attachment; filename="+ URLEncoder.encode(chName,"UTF-8"));
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
} catch (Exception e) {
throw new BizException("Export Hour send produce Error");
} finally {
FileUtil.del(zipfile);
}
}

@ApiOperation("Alarm Data Export")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="cameraId", value ="Camera id"),
@ApiImplicitParam(name ="algorithmId", value ="Algorithm id"),
@ApiImplicitParam(name ="exportType", value ="Export Type, 0- when Day,1- when Week,2- when Month, 3- Custom Date"),
@ApiImplicitParam(name ="startDate", value ="Start Time (yyyy-MM-dd HH:mm:ss)"),
@ApiImplicitParam(name ="endDate", value ="End Time (yyyy-MM-dd HH:mm:ss)"),
@ApiImplicitParam(name ="departIds", value ="belong belong Organization"),
@ApiImplicitParam(name ="state", value ="Review Status (1- Normal Alarm, 2- wrong Report Alarm)")
})
@SaCheckPermission(value = {"alarm-export-image"}, mode = SaMode.OR)
@GetMapping("/exportData")
public void exportData(Long algorithmId, Long cameraId, String startDate, String endDate, Integer state, Integer exportType, String departIds, HttpServletResponse response, @RequestParam(required = false) List<Integer> markList) throws Exception {
// Export Department Condition
List<Long> exportDepartIds = new ArrayList<>();
if(StrUtil.isNotBlank(departIds)) {
String[] sps = departIds.split(",");
try {
for(String sp: sps) {
exportDepartIds.add(Long.valueOf(sp));
}
} catch (Exception e) {
throw new BizException("Export Department Data Error");
}
}
//
// Determine Whether super Level Management member
List<Long> queryDepartIds = new ArrayList<>();
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null && account.getIsSuper() == 1) {
if(!exportDepartIds.isEmpty()) {
// List<Long> locationIdsByDeparts = locationService.getLocationIdsByDeparts(exportDepartIds);
// if(locationIdsByDeparts.isEmpty()) {
// throw new BizException("not has Phase close Data Need Export");
//}
// queryLocationIds.addAll(locationIdsByDeparts);
queryDepartIds.addAll(exportDepartIds);
}
} else {
Long departIdByAccount = account.getDepartId();
List<Long> departIdsByAccount = apDepartService.getCurrentAndChildIds(departIdByAccount); // By User belong belong Department Query Current Department and child Department
if(!exportDepartIds.isEmpty()) {
List<Long> includeDepartIds = new ArrayList<>();
for(Long departId: exportDepartIds) {
if(departIdsByAccount.contains(departId)) {
includeDepartIds.add(departId); // Determine Select Department Whether In User belong belong Department and child Department
}
}
//
if(includeDepartIds.isEmpty()) {
throw new BizException("not has Phase close Data Need Export");
}
//
// List<Long> locationIdsByDeparts = locationService.getLocationIdsByDeparts(includeDepartIds); // Query belong belong Department and child Department Corresponding Box
// if(locationIdsByDeparts.isEmpty()) {
// throw new BizException("not has Phase close Data Need Export");
//}
// queryLocationIds.addAll(locationIdsByDeparts);
queryDepartIds.addAll(includeDepartIds);
} else {
// List<Long> locationIdsByDeparts = locationService.getLocationIdsByDeparts(departIdsByAccount); // Query belong belong Department and child Department Corresponding Box
// if(locationIdsByDeparts.isEmpty()) {
// throw new BizException("not has Phase close Data Need Export");
//}
// queryLocationIds.addAll(locationIdsByDeparts);
queryDepartIds.addAll(departIdsByAccount);
}
}

// Default Export Type
if(exportType == null) {
exportType = 0; // Default Export when Day Data
}
//
long startMills = 0L, endMills = 0L; // Start Time and End Time ms Value
if(exportType == 0) {// when Day
startMills = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
endMills = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();
} else if(exportType == 1) {// when Week
DateTime beginOfWeek = DateUtil.beginOfWeek(new Date());
startMills = DateUtil.truncate(beginOfWeek, DateField.DAY_OF_MONTH).getTime();
endMills = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();
} else if(exportType == 2) {// when Month
DateTime beginOfMonth = DateUtil.beginOfMonth(new Date());
startMills = DateUtil.truncate(beginOfMonth, DateField.DAY_OF_MONTH).getTime();
endMills = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();
} else if(exportType == 3) {// Custom Time
if(StrUtil.isBlank(startDate) || StrUtil.isBlank(endDate)) {
throw new BizException("Please select Start Time and End Time");
}
//
startMills = DateUtil.parse(startDate,"yyyy-MM-dd HH:mm:ss").getTime();
endMills = DateUtil.parse(endDate,"yyyy-MM-dd HH:mm:ss").getTime();
if (endMills <= startMills) {
throw new BizException("Start Time not Get big at End Time");
}
} else {
throw new BizException("Export Type Error");
}
// Query Export Data
List<Report> reportList = reportService.listExportByDeparts(algorithmId, cameraId, startMills, endMills, state, queryDepartIds, markList);
if(reportList.isEmpty()) {
throw new BizException("not has Need Export Data");
}
//
List<Camera> cameraList = cameraService.listData();
Map<Long, String> cameraMap = cameraList.stream().collect(Collectors.toMap(Camera::getId, Camera::getName, (key1, key2) -> key1));
//
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}
Map<Long, String> algorithmMap = algorithmList.stream().collect(Collectors.toMap(Algorithm::getId, Algorithm::getName, (key1, key2) -> key1));
//
List<Location> locationList = locationService.listByType("2");
Map<Long, Location> locationMap = locationList.stream().collect(Collectors.toMap(Location::getId, obj -> obj));
//
List<Depart> departList = apDepartService.listData();
Map<Long, String> departMap = departList.stream().collect(Collectors.toMap(Depart::getId, Depart::getName, (key1, key2) -> key1));
//
List<ReportExportDataVo> dataVos = new ArrayList<>();
for(Report report: reportList) {
Long _cameraId = report.getCameraId();
Long _algorithmId = report.getAlgorithmId();
Long _departId = report.getDepartId();
Long _boxId = report.getBoxId();

// Place set Status
String auditResult ="-";
if(report.getAuditResult() == null || report.getAuditResult() == 0) {
auditResult ="Pending Place set";
} else if(report.getAuditResult() == 1) {
auditResult ="Place set";
} else if(report.getAuditResult() == 2) {
auditResult ="Closed";
} else if(report.getAuditResult() == 3) {
auditResult ="self Dynamic Place set";
}

// Place set Time & Place set Hour long
String auditAt ="-";
String auditTimeLen ="-";
if(report.getAuditResult()!= null && (report.getAuditResult() == 1 || report.getAuditResult() == 2 || report.getAuditResult() == 3)) {
if(report.getAuditAt()!= null) {
auditAt = DateUtil.format(report.getAuditAt(),"yyyy-MM-dd HH:mm:ss");

auditTimeLen = formatDuration(report.getCreatedAt(), report.getAuditAt());
}
}

ReportExportDataVo dataVo = new ReportExportDataVo();
dataVo.setCameraName(cameraMap.get(_cameraId) == null?"None": cameraMap.get(_cameraId));
dataVo.setAlgorithmName(algorithmMap.get(_algorithmId) == null?"None": algorithmMap.get(_algorithmId));
dataVo.setDepartName(departMap.get(_departId) == null?"None": apDepartService.getLinkName(_departId, departList));
dataVo.setBoxSn(locationMap.get(_boxId) == null?"None": (locationMap.get(_boxId).getBoxNo() == null?"None": locationMap.get(_boxId).getBoxNo()));
dataVo.setBoxIp(locationMap.get(_boxId) == null?"None": (locationMap.get(_boxId).getIpAddr() == null?"None": locationMap.get(_boxId).getIpAddr()));
dataVo.setFileName(report.getFileName());
dataVo.setParams(report.getParams());
dataVo.setCreatedAt(report.getCreatedAt() == null?"None": DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
dataVo.setAuditResult(auditResult);
dataVo.setAuditAt(auditAt);
dataVo.setAuditTimeLen(auditTimeLen);
Integer mark = report.getMark();
if(ObjectUtil.equals(0, mark)){
dataVo.setMark("Pending Fixed");
}else if(ObjectUtil.equals(1, mark)){
dataVo.setMark("Correct Report");
}else{
dataVo.setMark("wrong Report");
}
dataVos.add(dataVo);
}

//
response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
response.setCharacterEncoding("utf-8");
// Here URLEncoder.encode can with Prevent in Text Messy code when and easyexcel not has close System
String fileName = URLEncoder.encode("Alarm Data Export","UTF-8").replaceAll("\\+","%20");
response.setHeader("Content-disposition","attachment;filename*=utf-8''"+ fileName +".xlsx");
EasyExcel.write(response.getOutputStream(), ReportExportDataVo.class).sheet("Alarm Data").doWrite(dataVos);
}

/**
* Manual Push
* @param id
* @return
*/
@ApiOperation("Manual Push Data")
@ApiImplicitParam(name ="id", value ="Data id")
@SaCheckPermission("XXXXXXX")
@PostMapping("/pushData")
@ResponseBody
public JsonResult pushData(Long id) {
// Push Third Party
String reportPushUrl = configService.getByValTag("reportPushUrl");
String reportPushImage = configService.getByValTag("reportPushImage");
if(StrUtil.isBlank(reportPushUrl)) {
return JsonResultUtils.fail("Push Address not Config");
}

//
Report report = reportService.getById(id);
if(report == null) {
return JsonResultUtils.fail("Alert Data Not Exist, or Deleted");
}

//
Camera camera = cameraService.getById(report.getCameraId());
if(camera == null) {
return JsonResultUtils.fail("Camera Data Not Exist, or Deleted");
}

//
Algorithm algorithm = algorithmService.getById(report.getAlgorithmId());
if(algorithm == null) {
return JsonResultUtils.fail("Algorithm Data Not Exist, or Deleted");
}

//
try {
JSONObject reportMap = new JSONObject();
reportMap.put("cmpn_cd","TLB");
reportMap.put("camera_id", String.valueOf(report.getCameraId()));
reportMap.put("camera_name", camera.getName());
reportMap.put("algorithm_id", String.valueOf(report.getAlgorithmId()));
reportMap.put("algorithm_name", algorithm.getName());
reportMap.put("level","F");
reportMap.put("img_path", report.getFileName());
reportMap.put("img_ext", FileUtil.extName(report.getFileName()));
reportMap.put("img_name", FileUtil.getName(report.getFileName()));
reportMap.put("alarm_dt", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
reportMap.put("report_id", String.valueOf(report.getId()));
reportMap.put("params", report.getParams());

// imageBase64 this Data Make Complete Config style, Because for Need Consume Consume Time
boolean toBase64 = false;
if (reportPushImage!= null &&"true".equals(reportPushImage)) {
toBase64 = true;
}

reportPushService.request(reportPushUrl, reportMap, toBase64, report.getFileName());
return JsonResultUtils.success();
} catch (Exception e) {
return JsonResultUtils.fail("Push Exception @"+ e.getMessage());
}
}

/**
*
* @return
*/
@SaCheckPermission("XXXXXXX")
@GetMapping("/test")
@ResponseBody
public JsonResult test() {
ReportMessage reportMessage = new ReportMessage();
reportMessage.setType("REPORT_SHOW");
reportMessage.setCameraId("1591683544965459969");
reportMessage.setAlgorithmId("1591683470285877249");
reportMessage.setParams("[{\"confidence\":0.52,\"position\":[1036,380,1228,544],\"type\":\"water\"}]");
reportMessage.setCameraName("test");
reportMessage.setAlgorithmName("test");
reportMessage.setAlarmTime(DateUtil.format(new Date(),"yyyy-MM-dd MM:ss"));
reportMessage.setWareName("test");
reportMessage.setId("1630107941640114178");
reportMessage.setWebUrl(configService.getByValTag("webUrl"));
reportWebsocket.sendToAll(JSON.toJSONString(reportMessage));
//app Push
appPushController.sendReportToAll(reportMessage);

return JsonResultUtils.success();
}

/**
* Card List Data
* @return
*/
@ApiOperation("Query Alert Data List")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size"),
@ApiImplicitParam(name ="cameraId", value ="Camera id"),
@ApiImplicitParam(name ="algorithmId", value ="Algorithm id"),
@ApiImplicitParam(name ="startDate", value ="Start Time (yyyy-MM-dd)"),
@ApiImplicitParam(name ="endDate", value ="End Time (yyyy-MM-dd)"),
})
@SaCheckPermission("XXXXXXX")
@GetMapping(value ="/list_card_data")
@ResponseBody
public PageResult listCardData(@RequestParam(defaultValue ="1") Integer page, @RequestParam(defaultValue ="8") Integer limit, Long cameraId, Long algorithmId, String startDate, String endDate, String startDate1, String endDate1) {
//
Long startMills = null;
Long endMills = null;
if(StrUtil.isBlank(startDate) && StrUtil.isBlank(endDate)) {
try {
if(StrUtil.isNotBlank(startDate1)) {
startMills = DateUtil.parse(startDate1,"yyyy-MM-dd").getTime();
}
if(StrUtil.isNotBlank(endDate1)) {
Date date = DateUtil.parse(endDate1,"yyyy-MM-dd");
endMills = DateUtil.offsetDay(date, 1).getTime();
}
} catch (Exception e) {
//
}
} else {
try {
if (StrUtil.isNotBlank(startDate)) {
startMills = DateUtil.parse(startDate,"yyyy-MM-dd").getTime();
}
if (StrUtil.isNotBlank(endDate)) {
Date date = DateUtil.parse(endDate,"yyyy-MM-dd");
endMills = DateUtil.offsetDay(date, 1).getTime();
}
} catch (Exception e) {
//
}
}

//
IPage<Report> pageResult = this.reportService.listByPage(new Page<>(page, limit), cameraId, algorithmId, null, startMills, endMills, null, new ArrayList<>(), 0, null, null);
List<Report> reportList = pageResult.getRecords();
if (reportList == null) {
reportList = new ArrayList<>();
}
Map<Long, String> cameraNames = new HashMap<>();
List<Camera> cameraList = this.cameraService.list();
if (cameraList!= null) {
for (Camera camera: cameraList) {
cameraNames.put(camera.getId(), camera.getName());
}
}
Map<Long, String> algorithmNames = new HashMap<>();
List<Algorithm> algorithmList = this.algorithmService.list();
if (algorithmList!= null) {
for (Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());
}
}

//
List<Map<String, Object>> dataList = new ArrayList<>();
for (Report report: reportList) {
//
String cameraName = cameraNames.get(report.getCameraId());
String algorithmName = algorithmNames.get(report.getAlgorithmId());

// Region Bit set Name
String wareName ="-";
// Camera camera = cameraService.getById(report.getCameraId());

//
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("id", report.getId());
dataMap.put("params", report.getParams());
dataMap.put("cameraName","The belong Camera:"+ cameraName);
dataMap.put("algorithmName","Alert Type:"+ algorithmName);
dataMap.put("wareName","Region Name:"+ wareName);
dataMap.put("image","/report/stream?id="+ report.getId());
dataMap.put("alarmTime","Alert Time:"+ report.getCreatedAt()!= null? DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"):"");
dataList.add(dataMap);
}
return PageResultUtils.success(pageResult.getTotal(), dataList);
}

/**
* Query wsUrl
* @return
*/
@ApiOperation("Query wsUrl")
@SaCheckPermission("XXXXXXX")
@PostMapping(value ="/wsUrl")
@ResponseBody
public JsonResult<String> getWsUrl() {
String wsUrl = configService.getByValTag("wsUrl");
return JsonResultUtils.success(wsUrl +"/report/"+ StpUtil.getTokenValue());
}

/**
*
* @param page
* @param limit
* @return
*/
@ApiOperation("H5 Query Alert Data List")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/listOperationPage"})
@ResponseBody
public PageResult listOperationPage(@RequestParam(defaultValue ="1") Integer page, @RequestParam(defaultValue ="10") Integer limit) {
Report reportQuery = new Report();
reportQuery.setAuditState(0);
IPage<Report> pageResult = this.reportService.listPage(new Page<>(page, limit), reportQuery);
List<Report> reportList = pageResult.getRecords();
if (reportList == null) {
reportList = new ArrayList<>();
}
Map<Long, String> cameraNames = new HashMap<>();
List<Camera> cameraList = this.cameraService.list();
if (cameraList!= null) {
for (Camera camera: cameraList) {
cameraNames.put(camera.getId(), camera.getName());
}
}
Map<Long, String> algorithmNames = new HashMap<>();
List<Algorithm> algorithmList = this.algorithmService.list();
if (algorithmList!= null) {
for (Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());
}
}
for (Report report: reportList) {
String cameraName = cameraNames.get(report.getCameraId());
String algorithmName = algorithmNames.get(report.getAlgorithmId());
report.setCameraName(cameraName == null?"": cameraName);
report.setAlgorithmName(algorithmName == null?"": algorithmName);
report.setCreatedStr(report.getCreatedAt()!= null? DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"):"");
}
return PageResultUtils.success(pageResult.getTotal(), reportList);
}

/**
* Process Alarm - Protect Safe Process
* @param id
* @return
* @throws Exception
*/
@ApiOperation("Alarm Process")
@SaCheckPermission("XXXXXXX")
@PostMapping("/processed")
@ResponseBody
public JsonResult processed(Long id) throws Exception {
Report report = reportService.getById(id);
if(id == null) {
return JsonResultUtils.fail("Data Not Exist or Deleted");
}
//
if(report.getAuditState()!= null && report.getAuditState() > 0) {
return JsonResultUtils.fail("Alert Process, not Need re reply Process");
}
//
Report updateReport = new Report();
updateReport.setId(report.getId());
updateReport.setAuditState(1);
updateReport.setAuditResult(1);
reportService.updateById(updateReport);
return JsonResultUtils.success();
}

/**
* Process Alarm - wrong Report
* @param id
* @return
* @throws Exception
*/
@ApiOperation("wrong Report")
@SaCheckPermission("XXXXXXX")
@PostMapping("/misreport")
@ResponseBody
public JsonResult misreport(Long id) throws Exception {
Report report = reportService.getById(id);
if(id == null) {
return JsonResultUtils.fail("Data Not Exist or Deleted");
}
//
if(report.getAuditState()!= null && report.getAuditState() > 0) {
return JsonResultUtils.fail("Alert Process, not Need re reply Process");
}
//
Report updateReport = new Report();
updateReport.setId(report.getId());
updateReport.setAuditState(1);
updateReport.setAuditResult(2);
reportService.updateById(updateReport);
return JsonResultUtils.success();
}

@SaCheckPermission("XXXXXXX")
@GetMapping("/video/stream")
public void videoStream(Long reportId, HttpServletResponse response) {
try {
//
Report report = reportService.getById(reportId);
if(report == null || StrUtil.isBlank(report.getVideoPath())) {
return;
}
//
File videoFile = new File(report.getVideoPath());
if(!videoFile.exists()) {
return;
}
//
BufferedInputStream in = new BufferedInputStream(new FileInputStream(videoFile));
response.setContentType("video/mp4");
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
//e.printStackTrace();
}
}

@ApiOperation("Query tab table Head")
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping("/listTabs")
@ResponseBody
public JsonResult<List<Map<String, Object>>> listTabs(String startDate, String endDate, @RequestHeader("Lang") String language) {
long startMills = 0L, endMills = 0L; // Start Time and End Time ms Value
if(StrUtil.isBlank(startDate) && StrUtil.isBlank(endDate)) {// Only Query when day
return JsonResultUtils.fail("Please select Start Time and End Time");
} else if(StrUtil.isNotBlank(startDate) && StrUtil.isBlank(endDate)) {// Only Query with Start Log for when day
return JsonResultUtils.fail("Please select Start Time");
} else if(StrUtil.isNotBlank(startDate) && StrUtil.isBlank(endDate)) {// Only Query with End Log for when day
return JsonResultUtils.fail("Please select End Time");
} else {// with Start Date and End Date Query
Date date1 = DateUtil.parse(startDate,"yyyy-MM-dd HH:mm");
startMills = date1.getTime();
Date date2 = DateUtil.parse(endDate,"yyyy-MM-dd HH:mm");
endMills = date2.getTime();
if(startMills >= endMills) {
return JsonResultUtils.fail("Start Time not Get big at End Date");
}
}

// Query Algorithm List
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}

// Return Data
List<Map<String, Object>> retData = new ArrayList<>();

// by Algorithm id Group, Count Count
List<Map<String, Object>> datas = reportService.countAlgorithmGroupBy(startMills, endMills);
if(datas == null || datas.isEmpty()) {// Back All Algorithm Label
/*
for(Algorithm algorithm: algorithmList) {
Map<String, Object> ret = new HashMap<>();
ret.put("id", algorithm.getId());
ret.put("name", algorithm.getName());
ret.put("nameStr", algorithm.getName() +"(0)");
ret.put("count", 0);
retData.add(ret);
}
*/
return JsonResultUtils.success(retData);
}

// By Count Sort
Collections.sort(datas, new Comparator<Map<String, Object>>() {
@Override
public int compare(Map<String, Object> o1, Map<String, Object> o2) {
Long cnt1 = (Long) o1.get("cnt");
Long cnt2 = (Long) o2.get("cnt");
return Long.valueOf(cnt2 - cnt1).intValue();
}
});



// false set I They need will key for"key2"Map Move to No One Bit set
String targetKey ="1926874795980460033";
Map<String, Object> targetElement = null;
for(Map<String, Object> data: datas) {
String algorithmId = data.get("algorithm_id").toString();
if (StringUtils.equals(algorithmId, targetKey)) {
targetElement = data;
}
}
// like Result find to Target Element
if (targetElement!= null) {
// Remove Target Element
datas.remove(targetElement);
// Insert in to No One Bit set
datas.add(0, targetElement);
}
//
Map<Long, String> algorithmMap = algorithmList.stream().collect(Collectors.toMap(Algorithm::getId, Algorithm::getName, (s1, s2) -> s1));
Map<Long, String> algorithmEnglishMap = algorithmList.stream().filter(s -> StrUtil.isNotBlank(s.getEnglishName())).collect(Collectors.toMap(Algorithm::getId, Algorithm::getEnglishName, (s1, s2) -> s1));
for(Map<String, Object> data: datas) {
Long algorithmId = (Long) data.get("algorithm_id");
Long cnt = (Long) data.get("cnt");
String algorithmName = algorithmMap.get(algorithmId);
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
algorithmName = algorithmEnglishMap.get(algorithmId);
}
if(StrUtil.isBlank(algorithmName)) {// find not to Algorithm Name
continue;
}
Map<String, Object> ret = new HashMap<>();
ret.put("id", algorithmId);
ret.put("name", algorithmName);
ret.put("nameStr", algorithmName +"("+ cnt +")");
ret.put("count", cnt);
retData.add(ret);
}
return JsonResultUtils.success(retData);
}

@ApiIgnore("Query Data")
@SaCheckPermission("XXXXXXX")
@PostMapping("/delete")
@ResponseBody
public JsonResult<Boolean> delete(@RequestBody IdVo idVo) {
reportService.removeById(idVo.getId());
return JsonResultUtils.success(true);
}

/**
* Tree Hole Delete All Record and Image
* @author Abyss
* @date 2024/5/23 19:17
*/
@SaCheckPermission("XXXXXXX")
@GetMapping({"/clearReport"})
@ResponseBody
public JsonResult clearReport() {
List<Report> reportList = reportService.list();
for (Report report: reportList) {
FileUtil.del(report.getFileName());
reportService.removeById(report.getId());
}
return JsonResultUtils.success();
}

/**
* Export Collect Data
* @param exportVo
* @param response
* @return
*/
@SaCheckPermission("XXXXXXX")
@PostMapping("exportCollect")
public void exportCollect(@RequestBody ReportCollectExportVo exportVo, HttpServletResponse response) {
log.info("Collect Data Param {}", exportVo);
// Storage Directory
String dest = FileUtils.pathTo(uploadDir +"/"+"report_collect_exports"+"/"+ DateUtil.format(new Date(),"yyyyMMdd_HHmmss") +"_"+ RandomUtil.randomString(4) +"/");
FileUtil.mkdir(dest);

try {
List<Report> reports = reportService.listCollect(exportVo);
if (reports == null) {
reports = new ArrayList<>();
}

//
if (reports.isEmpty()) {
FileUtil.writeString("not has can Export Data", FileUtil.newFile(FileUtils.pathTo(dest +"/error.txt")), StandardCharsets.UTF_8);
} else {
for(Report report: reports) {
if(FileUtil.exist(report.getFileName())) {
FileUtil.copy(report.getFileName(), dest, true);
}
}
}
} catch (Exception e) {
log.error("Export Collect Image Exception", e);
FileUtil.writeString("Export Exception", FileUtil.newFile(FileUtils.pathTo(dest +"/error.txt")), StandardCharsets.UTF_8);
}

// Type Package zip
File zipfile = ZipUtil.zip(dest, StandardCharsets.UTF_8);

// Delete File
FileUtil.del(dest);

// Execute Export
try (FileInputStream fis = new FileInputStream(zipfile)) {
String chName ="Alarm Data Export _ Download.zip";
response.setContentType("application/octet-stream");
response.setContentLengthLong(zipfile.length());
response.addHeader("Content-Disposition","attachment; filename="+ URLEncoder.encode(chName,"UTF-8"));
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
} catch (Exception e) {
//
} finally {
FileUtil.del(zipfile);
}
}

/**
* Delete Collect Data
* @return
*/
@SaCheckPermission("XXXXXXX")
@PostMapping("removeCollect")
@ResponseBody
public JsonResult<?> removeCollect() {
reportService.removeCollect();
return JsonResultUtils.success();
}

/**
* Delete Collect Data
* @return
*/
@SaCheckPermission(value = {"algorithm-add","algorithm-edit"}, mode = SaMode.OR)
@GetMapping("getCollectCount")
@ResponseBody
public JsonResult<?> getCollectCount() {
Integer count = reportService.getCollectCount();
return JsonResultUtils.success(count);
}

/**
*
* @param cameraId
* @param algorithmId
* @return
*/
@SaCheckPermission("XXXXXXX")
@ApiOperation("Count Alert Data List")
@PostMapping({"/auditResultStatics"})
@ResponseBody
public JsonResult<?> auditResultStatics(Long cameraId, Long algorithmId, String startDate, String endDate) {
List<ReportAuditResultStaticsDTO> dataList = new ArrayList<>();

// Determine Whether super Level Management member
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null || account.getState() == null || account.getState()!= 0) {
dataList.add(ReportAuditResultStaticsDTO.builder().type("total").name("Total").count(0).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("un_audit").name("not Process Number").count(0).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("audited").name("Process Number").count(0).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("closed").name("Closed Number").count(0).build());
return JsonResultUtils.success(dataList);
}

//
List<Long> queryDepartIds = new ArrayList<>();
if(account.getIsSuper() == null || account.getIsSuper()!= 1) {// Non Management member
Long departIdByAccount = account.getDepartId();
List<Long> departIdsByAccount = apDepartService.getCurrentAndChildIds(departIdByAccount); // By User belong belong Department Query Current Department and child Department
if(departIdsByAccount == null || departIdsByAccount.isEmpty()) {
dataList.add(ReportAuditResultStaticsDTO.builder().type("total").name("Total").count(0).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("un_audit").name("not Process Number").count(0).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("audited").name("Process Number").count(0).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("closed").name("Closed Number").count(0).build());
return JsonResultUtils.success(dataList);
}
queryDepartIds.addAll(departIdsByAccount);

}

long startMills = DateUtil.parse(startDate,"yyyy-MM-dd HH:mm:ss").getTime();
long endMills = DateUtil.parse(endDate,"yyyy-MM-dd HH:mm:ss").getTime();

//
int total = this.reportService.getAuditResultStatics(cameraId, algorithmId, startMills, endMills, queryDepartIds, null);
int unAudit = this.reportService.getAuditResultStatics(cameraId, algorithmId, startMills, endMills, queryDepartIds, 0);
int audited = this.reportService.getAuditResultStatics(cameraId, algorithmId, startMills, endMills, queryDepartIds, 1);
int closed = this.reportService.getAuditResultStatics(cameraId, algorithmId, startMills, endMills, queryDepartIds, 2);

dataList.add(ReportAuditResultStaticsDTO.builder().type("total").name("Total").count(total).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("un_audit").name("not Process Number").count(unAudit).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("audited").name("Process Number").count(audited).build());
dataList.add(ReportAuditResultStaticsDTO.builder().type("closed").name("Closed Number").count(closed).build());
return JsonResultUtils.success(dataList);
}

/**
* Get Review and Push Manual Process Status
* @param id
* @return
*/
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping("getReportMust")
@ResponseBody
public JsonResult<?> getReportMust(Long id) {
if(id == null) {
return JsonResultUtils.success(new ReportMustDTO(false, false));
}

Report report = reportService.getById(id);
if(report == null) {
return JsonResultUtils.success(new ReportMustDTO(false, false));
}

// Push Address
// String reportPushUrl = configService.getByValTag("reportPushUrl");

// only need not Push, just Display must need Push
boolean mustPush = false;
// CameraAlgorithm cameraAlgorithm = cameraAlgorithmService.getByCameraAndAlgorithmId(report.getCameraId(), report.getAlgorithmId());
// if(cameraAlgorithm!= null && (algorithm.getPushEnable() == null || algorithm.getPushEnable() == 1)) {// Manual Push
// if(report.getPushed() == null || report.getPushed() == 0) {// still not has Push
// mustPush = true;
//}
//}

if(report.getPushed() == null || report.getPushed() == 0 || report.getPushed() == 2) {// still not has Push or Push Failed
mustPush = true;
}

//
ReportMustDTO reportMustDTO = new ReportMustDTO();

// Whether Need Manual Review Process
reportMustDTO.setMustAudit((report.getAuditResult() == null || report.getAuditResult() == 0));

// Whether Need Push
//reportMustDTO.setMustPush(StrUtil.isNotBlank(reportPushUrl) && (report.getPushed() == null || report.getPushed() == 0));
reportMustDTO.setMustPush(mustPush);

return JsonResultUtils.success(reportMustDTO);
}

/**
* Close Alarm
* @param id
* @return
*/
@SaCheckPermission("report-audit")
@PostMapping("saveAuditClose")
@ResponseBody
public JsonResult<?> saveAuditClose(Long id, String auditRemark) {
if(id == null) {
return JsonResultUtils.fail("Alarm ID Param Error");
}

Report report = reportService.getById(id);
if(report == null) {
return JsonResultUtils.fail("Alarm Data find not to");
}

if(report.getAuditResult()!= null && report.getAuditResult()!= 0) {
return JsonResultUtils.fail("Alarm Data Process");
}

// Close Alarm
this.handleAudit(report, 2, auditRemark);
return JsonResultUtils.success();
}

/**
* Push Alarm
* @param id
* @return
*/
@SaCheckPermission("report-audit")
@PostMapping("saveDataPush")
@ResponseBody
public JsonResult<?> saveDataPush(Long id) {
if(id == null) {
return JsonResultUtils.fail("Alarm ID Param Error");
}

Report report = reportService.getById(id);
if(report == null) {
return JsonResultUtils.fail("Alarm Data find not to");
}

if(report.getPushed()!= null && report.getPushed() == 1) {
return JsonResultUtils.fail("Alarm Data Push");
}

return handlePush(report);
}

/**
* Process Alarm
* @param id
* @return
*/
@SaCheckPermission("report-audit")
@PostMapping("saveAuditOk")
@ResponseBody
public JsonResult<?> saveAuditOk(Long id, String auditRemark) {
if(id == null) {
return JsonResultUtils.fail("Alarm ID Param Error");
}

Report report = reportService.getById(id);
if(report == null) {
return JsonResultUtils.fail("Alarm Data find not to");
}

if(report.getAuditResult()!= null && report.getAuditResult()!= 0) {
return JsonResultUtils.fail("Alarm Data Process");
}

handleAudit(report, 1, auditRemark);
return JsonResultUtils.success();
}

/**
* Process Alarm and Push
* @param id
* @return
*/
@SaCheckPermission("report-audit")
@PostMapping("saveAuditAndPush")
@ResponseBody
public JsonResult<?> saveAuditAndPush(Long id) {
if(id == null) {
return JsonResultUtils.fail("Alarm ID Param Error");
}

Report report = reportService.getById(id);
if(report == null) {
return JsonResultUtils.fail("Alarm Data find not to");
}

// Review
if(report.getAuditResult() == null || report.getAuditResult() == 0) {
this.handleAudit(report, 1,"no");
}

// Push
if(report.getPushed() == null || report.getPushed() == 0) {
JsonResult<?> result = this.handlePush(report);
if(result.getCode()!= 0) {
return result;
}
}
return JsonResultUtils.success();
}

// Process Review
private void handleAudit(Report report, int auditResult, String auditRemark) {
long seconds = DateUtil.between(report.getCreatedAt(), new Date(), DateUnit.SECOND);
if(seconds < 0) {
seconds = 0;
}
Date createdAt = report.getCreatedAt();
Date date = new Date();
if(createdAt.after(date)){
int randomSeconds = 5 + new java.util.Random().nextInt(11); // Generate 5-15 Random Number
date = DateUtil.offsetSecond(createdAt, randomSeconds);
}

Report modifyReport = new Report();
modifyReport.setId(report.getId());
modifyReport.setAuditResult(auditResult); // 1- Process Alarm,2- Close Alarm
modifyReport.setAuditAt(date);
modifyReport.setAuditRemark(auditRemark);
modifyReport.setHandleTime(seconds);
reportService.updateById(modifyReport);

// Create One Count Update Record
reportSummaryTaskService.addData(report.getCreatedAt());
}

// Push Alarm Data to Third Party
private JsonResult<?> handlePush(Report report) {
//
// String reportPushUrl = configService.getByValTag("reportPushUrl");
// if(StrUtil.isBlank(reportPushUrl)) {
// return JsonResultUtils.fail("not has Config Third Party Alarm Push Address");
//}

//
try {
Camera camera = cameraService.getById(report.getCameraId());
if(camera == null) {
return JsonResultUtils.fail("Camera not find to or Deleted");
}

Algorithm algorithm = algorithmService.getById(report.getAlgorithmId());
if(algorithm == null) {
return JsonResultUtils.fail("Algorithm not find to or Deleted");
}

Location location = locationService.getById(camera.getLocationId());
if(location == null) {
return JsonResultUtils.fail("Box not find to or Deleted");
}

// Push to Feishu
AlarmPushResult alarmPushResult = alarmPushServcie.sendSync(camera, algorithm, report);
if(alarmPushResult.isSuccess()) {
return JsonResultUtils.success();
} else {
return JsonResultUtils.fail(alarmPushResult.getError());
}


// // Send Alarm
// CompletableFuture<AlarmPushResultDTO> future = alarmThirdPushService.sendCommData(camera, algorithm, location, report);
// AlarmPushResultDTO alarmPushResultDTO = future.join();
// if(alarmPushResultDTO.isSuccess()) {
// Report modifyReport = new Report();
// modifyReport.setId(report.getId());
// modifyReport.setPushed(1);
// reportService.updateById(modifyReport);
// return JsonResultUtils.success();
//}
// return JsonResultUtils.fail(alarmPushResultDTO.getMsg());
} catch (Exception e) {
log.error("Push Common Algorithm Alarm to Third Party Exception: {}", e.getMessage());
}
return JsonResultUtils.fail("Push Process Exception, Please Again Try Try");
}

/**
* Process Alarm - wrong Report
*/
@SaCheckPermission("XXXXXX")
@GetMapping("/mis")
public String mis(@RequestParam(defaultValue ="1") Integer page, @RequestParam(defaultValue ="10") Integer limit, String key, String code, ModelMap modelMap) throws Exception {
modelMap.addAttribute("key", key == null?"": key);
modelMap.addAttribute("code", code == null?"": code);

if(!projectConfig.getAlarmDropKey().equals(key)) {
modelMap.addAttribute("datas", new ArrayList<>());
modelMap.addAttribute("pages", new ArrayList<>());
return"report/mis";
}

Long algorithmId = null;
Algorithm algorithm = algorithmService.getByNameEn(code);
if(algorithm!= null) {
algorithmId = algorithm.getId();
}

IPage<Report> reports = reportService.listMisData(algorithmId, page, limit);
List<Report> reportList = reports.getRecords();
if(reportList == null) {
reportList = new ArrayList<>();
}
modelMap.addAttribute("datas", reportList);


long total = reports.getTotal();
long y = total % limit;

long p = total / limit + (y > 0? 1: 0);

List<Long> pages = new ArrayList<>();
for(long i = 1; i <= p; i++) {
pages.add(i);
}
modelMap.addAttribute("pages", pages);
return"report/mis";
}

@SaCheckPermission("XXXXXX")
@GetMapping("/misok")
@ResponseBody
public String misok(Long id, String key) {
if(!projectConfig.getAlarmDropKey().equals(key)) {
return"Fail";
}

reportService.removeById(id);
return"OK";
}


/**
* Get Temp near One Record
* @return
*/
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping("nearly")
@ResponseBody
public JsonResult<Long> getNearly(@RequestBody ReportNearlyVo nearlyVo) {
if(nearlyVo.getId() == null) {
return JsonResultUtils.success(0L);
}

Report report = reportService.getNearlyRecord(nearlyVo);
if(report == null) {
return JsonResultUtils.success(0L);
}
return JsonResultUtils.success(report.getId());
}

/**
* Get front One Record
* @param id
* @return
*/
@SaCheckPermission(value = {"alarmManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@GetMapping("info")
@ResponseBody
public JsonResult<?> info(Long id, @RequestHeader("Lang") String language) {
if(id == null) {
return JsonResultUtils.success(new Report());
}

Report report = reportService.getById(id);
if(report == null) {
return JsonResultUtils.success(new Report());
}

Camera camera = cameraService.getById(report.getCameraId());
Algorithm algorithm = algorithmService.getById(report.getAlgorithmId());
AlarmLevel alarmLevel = algorithm == null? null: alarmLevelService.getById(report.getAlgorithmId());

report.setCameraName((camera == null || camera.getName() == null)?"": camera.getName());
report.setAlgorithmName((algorithm == null || algorithm.getName() == null)?"": algorithm.getName());
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
report.setAlgorithmName((algorithm == null || algorithm.getEnglishName() == null)?"": algorithm.getEnglishName());
}
report.setCreatedStr(report.getCreatedAt()!= null? DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"):"");
report.setAlarmLevel(alarmLevel);

// Whether Need Manual Review Process
report.setMustAudit((report.getAuditResult() == null || report.getAuditResult() == 0));

// Whether Need Push
String reportPushUrl = configService.getByValTag("reportPushUrl");
report.setMustPush(StrUtil.isNotBlank(reportPushUrl) && (report.getPushed() == null || report.getPushed() == 0));

// Calculate Process Hour long
report.setAuditTimeLen(formatDuration(report.getCreatedAt(), report.getAuditAt()));

// Group Info
CameraGroupItem cameraGroupItem = cameraGroupItemService.getByCamera(report.getCameraId());
if(cameraGroupItem == null) {
report.setGroupNames("-");
} else {
List<CameraGroup> cameraGroupList = cameraGroupService.list();
String groupNames = cameraGroupService.getCurrentAndParentNames(cameraGroupItem.getGroupId(), cameraGroupList);
report.setGroupNames(groupNames);
}

// Box Name
Location location = locationService.getById(camera == null? 0L: camera.getLocationId());
report.setBoxName(location == null?"-": location.getName());

return JsonResultUtils.success(report);
}

/**
* Alert Data Count List
* @param statVo
* @return
*/
@SaCheckPermission("alarmData")
@PostMapping("stat/list")
@ResponseBody
public JsonResult<?> statList(@RequestBody ReportStatVo statVo) {
List<Location> locationList = locationService.list();
if(locationList == null) {
locationList = new ArrayList<>();
}

List<Long> departIds = statVo.getDepartIds();
if(departIds == null) {
departIds = new ArrayList<>();
}

List<Long> locationIds = statVo.getLocationIds();
if(locationIds == null) {
locationIds = new ArrayList<>();
}

long startMills = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
if(StrUtil.isNotBlank(statVo.getStartDate())) {
startMills = DateUtil.parse(statVo.getStartDate(),"yyyy-MM-dd HH:mm:ss").getTime();
}

long endMills = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH).getTime();
if(StrUtil.isNotBlank(statVo.getEndDate())) {
endMills = DateUtil.parse(statVo.getEndDate(),"yyyy-MM-dd HH:mm:ss").getTime();
}

List<Map<String, Object>> dataList = new ArrayList<>();
for(Location location: locationList) {
if(location.getIsDef()!= null && location.getIsDef() == 1) {
continue;
}

if(location.getType() == null ||!location.getType().equalsIgnoreCase("2")) {
continue;
}

// Filter Drop Non Inference Box
if(location.getUseType() == null ||!(location.getUseType() == 0 || location.getUseType() == 2)) {
continue;
}

if(!departIds.isEmpty()) {
if(location.getDepartId() == null ||!departIds.contains(location.getDepartId())) {
continue;
}
}

if(!locationIds.isEmpty()) {
if(!locationIds.contains(location.getId())) {
continue;
}
}

// not Process
int d0 = reportService.countByBoxAndAudit(location.getId(), 0, startMills, endMills);

// Process
int d1 = reportService.countByBoxAndAudit(location.getId(), 1, startMills, endMills);

// Closed
int d2 = reportService.countByBoxAndAudit(location.getId(), 2, startMills, endMills);

Depart depart = apDepartService.getById(location.getDepartId());

int total = d0 + d1 + d2;
int rate = total == 0? 0: Double.valueOf((d1 + d2) * 1.0 / total * 100).intValue();

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("departId", depart == null? 0L: depart.getId());
dataMap.put("departName", depart == null?"Unknown": depart.getName());
dataMap.put("boxId", location.getId());
dataMap.put("boxName", location.getName());
dataMap.put("alarmTotal", d0 + d1 + d2);
dataMap.put("alarmUnhandle", d0);
dataMap.put("alarmHandled", d1);
dataMap.put("alarmClosed", d2);
dataMap.put("alarmRate", rate);
dataList.add(dataMap);
}

return JsonResultUtils.success(dataList);
}


// @PostMapping("/stat/summary/handle/rate")
// @ResponseBody
// public JsonResult<?> statSummaryHandleRate(@RequestBody ReportSummaryHandleVo handleVo) {
// Map<String, Object> dataMap = new HashMap<>();
// dataMap.put("type","hour"); // Count Dimension Degree,hour- h,day- Day,month- Month,year- Year
// dataMap.put("datas", Collections.emptyList());
//
// if(handleVo.getStartDate() == null || handleVo.getEndDate() == null) {
// return JsonResultUtils.success(dataMap);
//}
//
// // 1 Day by h Display, 3 Year with up by Year Display, 3 Year
//
//}


@SaCheckPermission("alarmData")
@PostMapping("stat/audit/result")
@ResponseBody
public JsonResult<?> statAuditResult() {
List<Map<String, Object>> statList = new ArrayList<>();
// for(int i = 600; i >= 0; i--) {
// Date startDate = DateUtil.truncate(DateUtil.offsetDay(new Date(), i * -1), DateField.DAY_OF_MONTH);
// Date endDate = DateUtil.truncate(DateUtil.offsetDay(startDate, 1), DateField.DAY_OF_MONTH);
//
// // not Process Number
// int d0 = reportService.countByAudit(startDate.getTime(), endDate.getTime(), 0);
//
// // Confirm Number
// int d1 = reportService.countByAudit(startDate.getTime(), endDate.getTime(), 1);
//
// // Closed Number
// int d2 = reportService.countByAudit(startDate.getTime(), endDate.getTime(), 2);
//
// // All Count
// int total = d0 + d1 + d2;
//
// // Process Rate
// int rate = total == 0? 0: Double.valueOf((d1 + d2) * 1.0 / total * 100).intValue();
//
// Map<String, Object> statMap = new HashMap<>();
// statMap.put("unhandle", d0);
// statMap.put("handled", d1);
// statMap.put("closed", d2);
// statMap.put("total", total);
// statMap.put("rate", rate);
// statMap.put("time", DateUtil.format(startDate,"yyyy-MM-dd"));
// statList.add(statMap);
//}

int days = 365; // Default One Year
String clearReportDay = configService.getByValTag("clearReportDay");
if(StrUtil.isNotBlank(clearReportDay)) {
try {
days = Integer.parseInt(clearReportDay);
} catch (Exception e) {
//
}
}

Map<Integer, Map<String, Object>> reportSummaryMap = new HashMap<>();

Date startDate = DateUtil.offsetDay(new Date(), days * -1);
String start = DateUtil.format(startDate,"yyyyMMdd");
List<ReportSummary> reportSummaryList = reportSummaryService.listData(Integer.parseInt(start));
for(ReportSummary reportSummary: reportSummaryList) {
Map<String, Object> statMap = new HashMap<>();
statMap.put("unhandle", reportSummary.getReportUnhandle());
statMap.put("handled", reportSummary.getReportHandled());
statMap.put("closed", reportSummary.getReportClosed());
statMap.put("total", reportSummary.getReportTotal());
statMap.put("rate", reportSummary.getReportHandleRate());
statMap.put("time", reportSummary.getDate());
// statList.add(statMap);

reportSummaryMap.put(reportSummary.getDate(), statMap);
}

List<Integer> dateList = new ArrayList<>();
DateUtil.rangeToList(startDate, new Date(), DateField.DAY_OF_YEAR).forEach(date1 -> {
int date = Integer.parseInt(DateUtil.format(date1,"yyyyMMdd"));
dateList.add(date);
});

for(Integer date: dateList) {
if(reportSummaryMap.containsKey(date)) {
statList.add(reportSummaryMap.get(date));
} else {
Map<String, Object> statMap = new HashMap<>();
statMap.put("unhandle", 0);
statMap.put("handled", 0);
statMap.put("closed", 0);
statMap.put("total", 0);
statMap.put("rate", 0);
statMap.put("time", date);
statList.add(statMap);
}
}

return JsonResultUtils.success(statList);
}

@SaCheckPermission("alarmData")
@PostMapping("stat/audit/time")
@ResponseBody
public JsonResult<?> statAuditTime() {
List<Map<String, Object>> statList = new ArrayList<>();
// for(int i = 600; i >= 0; i--) {
// Date startDate = DateUtil.truncate(DateUtil.offsetDay(new Date(), i * -1), DateField.DAY_OF_MONTH);
// Date endDate = DateUtil.truncate(DateUtil.offsetDay(startDate, 1), DateField.DAY_OF_MONTH);
//
// // not Process Number
// int d0 = reportService.countByAudit(startDate.getTime(), endDate.getTime(), 0);
//
// // Confirm Number
// int d1 = reportService.countByAudit(startDate.getTime(), endDate.getTime(), 1);
//
// // Closed Number
// int d2 = reportService.countByAudit(startDate.getTime(), endDate.getTime(), 2);
//
// // Process Time s Number
// long secs = reportService.sumByAuditTimeLen(startDate.getTime(), endDate.getTime());
//
// // Average Process Time, min
// double avgTime = 0;
// if((d1 + d2) > 0) {
// avgTime = Double.valueOf(secs * 1.0 / (d1 + d2) / 60).longValue();
//}
//
// Map<String, Object> statMap = new HashMap<>();
// statMap.put("unhandle", d0);
// statMap.put("handled", d1);
// statMap.put("closed", d2);
// statMap.put("avgTime", avgTime);
// statMap.put("time", DateUtil.format(startDate,"yyyy-MM-dd"));
// statList.add(statMap);
//}

int days = 365; // Default One Year
String clearReportDay = configService.getByValTag("clearReportDay");
if(StrUtil.isNotBlank(clearReportDay)) {
try {
days = Integer.parseInt(clearReportDay);
} catch (Exception e) {
//
}
}

//
Map<Integer, Map<String, Object>> reportSummaryMap = new HashMap<>();

Date startDate = DateUtil.offsetDay(new Date(), days * -1);
String start = DateUtil.format(startDate,"yyyyMMdd");
List<ReportSummary> reportSummaryList = reportSummaryService.listData(Integer.parseInt(start));
for(ReportSummary reportSummary: reportSummaryList) {
Map<String, Object> statMap = new HashMap<>();
statMap.put("unhandle", reportSummary.getReportUnhandle());
statMap.put("handled", reportSummary.getReportHandled());
statMap.put("closed", reportSummary.getReportClosed());
statMap.put("avgTime", reportSummary.getReportHandleTime());
statMap.put("time", reportSummary.getDate());
statMap.put("total", reportSummary.getReportTotal());
//statList.add(statMap);

reportSummaryMap.put(reportSummary.getDate(), statMap);
}

List<Integer> dateList = new ArrayList<>();
DateUtil.rangeToList(startDate, new Date(), DateField.DAY_OF_YEAR).forEach(date1 -> {
int date = Integer.parseInt(DateUtil.format(date1,"yyyyMMdd"));
dateList.add(date);
});

//
for(Integer date: dateList) {
if(reportSummaryMap.containsKey(date)) {
statList.add(reportSummaryMap.get(date));
} else {
Map<String, Object> statMap = new HashMap<>();
statMap.put("unhandle", 0);
statMap.put("handled", 0);
statMap.put("closed", 0);
statMap.put("avgTime", 0);
statMap.put("total", 0);
statMap.put("time", date);
statList.add(statMap);
}
}

return JsonResultUtils.success(statList);
}


/**
* will ms Number Convert for class Like"3min","2hour"Format
*/
public static String formatDuration(Date minDate, Date maxDate) {
if(minDate == null || maxDate == null) {
return"";
}

long millis = maxDate.getTime() - minDate.getTime();

long totalSeconds = millis / 1000;
if(totalSeconds < 0) {
return"0s";
}

if(totalSeconds < 60) {
return totalSeconds +"s";
}

long days = totalSeconds / (24 * 3600);
long hours = (totalSeconds % (24 * 3600)) / 3600;
long minutes = (totalSeconds % 3600) / 60;
long seconds = totalSeconds % 60;

StringBuilder sb = new StringBuilder();
if (days > 0) sb.append(days).append("d");
if (hours > 0) sb.append(hours).append("h");
if (minutes > 0) sb.append(minutes).append("min");
if (seconds > 0) sb.append(seconds).append("s");

return sb.toString();
}

@ApiOperation(value ="Alarm Data Count", notes ="Count Alarm Close Count, Process Count, not Process Count, Today Day Alert Count")
@SaCheckPermission("alarmData")
@PostMapping("summary")
@ResponseBody
public JsonResult<ReportSummaryDTO> summary(@RequestBody ReportSummaryVo vo) {
// when Day Alert Count
ReportSummaryVo todayVo = new ReportSummaryVo();
BeanUtils.copyProperties(vo, todayVo);
todayVo.setStartTime(DateUtil.beginOfDay(new Date()));
todayVo.setEndTime(DateUtil.endOfDay(new Date()));
int todayCount =reportService.countForSummary(todayVo);

// not Process Count
vo.setAppendResult(0);
int unhandleCount =reportService.countForSummary(vo);

// Process Count
vo.setAppendResult(1);
int handleCount =reportService.countForSummary(vo);

// Closed Count
vo.setAppendResult(2);
int closeCount =reportService.countForSummary(vo);

// self Dynamic Process Count
vo.setAppendResult(3);
int autoHandleCount =reportService.countForSummary(vo);

// input out
ReportSummaryDTO dto = new ReportSummaryDTO();
dto.setTodayCount(todayCount);
dto.setUnHandleCount(unhandleCount);
dto.setHandleCount(handleCount + autoHandleCount);
dto.setCloseCount(closeCount);
return JsonResultUtils.success(dto);
}
}
