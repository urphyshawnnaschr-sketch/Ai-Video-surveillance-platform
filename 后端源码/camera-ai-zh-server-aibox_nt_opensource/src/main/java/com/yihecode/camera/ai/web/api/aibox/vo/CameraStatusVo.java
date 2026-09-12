package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "Edge Box Sync Camera Status Param")
@Data
public class CameraStatusVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    private String key;

    @ApiModelProperty(value = "Box No", dataType = "string")
    private String sn;

    @ApiModelProperty(value = "Camera ID", dataType = "array")
    private List<CameraStatusSubVo> cameras;
}
