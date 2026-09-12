package com.yihecode.camera.ai.isapi;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpPutResult {

    private boolean success;

    private String returnXml;
}
