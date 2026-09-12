package com.yihecode.camera.ai.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
* Description:
* <p>
* Date: 2023/6/15
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Alert Hour Segment VO")
public class ReportPeriodVo {

    @ApiModelProperty("Data ID")
    private Long id;

    @ApiModelProperty("Camera ID")
    private Long cameraId;

    @ApiModelProperty("Camera Name")
    private String cameraName;

    @ApiModelProperty("Algorithm ID")
    private Long algorithmId;

    @ApiModelProperty("Algorithm Name")
    private String algorithmName;

    @ApiModelProperty("Alert Hour Segment Area between")
    private String period;

}
