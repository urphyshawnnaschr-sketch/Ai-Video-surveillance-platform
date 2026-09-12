package com.yihecode.camera.ai.web.api.face.vo;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
* Face Sync List Request Param
*/
@ApiIgnore
@Data
public class FaceSyncObjectListVO {

    //key Value
private String key;

// order Column No
private String sn;

}
