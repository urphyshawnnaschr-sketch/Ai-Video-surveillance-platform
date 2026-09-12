package com.yihecode.camera.ai.web.api.face.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Third Party Report User Data
*/
@Data
public class FaceUserDTO {

    private Long id;

    @ApiModelProperty(value = "Person member Name")
    private String name;

    @ApiModelProperty(value = "Contact Phone")
    private String tel;
}
