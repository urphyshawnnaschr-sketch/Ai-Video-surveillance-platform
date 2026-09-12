package com.yihecode.camera.ai.web.vo;

import com.yihecode.camera.ai.entity.Camera;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Camera Param")
public class CameraVo {

    @ApiModelProperty("Camera Basic Param")
    private Camera camera;

    @ApiModelProperty("Relate Algorithm ids, Example (1,2,3)")
    private String algorithmvos;

    @ApiModelProperty("Relate Confidence Example (0.5,0.5,0.5)")
    private String confidencevos;

    @ApiModelProperty("Region rois")
    private String markpointsvos;

    @ApiModelProperty("Whether Update roi(0- No,1- is)")
    private Integer updatePoint = 0;
}
