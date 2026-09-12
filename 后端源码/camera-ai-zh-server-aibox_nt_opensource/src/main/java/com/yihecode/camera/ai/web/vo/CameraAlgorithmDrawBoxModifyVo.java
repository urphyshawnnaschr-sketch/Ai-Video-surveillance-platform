package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Function can: new Version Camera Relate Draw make roi Save Param
* Coordinate Complete all by Frontend Calculate, Calculate is original start image up Coordinate, not Need again sub Calculate
*
* @author zhoumingxing
* @date 2024/8/19
*/
@Data
@ApiModel("new Version Camera Relate Draw make roi Save Param")
public class CameraAlgorithmDrawBoxModifyVo {

    @ApiModelProperty("x Coordinate")
    private Integer x;

    @ApiModelProperty("y Coordinate")
    private Integer y;
}
