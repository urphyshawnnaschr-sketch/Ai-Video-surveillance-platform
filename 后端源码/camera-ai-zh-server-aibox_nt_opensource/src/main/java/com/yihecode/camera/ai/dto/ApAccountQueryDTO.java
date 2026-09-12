package com.yihecode.camera.ai.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@ApiModel(value = "Account Query DTO")
public class ApAccountQueryDTO implements Serializable {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long accountId;
    /**
* Page Num
*/
    @ApiModelProperty(value = "Page Num")
    private Integer pageNum;
    /**
* page Capacity
*/
    @ApiModelProperty(value = "page Capacity")
    private Integer pageSize;

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

    @ApiModelProperty(value = "Role ID")
    private Long roleIds;

    @ApiModelProperty(value = "Team ID")
    private Long teamId;

    @ApiModelProperty(value = "Organization ID")
    private Set<Long> departIds;

    @ApiModelProperty(value = "Phone code")
    private String phone;

    private Set<Long> userIds;
}