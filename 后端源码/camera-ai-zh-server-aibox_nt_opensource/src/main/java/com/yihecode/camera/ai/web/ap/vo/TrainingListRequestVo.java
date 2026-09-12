package com.yihecode.camera.ai.web.ap.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("increase Quantity Training List Query Param")
public class TrainingListRequestVo {

    @ApiModelProperty("Camera ID")
    private Long cameraId;

    @ApiModelProperty("Algorithm ID")
    private Long algorithmId;

    private Integer page;

    private Integer limit;
}
