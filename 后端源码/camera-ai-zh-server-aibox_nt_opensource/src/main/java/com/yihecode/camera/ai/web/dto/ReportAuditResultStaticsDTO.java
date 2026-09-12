package com.yihecode.camera.ai.web.dto;

import lombok.Builder;
import lombok.Data;

/**
* Alarm Count summary total DTO
*
* @author 465769438@qq.com
* @since 2025/2/25
*/
@Data
@Builder
public class ReportAuditResultStaticsDTO {

    //Type
private String type;

// Name
private String name;

// Count
private Integer count;
}