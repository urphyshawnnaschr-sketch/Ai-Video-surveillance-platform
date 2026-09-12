package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Query Model File List Param")
@Data
public class ModelCheckVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    String key;

    @ApiModelProperty(value = "Model Name", dataType = "string")
    String modelName;

    @ApiModelProperty(value = "md5.txt File Storage md5 Value", dataType = "string")
    String md5;
}
