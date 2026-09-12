package com.yihecode.camera.ai.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Account Management Entity - front Period Simple form Process
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Account Management Entity")
@Data
@TableName("tbl_biz_account")
public class Account {

    @ApiModelProperty(value = "id")
    private Long id;

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

    @ApiModelProperty(value = "Create Time")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time")
    @TableField("updated_at")
    private Date updatedAt;

    @ApiModelProperty(value = "Push cid")
    @TableField("push_cid")
    private String pushCid;

    @ApiModelProperty(value = "Device Run app Version")
    @TableField("app_version")
    private String appVersion;

    @ApiModelProperty(value = "Phone code")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "belong belong Department")
    @TableField("depart_id")
    private Long departId;

    @ApiModelProperty(value = "Whether super Level Management member, 0- No,1- is")
    @TableField("is_super")
    private Integer isSuper;
}