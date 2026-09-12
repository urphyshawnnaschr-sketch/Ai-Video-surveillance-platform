package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Alarm Count Day Data
*/
@Data
@ApiModel(value = "Alarm Count Day Data")
@TableName("tbl_biz_report_summary")
public class ReportSummary {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Count Year")
    @TableField("s_year")
    private Integer year;

    @ApiModelProperty(value = "Count Month")
    @TableField("s_month")
    private Integer month;

    @ApiModelProperty(value = "Count Day")
    @TableField("s_day")
    private Integer day;

    @ApiModelProperty(value = "total Alarm Number")
    @TableField("report_total")
    private Integer reportTotal;

    @ApiModelProperty(value = "Closed Number")
    @TableField("report_closed")
    private Integer reportClosed;

    @ApiModelProperty(value = "Process Number")
    @TableField("report_handled")
    private Integer reportHandled;

    @ApiModelProperty(value = "self Dynamic Process Number")
    @TableField("report_auto_handled")
    private Integer reportAutoHandled;

    @ApiModelProperty(value = "not Process Number")
    @TableField("report_unhandle")
    private Integer reportUnhandle;

    @ApiModelProperty(value = "Process Hour long (part)")
    @TableField("report_handle_time")
    private Integer reportHandleTime;

    @ApiModelProperty(value = "Process Rate")
    @TableField("report_handle_rate")
    private Integer reportHandleRate;

    @ApiModelProperty(value = "Date, Format:yyyyMMdd")
    @TableField("s_date")
    private Integer date;

    @ApiModelProperty(value = "Data Update Date")
    @TableField("update_at")
    private Date updateAt;

}
