package com.yihecode.camera.ai.web.vo;

import lombok.Data;

import java.util.List;

/**
* Camera Page Query Param
*
* @author zhou
* @since 2025.6.19
*/
@Data
public class CameraListVo {

    //Page Number
private Integer page = 1;

// Pagination Count
private Integer limit = 10;

// Camera Name
private String name;

// Box ID
private Long locationId;

// Box Type, no Use
private String locationType;

// belong belong Department
private Long departId;

// Algorithm ID
private List<Long> algorithmIds;

// Sort Field
private List<SortByVo> sortBys;

// Non input in Param, Pending Query Box Ids
private List<Long> locationIds;

// Non input in Param, Pending Query Camera Ids
private List<Long> cameraIds;

}
