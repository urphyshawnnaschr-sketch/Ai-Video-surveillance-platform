package com.yihecode.camera.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Use at Query pProject Set combine Object
* Please By real International supplement Charge Field and Fix mapper File
*/
@Data
@ApiModel(value = "Project Count")
public class ApProjectStatisticsQueryDTO {

    @ApiModelProperty(value = "Project id")
    private Long projectId;

    @ApiModelProperty(value = "Annotation group id")
    private Integer userGroupId;

    @ApiModelProperty(value = "Team id")
    private Long teamId;

    @ApiModelProperty(value = "group member id")
    private Long userId;

    @ApiModelProperty(value = "Start Time",required = true)
    private String startTime;

    @ApiModelProperty(value = "End Time",required = true)
    private String endTime;

    @ApiModelProperty(value = "Page Num")
    private Integer pageNum;

    @ApiModelProperty(value = "page Capacity")
    private Integer pageSize;



}