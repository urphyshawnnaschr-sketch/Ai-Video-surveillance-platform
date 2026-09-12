package com.yihecode.camera.ai.web.api.temperature;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.enums.ReportType;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.vo.ReportMessage;
import com.yihecode.camera.ai.web.api.aibox.AiBoxReportTime;
import com.yihecode.camera.ai.web.api.comm.AlarmPushServcie;
import com.yihecode.camera.ai.websocket.ReportWebsocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
* Temp Control Camera Alarm Receive
*/
@ApiIgnore
@SaIgnore
@Slf4j
@RestController
@RequestMapping("/api/temperature/alarm")
public class ApiTemperatureAlarmController {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private AlarmPushServcie alarmPushServcie;

    @Resource
    private ReportWebsocket reportWebsocket;

    @Autowired
    private AccountService accountService;

    @Value("${cameraDir}")
    private String cameraDir;

    /**
* Alarm Process
*
* @param TMA Temperature Alarm Data
* @param visibleLightImage Image
* @return
*/
    @PostMapping(value = {"", "/"})
    public JsonResult<?> index(String TMA, MultipartFile visibleLightImage, MultipartFile thermalImage, HttpServletRequest request) {
        if(StrUtil.isBlank(TMA)) {
            //log.error("Temp Control Alarm Filter, Non TMA Alarm");
return JsonResultUtils.success();
}

if(visibleLightImage == null && thermalImage == null) {
log.error("Temp Control Alarm Filter, no Thermal Image or Visible Light Image");
return JsonResultUtils.success();
}

log.info("Temp Control Alarm XML: {}", TMA);

// Parse Data
TemperatureAlarmXmlResult result = TemperatureAlarmXmlParser.parse(TMA);
if(!result.isSuccess()) {
log.error("Temp Control Alarm Parse Error, ex: {}", result.getError());
return JsonResultUtils.success();
}

// Query Camera
Camera camera = cameraService.getByIp(result.getIpAddress());
if(camera == null) {
log.error("Temp Control Alarm, By IP no Method check find to Camera, ip: {}", result.getIpAddress());
return JsonResultUtils.success();
}

Algorithm algorithm = algorithmService.getByNameEn("temperatureAlarm");
CameraAlgorithm cameraAlgorithm = cameraAlgorithmService.getByCameraAndAlgorithmId(camera.getId(), algorithm.getId());
if(cameraAlgorithm == null) {
log.error("Temp Control Alarm, Camera not Config Temp Control Alarm, camera id: {}, name: {}", camera.getId(), camera.getName());
return JsonResultUtils.success();
}

// Determine Whether Filter Alarm
Long lastTime = AiBoxReportTime.getInst().get(camera.getId() +"-"+ algorithm.getId());
long diff = System.currentTimeMillis() - lastTime;
if (diff < camera.getAlarmInterval() * 1000) {
log.error("Temp Control Alarm Algorithm, Filter Algorithm, small at Alarm Interval, cameraId: {}, algoId: {}, alarmInterval(ms): {}, lastTime: {}, now: {}, diff: {}", camera.getId(), algorithm.getId(), camera.getAlarmInterval() * 1000, new Date(lastTime), new Date(), diff);
return JsonResultUtils.success();
}

// Query belong belong Box
Location location = locationService.getById(camera.getLocationId());

// Save Image to Disk
String filepath = null;
if(thermalImage!= null) {// Thermal Image
try {
String date = DateUtil.format(new Date(),"yyyyMMdd");
String saveDest = String.format("%s/%s/%s/%s/", cameraDir,"temperature_alarm", date, camera.getId());
saveDest = FileUtils.pathTo(saveDest);
if (!FileUtil.exist(saveDest)) {
FileUtil.mkdir(saveDest);
}

filepath = FileUtils.pathTo(saveDest +"/"+ IdUtil.randomUUID() +".jpg");
thermalImage.transferTo(new File(filepath));
} catch (Exception e) {
log.error("Temp Control Alarm Error, Thermal Image Write Disk Exception", e);
return JsonResultUtils.success();
}
} else {
try {
String date = DateUtil.format(new Date(),"yyyyMMdd");
String saveDest = String.format("%s/%s/%s/%s/", cameraDir,"temperature_alarm", date, camera.getId());
saveDest = FileUtils.pathTo(saveDest);
if(!FileUtil.exist(saveDest)) {
FileUtil.mkdir(saveDest);
}

filepath = FileUtils.pathTo(saveDest +"/"+ IdUtil.randomUUID() +".jpg");
visibleLightImage.transferTo(new File(filepath));
} catch (Exception e) {
log.error("Temp Control Alarm Error, Visible Light Image Write Disk Exception", e);
return JsonResultUtils.success();
}
}

// Storage Alert
Date date = new Date();
Report report = new Report();
report.setCameraId(camera.getId());
report.setAlgorithmId(algorithm.getId());
report.setType(ReportType.AI.getType());
report.setFileName(filepath);
report.setParams("");
report.setCreatedAt(date);
report.setCreatedMills(date.getTime());
report.setAuditResult((cameraAlgorithm.getAutoPush()!= null && cameraAlgorithm.getAutoPush() == 0)? 3: 0); // 3- self Dynamic Process
report.setAuditType(report.getAuditResult() == 3? 1: 0); // self Dynamic / Manual Process Alarm
report.setAuditAt(report.getAuditResult() == 3? new Date(): null); // self Dynamic Process Alarm, rule self Dynamic Fill Charge Process Time
report.setAuditState(0);
report.setDisplay(0);
report.setBoxId(location == null? 0L: location.getId());
report.setDepartId(location == null? 0L: (location.getDepartId() == null? 0L: location.getDepartId()));
report.setRois("");
report.setLines("");
report.setPushed(3); // Push in, should for All Alarm all is self Dynamic Push
reportService.save(report);
log.info("Temp Control Alarm");

// Record This sub Alert Time
AiBoxReportTime.getInst().put(camera.getId(), algorithm.getId());

// Query the Box belong belong Department Corresponding Person member
List<Account> accounts = accountService.listByDepartId(location == null? null: location.getDepartId());

// Send web Frontend
sendToWeb(camera, algorithm, location, report, accounts);

// Send Third Party. self Dynamic Push

// Feishu Push, by Need Push
alarmPushServcie.send(camera, algorithm, report);
return JsonResultUtils.success();
}

/**
* Alarm Process
*
* @return
*/
@PostMapping(value = {"ballhead"})
public JsonResult<Void> ballhead(HttpServletRequest request) {

// Temp Control Alarm, Request Field: name: TMA, contentType: application/xml; charset="UTF-8"
// Temp Control Alarm, Request Field: name: TMA, contentType: image/jpeg
// Temp Control Alarm, Request Field: name: TMA, contentType: image/jpeg

String TMA ="";
Part file1 = null;
Part file2 = null;

// String date = DateUtil.format(new Date(),"yyyyMMdd");
// String saveDest = String.format("%s/%s/%s/%s/", cameraDir,"temperature_alarm", date, camera.getId());
// saveDest = FileUtils.pathTo(saveDest);
// if(!FileUtil.exist(saveDest)) {
// FileUtil.mkdir(saveDest);
//}
//
// filepath = FileUtils.pathTo(saveDest +"/"+ IdUtil.randomUUID() +".jpg");

try {
Collection<Part> parts = request.getParts();
if(parts == null || parts.isEmpty()) {
log.error("Dome Temp Control _ Temp Control Alarm parts Is Empty");
} else {
for(Part part: parts) {
String contentType = part.getContentType();
if (contentType!= null && contentType.contains("application/xml")) {
// try (InputStream inputStream = part.getInputStream();
// BufferedReader reader = new BufferedReader(
// new InputStreamReader(inputStream, StandardCharsets.UTF_8)
//)) {
// StringBuilder xmlContent = new StringBuilder();
// String line;
// while ((line = reader.readLine())!= null) {
// xmlContent.append(line).append("\n");
//}
// TMA = xmlContent.toString().trim();
//
// // ✅ current In xmlString just is Complete whole XML Content String
// log.info("Dome Temp Control _ Parse to XML Content:\n{}", TMA);
// // You can with In Here Parse XML, Validate, Storage etc
//} catch (IOException e) {
// log.error("Dome Temp Control _ Read XML part Failed", e);
// break;
//}

TMA = readPartAsString(part);
}

if(contentType!= null && contentType.startsWith("image/")) {
if(file1 == null) {
file1 = part;
} else {
file2 = part;
}
}
// log.info("Temp Control Alarm, Request Field: name: {}, contentType: {}", part.getName(), part.getContentType());
}
}
} catch (Exception e) {
log.error("Temp Control Alarm _ Parse multipart Request failed", e);
}

if(StrUtil.isBlank(TMA)) {
log.error("Dome Temp Control _TMA Param Is Empty");
return JsonResultUtils.success();
}

if(file1 == null && file2 == null) {
log.error("Dome Temp Control _ Image Is Empty");
return JsonResultUtils.success();
}

// Parse Data
TemperatureAlarmXmlResult result = TemperatureAlarmXmlParser.parse(TMA);
//log.info("Dome Temp Control _ Parse Result, result: {}", result);
if(!result.isSuccess()) {
//log.error("Dome Temp Control _ Alarm Parse Error, xml: {}, ex: {}", TMA, result.getError());
return JsonResultUtils.success();
}

// Query Camera
Camera camera = cameraService.getByIp(result.getIpAddress());
if(camera == null) {
log.error("Dome Temp Control _ Alarm, By IP no Method check find to Camera, ip: {}", result.getIpAddress());
return JsonResultUtils.success();
}

Algorithm algorithm = algorithmService.getByNameEn("temperatureAlarm");
CameraAlgorithm cameraAlgorithm = cameraAlgorithmService.getByCameraAndAlgorithmId(camera.getId(), algorithm.getId());
if(cameraAlgorithm == null) {
log.error("Dome Temp Control _ Alarm, Camera not Config Temp Control Alarm, camera id: {}, name: {}", camera.getId(), camera.getName());
return JsonResultUtils.success();
}

// Determine Whether Filter Alarm
Long lastTime = AiBoxReportTime.getInst().get(camera.getId() +"-"+ algorithm.getId());
long diff = System.currentTimeMillis() - lastTime;
if (diff < camera.getAlarmInterval() * 1000) {
log.error("Dome Temp Control _ Alarm Algorithm, Filter Algorithm, small at Alarm Interval, cameraId: {}, algoId: {}, alarmInterval(ms): {}, lastTime: {}, now: {}, diff: {}", camera.getId(), algorithm.getId(), camera.getAlarmInterval() * 1000, new Date(lastTime), new Date(), diff);
return JsonResultUtils.success();
}

// Query belong belong Box
Location location = locationService.getById(camera.getLocationId());

// Save Image to Disk
String filepath = null;
if(file2!= null) {// Thermal Image
try {
String date = DateUtil.format(new Date(),"yyyyMMdd");
String saveDest = String.format("%s/%s/%s/%s/", cameraDir,"temperature_alarm", date, camera.getId());
saveDest = FileUtils.pathTo(saveDest);
if (!FileUtil.exist(saveDest)) {
FileUtil.mkdir(saveDest);
}

//filepath = FileUtils.pathTo(saveDest +"/"+ IdUtil.randomUUID() +".jpg");

filepath = saveImagePart(file2, saveDest);
} catch (Exception e) {
log.error("Dome Temp Control _ Alarm Error, Thermal Image Write Disk Exception", e);
return JsonResultUtils.success();
}
} else {
try {
String date = DateUtil.format(new Date(),"yyyyMMdd");
String saveDest = String.format("%s/%s/%s/%s/", cameraDir,"temperature_alarm", date, camera.getId());
saveDest = FileUtils.pathTo(saveDest);
if(!FileUtil.exist(saveDest)) {
FileUtil.mkdir(saveDest);
}

//filepath = FileUtils.pathTo(saveDest +"/"+ IdUtil.randomUUID() +".jpg");

filepath = saveImagePart(file1, saveDest);
} catch (Exception e) {
log.error("Temp Control Alarm Error, Visible Light Image Write Disk Exception", e);
return JsonResultUtils.success();
}
}

if(StrUtil.isBlank(filepath)) {
log.error("Dome Temp Control _ Image Storage Failed");
return JsonResultUtils.success();
}

// Storage Alert
Date date = new Date();
Report report = new Report();
report.setCameraId(camera.getId());
report.setAlgorithmId(algorithm.getId());
report.setType(ReportType.AI.getType());
report.setFileName(filepath);
report.setParams("");
report.setCreatedAt(date);
report.setCreatedMills(date.getTime());
report.setAuditResult((cameraAlgorithm.getAutoPush()!= null && cameraAlgorithm.getAutoPush() == 0)? 3: 0); // 3- self Dynamic Process
report.setAuditType(report.getAuditResult() == 3? 1: 0); // self Dynamic / Manual Process Alarm
report.setAuditAt(report.getAuditResult() == 3? new Date(): null); // self Dynamic Process Alarm, rule self Dynamic Fill Charge Process Time
report.setAuditState(0);
report.setDisplay(0);
report.setBoxId(location == null? 0L: location.getId());
report.setDepartId(location == null? 0L: (location.getDepartId() == null? 0L: location.getDepartId()));
report.setRois("");
report.setLines("");
report.setPushed(3); // Push in, should for All Alarm all is self Dynamic Push
reportService.save(report);

// Record This sub Alert Time
AiBoxReportTime.getInst().put(camera.getId(), algorithm.getId());

// Query the Box belong belong Department Corresponding Person member
List<Account> accounts = accountService.listByDepartId(location == null? null: location.getDepartId());

// Send web Frontend
sendToWeb(camera, algorithm, location, report, accounts);

// Send Third Party. self Dynamic Push

// Feishu Push, by Need Push
alarmPushServcie.send(camera, algorithm, report);
return JsonResultUtils.success();
}

private String readPartAsString(Part part) {
try (InputStream is = part.getInputStream();
ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
byte[] data = new byte[1024];
int nRead;
while ((nRead = is.read(data, 0, data.length))!= -1) {
buffer.write(data, 0, nRead);
}
return buffer.toString(StandardCharsets.UTF_8.name());
} catch (IOException e) {
//
}
return"";
}

public static String saveImagePart(Part part, String uploadDir) {
if (part == null || part.getSize() <= 0) {
return null;
}

try {
// 1. Get original start File Name with Raise Get Extension
String originalName = getSubmittedFileName(part);
String extension = extractExtension(originalName, part.getContentType());

// 2. Generate Unique One File Name
String uniqueFileName = UUID.randomUUID().toString() + extension;

// 3. Build Complete whole Path
Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
if (!Files.exists(uploadPath)) {
Files.createDirectories(uploadPath);
}
Path filePath = uploadPath.resolve(uniqueFileName);

// 4. Write File (make Use NIO more Simple Clean)
try (InputStream input = part.getInputStream()) {
Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
}

return filePath.toString();

} catch (IOException e) {
// real International Project in build Discuss make Use Log Framework (like SLF4J)
log.error("Dome Temp Control _ Save Image Failed:"+ e.getMessage());
//e.printStackTrace();
return null;
}
}

/**
* from Part in Safe all Raise Get original start File Name (Prevent Path Through Exceed)
*/
private static String getSubmittedFileName(Part part) {
String header = part.getHeader("content-disposition");
if (header == null) return null;
for (String cd: header.split(";")) {
cd = cd.trim();
if (cd.startsWith("filename")) {
String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"","");
// only Keep File Name, Remove Divide Path (like a/b/c.jpg → c.jpg)
return Paths.get(fileName).getFileName().toString();
}
}
return null;
}

