package com.yihecode.camera.ai.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Use at Query group Set combine Object
* Please By real International supplement Charge Field and Fix mapper File
*/
@Data
public class ApGroupQueryDTO {

    /**
* ID
*/
    @ApiModelProperty(value = "Annotation group id")
    private Long groupId;
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

    @ApiModelProperty(value = "Annotation group Name", dataType = "string", example = "Safe all Belt Category")
    private String name;

    @ApiModelProperty(value = "Annotation group Description", dataType = "string", example = "Annotation Safe all Belt")
    private String description;

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



}