package com.yihecode.camera.ai.vo.ap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
* @Author lichangliang
* @Date 2023/7/26 10:27
* @Describe
* @Version 1.0
*/
@ApiModel(value = "Count Annotation Situation")
@Data
@Builder
public class ComputeAnnotationVO {

    @ApiModelProperty(value = "total Image Count")
    private String count;

    @ApiModelProperty(value = "Annotation Box Count")
    private String annInfoCount;

    @ApiModelProperty(value = "Annotation Count")
    private String annCount;

    @ApiModelProperty(value = "not Annotation Count")
    private Integer noAnnoCount;

    @ApiModelProperty(value = "Pending Modify Count")
    private Integer backCount;

    @ApiModelProperty(value = "than Example")
    private BigDecimal finshRatio;


}
