package com.yihecode.camera.ai.isapi;

import lombok.Builder;
import lombok.Data;

/**
* Thermal total can Force, main need Use at Parse Channel
*/
@Data
@Builder
public class ThermalCapXmlResult {

    //Parse Whether Success
private boolean success;

// Error Message
private String error;

// original start XML Data
private String xmlStr;

// Parse channel Param
private String channelID;
}
