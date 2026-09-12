package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "Login Log")
@Data
@TableName(value = "tbl_biz_login_log", autoResultMap = true)
public class LoginLog {

    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1")
    private Long id;

    @ApiModelProperty(value = "Account ID", dataType = "long", example = "1")
    @TableField("account_id")
    private Long accountId;

    @ApiModelProperty(value = "Login Account", dataType = "string", example = "zhangsan")
    @TableField("account")
    private String account;

    @ApiModelProperty(value = "Login Timestamp", dataType = "long", example = "1")
    private Long loginMills;

    @ApiModelProperty(value = "Login Time", dataType = "date", example = "2025-02-01 11:22:33")
    private Date loginAt;

    @ApiModelProperty(value = "Login IP", dataType = "string", example = "127.0.0.1")
    @TableField("login_ip")
    private String loginIp;

    @ApiModelProperty(value = "Login Status,0- Success,1- Failed", dataType = "int", example = "0")
    private Integer loginState;

    @ApiModelProperty(value = "Login Error", dataType = "string", example = "Password Error")
    @TableField("login_error")
    private String loginError;
}
