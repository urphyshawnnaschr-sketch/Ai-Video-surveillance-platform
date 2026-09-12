package com.yihecode.camera.ai.job;

import cn.hutool.core.util.IdUtil;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.wvp.GBDeviceChannel;
import com.yihecode.camera.ai.media.MediaRestfulService;
import com.yihecode.camera.ai.media.MediaStreamNode;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.MessageType;
import com.yihecode.camera.ai.netty.data.StreamCloseRequest;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.wvp.GBDeviceChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* Stream no Person View, Close
*
* @author 465769438@qq.com
* @since 2025/3/7
*/
@Component
public class StreamNoneReaderJob {

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    @Autowired
    private MediaRestfulService mediaRestfulService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private GBDeviceChannelService gbDeviceChannelService;

    @Autowired
    private ProjectConfig projectConfig;

    public void runJob() {
        //Cross net, like Result Local Stream Media not has View, rule Notification customer account Local Device Stop Push Stream
if(projectConfig.isCrossNet()) {
List<MediaStreamNode> mediaStreamNodeList = mediaRestfulService.getMediaList();
if(mediaStreamNodeList == null || mediaStreamNodeList.isEmpty()) {
return;
}

for(MediaStreamNode mediaStreamNode: mediaStreamNodeList) {
if(mediaStreamNode.getReaderCount() == 0) {
Long cameraId = null;
String stream = mediaStreamNode.getStream();
if(stream.contains("_")) {// GB Standard Device
String[] d2c = stream.split("_");
GBDeviceChannel gbDeviceChannel = gbDeviceChannelService.getByDeviceIdAndChannelId(d2c[0], d2c[1]);
if(gbDeviceChannel!= null) {
cameraId = gbDeviceChannel.getCameraId();
}
} else {// Camera
cameraId = Long.valueOf(mediaStreamNode.getStream());
}

if(cameraId == null || cameraId == 0) {
continue;
}

Camera camera = cameraService.getById(cameraId);
if(camera == null) {
continue;
}

Location location = locationService.getById(camera.getLocationId());
if(location == null) {
continue;
}

StreamCloseRequest request = new StreamCloseRequest();
request.setType(MessageType.STREAM_CLOSE.getType());
request.setSn(location.getBoxNo());
request.setRequestId(IdUtil.randomUUID());
request.setCameraId(cameraId);
request.setCloudStreamPort(projectConfig.getCloudStreamPort());
messageSenderAndWaiter.sendRequest(request);

}
}
}
}
}
