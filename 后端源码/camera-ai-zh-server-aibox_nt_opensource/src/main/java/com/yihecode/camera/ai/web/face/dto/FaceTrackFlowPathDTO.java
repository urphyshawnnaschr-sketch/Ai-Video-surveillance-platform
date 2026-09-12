package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
* Face Search Result Data
*
* @author zhou
* @since 2025.6.30
*/
@ApiModel(value = "Face Search Result Data")
@Data
public class FaceTrackFlowPathDTO {

    @ApiModelProperty(value = "Alarm Time", example = "2025-01-01 01:01:01", dataType = "string")
    private String reportAt;

    @ApiModelProperty(value = "Camera Name", example = "1", dataType = "long")
    private String cameraName;

    @ApiModelProperty(value = "Alarm ID, Use at Read Image", example = "1", dataType = "long")
    private Long reportId;

    @ApiModelProperty(value = "Similarity", example = "0.55", dataType = "float")
    private Float similarity;

    @ApiModelProperty(value = "User ID", example = "1", dataType = "long")
    private Long userId;

    @ApiModelProperty(value = "User Name", example = "sheet Three", dataType = "string")
    private String userName;

    @ApiModelProperty(value = "Group Camera Name", example = "A Area", dataType = "string")
    private String cameraGroupName;

    private List<Integer> cameraPoints;

}
