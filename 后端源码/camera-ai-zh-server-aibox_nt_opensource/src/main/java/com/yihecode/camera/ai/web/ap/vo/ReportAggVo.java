package com.yihecode.camera.ai.web.ap.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@ApiModel("Alert gather combine Result")
public class ReportAggVo {

    @ApiModelProperty("Camera ID")
    private Long cameraId;

    @ApiModelProperty("Algorithm ID")
    private Long algorithmId;

    @ApiModelProperty("Camera Name")
    private String cameraName;

    @ApiModelProperty("Algorithm Name")
    private String algorithmName;

    @ApiModelProperty("Image Count")
    private Long imageNum;

    @ApiModelProperty("Update Time")
    private Date updatedAt;
}
