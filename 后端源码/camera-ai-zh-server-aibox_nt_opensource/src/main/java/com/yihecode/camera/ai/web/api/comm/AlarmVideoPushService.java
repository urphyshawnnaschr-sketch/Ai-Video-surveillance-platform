package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.yihecode.camera.ai.config.MinioConfig;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.Record;
import com.yihecode.camera.ai.utils.FileUtils;
import io.minio.DownloadObjectArgs;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
* Alarm Video Third Party Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmVideoPushService {

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    private MinioConfig minioConfig;

    @Value("${uploadDir}")
    private String uploadDir;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    public void sendVideo(Record record, List<Long> reportIds) {
        CompletableFuture.runAsync(() -> {
            try {
                //
String thirdPushVideoUrl = projectConfig.getThirdPushVideoUrl();
String thirdPushVideoType = projectConfig.getThirdPushVideoType();
if(StrUtil.isBlank(thirdPushVideoUrl)) {
log.info("Third Party Video Push Failed, Push Address not Config");
return;
}

//
Map<String, Object> videoInfo = new HashMap<>();
videoInfo.put("recordId", record.getId());
videoInfo.put("reportIds", JSON.toJSONString(reportIds));
videoInfo.put("videoName", record.getFileName());
videoInfo.put("videoSize", record.getFileSize());
videoInfo.put("videoExt", FileUtil.extName(record.getFileName()).toLowerCase());

// Only Push Video Address
if(StrUtil.isBlank(thirdPushVideoType) ||!"file".equalsIgnoreCase(thirdPushVideoType)) {
String playUrl = String.format("%s/%s", minioConfig.getUrl(), record.getRecordPath());
videoInfo.put("videoUrl", playUrl);
//log.info("{}", JSON.toJSONString(videoInfo));
String responseText = HttpUtil.post(thirdPushVideoUrl, JSON.toJSONString(videoInfo), 15000);
log.info("Third Party Video Push Complete Complete (Only url), Push Return Result, response={}", responseText);
return;
}

// Only Push Video File
String tempFileDir = uploadDir +"/"+"temp_download/";
FileUtil.mkdir(FileUtils.pathTo(tempFileDir));

String tempFile = FileUtils.pathTo(tempFileDir +"/"+ IdUtil.fastSimpleUUID() +".mp4");

boolean isOk = downloadFile("record", record.getRecordPath(), tempFile);
if(!isOk) {
log.info("Third Party Video Push Complete (Only File), Push Failed, minio Video File Download failed");
return;
}

Map<String, Object> params = new HashMap<>();
params.put("videoInfo", JSON.toJSONString(videoInfo));
params.put("videoFile", new File(tempFile));
String responseText = HttpUtil.post(thirdPushVideoUrl, params, 300000);
log.info("Third Party Video Push Complete (Only File), Push Return Result, response={}", responseText);
} catch (Exception e) {
log.error("Third Party Video Push Exception", e);
}
}, executor);
}

/**
* from minio Download File
* @param bucketName
* @param objectName
* @param destFilePath
* @return
*/
private boolean downloadFile(String bucketName, String objectName, String destFilePath) {
try {
minioConfig.getMinioClient().downloadObject(
DownloadObjectArgs.builder()
.bucket(bucketName)
.object(objectName)
.filename(destFilePath)
.build()
);
//log.info("Download success");
return true;
} catch (MinioException e) {
log.error("MinIO Download File Error:", e);
} catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
log.error("MinIO Download File Other Error:"+ e);
}
return false;
}
}
