package com.yihecode.camera.ai.web.vo;

import lombok.Data;

/**
* Collect Data Algorithm Config Management
*
* @author 465769438@qq.com
* @since 2025/3/15
*/
@Data
public class AlarmCollectAlgoVo {

    /**
* Algorithm ID
*/
    private Long algoId;

    /**
*
*/
    private Integer collectFlag;

    /**
* Collect Confidence
*/
    private Float collectConfidence;

    /**
* Push ID,0- not main Dynamic Push,1- main Dynamic Push
*/
    private Integer pushEnable;
}
