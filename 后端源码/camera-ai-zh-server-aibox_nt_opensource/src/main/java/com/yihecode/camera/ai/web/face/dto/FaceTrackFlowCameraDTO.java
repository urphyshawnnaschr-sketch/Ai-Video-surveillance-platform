package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Face Search Camera")
@Data
public class FaceTrackFlowCameraDTO {

    @ApiModelProperty(value = "Camera ID", example = "1", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "Camera Name", example = "Guard Door", dataType = "string")
    private String name;
}
