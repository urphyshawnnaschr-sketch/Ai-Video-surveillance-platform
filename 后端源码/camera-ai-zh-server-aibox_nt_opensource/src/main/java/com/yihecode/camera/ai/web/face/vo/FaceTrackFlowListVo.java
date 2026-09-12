package com.yihecode.camera.ai.web.face.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel("Person member Stream Dynamic Data Query Param")
@Data
public class FaceTrackFlowListVo {

    //Start Date
@ApiModelProperty(value ="Start Time", example ="2025-02-01 11:22:33", required = true)
@JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
private Date startDate;

// End Date
@ApiModelProperty(value ="End Date", example ="2025-02-02 11:22:33", required = true)
@JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
private Date endDate;

// Person member ID
@ApiModelProperty(value ="Person member ID", example ="1", required = true)
private Long userId;
}
