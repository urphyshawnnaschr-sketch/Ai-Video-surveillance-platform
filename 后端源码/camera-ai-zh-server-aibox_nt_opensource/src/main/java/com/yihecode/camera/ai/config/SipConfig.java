package com.yihecode.camera.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sip-conf")
@Data
public class SipConfig {

    private Integer port = 8116;
    private String domain = "4101050000";
    private String id = "41010500002000000001";
    private String password = System.getenv("SIP_PASSWORD");
    private String ip = "192.168.0.122";

}
