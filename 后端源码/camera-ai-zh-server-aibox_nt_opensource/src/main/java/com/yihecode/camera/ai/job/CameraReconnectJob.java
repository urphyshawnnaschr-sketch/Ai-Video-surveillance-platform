package com.yihecode.camera.ai.job;

import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.netty.MessageSendHandler;
import com.yihecode.camera.ai.netty.data.CameraAddResponse;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* Camera re connect Task
* for at Camera is Normal Status, and Enable, But is Execute Status Exception Camera, in Line re connect Process
*
* @author zhou
* @since 2025-08-03
*/
@Slf4j
@Component
public class CameraReconnectJob {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MessageSendHandler messageSendHandler;

    private boolean handing = false;

    /**
* Execute
*/
    public void runJob() {
        //Control Non Concurrency Execute
if(handing) {
return;
}
handing = true;

try {
// Query All Status Normal and Enable, But is Execute Status Exception Camera
List<Camera> cameraList = cameraService.listException();
for(Camera camera: cameraList) {
// check find belong belong Box
Location location = locationService.getById(camera.getLocationId());
if(location == null) {
Camera modifyCamera = new Camera();
modifyCamera.setId(camera.getId());
modifyCamera.setRunning(0);
modifyCamera.setAiboxExecStatus(3000);
modifyCamera.setAiboxExecMsg("Box not find to");
cameraService.updateById(modifyCamera);
continue;
}

// Send Request
CameraAddResponse cameraAddResponse = messageSendHandler.sendAddCamera(location, camera);
if(cameraAddResponse.isStatus()) {
Camera modifyCamera = new Camera();
modifyCamera.setId(camera.getId());
modifyCamera.setAiboxExecStatus(1000);
modifyCamera.setAiboxExecMsg("Run in");
cameraService.updateById(modifyCamera);
} else {
Camera modifyCamera = new Camera();
modifyCamera.setId(camera.getId());
// modifyCamera.setAiboxExecStatus(3000);
modifyCamera.setAiboxExecMsg(cameraAddResponse.getMsg());
cameraService.updateById(modifyCamera);
}

// Wait 10 s, Prevent Frequent for One Box send Start Request import Cause Algorithm Process Exception
Thread.sleep(10000);
}
} catch (Exception e) {
log.error("Scheduled Task _ Camera re connect Exception", e);
} finally {
handing = false;
}
}
}
