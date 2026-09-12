package com.yihecode.camera.ai.dto;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* ap_user_group DTO
*/
@Data
@ApiModel(value = "Annotation group DTO")
public class ApUserGroupDTO implements Serializable {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;
    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;
    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;
    @TableLogic(value = "null", delval = "now()")
    @ApiModelProperty(value = "Annotation group Name", dataType = "string", example = "Safe all Belt Category")
    private String name;
    @ApiModelProperty(value = "Annotation group Description", dataType = "string", example = "Annotation Safe all Belt")
    private String description;
    @ApiModelProperty(value = "Complete member Count", dataType = "Long", example = "10")
    private Long memberNum;

    @ApiModelProperty(value = "Team ID", dataType = "Long", example = "0")
    private Long teamId;
}