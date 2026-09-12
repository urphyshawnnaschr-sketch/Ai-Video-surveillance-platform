package com.yihecode.camera.ai.isapi;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasicParamXmlResult {

    //Parse Whether Success
private boolean success;

// Error Message
private String error;

// original start XML Data
private String xmlStr;

// Parse enabled Param
private String enabled;

// Modify after XML
private String modifyXml;
}
