package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Face Detail Data
*
* @author zhou
* @since 2025.6.30
*/
@ApiModel(value = "Face Detail Data")
@Data
public class FaceReportInfoDTO {

    @ApiModelProperty(value = "Alarm ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "Camera Name", example = "Guard Room")
    private String cameraName;

    @ApiModelProperty(value = "Alarm Time", example = "2025-06-30 12:14:15")
    private String reportTime;

    @ApiModelProperty(value = "Similarity, Keep Two Bit small Number", example = "0.95")
    private String similiarity;

    @ApiModelProperty(value = "User Name", example = "sheet Three")
    private String userName;

    @ApiModelProperty(value = "User ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "Group Name Name", example = "Black Name form")
    private String groupName;

    @ApiModelProperty(value = "Stranger produce Person", example = "Stranger produce Person")
    private String stranger;

    @ApiModelProperty(value = "Stranger produce Person Type, 0- Familiar Person, 1- Stranger produce Person", example = "1")
    private Integer strangerType;
}
