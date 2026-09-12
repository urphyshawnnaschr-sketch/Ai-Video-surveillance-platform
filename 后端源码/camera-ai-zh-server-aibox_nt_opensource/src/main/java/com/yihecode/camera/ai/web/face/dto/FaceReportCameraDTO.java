package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
* Face Detail Data
*
* @author zhou
* @since 2025.6.30
*/
@ApiModel(value = "Face Alarm Corresponding Camera Data")
@Data
@Builder
public class FaceReportCameraDTO {

    @ApiModelProperty(value = "Camera ID", example = "1", dataType = "string")
    private Long id;

    @ApiModelProperty(value = "Camera Name", example = "Guard Door", dataType = "string")
    private String name;
}
