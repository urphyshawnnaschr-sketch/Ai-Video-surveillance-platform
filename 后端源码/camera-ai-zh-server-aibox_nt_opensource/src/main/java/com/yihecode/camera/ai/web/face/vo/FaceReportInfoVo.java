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
@Data
@ApiModel(value = "Face Info Query Param")
public class FaceReportInfoVo {

    @ApiModelProperty(value = "Alarm ID", example = "123", required = true)
    private Long id;
}
