package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Algorithm Push Config
*/
@ApiModel("Third Party Push Data Config")
@Data
public class AlgorithmPushConfigVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("Third Party Push ID")
    private Integer pushEnable;

}
