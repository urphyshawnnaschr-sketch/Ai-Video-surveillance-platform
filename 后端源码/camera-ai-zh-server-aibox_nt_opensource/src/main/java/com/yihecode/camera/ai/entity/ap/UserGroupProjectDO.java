package com.yihecode.camera.ai.entity.ap;


import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@ApiModel(value = "Project Annotation group Entity")
@Data
@TableName(value = "ap_user_group_project", autoResultMap = true)
public class UserGroupProjectDO {


    @ApiModelProperty(value = "User id", dataType = "long", example = "0")
    private Long projectId;

    @ApiModelProperty(value = "Annotation group id", dataType = "long", example = "0")
    private Long groupId;

}
