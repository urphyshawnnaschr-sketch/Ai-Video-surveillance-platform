package com.yihecode.camera.ai.web.vo;

import lombok.Data;

import java.util.List;

/**
* Collect Data Management
*
* @author 465769438@qq.com
* @since 2025/3/15
*/
@Data
public class AlarmCollectVo {

    /**
* Collect day Number
*/
    private Integer collectDay;

    /**
* Collect Algorithm Config
*/
    private List<AlarmCollectAlgoVo> collectAlgos;
}
