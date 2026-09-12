package com.yihecode.camera.ai.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.netty.data.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MessageSenderAndWaiter {

    //Storage Each Request up down Text
private final Map<String, RequestContext> requestContextMap = new ConcurrentHashMap<>();

public Response sendRequest(Request request) {
log.info("Send Request {}", JSON.toJSONString(request));
String requestId = request.getRequestId();
RequestContext context = new RequestContext();
requestContextMap.put(requestId, context);
try {
// Send Message
String sn = request.getSn();
boolean foundOk = false;
boolean sendOk = false;
for(Map.Entry<String, Channel> entry: NettyServer.clientChannels.entrySet()) {
if(sn.equalsIgnoreCase(entry.getKey())) {
foundOk = true;

Channel channel = entry.getValue();
if(channel.isActive() && channel.isWritable()) {
// Send Message
channel.writeAndFlush(JSON.toJSONString(request) +"\n").addListener(future -> {
if (future.isSuccess()) {
log.info("Message Send Success, request_id: {}", request.getRequestId());
} else {
log.error("Message Send Failed: request_id: {}, ex: {}", request.getRequestId(), future.cause().getMessage());
}
});

// Register One Listener Incoming Process Client Response
channel.pipeline().addLast(new ResponseHandler(requestContextMap));

// Send Success
sendOk = true;
} else {
log.info("Message no Method Send, Channel Closed or not can write Data, main Dynamic Close Client, request_id: {}", request.getRequestId());
ChannelFuture future = channel.close();
future.addListener(new ChannelFutureListener() {
@Override
public void operationComplete(ChannelFuture future) throws Exception {
if (future.isSuccess()) {
log.info("Client Channel Close Success: {}", channel.remoteAddress());
// Here can with Add Other Resource Clear Reason Operation
} else {
log.error("Client Channel Close Failed: {}, ex: {}", channel.remoteAddress(), future.cause().getMessage());
}
}
});

}
}
}

if(!foundOk) {
return buildResponse(request.getType(),"Box Device Offline, Please Retry Later");
}

// Send Failed
if(!sendOk) {
return buildResponse(request.getType(),"Box Device via Letter Failed, Please Retry Later");
}

// Wait Client Reply, most multi Wait 300 s
boolean awaitResult = context.latch.await(300, TimeUnit.SECONDS);
if (awaitResult) {
return buildResponse(context.responseHolder.getResponse());
} else {
return buildResponse(request.getType(),"Box Device via Letter Timeout, Please Retry Later");
}
} catch (InterruptedException e) {
return buildResponse(request.getType(),"Box Device Process Exception, Please Retry Later");
} finally {
requestContextMap.remove(requestId);
}
}

public Response buildResponse(String type, String msg) {
MessageType messageType = MessageType.getMessageType(type);
switch (Objects.requireNonNull(messageType)) {
case RESTART:
return RestartResponse.builder().status(false).msg(msg).build();
case ADD_CAMERA:
return CameraAddResponse.builder().status(false).msg(msg).build();
case DEL_CAMERA:
return CameraDelResponse.builder().status(false).msg(msg).build();
case ACTIVATION_STATUS:
return ActivationStatusResponse.builder().status(false).msg(msg).build();
case DEVICE_SERIAL:
return DeviceSerialResponse.builder().status(false).msg(msg).build();
case ACTIVATE_DEVICE:
return ActivateDeviceResponse.builder().status(false).msg(msg).sn("").build();
case UPGRADE_ALGO:
return UpgradeResponse.builder().status(false).msg(msg).build();
case IMAGE_CAPTURE:
return ImageCaptureResponse.builder().status(false).msg(msg).build();
case STREAM_PUSHER:
return StreamPusherResponse.builder().status(false).msg(msg).build();
case STREAM_CLOSE:
return StreamCloseResponse.builder().status(false).msg(msg).build();
case STREAM_RECORD:
return StreamRecordResponse.builder().status(false).msg(msg).build();
case ALGO_EXTRAS:
return AlgoExtrasResponse.builder().status(false).msg(msg).build();
case DEL_FILES:
return DelFilesResponse.builder().status(false).msg(msg).build();
case FACE_RECOGNIZE:
return FaceRecognizeResponse.builder().status(false).msg(msg).build();
case FACE_RECOGNIZE2:
return FaceRecognize2Response.builder().status(false).msg(msg).build();
case FACE_COMPARE:
return FaceCompareResponse.builder().status(false).msg(msg).build();
default:
return null;
}
}

public Response buildResponse(JSONObject object) {
MessageType messageType = MessageType.getMessageType(object.getString("type"));
switch (Objects.requireNonNull(messageType)) {
case RESTART:
return RestartResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).build();
case ADD_CAMERA:
return CameraAddResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).cameraId(object.getLong("camera_id")).build();
case DEL_CAMERA:
return CameraDelResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).cameraId(object.getLong("camera_id")).build();
case ACTIVATION_STATUS:
return ActivationStatusResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).sn(object.getString("sn")).isActived(object.getBooleanValue("is_actived")).build();
case DEVICE_SERIAL:
return DeviceSerialResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).sn(object.getString("sn")).activeSn(object.getString("active_sn")).build();
case ACTIVATE_DEVICE:
return ActivateDeviceResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).sn(object.getString("sn")).build();
case UPGRADE_ALGO:
return UpgradeResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).build();
case IMAGE_CAPTURE:
return ImageCaptureResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).fileName(object.getString("file_name")).videoFps(object.getIntValue("video_fps")).videoWidth(object.getIntValue("video_width")).videoHeight(object.getIntValue("video_height")).videoCodec(object.getString("video_codec")).build();
case STREAM_PUSHER:
return StreamPusherResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).build();
case STREAM_CLOSE:
return StreamCloseResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).build();
case STREAM_RECORD:
return StreamRecordResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).build();
case ALGO_EXTRAS:
return AlgoExtrasResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).build();
case DEL_FILES:
return DelFilesResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).build();
case FACE_RECOGNIZE:
return FaceRecognizeResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).resultJson(object.getString("result_json")).build();
case FACE_RECOGNIZE2:
return FaceRecognize2Response.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).resultJson(object.getString("result_json")).build();
case FACE_COMPARE:
return FaceCompareResponse.builder().status(object.getBooleanValue("status")).msg(object.getString("msg")).resultJson(object.getString("result_json")).build();
default:
return null;
}
}

// Request up down Text class, Contain CountDownLatch and ResponseHolder
private static class RequestContext {
private final CountDownLatch latch = new CountDownLatch(1);
private final ResponseHolder responseHolder = new ResponseHolder();
}

// Internal class, Use at Save Client Response
private static class ResponseHolder {
private JSONObject response;

public JSONObject getResponse() {
return response;
}

public void setResponse(JSONObject response) {
this.response = response;
}
}

// Internal class, Use at Process Client Response
private static class ResponseHandler extends io.netty.channel.SimpleChannelInboundHandler<String> {
private final Map<String, RequestContext> requestContextMap;

public ResponseHandler(Map<String, RequestContext> requestContextMap) {
this.requestContextMap = requestContextMap;
}

@Override
protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
JSONObject respData = JSON.parseObject(msg);
String requestId = respData.getString("request_id");

RequestContext context = requestContextMap.get(requestId);
if(context!= null) {
context.responseHolder.setResponse(respData);
context.latch.countDown();
}

// Process Complete Response after Remove the Handler
ctx.pipeline().remove(this);
}

@Override
public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) throws Exception {
// cause.printStackTrace();
ctx.close();
// Exception Situation down Remove Handler
ctx.pipeline().remove(this);
}
}

}
























