package com.yihecode.camera.ai.netty;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.javacv.DecodeRtspUlr;
import com.yihecode.camera.ai.netty.data.*;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.BoxVersionService;
import com.yihecode.camera.ai.service.CameraAlgorithmService;
import com.yihecode.camera.ai.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
* Netty Message Send Process
*
* @author 465769438@qq.com
* @since 2025/3/5
*/
@Slf4j
@Component
public class MessageSendHandler {

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private BoxVersionService boxVersionService;

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    @Value("${dataModelsDir}")
    private String dataModelDir;

    //Send Camera Add Process
public CameraAddResponse sendAddCamera(Location location, Camera camera) {
// if(camera.getRunning() == null || camera.getRunning()!= 1) {
// return CameraAddResponse.builder().status(true).msg("Operation success").cameraId(camera.getId()).build();
//}

List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByCamera(camera.getId());
if(cameraAlgorithmList == null || cameraAlgorithmList.isEmpty()) {
return CameraAddResponse.builder().status(false).msg("not has Config Algorithm").cameraId(camera.getId()).build();
}

List<CameraAddRequest.Algo> algos = new ArrayList<>();
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
Algorithm algorithm = algorithmService.getById(cameraAlgorithm.getAlgorithmId());
if(algorithm == null) {
continue;
}

// Temp Control Alarm, the Algorithm is by Temp Measure Camera Alarm Trigger, and not belong at Image Recognition
if("temperatureAlarm".equalsIgnoreCase(algorithm.getNameEn())) {
continue;
}

BoxVersion boxVersion = boxVersionService.getData(location.getId(), algorithm.getId());
if(boxVersion == null) {
continue;
}

String platform = location.getPlatform(); //algorithm.getPlatform();
String nameEn = algorithm.getNameEn();
Integer shareMode = algorithm.getShareMode() == null? 0: algorithm.getShareMode();

// log.info("cameraAlgorithm.getAlgorithmVersion() {}", cameraAlgorithm.getAlgorithmVersion());
// String version = StrUtil.isNotBlank(cameraAlgorithm.getAlgorithmVersion())? cameraAlgorithm.getAlgorithmVersion(): this.findAlgoVer(location.getId(), algorithm, platform);

// Update version to CameraAlgorithm
// if(StrUtil.isBlank(cameraAlgorithm.getAlgorithmVersion())) {
// cameraAlgorithm.setAlgorithmVersion(version);
// cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
//}

// Confidence, like Result Need receive Set Data, rule make Use receive Set Data Confidence
float confidence = 0;
if(algorithm.getCollectFlag()!= null && algorithm.getCollectFlag() == 1 && algorithm.getCollectConfidence()!= null) {
confidence = algorithm.getCollectConfidence();
}

if(confidence == 0) {
confidence = cameraAlgorithm.getConfidence();
}

CameraAddRequest.Algo algo = new CameraAddRequest.Algo();
algo.setAlgoId(algorithm.getId());
algo.setAlgoName(algorithm.getName());
algo.setAlgoNameEn(algorithm.getNameEn());
algo.setAlgoConfidence(confidence);
algo.setAlgoVer(boxVersion.getVersionNum());
algo.setAlgoRois(cameraAlgorithm.getImagePoints() == null?"": cameraAlgorithm.getImagePoints());
algo.setAlgoLines(cameraAlgorithm.getLineImagePoints() == null?"": cameraAlgorithm.getLineImagePoints());
algo.setZipFile(String.format("/%s/%s/%s-%s-%s.zip", platform, nameEn, platform, nameEn, boxVersion.getVersionNum()));
algo.setZipName(String.format("%s-%s-%s.zip", platform, nameEn, boxVersion.getVersionNum()));
algo.setMd5(DigestUtil.md5Hex(FileUtil.newFile(FileUtils.pathTo(dataModelDir +"/"+ algo.getZipFile()))));
algo.setShareMode(shareMode);
algo.setExtras(algorithm.getExtras() == null?"": algorithm.getExtras());
algos.add(algo);
}

CameraAddRequest cameraAddRequest = new CameraAddRequest();
cameraAddRequest.setType(MessageType.ADD_CAMERA.getType());
cameraAddRequest.setSn(location.getBoxNo());
cameraAddRequest.setRequestId(IdUtil.randomUUID());
cameraAddRequest.setCameraId(camera.getId());
cameraAddRequest.setIntervalTime(camera.getIntervalTime());
cameraAddRequest.setRtspUrl(DecodeRtspUlr.processRtspUrl(camera.getRtspUrl()));
cameraAddRequest.setVideoFps(camera.getVideoFps());
cameraAddRequest.setAlgorithms(algos);

Response response = messageSenderAndWaiter.sendRequest(cameraAddRequest);
if(response == null) {
return CameraAddResponse.builder().status(false).msg("Unknown Error").cameraId(camera.getId()).build();
}
return (CameraAddResponse) response;
}

