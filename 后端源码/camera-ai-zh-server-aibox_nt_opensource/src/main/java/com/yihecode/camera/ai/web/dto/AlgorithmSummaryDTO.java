package com.yihecode.camera.ai.web.dto;

import lombok.Data;

/**
* Algorithm summary total, summary total Download Model Package Count, Run in Model Count, can Use Count etc
*
* @auhtor 465769438@qq.com
* @since 2025/12/29
*/
@Data
public class AlgorithmSummaryDTO {

    private Integer runningCount;

    private Integer availableCount;

    private Integer downloadCount;
}
