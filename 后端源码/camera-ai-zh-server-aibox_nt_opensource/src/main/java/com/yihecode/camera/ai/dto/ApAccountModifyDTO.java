package com.yihecode.camera.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "Account Add / Modify DTO")
public class ApAccountModifyDTO implements Serializable {

    /**
* ID
*/
    @ApiModelProperty(value = "Account id")
    private Long id;
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

    @ApiModelProperty(value = "Login Account")
    private String account;

    @ApiModelProperty(value = "Account Password", dataType = "string", example = "")
    private String password;

    @ApiModelProperty(value = "Status", notes = "0- Normal 1- Disabled")
    private Integer state;

    @ApiModelProperty(value = "Role ID List")
    private List<Long> roleIds;

    @ApiModelProperty(value = "Team ID List")
    private List<Long> teamIds;

    @ApiModelProperty(value = "Account Name", dataType = "string", example = "")
    private String name;

    @ApiModelProperty(value = "Phone code")
    private String phone;

    @ApiModelProperty(value = "belong belong Department")
    private Long departId;

    @ApiModelProperty(value = "work No")
    private String staffNo;

    /**
* Create Time
*/
    private Date createdAt;

    /**
* Modify Time
*/
    private Date updatedAt;

    /**
* Delete Time
*/
    private Date deletedAt;

}