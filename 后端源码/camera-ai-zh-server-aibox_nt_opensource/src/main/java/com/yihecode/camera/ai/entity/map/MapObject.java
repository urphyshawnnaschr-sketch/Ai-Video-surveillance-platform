package com.yihecode.camera.ai.entity.map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.yihecode.camera.ai.entity.map.handler.DoubleTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Ground image or image Layer and Camera or Box Config Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Ground image or image Layer and Camera or Box Config Management")
@Data
@TableName(value = "tbl_biz_map_object", autoResultMap = true)
public class MapObject {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Ground image & image Layer ID")
    @TableField("map_id")
    private Long mapId;

    @ApiModelProperty(value = "Camera ID/ Box ID")
    @TableField("object_id")
    private Long objectId;

    @ApiModelProperty(value = "Type,0- Box,1- Camera")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "Cover image")
    @TableField(typeHandler = DoubleTypeHandler.class )
    private List<Double> position;
}
