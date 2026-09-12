package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
* Alarm Month Degree Target Value Config table
*
* @author zhou
* @since 2025.6.26
*/
@Data
@TableName("tbl_biz_report_target")
public class ReportTarget {

    private Long id;

    @TableField("t_month")
    private Integer month;

    @TableField("t_val")
    private Integer value;
}
