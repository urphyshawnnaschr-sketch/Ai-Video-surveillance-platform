package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Face Tracking Camera Mark Data
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel(value = "Camera Mark Data")
@Data
public class FaceTrackCameraItemDTO {

    @ApiModelProperty(value = "Primary Key", example = "1")
    private Long id;

    @ApiModelProperty(value = "Base image Config ID", example = "1")
    private Long configId;

    @ApiModelProperty(value = "Camera ID", example = "1")
    private Long cameraId;

    @ApiModelProperty(value = "Insert Point Bit set", example = "[10,10,20,20]")
    private List<Integer> position;

    @ApiModelProperty(value = "Camera Name", example = "Guard Doorway")
    private String cameraName;

    @ApiModelProperty(value = "Group Name Name", example = "1 Group")
    private String groupName;

}
