package com.yihecode.camera.ai.web.app;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.app.dto.AppCameraDTO;
import com.yihecode.camera.ai.web.app.dto.AppLocationDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* app End Box Phase close
* @author Abyss
* @date 2023/12/26 15:08
*/
@Api(tags = "app End _ Box Phase close")
@SaCheckLogin
@Controller
@RequestMapping({"/app/location"})
public class AppLocationController {

    @Autowired
    private LocationService locationService;
    @Autowired
    private CameraService cameraService;


    @ApiOperation("Page Query Box List")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "page", value = "Page Number"),
            @ApiImplicitParam(name = "limit", value = "Page Size"),
            @ApiImplicitParam(name = "status", value = "Online Status (0- Offline 1- Online)"),
            @ApiImplicitParam(name = "name", value = "Box Name")
    })
    @GetMapping("listPage")
    @ResponseBody
    public PageResult<List<Location>> listPage(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer limit,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) String name) {
        IPage<Location> pageResult = locationService.listPage(page, limit, name, new ArrayList<>(), null);
        List<Location> records = pageResult.getRecords();
        if (records == null) {
            records = new ArrayList<>();
        }
        //
List<Location> datas = new ArrayList<>();
for (Location record: records) {
record.setOnline("0");
//
if (record.getBoxHeartTime()!= null && (System.currentTimeMillis() - record.getBoxHeartTime()) < 8 * 60 * 1000) {
record.setOnline("1");
}
LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.and(wapper ->wapper.eq(Camera::getLocationId, record.getId()).eq(Camera::getState,"0"));
// queryWrapper.eq(Camera::getLocationId, record.getId());
record.setCameraNum(cameraService.count(queryWrapper));
// Filter Status
if(status == null) {
datas.add(record);
} else if(status == 0 &&"0".equals(record.getOnline())) {
datas.add(record);
} else if(status == 1 &&"1".equals(record.getOnline())) {
datas.add(record);
}
}
return PageResultUtils.success(Integer.valueOf(datas.size()).longValue(), datas);
}

@SaIgnore
@ApiOperation("Page Query Box List")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size"),
@ApiImplicitParam(name ="status", value ="Online Status (0- Offline 1- Online)"),
@ApiImplicitParam(name ="name", value ="Box Name")
})
@GetMapping("listPageV2")
@ResponseBody
public PageResult<List<AppLocationDTO>> listPageV2(@RequestParam(defaultValue ="1") Integer page,
@RequestParam(defaultValue ="10") Integer limit,
@RequestParam(required = false) Integer status,
@RequestParam(required = false) String name) {

List<AppLocationDTO> appLocationDTOS = new ArrayList<>();

//
List<Location> locationList = locationService.list();
if(locationList == null || locationList.isEmpty()) {
return PageResultUtils.success(0L, appLocationDTOS);
}

for(Location location: locationList) {
if(location.getType() == null ||!"2".equalsIgnoreCase(location.getType()) || location.getIsDef() == null || location.getIsDef()!= 0) {
continue;
}

int online = 0;
String onlineText ="Offline";
if (location.getBoxHeartTime()!= null && (System.currentTimeMillis() - location.getBoxHeartTime()) < 8 * 60 * 1000) {
online = 1;
onlineText ="Online";
}

String activeTime ="-";
if(location.getBoxHeartTime()!= null && location.getBoxHeartTime()!= 0) {
try {
Date date = DateUtil.date(location.getBoxHeartTime());
activeTime = DateUtil.format(date,"yyyy-MM-dd HH:mm:ss");
} catch (Exception e) {
//
}
}

AppLocationDTO appLocationDTO = new AppLocationDTO();
appLocationDTO.setBoxId(location.getId());
appLocationDTO.setBoxName(location.getName());
appLocationDTO.setBoxNo(location.getBoxNo());
appLocationDTO.setBoxIpAddr(location.getIpAddr());
appLocationDTO.setActiveTime(activeTime);
appLocationDTO.setOnline(online);
appLocationDTO.setOnlineText(onlineText);
appLocationDTO.setCameraCount(0);
appLocationDTOS.add(appLocationDTO);
}

//
for(AppLocationDTO appLocationDTO: appLocationDTOS) {
List<Camera> cameraList = cameraService.listByBoxId(appLocationDTO.getBoxId());
List<AppCameraDTO> appCameraDTOS = new ArrayList<>();
if(cameraList == null || cameraList.isEmpty()) {
appLocationDTO.setCameras(appCameraDTOS);
} else {
for(Camera camera: cameraList) {

int predictState = 0;
String predictText ="not Enable";
if(camera.getRunning()!= null) {
if(camera.getRunning() == 0) {
predictText ="not make Use";
} else {
if(camera.getAiboxExecStatus()!= null && camera.getAiboxExecStatus() == 1000) {
predictState = 1;
predictText ="Inference in";
} else {
predictText ="not make Use";
}
}
}

AppCameraDTO appCameraDTO = new AppCameraDTO();
appCameraDTO.setCameraId(camera.getId());
appCameraDTO.setCameraName(camera.getName());
appCameraDTO.setPredictState(predictState);
appCameraDTO.setPredictText(predictText);
appCameraDTOS.add(appCameraDTO);
}
}
appLocationDTO.setCameras(appCameraDTOS);
appLocationDTO.setCameraCount(appCameraDTOS.size());
}
return PageResultUtils.success((long) appLocationDTOS.size(), appLocationDTOS);
}
}
