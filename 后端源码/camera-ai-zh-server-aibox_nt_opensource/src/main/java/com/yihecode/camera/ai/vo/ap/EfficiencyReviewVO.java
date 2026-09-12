package com.yihecode.camera.ai.vo.ap;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Author lichangliang
* @Date 2023/7/27 20:20
* @Describe
* @Version 1.0
*/
@Data
public class EfficiencyReviewVO {
    @ApiModelProperty(value = "Date")
    private String date;
    @ApiModelProperty(value = "Quality Check Pass Count")
    private Integer passNum;
    @ApiModelProperty(value = "Quality Check not combine Grid Count")
    private Integer backNum;
}
