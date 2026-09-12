package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "Annotation group Entity")
@Data
@TableName(value = "ap_user_group", autoResultMap = true)
public class UserGroup {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @TableLogic(value = "null", delval = "now()")
    @JsonIgnore
    private Date deletedAt;

    @ApiModelProperty(value = "Annotation group Name", dataType = "string", example = "Safe all Belt Category")
    private String name;

    @ApiModelProperty(value = "Annotation group Description", dataType = "string", example = "Annotation Safe all Belt")
    private String description;

    @ApiModelProperty(value = "Team ID", dataType = "long", example = "0")
    private Long teamId;

    @ApiModelProperty(value = "Creator User ID", dataType = "long")
    private Long userId;

}