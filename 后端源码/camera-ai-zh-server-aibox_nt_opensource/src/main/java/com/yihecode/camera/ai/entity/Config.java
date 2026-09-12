package com.yihecode.camera.ai.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Config Management Entity
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
@ApiModel(value = "Config Management Entity")
@TableName("tbl_biz_config")
public class Config {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Config Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Config Item")
    @TableField("tag")
    private String tag;

    @ApiModelProperty(value = "Config Value")
    @TableField("val")
    private String val;
    @ApiModelProperty(value = "Config Value English")
    @TableField("english_val")
    private String englishVal;

}