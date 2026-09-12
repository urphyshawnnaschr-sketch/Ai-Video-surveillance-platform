package com.yihecode.camera.ai.web.ap.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("increase Quantity Training Basic Param Param")
public class TrainingBaseVo {

    @ApiModelProperty("Camera ID")
    private Long cameraId;

    @ApiModelProperty("Algorithm ID")
    private Long algorithmId;

    @ApiModelProperty("Time Interval, form Bit s")
    private Integer interval;

    private Integer begin;

    private Integer end;

}
