package com.yihecode.camera.ai.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* Alarm Data Count, Count Alarm Close Count, Process Count, not Process Count, Today Day Alert Count
*
* @author 465769438@qq.com
* @since 2025/3/15
*/
@Data
public class ReportSummaryVo {

    /**
* Camera ID
*/
    private Long cameraId;

    /**
* Review Result Status,0- not Process,1- Process,2- Closed,3- self Dynamic Process
*/
    private Integer auditResult;

    /**
* Annotation 0- Pending Fixed, 1- Correct Report, 2- wrong Report
*/
    private List<Integer> mark;

    /**
* same - Review Result Status, only Use at part Condition Query
*/
    private Integer appendResult;

    /**
* Start Time
*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
* End Time
*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
