package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "Edge Box Sync Camera Status Param")
@Data
public class CameraAlgorithmlVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    private String key;

    @ApiModelProperty(value = "Box No", dataType = "string")
    private String sn;

    @ApiModelProperty(value = "Description Info", dataType = "string")
    private String desc;

    @ApiModelProperty(value = "Box Algorithm Update Status 0 no Update Task 1 Wait Update 2 Update in 3 Update Complete Complete", dataType = "integer")
    private Integer status;

    @ApiModelProperty(value = "Algorithm Name, multi kind Algorithm For Current Camera Draw Surface", dataType = "string")
    private String name;

    @ApiModelProperty(value = "Algorithm Name, multi kind Algorithm For Current Camera Draw Surface", dataType = "string")
    private String version;
}
