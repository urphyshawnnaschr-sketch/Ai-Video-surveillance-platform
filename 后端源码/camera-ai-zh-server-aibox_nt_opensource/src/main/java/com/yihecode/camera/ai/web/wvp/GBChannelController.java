package com.yihecode.camera.ai.web.wvp;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.MediaServer;
import com.yihecode.camera.ai.entity.wvp.GBDevice;
import com.yihecode.camera.ai.entity.wvp.GBDeviceChannel;
import com.yihecode.camera.ai.javacv.TakePhoto;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.MediaServerService;
import com.yihecode.camera.ai.service.wvp.GBDeviceChannelService;
import com.yihecode.camera.ai.service.wvp.GBDeviceService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.wvp.dto.LocationChannelCountDTO;
import com.yihecode.camera.ai.web.wvp.vo.ChannelAssignVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Code Rule Scope https://blog.csdn.net/weixin_70208651/article/details/137799708

@Slf4j
@Api(tags ="GB Standard Channel Info Management")
@Controller
@RequestMapping({"/wvp/channel"})
public class GBChannelController {

@Autowired
private GBDeviceChannelService deviceChannelService;

@Autowired
private GBDeviceService deviceService;

@Autowired
private LocationService locationService;

@Autowired
private CameraService cameraService;

@Autowired
private TakePhoto takePhoto;

@Autowired
private MediaServerService mediaServerService;

@SaCheckPermission("gb-channel-list")
@ApiOperation("Query GB Standard Channel Info List")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size"),
@ApiImplicitParam(name ="deviceId", value ="Device ID"),
@ApiImplicitParam(name ="status", value ="Online Status, 0- Offline,1- Online")
})
@PostMapping("listPage")
@ResponseBody
public PageResult<List<GBDeviceChannel>> listPage(@RequestParam(defaultValue ="1") Integer page,
@RequestParam(defaultValue ="10") Integer limit,
@RequestParam(required = false) String deviceId,
@RequestParam(required = false) Integer cameraStatus,
@RequestParam(required = false) Integer boxStatus) {
IPage<GBDeviceChannel> deviceChannelIPage = deviceChannelService.listPage(page, limit, deviceId, cameraStatus, boxStatus);
List<GBDeviceChannel> records = deviceChannelIPage.getRecords();
if(records == null) {
records = new ArrayList<>();
}

if(!records.isEmpty()) {
List<GBDevice> gbDevices = deviceService.list();
if(gbDevices == null) {
gbDevices = new ArrayList<>();
}
//Map<String, String> deviceNameMap = gbDevices.stream().collect(Collectors.toMap(GBDevice::getDeviceId, GBDevice::getName));

Map<String, String> deviceNameMap = new HashMap<>();
gbDevices.forEach(device -> {
String id = device.getDeviceId();
String name = device.getName(); // can with for null
if (id!= null) {
deviceNameMap.put(id, name == null?"Unknown": name);
}
});

for(GBDeviceChannel record: records) {
record.setDeviceName(deviceNameMap.getOrDefault(record.getDeviceId(),"Unknown Device"));
}
}

return PageResultUtils.success(deviceChannelIPage.getTotal(), deviceChannelIPage.getRecords());
}

@ApiOperation("Query Box and Relate Camera Count")
@SaCheckPermission("gb-channel-list")
@GetMapping("box/channel/count")
@ResponseBody
public JsonResult<List<LocationChannelCountDTO>> listBoxAndChannelCount() {
List<LocationChannelCountDTO> locationChannelCountDTOS = new ArrayList<>();
List<Location> locations = locationService.listInferBox();
log.info("locations {}", locations);
for(Location location: locations) {
int count = deviceChannelService.countByLocation(location.getId());

LocationChannelCountDTO dto = new LocationChannelCountDTO();
dto.setId(location.getId());
dto.setName(location.getName());
dto.setChannelCount(count);
locationChannelCountDTOS.add(dto);
}
return JsonResultUtils.success(locationChannelCountDTOS);
}

