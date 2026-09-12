package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Face Detail Data
*
* @author zhou
* @since 2025.6.30
*/
@ApiModel(value = "Face Detail Data")
@Data
public class FaceTrackSearchInfoDTO {

    @ApiModelProperty(value = "Alarm ID", example = "1")
    private Long id;

    //Search Status, 0- not Search, 1- In Progress Search,2- Search Success,3- Search Failed
@ApiModelProperty(value ="Search Status, 0- not Search, 1- In Progress Search,2- Search Success,3- Search Failed", example ="2", dataType ="int")
private Integer status;

@ApiModelProperty(value ="Search Result Description", example ="Search Success", dataType ="string")
private String resultMsg;

@ApiModelProperty(value ="Search Result Time", example ="2025-01-01 01:01:01", dataType ="string")
private String resultAt;

@ApiModelProperty(value ="Search Data Count", example ="50", dataType ="int")
private Integer resultNum;

@ApiModelProperty(value ="most early out current Time", example ="2025-01-01 01:01:01", dataType ="string")
private String resultStartDate;

@ApiModelProperty(value ="most after out current Time", example ="2025-05-01 01:01:01", dataType ="string")
private String resultEndDate;

@ApiModelProperty(value ="most early out current Camera Name", example ="A Area", dataType ="string")
private String resultStartCameraName;

@ApiModelProperty(value ="most after out current Camera Name", example ="C Area", dataType ="string")
private String resultEndCameraName;
}
