package com.yihecode.camera.ai.web.dto;

import lombok.Data;

@Data
public class CameraLocationDTO {

    private Long cameraId;

    private Long locationId;

    private String cameraName;

    private String locationName;
}
