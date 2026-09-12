package com.yihecode.camera.ai.entity.tracker;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yihecode.camera.ai.entity.Camera;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Person Stream Quantity Tracking Recognition Result - Person Stream Quantity Tracking child Module
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Person Stream Quantity Tracking Recognition Result")
@Data
@TableName("tbl_biz_tracker_report")
public class TrackerReport {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Camera ID", dataType = "long")
    @TableField("camera_id")
    private Long cameraId;

    @ApiModelProperty(value = "Alert Time", dataType = "string")
    @TableField("timestamp")
    private String timestamp;

    @ApiModelProperty(value = "in in Headcount", dataType = "string")
    @TableField("enter_count")
    private Integer enterCount;

    @ApiModelProperty(value = "Away open Headcount", dataType = "string")
    @TableField("leave_count")
    private Integer leaveCount;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @TableField("extend_data_1")
    private Integer extendData1;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @TableField("extend_data_2")
    private Integer extendData2;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @TableField("extend_str_1")
    private String extendStr1;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @TableField("extend_str_2")
    private String extendStr2;

    @ApiModelProperty(value = "Expand Data", dataType = "string")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Create Time Stamp", dataType = "long")
    @TableField("created_mills")
    private Long createdMills;

    @ApiModelProperty(value = "belong belong Department id", dataType = "long")
    @TableField("depart_id")
    private Long departId;

    @ApiModelProperty(value = "belong belong Box ID", dataType = "long")
    @TableField("box_id")
    private Long boxId;
}

