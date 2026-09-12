package com.yihecode.camera.ai.job;

import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraAlgorithm;
import com.yihecode.camera.ai.isapi.ISAPIService;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.CameraAlgorithmService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
* Temp Control Camera self Dynamic Config
*/
@Slf4j
@Component
public class TemperatureCameraConfigJob {

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ISAPIService isapiService;

    @Value("${temperature-alarm-callback.ip:}")
    private String hostIp;

    @Value("${temperature-alarm-callback.port:}")
    private String hostPort;

    @Value("${temperature-alarm-callback.url:}")
    private String hostUrl;

    //via Success Set Camera, like Result via Set Success, that What not again in Line Check
private final List<Long> successCameraList = new ArrayList<>();

// Execute in Status
private boolean handing = false;

/**
* Execute JOB
*/
public void runJob() {
if(handing) {
return;
}
handing = true; // Processing

try {
// //
// String ipPort = configService.getByValTag("ipAddr");
// if(StrUtil.isBlank(ipPort)) {
// log.info("Scheduled Task _ not has Config IP and port, Please Check < Basic Config ->ip Address > Whether Config");
// return;
//}
//
// if(!ipPort.contains(":")) {
// log.info("Scheduled Task _ Please Check < Basic Config ->ip Address > Param, no Method Parse out ip and port, Value: {}", ipPort);
// return;
//}

if(StrUtil.isBlank(hostIp) || StrUtil.isBlank(hostPort) || StrUtil.isBlank(hostUrl)) {
log.info("Scheduled Task _ Temp Control Alarm,ip,port,url not Config, Please In application-prod.yml Config,temperature-alarm-callback: > ip(java Service IP), > port(java Service PORT), > url(Fixed Fixed:/api/temperature/alarm)");
return;
}

// Temp Control Alarm Algorithm
Algorithm algorithm = algorithmService.getByNameEn("temperatureAlarm");
if(algorithm == null) {
log.info("Scheduled Task _ Temp Control Alarm Algorithm does not exist");
return;
}

// Query Config Temp Control Algorithm Camera List
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
if(cameraAlgorithmList.isEmpty()) {
log.info("Scheduled Task _ not has Camera Config Temp Control Alarm Algorithm");
return;
}

// Loop Process Camera
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
Camera camera = cameraService.getById(cameraAlgorithm.getCameraId());
// Camera does not exist
if(camera == null) {
continue;
}

// Non Normal Status
if(camera.getState() == null || camera.getState()!= 0) {
continue;
}

// not Enable Camera
if(camera.getRunning() == null || camera.getRunning()!= 1) {
continue;
}

// Check Whether via Set Success over
if(successCameraList.contains(camera.getId())) {
continue;
}

//
boolean success = isapiService.setConfig(camera, hostIp, hostPort, hostUrl);
if(success) {
successCameraList.add(camera.getId());
}
}
} catch (Exception e) {
log.error("Scheduled Task _ Temp Control Camera Config Check and self Dynamic Config Exception", e);
} finally {
handing = false;
}
}
}
