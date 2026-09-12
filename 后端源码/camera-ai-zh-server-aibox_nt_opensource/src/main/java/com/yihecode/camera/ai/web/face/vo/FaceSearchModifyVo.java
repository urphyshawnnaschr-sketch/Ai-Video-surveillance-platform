package com.yihecode.camera.ai.web.face.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* Face Search Add / Edit Param
*
* @author zhou
* @since 2025.6.30
*/
@ApiModel(value = "Face Search Add / Edit Param")
@Data
public class FaceSearchModifyVo {

    @ApiModelProperty(value = "File Name Name", example = "aaaa.jpg", required = true)
    private String filename;

    @ApiModelProperty(value = "Camera Group ID List", example = "", required = false)
    private List<Long> groupIds;

    @ApiModelProperty(value = "Start Time", example = "2025-06-30 01:02:03", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @ApiModelProperty(value = "End Time", example = "2025-06-30 09:02:03", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
}
