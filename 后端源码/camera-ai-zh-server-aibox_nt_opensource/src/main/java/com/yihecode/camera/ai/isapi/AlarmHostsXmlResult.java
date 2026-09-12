package com.yihecode.camera.ai.isapi;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
* Alarm Server Set
*/
@Data
@Builder
public class AlarmHostsXmlResult {

    private boolean success;

    private String error;

    //Whether via Set Alarm Server
private List<AlarmHosts> alarmHostsList;

// original start XML
private String xmlStr;

// Modify after XML
private String modifyXml;
}