@SaCheckPermission("gb-channel-list")
@ApiOperation("Assign Box / Server")
@PostMapping("assign")
@ResponseBody
public JsonResult<?> assign(@RequestBody ChannelAssignVO assignVO) {
if(assignVO.getBoxId() == null) {
return JsonResultUtils.fail("not Select Box");
}

if(ObjectUtil.isEmpty(assignVO.getChannelIds())) {
return JsonResultUtils.fail("not Select Channel");
}

for(Long channelId: assignVO.getChannelIds()) {
GBDeviceChannel deviceChannel = new GBDeviceChannel();
deviceChannel.setId(channelId);
deviceChannel.setLocationId(assignVO.getBoxId());
deviceChannelService.updateById(deviceChannel);
}
return JsonResultUtils.success();
}

@SaCheckPermission("gb-channel-list")
@ApiOperation("Splice connect Stream Address")
@ApiImplicitParam(name ="id", value ="GB Standard Channel ID")
@GetMapping("stream/url")
@ResponseBody
public JsonResult<?> streamUrl(Long id) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("mediaServerId", 0L);
dataMap.put("streamUrl","");

// Query GB Standard Channel
GBDeviceChannel gbDeviceChannel = deviceChannelService.getById(id);
if(gbDeviceChannel == null) {
return JsonResultUtils.success(dataMap);
}

// Query most small Load Media Node
Long mediaServerId = cameraService.getMinMediaServer();
MediaServer mediaServer = mediaServerService.getById(mediaServerId);

if(mediaServer == null) {
return JsonResultUtils.success(dataMap);
}
//log.info("most small Load mediaServer Node {} {}", mediaServer.getIp(), mediaServer.getHttpPort());

//mediaServer = mediaServerService.getByIpAndPort("testpoc.yihecode.com", 7066);
//log.info("Refer Fixed IP and Port mediaServer Node {} {}", mediaServer.getIp(), mediaServer.getHttpPort());

// Splice connect Stream Address
String streamUrl = String.format("rtsp://%s:%s/rtp/%s_%s", mediaServer.getIp(), mediaServer.getRtspPort(), gbDeviceChannel.getDeviceId(), gbDeviceChannel.getChannelId());

// Return Data
dataMap.put("streamUrl", streamUrl);
dataMap.put("mediaServerId", mediaServerId);
return JsonResultUtils.success(dataMap);
}

@SaCheckPermission("gb-channel-list")
@ApiOperation("Splice connect Stream Address")
@ApiImplicitParam(name ="id", value ="GB Standard Channel ID")
@GetMapping("stream/check")
@ResponseBody
public JsonResult<?> checkStream(Long id) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("errMsg","OK");
dataMap.put("success", false);

// Query GB Standard Channel
GBDeviceChannel gbDeviceChannel = deviceChannelService.getById(id);
if(gbDeviceChannel == null) {
dataMap.put("errMsg","GB Standard Channel does not exist");
return JsonResultUtils.success(dataMap);
}

// Query most small Load Media Node
Long mediaServerId = cameraService.getMinMediaServer();
MediaServer mediaServer = mediaServerService.getById(mediaServerId);
if(mediaServer == null) {
dataMap.put("errMsg","Media Node does not exist");
return JsonResultUtils.success(dataMap);
}

//log.info("most small Load mediaServer Node {} {}", mediaServer.getIp(), mediaServer.getHttpPort());

//mediaServer = mediaServerService.getByIpAndPort("testpoc.yihecode.com", 7066);
//log.info("Refer Fixed IP and Port mediaServer Node {} {}", mediaServer.getIp(), mediaServer.getHttpPort());

// Splice connect Stream Address
String streamUrl = String.format("rtsp://%s:%s/rtp/%s_%s", mediaServer.getIp(), mediaServer.getRtspPort(), gbDeviceChannel.getDeviceId(), gbDeviceChannel.getChannelId());
Map<String, String> result = takePhoto.take(streamUrl);
if(result == null) {
dataMap.put("errMsg","Video Stream no Method Connection, Please Again Try Try");
return JsonResultUtils.success(dataMap);
}

// Return Data
dataMap.put("success", true);
dataMap.putAll(result);
return JsonResultUtils.success(dataMap);
}
}
