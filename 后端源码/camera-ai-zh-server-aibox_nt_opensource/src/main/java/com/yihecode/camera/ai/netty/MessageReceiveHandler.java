package com.yihecode.camera.ai.netty;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.netty.data.ActivationStatusRequest;
import com.yihecode.camera.ai.netty.data.ActivationStatusResponse;
import com.yihecode.camera.ai.netty.data.MessageType;
import com.yihecode.camera.ai.netty.data.Response;
import com.yihecode.camera.ai.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
* Netty Message Receive Process
*
* @author 465769438@qq.com
* @since 2025/3/5
*/
@Slf4j
@Component
public class MessageReceiveHandler {

    @Autowired
    private LocationService locationService;

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    /**
* Box Register
* @param message
*/
    public void handleRegister(JSONObject message) {
        String sn = message.getString("sn");
        String name = message.getString("name");
        int useType = 0; //make Use Use Path,0- Pure Inference Box,1- Face Service Box,2- Inference + Face Service Box
if(message.containsKey("useType")) {
useType = message.getIntValue("useType");
}
String ipAddr ="127.0.0.1";
if(message.containsKey("ipAddr")) {
ipAddr = message.getString("ipAddr");
}

Location location = locationService.getBySn(sn);
if(location == null) {
Location parentLocation = locationService.getDefRoot(2);

location = new Location();
location.setName(name);
location.setBoxHeartTime(System.currentTimeMillis());
location.setLocationType("2");
location.setType("2");
location.setParentId(parentLocation == null? 0L: parentLocation.getId());
location.setIsDef(0);
location.setIpAddr(ipAddr);
location.setSort(1);
location.setBoxNo(sn);
location.setPlatform(message.getString("platform"));
location.setUseType(useType);
locationService.save(location);
} else {
Location updateLocation = new Location();
updateLocation.setId(location.getId());
//updateLocation.setName(name);
updateLocation.setBoxHeartTime(System.currentTimeMillis());
updateLocation.setUseType(useType);
updateLocation.setIpAddr(ipAddr);
locationService.updateById(updateLocation);
}

// Delay Hour 10 s Execute Activate Status Get
Long boxId = location.getId();
String boxSn = location.getBoxNo();
String boxName = location.getName();
ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
executor.schedule(() -> {
// Get Activate Status
ActivationStatusRequest request = new ActivationStatusRequest();
request.setType(MessageType.ACTIVATION_STATUS.getType());
request.setRequestId(IdUtil.randomUUID());
request.setSn(boxSn);
Response response = messageSenderAndWaiter.sendRequest(request);
log.info("Socket Client Connection after send Start Activate Status Get, Back Activate Message, Box ID: {}, Box Index:{}, Box Name: {}, Message Reply {}", boxId, boxSn, boxName, response);
if(response!= null) {
ActivationStatusResponse activationStatusResponse = (ActivationStatusResponse) response;
//log.info("Box SN: {}, Process Status {} Activate {}", activationStatusResponse.getSn(), activationStatusResponse.isStatus(), activationStatusResponse.isActived());
if(activationStatusResponse.isStatus()) {
//log.info("Process Status for true");
if(activationStatusResponse.isActived()) {
//log.info("Box Activate, Standard device Update Status");
Location location1 = locationService.getBoxSnForRemote(activationStatusResponse.getSn());
//log.info("Update Box Activate Status location info {},", location1);
if (location1!= null) {
locationService.updateActiveStatus(location1.getId(), 1);
}
} else {
//log.info("Box not has Activate, Standard device Update Status");
Location location1 = locationService.getBoxSnForRemote(activationStatusResponse.getSn());
if(location1!= null) {
locationService.updateActiveStatus(location1.getId(), 0);
}
}
}
}

// Close Thread Pool
executor.shutdown();
}, 10, TimeUnit.SECONDS);
}

/**
* Heartbeat Process
* @param message
*/
public void handleHeartbeat(JSONObject message) {
String sn = message.getString("sn");
Long memoryTotal = message.getLong("memoryTotal");
Long diskTotal = message.getLong("diskTotal");

Location location = locationService.getBySn(sn);
if(location == null) {
log.error("Heartbeat Process Exception, Box does not exist, data: {}", message);
return;
}

Location updateLocation = new Location();
updateLocation.setId(location.getId());
updateLocation.setBoxHeartTime(System.currentTimeMillis());
updateLocation.setMemoryTotal(memoryTotal == null? 0L: memoryTotal);
updateLocation.setDiskTotal(diskTotal == null? 0L: diskTotal);
locationService.updateById(updateLocation);
}
}