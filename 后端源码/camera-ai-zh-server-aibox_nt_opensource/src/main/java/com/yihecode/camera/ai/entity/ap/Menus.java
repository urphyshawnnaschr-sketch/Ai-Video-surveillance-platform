package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;

@ApiModel(value = "Menu Bar Entity")
@Data
@TableName(value = "ap_menus", autoResultMap = true)
public class Menus {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

//@TableLogic(value ="null", delval ="now()")
@JsonIgnore
private Date deletedAt;

@ApiModelProperty(value ="Menu Name", dataType ="string", example ="Count Surface Board")
private String name;

@ApiModelProperty(value ="Menu English Name", dataType ="string", example ="Statistics Dashboard")
private String englishName;

@ApiModelProperty(value ="parent id", dataType ="long", example ="0")
private Long parent;

@ApiModelProperty(value ="Corresponding url Path", dataType ="string", example ="/api/v1/statistics")
private String path;

@ApiModelProperty(value ="Unique ID", dataType ="string", example ="base:sys:log")
private String auth;

@ApiModelProperty(value ="Type (0- Directory, 1- Menu, 2- Button)", dataType ="string", example ="0")
private Integer type;

@ApiModelProperty(value ="Whether Hide (0- No,1- is)", dataType ="string", example ="0")
private Integer isHidden;

@ApiModelProperty(value ="icon Name", dataType ="string", example ="icon-menu")
private String icon;

@ApiModelProperty(value ="File Path", dataType ="string", example ="")
private String filePath;

@ApiModelProperty(value ="child Menu", dataType ="list")
@TableField(exist = false)
private List<Menus> children;

@ApiModelProperty(value ="Whether Check select", dataType ="boolean")
@TableField(exist = false)
private boolean checked = false;

@ApiModelProperty(value ="Whether Hide", dataType ="map")
@TableField(exist = false)
private Map<String, Object> meta = new HashMap<>();
}