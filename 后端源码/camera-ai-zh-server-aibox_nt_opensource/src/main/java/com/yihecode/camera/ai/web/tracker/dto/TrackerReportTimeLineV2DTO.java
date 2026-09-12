package com.yihecode.camera.ai.web.tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Time Axis Data
* @author zhoumingxing
* @date 2024/9/14
*/
@ApiModel("Time Axis Data")
@Data
public class TrackerReportTimeLineV2DTO {

    @ApiModelProperty(value = "Time Label", dataType = "array")
    private List<String> labels;

    @ApiModelProperty(value = "in in Headcount", dataType = "array")
    private List<Integer> enterCounts;

    @ApiModelProperty(value = "Away open Headcount", dataType = "array")
    private List<Integer> leaveCounts;

    @ApiModelProperty(value = "Away open Headcount", dataType = "array")
    private List<Integer> remainCounts;
}