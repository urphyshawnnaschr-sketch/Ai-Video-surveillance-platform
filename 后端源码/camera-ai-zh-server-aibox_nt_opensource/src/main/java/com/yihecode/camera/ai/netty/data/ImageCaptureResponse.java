package com.yihecode.camera.ai.netty.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageCaptureResponse extends Response {

    private boolean status;

    private String msg;

    private String sn;

    private String fileName;

    private Integer videoFps;

    private String videoCodec;

    private Integer videoWidth;

    private Integer videoHeight;

}
