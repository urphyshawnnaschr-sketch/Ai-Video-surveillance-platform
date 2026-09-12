package com.yihecode.camera.ai.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* Netty Server Management
*
* @author 465769438@qq.com
* @since 2025/3/5
*/
@Slf4j
@Component
public class NettyServer {

    @Value("${netty.port:8023}")
    private Integer port;

    //Use at Storage All Client Connection Channel, make Use Thread Safe all Set combine
public static final ConcurrentHashMap<String, Channel> clientChannels = new ConcurrentHashMap<>();

// Use at Sync Object
public static final Object lock = new Object();

private EventLoopGroup bossGroup = new NioEventLoopGroup(1); // Use at Receive Connection Thread group
private EventLoopGroup workerGroup = new NioEventLoopGroup(); // Use at Process by Receive Connection Thread group
private ChannelFuture channelFuture; // Use at Listen Server Start and Close ChannelFuture Object

@PostConstruct
public void start() throws Exception {
ServerBootstrap bootstrap = new ServerBootstrap();
bootstrap.group(bossGroup, workerGroup)
.channel(NioServerSocketChannel.class) // make Use NIO Channel real current
.childHandler(new ChannelInitializer<SocketChannel>() {// Fixed Meaning Process Client Request ChannelInitializer real current
@Override
protected void initChannel(SocketChannel ch) {
ChannelPipeline pipeline = ch.pipeline();
ByteBuf buf = Unpooled.copiedBuffer("\n".getBytes());
pipeline.addLast(new DelimiterBasedFrameDecoder(1024, buf));
pipeline.addLast(new StringDecoder()); // Add Solve code Device, will Receive to ByteBuf Convert Complete String
pipeline.addLast(new StringEncoder()); // Add Code Device, will Send String Convert Complete ByteBuf
pipeline.addLast(new ServerHandler());
}
})
.option(ChannelOption.SO_BACKLOG, 128)
.option(ChannelOption.SO_KEEPALIVE, true)
.childOption(ChannelOption.SO_KEEPALIVE, true);
channelFuture = bootstrap.bind(port).sync(); // Bind Port and Sync Wait Direct to Bind Complete Complete
log.info("Netty server started on port {}", port);
}

// to All Client Send Message Method
public static void sendMessageToAllClients(String message) {
// synchronized (lock) {
//// for (Channel channel: clientChannels) {
//// if (channel.isActive()) {
//// channel.writeAndFlush(message);
////}
////}
//}
for (Channel channel: clientChannels.values()) {
if (channel.isActive()) {
channel.writeAndFlush(message);
}
}

}

public static boolean sendMessage(String sn, String message) {
// boolean sendOk = false;
// for(Map.Entry<String, Channel> entry: NettyServer.clientChannels.entrySet()) {
// if(entry.getKey().equalsIgnoreCase(sn)) {
// Channel channel = entry.getValue();
// if(channel.isActive()) {
// channel.writeAndFlush(message);
// sendOk = true;
//}
//}
//
//}
return false;
}

@PreDestroy
public void destroy() throws Exception {
channelFuture.channel().close(); // Close channel and Explain Put All Resource
workerGroup.shutdownGracefully(); // Optimize Elegant Ground Close workerGroup All Thread and Queue in Task, most after Explain Put All Resource
bossGroup.shutdownGracefully(); // Optimize Elegant Ground Close bossGroup All Thread and Queue in Task, most after Explain Put All Resource
log.info("Netty server stopped");
}

}