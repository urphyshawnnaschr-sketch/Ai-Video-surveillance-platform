package com.yihecode.camera.ai.web.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Department Node Info
*/
@ApiModel("app End _ Organization (Department) Info")
@Data
public class AppDepartDTO {

    @ApiModelProperty(value = "Organization ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "Organization Name", example = "Dept Tech part")
    private String name;

    @ApiModelProperty(value = "Address Bit set", example = "Beijing")
    private String geoAddr;

    @ApiModelProperty(value = "Remark", example = "One Remark")
    private String remark;
}
