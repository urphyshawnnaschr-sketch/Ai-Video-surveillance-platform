package com.yihecode.camera.ai.web.dto;

import lombok.Data;

/**
* Count Alarm Process Hour long and Target Data
*/
@Data
public class AlarmAuditTimeTargetDTO {

    private String date;

    //real International Value
private Double timeVal;

// Target Value
private Integer targetVal;
}
