package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Function can: new Version Camera Save Request Param
*
* @author zhoumingxing
* @date 2024/8/19
*/
@Data
@ApiModel("new Version Camera Save Param")
public class CameraModifyVo {

    @ApiModelProperty("Camera id")
    private Long id;

    @ApiModelProperty("Camera Name")
    private String name;

    @ApiModelProperty("Camera Stream Address")
    private String rtspUrl;

    @ApiModelProperty("Camera Stream Address 2")
    private String rtspUrl2;

    @ApiModelProperty("Recognition Interval")
    private Float intervalTime;

    @ApiModelProperty("Alert Interval")
    private Float alarmInterval;

    @ApiModelProperty("Camera Draw Surface image")
    private String fileName;

    @ApiModelProperty("belong belong Region")
    private Long locationId;

    @ApiModelProperty("Video Code Format")
    private String videoCodec;

    @ApiModelProperty("Algorithm List")
    private List<CameraAlgorithmModifyVo> algorithms;

    @ApiModelProperty("Speaker Pole ID")
    private Long soundColumnId;

    @ApiModelProperty("Frame Rate")
    private Integer videoFps;

    @ApiModelProperty("Video Width")
    private Integer videoWidth;

    @ApiModelProperty("Video high Degree")
    private Integer videoHeight;

    @ApiModelProperty("GB Standard Channel ID")
    private Long gbId;

    @ApiModelProperty("GB Standard Channel Name")
    private String gbName;

    @ApiModelProperty("GB Standard Media Node ID")
    private Long gbMediaServerId;

    @ApiModelProperty("Source Type,0- Manual Add,1- GB Standard Channel")
    private Integer sourceType;

}