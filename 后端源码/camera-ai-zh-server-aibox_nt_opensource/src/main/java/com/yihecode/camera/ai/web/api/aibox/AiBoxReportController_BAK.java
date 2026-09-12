//package com.yihecode.camera.ai.web.api.aibox;
//
//import cn.dev33.satoken.annotation.SaIgnore;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.yihecode.camera.ai.config.ProjectConfig;
//import com.yihecode.camera.ai.entity.*;
//import com.yihecode.camera.ai.enums.MessageType;
//import com.yihecode.camera.ai.enums.ReportType;
//import com.yihecode.camera.ai.notify.dingding.DingdingRobotSendUtils;
//import com.yihecode.camera.ai.notify.sms.SendSmsUtil;
//import com.yihecode.camera.ai.notify.voice.SendVoiceUtil;
//import com.yihecode.camera.ai.notify.wework.WeWorkRobotSendUtils;
//import com.yihecode.camera.ai.service.*;
//import com.yihecode.camera.ai.utils.JsonResult;
//import com.yihecode.camera.ai.utils.JsonResultUtils;
//import com.yihecode.camera.ai.utils.SoundColumnUtils;
//import com.yihecode.camera.ai.vo.Message;
//import com.yihecode.camera.ai.vo.ReportMessage;
//import com.yihecode.camera.ai.web.api.aibox.vo.ReportVo;
//import com.yihecode.camera.ai.web.api.comm.AlarmThirdPushService;
//import com.yihecode.camera.ai.web.api.comm.AlarmVoicePushService;
//import com.yihecode.camera.ai.web.app.push.AppPushController;
//import com.yihecode.camera.ai.websocket.MessageWebsocket;
//import com.yihecode.camera.ai.websocket.ReportWebsocket;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import springfox.documentation.annotations.ApiIgnore;
//
//import javax.annotation.Resource;
//import java.io.File;
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * Alert Push Data, Integrate Algorithm Push Alert Data
// *
// * @author zhoumingxing
// * @mail 465769438@qq.com
// */
//@ApiIgnore
//@SaIgnore
//@Api(tags ="Edge Box Alarm Report Management")
//@Slf4j
//@RestController
//@RequestMapping({"/api/aibox/report"})
//public class AiBoxReportController_BAK {
//
// @Resource
// private CameraService cameraService;
//
// @Resource
// private AlgorithmService algorithmService;
//
// @Resource
// private ReportPeriodService reportPeriodService;
//
// @Resource
// private ConfigService configService;
//
// @Resource
// private ReportService reportService;
//
// @Resource
// private MessageWebsocket websocket;
//
// @Resource
// private ReportWebsocket reportWebsocket;
//
// @Resource
// private LocationService locationService;
//
// @Resource
// private SmsPhoneService smsPhoneService;
//
// @Resource
// private CameraAlgorithmService cameraAlgorithmService;
//
// @Autowired
// private AlarmLevelService alarmLevelService;
//
// @Autowired
// private VoicePhoneService voicePhoneService;
//
// @Autowired
// private SoundColumnService soundColumnService;
//
// @Autowired
// private AppPushController appPushController;
//
// @Autowired
// private AccountService accountService;
//
// @Autowired
// private AlgorithmAlarmLevelService algorithmAlarmLevelService;
//
// @Autowired
// private AlarmThirdPushService alarmThirdPushService;
//
// @Autowired
// private AlarmVoicePushService alarmVoicePushService;
//
// @Autowired
// private ProjectConfig projectConfig;
//
// @Value("${cameraDir}")
// private String cameraDir;
//
// @Value("${dataModelsDir}")
// public String MODEL_DIR;
//
// /**
// * Speaker Pole Data Send Thread
// */
// @ApiOperation("Edge Box Alarm Report")
// @PostMapping({"","/"})
// public JsonResult<?> report(ReportVo reportVo) {
// long t = System.currentTimeMillis();
// try {
// // Param Check
// if (reportVo.getCameraId() == null || StrUtil.isBlank(reportVo.getParams()) || reportVo.getFile() == null) {
// //log.error("Edge Box Report Data Exception: cameraId {}, params: {}, file not exist?: {}", reportVo.getCameraId(), reportVo.getParams(), reportVo.getFile() == null);
// return JsonResultUtils.fail("Data Error");
//}
//
// // Query Camera
// Camera camera = cameraService.getById(reportVo.getCameraId());
// if (camera == null) {
// log.error("Edge Box Report Exception: Camera does not exist {}", reportVo.getCameraId());
// return JsonResultUtils.fail("Camera does not exist");
//}
//
// // Alert Time
// /* remove 2024-10-11 out current Alarm Time Away Odd out current far big at Current Date, First Note Sell Test */
// Date reportTime = new Date();
// if (StrUtil.isNotBlank(reportVo.getTimestamp())) {
// try {
// reportTime = DateUtil.parse(reportVo.getTimestamp(),"yyyy-MM-dd HH:mm:ss");
//
// int result = DateUtil.compare(reportTime, new Date());
// if(result > 0) {
// reportTime = new Date();
//}
//} catch (Exception e) {
// //
//}
//}
//
// // Query Relate Algorithm
// List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByCamera(camera.getId());
// if (cameraAlgorithmList.isEmpty()) {
// //log.error("Camera no Relate Algorithm {}", reportVo.getCameraId());
// return JsonResultUtils.fail("Camera no Relate Algorithm");
//}
// Map<Long, CameraAlgorithm> cameraAlgorithmMap = cameraAlgorithmList.stream().collect(Collectors.toMap(CameraAlgorithm::getAlgorithmId, Function.identity(), (n1, n2) -> n1));
//
// // Query Algorithm
// Map<Long, Algorithm> algorithmMap = new HashMap<>();
// for(CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
// Algorithm algorithm = algorithmService.getById(cameraAlgorithm.getAlgorithmId());
// if(algorithm == null) {
// continue;
//}
//
// algorithm.setCameraConfidence(cameraAlgorithm.getConfidence() == null? 0.3f: cameraAlgorithm.getConfidence());
// algorithmMap.put(algorithm.getId(), algorithm);
//}
//
// // Parse Data
// long t1 = System.currentTimeMillis();
//
// // Collect Data
// Map<Long, List<JSONObject>> collectDatas = new HashMap<>();
//
// // Alarm Data
// Map<Long, List<JSONObject>> algoDatas = new HashMap<>();
//
// // Parse Data
// JSONArray datas = JSON.parseArray(reportVo.getParams());
// int dlen = datas.size();
// for (int i = 0; i < dlen; i++) {
// JSONObject data = datas.getJSONObject(i);
//
// // Get Algorithm ID
// Long algorithmId = data.getLong("algoId"); // Algorithm ID
//
// if(!algorithmMap.containsKey(algorithmId)) {
// continue;
//}
//
// Algorithm algorithm = algorithmMap.get(algorithmId);
//
// // Validate Alert Interval
// if (camera.getAlarmInterval() == null || camera.getAlarmInterval() <= 0) {
// //log.info("Alert Interval Time Error: {}", camera.getAlarmInterval());
// continue;
//}
//
// // Validate Alert Interval
// Long lastTime = AiBoxReportTime.getInst().get(reportVo.getCameraId() +"-"+ algorithmId);
// if ((System.currentTimeMillis() - lastTime) < camera.getAlarmInterval() * 1000) {
// //log.info("small at Alert Interval Time: {}, {}", camera.getId(), algorithmId);
// continue;
//}
//
// // Validate Relate Property
//// CameraAlgorithm cameraAlgorithm = cameraAlgorithmMap.get(algorithmId);
//// if (cameraAlgorithm == null) {
//// // log.error("find not to Camera Relate Algorithm Config: algorithmId: {}", algorithmId);
//// continue;
////}
//
// // Validate Confidence
// double conf = data.getDouble("confidence"); // Confidence
//
// // Collect Data
// if(algorithm.getCollectFlag()!= null && algorithm.getCollectFlag() == 1 && algorithm.getCollectConfidence()!= null) {
// if(algorithm.getCollectEndTime() > reportTime.getTime()) {
// if(conf >= algorithm.getCollectConfidence()) {
// if(collectDatas.containsKey(algorithmId)) {
// List<JSONObject> collectList = collectDatas.get(algorithmId);
// collectList.add(data);
//} else {
// List<JSONObject> collectList = new ArrayList<>();
// collectList.add(data);
// collectDatas.put(algorithmId, collectList);
//}
//}
//}
//}
//
// // low at Camera Set Confidence
// float presetConf = algorithm.getCameraConfidence();
// if (conf < presetConf) {
// // log.info("Confidence Error Config Value {}, transmit deliver Value {}", presetConf, conf);
// continue;
//}
//
// // Validate Alert Hour Segment
// if (!isInPeriod(camera.getId(), algorithmId)) {
// // log.info("not In Alert Hour Segment: {}, {}", camera.getId(), algorithmId);
// continue;
//}
//
// // Temp Hour By Algorithm ID Storage Data
// if (algoDatas.containsKey(algorithmId)) {
// List<JSONObject> algoDataList = algoDatas.get(algorithmId);
// algoDataList.add(data);
// algoDatas.put(algorithmId, algoDataList);
//} else {
// List<JSONObject> algoDataList = new ArrayList<>();
// algoDataList.add(data);
// algoDatas.put(algorithmId, algoDataList);
//}
//}
//
// // not has Alarm Data
// if (algoDatas.isEmpty() && collectDatas.isEmpty()) {
// return JsonResultUtils.fail("no Alarm Data, or Filter");
//}
//
// // Storage Image
// long t2 = System.currentTimeMillis();
// String fileName = null;
// try {
// Calendar calendar = Calendar.getInstance();
// String saveDest = String.format("%s/%s/%s/%s/%s/", cameraDir, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), reportVo.getCameraId());
// FileUtil.mkdir(saveDest);
// String saveFile = saveDest + IdUtil.fastSimpleUUID() +".jpg";
// reportVo.getFile().transferTo(new File(saveFile));
// fileName = saveFile;
//} catch (Exception e) {
// log.error("Upload Image Exception:{}", e.getMessage());
//}
// log.info("Report Image Storage Time: {}", (System.currentTimeMillis() - t2));
//
// // Query belong belong Box
// Location location = locationService.getById(camera.getLocationId());
//
// // receive Set Data Save
// for(Map.Entry<Long, List<JSONObject>> entry: collectDatas.entrySet()) {
// // Get Algorithm ROI
// String imagePoints ="";
// String linePoints ="";
// CameraAlgorithm cameraAlgorithm = cameraAlgorithmMap.get(entry.getKey());
// if (cameraAlgorithm!= null) {
// imagePoints = cameraAlgorithm.getImagePoints();
// linePoints = cameraAlgorithm.getLineImagePoints();
//
// imagePoints = imagePoints == null?"": imagePoints;
// linePoints = linePoints == null?"": linePoints;
//}
//
// // Storage Alert
// Report report = new Report();
// report.setCameraId(camera.getId());
// report.setAlgorithmId(entry.getKey());
// report.setType(ReportType.AI.getType());
// report.setFileName(fileName);
// report.setParams(JSON.toJSONString(entry.getValue()));
// report.setCreatedAt(reportTime);
// report.setCreatedMills(reportTime.getTime());
// report.setAuditResult(0);
// report.setAuditState(projectConfig.isAlarmAutoAudit()? 1: 0);
// report.setDisplay(8); // Collect Data receive Set, not Direct connect Display In Alarm page Surface
// report.setBoxId(location == null? 0L: location.getId());
// report.setDepartId(location == null? 0L: (location.getDepartId() == null? 0L: location.getDepartId()));
// report.setRois(imagePoints);
// report.setLines(linePoints);
// this.reportService.save(report);
//}
//
// // no Alarm
// if(algoDatas.isEmpty()) {
// return JsonResultUtils.success();
//}
//
// // Query Algorithm List
// //Map<Long, Algorithm> algorithmMap = algorithmService.getDataMap();
//
// // Query Algorithm Alert Level
// Map<Long, AlarmLevel> alarmLevelMap = alarmLevelService.getDataMap();
//
// // form Algorithm Parse
// for (Long algorithmId: algoDatas.keySet()) {
// long t3 = System.currentTimeMillis();
// // Query Algorithm Config
// Algorithm algorithm = algorithmMap.get(algorithmId);
//
// // Parse Data, and Record
// List<JSONObject> algoData = algoDatas.get(algorithmId);
// String algorithmJson = JSON.toJSONString(algoData);
//
// // Get Algorithm ROI
// String imagePoints ="";
// String linePoints ="";
// CameraAlgorithm cameraAlgorithm = cameraAlgorithmMap.get(algorithmId);
// if (cameraAlgorithm!= null) {
// imagePoints = cameraAlgorithm.getImagePoints();
// linePoints = cameraAlgorithm.getLineImagePoints();
//
// imagePoints = imagePoints == null?"": imagePoints;
// linePoints = linePoints == null?"": linePoints;
//}
//
// // Storage Alert
// Report report = new Report();
// report.setCameraId(camera.getId());
// report.setAlgorithmId(algorithmId);
// report.setType(ReportType.AI.getType());
// report.setFileName(fileName);
// report.setParams(algorithmJson);
// report.setCreatedAt(reportTime);
// report.setCreatedMills(reportTime.getTime());
// report.setAuditResult(projectConfig.isAlarmAutoAudit()? 1: 0);
// report.setAuditState(0);
// report.setDisplay(0);
// report.setBoxId(location == null? 0L: location.getId());
// report.setDepartId(location == null? 0L: (location.getDepartId() == null? 0L: location.getDepartId()));
// report.setRois(imagePoints);
// report.setLines(linePoints);
// this.reportService.save(report);
//
// // Record This sub Alert Time
// AiBoxReportTime.getInst().put(reportVo.getCameraId(), algorithmId);
//
// // Query the Box belong belong Department Corresponding Person member
// List<Account> accounts = accountService.listByDepartId(location == null? null: location.getDepartId());
//
// // Send web Frontend
// sendToWeb(camera, algorithm, location, report, accounts);
//
// // Send Third Party
// sendToThirdpart(camera, algorithm, location, report, algoData.size());
//
// // Send Speaker Pole Voice Play Put
//// sendToSoundColumn(camera, algorithm);
// List<Algorithm> algorithmList = algorithmService.list();
// if (algorithmList == null) {
// algorithmList = new ArrayList<>();
//}
//
// Map<Long, String> algorithmSoundFileMap = algorithmList.stream().filter(s -> s.getSoundFile()!= null).collect(Collectors.toMap(Algorithm::getId, Algorithm::getSoundFile));
//
// // Query Speaker Pole Config
// List<SoundColumn> soundColumnList = soundColumnService.list();
// if (soundColumnList == null) {
// soundColumnList = new ArrayList<>();
//}
//
// Map<Long, SoundColumn> soundColumnMap = soundColumnList.stream().collect(Collectors.toMap(SoundColumn::getId, v -> v, (key1, key2) -> key1));
//
// sendToSoundColumn(algorithmId, algorithmSoundFileMap.get(algorithmId), camera.getSoundColumnId(), soundColumnMap);
//
// // part Account Determine Send SMS, WeWork, DingTalk etc
// for (Account account: accounts) {
// Long alarmLevelId = algorithmAlarmLevelService.getLevelIdByAlgorithmIdAndAccountId(algorithmId, account.getId());
// AlarmLevel alarmLevel = alarmLevelMap.get(alarmLevelId);
// if (alarmLevel!= null && alarmLevel.getShowTypes()!= null &&!alarmLevel.getShowTypes().isEmpty()) {
// // Send SMS
// if (alarmLevel.getShowTypes().contains("sms")) {
// sendToSms(camera, algorithm, account);
//}
// // Send WeWork
// if (alarmLevel.getShowTypes().contains("we_work")) {
// sendToWechat(camera, algorithm, report, account);
//}
// // Send DingTalk Group
// if (alarmLevel.getShowTypes().contains("dingding")) {
// sendToDingding(camera, algorithm, report, account);
//}
// // Send Voice Play Report
// if (alarmLevel.getShowTypes().contains("voice")) {
// sendToVoice(camera, algorithm, report, account, alarmLevelId);
//}
//}
//}
// log.info("Report form Data Save Alert, Push, WeChat Push etc etc Process Time: {}", (System.currentTimeMillis() - t3));
//}
//} catch (Exception e) {
// log.error("Common Algorithm Report Exception: {}", e.getMessage());
//}
// log.info("Report Consume Consume Time: {}", (System.currentTimeMillis() - t));
// return JsonResultUtils.success();
//}
//
// /**
// * Whether In Alert Hour Segment
// *
// * @param cameraId
// * @param algorithmId
// * @return
// */
// private boolean isInPeriod(Long cameraId, Long algorithmId) {
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
// // not In Alert Hour Segment
// return inPeriod;
//}
//
// /**
// * Push Message to Frontend
// *
// * @param camera
// * @param algorithm
// * @param location
// * @param report
// */
// private void sendToWeb(Camera camera, Algorithm algorithm, Location location, Report report, List<Account> accounts) {
// try {
// // Push Alert Detail (Detail, can show show Image)
// ReportMessage reportMessage = new ReportMessage();
// reportMessage.setType("REPORT_SHOW");
// reportMessage.setCameraId(String.valueOf(camera.getId()));
// reportMessage.setCameraName(camera.getName());
// reportMessage.setAlgorithmId(String.valueOf(algorithm.getId()));
// reportMessage.setAlgorithmName(algorithm.getName());
//
// reportMessage.setAlgorithmNameEn(algorithm.getNameEn());
// reportMessage.setParams(report.getParams());
// reportMessage.setAlarmTime(DateUtil.format(report.getCreatedAt(),"MM-dd HH:mm:ss"));
// reportMessage.setWareName(location == null?"": location.getName());
// reportMessage.setId(String.valueOf(report.getId()));
//
// // Push to Frontend
// log.info("send to accounts {}, {}", accounts, JSON.toJSONString(reportMessage));
// reportWebsocket.sendToAccounts(accounts, JSON.toJSONString(reportMessage));
//
// // app Push
// if(projectConfig.isAppEnable()) {
// appPushController.sendReportToAll(reportMessage);
//}
//
// // Push Alert Notification (Simple form Info)
// Message messageVo = new Message();
// messageVo.setType(MessageType.REPORT.getType());
// messageVo.setContent(camera.getName() +"out current"+ algorithm.getName() +"Alert");
// Map<String, Object> dataMap = new HashMap<>();
// dataMap.put("reportId", report.getId());
// dataMap.put("cameraName", camera.getName());
// messageVo.setData(dataMap);
// websocket.sendToAll(JSON.toJSONString(messageVo));
//} catch (Exception e) {
// // e.printStackTrace();
//}
//}
//
// /**
// * Send to Third Party
// *
// * @param camera
// * @param algorithm
// * @param location
// * @param report
// */
// private void sendToThirdpart(Camera camera, Algorithm algorithm, Location location, Report report, int alarmCount) {
// if(!projectConfig.isAlarmAutoPush()) {
// return;
//}
// alarmThirdPushService.sendComm(camera, algorithm, location, report);
//}
//
// /**
// * Push to ip Speaker Pole Voice Play Report
// *
// * @param algorithmId
// * @param soundFile
// */
// @Async
// public void sendToSoundColumn(Long algorithmId, String soundFile, Long soundColumnId, Map<Long, SoundColumn> soundColumnMap) {
// if (StrUtil.isBlank(soundFile) || soundColumnId == null || soundColumnMap.get(soundColumnId) == null) {
// return;
//}
// //
// String path = MODEL_DIR +"/soundFile/"+ soundFile;
// File tar = new File(path);
// if (tar.exists() && tar.isFile()) {
// // Generate mp3 Address
// String ipAddr = configService.getByValTag("ipAddr");
// String mp3file ="http://"+ ipAddr +"/algorithm/sound/stream?id="+ algorithmId;
//
// //
// SoundColumn soundColumn = soundColumnMap.get(soundColumnId);
// if ("yuelang".equals(soundColumn.getType())) {
// SoundColumnUtils.sendYueLangPlay(soundColumn.getServer(), soundColumn.getSn(), mp3file,
// soundColumn.getUserName(), soundColumn.getPassword(), soundColumn.getVol());
//} else {
// SoundColumnUtils.sendPlay(soundColumn.getServer(), soundColumn.getSn(), mp3file);
// // this.soundColumnThread.putMp3(mp3file);
//}
//
//}
//}
//
// /**
// * Send SMS
// *
// * @param camera
// * @param algorithm
// * @param account
// */
// public void sendToSms(Camera camera, Algorithm algorithm, Account account) {
// String smsEnable = configService.getByValTag("smsEnable"+ account.getId());
// if ("true".equals(smsEnable)) {
// String mobiles = smsPhoneService.listByAccountIdStr(account.getId());
// if (StrUtil.isNotBlank(mobiles)) {
// String smsAppKey = configService.getByValTag("smsAppKey"+ account.getId());
// String smsTplId = configService.getByValTag("smsTplId"+ account.getId());
// if (StrUtil.isNotBlank(smsAppKey) && StrUtil.isNotBlank(smsTplId)) {
// SendSmsUtil.send(mobiles, camera.getName(), algorithm.getName(), smsAppKey, smsTplId);
//}
//}
//}
//}
//
// /**
// * Push to WeWork Group Bot
// * @param camera
// * @param algorithm
// */
// public void sendToWechat(Camera camera, Algorithm algorithm, Report report, Account account) {
// String weworkEnable = configService.getByValTag("weworkEnable"+ account.getId());
// if ("true".equals(weworkEnable)) {
// String weworkUrl = configService.getByValTag("weworkUrl"+ account.getId());
// String webUrl = configService.getByValTag("webUrl");
// if (StrUtil.isNotBlank(weworkUrl) && StrUtil.isNotBlank(webUrl)) {
// String clickUrl = webUrl +"/report/detail?id="+ report.getId();
// String picUrl = webUrl +"/report/stream?id="+ report.getId();
// String title ="Camera Name:"+ camera.getName();
// String description = String.format("Monitor Content:%s,\n Alarm Time:%s.", algorithm.getName(), DateUtil.format(report.getCreatedAt(),"yyyy Year MM Month dd Day HH Hour mm part ss s"));
// WeWorkRobotSendUtils.sendTextAndImage(weworkUrl, title, description, clickUrl, picUrl);
//}
//}
//}
//
// /**
// * Push to DingTalk Group Bot
// *
// * @param camera
// * @param algorithm
// * @param report
// * @param account
// */
// public void sendToDingding(Camera camera, Algorithm algorithm, Report report, Account account) {
// String dingdingEnable = configService.getByValTag("dingdingEnable"+ account.getId());
// if ("true".equals(dingdingEnable)) {
// String webhook = configService.getByValTag("dingdingUrl"+ account.getId());
// String webUrl = configService.getByValTag("webUrl");
// String sign = configService.getByValTag("dingdingSign"+ account.getId());
// if (StrUtil.isNotBlank(webhook) && StrUtil.isNotBlank(webUrl)) {
// String clickUrl = webUrl +"/report/detail?id="+ report.getId();
// String picUrl = webUrl +"/report/stream?id="+ report.getId();
// String title ="Camera Name:"+ camera.getName();
// String text = String.format("Monitor Content:%s,\n Alarm Time:%s.", algorithm.getName(), DateUtil.format(report.getCreatedAt(),"yyyy Year MM Month dd Day HH Hour mm part ss s"));
// DingdingRobotSendUtils.send(webhook, sign, title, text, picUrl, clickUrl);
//}
//}
//}
//
// /**
// * Voice Push Notification
// *
// * @param camera
// * @param algorithm
// * @param report
// * @param account
// * @param alarmLevelId
// */
// public void sendToVoice(Camera camera, Algorithm algorithm, Report report, Account account, Long alarmLevelId) {
// String voiceEnable = configService.getByValTag("voiceEnable"+ account.getId());
// if ("true".equals(voiceEnable)) {
// String voiceAppId = configService.getByValTag("voiceAppId"+ account.getId());
// String voiceAppSecret = configService.getByValTag("voiceAppSecret"+ account.getId());
// String voiceTemplateId = configService.getByValTag("voiceTemplateId"+ account.getId());
// // Query Phone and Filter select out Match Alert Level
// List<VoicePhone> voicePhones = voicePhoneService.listPhones(account.getId());
// // According to Alarm Level Filter select Send Phone
// List<String> mobiles = new ArrayList<>();
// for (VoicePhone voicePhone: voicePhones) {
// if (voicePhone.getLevelId().equals(alarmLevelId)) {
// mobiles.add(voicePhone.getPhone());
//}
//}
// // Send Voice
// if (!mobiles.isEmpty()) {
// String content = String.format("%s, at %s send produce %s Alarm", camera.getName(), DateUtil.format(report.getCreatedAt(),"yyyy Year MM Month dd Day HH Hour mm part ss s"), algorithm.getName());
// SendVoiceUtil.sendAsync(mobiles, voiceTemplateId, content, voiceAppId, voiceAppSecret);
//}
//}
//}
//}