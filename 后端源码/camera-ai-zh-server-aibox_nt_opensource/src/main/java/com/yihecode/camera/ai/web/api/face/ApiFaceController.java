package com.yihecode.camera.ai.web.api.face;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.face.FaceReportService;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.ImageUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.api.aibox.vo.ReportVo;
import com.yihecode.camera.ai.web.api.comm.AlarmThirdPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Box Face
*/
@ApiIgnore
@SaIgnore
@Api(tags = "Edge Box Info Report Management")
@Slf4j
@RestController
@RequestMapping("/api/face")
public class ApiFaceController {

    @Resource
    private CameraService cameraService;

    @Resource
    private CameraAlgorithmService cameraAlgorithmService;

    @Resource
    private AlgorithmService algorithmService;

    @Resource
    private FaceReportService faceReportService;

    @Resource
    private ConfigService configService;

    @Resource
    private LocationService locationService;

    @Resource
    private AlarmThirdPushService alarmPushService;

    //Image Storage Directory
@Value("${cameraDir}")
private String imageDir;

/**
* Get Config Face Recognition Camera
* @return
*/
@ApiOperation("Get Config Face Recognition Camera")
@PostMapping("/camera/list")
public JsonResult<?> cameraList() {
Algorithm algorithm = algorithmService.getByNameEn("face_recognize");
if (algorithm == null) {
return JsonResultUtils.success();
}
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
List<Camera> cameraList = new ArrayList<>();
for(CameraAlgorithm ca: cameraAlgorithmList) {
Camera camera = cameraService.getById(ca.getCameraId());
cameraList.add(camera);
}
return JsonResultUtils.success(cameraList);
}

/**
* Report Stranger produce Person Snapshot
* @return
*/
@ApiOperation("Report Stranger produce Person Snapshot")
@PostMapping("/report")
public JsonResult<?> report(ReportVo reportVo) {
if(StrUtil.isBlank(reportVo.getParams())) {
log.error("Face Recognition Report Error, Face Recognition Data Is Empty");
return JsonResultUtils.fail("Face Recognition Data Is Empty");
}

if(reportVo.getFile() == null) {
log.error("Face Recognition Report Error, Image Is Empty");
return JsonResultUtils.fail("Image Is Empty");
}

// most small Similarity Threshold
float faceMinSimiliarity = Convert.toFloat(configService.getByValTag("faceMinSimiliarity"), 0.3f);
// near Like Similarity Threshold
float faceSameSimiliarity = Convert.toFloat(configService.getByValTag("faceSameSimiliarity"), 0.8f);

// Parse Save Data
try {
JSONObject rootJson = JSON.parseObject(reportVo.getParams());
if(rootJson.containsKey("status") && rootJson.getIntValue("status") == 1) {
log.info("Face Recognition Result not Correct,json: {}", reportVo.getParams());
return JsonResultUtils.success();
}

// Date
String date = DateUtil.format(new Date(),"yyyyMMdd");

// Storage Image Path Format: /data/camera/face/yyyyMMdd/uuid.jpg
String dest = FileUtils.pathTo(imageDir +"/face/"+ date);
if(!FileUtil.exist(dest)) {
FileUtil.mkdir(dest);
}

String filename = IdUtil.randomUUID() +".jpg";
try {
reportVo.getFile().transferTo(FileUtil.newFile(FileUtils.pathTo(dest +"/"+ filename)));
} catch (Exception e) {
log.error("Face Report Image Storage Exception:{}", e.getMessage());
return JsonResultUtils.success();
}

// Create small image Storage Path
String headDest = FileUtils.pathTo(imageDir +"/face_box/"+ date);
if(!FileUtil.exist(headDest)) {
FileUtil.mkdir(headDest);
}

JSONArray faces = rootJson.getJSONArray("faces");
int facesLen = faces.size();
for(int i = 0; i < facesLen; i++) {
JSONObject faceJson = faces.getJSONObject(i);
JSONArray bbox = faceJson.getJSONArray("bbox");
//
int hasStranger = 0;
Long faceId = 0L;
Long groupId = 0L;
Long userId = 0L;
float similiarity = parseSimiliarity(faceJson.getFloatValue("similiarity"));
if(similiarity < faceMinSimiliarity) {// low at most small Similarity, Filter
continue;
}

if (similiarity < faceSameSimiliarity) {// when Similarity big at Threshold Hour, Recognize Fixed for Familiar Person
hasStranger = 1;
}

JSONObject userInfo = faceJson.getJSONObject("user_info");
faceId = userInfo.getLong("face_id");
groupId = userInfo.getLong("group_id");
userId = userInfo.getLong("user_id");

// Determine Whether Filter
if(hasStranger == 0) {
boolean continueReport = ApiFaceFilter.getInst().putAndReport(reportVo.getCameraId(), userId, similiarity);
// log.info("Face Alarm:cameraID:{}, userId:{}, time: {}, continueReport: {}", reportVo.getCameraId(), userId, DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"), continueReport);
if(!continueReport) {
log.info("Filter re reply Face Alarm:cameraId: {}, userId: {}, similiarity: {}", reportVo.getCameraId(), userId, similiarity);
continue;
}
}

// Cut Get small image
String mainName = FileUtil.mainName(filename);
String headName = mainName +"_"+ i +".jpg";
String faceFilePath = null;
try {
faceFilePath = ImageUtils.croppedImage2SafeBox(FileUtils.pathTo(dest +"/"+ filename), FileUtils.pathTo(headDest +"/"+ headName), bbox.getIntValue(0), bbox.getIntValue(1), bbox.getIntValue(2), bbox.getIntValue(3));
} catch (Exception e) {
log.error("Cut Get Head part small image Exception", e);
}

// Storage
FaceReport faceReport = new FaceReport();
faceReport.setCameraId(reportVo.getCameraId());
faceReport.setHasStranger(hasStranger);
faceReport.setGroupId(groupId);
faceReport.setFaceId(faceId);
faceReport.setUserId(userId);
//faceReport.setResultJson(reportVo.getParams());
faceReport.setResultJson(JSON.toJSONString(faceJson));
faceReport.setFilePath(faceFilePath);
faceReport.setCreatedAt(new Date());
faceReport.setCreatedMills(System.currentTimeMillis());
faceReport.setSourceFile(FileUtils.pathTo(dest +"/"+ filename));
faceReport.setSimilarity(similiarity);
faceReportService.save(faceReport);

// Push Third Party
sendToThrid(faceReport);
}
return JsonResultUtils.success();
} catch (Exception e) {
log.info("###### exception face json {}", reportVo.getParams());
log.error("Face Report Exception", e);
return JsonResultUtils.fail("Face Report Data Failed,"+ e.getMessage());
} finally {
// Delete big image
// remove at 20250630, Frontend Need show show original image, not should the Delete
// FileUtil.del(FileUtils.pathTo(dest +"/"+ filename));
}
}

/**
* Similarity Keep Two Bit small Number
* @param f
* @return
*/
public static float parseSimiliarity(float f) {
String formatted = String.format("%.2f", f);
return Float.parseFloat(formatted);
}

/**
* Send to Third Party
*/
private void sendToThrid(FaceReport faceReport) {
String thirdUrl = configService.getByValTag("reportPushUrl");
if(StrUtil.isBlank(thirdUrl)) {
return;
}

// Query Algorithm
Algorithm algorithm = algorithmService.getByNameEn("face_recognize");
if(algorithm == null) {
return;
}

// Query Camera
Camera camera = cameraService.getById(faceReport.getCameraId());
if(camera == null) {
return;
}

// Query Box
Location location = locationService.getById(camera.getLocationId());

// Push Third Party
alarmPushService.sendFace(camera, algorithm, location, faceReport);
}

/*
public static void main(String[] args) throws Exception {
FaceUserDTO faceUserDTO = new FaceUserDTO();
faceUserDTO.setId(1840333564135124993L);
faceUserDTO.setName("Liss");
faceUserDTO.setTel("");

FaceGroupDTO faceGroupDTO = new FaceGroupDTO();
faceGroupDTO.setId(1718922509339394048L);
faceGroupDTO.setName("White Name form");

JSONObject reportMap = new JSONObject(new LinkedHashMap());
reportMap.put("pushType","comm");
reportMap.put("cameraId", String.valueOf(1845678841293451266L));
reportMap.put("cameraName","big Doorway");
reportMap.put("rtspUrl","rtsp://admin:123456@192.168.0.100/Streaming/Channels/101");
reportMap.put("algorithmId", String.valueOf(1696809711436365855L));
reportMap.put("algorithmName","Vehicle Detection");
reportMap.put("algorithmNameEn","car");
reportMap.put("alarmAt", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
reportMap.put("params","{\"face_num\":1,\"faces\":[{\"bbox\":[1394,717,1596,919],\"liveness\":\"-4\",\"similiarity\":0.10879924148321152,\"user_info\":{\"face_id\":\"1840333564135124993\",\"group_id\":\"1718922509339394048\",\"user_id\":\"1840333563933798402\"}}]}");
reportMap.put("imgUrl", String.format("http://%s/face/report/image?filepath=%s","192.168.0.116:8081", 1844936017061203970L));
reportMap.put("thumbImgUrl", String.format("http://%s/face/report/image?filepath=%s","192.168.0.116:8081", 1844936017061203970L));
reportMap.put("boxSn","d6010341334135364310857356921b25");
reportMap.put("boxId","1");
reportMap.put("boxIpAddr","192.168.1.127");
reportMap.put("boxName","chaoxing");
reportMap.put("alarmCount", 1);
reportMap.put("faceUser", faceUserDTO);
reportMap.put("faceGroup", faceGroupDTO);
reportMap.put("imageBase64","");

System.out.println(JSONObject.toJSONString(reportMap));
}

*/

}
