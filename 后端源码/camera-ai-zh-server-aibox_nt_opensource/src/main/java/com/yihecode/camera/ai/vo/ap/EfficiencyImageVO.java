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
public class EfficiencyImageVO {
    @ApiModelProperty(value = "Date")
    private String date;
    @ApiModelProperty(value = "Annotation Image Count")
    private Integer imageNum;
    @ApiModelProperty(value = "Annotation Box Count")
    private Integer annNum;
}
