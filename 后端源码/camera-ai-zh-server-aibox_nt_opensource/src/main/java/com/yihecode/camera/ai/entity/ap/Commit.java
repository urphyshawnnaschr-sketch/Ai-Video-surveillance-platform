package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel(value = "Annotation Submit Entity")
@Data
@TableName(value = "ap_commit", autoResultMap = true)
public class Commit {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890")
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

    @ApiModelProperty(value = "Image ID", dataType = "long", example = "1234567890", required = true)
    private Long imageId;

    @ApiModelProperty(value = "Annotation id", dataType = "long", example = "1234567890")
    private Long userId;

    @ApiModelProperty(value = "Annotation Box Count", dataType = "int", example = "12")
    private Integer annotationCount;

    @ApiModelProperty(value = "Whether Valid", dataType = "int", example = "0 Invalid 1 Valid")
    private Byte isValid;

    @ApiModelProperty(value = "Annotation Box")
    @TableField(exist = false)
    private List<Annotation> annotations;
}
