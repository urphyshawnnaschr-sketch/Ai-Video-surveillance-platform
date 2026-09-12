package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/19 22:22
* @Describe
* @Version 1.0
*/
@ApiModel(value = "Project Entity")
@Data
@TableName(value = "ap_project", autoResultMap = true)
public class ApProjectDO {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @TableLogic(value = "null", delval = "now()")
    @JsonIgnore
    private Date deletedAt;

    @ApiModelProperty(value = "Project Name", dataType = "String")
    private String projectName;

    @ApiModelProperty(value = "Project Type, 1- Image Category, 2- Target Detection 3-OCR Detection 4- increase Quantity Training", dataType = "int")
    private Integer projectType;

    @ApiModelProperty(value = "Project Description", dataType = "String")
    private String projectDetail;

    @ApiModelProperty(value = "Label Tree", dataType = "String")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Label> labels;

    @ApiModelProperty(value = "Annotation group id")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> userGroupId;

    @ApiModelProperty(value = "Whether Need Quality Check")
    private Integer needReview;

    @ApiModelProperty(value = "Quality Check than Example")
    private Integer reviewRatio;

    @ApiModelProperty(value = "Annotation Timeout Time")
    private Integer labelTaskTimeOut;

    @ApiModelProperty(value = "Whether Need Aux assist Box")
    private Integer needAuxiliaryBox;

    @ApiModelProperty(value = "Aux assist Box Size")
    @TableField(value = "auxiliary_box",typeHandler = JacksonTypeHandler.class)
    private AuxiliaryBox auxiliaryBox;

    @ApiModelProperty(value = "Data Set id")
    private Long dataFileId;

    @ApiModelProperty(value = "Annotation Example Document id")
    private Long docFileId;

    @ApiModelProperty(value = "Project Status,1- Data Read in,2- Smart can Annotation in,11- Annotation in,99- Deliver")
    private Integer status;

    @ApiModelProperty(value = "Image Count")
    private Integer itemCount;

    @ApiModelProperty(value = "Annotation Count")
    private Integer labeled;

    @ApiModelProperty(value = "Algorithm id")
    @TableField(value = "algorithm_ids",typeHandler = JacksonTypeHandler.class)
    private List<Long> algorithmIds;


}
