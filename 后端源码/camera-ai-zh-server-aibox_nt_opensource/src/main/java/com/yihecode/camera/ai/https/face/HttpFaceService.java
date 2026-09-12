package com.yihecode.camera.ai.https.face;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
* Face Recognition Basic Function can HTTP API - Face Recognition child Module
*/
@Slf4j
@Component
public class HttpFaceService {

    @Resource
    private ConfigService configService;

    //http://121.229.96.28:8003

public static void main(String[] args) {
HttpFaceService httpFaceService = new HttpFaceService();
String time = DateUtil.format(new Date(),"yyyy-MM-dd-HH-mm-ss-ss");
Object obj = httpFaceService.recoginze("C:\\Users\\Administrator\\Desktop\\de0cc0ee14c686764bbefe622587cb20.jpg", 100L, time);
System.out.println(obj);
}

/**
* Face Detection
* @param captureTime eg.2023-10-27-10-11-11
*/
public List<HttpFaceRecognize> recoginze(String file, Long cameraId, String captureTime) {
List<HttpFaceRecognize> httpFaceRecognizes = new ArrayList<>();
try {
//
// String baseUrl ="http://121.229.96.28:8003";
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return httpFaceRecognizes;
}

//
File tarfile = new File(file);
if(!tarfile.exists()) {
log.info("Face Recognition File does not exist");
return httpFaceRecognizes;
}

//
String imageBase64 = ImgUtil.toBase64(ImgUtil.read(file), FileUtil.extName(file));
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image", imageBase64);
params.put("camera_id", String.valueOf(cameraId));
params.put("capture_time", captureTime);
//
String json = HttpUtil.post(baseUrl +"/recognize", JSON.toJSONString(params));
// log.info("Recognition Result"+ json);

File tar = new File("/data/camera/face_jsons/");
if(!tar.exists()) {
tar.mkdirs();
}
FileUtil.writeString(json,"/data/camera/face_jsons/"+ FileUtil.mainName(file) +".json","utf-8");

//
JSONObject object = JSONObject.parseObject(json);
int faceNum = object.getInteger("face_num");
if(faceNum == 0) {
return httpFaceRecognizes;
}
JSONArray array = object.getJSONArray("faces");
int len = array.size();
for(int i = 0; i < len; i++) {
JSONObject face = array.getJSONObject(i);
HttpFaceRecognize httpFaceRecognize = new HttpFaceRecognize();
// httpFaceRecognize.setProbability(face.getDouble("face_probability"));
httpFaceRecognize.setLiveness(face.getString("liveness"));
httpFaceRecognize.setSimiliarity(face.getDouble("similiarity"));
//
List<Integer> points = new ArrayList<>();
JSONArray bbox = face.getJSONArray("bbox");
int blen = bbox.size();
for(int j = 0; j < blen; j++) {
points.add(bbox.getIntValue(j));
}
httpFaceRecognize.setBbox(points);
//
JSONObject userInfo = face.getJSONObject("user_info");
httpFaceRecognize.setFaceGroupId(parseToLong(userInfo.getString("group_id")));
httpFaceRecognize.setFaceUserId(parseToLong(userInfo.getString("user_id")));
httpFaceRecognize.setFaceImageId(parseToLong(userInfo.getString("face_id")));
httpFaceRecognizes.add(httpFaceRecognize);
}
} catch (Exception e) {
log.info("Call Face Recognition Service Exception", e);
}
return httpFaceRecognizes;
}

/**
* Stranger produce Person member Track Info
* @return
*/
public void personTrack() {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return;
}
//
String json = HttpUtil.post(baseUrl +"/person_track", new HashMap<>());
// log.info(json);
} catch (Exception e) {
e.printStackTrace();
}
}

/**
* Stranger produce Person member database Reset (Clear Reason)
*/
public void strangerReset() {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return;
}
//
String json = HttpUtil.post(baseUrl +"/stranger_reset", new HashMap<>());
// log.info(json);
} catch (Exception e) {
e.printStackTrace();
}
}

/**
* Face Detection
*/
public String detect(String imageBase64) {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return null;
}
//
// String imageBase64 = ImgUtil.toBase64(ImgUtil.read(file), FileUtil.extName(file));
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image", imageBase64);
//
String json = HttpUtil.post(baseUrl +"/detect", JSON.toJSONString(params));
// log.info(json);
return json;
} catch (Exception e) {
e.printStackTrace();
return null;
}
}
public String recognize(String imageBase64) {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return null;
}
//
// String imageBase64 = ImgUtil.toBase64(ImgUtil.read(file), FileUtil.extName(file));
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image", imageBase64);
//
String json = HttpUtil.post(baseUrl +"/recognize", JSON.toJSONString(params));
// log.info(json);
return json;
} catch (Exception e) {
e.printStackTrace();
return null;
}
}
public String compare(String image1Base64, String image2Base64) {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return null;
}
//
// String imageBase64 = ImgUtil.toBase64(ImgUtil.read(file), FileUtil.extName(file));
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image1", image1Base64);
params.put("image2", image2Base64);
//
String json = HttpUtil.post(baseUrl +"/compare", JSON.toJSONString(params));
// log.info(json);
return json;
} catch (Exception e) {
e.printStackTrace();
return null;
}
}

