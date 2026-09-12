package com.yihecode.camera.ai.web.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* aibox Add or Update transmit input Param
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel("Add or Update transmit input Param")
@Data
public class AiboxBasicModifyVo {

    @ApiModelProperty("Primary Key")
    private Long id;

    @ApiModelProperty("Box Name")
    private String name;

    @ApiModelProperty("Box ip Address")
    private String ipAddr;

    @ApiModelProperty("Box No")
    private String sn;

    @ApiModelProperty("belong belong Organization")
    private Long departId;
}
