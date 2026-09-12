package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Face Similarity Threshold Config, Filter Threshold, Stranger produce Person Threshold")
@Data
public class ConfigFaceSimiliarityVo {

    @ApiModelProperty(value = "most small Similarity", example = "0.3", dataType = "float", required = true)
    private Float minSimiliarity;

    @ApiModelProperty(value = "near Like Similarity", example = "0.8", dataType = "float", required = true)
    private Float sameSimiliarity;
}
