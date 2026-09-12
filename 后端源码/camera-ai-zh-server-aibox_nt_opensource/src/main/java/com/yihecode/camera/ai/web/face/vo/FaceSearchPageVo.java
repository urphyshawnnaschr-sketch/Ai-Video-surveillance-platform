package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Search History Query Param")
public class FaceSearchPageVo {

    @ApiModelProperty(value = "Page Number", example = "1", required = true)
    private Integer page = 1;

    @ApiModelProperty(value = "Pagination Count", example = "10", required = true)
    private Integer limit = 10;
}
