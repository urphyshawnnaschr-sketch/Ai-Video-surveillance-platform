package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Camera Batch Import Temp Hour table
* @author Abyss
* @date 2024/3/5 13:31
*/
@Data
@ApiModel(value = "Camera Batch Import Temp Hour table")
@TableName("tbl_biz_camera_import")
public class CameraBatchImport {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Camera Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Brand")
    @TableField("brand")
    private String brand;

    @ApiModelProperty(value = "ipHost")
    @TableField("ipHost")
    private String ipHost;

    @ApiModelProperty(value = "port")
    @TableField("port")
    private String port;

    @ApiModelProperty(value = "Account")
    @TableField("account")
    private String account;

    @ApiModelProperty(value = "Password")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "Channel")
    @TableField("channel")
    private String channel;

    @Deprecated
    @ApiModelProperty(value = "Deprecated, Validate Status 0 not Validate 1 Validate in 2 Validation failed 3 Validation success")
    @TableField("check_state")
    private Integer checkState;

    @ApiModelProperty(value = "Import Status 0 not Import 1 Import in 2 Import failed 3 Import success")
    @TableField("import_state")
    private Integer importState;

    @ApiModelProperty(value = "Point Bit belong belong")
    @TableField("location")
    private String location;

    @ApiModelProperty(value = "Algorithm Recognition Interval (Unit: second)")
    @TableField("interval_time")
    private Float intervalTime;

    @ApiModelProperty(value = "Alert Interval (Unit: second)")
    @TableField("alarm_interval")
    private Float alarmInterval;

    @ApiModelProperty(value = "camera rtsp stream url")
    @TableField("rtsp_url")
    private String rtspUrl;

    @ApiModelProperty(value = "Relate Algorithm")
    @TableField("algorithms")
    private String algorithms;

    @ApiModelProperty(value = "User id")
    @TableField("account_id")
    private String accountId;

    @ApiModelProperty(value = "Error Message")
    @TableField("mistake")
    private String mistake;

    @ApiModelProperty(value = "Region Type (1- Server Version 2- Box Version)")
    @TableField("location_type")
    private String locationType;

    @ApiModelProperty(value = "Create Time")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Algorithm ids")
    @TableField("algorithm_ids")
    private String algorithmIds;

    @ApiModelProperty(value = "Region id")
    @TableField("location_id")
    private Long locationId;

    @ApiModelProperty(value = "Region ids")
    @TableField("location_ids")
    private String locationIds;

    @ApiModelProperty(value = "Import File Name")
    @TableField("file_name")
    private String filename;

    @ApiModelProperty(value = "Import Batch Label")
    @TableField("tag")
    private String tag;

    @ApiModelProperty(value = "Import Error Note")
    @TableField("mistake_desc")
    private String mistakeDesc;

    @ApiModelProperty(value = "customer account input in Video Stream Address Ha")
    @TableField("rtsp_url2")
    private String rtspUrl2;

}