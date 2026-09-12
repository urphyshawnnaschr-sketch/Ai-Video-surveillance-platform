package com.yihecode.camera.ai.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
* Alarm Count Day Data Task
*/
@Data
@ApiModel(value = "Alarm Count Day Data Task")
public class ReportSummaryTaskDTO {

    @ApiModelProperty(value = "Data ID")
    private String startDate;

    @ApiModelProperty(value = "Count Date")
    private String endDate;

}
