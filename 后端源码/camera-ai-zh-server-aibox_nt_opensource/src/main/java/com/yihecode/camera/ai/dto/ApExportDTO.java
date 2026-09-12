package com.yihecode.camera.ai.dto;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel(value = "Export DTO")
@Data
public class ApExportDTO {
    @ApiModelProperty(value = "Primary Key", dataType = "long")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private String createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private String updatedAt;

    @TableLogic(value = "null", delval = "now()")
    @JsonIgnore
    private String deletedAt;

    @ApiModelProperty(value = "Project id", dataType = "long", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long projectId;

    @ApiModelProperty(value = "Storage Path")
    private String storagePath;

    @ApiModelProperty(value = "Export Format 1 VOC 2 COCO")
    private Integer type;

    @ApiModelProperty(value = "User id")
    private Long userId;

    @ApiModelProperty(value = "Export Status 0 Type Package in 1 Type Package Complete Complete")
    private Integer status;

    @ApiModelProperty(value = "Whether Export Image 1 for is 0 for No")
    private Integer exportImage;

    private Long fileId;

    private String projectName;

}
