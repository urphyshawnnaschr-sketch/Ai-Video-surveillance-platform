package com.yihecode.camera.ai.web.dto;

import lombok.Data;

/**
* by Algorithm Dimension Degree Count Alarm Process Result
*/
@Data
public class StatisticAlgorithmAuditDTO {

    private Long algorithmId;

    private String algorithmName;

    private Integer unhandleCount;

    private Integer handledCount;

    private Integer totalCount;
}
