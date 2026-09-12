package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Alarm Count Day Data Task
*/
@Data
@ApiModel(value = "Alarm Count Day Data Task")
@TableName("tbl_biz_report_summary_task")
public class ReportSummaryTask {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Count Date")
    @TableField("task_date")
    private Integer taskDate;

    @ApiModelProperty(value = "Count Year")
    @TableField("task_year")
    private Integer taskYear;

    @ApiModelProperty(value = "Count Month")
    @TableField("task_month")
    private Integer taskMonth;

    @ApiModelProperty(value = "Count Day")
    @TableField("task_day")
    private Integer taskDay;
}
