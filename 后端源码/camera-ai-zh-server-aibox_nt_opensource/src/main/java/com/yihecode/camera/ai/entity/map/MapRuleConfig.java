package com.yihecode.camera.ai.entity.map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel(value = "Rule rule Detail Management")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapRuleConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Name")
    private String name;

    @ApiModelProperty(value = "most small Value")
    private Integer min;

    @ApiModelProperty(value = "most big Value")
    private Integer max;

    @ApiModelProperty(value = "Color Value,#000000")
    private String color;
}
