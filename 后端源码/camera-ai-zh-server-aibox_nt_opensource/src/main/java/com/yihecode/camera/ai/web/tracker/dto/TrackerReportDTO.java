package com.yihecode.camera.ai.web.tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Camera Count List Data
* @author zhoumingxing
* @date 2024/9/14
*/
@ApiModel("Camera Count List Data")
@Data
public class TrackerReportDTO {

    @ApiModelProperty(value = "Camera ID", dataType = "long")
    private Long cameraId;

    @ApiModelProperty(value = "Camera Name", dataType = "string")
    private String cameraName;

    @ApiModelProperty(value = "in in Headcount", dataType = "string")
    private Integer enterCount;

    @ApiModelProperty(value = "Away open Headcount", dataType = "string")
    private Integer leaveCount;

    @ApiModelProperty(value = "Away open Headcount", dataType = "string")
    private Integer remainCount;

    @ApiModelProperty(value = "belong belong Department Name", dataType = "string")
    private String departName;

    @ApiModelProperty(value = "belong belong Box IP", dataType = "string")
    private String boxIp;

    @ApiModelProperty(value = "belong belong Box Name", dataType = "string")
    private String boxName;

}
