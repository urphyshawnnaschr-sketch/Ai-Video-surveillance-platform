package com.yihecode.camera.ai.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class CameraGroupCameraListDTO {

    //not Select Camera
private List<CameraGroupCameraDTO> cameras;

// Select Camera
private List<Long> selects;
}
