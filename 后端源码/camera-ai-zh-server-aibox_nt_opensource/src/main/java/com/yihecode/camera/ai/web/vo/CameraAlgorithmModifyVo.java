package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Function can: new Version Camera Relate Algorithm Save Param
*
* @author zhoumingxing
* @date 2024/8/19
*/
@Data
@ApiModel("new Version Camera Relate Algorithm Save Param")
public class CameraAlgorithmModifyVo {

    @ApiModelProperty("Algorithm id")
    private Long algorithmId;

    @ApiModelProperty("Algorithm Code")
    private String algorithmNameEn;

    @ApiModelProperty("Algorithm Confidence")
    private Float algorithmConf;

    @ApiModelProperty("Alarm Time Segment")
    private List<CameraAlgorithmAlarmTimeModifyVo> alarmTimes;

    @ApiModelProperty("Draw make rois")
    private List<List<CameraAlgorithmDrawBoxModifyVo>> drawBoxs;

    @ApiModelProperty("Draw make lines")
    private List<List<CameraAlgorithmDrawBoxModifyVo>> drawLines;

    @ApiModelProperty("Social Push")
    private List<Long> socials;

    @ApiModelProperty("Push Mode")
    private Integer autoPush;
}

