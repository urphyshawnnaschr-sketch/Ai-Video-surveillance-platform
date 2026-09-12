package com.yihecode.camera.ai.web.map.dto;

import lombok.Data;

/**
* Camera Group Detail
*/
@Data
public class CameraGroupItemDTO {

    //Camera ID
private Long id;

// Camera Name
private String name;

// Cover image File Name
private String filename;

// Calculate Device ID
private Long locationId;

// Calculate Device Name
private String locationName;
}
