package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Edge Box Register Param")
@Data
public class StatusVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    String key;

    @ApiModelProperty(value = "Box No", dataType = "string")
    String sn;

    @ApiModelProperty(value = "Disk make Use Quantity", dataType = "number")
    Double diskUsed;

    @ApiModelProperty(value = "CPU make Use Rate", dataType = "number")
    Double cpuUsed;

    @ApiModelProperty(value = "inner Store make Use Quantity", dataType = "number")
    Double memoryUsed;

    @ApiModelProperty(value = "APU make Use Rate", dataType = "number")
    Double apuUsed;

    @ApiModelProperty(value = "VIC make Use Rate", dataType = "number")
    Double vicUsed;

    @ApiModelProperty(value = "IPE Frame Rate", dataType = "number")
    Double ipeUsed;

    @ApiModelProperty(value = "Chip Temperature", dataType = "number")
    Double temperatureUsed;
}
