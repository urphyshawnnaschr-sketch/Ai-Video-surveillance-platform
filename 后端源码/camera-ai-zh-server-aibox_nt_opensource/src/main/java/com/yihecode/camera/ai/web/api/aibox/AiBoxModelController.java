package com.yihecode.camera.ai.web.api.aibox;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraAlgorithm;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.Md5FileUtils;
import com.yihecode.camera.ai.web.api.aibox.vo.ModelCheckVo;
import com.yihecode.camera.ai.web.api.aibox.vo.ModelFilesVo;
import com.yihecode.camera.ai.web.api.aibox.vo.ModelListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
* Edge Box Model Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@SaIgnore
@ApiIgnore
@Api(tags = "Edge Box Model File Management")
@Slf4j
@RestController
@RequestMapping({"/api/aibox/model"})
public class AiBoxModelController {

    @Resource
    private ConfigService configService;

    @Resource
    private LocationService locationService;

    @Resource
    private CameraService cameraService;

    @Resource
    private AlgorithmService algorithmService;

    @Resource
    private CameraAlgorithmService cameraAlgorithmService;

    //Model Path
@Value("${dataModelsDir}")
public String modelDir; //"E:/models/"; //
/**
* Query Model File List, Provide to Edge Box in Line far Process Download
* @return
*/
@ApiOperation("Query Model File List")
@PostMapping("/files")
public JsonResult<List<Map<String, String>>> listModelFiles(@RequestBody ModelFilesVo modelFilesVo) {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(modelFilesVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}
// Model Name Error
if(StrUtil.isBlank(modelFilesVo.getModelName())) {
return JsonResultUtils.fail("modelName Value Error");
}
// Query Model Path
File tar = new File(modelDir + File.separator + modelFilesVo.getModelName());
if(!tar.exists()) {
return JsonResultUtils.fail("Model does not exist");
}
// Get File List
List<Map<String, String>> fileList = new ArrayList<>();
File[] files = tar.listFiles();
if(files == null) {
return JsonResultUtils.fail("Model down no File");
}
//
for(File file: files) {
if(!file.isFile()) {
continue;
}
String filename = file.getName();
String md5 = Md5FileUtils.getMD5(file);
Map<String, String> fileMap = new HashMap<>();
fileMap.put("filename", filename);
fileMap.put("md5", md5);
fileMap.put("filesize", file.length() +"");
fileList.add(fileMap);
}
return JsonResultUtils.success(fileList);
}

/**
* Model File Download, Provide to Box in Line far Process Download
* @param zipFile
* @param response
*/
@ApiOperation("Download Model File")
@GetMapping("/download")
public void download(String key, String zipFile, HttpServletResponse response) throws Exception {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(key)) {
response.reset();
response.sendError(500,"key Value Error");
return;
}
// Model Name Error
if(StrUtil.isBlank(zipFile)) {
response.reset();
response.sendError(500,"zipFile Value Error");
return;
}
//
File file = new File(modelDir + File.separator + zipFile);
if(!file.exists()) {
// response.setContentType("application/json");
// PrintWriter out = response.getWriter();
// Map<String, Object> retData = new HashMap<>();
// retData.put("code", 500);
// retData.put("msg","zipFile Value Error");
// out.println(JSON.toJSONString(retData)); //
// out.flush();
// out.close();

//
response.reset();
response.sendError(500,"zip File does not exist");
return;
}
//
OutputStream os = null;
try {
os = response.getOutputStream();
String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
response.setHeader("Content-Type", contentType);
response.setHeader("Content-Disposition","attachment;filename="+ new String(file.getName().getBytes("utf-8"),"utf-8"));
FileInputStream fileInputStream = new FileInputStream(file);
WritableByteChannel writableByteChannel = Channels.newChannel(os);
FileChannel fileChannel = fileInputStream.getChannel();
fileChannel.transferTo(0, fileChannel.size(), writableByteChannel);
fileChannel.close();
os.flush();
writableByteChannel.close();
} catch (IOException e) {
e.printStackTrace();

//
response.reset();
response.sendError(500,"Download Exception @"+ e.getMessage());
} finally {
try {
if (os!= null) {
os.close();
}
} catch (IOException e) {
e.printStackTrace();
}
}
}

