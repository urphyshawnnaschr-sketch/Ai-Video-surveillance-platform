package com.yihecode.camera.ai.web.api.temperature;

import lombok.Builder;
import lombok.Data;

/**
* Temperature Alarm Parse Data
*/
@Data
@Builder
public class TemperatureAlarmXmlResult {

    private boolean success;

    private String error;

    private String ipAddress;

    private String highTemperature;

    private Integer positionX;

    private Integer positionY;
}
