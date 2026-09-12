package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Query Model List Param")
@Data
public class ModelListVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    String key;

    @ApiModelProperty(value = "Box Code", dataType = "string")
    String sn;
}
