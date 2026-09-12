package com.yihecode.camera.ai.web.api.face.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Third Party Report Group Data
*/
@Data
public class FaceGroupDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Group Name Name")
    private String name;
}
