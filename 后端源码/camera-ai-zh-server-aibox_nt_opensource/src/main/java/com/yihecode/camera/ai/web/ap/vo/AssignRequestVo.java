package com.yihecode.camera.ai.web.ap.vo;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Claim Annotation Task Param")
public class AssignRequestVo {

    @ApiModelProperty(value = "Project ID",  required = true)
    private Long projectId;

    @ApiModelProperty("Count, Default 1")
    private Integer count;
}
