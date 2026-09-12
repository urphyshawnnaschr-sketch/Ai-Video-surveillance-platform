package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Menu Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
public class MenusSaveRequestVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Menu Name")
    private String name;

    @ApiModelProperty(value = "Menu Path")
    private String path;

    @ApiModelProperty(value = "Type (0- Directory, 1- Menu, 2- Button)")
    private Integer type;

    @ApiModelProperty(value = "up Level Menu ID")
    private Long parentId;

    @ApiModelProperty(value = "Menu Code")
    private String auth;

    @ApiModelProperty(value = "Whether Hide (0- No, 1- is)")
    private Integer isHidden;

    @ApiModelProperty(value = "icon Name")
    private String icon;

    @ApiModelProperty(value = "File Path")
    private String filePath;

}