package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Function can: new Version Camera Relate Alarm Time Save Param
*
* @author zhoumingxing
* @date 2024/8/19
*/
@Data
@ApiModel("new Version Camera Relate Alarm Time Save Param")
public class CameraAlgorithmAlarmTimeModifyVo {

    @ApiModelProperty("Start Time, Format:hh:mm")
    private String startTime;

    @ApiModelProperty("End Time, Format:hh:mm")
    private String endTime;
}

