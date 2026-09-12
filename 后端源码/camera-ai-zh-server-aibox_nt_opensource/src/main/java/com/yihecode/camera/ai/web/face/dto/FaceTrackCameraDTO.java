package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Face Tracking Data
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel(value = "Camera Insert Point Data")
@Data
public class FaceTrackCameraDTO {

    //Mark Data
@ApiModelProperty(value ="Annotation Camera List")
private List<FaceTrackCameraItemDTO> markedList;

// not Mark Data
@ApiModelProperty(value ="not Annotation Camera List")
private List<FaceTrackCameraItemDTO> unmarkList;

}