// Send Camera Delete Process
public CameraDelResponse sendDelCamera(Location location, Camera camera) {
if(camera.getRunning() == null || camera.getRunning()!= 1) {
return CameraDelResponse.builder().status(true).msg("Operation success").cameraId(camera.getId()).build();
}

CameraDelRequest cameraDelRequest = new CameraDelRequest();
cameraDelRequest.setType(MessageType.DEL_CAMERA.getType());
cameraDelRequest.setSn(location.getBoxNo());
cameraDelRequest.setRequestId(IdUtil.randomUUID());
cameraDelRequest.setCameraId(camera.getId());
Response response = messageSenderAndWaiter.sendRequest(cameraDelRequest);
if(response == null) {
return CameraDelResponse.builder().status(false).msg("Unknown Error").cameraId(camera.getId()).build();
}
return (CameraDelResponse) response;
}

// Send Algorithm Expand Param Update Process
public AlgoExtrasResponse sendAlgoExtras(Location location, Long algoId, String extras) {
AlgoExtrasRequest algoExtrasRequest = new AlgoExtrasRequest();
algoExtrasRequest.setType(MessageType.ALGO_EXTRAS.getType());
algoExtrasRequest.setSn(location.getBoxNo());
algoExtrasRequest.setRequestId(IdUtil.randomUUID());
algoExtrasRequest.setAlgoId(algoId);
algoExtrasRequest.setExtras(extras);
Response response = messageSenderAndWaiter.sendRequest(algoExtrasRequest);
if(response == null) {
return AlgoExtrasResponse.builder().status(false).msg("Unknown Error").build();
}
return (AlgoExtrasResponse) response;
}

// Send Algorithm Expand Param Update Process
public DelFilesResponse sendDelFiles(Location location, Long algoId, String algoName, String algoCode) {
DelFilesRequest delFilesRequest = new DelFilesRequest();
delFilesRequest.setType(MessageType.DEL_FILES.getType());
delFilesRequest.setSn(location.getBoxNo());
delFilesRequest.setRequestId(IdUtil.randomUUID());
delFilesRequest.setAlgoId(algoId);
delFilesRequest.setAlgoName(algoName);
delFilesRequest.setAlgoCode(algoCode);
Response response = messageSenderAndWaiter.sendRequest(delFilesRequest);
if(response == null) {
return DelFilesResponse.builder().status(false).msg("Unknown Error").build();
}
return (DelFilesResponse) response;
}

/**
* Send Face Recognition Process
* @param searchId Search ID, tbl_biz_face_search.id
*/
public FaceRecognizeResponse sendFaceRecognize(Location location, Long searchId) {
FaceRecognizeRequest faceRecognizeRequest = new FaceRecognizeRequest();
faceRecognizeRequest.setType(MessageType.FACE_RECOGNIZE.getType());
faceRecognizeRequest.setSn(location.getBoxNo());
faceRecognizeRequest.setRequestId(IdUtil.randomUUID());
faceRecognizeRequest.setSearchId(searchId);
Response response = messageSenderAndWaiter.sendRequest(faceRecognizeRequest);
if(response == null) {
return FaceRecognizeResponse.builder().status(false).msg("Unknown Error").build();
}
return (FaceRecognizeResponse) response;
}

