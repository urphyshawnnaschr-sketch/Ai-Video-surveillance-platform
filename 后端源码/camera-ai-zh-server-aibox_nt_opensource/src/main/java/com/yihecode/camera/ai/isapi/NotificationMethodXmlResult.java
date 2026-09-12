package com.yihecode.camera.ai.isapi;

import lombok.Builder;
import lombok.Data;

/**
* Linkage Mode Result
*/
@Data
@Builder
public class NotificationMethodXmlResult {

    private boolean success;

    private String error;

    private String xmlStr;

    //Whether via Set' Upload in Core' Mode
private boolean hasCenter;

// Modify after XML
private String modifyXml;
}
