package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "Edge Box Sync Camera Status Param")
@Data
public class CameraStatuslVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    private String key;

    @ApiModelProperty(value = "Box No", dataType = "string")
    private String sn;

    @ApiModelProperty(value = "Camera ID", dataType = "long")
    private Long cameraId;

    @ApiModelProperty(value = "Description Info", dataType = "string")
    private String desc;

    @ApiModelProperty(value = "Camera Status (1000 Inference in 2000 Download Algorithm in 3000 not make Use)", dataType = "integer")
    private Integer status;

    @ApiModelProperty(value = "Algorithm Name, multi kind Algorithm For Current Camera Draw Surface", dataType = "string")
    private List<String> name;
}
