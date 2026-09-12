package com.yihecode.camera.ai.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "Camera Import DTO")
public class CameraImportDTO {

    private Long id;

    private String rtspUrl;

}
