package com.yihecode.camera.ai.isapi;

import lombok.Data;

/**
* rtsp Address Info, User, Password,IP Address
*/
@Data
public class RtspInfo {

    private String username;

    private String password;

    private String ip;
}
