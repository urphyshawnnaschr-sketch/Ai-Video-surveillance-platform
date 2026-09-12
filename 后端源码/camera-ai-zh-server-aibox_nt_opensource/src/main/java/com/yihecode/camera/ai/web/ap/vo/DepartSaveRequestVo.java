package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Organization Organization Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
public class DepartSaveRequestVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "' Department Name'")
    private String name;

    @ApiModelProperty(value = "' up Level Department ID'")
    private Long parentId;

    @ApiModelProperty(value = "' Address Bit set Note'")
    private String geoAddr;

    @ApiModelProperty(value = "' Remark'")
    private String remark;

}