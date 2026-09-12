package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* super Star Play Put Control
* @author zhoumingxing
* @date 2024/9/14
*/
@ApiModel("super Star Play Put Control Param")
@Data
public class PlayCxVo {

    @ApiModelProperty("Camera Ids")
    private List<Long> cameraIds;
}
