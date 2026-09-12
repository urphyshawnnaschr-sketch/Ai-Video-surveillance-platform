package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "Edge Box Sync Camera Status Param")
@Data
public class CameraStatusSubVo {

    @ApiModelProperty(value = "Camera ID", dataType = "long")
    private Long cameraId;

    @ApiModelProperty(value = "Algorithm Name, multi kind Algorithm For Current Camera Draw Surface", dataType = "array")
    private List<String> algorithmIds;
}
