package com.yihecode.camera.ai.web.dto;

import lombok.Data;

/**
* Collect Data Algorithm Config Management
*
* @author 465769438@qq.com
* @since 2025/3/15
*/
@Data
public class AlarmCollectAlgoDTO {

    /**
* Algorithm ID
*/
    private Long id;

    /**
* Algorithm Name
*/
    private String name;

    /**
*
*/
    private Integer collectFlag;

    /**
* Collect Confidence
*/
    private Float collectConfidence;

    /**
* Push Status
*/
    private Integer pushEnable;
}
