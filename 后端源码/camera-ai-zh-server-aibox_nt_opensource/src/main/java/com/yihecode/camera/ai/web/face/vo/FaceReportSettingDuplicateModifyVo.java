package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Face Alarm Data Remove re reply Set
* @author zhou
* @since 2025.6.30
*/
@Data
@ApiModel(value = "Face Alarm Data Remove re reply Set Param")
public class FaceReportSettingDuplicateModifyVo {

    @ApiModelProperty(value = "Remove re min Number", example = "5", required = true)
    private Integer minute;
}
