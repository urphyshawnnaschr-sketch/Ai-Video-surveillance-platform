package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yihecode.camera.ai.entity.Location;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Organization Organization Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Organization Organization Management")
@Data
@TableName("ap_depart")
public class Depart {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "' Department Name'")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "' up Level Department ID'")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "' Creator member'")
    @TableField("created_by")
    private Long createdBy;

    @ApiModelProperty(value = "Create Time")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "' Updater member'")
    @TableField("updated_by")
    private Long updatedBy;

    @ApiModelProperty(value = "Update Time")
    @TableField("updated_at")
    private Date updatedAt;

    @ApiModelProperty(value = "child part part")
    @TableField(exist = false)
    private List<Depart> children = new ArrayList<>();

    @ApiModelProperty(value = "Region list")
    @TableField(exist = false)
    private List<Location> locationList = new ArrayList<>();
}