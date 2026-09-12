package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* @Author lichangliang
* @Date 2023/7/28 15:33
* @Describe
* @Version 1.0
*/
@ApiModel(value = "Export Entity")
@Data
@TableName(value = "ap_export", autoResultMap = true)
public class ApExportDO {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @TableLogic(value = "null", delval = "now()")
    @JsonIgnore
    private Date deletedAt;

    @ApiModelProperty(value = "Project id", dataType = "long", example = "1234567890", required = true)
    private Long projectId;

    @ApiModelProperty(value = "Storage Path")
    private String storagePath;
    @ApiModelProperty(value = "Export Format 1 VOC 2COCO")
    private Integer type;

    @ApiModelProperty(value = "User id")
    private Long userId;

    @ApiModelProperty(value = "Export Status 0 Type Package in 1 Type Package Complete Complete")
    private Integer status;

    @ApiModelProperty(value = "Whether Export Image 1 for is 0 for No")
    private Integer exportImage;

}
