package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ApiModel(value = "Query Model File List Param")
@Data
@ToString
public class ReportVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    private String key;

    @ApiModelProperty(value = "Camera ID", dataType = "long")
    private Long cameraId;

    @ApiModelProperty(value = "Result Set,json String", dataType = "string")
    private String params;

    @ApiModelProperty(value = "Image File", dataType = "file")
    private MultipartFile file;

    @ApiModelProperty(value = "Alert Time", dataType = "string")
    private String timestamp;
}
