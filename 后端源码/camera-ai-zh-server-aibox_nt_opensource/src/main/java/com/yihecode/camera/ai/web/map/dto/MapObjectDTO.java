package com.yihecode.camera.ai.web.map.dto;

import lombok.Data;

import java.util.List;

@Data
public class MapObjectDTO {

    //Record ID
private Long id;

// Ground image ID or part Layer ID
private Long mapId;

// Camera ID | Camera Group ID
private Long objectId;

// Camera Name | Camera Group Name Name
private String objectName;

// Camera Corresponding Box Name | Camera Group Corresponding part Layer Path Name
private String relName;

// Camera Group Contain Camera Count
private String cameraCount;

// Mark Coordinate
private List<Double> position;

// Type Name camera- Camera | group- Camera Group
private String typeName;

}
