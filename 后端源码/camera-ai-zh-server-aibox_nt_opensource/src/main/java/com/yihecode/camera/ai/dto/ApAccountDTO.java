package com.yihecode.camera.ai.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "Account DTO")
public class ApAccountDTO implements Serializable {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @TableLogic(value = "null", delval = "now()")
    @ApiModelProperty(value = "Login Account")
    @TableField("account")
    private String account;

    @ApiModelProperty(value = "Login Password")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "Account Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Status", notes = "0- Normal 1- Disabled")
    @TableField("state")
    private Integer state;

    @ApiModelProperty(value = "belong belong Department")
    @TableField("depart_id")
    private Long departId;

    @ApiModelProperty(value = "Phone")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "Role List")
    private List<ApRoleDTO> roles;

    @ApiModelProperty(value = "Group Queue table")
    private List<ApRoleDTO> teams;

    @ApiModelProperty(value = "belong belong Department List")
    @TableField(exist = false)
    private String departNames;

    @ApiModelProperty(value = "work No")
    private String staffNo;
}