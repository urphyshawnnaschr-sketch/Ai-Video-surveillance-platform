//package com.yihecode.camera.ai.web.api;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import com.yihecode.camera.ai.entity.AlarmLevel;
//import com.yihecode.camera.ai.entity.Algorithm;
//import com.yihecode.camera.ai.entity.Camera;
//import com.yihecode.camera.ai.entity.Location;
//import com.yihecode.camera.ai.entity.Report;
//import com.yihecode.camera.ai.entity.ReportPeriod;
//import com.yihecode.camera.ai.entity.VoicePhone;
//import com.yihecode.camera.ai.enums.MessageType;
//import com.yihecode.camera.ai.enums.ReportType;
//import com.yihecode.camera.ai.notify.dingding.DingdingRobotSendUtils;
//import com.yihecode.camera.ai.notify.sms.SendSmsUtil;
//import com.yihecode.camera.ai.notify.voice.SendVoiceUtil;
//import com.yihecode.camera.ai.notify.wework.WeWorkRobotSendUtils;
//import com.yihecode.camera.ai.service.AlarmLevelService;
//import com.yihecode.camera.ai.service.AlgorithmService;
//import com.yihecode.camera.ai.service.CameraService;
//import com.yihecode.camera.ai.service.ConfigService;
//import com.yihecode.camera.ai.service.LocationService;
//import com.yihecode.camera.ai.service.ReportPeriodService;
//import com.yihecode.camera.ai.service.ReportService;
//import com.yihecode.camera.ai.service.SmsPhoneService;
//import com.yihecode.camera.ai.service.VoicePhoneService;
//import com.yihecode.camera.ai.utils.JsonResult;
//import com.yihecode.camera.ai.utils.JsonResultUtils;
//import com.yihecode.camera.ai.vo.Message;
//import com.yihecode.camera.ai.vo.ReportMessage;
//import com.yihecode.camera.ai.web.app.push.AppPushController;
//import com.yihecode.camera.ai.websocket.MessageWebsocket;
//import com.yihecode.camera.ai.websocket.ReportWebsocket;
//
//import cn.hutool.core.date.DateField;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.StrUtil;
//import lombok.extern.slf4j.Slf4j;
//import springfox.documentation.annotations.ApiIgnore;
//
///**
// * Alert Push Data, Integrate Algorithm Push Alert Data
// *
// * @author zhoumingxing
// * @mail 465769438@qq.com
// */
//@ApiIgnore
//@Slf4j
//@Controller
//@RequestMapping({"/api/report"})
//public class ReportApiController {
//
// //
// @Autowired
// private CameraService cameraService;
//
// //
// @Autowired
// private AlgorithmService algorithmService;
//
// //
// @Autowired
// private ReportPeriodService reportPeriodService;
//
// //
// @Autowired
// private ConfigService configService;
//
// //
// @Autowired
// private ReportPushService reportPushService;
//
// //
// @Autowired
// private ReportService reportService;
//
// @Autowired
// private MessageWebsocket websocket;
//
// @Autowired
// private ReportWebsocket reportWebsocket;
//
// @Autowired
// private LocationService locationService;
//
// @Autowired
// private SmsPhoneService smsPhoneService;
//
// @Autowired
// private AppPushController appPushController;
//
// @Autowired
// private AlarmLevelService alarmLevelService;
//
// @Autowired
// private VoicePhoneService voicePhoneService;
//
// /**
// *
// * @param cameraId
// * @param algorithmId
// * @param fileName
// * @param params
// * @return
// */
// @RequestMapping({"","/"})
// @ResponseBody
// public JsonResult report(@RequestParam(value ="camera_id", required = false) Long cameraId,
// @RequestParam(value ="algorithm_id", required = false) Long algorithmId,
// @RequestParam(value ="file_name", required = false) String fileName,
// @RequestParam(value ="params", required = false) String params,
// @RequestParam(value ="file", required = false) MultipartFile file,
// HttpServletRequest request) {
// try {
// // System.out.println("report api params");
// // System.out.println("cameraId:"+ cameraId);
// // System.out.println("algorithmId:"+ algorithmId);
// // System.out.println("fileName:"+ fileName);
// // System.out.println("params:"+ params);
//
// if (cameraId == null || algorithmId == null) {
// return JsonResultUtils.fail("camera_id or algorithm_id is null");
//}
//
// //
// Camera camera = cameraService.getById(cameraId);
// if (camera == null) {
// log.error("camera is null");
// return JsonResultUtils.fail("the camera is not found.");
//}
//
// //
// Algorithm algorithm = this.algorithmService.getById(algorithmId);
// if (algorithm == null) {
// log.error("algorithm is null");
// return JsonResultUtils.fail("the algorithm is not found.");
//}
//
// // Yinchuan Coal Mine, Each day only Alert One Data
// if ("Inspection Recognition".equals(algorithm.getName())) {
// Date startDate = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH);
// Date endDate = DateUtil.truncate(DateUtil.offsetDay(new Date(), 1), DateField.DAY_OF_MONTH);
// // Area between Time Segment inner Alert Count super over 1 rule not Use again sub Alert
// int reportCount = reportService.findReportCountByDate(cameraId, algorithmId, startDate.getTime(),
// endDate.getTime());
// if (reportCount > 0) {
// return JsonResultUtils.success();
//}
//}
//
// //
// boolean isDiscard = false; // ReportDiscard.getInst().isFilter(cameraId, algorithmId, params);
// if (isDiscard) {
// log.info("Report Discard: {}, {}, {}", cameraId, algorithmId, params);
// return JsonResultUtils.success();
//}
//
// //
// // String algorithmName = algorithm.getName();
// // boolean isFilter = (algorithmName.contains("Fall") || algorithmName.contains("Smoking") ||
// // algorithmName.contains("Accumulate Water") || algorithmName.contains("Smoking"));
//
// // What all not Manage, First Message Push out Remove -- Temp Hour not Ground Method show show
// // try {
// // // if(!isFilter) {
// // ReportMessage reportMessage = new ReportMessage();
// // reportMessage.setType("REPORT");
// // reportMessage.setCameraId(String.valueOf(cameraId));
// // reportMessage.setParams(params);
// //// reportMessage.setCameraName(camera.getName());
// //// reportMessage.setAlgorithmName(algorithm.getName());
// //// reportMessage.setAlarmTime(DateUtil.format(new Date(),"yyyy-MM-dd MM:ss"));
// // reportWebsocket.sendToAll(JSON.toJSONString(reportMessage));
// // //}
// //} catch (Exception e) {
// // //
// // log.error("report message exception: {}", e.getMessage());
// //}
//
// // Alert Time Segment Determine
// boolean inPeriod = false;
// Integer period = Integer.valueOf(DateUtil.format(new Date(),"HHmm"));
// List<ReportPeriod> reportPeriodList = reportPeriodService.listData(cameraId, algorithmId);
// if (reportPeriodList.isEmpty()) {
// inPeriod = true;
//} else {
// for (ReportPeriod reportPeriod: reportPeriodList) {
// if (reportPeriod.getStartTime() <= period && period <= reportPeriod.getEndTime()) {
// inPeriod = true;
// break;
//}
//}
//}
//
// // not In Alert Hour Segment
// if (!inPeriod) {
// log.info("not In Alert Hour Segment {}, {}, {}, {}", camera.getName(), algorithm.getName(), fileName, params);
// return JsonResultUtils.success("not In Alarm Time Segment inner");
//}
//
// //
// Report report = new Report();
// report.setCameraId(cameraId);
// report.setAlgorithmId(algorithmId);
// report.setType(ReportType.AI.getType());
// report.setFileName(fileName);
// report.setParams(params);
// report.setCreatedAt(new Date());
// report.setCreatedMills(System.currentTimeMillis());
// report.setAuditResult(0);
// report.setAuditState(0);
// int display = 0;
//
// // By Camera Alert Interval Process
// Float intervalTime = camera.getAlarmInterval();
// if (intervalTime!= null && intervalTime > 0) {
// Report last = this.reportService.findLast(cameraId, algorithmId);
// if (last!= null) {
// if ((System.currentTimeMillis() - last.getCreatedMills()) < intervalTime * 1000) {
// display = 1;
//}
//}
//}
//
// //
// /*
// * if(isFilter) {log.info("Filter Type Alert {}, {}, {}, {}", camera.getName(), algorithm.getName(), fileName,
// * params); display = 1; report.setDisplay(1); // not Display}
// */
//
// //
// if (display == 0) {
// // Storage Image
// if (file!= null) {
// try {
// // Storage Directory
// File tar = new File(FileUtil.getParent(fileName, 1));
// if (!tar.exists()) {
// tar.mkdirs();
//}
// // Storage File
// file.transferTo(new File(fileName));
//} catch (Exception e) {
// log.error("Upload Image Exception:{}", e.getMessage());
//}
//}
//
// // display = 1 Data all Lose Abandon Drop
// report.setDisplay(display);
// this.reportService.save(report);
//
// // Push Data to Frontend Display
// String wareHouseName ="-";
// Long wareHouseId = camera.getLocationId();
// if (wareHouseId!= null && wareHouseId!= 0) {
// Location wareHouse = locationService.getById(wareHouseId);
// if (wareHouse!= null) {
// wareHouseName = wareHouse.getName();
//}
//}
//
// // Wait File Storage Complete Finish
// waitForFile(fileName);
//
// //
// ReportMessage reportMessage = new ReportMessage();
// reportMessage.setType("REPORT_SHOW");
// reportMessage.setCameraId(String.valueOf(cameraId));
// reportMessage.setAlgorithmId(String.valueOf(algorithm.getId()));
// reportMessage.setParams(params);
// reportMessage.setCameraName(camera.getName());
// reportMessage.setAlgorithmName(algorithm.getName());
// reportMessage.setAlgorithmNameEn(algorithm.getNameEn());
// reportMessage.setAlarmTime(DateUtil.format(new Date(),"MM-dd MM:ss"));
// reportMessage.setWareName(wareHouseName);
// reportMessage.setId(String.valueOf(report.getId()));
// reportWebsocket.sendToAll(JSON.toJSONString(reportMessage));
// // app Push
// appPushController.sendReportToAll(reportMessage);
//
// //
// Message messageVo = new Message();
// messageVo.setType(MessageType.REPORT.getType());
// messageVo.setContent(camera.getName() +"out current"+ algorithm.getName() +"Alert");
//
// Map<String, Object> dataMap = new HashMap<>();
// dataMap.put("reportId", report.getId());
// dataMap.put("cameraName", camera.getName());
// messageVo.setData(dataMap);
//
// ObjectMapper objectMapper = new ObjectMapper();
// SimpleModule simpleModule = new SimpleModule();
// simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
// simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
// objectMapper.registerModule(simpleModule);
// String voJson = objectMapper.writeValueAsString(messageVo);
// websocket.sendToAll(voJson);
//
// // Push Third Party
// String reportPushUrl = configService.getByValTag("reportPushUrl");
// String reportPushImage = configService.getByValTag("reportPushImage");
// if (StrUtil.isNotBlank(reportPushUrl)) {
// try {
// JSONObject reportMap = new JSONObject();
// reportMap.put("cmpn_cd","TLB");
// reportMap.put("camera_id", String.valueOf(cameraId));
// reportMap.put("camera_name", camera.getName());
// reportMap.put("algorithm_id", String.valueOf(algorithmId));
// reportMap.put("algorithm_name", algorithm.getName());
// reportMap.put("level","F");
// reportMap.put("img_path", report.getFileName());
// reportMap.put("img_ext", FileUtil.extName(report.getFileName()));
// reportMap.put("img_name", FileUtil.getName(report.getFileName()));
// reportMap.put("alarm_dt", DateUtil.format(new Date(),"MM/dd HH:mm"));
// reportMap.put("report_id", String.valueOf(report.getId()));
// reportMap.put("params", params);
// reportMap.put("webUrl", configService.getByValTag("webUrl"));
//
// // imageBase64 this Data Make Complete Config style, Because for Need Consume Consume Time
// boolean toBase64 = false;
// if (reportPushImage!= null &&"true".equals(reportPushImage)) {
// toBase64 = true;
//}
//
// reportPushService.request(reportPushUrl, reportMap, toBase64, fileName);
//} catch (Exception e) {
// e.printStackTrace();
//}
//} else {
// // log.info("not Push Alert, not has Config Push Address {}, {}, {}, {}", camera.getName(), algorithm.getName(), fileName,
// // params);
//}
//
// // Push SMS
// Long alarmLevelId = algorithm.getAlarmLevelId();
// Map<Long, AlarmLevel> levelMap = alarmLevelService.getDataMap();
// AlarmLevel alarmLevel = levelMap.get(alarmLevelId);
// if (alarmLevel!= null && alarmLevel.getShowTypes()!= null && alarmLevel.getShowTypes().size() > 0) {
// if (alarmLevel.getShowTypes().contains("sms")) {
// String smsEnable = configService.getByValTag("smsEnable");
// // log.info("SMS Send ID: {}", smsEnable);
// if (StrUtil.isNotBlank(smsEnable) &&"true".equals(smsEnable)) {
// String mobiles = smsPhoneService.listPhoneStr("test");
// // log.info("SMS Send No code:{}", mobiles);
// if (StrUtil.isNotBlank(mobiles)) {
// String smsAppKey = configService.getByValTag("smsAppKey");
// String smsTplId = configService.getByValTag("smsTplId");
// if (StrUtil.isNotBlank(smsAppKey) && StrUtil.isNotBlank(smsTplId)) {
// SendSmsUtil.send(mobiles, camera.getName(), algorithm.getName(), smsAppKey,
// smsTplId);
//} else {
// // log.info("SMS Config lack Missing, appKey: {}, tplId: {}", smsAppKey, smsTplId);
//}
//}
//}
//}
//}
// if (alarmLevel.getShowTypes().contains("we_work")) {
// // Push to WeWork Group Bot
// String weworkEnable = configService.getByValTag("weworkEnable");
// // log.info("WeWork ID: {}", weworkEnable);
// if (StrUtil.isNotBlank(weworkEnable) &&"true".equals(weworkEnable)) {
// String weworkUrl = configService.getByValTag("weworkUrl");
// String webUrl = configService.getByValTag("webUrl");
// // log.info("WeWork Group Address: {}", weworkUrl);
// // log.info("WeWork /web Address: {}", webUrl);
// if (StrUtil.isNotBlank(weworkUrl) && StrUtil.isNotBlank(webUrl)) {
// String clickUrl = webUrl +"/report/detail?id="+ report.getId();
// String picUrl = webUrl +"/report/stream?id="+ report.getId();
// WeWorkRobotSendUtils.sendTextAndImage(weworkUrl,
// camera.getName() +"#"+ algorithm.getName()
// +"# Alert,"+ DateUtil.format(new Date(),"MM/dd HH:mm"),
// null, clickUrl, picUrl);
//}
//}
//}
// if (alarmLevel.getShowTypes().contains("dingding")) {
// // Push to DingTalk Group Bot
// String dingdingEnable = configService.getByValTag("dingdingEnable");
// if ("true".equals(dingdingEnable)) {
// String webhook = configService.getByValTag("dingdingUrl");
// String webUrl = configService.getByValTag("webUrl");
// String sign = configService.getByValTag("dingdingSign");
// if (StrUtil.isNotBlank(webhook) && StrUtil.isNotBlank(webUrl)) {
// String clickUrl = webUrl +"/report/detail?id="+ report.getId();
// String picUrl = webUrl +"/report/stream?id="+ report.getId();
// DingdingRobotSendUtils.send(webhook, sign,"Monitor Alert",
// camera.getName() +"#"+ algorithm.getName()
// +"# Alert,"+ DateUtil.format(new Date(),"MM/dd HH:mm"),
// picUrl, clickUrl);
//}
//}
//}
//
// if (alarmLevel.getShowTypes().contains("voice")) {
// // Voice Push Notification
// String voiceEnable = configService.getByValTag("voiceEnable");
// System.out.println("voiceEnable:"+ voiceEnable);
// if ("true".equals(voiceEnable)) {
// String voiceAppId = configService.getByValTag("voiceAppId");
// String voiceAppSecret = configService.getByValTag("voiceAppSecret");
// String voiceTemplateId = configService.getByValTag("voiceTemplateId");
// // Query Phone and Filter select out Match Alert Level
// List<VoicePhone> voicePhoneList = voicePhoneService.list();
// List<String> mobiles = new ArrayList<String>();
// for (VoicePhone voicePhone: voicePhoneList) {
// if (voicePhone.getLevelId().equals(alarmLevelId)) {
// mobiles.add(voicePhone.getPhone());
//}
//}
// if (mobiles!= null && mobiles.size() > 0) {
// SendVoiceUtil.sendAsync(
// mobiles, voiceTemplateId,
// algorithm.getName()
// +", at"+ DateUtil.format(new Date(),"yyyy Year MM Month dd Day HH Hour mm part ss s")
// +"send produce"+ algorithm.getName(),
// voiceAppId, voiceAppSecret);
//}
//}
//}
//}
//
// return JsonResultUtils.success();
//} catch (Exception e) {
// log.error("Call Alert Report API Exception {}", e.getMessage());
// return JsonResultUtils.fail(e.getMessage());
//}
//}
//
// /**
// * Algorithm Storage Image for Async, The with must etc Image Complete Complete Storage Talent Push to Frontend
// *
// * @param filepath
// */
// private void waitForFile(String filepath) {
// try {
// // Determine long Degree Whether Change
// int count = 0;
// long fileLen = -1;
// while (true) {
// count++;
// File file = new File(filepath);
// if (file.exists()) {
// if (file.length() > fileLen) {
// fileLen = file.length();
//} else {
// break;
//}
//}
//
// //
// Thread.sleep(10);
//
// //
// if (count >= 50) {
// break;
//}
//}
//} catch (Exception e) {
// //
//}
//}
//
// /**
// * Upload File
// *
// * @param args
// */
// @PostMapping("/upload")
//
// public static void main(String[] args) {
// // JSONObject reportMap = new JSONObject();
// // reportMap.put("cmpn_cd","TLB");
// // reportMap.put("camera_id", String.valueOf(1591638383556427777L));
// // reportMap.put("camera_name","big Hall");
// // reportMap.put("algorithm_id", String.valueOf(1591715452315369474L));
// // reportMap.put("algorithm_name","Vest Recognition");
// // reportMap.put("level","F");
// // reportMap.put("img_path",
// //"/data/camera/2022/12/28/1591638383556427777/1591715452315369474-be9037a8-7d00-11ed-b3ab-581122ab15a1.jpg");
// // reportMap.put("img_ext","jpg");
// // reportMap.put("img_name","1591715452315369474-be9037a8-7d00-11ed-b3ab-581122ab15a1.jpg");
// // reportMap.put("alarm_dt","2022-12-28 11:51:17");
// // reportMap.put("report_id","1607947266406273025");
// // reportMap.put("params","[{\"type\": \"no-vest\", \"position\": [806, 552, 873, 724], \"confidence\":
// // 0.85}]");
// //
// // ReportPushService service = new ReportPushService();
// // service.request("https://uataiplat.mapfarm.com/api/v1/monitor/discern/handle", reportMap, true,
// //"e:/test1.jpg");
// //
// // reportMap.remove("imageBase64");
// // System.out.println(reportMap.toJSONString());
//
// String p = FileUtil.getParent("/data/2018/4/12/34/193933.jpg", 1);
// System.out.println(p);
//}
//}