package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Face Tracking Insert Point Add / Modify Param
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel("Face Tracking Insert Point Add / Modify Param")
@Data
public class FaceTrackCameraModifyVo {

    //Primary Key
@ApiModelProperty(value ="Primary Key", example ="1")
private Long id;

// Config ID
@ApiModelProperty(value ="Base image Config ID", example ="1", required = true)
private Long configId;

// Camera ID
@ApiModelProperty(value ="Camera ID", example ="1", required = true)
private Long cameraId;

// Insert Point Bit set
@ApiModelProperty(value ="Insert Point Bit set", example ="[10,10,20,20]", required = true)
private List<Integer> position;
}
