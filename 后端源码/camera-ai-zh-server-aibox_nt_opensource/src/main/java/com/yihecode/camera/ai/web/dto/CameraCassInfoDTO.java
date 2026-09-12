package com.yihecode.camera.ai.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Department -> Box -> Camera Cascade Data result structure
*/
@ApiModel("Department -> Box -> Camera Cascade Data result structure")
@Data
public class CameraCassInfoDTO {

    @ApiModelProperty("Camera ID")
    private Long id;

    @ApiModelProperty("Camera Name")
    private String name;

    @ApiModelProperty("Type")
    private int type = 2;
}
