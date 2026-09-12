package com.yihecode.camera.ai.netty.data;

import lombok.Builder;
import lombok.Data;

/**
* Face Recognition Result
*/
@Data
@Builder
public class FaceRecognize2Response extends Response {

    private boolean status;

    private String msg;

    private String sn;

    //Recognition Result
private String resultJson;

}
