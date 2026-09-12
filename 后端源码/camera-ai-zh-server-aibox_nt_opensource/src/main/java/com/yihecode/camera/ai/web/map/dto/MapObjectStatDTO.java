package com.yihecode.camera.ai.web.map.dto;

import lombok.Data;

import java.util.List;

@Data
public class MapObjectStatDTO {

    //Box ID or Camera ID
private Long objectId;

// Box Name or Camera Name
private String objectName;

// Type,box,camera
private String type;

// Relate Name, Department Name or Box Name
private String relName;

// Count
private Integer num;

// Bit set
private List<Double> position;

// Color
private String color;

// svg
private String svgName;

// Rule rule Name
private String ruleName;

// most small most big Value
private String minMax;

private List<ReportDTO> reportList;
}
