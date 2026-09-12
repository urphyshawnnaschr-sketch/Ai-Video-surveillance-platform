package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Edge Box Register Param")
@Data
public class RegisterVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    String key;

    @ApiModelProperty(value = "Box No", dataType = "string")
    String sn;

    @ApiModelProperty(value = "make build merchant", dataType = "string")
    String makers;

    @ApiModelProperty(value = "IP Address", dataType = "string")
    String ipAddr;

    @ApiModelProperty(value = "Device Mode", dataType = "string")
    String deviceMode;

    @ApiModelProperty(value = "CPU type No", dataType = "string")
    String cpuVersion;

    @ApiModelProperty(value = "inner Core Version", dataType = "string")
    String kernelVersion;

    @ApiModelProperty(value = "Lingxi Drive Dynamic Version", dataType = "string")
    String lyndriverVersion;

    @ApiModelProperty(value = "Lingxi SDK Version", dataType = "string")
    String lynsdkVersion;

    @ApiModelProperty(value = "Operation System Version", dataType = "string")
    String osVersion;

    @ApiModelProperty(value = "Disk total Quantity", dataType = "integer")
    Long diskTotal;

    @ApiModelProperty(value = "inner Store total Quantity", dataType = "integer")
    Long memoryTotal;
}
