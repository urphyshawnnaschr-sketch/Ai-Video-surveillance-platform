package com.yihecode.camera.ai.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Department -> Box -> Camera Cascade Data result structure
*/
@ApiModel("Department -> Box -> Camera Cascade Data result structure")
@Data
public class DepartCassInfoDTO {

    @ApiModelProperty("Department ID")
    private Long id;

    @ApiModelProperty("Department Name")
    private String name;

    @ApiModelProperty("Type")
    private int type = 0;

    @ApiModelProperty("Department Cascade Data")
    private List<BoxCassInfoDTO> children;

}
