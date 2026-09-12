package com.yihecode.camera.ai.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.AlgorithmFile;
import com.yihecode.camera.ai.entity.AlgorithmTask;
import com.yihecode.camera.ai.service.AlgorithmFileService;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.AlgorithmTaskService;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.vo.AccessControlMessage;
import com.yihecode.camera.ai.web.dto.OssFileDTO;
import com.yihecode.camera.ai.websocket.DownloadWebsocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
* oss Phase close Service
*
* @author zhoumingxing
* @date 2024-10-20
*/
@Slf4j
@Component
public class OssService {

    @Resource
    private AlgorithmFileService algorithmFileService;

    @Resource
    private AlgorithmTaskService algorithmTaskService;

    @Resource
    private AlgorithmService algorithmService;

    @Autowired
    private DownloadWebsocket downloadWebsocket;

    @Value("${dataModelsDir}")
    public String MODEL_DIR;

    /**
* Download File
* @param platform
* @param nameEn
* @param fileName
* @param sync
* @return
*/
    public JsonResult<?> downloadFile(String platform, String nameEn, String fileName, boolean sync) {
        if(StrUtil.isBlank(platform)) {
            return JsonResultUtils.fail("Please select Hardware Platform");
        }
        if(StrUtil.isBlank(nameEn)) {
            return JsonResultUtils.fail("Please select Algorithm Model");
        }
        if(StrUtil.isBlank(fileName)) {
            return JsonResultUtils.fail("Please select Algorithm File");
        }

        AlgorithmTask algorithmTask = algorithmTaskService.getByFileName(fileName);
        if (null != algorithmTask) {
            return JsonResultUtils.fail("In Progress Download");
        }

        //Validate Local File Whether and OSS Consistent
String localFilePath = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ nameEn +"/"+ fileName);
File localFile = new File(localFilePath);
if (localFile.exists()) {
OssFileDTO ossFileDTO = OssUtils.getOssFile(platform, nameEn, fileName);
if(ossFileDTO!= null) {
if(ossFileDTO.getFilesize() == localFile.length()) {
return JsonResultUtils.fail("Algorithm Package Exist, no Need Download");
}
}
}

// Create Algorithm Package Storage Directory
String localPath = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ nameEn);
if(!FileUtil.exist(localPath)) {
FileUtil.mkdir(localPath);
}

try {
// Add Download Task
AlgorithmTask task = new AlgorithmTask();
task.setState("0");
task.setNameEn(nameEn);
task.setFileName(fileName);
task.setFilePath(localFilePath);
task.setRemark("");
algorithmTaskService.save(task);

// Enable Download Task
CompletableFuture<String> future = downloading(task);
if(sync) {
try {
String result = future.get();
return JsonResultUtils.success(true);
} catch (Exception e) {
return JsonResultUtils.success(false);
} finally {
algorithmTaskService.removeById(task.getId());
}
} else {
return JsonResultUtils.success(false);
}
} catch (Exception e) {
return JsonResultUtils.fail("Algorithm Package Download failed, Please Retry Later");
}
}

/**
* Download Algorithm Package Task use
* @param task Download Task Object
* @return
*/
private CompletableFuture<String> downloading(AlgorithmTask task) {
return CompletableFuture.supplyAsync(() -> {
String[] parts = FileUtil.mainName(task.getFileName()).split("-"); // Corresponding to Algorithm Package File Name
String platform = parts[0]; //"chaoxing"
String nameEn = parts[1]; //"car"

Algorithm algorithm = algorithmService.getByNameEn(nameEn);

// Temp Hour File Storage Directory
String tempPath = FileUtils.pathTo(MODEL_DIR +"/temp");
if(!FileUtil.exist(tempPath)) {
FileUtil.mkdir(tempPath);
}

//
String ossFilePath = platform +"/"+ task.getNameEn() +"/"+ task.getFileName(); // oss Corresponding Path
String tempFilePath = tempPath +"/"+ task.getNameEn(); // Temp Hour File
String result = OssUtils.downloadOssFile(ossFilePath, tempFilePath);
if(StrUtil.isBlank(result)) {// Download success
// Move Download File to Algorithm Directory
FileUtil.move(new File(tempFilePath), new File(task.getFilePath()), true);

//
AlgorithmFile algorithmFile = new AlgorithmFile();
algorithmFile.setFileName(task.getFileName()); // File Name Name
algorithmFile.setNameEn(nameEn);
algorithmFile.setPlatform(platform);
algorithmFileService.save(algorithmFile);

// Success
task.setState("1");
algorithmTaskService.updateById(task);

// Push Message to Frontend
AccessControlMessage accessControlMessage = new AccessControlMessage();
accessControlMessage.setType("0");
accessControlMessage.setTitle(String.format("%s(%s) Download Complete Complete", algorithm.getName(), task.getNameEn()));
downloadWebsocket.sendToAll(JSON.toJSONString(accessControlMessage));

// Delete Download Record
algorithmTaskService.removeById(task.getId());
} else {
// Failed
task.setState("2");
algorithmTaskService.updateById(task);

// Push Message to Frontend
AccessControlMessage accessControlMessage = new AccessControlMessage();
accessControlMessage.setType("1");
accessControlMessage.setTitle(String.format("%s(%s) Download failed, %s", algorithm.getName(), task.getNameEn(), result));
downloadWebsocket.sendToAll(JSON.toJSONString(accessControlMessage));

// Delete Download Record
algorithmTaskService.removeById(task.getId());

// Delete Temp Hour File
if(FileUtil.exist(FileUtils.pathTo(tempFilePath +"/"+ task.getFileName()))) {
FileUtil.del(FileUtils.pathTo(tempFilePath +"/"+ task.getFileName()));
}
}

// remove by zhoumingxing 2024-10-20 for What need Remove Update Algorithm Config Version?
// Update cameraAlgorithm like Result not has Version, Update Version
/*
String version = parts[2];
if (algorithm!= null) {
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
if (StringUtils.isBlank(cameraAlgorithm.getAlgorithmVersion())) {
cameraAlgorithm.setAlgorithmVersion(version);
cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
}
}
}
*/

return"success";
});
}

}
