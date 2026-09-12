//package com.yihecode.camera.ai.web;
//
//import cn.dev33.satoken.annotation.SaIgnore;
//import cn.hutool.core.date.DateField;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.http.HttpUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.yihecode.camera.ai.entity.*;
//import com.yihecode.camera.ai.entity.Record;
//import com.yihecode.camera.ai.enums.ReportType;
//import com.yihecode.camera.ai.isapi.ISAPIService;
//import com.yihecode.camera.ai.service.*;
//import com.yihecode.camera.ai.utils.FileUtils;
//import com.yihecode.camera.ai.utils.JsonResult;
//import com.yihecode.camera.ai.utils.JsonResultUtils;
//import com.yihecode.camera.ai.web.api.aibox.vo.CameraStatusSubVo;
//import com.yihecode.camera.ai.web.api.aibox.vo.CameraStatusVo;
//import com.yihecode.camera.ai.web.api.comm.AlarmVideoPushService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@SaIgnore
//@Slf4j
//@RestController
//@RequestMapping("mock")
//public class MockController {
//
// @Value("${mock:true}")
// private boolean mock;
//
// @Autowired
// private CameraService cameraService;
//
// @Autowired
// private CameraAlgorithmService cameraAlgorithmService;
//
// @Autowired
// private AlgorithmService algorithmService;
//
// @Autowired
// private ReportService reportService;
//
// @Autowired
// private LocationService locationService;
//
// @Autowired
// private ReportPeriodService reportPeriodService;
//
// @Autowired
// private ReportSummaryService reportSummaryService;
//
// @Autowired
// private ISAPIService isapiService;
//
// @Autowired
// private AlarmVideoPushService alarmVideoPushService;
//
//
// @Value("${uploadDir}")
// private String uploadDir;
//
// @Value("${temperature-alarm-callback.ip:}")
// private String hostIp;
//
// @Value("${temperature-alarm-callback.port:}")
// private String hostPort;
//
// @Value("${temperature-alarm-callback.url:}")
// private String hostUrl;
//
//
// @GetMapping("test_alarm_video_url_push")
// public JsonResult<?> test_alarm_video_url_push() {
// String x ="D:\\ZLMediaKit_Win\\Release\\www\\record\\rtp\\34020000001320000001_34020000001320000003\\2025-12-21\\00-01-35-0.mp4";
// File f = new File(x);
// Record record = new Record();
// record.setId(123L);
// record.setRecordPath("/123/4.mp4");
// record.setFileName("4.mp4");
// record.setFileSize(f.length());
//
// alarmVideoPushService.sendVideo(record, Arrays.asList(123L, 234L));
// return JsonResultUtils.success();
//}
//
// @PostMapping("test_alarm_video_url_data")
// public JsonResult<?> test_alarm_video_url_data(@RequestBody JSONObject object) {
// log.info("object={}", object);
// return JsonResultUtils.success();
//}
//
// @PostMapping("test_alarm_video_file_data")
// public JsonResult<?> test_alarm_video_file_data(@RequestParam(value ="videoFile") MultipartFile file, @RequestParam(value ="videoInfo") String videoInfo) {
// log.info("videoInfo={}", videoInfo);
//
// if(file == null) {
// log.info("videoFile is null");
//} else {
// log.info("videoFile {}", file.getOriginalFilename());
// log.info("{}", file);
//}
//
// return JsonResultUtils.success();
//}
//
// @GetMapping("test/tma")
// public JsonResult<?> testTmp(Long id) {
// Camera camera = cameraService.getById(id);
// if(camera == null) {
// return JsonResultUtils.fail("Camera does not exist");
//}
//
// Algorithm algorithm = algorithmService.getByNameEn("temperatureAlarm");
// if(algorithm == null) {
// return JsonResultUtils.fail("Temperature Alarm Algorithm does not exist");
//}
//
// CameraAlgorithm cameraAlgorithm = cameraAlgorithmService.getByCameraAndAlgorithmId(camera.getId(), algorithm.getId());
// if(cameraAlgorithm == null) {
// return JsonResultUtils.fail("Camera not has Config Temperature Alarm Algorithm, cameraName:"+ camera.getName());
//}
//
// boolean success = isapiService.setConfig(camera, hostIp, hostPort, hostUrl);
//
// return JsonResultUtils.success(success);
//}
//
//
// @GetMapping("test/camera/tma")
// public JsonResult<?> testCameraTMA() {
// Camera camera = new Camera();
// camera.setId(1L);
// camera.setName("tma");
// camera.setRtspUrl("rtsp://admin:hkws@1234@192.168.3.64/Streaming/Channels/101");
// isapiService.setConfig(camera,"192.168.3.121","8022","/mock/hot/camera");
// return JsonResultUtils.success();
//}
//
// @PostMapping("hot/camera")
// public JsonResult<?> testHotCamera(String TMA, MultipartFile visibleLightImage, HttpServletRequest request) throws IOException {
// log.info("Temperature Alarm File: {}", visibleLightImage);
// log.info("Temperature Alarm Data: {}", TMA);
//
// //visibleLightImage.transferTo(new File("d:/aaa.jpg"));
//
// Enumeration<String> names = request.getParameterNames();
// while(names.hasMoreElements()) {
// String name1 = names.nextElement();
//
// String x = request.getParameter("name");
// log.info("name: {}, x: {}", name1, x);
//
//}
//// log.info("");
//// log.info("");
// return JsonResultUtils.success();
//}
//
// @GetMapping("create")
// public JsonResult<?> mockBoxAndCamera(Integer bnum, Integer cnum) {
// if(!mock) {
// return JsonResultUtils.fail("Non mock Status");
//}
// if(bnum == null) {
// return JsonResultUtils.fail("bNum_ Box Count _ Param Is Empty");
//}
//
// if(cnum == null) {
// return JsonResultUtils.fail("cNum_ Camera Count _ Param Is Empty");
//}
//
// List<Algorithm> algorithms = algorithmService.list();
// if(algorithms == null || algorithms.isEmpty()) {
// return JsonResultUtils.success("not has can Use Algorithm");
//}
//
// int len = algorithms.size();
// if(len >= 3) {
// len = 3;
//}
//
// Location parent = locationService.getDefRoot(2);
// Long parentId = 0L;
// if(parent!= null) {
// parentId = parent.getId();
//}
//
// for(int i = 0; i < bnum; i++) {
// String lr = DateUtil.format(new Date(),"mmss");
// Location location = new Location();
// location.setName("Test_"+ lr + i);
// location.setPlatform("chaoxing");
// location.setBoxHeartTime(System.currentTimeMillis());
// location.setBoxNo(IdUtil.randomUUID());
// location.setSort(0);
// location.setLocationType("2");
// location.setType("2");
// location.setIpAddr("127.0.0.1");
// location.setIsDef(0);
// location.setParentId(parentId);
// location.setActiveStatus(1);
// locationService.save(location);
//
//
// for(int j = 0; j < cnum; j++) {
// String cr = DateUtil.format(new Date(),"mmss");
// Camera camera = new Camera();
// camera.setName("Camera_"+ cr + j);
// camera.setRtspUrl("rtsp://admin:hkws@1234@192.168.3.11/Streaming/Channels/101");
// camera.setAction(1);
// camera.setCreatedAt(new Date());
// camera.setState(0);
// camera.setRunning(0);
// camera.setIntervalTime(15f);
// camera.setAlarmInterval(15f);
// camera.setRtspType(0);
// camera.setLocationId(1943177987714510849L);
// camera.setLocationIds("null/1943177987714510849");
// camera.setLocationType("2");
// camera.setAiboxExecStatus(3000);
// camera.setAiboxExecTime(new Date());
// camera.setActionCounter(1);
// camera.setVideoCodec("H264");
// camera.setVideoFps(15);
// camera.setSoundColumnId(0L);
// camera.setAiboxExecMsg("Test Data");
// camera.setSourceType(0);
// camera.setMediaServerId(0L);
// cameraService.save(camera);
//
// Collections.shuffle(algorithms);
//
// for(int k = 0; k < len; k++) {
// Algorithm algorithm = algorithms.get(k);
//
// CameraAlgorithm cameraAlgorithm = new CameraAlgorithm();
// cameraAlgorithm.setAlgorithmId(algorithm.getId());
// cameraAlgorithm.setCameraId(camera.getId());
// cameraAlgorithm.setConfidence(0.5f);
// cameraAlgorithmService.save(cameraAlgorithm);
//
// ReportPeriod reportPeriod = new ReportPeriod();
// reportPeriod.setCameraId(camera.getId());
// reportPeriod.setAlgorithmId(algorithm.getId());
// reportPeriod.setStartTime(0);
// reportPeriod.setStartText("00:00");
// reportPeriod.setEndTime(2355);
// reportPeriod.setEndText("23:55");
// reportPeriodService.save(reportPeriod);
//}
//}
//}
//
// return JsonResultUtils.success();
//}
//
//
// @GetMapping("report")
// @ResponseBody
// public JsonResult<?> mockReport(String date) {
// if(!mock) {
// return JsonResultUtils.fail("Non mock Status");
//}
// Date date1 = new Date();
// if(StrUtil.isNotBlank(date)) {
// try {
// date1 = DateUtil.parse(date,"yyyyMMddHHmmss");
//} catch (Exception e) {
// return JsonResultUtils.fail("Time Format Error, Correct Format:yyyyMMddHHmmss");
//}
//}
//
// List<Camera> cameraList = cameraService.listData();
// if(cameraList == null || cameraList.isEmpty()) {
// return JsonResultUtils.fail("not has Camera");
//}
//
// List<Algorithm> algorithmList = algorithmService.list();
// if(algorithmList == null || algorithmList.isEmpty()) {
// return JsonResultUtils.fail("not has Algorithm");
//}
//
// List<Map<String, Object>> params = new ArrayList<>();
// Map<String, Object> p = new HashMap<>();
// p.put("type","fire");
// p.put("position", Arrays.asList(200, 200, 400, 400));
// p.put("confidence","0.3");
// params.add(p);
//
// int len = Math.min(algorithmList.size(), 3);
// for(Camera camera: cameraList) {
// for(int i = 0; i < len; i++) {
// Algorithm algorithm = algorithmList.get(i);
// // Storage Alert
// Report report = new Report();
// report.setCameraId(camera.getId());
// report.setAlgorithmId(algorithm.getId());
// report.setType(ReportType.AI.getType());
// report.setFileName(FileUtils.pathTo(uploadDir +"/"+"1.jpg"));
// report.setFileName("d:/7.jpg");
// report.setParams(JSON.toJSONString(params));
// report.setCreatedAt(date1);
// report.setCreatedMills(date1.getTime());
// report.setAuditResult(0);
// report.setAuditState(0);
// report.setDisplay(0);
// report.setBoxId(camera.getLocationId());
// report.setDepartId(0L);
// report.setRois("");
// report.setLines("");
// reportService.save(report);
// log.info("insert one");
//}
//}
// return JsonResultUtils.success();
//}
//
//
//
//
// @GetMapping("reportReq")
// @ResponseBody
// public JsonResult<?> reportReq(String date, Float conf) throws Exception {
// if(!mock) {
// return JsonResultUtils.fail("Non mock Status");
//}
// Date date1 = new Date();
// if(StrUtil.isNotBlank(date)) {
// try {
// date1 = DateUtil.parse(date,"yyyyMMddHHmmss");
//} catch (Exception e) {
// return JsonResultUtils.fail("Time Format Error, Correct Format:yyyyMMddHHmmss");
//}
//}
//
// List<Camera> cameraList = cameraService.listData();
// if(cameraList == null || cameraList.isEmpty()) {
// return JsonResultUtils.fail("not has Camera");
//}
//
// List<Algorithm> algorithmList = algorithmService.list();
// if(algorithmList == null || algorithmList.isEmpty()) {
// return JsonResultUtils.fail("not has Algorithm");
//}
//
// Map<Long, String> codeMap = algorithmList.stream().collect(Collectors.toMap(Algorithm::getId, Algorithm::getNameEn));
//
// for(Camera camera: cameraList) {
// List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(camera.getId());
//
// for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
// String code = codeMap.get(cameraAlgorithm.getAlgorithmId());
// if(StrUtil.isBlank(code)) {
// continue;
//}
//
// List<Map<String, Object>> params = new ArrayList<>();
// Map<String, Object> p = new HashMap<>();
// p.put("type", code);
// p.put("position", Arrays.asList(200, 200, 400, 400));
// p.put("confidence", conf == null? 0.5: conf);
// p.put("algoId", cameraAlgorithm.getAlgorithmId());
// params.add(p);
//
// Map<String, Object> req = new HashMap<>();
// req.put("cameraId", camera.getId());
// req.put("params", JSON.toJSONString(params));
// //req.put("file", FileUtil.newFile(FileUtils.pathTo(uploadDir +"/"+"40b236b1-1e09-463a-8e91-f9f866b5985d.jpg")));
// req.put("file", FileUtil.newFile(FileUtils.pathTo("d:/7.jpg")));
// log.info("req data: {}", req);
//
// String resp = HttpUtil.post("http://localhost:8022/api/aibox/report", req);
// System.out.println(resp);
//
// //Thread.sleep(5000);
//
//}
//}
// return JsonResultUtils.success();
//}
//
// @PostMapping("alarmData")
// @ResponseBody
// public JsonResult<?> alarmData(@RequestBody JsonNode json) throws Exception {
// System.out.println(json);
// return JsonResultUtils.success();
//}
//
//
// @GetMapping("faceReq")
// @ResponseBody
// public JsonResult<?> faceReq(String date, Float conf) throws Exception {
// if(!mock) {
// return JsonResultUtils.fail("Non mock Status");
//}
//
// List<Camera> cameraList = cameraService.listData();
// Camera camera = cameraList.get(0);
//
// //for(Camera camera: cameraList) {
// Map<String, Object> params = new HashMap<>();
// params.put("cameraId", camera.getId());
// params.put("params","{\"face_num\": 2, \"faces\": [{\"bbox\": [990, 337, 1050, 397], \"liveness\": \"-3\", \"similiarity\": 0.1084309309720993, \"user_info\": {\"face_id\": \"123\", \"group_id\": \"123\", \"user_id\": \"123\"}}, {\"bbox\": [957, 589, 1011, 643], \"liveness\": \"-3\", \"similiarity\": 0.981647559106349945, \"user_info\": {\"face_id\": \"1935593243049263106\", \"group_id\": \"1718922509339394048\", \"user_id\": \"1935593242973765633\"}}], \"msg\": \"\", \"status\": \"0\"}");
// params.put("file", new File("d:/7.jpg"));
// String sd = HttpUtil.post("http://localhost:8022/api/face/report", params);
// log.info("sd {}", sd);
// //}
// return JsonResultUtils.success();
//}
//
//
// @GetMapping("report2")
// public JsonResult<?> report2(String date, Float conf) throws Exception {
// if(!mock) {
// return JsonResultUtils.fail("Non mock Status");
//}
//
//// List<Camera> cameraList = cameraService.listData();
//// Camera camera = cameraList.get(0);
//
// Long cameraId = 1923250279673069569L;
// Long algoId = 1696809711436365825L;
//
// List<Map<String, Object>> params = new ArrayList<>();
// Map<String, Object> p = new HashMap<>();
// p.put("type","fire");
// p.put("position", Arrays.asList(200, 200, 400, 400));
// p.put("confidence","0.3");
// p.put("algoId", algoId);
// params.add(p);
//
//
// //
// Date startDate = DateUtil.parse("20200101","yyyyMMdd");
// Date endDate = DateUtil.parse("20250721","yyyyMMdd");
// DateUtil.rangeToList(startDate, endDate, DateField.DAY_OF_YEAR).forEach(date1 -> {
// Date x = DateUtil.truncate(date1, DateField.DAY_OF_MONTH);
// Date y = DateUtil.truncate(DateUtil.offsetDay(date1, 1), DateField.DAY_OF_MONTH);
//
//
//
// // Storage Alert
// Report report = new Report();
// report.setCameraId(cameraId);
// report.setAlgorithmId(algoId);
// report.setType(ReportType.AI.getType());
// report.setFileName(FileUtils.pathTo(uploadDir +"/"+"1.jpg"));
// report.setFileName("d:/7.jpg");
// report.setParams(JSON.toJSONString(params));
// report.setCreatedAt(date1);
// report.setCreatedMills(date1.getTime());
// report.setAuditResult(0);
// report.setAuditState(0);
// report.setDisplay(0);
// report.setBoxId(1943177987714510849L);
// report.setDepartId(0L);
// report.setRois("");
// report.setLines("");
// report.setAuditResult(0);
// reportService.save(report);
//});
//
//
//
// //}
// return JsonResultUtils.success();
//}
//
//
// @GetMapping("report3")
// public JsonResult<?> report3(String date, Float conf) throws Exception {
// Random random = new Random();
//
// List<Report> reportList = reportService.list();
// for(Report report: reportList) {
// int x = random.nextInt(30);
// if(x % 3 == 0) {
// System.out.println("r"+ report.getId());
// Report r1 = new Report();
// r1.setId(report.getId());
// r1.setAuditResult(1);
// r1.setAuditAt(DateUtil.offsetMinute(report.getCreatedAt(), random.nextInt(30)));
// reportService.updateById(r1);
//}
//}
//
// return JsonResultUtils.success();
//}
//
// @GetMapping("report4")
// public JsonResult<?> report4(String date, Float conf) throws Exception {
// reportService.updateAllHandleTime();
//
// return JsonResultUtils.success();
//}
//
// private static Map<String, Object> mockAlgo(Long algoId) {
// Map<String, Object> d1 = new HashMap<>();
// d1.put("algoId", algoId);
// d1.put("confidence", 0.8);
// d1.put("position", Arrays.asList(100, 100, 150, 150));
// d1.put("type","head");
// return d1;
//}
//
// private static Map<String, Object> mockFace() {
// Map<String, Object> userInfo = new HashMap<>();
// userInfo.put("face_id", 123L);
// userInfo.put("group_id", 345L);
// userInfo.put("user_id", 456L);
//
// Map<String, Object> d1 = new HashMap<>();
// d1.put("bbox", Arrays.asList(100, 100, 150, 150));
// d1.put("similiarity", 0.8);
// d1.put("user_info", userInfo);
// return d1;
//}
//
// private static void mockCameraStatus() {
// CameraStatusSubVo cameraStatusSubVo = new CameraStatusSubVo();
// cameraStatusSubVo.setCameraId(1923250279673069569L);
// cameraStatusSubVo.setAlgorithmIds(Collections.singletonList("1696809711436365825"));
//
// CameraStatusVo cameraStatusVo = new CameraStatusVo();
// cameraStatusVo.setKey("f5e8df64b91d8915b1805d26c8ffc7c8");
// cameraStatusVo.setSn("d6010341334135364310647356921bad");
// cameraStatusVo.setCameras(new ArrayList<CameraStatusSubVo>(Collections.singleton(cameraStatusSubVo)));
//
// System.out.println(JSON.toJSONString(cameraStatusVo, SerializerFeature.PrettyFormat));
//
//}
//
// public static void main(String[] args) {
//// System.out.println(SecureUtil.md5("n2S8jh*V5"));
//// List<Map<String, Object>> algos = new ArrayList<>();
//// algos.add(mockAlgo(1696809711436365825L));
//// algos.add(mockAlgo(1696809711436365825L));
//// System.out.println(JSON.toJSONString(algos));
//
////
//// Map<String, Object> data = new HashMap<>();
//// List<Map<String, Object>> faces = new ArrayList<>();
//// faces.add(mockFace());
//// data.put("faces", faces);
//// System.out.println(JSON.toJSONString(data));
//
//
// mockCameraStatus();
//}
//
//}
