package com.yihecode.camera.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "Role DTO")
public class ApRoleDTO implements Serializable {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;
    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;
    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @ApiModelProperty(value = "Role Name", dataType = "string", example = "Platform Management member")
    private String nameCh;

    @ApiModelProperty(value = "English Name", dataType = "string", example = "administrator")
    private String nameEn;
}