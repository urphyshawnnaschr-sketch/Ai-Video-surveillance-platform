package com.yihecode.camera.ai.web.api.aibox.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* increase Add Algorithm For Camera Enable Hour, Camera no Method Connection, Camera no Method Get Frame, Model Load failed, Load success etc Situation in Line Report
*
* @author 465769438@qq.com
* @since 2025-04-14
*/
@Data
public class CameraReportVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    @JsonProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "Camera ID", dataType = "string")
    @JsonProperty(value = "camera_id")
    private Long cameraId;

    @ApiModelProperty(value = "Status Code", dataType = "int")
    @JsonProperty(value = "status")
    private Integer status;

    @ApiModelProperty(value = "Status Message", dataType = "string")
    @JsonProperty(value = "msg")
    private String msg;
}
