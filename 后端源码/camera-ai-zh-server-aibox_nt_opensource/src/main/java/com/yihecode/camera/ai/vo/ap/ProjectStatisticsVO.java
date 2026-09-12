package com.yihecode.camera.ai.vo.ap;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Author lichangliang
* @Date 2023/7/28 13:33
* @Describe
* @Version 1.0
*/
@Data
public class ProjectStatisticsVO {
    @ApiModelProperty(value = "Project Name")
    private String projectName;
    @ApiModelProperty(value = "User Name")
    private String userName;
    @ApiModelProperty(value = "User Role")
    private String roleName;
    @ApiModelProperty(value = "Annotation Image Count")
    private Integer annoNum;
    @ApiModelProperty(value = "Annotation Box Count")
    private Integer annoDetailNum;
    @ApiModelProperty(value = "Quality Check combine Grid Count")
    private Integer reviewNum;
    @ApiModelProperty(value = "not combine Grid Count")
    private Integer backNum;
    @ApiModelProperty(value = "Invalid Image Count")
    private Integer invalidNum;
}
