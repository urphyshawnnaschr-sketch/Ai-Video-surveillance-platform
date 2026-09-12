package com.yihecode.camera.ai.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Face Similarity Threshold Config, Filter Threshold, Stranger produce Person Threshold")
@Data
public class ConfigFaceSimiliarityDTO {

    @ApiModelProperty(value = "most small Similarity", example = "0.3", dataType = "float")
    private Float minSimiliarity;

    @ApiModelProperty(value = "near Like Similarity", example = "0.8", dataType = "float")
    private Float sameSimiliarity;
}
