package com.yihecode.camera.ai.web.ap.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Annotation Image List Query Param")
public class ImageHistoryRequestVo {

    @ApiModelProperty("Project ID")
    private Long projectId;

    @ApiModelProperty("Type,0 Annotation, 1 Quality Check")
    private Integer type;

    private Integer page;
    private Integer limit;
}
