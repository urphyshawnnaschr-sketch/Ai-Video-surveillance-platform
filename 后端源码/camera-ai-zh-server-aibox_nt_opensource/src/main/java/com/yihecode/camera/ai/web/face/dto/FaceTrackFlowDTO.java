package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Person member Trajectory
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel(value = "Person member Tracking Node Info")
@Data
public class FaceTrackFlowDTO {

    @ApiModelProperty(value = "Bit set Info", example = "[10,10]")
    private List<Integer> position;

    @ApiModelProperty(value = "Camera ID", example = "1")
    private Long cameraId;

    @ApiModelProperty(value = "Camera Name", example = "Guard Doorway")
    private String cameraName;

    @ApiModelProperty(value = "Alarm Time", example = "2025-02-01 11:22:33")
    private String date;
}