/**
* Send Face Recognition Process
* @param imageName Image File Name
*/
public FaceRecognize2Response sendFaceRecognize2(Location location, String imageName) {
FaceRecognize2Request faceRecognizeRequest = new FaceRecognize2Request();
faceRecognizeRequest.setType(MessageType.FACE_RECOGNIZE2.getType());
faceRecognizeRequest.setSn(location.getBoxNo());
faceRecognizeRequest.setRequestId(IdUtil.randomUUID());
faceRecognizeRequest.setImageName(imageName);
Response response = messageSenderAndWaiter.sendRequest(faceRecognizeRequest);
if(response == null) {
return FaceRecognize2Response.builder().status(false).msg("Unknown Error").build();
}
return (FaceRecognize2Response) response;
}

/**
* Send Face than for Process
* @param image1 No One sheet Image File Name
* @param image2 No Two sheet Image File Name
*/
public FaceCompareResponse sendFaceCompare(Location location, String image1, String image2) {
FaceCompareRequest faceCompareRequest = new FaceCompareRequest();
faceCompareRequest.setType(MessageType.FACE_COMPARE.getType());
faceCompareRequest.setSn(location.getBoxNo());
faceCompareRequest.setRequestId(IdUtil.randomUUID());
faceCompareRequest.setImage1(image1);
faceCompareRequest.setImage2(image2);
Response response = messageSenderAndWaiter.sendRequest(faceCompareRequest);
if(response == null) {
return FaceCompareResponse.builder().status(false).msg("Unknown Error").build();
}
return (FaceCompareResponse) response;
}

/**
* Notification Stream Close
* @param location
* @param cameraId
* @param cloudStreamPort
*/
public void sendStreamClose(Location location, Long cameraId, Integer cloudStreamPort) {
StreamCloseRequest request = new StreamCloseRequest();
request.setType(MessageType.STREAM_CLOSE.getType());
request.setSn(location.getBoxNo());
request.setRequestId(IdUtil.randomUUID());
request.setCameraId(cameraId);
request.setCloudStreamPort(cloudStreamPort);
messageSenderAndWaiter.sendRequest(request);
}

// // check find Algorithm Version
// private String findAlgoVer(Long locId, Algorithm algorithm, String platform) {
// List<CameraAlgorithm> caList = cameraAlgorithmService.listByAlgorithmAndBoxId(algorithm.getId(), locId);
// log.info("Box Mount Algorithm algoId: {}, locId: {}, caList {}", algorithm.getId(), locId, caList);
// if(caList == null) {
// caList = new ArrayList<>();
//}
//
// for(CameraAlgorithm ca: caList) {
// if(StrUtil.isNotBlank(ca.getAlgorithmVersion())) {
// return ca.getAlgorithmVersion();
//}
//}
//
// // check find Local File System most big Algorithm Version
// String path = FileUtils.pathTo(dataModelDir +"/"+ platform +"/"+ algorithm.getNameEn());
// if(!FileUtil.exist(path)) {// not has Download Model File
// return"";
//}
//
// List<String> fileNames = FileUtil.listFileNames(path);
// if(fileNames == null || fileNames.isEmpty()) {
// return"";
//}
//
// // Filter select All zip File
// List<String> zipFileNames = new ArrayList<>();
// for(String fileName: fileNames) {
// String ext = FileUtil.extName(fileName);
// if("zip".equalsIgnoreCase(ext)) {
// zipFileNames.add(fileName);
//}
//}
//
// if(zipFileNames.isEmpty()) {
// return"";
//}
//
// Collections.sort(zipFileNames);
//
// // most big File Version No
// String maxFileName = zipFileNames.get(zipFileNames.size() - 1);
// String mainName = FileUtil.mainName(maxFileName);
// String[] parts = mainName.split("-");
// return parts[2];
//}
}