/**
* Model File Download, Provide to Box in Line far Process Download
*/
@ApiOperation("Validate Model Whether Update")
@PostMapping("/check")
public JsonResult<Boolean> check(@RequestBody ModelCheckVo modelCheckVo) {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(modelCheckVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}
// Model Name Error
if(StrUtil.isBlank(modelCheckVo.getModelName())) {
return JsonResultUtils.fail("modelName Value Error");
}
//
File md5File = new File(modelDir + File.separator + modelCheckVo.getModelName() + File.separator +"md5.txt");
if(!md5File.exists()) {
return JsonResultUtils.fail("md5 File does not exist");
}
// Read md5.txt Content
String md5s = FileUtil.readString(md5File,"utf-8");
if(StrUtil.isBlank(md5s) ||!md5s.equals(modelCheckVo.getMd5())) {
return JsonResultUtils.success(false);
}
return JsonResultUtils.success(true);
}

/**
* Query Model File List, Provide to Edge Box in Line far Process Download
* @return
*/
@ApiOperation("Query Model List")
@PostMapping("/list")
public JsonResult<List<Map<String, Object>>> listModels(@RequestBody ModelListVo modelListVo) {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if(StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(modelListVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}
// Query Box Info
Location location = locationService.getBoxSnForRemote(modelListVo.getSn());
if(location == null) {
return JsonResultUtils.fail("sn find not to Box Info");
}
// By Box ID Query All Camera
List<Camera> cameraList = cameraService.listByBoxId(location.getId());
if(cameraList == null || cameraList.isEmpty()) {
return JsonResultUtils.success(new ArrayList<>());
}
// Query Algorithm List
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}
Map<Long, Algorithm> algorithmMap = algorithmList.stream().collect(Collectors.toMap(Algorithm::getId, (s1 -> s1)));
// find out the Box up Config Camera Relate Algorithm Model
List<Long> algorithmExistIds = new ArrayList<>();
List<Algorithm> selectAlgorithms = new ArrayList<>();
for(Camera camera: cameraList) {
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByCamera(camera.getId());
if(cameraAlgorithms == null || cameraAlgorithms.isEmpty()) {
continue;
}
//
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
if(!algorithmExistIds.contains(cameraAlgorithm.getAlgorithmId())) {
algorithmExistIds.add(cameraAlgorithm.getAlgorithmId());
Algorithm algorithm = algorithmMap.get(cameraAlgorithm.getAlgorithmId());
if(algorithm!= null) {
selectAlgorithms.add(algorithm);
}
}
}
}
// Capture Image Select Algorithm
List<Map<String, Object>> outModels = new ArrayList<>();
for(Algorithm algorithm: selectAlgorithms) {
File tar = new File(modelDir + File.separator + algorithm.getNameEn() + File.separator);
if(!tar.exists()) {
continue;
}
// Read MD5 Value
String md5 = null;
File[] files = tar.listFiles();
if(files == null) {
continue;
}
// find to md5.txt Read md5 Value
for(File file: files) {
if(file.isFile() &&"md5.txt".equals(file.getName())) {
md5 = FileUtil.readString(file,"utf-8");
break;
}
}
//
if(StrUtil.isBlank(md5)) {
continue;
}
//
Map<String, Object> outModel = new HashMap<>();
outModel.put("modelName", algorithm.getNameEn());
outModel.put("showName", algorithm.getName());
outModel.put("files'", algorithm.getNameEn() + File.separator + md5 +".zip");
outModel.put("md5", md5);
outModel.put("ver","1.0");
outModel.put("verTimer", algorithm.getUpdatedAt() == null? DateUtil.format(new Date(),"yyyy/MM/dd HH:mm:ss"): DateUtil.format(algorithm.getUpdatedAt(),"yyyy/MM/dd HH:mm:ss"));
outModel.put("run", 0);
outModels.add(outModel);
}
return JsonResultUtils.success(outModels);
}
}