/**
* By original start File Name or Content-Type Push Break Extension
*/
private static String extractExtension(String originalName, String contentType) {
// Optimize First from original start File Name Get Extension
if (originalName!= null && originalName.contains(".")) {
String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
// White Name form Validate (can select But Push Recommend)
if (ext.matches("\\.(jpg|jpeg|png|gif|bmp|webp)")) {
return ext;
}
}

// its sub By Content-Type Push Break
if (contentType!= null) {
if (contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/jpg")) {
return".jpg";
} else if (contentType.equalsIgnoreCase("image/png")) {
return".png";
} else if (contentType.equalsIgnoreCase("image/gif")) {
return".gif";
} else if (contentType.equalsIgnoreCase("image/webp")) {
return".webp";
} else if (contentType.equalsIgnoreCase("image/bmp")) {
return".bmp";
}
}

// Default
return".jpg";
}

/**
* Push Message to Frontend
*
* @param camera
* @param algorithm
* @param location
* @param report
*/
private void sendToWeb(Camera camera, Algorithm algorithm, Location location, Report report, List<Account> accounts) {
try {
// Push Alert Detail (Detail, can show show Image)
ReportMessage reportMessage = new ReportMessage();
reportMessage.setType("REPORT_SHOW");
reportMessage.setCameraId(String.valueOf(camera.getId()));
reportMessage.setCameraName(camera.getName());
reportMessage.setAlgorithmId(String.valueOf(algorithm.getId()));
reportMessage.setAlgorithmName(algorithm.getName());

reportMessage.setAlgorithmNameEn(algorithm.getNameEn());
reportMessage.setParams(report.getParams());
reportMessage.setAlarmTime(DateUtil.format(report.getCreatedAt(),"MM-dd HH:mm:ss"));
reportMessage.setWareName(location == null?"": location.getName());
reportMessage.setId(String.valueOf(report.getId()));

// Push to Frontend
reportWebsocket.sendToAccounts(accounts, JSON.toJSONString(reportMessage));
} catch (Exception e) {
// e.printStackTrace();
}
}
}
