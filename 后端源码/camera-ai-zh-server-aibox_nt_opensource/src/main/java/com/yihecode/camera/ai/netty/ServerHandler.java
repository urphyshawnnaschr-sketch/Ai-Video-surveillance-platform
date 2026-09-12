package com.yihecode.camera.ai.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.netty.data.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

/**
* Message Receive Process Management
*
* @author 465769438@qq.com
* @since 2025/3/5
*/
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final MessageReceiveHandler messageReceiveHandler;

    public ServerHandler() {
        this.messageReceiveHandler = SpringContextUtil.getBean(MessageReceiveHandler.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //
}

@Override
public void channelInactive(ChannelHandlerContext ctx) throws Exception {
ctx.fireChannelInactive();

String currentId = ctx.channel().id().asLongText();
for(Map.Entry<String, Channel> channel: NettyServer.clientChannels.entrySet()) {
String channelId = channel.getValue().id().asLongText();
if(channelId.equalsIgnoreCase(currentId)) {
NettyServer.clientChannels.remove(channel.getKey());
}
}
}

@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
if(msg == null) {
return;
}

try {
// log.info("message handler {}", messageReceiveHandler);
String clientMessage = (String) msg;
// log.info("Client Request Data {}", clientMessage);

JSONObject message = JSON.parseObject(clientMessage);
String type = message.getString("type");
// log.info("type {}", type);


// if(!(MessageType.REGISTER.getType().equalsIgnoreCase(type) || MessageType.HEARTBEAT.getType().equalsIgnoreCase(type))) {
// log.info("Receive to Message {}", message);
//}

// Register Process
if(MessageType.REGISTER.getType().equalsIgnoreCase(type)) {
// log.info("Client Register");
String sn = message.getString("sn");
//if(NettyServer.clientChannels.containsKey(sn)) {
// return;
//}
NettyServer.clientChannels.put(sn, ctx.channel());

messageReceiveHandler.handleRegister(message);
}

// Heartbeat Process
if(MessageType.HEARTBEAT.getType().equalsIgnoreCase(type)) {
messageReceiveHandler.handleHeartbeat(message);
}

// Add or Update Camera
if(MessageType.ADD_CAMERA.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Delete Camera
if(MessageType.DEL_CAMERA.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Model Upgrade
if(MessageType.UPGRADE_ALGO.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Algorithm Restart
if(MessageType.RESTART.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Query Device Whether Activate
if(MessageType.ACTIVATION_STATUS.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Get Device order Column
if(MessageType.DEVICE_SERIAL.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Activate Device
if(MessageType.ACTIVATE_DEVICE.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Get Image
if(MessageType.IMAGE_CAPTURE.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// to Cloud End Push Stream
if(MessageType.STREAM_PUSHER.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Cloud End Close Push Stream
if(MessageType.STREAM_CLOSE.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Cloud End Record make
if(MessageType.STREAM_RECORD.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Expand Param Update
if(MessageType.ALGO_EXTRAS.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Expand Param Update
if(MessageType.DEL_FILES.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Face Recognition
if(MessageType.FACE_RECOGNIZE.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Face Recognition
if(MessageType.FACE_RECOGNIZE2.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}

// Face than for
if(MessageType.FACE_COMPARE.getType().equalsIgnoreCase(type)) {
ctx.fireChannelRead(msg);
}
} catch (Exception e) {
// e.printStackTrace();
}
}

@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//log.info("exceptionCaught {}", ctx.channel().id().asLongText());
// cause.printStackTrace();
ctx.close();
}

}
