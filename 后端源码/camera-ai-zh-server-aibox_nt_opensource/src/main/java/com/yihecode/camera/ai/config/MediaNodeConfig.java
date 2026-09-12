package com.yihecode.camera.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zlmediakit")
@Data
public class MediaNodeConfig {

    //# Node Name
@Value("${zlmediakit.name:node1}")
private String name;

//# Server Address, Default not Set, Pass config.js Read, like Result Read Failed, rule Default for 127.0.0.1, like Result Set, rule with the Address for Standard
@Value("${zlmediakit.http-ip:}")
private String httpIp;

//# docker Mapper out Port
@Value("${zlmediakit.http-port:0}")
private Integer httpPort;

//# customer account Mapper public net Port or Through Transparent Port, Non must, like Result not Config, make Use http-port
@Value("${zlmediakit.http-play-port:0}")
private Integer httpPlayPort;

//# docker Mapper out Port
@Value("${zlmediakit.rtsp-port:0}")
private Integer rtspPort;

//# webrtc Port
@Value("${zlmediakit.webrtc-port:0}")
private Integer webrtcPort;

//# webrtc Port
@Value("${zlmediakit.rtp-port-range:50000,50300}")
private String rtpPortRange;

//# webrtc Port
@Value("${zlmediakit.send-rtp-port-range:50000,50300}")
private String sendRtpPortRange;

//# zlm Password +
@Value("${zlmediakit.secret:123456}")
private String secret;

//# java IP
@Value("${zlmediakit.hook-ip:}")
private String hookIp;

//# java port
@Value("${zlmediakit.hook-port:0}")
private Integer hookPort;

private boolean connected = false;
}
