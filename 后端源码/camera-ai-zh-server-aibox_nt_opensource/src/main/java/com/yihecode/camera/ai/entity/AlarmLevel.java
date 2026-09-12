package com.yihecode.camera.ai.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* Alert Level Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Alert Level Management")
@Data
@TableName(value = "tbl_biz_alarm_level", autoResultMap = true)
public class AlarmLevel {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Alert Level Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Alert Level Color")
    @TableField("show_color")
    private String showColor;

    @ApiModelProperty(value = "Alert Level Color alpha")
    @TableField("show_color_alpha")
    private String showColorAlpha;

    @ApiModelProperty(value = "Alert show show Mode", dataType = "json")
    @TableField(value = "show_types", typeHandler = FastjsonTypeHandler.class)
    private List<String> showTypes;

    @ApiModelProperty(value = "User id")
    @TableField("account_id")
    private Long accountId;

    @ApiModelProperty(value = "Alert show show Mode Splice connect Result", dataType = "string")
    @TableField(exist = false)
    private String showTypeNames;
}