package com.yihecode.camera.ai.entity.map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.yihecode.camera.ai.entity.map.handler.MapRuleConfigListTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Camera or Box Rule rule Config Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Camera or Box Rule rule Config Management")
@Data
@TableName(value = "tbl_biz_map_rule", autoResultMap = true)
public class MapRule {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "svg File Name Name")
    @TableField("svg_name")
    private String svgName;

    @ApiModelProperty(value = "Calculate h Number")
    @TableField("calc_day")
    private Integer calcDay;

    @ApiModelProperty(value = "Type,0- Camera Group,1- Camera")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "Rule rule List")
    @TableField(value = "rules", typeHandler = MapRuleConfigListTypeHandler.class)
    private List<MapRuleConfig> rules;
}
