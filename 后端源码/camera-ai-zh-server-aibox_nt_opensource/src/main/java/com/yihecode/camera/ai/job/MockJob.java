package com.yihecode.camera.ai.job;

import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MockJob {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private LocationService locationService;

    public void runJob() {
//Location location = new Location();
// location.setId(1847469074477486082L);
// location.setBoxHeartTime(System.currentTimeMillis());
// locationService.updateById(location);
//
// Location location2 = new Location();
// location2.setId(1862428870423085058L);
// location2.setBoxHeartTime(System.currentTimeMillis());
// locationService.updateById(location2);
//
//
//
// Camera camera1 = new Camera();
// camera1.setId(1898200139878903810L);
// camera1.setAiboxExecStatus(1000);
//// camera1.setRunning(1);
// camera1.setAiboxExecTime(new Date());
// cameraService.updateById(camera1);
//
// Camera camera2 = new Camera();
// camera2.setId(1895673482806923265L);
// camera2.setAiboxExecStatus(1000);
//// camera2.setRunning(1);
// camera2.setAiboxExecTime(new Date());
// cameraService.updateById(camera2);

}
}
