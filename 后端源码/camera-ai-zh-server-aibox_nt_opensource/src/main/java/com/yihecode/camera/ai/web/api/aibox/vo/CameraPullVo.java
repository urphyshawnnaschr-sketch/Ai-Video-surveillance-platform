package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Edge Box Pull Get Camera Resource List Param")
@Data
public class CameraPullVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    String key;

    @ApiModelProperty(value = "Box No", dataType = "string")
    String sn;
}
