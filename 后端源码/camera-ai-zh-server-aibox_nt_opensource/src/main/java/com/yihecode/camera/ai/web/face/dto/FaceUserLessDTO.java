package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Person member Simple need Info
*/
@ApiModel(value = "Person member Simple need Info")
@Data
public class FaceUserLessDTO {

    @ApiModelProperty(value = "Person member ID", example = "1", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "Person member Name", example = "sheet Three", dataType = "string")
    private String name;
}
