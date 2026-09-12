package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
* Group Push Config Entity class
*/
@Data
@ApiModel("Group Push Config")
@TableName("group_push_config")
public class GroupPushConfig {

    @ApiModelProperty(value = "Primary Key", required = true)
    private String id;

    @ApiModelProperty(value = "Camera group id", required = true)
    private Long groupId;

    @ApiModelProperty(value = "Camera group Name", required = true)
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "multi Algorithm Name, Use English Comma No Separate open", required = true)
    private String algorithmNames;

    @ApiModelProperty(value = "Feishu Group Name", required = true)
    @TableField(exist = false)
    private String socialHookNames;


    @ApiModelProperty(value = "Hierarchy, from 1 Start", required = true)
    private Integer groupLevel = 1;

    @ApiModelProperty(value = "multi Algorithm id, Use English Comma No Separate open", required = true)
    private String algorithmIds;


    @ApiModelProperty(value = "multi Duty Task Person Name, Use English Comma No Separate open", required = true)
    private String responsiblePerson;

    @ApiModelProperty(value = "multi Duty Task Person work No, Use English Comma No Separate open", required = true)
    private String responsiblePersonNo;

    @ApiModelProperty(value = "multi Push Group id, Use English Comma No Separate open, when Hierarchy for 3 Hour Wait Required")
    private String socialHookIds;

    @ApiModelProperty(value = "Create Time")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time")
    private Date updatedAt;
}
