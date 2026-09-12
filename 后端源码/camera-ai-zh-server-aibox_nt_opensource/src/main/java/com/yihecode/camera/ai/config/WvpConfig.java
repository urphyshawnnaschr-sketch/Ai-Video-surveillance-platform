package com.yihecode.camera.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class WvpConfig {

    //WVP Service IP, Required
@Value("${wvp-conf.wvp-ip:127.0.0.1}")
private String wvpIp;

// WVP Server Mouth, Required
@Value("${wvp-conf.wvp-port:18080}")
private Integer wvpPort;

}
