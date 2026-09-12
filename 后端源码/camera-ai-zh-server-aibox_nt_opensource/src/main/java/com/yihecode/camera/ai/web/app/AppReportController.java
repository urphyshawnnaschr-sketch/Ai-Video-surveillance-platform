package com.yihecode.camera.ai.web.app;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.entity.AlarmLevel;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.enums.ReportType;
import com.yihecode.camera.ai.notify.sms.SendSmsUtil;
import com.yihecode.camera.ai.notify.wework.WeWorkRobotSendUtils;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

/**
* app End Count Phase close
*
* @author Abyss
* @date 2023/12/16 14:52
*/
@Api(tags = "app End _ Count Phase close")
@SaCheckLogin
@Controller
@RequestMapping({ "/app/report" })
public class AppReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private AlarmLevelService alarmLevelService;
    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private SmsPhoneService smsPhoneService;

    /**
* Get Today Day Basic Count Data
*
* @author Abyss
* @date 2023/12/20 15:01
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get Today Day Basic Count Data")
    @GetMapping("/getTodayCount")
    @ResponseBody
    public JsonResult getTodayCount() {
        Map<String, Object> resultMap = reportService.selectTodayCount();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get Seven day inner Basic Count Data
*
* @author Abyss
* @date 2023/12/20 15:02
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get Seven day inner Basic Count Data")
    @GetMapping("/get7DayCount")
    @ResponseBody
    public JsonResult get7DayCount() {
        Map<String, Object> resultMap = reportService.select7DayCount();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get This Month Basic Count Data
*
* @author Abyss
* @date 2023/12/20 15:02
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get This Month Basic Count Data")
    @GetMapping("/getMonthCount")
    @ResponseBody
    public JsonResult getMonthCount() {
        Map<String, Object> resultMap = reportService.selectMonthCount();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get Alert Type Row Line (Today Day)
*
* @author Abyss
* @date 2023/12/20 15:22
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get Alert Type Row Line (Today Day)")
    @GetMapping("/getReportTypeRankingToday")
    @ResponseBody
    public JsonResult getReportTypeRankingToday() {
        List<Map<String, Object>> resultMap = reportService.selectReportTypeRankingToday();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get Alert Row Line Type (near 7 Day)
*
* @author Abyss
* @date 2023/12/20 15:22
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get Alert Row Line Type (near 7 Day)")
    @GetMapping("/getReportTypeRanking7Day")
    @ResponseBody
    public JsonResult getReportTypeRanking7Day() {
        List<Map<String, Object>> resultMap = reportService.selectReportTypeRanking7Day();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get Alert Type Row Line (This Month)
*
* @author Abyss
* @date 2023/12/20 15:22
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get Alert Type Row Line (This Month)")
    @GetMapping("/getReportTypeRankingMonth")
    @ResponseBody
    public JsonResult getReportTypeRankingMonth() {
        List<Map<String, Object>> resultMap = reportService.selectReportTypeRankingMonth();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get Alert Type - Count Count (This Day, Each Two h One Count)
*
* @author Abyss
* @date 2023/12/20 16:43
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get Alert Type - Count Count (This Day, Each Two h One Count)")
    @GetMapping("/getReportTypeCountToday")
    @ResponseBody
    public JsonResult getReportTypeCountToday() {
        Map<String, Object> resultMap = reportService.selectReportTypeCountToday();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get Alert Type - Count Count (near Seven Day, Each day One Count)
*
* @author Abyss
* @date 2023/12/20 17:36
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get Alert Type - Count Count (near Seven Day, Each day One Count)")
    @GetMapping("/getReportTypeCount7Day")
    @ResponseBody
    public JsonResult getReportTypeCount7Day() {
        Map<String, Object> resultMap = reportService.selectReportTypeCount7Day();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get Alert Type - Count Count (This Month, Each day One Count)
*
* @author Abyss
* @date 2023/12/20 18:37
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Get Alert Type - Count Count (This Month, Each day One Count)")
    @GetMapping("/getReportTypeCountMonth")
    @ResponseBody
    public JsonResult getReportTypeCountMonth() {
        Map<String, Object> resultMap = reportService.selectReportTypeCountMonth();
        return JsonResultUtils.success(resultMap);
    }

    /**
* Get Alert Page List
*
* @author Abyss
* @date 2023/12/23 14:53
*/
    @ApiOperation("Query Alert Data List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "Pagination page Surface", example = "1"),
            @ApiImplicitParam(name = "limit", value = "Pagination Number", example = "10"),
            @ApiImplicitParam(name = "cameraName", value = "Camera Name", example = "for Doorway"),
            @ApiImplicitParam(name = "algorithmId", value = "Algorithm id", example = "1"),
            @ApiImplicitParam(name = "type", value = "Alarm Type", example = "1"),
            @ApiImplicitParam(name = "startDate", value = "Start Date", example = "2025-02-03 11:22:33"),
            @ApiImplicitParam(name = "endDate", value = "End Date", example = "2025-02-04 11:22:33"),
            @ApiImplicitParam(name = "alarmLevelId", value = "Alarm Level id", example = "1"),
            @ApiImplicitParam(name = "auditState", value = "Audit Status", example = "0"),
    })
    @GetMapping({ "/listPage" })
    @ResponseBody
    public PageResult listPage(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer limit,
                               String cameraName,
                               Long algorithmId,
                               Integer type,
                               String startDate,
                               String endDate,
                               Long alarmLevelId,
                               Integer auditState) {
        Long startMills = null;
        if (StrUtil.isNotBlank(startDate)) {
            Date startDateObj = DateUtil.parse(startDate, "yyyy-MM-dd");
            startMills = DateUtil.truncate(startDateObj, DateField.DAY_OF_MONTH).getTime();
        }
        Long endMills = null;
        if (StrUtil.isNotBlank(endDate)) {
            Date endDateObj = DateUtil.parse(endDate, "yyyy-MM-dd");
            endMills = DateUtil.truncate(DateUtil.offsetDay(endDateObj, 1), DateField.DAY_OF_MONTH).getTime();
        }

        IPage<Report> pageResult = this.reportService.listByPageApp(new Page<>(page, limit), cameraName, algorithmId,
                type, startMills, endMills, alarmLevelId, auditState);
        List<Report> reportList = pageResult.getRecords();
        if (reportList == null) {
            reportList = new ArrayList<>();
        }
        Map<Long, String> cameraNames = new HashMap<>();
        List<Camera> cameraList = this.cameraService.list();
        if (cameraList != null) {
            for (Camera camera : cameraList) {
                cameraNames.put(camera.getId(), camera.getName());
            }
        }

        //Query Alert Level List
Map<Long, AlarmLevel> alarmLevelMap = alarmLevelService.getDataMap();

//
Map<Long, String> algorithmNames = new HashMap<>();
Map<Long, AlarmLevel> algorithmAlarmLevels = new HashMap<>();
List<Algorithm> algorithmList = this.algorithmService.list();
if (algorithmList!= null) {
for (Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());

// Algorithm Corresponding Alert Level Name
algorithmAlarmLevels.put(algorithm.getId(), alarmLevelMap.get(algorithm.getAlarmLevelId()));
}
}
for (Report report: reportList) {
String cameraName2 = cameraNames.get(report.getCameraId());
String algorithmName = algorithmNames.get(report.getAlgorithmId());
report.setCameraName(cameraName2 == null?"": cameraName2);
report.setAlgorithmName(algorithmName == null?"": algorithmName);
report.setCreatedStr(
report.getCreatedAt()!= null? DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"):"");
String typeName ="UNKNOW";
if (ReportType.AI.getType() == report.getType()) {
typeName = ReportType.AI.getText();
} else if (ReportType.STREAM.getType() == report.getType()) {
typeName = ReportType.STREAM.getText();
}
report.setTypeName(typeName);
report.setAlarmLevel(algorithmAlarmLevels.get(report.getAlgorithmId()));
}
return PageResultUtils.success(pageResult.getTotal(), reportList);
}

