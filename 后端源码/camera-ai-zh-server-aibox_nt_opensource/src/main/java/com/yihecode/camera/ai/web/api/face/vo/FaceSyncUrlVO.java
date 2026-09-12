package com.yihecode.camera.ai.web.api.face.vo;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
* Face Sync List Request Param
*/
@ApiIgnore
@Data
public class FaceSyncUrlVO {

    //key Value
private String key;

// order Column No
private String sn;

// original has Face Service IP
private String faceHttpIp;

// original has Face Service SN
private String faceSn;

}
