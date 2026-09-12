package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Face Search History Data
*/
@Data
@ApiModel(value = "Face Search Data")
public class FaceSearchInfoDTO {

    @ApiModelProperty(value = "Primary Key", example = "1")
    private Long id;

    @ApiModelProperty(value = "Search Time", example = "2025-06-30 01:03:05")
    private String createdAt;

    @ApiModelProperty(value = "File Name", example = "a.jpg")
    private String filename;
}
