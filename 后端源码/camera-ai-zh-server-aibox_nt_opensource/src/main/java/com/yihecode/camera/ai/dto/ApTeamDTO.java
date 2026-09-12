package com.yihecode.camera.ai.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "Team DTO")
public class ApTeamDTO implements Serializable {

    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @ApiModelProperty(value = "Team Name", dataType = "string", example = "Safe all Belt Category")
    private String name;

    @ApiModelProperty(value = "Team Description", dataType = "string", example = "Annotation Safe all Belt")
    private String description;

    @ApiModelProperty(value = "Creator User ID", dataType = "long")
    private Long userId;

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
}