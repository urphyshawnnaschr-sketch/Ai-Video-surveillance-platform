package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel(value = "Role Entity")
@Data
@TableName(value = "ap_role", autoResultMap = true)
public class Role {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @TableLogic(value = "null", delval = "now()")
    @JsonIgnore
    private Date deletedAt;

    @ApiModelProperty(value = "Role Name", dataType = "string", example = "Platform Management member")
    private String nameCh;

    @ApiModelProperty(value = "English Name", dataType = "string", example = "administrator")
    private String nameEn;

    @ApiModelProperty(value = "Relate Menu ids", dataType = "list", example = "administrator")
    @TableField(exist = false)
    private List<Long> menusIds;

    @ApiModelProperty(value = "Relate Region ids", dataType = "list", example = "administrator")
    @TableField(exist = false)
    private List<Long> locationIds;

    @ApiModelProperty(value = "Relate Menu", dataType = "list")
    @TableField(exist = false)
    private List<Menus> menuss;

    @ApiModelProperty(value = "Person member Count", dataType = "int")
    @TableField(exist = false)
    private Integer accountNum;
}