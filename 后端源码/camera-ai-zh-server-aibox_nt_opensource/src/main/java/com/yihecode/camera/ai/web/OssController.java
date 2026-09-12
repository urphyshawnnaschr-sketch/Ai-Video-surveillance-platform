package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.oss.OssService;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.oss.OssUtils;
import com.yihecode.camera.ai.web.dto.OssFileDTO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* A In oss
* @author Abyss
*/
@Api(tags = "Model Test Management")
@Slf4j
@Controller
@RequestMapping({"/oss"})
public class OssController {

    @Autowired
    private AlgorithmTaskService algorithmTaskService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private OssService ossService;

    @Value("${dataModelsDir}")
    public String MODEL_DIR;

    @Value("${proj-confs.oss-net:true}")
    public boolean ossNet;

    @SaCheckPermission("XXXXXX")
    @GetMapping({ "/clearTask" })
    @ResponseBody
    public JsonResult<?> clearTask() {
        try {
            List<AlgorithmTask> algorithmTasks = algorithmTaskService.list();
            for (AlgorithmTask task : algorithmTasks) {
                algorithmTaskService.removeById(task.getId());
            }
            return JsonResultUtils.success();
        } catch (Exception e) {
            return JsonResultUtils.fail();
        }
    }

    //use
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@GetMapping("/getFileOrigin")
@ResponseBody
public JsonResult<?> getFileOrigin(String suanfa, String platform) {
// Return Data
List<Map<String, Object>> dataList = new ArrayList<>();

try {
if(!ossNet) {
//return JsonResultUtils.fail("inner net Environment no Method Search outer net Model File, Please In Algorithm Card"More - Version Management"inner Operation.");
return JsonResultUtils.success(dataList);
}

// like Result no Method Chain connect outer net, Direct connect Back Tip
List<OssFileDTO> ossFiles = OssUtils.getOssFiles(platform, suanfa, ossNet);
if(ossFiles == null) {// Network Exception
//return JsonResultUtils.fail("inner net Environment no Method Search outer net Model File, Please In Algorithm Card"More - Version Management"inner Operation.");
return JsonResultUtils.success(dataList);
}


// Query Config Algorithm
Algorithm algorithm = algorithmService.getByNameEn(suanfa);
if(algorithm == null) {
return JsonResultUtils.success(dataList);
}

// Local Algorithm Package Directory
String path = FileUtils.pathTo(MODEL_DIR + File.separator + platform + File.separator + suanfa + File.separator);

// Local not has Download File, Direct connect Back Online File List
if(!FileUtil.exist(path)) {
for(OssFileDTO ossFileDTO: ossFiles) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("md5Str","not Consistent");
dataMap.put("localLength", 0);
dataMap.put("localSize","");
dataMap.put("length", ossFileDTO.getFilesize());
dataMap.put("size", FileUtils.convertBytes(ossFileDTO.getFilesize()));
dataMap.put("name", ossFileDTO.getFilename());
dataMap.put("type","blob");
dataMap.put("path", FileUtils.pathTo(platform + File.separator + suanfa + File.separator + ossFileDTO.getFilename()));
dataMap.put("thisVersion", ossFileDTO.getVersion());
dataMap.put("state","not Download");
dataList.add(dataMap);
}
return JsonResultUtils.success(dataList);
}

// Query Local make Use Algorithm Version
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
String useVersion ="";
if(!cameraAlgorithms.isEmpty()) {
CameraAlgorithm cameraAlgorithm = cameraAlgorithms.get(0);
useVersion = cameraAlgorithm.getAlgorithmVersion();
}

// Column out Local File List
for(OssFileDTO ossFileDTO: ossFiles) {
String filepath = path +"/"+ ossFileDTO.getFilename();
long len = 0l;
String md5Str ="not Consistent";
String state ="not Download"; // not Download Enabled in Download not Enabled has Update
if(FileUtil.exist(filepath)) {
File file = new File(filepath);
len = file.length();

// Compare Consistent Property
if(len == ossFileDTO.getFilesize()) {
md5Str ="Consistent";
}

// Determine Download Status
if("".equals(useVersion)) {
state ="Download not Enabled";
} else if(useVersion.equals(ossFileDTO.getVersion())){
if (len == ossFileDTO.getFilesize()) {
state ="Enabled in";
} else {
state ="Enabled in, has Update";
}
} else {
if (len == ossFileDTO.getFilesize()) {
state ="Download not Enabled";
} else {
state ="not Enabled, has Update";
}
}
}

Map<String, Object> dataMap = new HashMap<>();
dataMap.put("md5Str", md5Str);
dataMap.put("localLength", len);
dataMap.put("localSize", FileUtils.convertBytes(len));
dataMap.put("length", ossFileDTO.getFilesize());
dataMap.put("size", FileUtils.convertBytes(ossFileDTO.getFilesize()));
dataMap.put("name", ossFileDTO.getFilename());
dataMap.put("type","blob");
dataMap.put("path", FileUtils.pathTo(platform + File.separator + suanfa + File.separator + ossFileDTO.getFilename()));
dataMap.put("thisVersion", ossFileDTO.getVersion());
dataMap.put("state", state);
dataList.add(dataMap);
}
return JsonResultUtils.success(dataList);
} catch (Exception e) {
return JsonResultUtils.fail("Get Version List Error");
}
}

