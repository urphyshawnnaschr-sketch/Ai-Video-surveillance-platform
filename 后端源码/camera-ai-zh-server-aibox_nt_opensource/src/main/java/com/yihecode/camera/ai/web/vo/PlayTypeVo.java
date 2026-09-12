package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Set Play Put Type Param")
public class PlayTypeVo {

    @ApiModelProperty(value = "Play Put Type 0- original start Stream Play Put, 1- combine Complete Stream Play Put")
    private Integer playType;

}