@ApiOperation("Query All Algorithm List")
@GetMapping({"/algorithmList"})
@ResponseBody
public JsonResult algorithmList() {
return JsonResultUtils.success(algorithmService.list());
}

/**
* Get Alert Detail
*
* @author Abyss
* @date 2023/12/23 14:53
*/
@ApiOperation("Query Alert Detail Data")
@ApiImplicitParam(name ="id", value ="Alarm id")
@GetMapping({"/detail"})
@ResponseBody
public JsonResult detailInfo(Long id) {
Map<String, Object> retMap = new HashMap<>();

//
Report report = reportService.getById(id);
if (report!= null && report.getCreatedAt()!= null) {
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
if (report.getAlgorithmId() == 0L && report.getType() == ReportType.STREAM.getType()) {
algorithm = new Algorithm();
algorithm.setName(ReportType.STREAM.getText());
}
retMap.put("algorithm", algorithm);

return JsonResultUtils.success(retMap);
}

/**
* Get total View Data
*
* @author Abyss
* @date 2023/12/23 14:53
*/
@ApiOperation("Get total View Data")
@GetMapping("/overview")
@ResponseBody
public JsonResult overview() {
Map<String, Object> resultMap = reportService.selectOverview();
return JsonResultUtils.success(resultMap);
}

/**
* Process
*
* @author Abyss
* @date 2023/12/23 15:04
*/
@ApiOperation("Audit Process")
@ApiImplicitParam(name ="id", value ="Alarm id")
@PostMapping("/audit")
@ResponseBody
public JsonResult audit(Long id) {
Report report = new Report();
report.setId(id);
report.setAuditState(1);
report.setAuditAt(new Date());
reportService.saveOrUpdate(report);
return JsonResultUtils.success();
}

/**
* SMS Push
*
* @author Abyss
* @date 2023/12/23 15:32
*/
@ApiOperation("SMS Push")
@ApiImplicitParam(name ="id", value ="Alarm id")
@PostMapping("/smsPush")
@ResponseBody
public JsonResult smsPush(Long id) {
// Push SMS
String smsEnable = configService.getByValTag("smsEnable");
// log.info("SMS Send ID: {}", smsEnable);
if (StrUtil.isNotBlank(smsEnable) &&"true".equals(smsEnable)) {
Report report = reportService.getById(id);
Camera camera = cameraService.getById(report == null? 0L: report.getCameraId());
Algorithm algorithm = algorithmService.getById(report == null? 0L: report.getAlgorithmId());
if (null == camera || null == algorithm) {
return JsonResultUtils.fail("Alert Info Exception, no Camera or Algorithm Data");
}
String mobiles = smsPhoneService.listPhoneStr("test");
// log.info("SMS Send No code:{}", mobiles);
if (StrUtil.isNotBlank(mobiles)) {
String smsAppKey = configService.getByValTag("smsAppKey");
String smsTplId = configService.getByValTag("smsTplId");
if (StrUtil.isNotBlank(smsAppKey) && StrUtil.isNotBlank(smsTplId)) {
SendSmsUtil.send(mobiles, camera.getName(), algorithm.getName(), smsAppKey, smsTplId);
} else {
// log.info("SMS Config lack Missing, appKey: {}, tplId: {}", smsAppKey, smsTplId);
}
}
return JsonResultUtils.success();
} else {
return JsonResultUtils.fail("not Enable SMS Push");
}
}

/**
* WeChat Push
*
* @author Abyss
* @date 2023/12/23 15:32
*/
@ApiOperation("WeChat Push")
@ApiImplicitParam(name ="id", value ="Alarm id")
@PostMapping("/wechatPush")
@ResponseBody
public JsonResult wechatPush(Long id) {
// Push to WeWork Group Bot
String weworkEnable = configService.getByValTag("weworkEnable");
// log.info("WeWork ID: {}", weworkEnable);
if (StrUtil.isNotBlank(weworkEnable) &&"true".equals(weworkEnable)) {
Report report = reportService.getById(id);
Camera camera = cameraService.getById(report == null? 0L: report.getCameraId());
Algorithm algorithm = algorithmService.getById(report == null? 0L: report.getAlgorithmId());
if (null == camera || null == algorithm) {
return JsonResultUtils.fail("Alert Info Exception, no Camera or Algorithm Data");
}
String weworkUrl = configService.getByValTag("weworkUrl");
String webUrl = configService.getByValTag("webUrl");
// log.info("WeWork Group Address: {}", weworkUrl);
// log.info("WeWork /web Address: {}", webUrl);
if (StrUtil.isNotBlank(weworkUrl) && StrUtil.isNotBlank(webUrl)) {
String clickUrl = webUrl +"/report/detail?id="+ report.getId();
String picUrl = webUrl +"/report/stream?id="+ report.getId();
WeWorkRobotSendUtils.sendTextAndImage(weworkUrl, camera.getName() +"#"+ algorithm.getName() +"# Alert,"
+ DateUtil.format(new Date(),"MM/dd HH:mm"), null, clickUrl, picUrl);
}
return JsonResultUtils.success();
} else {
return JsonResultUtils.fail("not Enable WeWork Group Push");
}
}

@ApiIgnore
@SaIgnore
@PostMapping("/mock")
@ResponseBody
public JsonResult<?> MOCK(String date) {
Date t = new Date();
if(StrUtil.isNotBlank(date)) {
t = DateUtil.parse(date,"yyyyMMddHHmmss");
}


List<Algorithm> algorithms = algorithmService.list();
if(algorithms == null || algorithms.isEmpty()) {
return JsonResultUtils.success();
}

List<Camera> cameras = cameraService.listData();
if(cameras == null || cameras.isEmpty()) {
return JsonResultUtils.success();
}

Random random = new Random();

List<Map<String, Object>> params = new ArrayList<>();
Map<String, Object> p = new HashMap<>();
p.put("type","fire");
p.put("position", Arrays.asList(200, 200, 400, 400));
p.put("confidence","0.6");
params.add(p);

for(Camera camera: cameras) {
for(Algorithm algorithm: algorithms) {
int num = random.nextInt(3) + 1;

for(int i = 0; i < num; i++) {
// Storage Alert
Report report = new Report();
report.setCameraId(camera.getId());
report.setAlgorithmId(algorithm.getId());
report.setType(ReportType.AI.getType());
report.setFileName("d:/12.png");
report.setParams(JSON.toJSONString(params));
report.setCreatedAt(t);
report.setCreatedMills(t.getTime());
report.setAuditResult(0);
report.setAuditState(0);
report.setDisplay(0);
report.setBoxId(camera.getLocationId());
report.setDepartId(0L);
report.setRois("");
report.setLines("");
reportService.save(report);
}
}
}
return JsonResultUtils.success();
}

}
