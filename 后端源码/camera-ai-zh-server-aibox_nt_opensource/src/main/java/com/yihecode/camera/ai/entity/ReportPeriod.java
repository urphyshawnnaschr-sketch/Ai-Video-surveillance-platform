package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Alert Hour Segment Config Entity
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
@ApiModel(value = "Alert Hour Segment Config Entity")
@TableName("tbl_biz_report_period")
public class ReportPeriod {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Camera id")
    @TableField("camera_id")
    private Long cameraId;

    @ApiModelProperty(value = "Algorithm id")
    @TableField("algorithm_id")
    private Long algorithmId;

    @ApiModelProperty(value = "Start Time")
    @TableField("start_time")
    private Integer startTime;

    @ApiModelProperty(value = "End Time")
    @TableField("end_time")
    private Integer endTime;

    @ApiModelProperty(value = "Start Time (Text)")
    @TableField("start_text")
    private String startText;

    @ApiModelProperty(value = "End Time (Text)")
    @TableField("end_text")
    private String endText;

}
