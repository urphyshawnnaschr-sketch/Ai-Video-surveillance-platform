package com.yihecode.camera.ai.web.vo;

import lombok.Data;

import java.util.List;

/**
* Box Query Condition
*/
@Data
public class LocationListData5Vo {

    //real International Corresponding Department ID
private List<Long> objectIds;

// real International Corresponding Box Name
private String objectName;

// Whether Query All
private boolean objectAll = false;
}
