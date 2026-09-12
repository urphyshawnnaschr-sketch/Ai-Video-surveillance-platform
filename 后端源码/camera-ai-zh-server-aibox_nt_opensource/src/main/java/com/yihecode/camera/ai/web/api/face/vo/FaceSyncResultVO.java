package com.yihecode.camera.ai.web.api.face.vo;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
* Face Sync List Request Param
*/
@ApiIgnore
@Data
public class FaceSyncResultVO {

    //key Value
private String key;

// order Column No
private String sn;

// Data ID
private Long id;

// Sync Result Status,0- Success,1- Failed
private Integer status;

// Sync Result Message
private String msg;
}
