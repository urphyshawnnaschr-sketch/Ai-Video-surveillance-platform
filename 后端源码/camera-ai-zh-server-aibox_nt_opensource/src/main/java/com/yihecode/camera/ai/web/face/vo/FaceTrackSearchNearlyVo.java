package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Face Detail Query
*
* @author zhou
* @since 2025.6.30
*/
@ApiModel(value = "Face Alarm up One / down One Request Param")
@Data
public class FaceTrackSearchNearlyVo {

    @ApiModelProperty(value = "Search ID", example = "123", required = true)
    private Long searchId;

    @ApiModelProperty(value = "Alarm ID", example = "123", required = true)
    private Long reportId;

    @ApiModelProperty(value = "Type: 0- up One (most new), 1- down One (over Remove)", example = "0", required = true)
    private Integer type;
}
