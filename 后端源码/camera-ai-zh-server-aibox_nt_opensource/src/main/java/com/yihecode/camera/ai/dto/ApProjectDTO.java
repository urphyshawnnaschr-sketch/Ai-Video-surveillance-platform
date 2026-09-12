package com.yihecode.camera.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihecode.camera.ai.entity.ap.AuxiliaryBox;
import com.yihecode.camera.ai.entity.ap.Label;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* pProject DTO
*/
@Data
@ApiModel(value = "Project DTO")
public class ApProjectDTO implements Serializable {


    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    private Date deletedAt;

    @ApiModelProperty(value = "Project Name", dataType = "String")
    private String projectName;

    @ApiModelProperty(value = "Project Type, 1- Image Category, 2- Target Detection 3-OCR Detection 4- increase Quantity Training", dataType = "int")
    private Integer projectType;

    @ApiModelProperty(value = "Project Description", dataType = "String")
    private String projectDetail;

    @ApiModelProperty(value = "Project Description", dataType = "list")
    private List<Label> labels;


    @ApiModelProperty(value = "Annotation group id", dataType = "String")
    private List<Long> userGroupId;

    @ApiModelProperty(value = "Whether Need Quality Check 0 No 1 is")
    private Integer needReview;

    @ApiModelProperty(value = "Quality Check than Example")
    private Integer reviewRatio;

    @ApiModelProperty(value = "Annotation Timeout Time")
    private Integer labelTaskTimeOut;

    @ApiModelProperty(value = "Whether Need Aux assist Box")
    private Integer needAuxiliaryBox;

    @ApiModelProperty(value = "Aux assist Box Size")
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

    @ApiModelProperty(value = "Annotation than Example")
    private BigDecimal annoRatio;

    @ApiModelProperty(value = "Algorithm id")
    private List<Long> algorithmIds;

    @ApiModelProperty(value = "Annotation Example Path")
    private String docPath;

    @ApiModelProperty(value = "not Quality Check Image")
    private Integer noPassImageNum;
    @ApiModelProperty(value = "Quality Check Total")
    private Integer reviewNum;


}