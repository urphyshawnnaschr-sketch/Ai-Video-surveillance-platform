package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

/**
* Person member Tracking Data
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel(value = "Person member Tracking Info")
@Data
public class FaceTrackDTO {

    //Insert Point Data
@ApiModelProperty(value ="Person member Tracking Node List Info")
private List<FaceTrackFlowDTO> flowList = new ArrayList<>();

// Person member Data
@ApiModelProperty(value ="Person member Info")
private FaceTrackUserDTO user = new FaceTrackUserDTO();
}