/**
* Again Download?
* @param suanfa
* @param platform
* @param fileName
* @return
*/
@SaCheckPermission("XXXXXX")
@GetMapping({"/reDownloadSingleFile"})
@ResponseBody
public JsonResult<?> reDownloadSingleFile(String suanfa, String platform, String fileName) {
// Delete Download Task
AlgorithmTask oldTask = algorithmTaskService.getByFileName(fileName);
if (null!= oldTask) {
algorithmTaskService.removeById(oldTask.getId());
}

return ossService.downloadFile(platform, suanfa, fileName, false);

// // Algorithm Package Storage Path
// String localPath = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ suanfa);
// if(!FileUtil.exist(localPath)) {
// FileUtil.mkdir(localPath);
//}
//
// try {
// // Add Download Task
// AlgorithmTask task = new AlgorithmTask();
// task.setState("0");
// task.setNameEn(suanfa);
// task.setRemark("");
// task.setFileName(fileName);
// task.setFilePath(localPath + fileName);
// algorithmTaskService.save(task);
//
// // Enable Download Task
// CompletableFuture<String> future = startDownloadSingleFile(task);
// future.thenAccept(result -> {
//
//});
// return JsonResultUtils.success();
//} catch (Exception e) {
// return JsonResultUtils.fail("Algorithm Package Download failed, Please Retry Later");
//}
}

/**
* Download Algorithm Package use
* @param suanfa
* @param platform
* @param fileName
* @return
*/
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@GetMapping({"/downloadSingleFile"})
@ResponseBody
public JsonResult<?> downloadSingleFile(String suanfa, String platform, String fileName) {
return ossService.downloadFile(platform, suanfa, fileName, true);
// if(StrUtil.isBlank(platform)) {
// return JsonResultUtils.fail("Please select Hardware Platform");
//}
// if(StrUtil.isBlank(suanfa)) {
// return JsonResultUtils.fail("Please select Algorithm Model");
//}
// if(StrUtil.isBlank(fileName)) {
// return JsonResultUtils.fail("Please select Algorithm File");
//}
//
// AlgorithmTask algorithmTask = algorithmTaskService.getByFileName(fileName);
// if (null!= algorithmTask) {
// return JsonResultUtils.fail("In Progress Download");
//}
//
// // Validate Local File Whether and OSS Consistent
// String localFilePath = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ suanfa +"/"+ fileName);
// File localFile = new File(localFilePath);
// if (localFile.exists()) {
// OssFileDTO ossFileDTO = OssUtils.getOssFile(platform, suanfa, fileName);
// if(ossFileDTO!= null) {
// if(ossFileDTO.getFilesize() == localFile.length()) {
// return JsonResultUtils.fail("Algorithm Package Exist, no Need Download");
//}
//}
//}
//
// // Create Algorithm Package Storage Directory
// String localPath = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ suanfa);
// if(!FileUtil.exist(localPath)) {
// FileUtil.mkdir(localPath);
//}
//
// try {
// // Add Download Task
// AlgorithmTask task = new AlgorithmTask();
// task.setState("0");
// task.setNameEn(suanfa);
// task.setFileName(fileName);
// task.setFilePath(localFilePath);
// task.setRemark("");
// algorithmTaskService.save(task);
//
// // Enable Download Task
// CompletableFuture<String> future = startDownloadSingleFile(task);
// future.thenAccept(result -> {
//
//});
// return JsonResultUtils.success();
//} catch (Exception e) {
// return JsonResultUtils.fail("Algorithm Package Download failed, Please Retry Later");
//}
}

