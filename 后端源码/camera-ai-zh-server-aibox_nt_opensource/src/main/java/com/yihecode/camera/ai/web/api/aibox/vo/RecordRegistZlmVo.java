package com.yihecode.camera.ai.web.api.aibox.vo;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
* Record make Node Register
*/
@ApiIgnore
@Data
public class RecordRegistZlmVo {

    //key
private String key;

// sn
private String sn;

// zlm IP
private String ip;

// zlm Password
private String secret;

// zlm mediaServerId, general.mediaServerId
private String name;

// zlm http Port, http.port
private Integer httpPort;

// zlm rtsp Port, rtsp.port
private Integer rtspPort;

// zlm rtc Port, rtc.port
private Integer rtcPort;

// zlm rtp Port Range, rtp_proxy.port_range
private String rtpPortRange;

// zlm rtp Send Port Range, rtp_proxy.port_range
private String sendRtpPortRange;

// zlm Node Type,0- Play Put Node,1- Record make Node,2- Play Put + Record make Node
private Integer nodeType;
}
