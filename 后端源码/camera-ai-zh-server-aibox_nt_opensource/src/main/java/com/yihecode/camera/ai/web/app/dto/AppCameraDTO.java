package com.yihecode.camera.ai.web.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("app End _ Camera Info")
@Data
public class AppCameraDTO {

    @ApiModelProperty(value = "Camera ID", example = "1")
    private Long cameraId;

    @ApiModelProperty(value = "Camera Name", example = "Guard Doorway")
    private String cameraName;

    @ApiModelProperty(value = "Inference Status Description", example = "not make Use")
    private String predictText;

    @ApiModelProperty(value = "Inference Status,0- not make Use,1- Inference in", example = "0")
    private Integer predictState;

}