/**
* Download Algorithm Package Task use
* @param task Download Task Object
* @return
*/
// private CompletableFuture<String> startDownloadSingleFile(AlgorithmTask task) {
// return CompletableFuture.supplyAsync(() -> {
// String[] parts = FileUtil.mainName(task.getFileName()).split("-"); // Corresponding to Algorithm Package File Name
// String platform = parts[0]; //"chaoxing"
// String nameEn = parts[1]; //"car"
//
// Algorithm algorithm = algorithmService.getByNameEn(nameEn);
//
//
// // Temp Hour File Storage Directory
// String tempPath = FileUtils.pathTo(MODEL_DIR +"/temp");
// if(!FileUtil.exist(tempPath)) {
// FileUtil.mkdir(tempPath);
//}
//
// //
// String ossFilePath = platform +"/"+ task.getNameEn() +"/"+ task.getFileName(); // oss Corresponding Path
// String tempFilePath = tempPath +"/"+ task.getNameEn(); // Temp Hour File
// String result = OssUtils.downloadOssFile(ossFilePath, tempFilePath);
// if(StrUtil.isBlank(result)) {// Download success
// // Move Download File to Algorithm Directory
// FileUtil.move(new File(tempFilePath), new File(task.getFilePath()), true);
//
// //
// AlgorithmFile algorithmFile = new AlgorithmFile();
// algorithmFile.setFileName(task.getFileName()); // File Name Name
// algorithmFile.setNameEn(nameEn);
// algorithmFile.setPlatform(platform);
// algorithmFileService.save(algorithmFile);
//
// // Success
// task.setState("1");
// algorithmTaskService.updateById(task);
//
// // Push Message to Frontend
// AccessControlMessage accessControlMessage = new AccessControlMessage();
// accessControlMessage.setType("0");
// accessControlMessage.setTitle(String.format("%s(%s) Download Complete Complete", algorithm.getName(), task.getNameEn()));
// downloadWebsocket.sendToAll(JSON.toJSONString(accessControlMessage));
//
// // Delete Download Record
// // algorithmTaskService.removeById(task.getId());
//} else {
// // Failed
// task.setState("2");
// algorithmTaskService.updateById(task);
//
// // Push Message to Frontend
// AccessControlMessage accessControlMessage = new AccessControlMessage();
// accessControlMessage.setType("1");
// accessControlMessage.setTitle(String.format("%s(%s) Download failed, %s", algorithm.getName(), task.getNameEn(), result));
// downloadWebsocket.sendToAll(JSON.toJSONString(accessControlMessage));
//
// // Delete Download Record
// // algorithmTaskService.removeById(task.getId());
//}
//
// // remove by zhoumingxing 2024-10-20 for What need Remove Update Algorithm Config Version?
// // Update cameraAlgorithm like Result not has Version, Update Version
// /*
// String version = parts[2];
// if (algorithm!= null) {
// List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
// for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
// if (StringUtils.isBlank(cameraAlgorithm.getAlgorithmVersion())) {
// cameraAlgorithm.setAlgorithmVersion(version);
// cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
//}
//}
//}
// */
//
// return"success";
//});
//}

/**
* Download Algorithm Package in Degree reverse Feedback use
* @param suanfa Compute Package File Name Name
* @return
*/
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@GetMapping({"/downloadCheck"})
@ResponseBody
public JsonResult<?> downloadCheck(String suanfa) {
AlgorithmTask task = algorithmTaskService.getByFileName(suanfa);
if (null == task)
return JsonResultUtils.success("does not exist Download Task");

Map<String, Object> resultMap = new HashMap<>();
if (StrUtil.isNotBlank(task.getFilePath())) {
File file = new File(task.getFilePath());
long length = file.length();
if (length == 0L) {
String tempPath = FileUtils.pathTo(MODEL_DIR +"/temp");
String tempFilePath = FileUtils.pathTo(tempPath +"/"+ task.getFileName());
if(FileUtil.exist(tempFilePath)) {
File tempFile = new File(tempFilePath);
length = tempFile.length();
}
}
resultMap.put("length", length);
resultMap.put("size", FileUtils.convertBytes(length));
}
//
switch (task.getState()) {
case"0":
resultMap.put("msg","Download in");
return JsonResultUtils.success(resultMap);
case"1":
resultMap.put("msg","Download Complete Complete");
algorithmTaskService.removeById(task.getId());
return JsonResultUtils.success(resultMap);
case"2":
resultMap.put("msg","Download failed");
algorithmTaskService.removeById(task.getId());
return JsonResultUtils.success(resultMap);
case"3":
resultMap.put("msg","via is most new");
algorithmTaskService.removeById(task.getId());
return JsonResultUtils.success(resultMap);
}
return null; //?
}

// /**
// * By Platform Query Contain Algorithm
// */
// public List<String> getPlatformAlgorithm(String platform) {
// List<String> gitAlgorithmNames = new ArrayList<String>();
// try {
//// InetAddress address = InetAddress.getByName("101.200.212.176");
//// Boolean isNet = address.isReachable(1500);
//// if (!isNet) {
//// return gitAlgorithmNames;
////}
// if (!isNet("http://gitlab.yihecode.cn")) {
// return gitAlgorithmNames;
//}
// OssUtils ossUtils = new OssUtils();
// ObjectListing objectListing = ossUtils.fileList(platform +"/");
// for (String str: objectListing.getCommonPrefixes()) {
// gitAlgorithmNames.add(str.replace(platform +"/","").replace("/",""));
//}
//} catch (Exception e) {
// e.printStackTrace();
//}
// return gitAlgorithmNames;
//}

// private boolean isNet(String urlString) {
// try {
// URL url = new URL(urlString);
// HttpURLConnection connection = (HttpURLConnection) url.openConnection();
// connection.setRequestMethod("HEAD");
// connection.setConnectTimeout(1500);
// connection.setReadTimeout(1500);
// int responseCode = connection.getResponseCode();
// return (responseCode >= 200 && responseCode < 400);
//} catch (IOException e) {
// //e.printStackTrace();
// return false;
//}
//}


}
