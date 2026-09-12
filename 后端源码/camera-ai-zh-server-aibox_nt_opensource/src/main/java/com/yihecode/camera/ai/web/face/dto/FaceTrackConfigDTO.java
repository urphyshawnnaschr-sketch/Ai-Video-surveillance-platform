package com.yihecode.camera.ai.web.face.dto;

import com.yihecode.camera.ai.entity.face.FaceTrackCamera;
import com.yihecode.camera.ai.entity.face.FaceTrackConfig;
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
@ApiModel(value = "Base image and Camera Insert Point Data")
@Data
public class FaceTrackConfigDTO {

    //Face Tracking Base image Info
@ApiModelProperty(value ="Base image Config Info")
private FaceTrackConfig faceTrackConfig;

// Face Tracking Camera Insert Point Info
@ApiModelProperty(value ="Camera Insert Point List Info")
private List<FaceTrackCamera> faceTrackCameraList;
}
