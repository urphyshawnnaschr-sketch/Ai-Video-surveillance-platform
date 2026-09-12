package com.yihecode.camera.ai.web.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Alarm Count Process Request Param
*/
@ApiModel(value = "Alarm Count Process Request Param")
@Data
public class ReportSummaryHandleVo {

    @ApiModelProperty(value = "Start Time", example = "2025-07-01 10:10:10", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @ApiModelProperty(value = "End Time", example = "2025-07-01 10:10:10", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
}
