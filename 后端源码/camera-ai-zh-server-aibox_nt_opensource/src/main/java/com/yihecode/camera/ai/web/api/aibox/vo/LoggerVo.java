package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(value = "Log Param")
@Data
@ToString
public class LoggerVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    private String key;

    @ApiModelProperty(value = "Log Content", dataType = "string")
    private String msg;

}
