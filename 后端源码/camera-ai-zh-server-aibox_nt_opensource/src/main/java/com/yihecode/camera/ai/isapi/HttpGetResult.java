package com.yihecode.camera.ai.isapi;

import lombok.Builder;
import lombok.Data;

/**
* HTTP GET Request Return Data
*/
@Data
@Builder
public class HttpGetResult {

    private boolean success;

    private String error;

    private String xmlStr;
}
