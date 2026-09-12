package com.yihecode.camera.ai.netty.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivationStatusResponse extends Response {

    private boolean status;

    private String msg;

    private boolean isActived;

    private String sn;
}
