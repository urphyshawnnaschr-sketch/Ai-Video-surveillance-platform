package com.yihecode.camera.ai.web.tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Time Axis Data
* @author zhoumingxing
* @date 2024/9/14
*/
@ApiModel("Time Axis Data")
@Data
public class TrackerReportTimeLineDTO {

    @ApiModelProperty(value = "Time Label", dataType = "string")
    private String label;

    @ApiModelProperty(value = "in in Headcount", dataType = "int")
    private Integer enterCount;

    @ApiModelProperty(value = "Away open Headcount", dataType = "int")
    private Integer leaveCount;

    @ApiModelProperty(value = "Away open Headcount", dataType = "int")
    private Integer remainCount;
}