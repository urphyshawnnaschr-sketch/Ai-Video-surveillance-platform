package com.yihecode.camera.ai.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Algorithm Label")
@TableName("tbl_biz_tag")
public class Tag {

    @ApiModelProperty(value = "Label ID")
    private Long id;

    @ApiModelProperty(value = "Tag Name Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Label Type, 1 Line Industry")
    @TableField("type")
    private String type;

}