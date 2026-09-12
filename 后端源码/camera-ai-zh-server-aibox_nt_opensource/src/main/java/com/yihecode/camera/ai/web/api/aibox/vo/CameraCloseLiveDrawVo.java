package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Edge Box Close Push Stream Param")
@Data
public class CameraCloseLiveDrawVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    private String key;

    @ApiModelProperty(value = "Camera ID", dataType = "long")
    private Long cameraId;
}
