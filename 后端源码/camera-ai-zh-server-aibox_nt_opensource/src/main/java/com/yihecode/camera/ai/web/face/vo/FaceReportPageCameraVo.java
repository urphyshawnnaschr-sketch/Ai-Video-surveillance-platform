package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Face Alarm Query _ Camera Query Param
*
* @author zhou
* @since 2025.6.30
*/
@ApiModel(value = "Face Alarm Query _ Camera Query Param")
@Data
public class FaceReportPageCameraVo {

    @ApiModelProperty(value = "Group ID List", example = "[1,2,3]", dataType = "list")
    private List<Long> groupIds;
}
