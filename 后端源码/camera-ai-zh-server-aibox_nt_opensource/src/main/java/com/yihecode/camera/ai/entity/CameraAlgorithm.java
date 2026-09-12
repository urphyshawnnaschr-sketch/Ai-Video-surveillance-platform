package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Camera and Algorithm Relate Entity
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
@ApiModel(value = "Camera and Algorithm Relate Entity")
@TableName("tbl_biz_camera_algorithm")
public class CameraAlgorithm {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Camera ID")
    @TableField("camera_id")
    private Long cameraId;

    @ApiModelProperty(value = "Algorithm ID")
    @TableField("algorithm_id")
    private Long algorithmId;

    @ApiModelProperty(value = "Confidence")
    @TableField("confidence")
    private Float confidence;

    @ApiModelProperty(value = "Canvas roi")
    @TableField("mark_points")
    private String markPoints;

    @ApiModelProperty(value = "Image roi")
    @TableField("image_points")
    private String imagePoints;

    @ApiModelProperty(value = "Canvas line")
    @TableField("line_mark_points")
    private String lineMarkPoints;

    @ApiModelProperty(value = "Image line")
    @TableField("line_image_points")
    private String lineImagePoints;

    @ApiModelProperty(value = "Algorithm Version")
    @TableField("algorithm_version")
    private String algorithmVersion;

    @ApiModelProperty(value = "Box Algorithm Update Status 0 no Update Task 1 Wait Update 2 Update in 3 Update Complete Complete 4 Algorithm File Download in")
    @TableField("box_update_status")
    private Integer boxUpdateStatus;

    @ApiModelProperty(value = "Algorithm Run Status, 0- not Run, 1- In Progress Run")
    @TableField("run_status")
    private Integer runStatus;

    @ApiModelProperty(value = "Algorithm most after Run Time")
    @TableField("run_time")
    private Date runTime;

    @ApiModelProperty(value = "Whether self Dynamic Push,0- is,1- No")
    @TableField("auto_push")
    private Integer autoPush;

}
