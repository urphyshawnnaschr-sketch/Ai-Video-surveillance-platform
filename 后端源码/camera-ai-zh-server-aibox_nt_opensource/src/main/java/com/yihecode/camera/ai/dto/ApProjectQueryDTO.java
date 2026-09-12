package com.yihecode.camera.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Use at Query pProject Set combine Object
* Please By real International supplement Charge Field and Fix mapper File
*/
@Data
public class ApProjectQueryDTO {

    /**
* ID
*/
    @ApiModelProperty(value = "Project id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long projectId;
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


        /**
* Create Time
*/
            private String createdAt;
    
        /**
* Modify Time
*/
            private String updatedAt;
    
        /**
* Delete Time
*/
            private String deletedAt;
    
        /**
* Project Name
*/
        @ApiModelProperty(value = "Project Name")
            private String projectName;
    
        /**
* Project Type, 1- Image Category, 2- Target Detection
*/@ApiModelProperty(value = "Project Type 1- Image Category, 2- Target Detection 3-OCR Detection 4- increase Quantity Training")
            private Integer projectType;
    
        /**
* Project Description
*/
            private String projectDetail;
    
        /**
* Label Tree
*/
            private String labels;
    
        /**
* Annotation group id
*/
            private Integer userGroupId;
    
        /**
* Whether Need Quality Check
*/
            private Integer needReview;
    
        /**
* Quality Check than Example %
*/
            private Integer reviewRatio;
    
        /**
* Annotation Timeout Time
*/
            private Integer labelTaskTimeOut;
    
        /**
* Whether Need Aux assist Box
*/
            private Integer needAuxiliaryBox;
    
        /**
* Aux assist Box Size
*/
            private String auxiliaryBox;
    
        /**
* Data Set id
*/
            private Long dataFileId;
    
        /**
* Annotation Example Document id
*/
            private Long docFileId;
    
        /**
* Project Status,1- Data Read in,2- Smart can Annotation in,11- Annotation in,99- Deliver
*/@ApiModelProperty(value = "Project Status 1- Data Read in,2- Smart can Annotation in,11- Annotation in,99- Deliver")
            private Integer status;
    
        /**
* Image Count
*/
            private Integer itemCount;
    
        /**
* Annotation Count
*/
            private Integer labeled;

            private List<Long> projectIds;

            private String startTime;

            private String endTime;
    

}