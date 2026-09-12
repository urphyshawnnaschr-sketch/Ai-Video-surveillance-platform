package com.yihecode.camera.ai.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Department -> Box -> Camera Cascade Data result structure
*/
@ApiModel("Department -> Box -> Camera Cascade Data result structure")
@Data
public class BoxCassInfoDTO {

    @ApiModelProperty("Box ID")
    private Long id;

    @ApiModelProperty("Box Name")
    private String name;

    @ApiModelProperty("Box")
    private int type = 1;

    @ApiModelProperty("Camera Cascade Data")
    private List<CameraCassInfoDTO> children;
}
