package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("Camera Play Put Param")
public class StreamPlayingVo {

    @ApiModelProperty(value = "Camera Id(s)")
    private List<Long> cameraIds;

}
