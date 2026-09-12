package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Person member Search Camera Query Param
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel("Person member Search Camera Query Param")
@Data
public class FaceTrackFlowCameraVo {

    @ApiModelProperty(value = "Search ID", example = "1", dataType = "long", required = true)
    private Long searchId;
}
