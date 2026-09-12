package com.yihecode.camera.ai.web.api.comm;

import lombok.Builder;
import lombok.Data;

/**
* Push Result
*/
@Data
@Builder
public class AlarmPushResult {

    private boolean success = false;

    private String error = "OK";

    private String errorDetail;

    private String imageKey;

    private String token;
}
