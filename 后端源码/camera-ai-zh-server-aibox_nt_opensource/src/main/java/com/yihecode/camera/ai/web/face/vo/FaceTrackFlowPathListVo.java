package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Person member Stream Dynamic Data Query
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel("Person member Search Data Query Param")
@Data
public class FaceTrackFlowPathListVo {

    @ApiModelProperty(value = "Page Number", example = "1", dataType = "int", required = true)
    private Integer page = 1;

    @ApiModelProperty(value = "Pagination Count", example = "10", dataType = "int", required = true)
    private Integer limit = 10;

    @ApiModelProperty(value = "Search ID", example = "1", dataType = "long", required = true)
    private Long searchId;

    @ApiModelProperty(value = "Camera ID", example = "1", dataType = "long")
    private Long cameraId;

    @ApiModelProperty(value = "Start Time", example = "2025-01-01 01:01:01", dataType = "date")
    private Date startDate;

    @ApiModelProperty(value = "End Time", example = "2025-01-01 01:01:01", dataType = "date")
    private Date endDate;

}
