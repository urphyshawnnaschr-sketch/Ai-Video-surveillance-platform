package com.yihecode.camera.ai.web.api.face.vo;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
* Face than for Image Request Param
*/
@ApiIgnore
@Data
public class FaceCompareImageVO {

    //key Value
private String key;

// order Column No
private String sn;

// Image File Name
private String imageName;
}
