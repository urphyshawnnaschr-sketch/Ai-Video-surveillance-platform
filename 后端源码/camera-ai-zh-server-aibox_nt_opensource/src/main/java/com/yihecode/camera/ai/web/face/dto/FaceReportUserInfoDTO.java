package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "By User ID Get Trajectory User Info")
@Data
public class FaceReportUserInfoDTO {

    @ApiModelProperty(value = "User ID", example = "1", dataType = "string")
    private Long userId;

    @ApiModelProperty(value = "User Name", example = "sheet Three", dataType = "string")
    private String userName;

    @ApiModelProperty(value = "Group Name Name", example = "White Name form", dataType = "string")
    private String groupName;

    @ApiModelProperty(value = "most early Snapshot Camera", example = "A Area", dataType = "string")
    private String startCameraName;

    @ApiModelProperty(value = "most after Snapshot Camera", example = "B Area", dataType = "string")
    private String endCameraName;

    @ApiModelProperty(value = "most early Snapshot Time", example = "2025-01-01 01:01:01", dataType = "string")
    private String startCameraDate;

    @ApiModelProperty(value = "most after Snapshot Time", example = "2025-01-01 01:01:01", dataType = "string")
    private String endCameraDate;

    @ApiModelProperty(value = "Search Alarm Count", example = "55", dataType = "int")
    private int reportCount;

    @ApiModelProperty(value = "Camera List", dataType = "list")
    private List<FaceReportCameraDTO> cameras;

}
