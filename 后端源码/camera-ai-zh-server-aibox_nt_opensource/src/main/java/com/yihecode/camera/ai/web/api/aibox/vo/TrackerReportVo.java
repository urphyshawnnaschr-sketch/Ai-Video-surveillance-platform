package com.yihecode.camera.ai.web.api.aibox.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Person Stream Quantity Report Data
* @author zhoumingxing
* @date 2024/9/14
*/
@ApiModel(value = "Person Stream Quantity Report Data")
@Data
public class TrackerReportVo {

    @ApiModelProperty(value = "Camera ID", dataType = "long")
    @JsonProperty("video_id")
    private Long cameraId;

    @ApiModelProperty(value = "Alert Time", dataType = "string")
    @JsonProperty("timestamp")
    private String timestamp;

    @ApiModelProperty(value = "in in Headcount", dataType = "string")
    @JsonProperty("enter_count")
    private Integer enterCount;

    @ApiModelProperty(value = "Away open Headcount", dataType = "string")
    @JsonProperty("leave_count")
    private Integer leaveCount;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @JsonProperty("extend_data_1")
    private Integer extendData1;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @JsonProperty("extend_data_2")
    private Integer extendData2;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @JsonProperty("extend_str_1")
    private String extendStr1;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @JsonProperty("extend_str_2")
    private String extendStr2;

}
