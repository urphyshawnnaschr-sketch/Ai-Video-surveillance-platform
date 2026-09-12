package com.yihecode.camera.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* pFile DO
*/
@ApiModel(value = "Data Set dto")
@Data
public class ApFileDTO {

    /**
* ID
*/
    @ApiModelProperty(value = "Primary Key", dataType = "long")
    private Long id;

    /**
* Create Time
*/
    @ApiModelProperty(value = "Create Time")
    private Date createdAt;

    /**
* Modify Time
*/
    @ApiModelProperty(value = "Modify Time")
    private Date updatedAt;

    /**
* Delete Time
*/
    @ApiModelProperty(value = "Delete Time")
    private Date deletedAt;

    /**
* User ID
*/
    @ApiModelProperty(value = "User ID")
    private Long userId;

    /**
* File Type, 1-zip Data Set,2-excel table Grid,3-doc
*/
    @ApiModelProperty(value = "File Type, 1-zip Data Set,2-excel table Grid,3-doc")
    private Integer fileType;

    /**
* File Address
*/
    @ApiModelProperty(value = "File Address")
    private String rawData;

    /**
* File Status
*/
    @ApiModelProperty(value = "File Status")
    private String status;

}