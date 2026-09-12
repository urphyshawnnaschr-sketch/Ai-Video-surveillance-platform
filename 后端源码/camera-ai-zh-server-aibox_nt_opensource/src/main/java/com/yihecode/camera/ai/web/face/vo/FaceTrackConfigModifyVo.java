package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Face Tracking Base image Add / Modify Param
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel("Face Tracking Base image Add / Modify Param")
@Data
public class FaceTrackConfigModifyVo {

    //Primary Key
@ApiModelProperty(value ="Primary Key", example ="1")
private Long id;

// Config Name
@ApiModelProperty(value ="Config Name", example ="Default Base image", required = true)
private String name;

// Ground image File Name Name
@ApiModelProperty(value ="Ground image File Name Name", example ="aaa.jpg", required = true)
private String filename;

// Valid Status,0- Invalid,1- Valid
@ApiModelProperty(value ="Valid Status,0- Invalid,1- Valid", example ="1", required = true)
private Integer state;
}
