package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.entity.face.FaceGroup;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.javacv.DecodeRtspUlr;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.ReportService;
import com.yihecode.camera.ai.service.face.FaceGroupService;
import com.yihecode.camera.ai.service.face.FaceUserService;
import com.yihecode.camera.ai.web.api.aibox.PaintRectHandler;
import com.yihecode.camera.ai.web.api.aibox.dto.AlarmPushResultDTO;
import com.yihecode.camera.ai.web.api.face.dto.FaceGroupDTO;
import com.yihecode.camera.ai.web.api.face.dto.FaceUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
* Alarm Third Party Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmThirdPushService {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ConfigService configService;

    @Resource
    private FaceUserService faceUserService;

    @Resource
    private FaceGroupService faceGroupService;

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    //Common Algorithm Push, Send to Third Party
public CompletableFuture<AlarmPushResultDTO> sendComm(Camera camera, Algorithm algorithm, Location location, Report report) {
if(!projectConfig.isAlarmAutoPush()) {
log.info("Third Party Push, Common Algorithm, not Enable self Dynamic Push, exit out");
return CompletableFuture.completedFuture(new AlarmPushResultDTO(false,"not Enable self Dynamic Push"));
}

// Send
return this.sendCommData(camera, algorithm, location, report);
}

// Common Algorithm Push, Send to Third Party
public CompletableFuture<AlarmPushResultDTO> sendCommData(Camera camera, Algorithm algorithm, Location location, Report report) {
return CompletableFuture.supplyAsync(() -> {
try {
//
String thirdUrl = configService.getByValTag("reportPushUrl");
if(StrUtil.isBlank(thirdUrl)) {
log.info("Third Party Push, Common Algorithm, not has Config Third Party Push Address, exit out");
return new AlarmPushResultDTO(false,"not has Config Third Party Push Address");
}

// Alarm Number
int alarmCount = 0;
try {
JSONArray array = JSON.parseArray(report.getParams());
alarmCount = array.size();
} catch (Exception e) {
// json error
}

if(alarmCount == 0) {
log.info("Third Party Push, Common Algorithm, not has Alarm Data, exit out");
return new AlarmPushResultDTO(false,"not has Alarm Data, not Execute Push");
}

//
JSONObject reportMap = new JSONObject();
reportMap.put("pushType","comm");
reportMap.put("cameraId", String.valueOf(camera.getId()));
reportMap.put("cameraName", camera.getName());
reportMap.put("rtspUrl", DecodeRtspUlr.processSensitive(camera.getRtspUrl()));
reportMap.put("algorithmId", String.valueOf(algorithm.getId()));
reportMap.put("algorithmName", algorithm.getName());
reportMap.put("algorithmNameEn", algorithm.getNameEn());
reportMap.put("alarmAt", DateUtil.format(report.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
reportMap.put("params", report.getParams());
reportMap.put("imgUrl", String.format("http://%s/report/stream?id=%s", configService.getByValTag("ipAddr"), report.getId()));
reportMap.put("thumbImgUrl", String.format("http://%s/report/streamThumb?id=%s", configService.getByValTag("ipAddr"), report.getId()));
reportMap.put("boxSn", (location == null || location.getBoxNo() == null)?"": location.getBoxNo());
reportMap.put("boxId", location == null?"0": String.valueOf(location.getId()));
reportMap.put("boxIpAddr", (location == null || location.getIpAddr() == null)?"": location.getIpAddr());
reportMap.put("boxName", (location == null || location.getName() == null)?"": location.getName());
reportMap.put("alarmCount", alarmCount);
reportMap.put("imageBase64","");

// Whether Send Base64 Image
boolean withImg ="true".equals(configService.getByValTag("reportPushImage"));
if(withImg) {
try {
boolean withDraw ="true".equals(configService.getByValTag("reportPushDraw"));
PaintRectHandler paintRectHandler = new PaintRectHandler();
BufferedImage image = paintRectHandler.paintRect2Buffer(report.getFileName(), withDraw? report.getParams(): null);
if (image!= null) {
String imageBase64 = ImgUtil.toBase64(image, FileUtil.extName(report.getFileName()));
reportMap.put("imageBase64", imageBase64);
}
} catch (Exception e) {
//
}
}

String response = HttpUtil.post(thirdUrl, JSON.toJSONString(reportMap), 5000);

try {
JSONObject object = JSON.parseObject(response);
if(object.getIntValue("code") == projectConfig.getAlarmAutoPushSuccessCode()) {
Report modifyReport = new Report();
modifyReport.setId(report.getId());
//modifyReport.setPushed(1);
reportService.updateById(modifyReport);
log.info("Third Party Push, Common Algorithm, Push Success, resp: {}", response);
return new AlarmPushResultDTO(true,"ok");
} else {
log.info("Third Party Push, Common Algorithm, Push Failed, resp: {}", response);
return new AlarmPushResultDTO(false,"Push Failed, Receive Method Back Error @"+ object.getString("msg"));
}
} catch (Exception e) {
log.info("Third Party Push, Common Algorithm, Push Result Parse Exception, resp: {}", response);
return new AlarmPushResultDTO(false,"Push Result Parse Exception, no Method Confirm Whether Push Success @"+ response);
}
} catch (Exception e) {
log.error("Third Party Push, Common Algorithm, Push Common Algorithm Alarm to Third Party Exception: ex: {}", e.getMessage());
return new AlarmPushResultDTO(false,"Call Failed @"+ e.getMessage());
}
}, executor);
}

// Face Algorithm Push, Send Third Party
public CompletableFuture<AlarmPushResultDTO> sendFace(Camera camera, Algorithm algorithm, Location location, FaceReport faceReport) {
return CompletableFuture.supplyAsync(() -> {
try {
String thirdUrl = configService.getByValTag("reportPushUrl");
if (StrUtil.isBlank(thirdUrl)) {
log.error("Third Party Push, Face Recognition, not Set Third Party Push Address, exit out");
return new AlarmPushResultDTO(false,"not Set Third Party Push Address");
}

// Query Face Corresponding User
FaceUser faceUser = faceUserService.getById(faceReport.getUserId());
FaceUserDTO faceUserDTO = new FaceUserDTO();
if (faceUser!= null) {
BeanUtils.copyProperties(faceUser, faceUserDTO);
}

// Query Face Corresponding Group
FaceGroup faceGroup = faceGroupService.getById(faceReport.getGroupId());
FaceGroupDTO faceGroupDTO = new FaceGroupDTO();
if (faceGroup!= null) {
BeanUtils.copyProperties(faceGroup, faceGroupDTO);
}

JSONObject reportMap = new JSONObject();
reportMap.put("pushType","face");
reportMap.put("cameraId", String.valueOf(camera.getId()));
reportMap.put("cameraName", camera.getName());
reportMap.put("rtspUrl", camera.getRtspUrl());
reportMap.put("algorithmId", String.valueOf(algorithm.getId()));
reportMap.put("algorithmName", algorithm.getName());
reportMap.put("algorithmNameEn", algorithm.getNameEn());
reportMap.put("alarmAt", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
reportMap.put("params", faceReport.getResultJson());
reportMap.put("imgUrl", String.format("http://%s/face/report/image?filepath=%s", configService.getByValTag("ipAddr"), faceReport.getFilePath()));
reportMap.put("thumbImgUrl", String.format("http://%s/face/report/image?filepath=%s", configService.getByValTag("ipAddr"), faceReport.getFilePath()));
reportMap.put("boxSn", (location == null || location.getBoxNo() == null)?"": location.getBoxNo());
reportMap.put("boxId", location == null?"0": String.valueOf(location.getId()));
reportMap.put("boxIpAddr", (location == null || location.getIpAddr() == null)?"": location.getIpAddr());
reportMap.put("boxName", (location == null || location.getName() == null)?"": location.getName());
reportMap.put("alarmCount", 1);
reportMap.put("faceUser", faceUserDTO);
reportMap.put("faceGroup", faceGroupDTO);
reportMap.put("imageBase64","");

// imageBase64 this Data Make Complete Config style, Because for Need Consume Consume Time
boolean withImg ="true".equals(configService.getByValTag("reportPushImage"));
if (withImg) {
try {
PaintRectHandler paintRectHandler = new PaintRectHandler();
BufferedImage image = paintRectHandler.paintRect2Buffer(faceReport.getFilePath(), null);
if (image!= null) {
String imageBase64 = ImgUtil.toBase64(image, FileUtil.extName(faceReport.getFilePath()));
reportMap.put("imageBase64", imageBase64);
}
} catch (Exception e) {
//
}
}

String response = HttpUtil.post(thirdUrl, JSON.toJSONString(reportMap), 10000);
log.error("Third Party Push, Face Recognition, Face Algorithm Alarm to Third Party Return Result: resp: {}", response);
return new AlarmPushResultDTO(true,"ok");
} catch (Exception e) {
log.error("Third Party Push, Face Recognition, Face Algorithm Alarm to Third Party Exception: {}", e.getMessage());
return new AlarmPushResultDTO(false,"Call Failed @"+ e.getMessage());
}
}, executor);
}


/**
* Draw make Rectangle Box and Title
* @param image
* @param boxsJsonStr, Format: [{type:'peopel', position: [100, 100, 200, 200]}]
* @return
*/
// private BufferedImage paintRectAndTitle(BufferedImage image, String boxsJsonStr) {
// try {
// Graphics2D g = image.createGraphics();
// g.setColor(Color.RED);
// g.setStroke(new BasicStroke(2));
//
// // Draw make Rectangle Box and Title
// JSONArray boxsJson = JSON.parseArray(boxsJsonStr);
// int len = boxsJson.size();
// for (int i = 0; i < len; i++) {
// JSONObject row = boxsJson.getJSONObject(i);
// String title = row.getString("type");
// JSONArray position = row.getJSONArray("position");
// //
// g.setPaint(Color.RED); // Background
// if (position.size() == 4) {
// int x = position.getIntValue(0);
// int y = position.getIntValue(1);
// int width = position.getIntValue(2) - x;
// int height = position.getIntValue(3) - y;
// g.drawRect(x, y, width, height);
//
// int textX = x;
// int textY = y - 10; // up Method 10 Pixel Bit set
// Font font = getFont();
// if(font!= null) {
// font = font.deriveFont(16f);
// font = font.deriveFont(Font.BOLD);
// g.setFont(font);
//}
// // Set Text Char Background big small By Text Char long Degree Incoming, and Set for half Transparent Red
// FontMetrics fm = g.getFontMetrics();
// int textWidth = fm.stringWidth(title); // Calculate Text Width
//
// int backgroundColorWidth = textWidth + 35; // Background Width than Text Width Later big One Some
// int backgroundColorHeight = 10 + 14; // Background high Degree can with By Need Adjust whole
// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); // Set half Transparent combine Complete Rule rule
// g.fillRect(textX, y - backgroundColorHeight, backgroundColorWidth, backgroundColorHeight); // small Method Block Make for Background Mark, and Set for half Transparent
//
// g.setPaint(Color.white);
// g.drawString(title, textX + 4, textY + 4);
//}
//}
// g.dispose();
// return image;
//} catch (Exception e) {
// return null;
//}
//}

/**
* Get System Font, Optimize First make Use in Text Font
* @return
*/
// private Font getFont() {
// GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
// Font[] fonts = ge.getAllFonts();
// if(fonts == null || fonts.length == 0) {
// log.error("no Method Get System Font Set");
// return null;
//}
//
// for(Font font: fonts) {
// if(font.canDisplay('\u4e2d')) {
// return font;
//}
//}
// return fonts[0];
//}
}
