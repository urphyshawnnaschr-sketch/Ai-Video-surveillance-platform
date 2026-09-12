package com.yihecode.camera.ai.web.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* aibox Tree Structure input out Data
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel("Tree Structure input out Data")
@Data
public class AiboxTreeVo {

    @ApiModelProperty("Primary Key")
    private Long id;

    @ApiModelProperty("Organization Name")
    private String name;

    @ApiModelProperty("Whether Organization Node")
    private boolean isDepart;

    @ApiModelProperty("Whether Online")
    private boolean isOnline;

    @ApiModelProperty("up Level Node")
    private Long parentId;

    @ApiModelProperty("child Node")
    private List<AiboxTreeVo> children;

}
