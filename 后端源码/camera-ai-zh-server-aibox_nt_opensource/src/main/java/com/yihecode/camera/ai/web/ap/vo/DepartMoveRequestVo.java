package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Organization Organization Management, Node Move Param
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
public class DepartMoveRequestVo {

    @ApiModelProperty(value = "sourceId", name = "Source Node ID")
    private Long sourceId;

    @ApiModelProperty(value = "targetId", name = "Target Node ID")
    private Long targetId;
}
