package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Model Inference in Param
*/
@Data
@ApiModel("Model Inference in Param")
public class ModelPredictVo {

    @ApiModelProperty("File Name Name")
    private String file;

    @ApiModelProperty("Check select Algorithm List, Example [1,2,3]")
    private String algorithms;

    @ApiModelProperty("Camera ID")
    private String cameraId;

    @ApiModelProperty("Region ROI")
    private String marks;

    @ApiModelProperty("Image high Degree")
    private Double imgHeight;
}
