package com.yihecode.camera.ai.isapi;

import lombok.Data;

@Data
public class AlarmHosts {

    private String id;
    private String url;
    private String protocolType;
    private String parameterFormatType;
    private String addressingFormatType;
    private String ipAddress;
    private String portNo;
    private String userName;
    private String httpAuthenticationMethod;
}
