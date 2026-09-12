package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;


@ApiModel(value = "Annotation Box Entity")
@Data
@TableName(value = "ap_annotation", autoResultMap = true)
public class Annotation {

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

    @ApiModelProperty(value = "Image ID", dataType = "long", example = "1234567890", required = true)
    private Long imageId;

    @ApiModelProperty(value = "Submit ID", dataType = "long", example = "1234567890")
    private Long commitId;

    @ApiModelProperty(value = "Tag Name", dataType = "string", example = "Person")
    private String tagName;

    @ApiModelProperty(value = "Annotation Type", dataType = "int", example = "1 Image Category 2 Rectangle Box 5 form Point 7 multi Edge Shape")
    private Integer annotationType;

    @ApiModelProperty(value = "Annotation Box")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private AnnotationData annotation;

}
