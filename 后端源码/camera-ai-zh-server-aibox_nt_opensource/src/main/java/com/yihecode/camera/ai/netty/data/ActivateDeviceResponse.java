package com.yihecode.camera.ai.netty.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivateDeviceResponse extends Response {

    private boolean status;

    private String msg;

    private String sn;

}
