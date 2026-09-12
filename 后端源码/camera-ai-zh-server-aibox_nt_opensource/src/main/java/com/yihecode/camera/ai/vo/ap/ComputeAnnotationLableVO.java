package com.yihecode.camera.ai.vo.ap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
* @Author lichangliang
* @Date 2023/7/26 10:27
* @Describe
* @Version 1.0
*/
@ApiModel(value = "Count Annotation Label Situation")
@Data
@Builder
public class ComputeAnnotationLableVO {

    @ApiModelProperty(value = "Tag Name Name")
    private String label;

    @ApiModelProperty(value = "Annotation Count")
    private Integer annCount;

    @ApiModelProperty(value = "Annotation than Example")
    private BigDecimal annRatio;



}
