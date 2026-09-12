package com.yihecode.camera.ai.web.vo;

import lombok.Data;

import java.util.List;

/**
* Camera Query Condition
*/
@Data
public class CameraListData5Vo {

    //real International Corresponding Box ID
private List<Long> objectIds;

// real International Corresponding Camera Name
private String objectName;
}
