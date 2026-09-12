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
@ApiModel(value = "Count Annotation Quality Check Situation")
@Data
@Builder
public class ComputeAnnotationReviewVO {

    @ApiModelProperty(value = "total Need Quality Check Image")
    private String count;

    @ApiModelProperty(value = "Pass Count")
    private Integer reviewCount;

    @ApiModelProperty(value = "not Pass Count")
    private String noReviewCount;

    @ApiModelProperty(value = "exit return Count")
    private Integer backCount;

    @ApiModelProperty(value = "than Example")
    private BigDecimal finshRatio;



}