/**
* Face Register
*/
public JsonResult registerFaceImage(String file, Long faceGroupId, Long faceUserId, Long faceImageId) {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return JsonResultUtils.fail("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
}
//
String imageBase64 = ImgUtil.toBase64(ImgUtil.read(file), FileUtil.extName(file));
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image", imageBase64);
params.put("group_id", String.valueOf(faceGroupId));
params.put("user_id", String.valueOf(faceUserId));
params.put("face_id", String.valueOf(faceImageId));
//
String json = HttpUtil.post(baseUrl +"/register_face", JSON.toJSONString(params));
JSONObject object = JSONObject.parseObject(json);
if(object.getIntValue("status") == 0) {
if(object.containsKey("liveness")) {
switch (object.getString("liveness")) {
case"1": return JsonResultUtils.success();
case"2": return JsonResultUtils.fail("Non Work Body");
case"-1": return JsonResultUtils.fail("not Confirm");
case"-2": return JsonResultUtils.fail("transmit in Face Number big at 1");
case"-3": return JsonResultUtils.fail("Face over small");
case"-4": return JsonResultUtils.fail("Angle Degree over big");
case"-5": return JsonResultUtils.fail("Face super out Boundary");
case"6": return JsonResultUtils.fail("Deep Degree image Error");
case"7": return JsonResultUtils.fail("Red outer image Too Bright");
case"8": return JsonResultUtils.fail("Red outer image Too Dark");
case"-100": return JsonResultUtils.fail("Face Quality Quantity Error");
}
} else {
return JsonResultUtils.success();
}
} else if (object.getIntValue("status") == 3) {
return JsonResultUtils.fail("Face Register failed, Please more change Face Image Again Try Try");
}
return JsonResultUtils.fail(object.getString("msg"));
} catch (Exception e) {
// e.printStackTrace();
log.error("Face database Register failed", e);
return JsonResultUtils.fail("Face Register failed, Please Confirm Face database Whether Normal");
}
}

/**
* User group Query
*/
public void listFaceGroup() {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return;
}
//
Map<String, Object> params = new HashMap<>();
params.put("op_type","query_group_list");
//
String json = HttpUtil.post(baseUrl +"/face_lib_manager", JSON.toJSONString(params));
// log.info("User group Query:"+ json);
} catch (Exception e) {
e.printStackTrace();
}
}

/**
* User List Query
*/
public void listFaceUser(Long faceGroupId) {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return;
}
//
Map<String, Object> params = new HashMap<>();
params.put("op_type","query_user_list");
params.put("group_id", String.valueOf(faceGroupId));
//
String json = HttpUtil.post(baseUrl +"/face_lib_manager", JSON.toJSONString(params));
// log.info("User List Query:"+ json);
} catch (Exception e) {
e.printStackTrace();
}
}

/**
* Delete Face
*/
public void removeFaceImage(Long faceImageId, Long faceUserId, Long faceGroupId) {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// System.out.println("not has Config Face Recognition API Address");
return;
}
//
Map<String, Object> params = new HashMap<>();
params.put("op_type","clear_face");
params.put("group_id", String.valueOf(faceGroupId));
params.put("user_id", String.valueOf(faceUserId));
params.put("face_id", String.valueOf(faceImageId));
//
String json = HttpUtil.post(baseUrl +"/face_lib_manager", JSON.toJSONString(params));
//System.out.println("Delete Face:"+ json);
} catch (Exception e) {
e.printStackTrace();
}
}

/**
* Delete User
*/
public JsonResult<?> removeFaceUser(Long faceUserId, Long faceGroupId) {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// System.out.println("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return JsonResultUtils.fail("not has Config Face Recognition API Address");
}
//
Map<String, Object> params = new HashMap<>();
params.put("op_type","clear_user");
params.put("group_id", String.valueOf(faceGroupId));
params.put("user_id", String.valueOf(faceUserId));
//
String json = HttpUtil.post(baseUrl +"/face_lib_manager", JSON.toJSONString(params));
// System.out.println("Delete User:"+ json);
return JsonResultUtils.success();
} catch (Exception e) {
// e.printStackTrace();
return JsonResultUtils.fail("Face Register failed, Please Confirm Face database Whether Normal");

}
}

/**
* Delete User group
*/
public void removeFaceGroup(Long faceGroupId) {
try {
//
String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
if(StrUtil.isBlank(baseUrl)) {
// log.info("not has Config Face Recognition API Address (FACE_HTTP_BASE_URL)");
return;
}
//
Map<String, Object> params = new HashMap<>();
params.put("op_type","clear_group");
params.put("group_id", String.valueOf(faceGroupId));
//
String json = HttpUtil.post(baseUrl +"/face_lib_manager", JSON.toJSONString(params));
// log.info("Delete User group:"+ json);
} catch (Exception e) {
e.printStackTrace();
}
}

private Long parseToLong(String str) {
try {
return Long.parseLong(str);
} catch (Exception e) {
return 0L;
}
}
}
