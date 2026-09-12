package com.yihecode.camera.ai.web.vo;

import com.yihecode.camera.ai.entity.Camera;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Video Stream Alert Data")
public class StreamReportVo {

    @ApiModelProperty("Alert ID")
    private Long id;

    @ApiModelProperty("Alert Param")
    private String params;

    @ApiModelProperty("Camera Name")
    private String cameraName;

    @ApiModelProperty("Algorithm Name")
    private String algorithmName;

    @ApiModelProperty("Warehouse database Name")
    private String wareName;

    @ApiModelProperty("Alert Time")
    private String alarmTime;
}
