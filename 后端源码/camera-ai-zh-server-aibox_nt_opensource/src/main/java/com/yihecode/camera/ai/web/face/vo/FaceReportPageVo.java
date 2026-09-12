package com.yihecode.camera.ai.web.face.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* Face Page Query Param
*
* @author zhou
* @since 2025.6.30
*/
@Data
@ApiModel(value = "Face Page Query Param")
public class FaceReportPageVo {

    @ApiModelProperty(value = "Page Number", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "Pagination Count", example = "18")
    private Integer limit = 18;

    @ApiModelProperty(value = "Camera Group ID", example = "[1,2,3]", dataType = "list")
    private List<Long> groupIds;

    @ApiModelProperty(value = "Person member Group ID", example = "[1,2,3]", dataType = "long")
    private Long groupId;

    @ApiModelProperty(value = "Whether Query Stranger produce Person, 1- Stranger produce Person, Other Value", example = "1", dataType = "int")
    private Integer isStranger;

    @ApiModelProperty(value = "Camera ID", example = "[1,2,3]", dataType = "list")
    private List<Long> cameraIds;

    @ApiModelProperty(value = "Name", example = "Li Bright")
    private String userName;

    @ApiModelProperty(value = "Start Time, Format:yyyy-MM-dd HH:mm:ss", example = "2025-05-05 01:02:03")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "End Time, Format:yyyy-MM-dd HH:mm:ss", example = "2025-05-06 01:02:03")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "Current Face Alarm ID, Only Query up down Valid", example = "1", dataType = "long")
    private Long reportId;

    @ApiModelProperty(value = "up down,0= up One, 1= down One, Only Query up down Valid", example = "1", dataType = "int")
    private Integer type;

    //supplement Charge Query Condition, User Ids, by userName select Fixed Param after Query Get to
@ApiModelProperty(hidden = true)
private List<Long> userIds;
}
