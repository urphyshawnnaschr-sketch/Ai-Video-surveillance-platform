package com.yihecode.camera.ai.entity.ap;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@ApiModel(value = "User Annotation group Entity")
@Data
@TableName(value = "ap_user_group_relationships", autoResultMap = true)
public class UserGroupRelationships {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @TableLogic(value = "null", delval = "now()")
    @JsonIgnore
    private Date deletedAt;

    @ApiModelProperty(value = "User id", dataType = "long", example = "0")
    private Long userId;

    @ApiModelProperty(value = "Annotation group id", dataType = "long", example = "0")
    private Long groupId;

}
