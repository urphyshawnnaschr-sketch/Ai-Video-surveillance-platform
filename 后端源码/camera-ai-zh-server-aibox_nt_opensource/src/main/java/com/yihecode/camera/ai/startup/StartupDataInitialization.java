package com.yihecode.camera.ai.startup;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.CameraBatchImport;
import com.yihecode.camera.ai.job.ReportSummaryJob;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.ap.ApMenusService;
import com.yihecode.camera.ai.service.ap.ApProjectService;
import com.yihecode.camera.ai.web.api.face.ApiFaceFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
* Description: Init Data or
* <p>
* Date: 2023/7/21
*/
@Slf4j
@Component
public class StartupDataInitialization implements CommandLineRunner {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ApProjectService apProjectService;

    @Resource
    private CameraBatchImportService cameraBatchImportService;

    @Resource
    private AlgorithmTaskService algorithmTaskService;

    @Autowired
    private ApMenusService apMenusService;

    @Autowired
    private MediaServerService mediaServerService;

    @Autowired
    private ReportSummaryJob reportSummaryJob;

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    private MinioInstall minioInstall;

    @Value("${uploadDir}")
    private String uploadDir;

    @Value("${HOST_IP}")
    private String hostIp;

    @Value("${hostIpFile}")
    private String hostIpFile;

    @Override
    public void run(String... args) throws Exception {
        //Create Directory
FileUtil.mkdir(uploadDir);

// Version Info
log.info("Version No: {}", projectConfig.getVersion());
log.info("Chip Config:{}", projectConfig.getPlatform());

// not Enabled Speaker Pole Function can
if(!projectConfig.isSoundColumnEnable()) {
apMenusService.removeCurrentAndSub(1807663645489991681L);
}

// not Enabled Face Recognition Function can
if(!projectConfig.isFaceRecognizeEnable()) {
apMenusService.removeCurrentAndSub(1807667443054776321L);
}

// not Enabled Person Stream Quantity Function can
if(!projectConfig.isPeopleTrackEnable()) {
apMenusService.removeCurrentAndSub(1833049157994610699L);
}

// not Enable Handle public Push
if(!projectConfig.isPushToWeChat()) {
apMenusService.removeCurrentAndSub(1807664150073151490L);
}

// not Enable SMS Push
if(!projectConfig.isPushToSms()) {
apMenusService.removeCurrentAndSub(1807663982804307970L);
}

// not Enable Voice Push
if(!projectConfig.isPushToVoice()) {
apMenusService.removeCurrentAndSub(1807664553732968449L);
}

// not Enable GB Standard Service
if(!projectConfig.isWvpGbEnable()) {
apMenusService.removeCurrentAndSub(1788591303976218629L);
apMenusService.removeCurrentAndSub(1788591303976218630L);
}

// not Enable Recording Function can
if(!projectConfig.isRecordEnable()) {
configService.saveData("Recording open close","isRecord","false");
}

// not Enable Annotation Function can
if(!projectConfig.isApEnable()) {
//
}

// Process order Upgrade, Function can not real current
apMenusService.removeCurrentAndSub(1807662561191104513L);

// Set Algorithm Hardware Type
// remove at 2025-04-15 Need Support multi kind Box Device
// algorithmService.updatePlatform(projectConfig.getPlatform());

// Set Stream Media hook Info
// mediaRestfulService.setServerConfig();

// Init Media Node
mediaServerService.initData();

// Init Config Info
configService.initData(hostIp, hostIpFile);

// Init Node Info
locationService.initData();

// Delete via Delete Camera Relate Algorithm
cameraService.removeDeleted();

//
apProjectService.initProject();

//
algorithmTaskService.removeAll();

// Face Alarm Threshold
String faceReportDuplicate = configService.getByValTag("faceReportDuplicate");
if(StrUtil.isNotBlank(faceReportDuplicate)) {
ApiFaceFilter.getInst().setThreshold(Convert.toInt(faceReportDuplicate, 300));
}

// Init History Alarm Count Data
reportSummaryJob.runInit();

// Init minio
minioInstall.install();

// Again Import, Prevent System Restart etc Problem import Cause Import not Complete whole
new Thread(new Runnable() {
@Override
public void run() {
List<CameraBatchImport> cameraBatchImports = cameraBatchImportService.listByState(1);
for (CameraBatchImport cameraBatchImport: cameraBatchImports) {
cameraBatchImportService.saveImport(cameraBatchImport);
}
}
}).start();
}
}
