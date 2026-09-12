package com.yihecode.camera.ai.web.face;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

//@ApiIgnore
//@Slf4j
//@Controller
//@RequestMapping({"/test/face"})
public class HttpFaceTestController {

@Value("${modelDir}")
private String uploadDir;

@PostMapping({"/register_face"})
@ResponseBody
public JsonResult register_face(MultipartFile image, String ip, String port, String group_id, String user_id, String face_id){
try {
String imageBase64 = multipartFileToBase64(image);
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image", imageBase64);
params.put("image_name", image.getOriginalFilename());
params.put("group_id", String.valueOf(group_id));
params.put("user_id", String.valueOf(user_id));
params.put("face_id", String.valueOf(face_id));
System.out.println(JSON.toJSONString(params));
String baseUrl = String.format("http://%s:%s", ip, port);
String json = HttpUtil.post(baseUrl +"/register_face", JSON.toJSONString(params));
// JSONObject object = JSONObject.parseObject(json);
Map<String, Object> result = new HashMap<>();
result.put("params", params);
result.put("baseUrl", baseUrl);
result.put("result", json);
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail(e.getMessage());
}

}

@PostMapping({"/recognize"})
@ResponseBody
public JsonResult recognize(MultipartFile image, String ip, String port) throws IOException{
try {
String imageBase64 = multipartFileToBase64(image);
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image", imageBase64);
params.put("image_name", image.getOriginalFilename());
System.out.println(JSON.toJSONString(params));
String baseUrl = String.format("http://%s:%s", ip, port);
String json = HttpUtil.post(baseUrl +"/recognize", JSON.toJSONString(params));
// JSONObject object = JSONObject.parseObject(json);
Map<String, Object> result = new HashMap<>();
result.put("params", params);
result.put("baseUrl", baseUrl);
result.put("result", json);
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail(e.getMessage());
}

}

@PostMapping({"/detect"})
@ResponseBody
public JsonResult detect(MultipartFile image, String ip, String port) throws IOException{
try {
String imageBase64 = multipartFileToBase64(image);
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image", imageBase64);
params.put("image_name", image.getOriginalFilename());
String baseUrl = String.format("http://%s:%s", ip, port);
String json = HttpUtil.post(baseUrl +"/detect", JSON.toJSONString(params));
// JSONObject object = JSONObject.parseObject(json);
Map<String, Object> result = new HashMap<>();
result.put("params", params);
result.put("baseUrl", baseUrl);
result.put("result", json);
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail(e.getMessage());
}

}

@PostMapping({"/compare"})
@ResponseBody
public JsonResult compare(MultipartFile image1, MultipartFile image2, String ip, String port) throws IOException{
try {
String image1Base64 = multipartFileToBase64(image1);
String image2Base64 = multipartFileToBase64(image2);
Map<String, Object> params = new HashMap<>();
params.put("image_type","BASE64");
params.put("image1", image1Base64);
params.put("image_name1", image1.getOriginalFilename());
params.put("image2", image2Base64);
params.put("image_name2", image2.getOriginalFilename());
System.out.println(JSON.toJSONString(params));
String baseUrl = String.format("http://%s:%s", ip, port);
String json = HttpUtil.post(baseUrl +"/compare", JSON.toJSONString(params));
// JSONObject object = JSONObject.parseObject(json);
Map<String, Object> result = new HashMap<>();
result.put("params", params);
result.put("baseUrl", baseUrl);
result.put("result", json);
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail(e.getMessage());
}

}

@PostMapping({"/query_group_list"})
@ResponseBody
public JsonResult query_group_list(String ip, String port) throws IOException{
try {
Map<String, Object> params = new HashMap<>();
params.put("op_type","query_group_list");
System.out.println(JSON.toJSONString(params));
String baseUrl = String.format("http://%s:%s", ip, port);
String json = HttpUtil.post(baseUrl +"/face_lib_manager", JSON.toJSONString(params));
// JSONObject object = JSONObject.parseObject(json);
Map<String, Object> result = new HashMap<>();
result.put("params", params);
result.put("baseUrl", baseUrl);
result.put("result", json);
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail(e.getMessage());
}

}

@PostMapping({"/query_user_list"})
@ResponseBody
public JsonResult query_user_list(String group_id, String ip, String port) throws IOException{
try {
Map<String, Object> params = new HashMap<>();
params.put("op_type","query_user_list");
params.put("group_id", group_id);
System.out.println(JSON.toJSONString(params));
String baseUrl = String.format("http://%s:%s", ip, port);
String json = HttpUtil.post(baseUrl +"/face_lib_manager", JSON.toJSONString(params));
// JSONObject object = JSONObject.parseObject(json);
Map<String, Object> result = new HashMap<>();
result.put("params", params);
result.put("baseUrl", baseUrl);
result.put("result", json);
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail(e.getMessage());
}

}

private String multipartFileToBase64(MultipartFile file) throws IOException {
String originalFilename = file.getOriginalFilename();
String filePath = uploadDir + originalFilename;
File dest = new File(filePath);
try (InputStream inputStream = file.getInputStream()) {
Files.copy(inputStream, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
}
String imageBase64 = ImgUtil.toBase64(ImgUtil.read(dest), FileUtil.extName(dest));
dest.delete();
return imageBase64;
}
}
