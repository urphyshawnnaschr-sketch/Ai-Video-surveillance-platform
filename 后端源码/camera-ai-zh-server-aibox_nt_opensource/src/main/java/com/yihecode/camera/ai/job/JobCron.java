package com.yihecode.camera.ai.job;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.AlgorithmTask;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.face.FaceReportService;
import com.yihecode.camera.ai.web.api.ReportDiscard;
import com.yihecode.camera.ai.web.app.push.AppPushController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
* Framework inner Simple form Task Schedule
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class JobCron {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private AppPushController appPushController;

    @Autowired
    private AlgorithmTaskService algorithmTaskService;

    @Autowired
    private FaceReportService faceReportService;

    @Resource
    private StreamNoneReaderJob streamNoneReaderJob;

    @Resource
    private AlgorithmBoxJob algorithmBoxJob;

    @Resource
    private FaceSyncBoxJob faceSyncBoxJob;

    @Autowired
    private ReportSummaryJob reportSummaryJob;

    @Autowired
    private TemperatureCameraConfigJob temperatureCameraConfigJob;

    @Autowired
    private CameraReconnectJob cameraReconnectJob;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ProjectConfig projectConfig;

    @Resource
    private MockJob mockJob;

    @Autowired
    private ReportResendFeishuJob reportResendFeishuJob;

    /**
* Each Two min Execute One sub
*/
//@Async
// @Scheduled(cron ="0 0/2 * * *?")
// public void reportResendFeishu() {
// reportResendFeishuJob.resendFeishuTask();
//}

/**
* Each 5 min Clear Divide super over Time ReportFilter Time Record Value
*/
@Async
@Scheduled(cron ="0 0/5 * * *?")
public void reportDiscard() {
try {
ReportDiscard.getInst().remove();
} catch (Exception e) {
log.error("ReportFilter Time Record Value Clear Divide Exception", e);
}
}

/**
* Each day Clear Divide Alert Record
*/
@Async
@Scheduled(cron ="0 0 1 * *?")
public void clearReport() {
// Alarm Data Delete
String clearReportDay = configService.getByValTag("clearReportDay");
if(StrUtil.isNotBlank(clearReportDay)) {
int crd = Integer.parseInt(clearReportDay);
log.info("clearReportDay {}, {}", clearReportDay, DateUtil.truncate(DateUtil.offsetDay(new Date(), -1 * crd), DateField.DAY_OF_MONTH));
long mills = DateUtil.truncate(DateUtil.offsetDay(new Date(), -1 * crd), DateField.DAY_OF_MONTH).getTime();
reportService.deleteData(mills);
}

// Alarm Image Delete
String clearReportImageDay = configService.getByValTag("clearReportImageDay");
if(StrUtil.isNotBlank(clearReportImageDay)) {
int crd = Integer.parseInt(clearReportImageDay);
log.info("clearReportImageDay {}, {}", clearReportImageDay, DateUtil.truncate(DateUtil.offsetDay(new Date(), -1 * crd), DateField.DAY_OF_MONTH));
long mills = DateUtil.truncate(DateUtil.offsetDay(new Date(), -1 * crd), DateField.DAY_OF_MONTH).getTime();
reportService.deleteDataImage(mills);
}

//reportService.clearReport();
faceReportService.clearReport();
}

/**
* Each day early 8 AM Point Query not Process Alert Info, Push to app
*/
@Async
@Scheduled(cron ="0 0 8 * *?")
public void appPush() {
if(projectConfig.isAppEnable()) {
appPushController.sendUnAuditReportToAll();
}
}

/**
* Detection Camera Heartbeat Time, like Result super over 20 min, rule table show Offline
*/
@Async
@Scheduled(cron ="0 0/5 * * *?")
public void cameraOffline() {
Date date = DateUtil.offsetMinute(new Date(), -15);
cameraService.updateOffline(date);
}

/**
* Each Two min Check Download Task, Delete in Degree not Change Task
*/
@Value("${dataModelsDir}")
public String MODEL_DIR;
Map<String, Long> taskMap = new HashMap<>();
@Async
@Scheduled(cron ="0 0/2 * * *?")
public void algorithmTask() {
List<AlgorithmTask> taskList = algorithmTaskService.list();
Map<String, Long> map = new HashMap<>();
for (AlgorithmTask task: taskList) {
String localPath = MODEL_DIR +"temp";
String filePath = localPath +"/"+ task.getNameEn();
File localFile = new File(filePath);
if (localFile.exists()) {
Long length = localFile.length();
if (taskMap.containsKey(task.getNameEn())) {
if (Objects.equals(taskMap.get(task.getNameEn()), length)) {
algorithmTaskService.removeById(task.getId());
localFile.delete();
} else {
map.put(task.getNameEn(), length);
}
} else {
map.put(task.getNameEn(), length);
}
}
}
taskMap = map;
}

/**
* Monitor Recording Whether Normal Execute
*/
// 20250515, Recording by Local record_interface in Line Control
// @Async
// @Scheduled(cron ="0 0/1 * * *?")
// public void runStreamRecordJob() {
// streamRecordJob.runJob();
//}

/**
* Stream Media Again Set
*/
// 20250515, not again in Line Stream Media Set
// @Async
// @Scheduled(cron ="0 0/1 * * *?")
// public void runMediaConnect() {
// streamRecordJob.runConnect();
//}

/**
* Relate Alarm and Recording
*/
// 20250515, Modify Complete Submit Recording Hour, Determine Whether has Phase should Alert, like Result not has, rule Recording Lose Abandon
// @Async
// @Scheduled(cron ="0/30 * * * *?")
// public void runAddFlag() {
// streamRecordJob.runAddFlag();
//}

/**
* Delete no Use Recording Record and File
*/
// 20250515, Modify Complete Submit Recording Hour, Determine Whether has Phase should Alert, like Result not has, rule Recording Lose Abandon
// @Async
// @Scheduled(cron ="0 0/2 * * *?")
// public void runDelFlag() {
// streamRecordJob.runDelFlag();
//}

/**
* Cross net Mode, Notification customer account Local Stop Push Stream
*/
@Async
@Scheduled(cron ="0 0/2 * * *?")
public void runStreamNoneReader() {
streamNoneReaderJob.runJob();
}

/**
* Process Algorithm and Box Device Task
*/
@Async
@Scheduled(cron ="0/10 * * * *?")
public void runAlgoBox() {
algorithmBoxJob.runJob();
}

/**
* Process Face Data and Box Device Sync Task
*/
@Async
@Scheduled(cron ="0/30 * * * *?")
public void runFaceSyncBox() {
faceSyncBoxJob.runJob();
}

/**
* Again Calculate Alarm Day Count Data
*/
@Async
@Scheduled(cron ="0 0 2 * *?")
public void runSummaryTask() {
reportSummaryJob.runSummaryTask();
}

/**
* Execute when Day Alarm Data Count
*/
@Async
@Scheduled(cron ="0 0/10 * * *?")
public void runTodaySummary() {
reportSummaryJob.runTodaySummary();
}

// /**
// * Temp Control Alarm Camera self Dynamic Config
// */
// @Async
// @Scheduled(cron ="0 0/15 * * *?")
// public void runTemperatureCameraConfigJob() {
// temperatureCameraConfigJob.runJob();
//}

/**
* Camera re connect
*/
@Async
@Scheduled(cron ="0 0/5 * * *?")
public void runCameraReconnectJob() {
cameraReconnectJob.runJob();
}
}
