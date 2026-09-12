package com.yihecode.camera.ai.web.api.face.vo;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
* Face Search Image Request Param
*/
@ApiIgnore
@Data
public class FaceSearchImageVO {

    //key Value
private String key;

// order Column No
private String sn;

// Data ID
private Long searchId;
}
