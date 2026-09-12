package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Face Alarm Query _ Camera Query Data
*
* @author zhou
* @since 2025.6.30
*/
@ApiModel(value = "Face Alarm Query _ Camera Query Data")
@Data
public class FaceReportPageCameraDTO {

    @ApiModelProperty(value = "Camera ID", example = "1", dataType = "string")
    private Long cameraId;

    @ApiModelProperty(value = "Camera Name", example = "Guard", dataType = "string")
    private String cameraName;
}
