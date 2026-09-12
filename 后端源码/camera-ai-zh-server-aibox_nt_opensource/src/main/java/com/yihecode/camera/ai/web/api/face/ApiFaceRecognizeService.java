package com.yihecode.camera.ai.web.api.face;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.https.face.HttpFaceRecognize;
import com.yihecode.camera.ai.https.face.HttpFaceService;
import com.yihecode.camera.ai.service.face.FaceReportService;
import com.yihecode.camera.ai.utils.ImageUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.vo.ReportMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
* Face Recognition Call - Face Recognition child Module
*/
@Slf4j
@Service
public class ApiFaceRecognizeService {

    @Resource
    private HttpFaceService httpFaceService;

    @Resource
    private FaceReportService faceReportService;

    /**
* Call Recognition Service, and Return Result
* @param filepath
* @param cameraId
* @return
*/
    public JsonResult<?> recognize(String filepath, Long cameraId) {
        try {
            String caputerTime = DateUtil.format(new Date(), "yyyy-MM-dd-HH-mm-ss");
            List<HttpFaceRecognize> httpFaceRecognizes = httpFaceService.recoginze(filepath, cameraId, caputerTime);
            if (httpFaceRecognizes.isEmpty()) {
                //not has Recognition Result, Delete File
FileUtil.del(filepath);
return JsonResultUtils.success();
}
// Push socket
Map<String, Object> socketData = new HashMap<>();
socketData.put("filepath", filepath);
socketData.put("resultJson", httpFaceRecognizes);
//
ReportMessage reportMessage = new ReportMessage();
reportMessage.setType("FACE_RECOGNIZE");
reportMessage.setParams(JSON.toJSONString(socketData));
reportMessage.setCameraId(String.valueOf(cameraId));

int index = 1;
for (HttpFaceRecognize httpFaceRecognize: httpFaceRecognizes) {
if (httpFaceRecognize.getLiveness().equals("0")) {// Filter Non Work Body
continue;
}
String exam = filepath.substring(filepath.lastIndexOf('.') + 1);
String outPath = filepath.replace("."+ exam,"_"+ index +"."+ exam);
String faceFilePath = ImageUtils.croppedImage2SafeBox(filepath, outPath, httpFaceRecognize.getBbox().get(0), httpFaceRecognize.getBbox().get(1), httpFaceRecognize.getBbox().get(2), httpFaceRecognize.getBbox().get(3));
int hasStranger = 0;
if (httpFaceRecognize.getFaceUserId() == 0) {
hasStranger = 1;
}
// Storage
FaceReport faceReport = new FaceReport();
faceReport.setCameraId(cameraId);
faceReport.setHasStranger(hasStranger);
faceReport.setGroupId(httpFaceRecognize.getFaceGroupId());
faceReport.setFaceId(httpFaceRecognize.getFaceImageId());
faceReport.setUserId(httpFaceRecognize.getFaceUserId());
faceReport.setResultJson(JSON.toJSONString(httpFaceRecognizes));
faceReport.setFilePath(faceFilePath);
faceReport.setCreatedAt(new Date());
faceReport.setCreatedMills(System.currentTimeMillis());
faceReportService.save(faceReport);
index ++;
}

} catch (Exception e) {
log.error("Call Face Recognition Service Exception:{}", e.getMessage());
FileUtil.del(filepath);
}
return JsonResultUtils.success();
}
}
