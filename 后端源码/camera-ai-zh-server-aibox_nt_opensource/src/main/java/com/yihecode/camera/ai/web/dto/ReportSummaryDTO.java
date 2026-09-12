package com.yihecode.camera.ai.web.dto;

import lombok.Data;

/**
* Alarm Data Count, Count Alarm Close Count, Process Count, not Process Count, Today Day Alert Count
*
* @auhtor 465769438@qq.com
* @since 2025/12/29
*/
@Data
public class ReportSummaryDTO {

    //when Day Alert Count
private Integer todayCount;

// Closed Count
private Integer closeCount;

// Process Count, Contain self Dynamic Process Count
private Integer handleCount;

// not Process Count
private Integer unHandleCount;
}
