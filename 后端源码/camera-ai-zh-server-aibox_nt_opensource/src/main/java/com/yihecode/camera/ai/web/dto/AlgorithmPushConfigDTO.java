package com.yihecode.camera.ai.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Algorithm Push Config
*/
@ApiModel("Third Party Push Data Config")
@Data
public class AlgorithmPushConfigDTO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("Algorithm Name")
    private String name;

    @ApiModelProperty("Algorithm Code")
    private String nameEn;

    @ApiModelProperty("Third Party Push ID,0- not Enable,1- Enable")
    private Integer pushEnable;

}
