package com.yihecode.camera.ai.web.face.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "Stranger produce Person Add to Exist Person member Return Data")
@Data
public class FaceUserAddToExistDTO {

    @ApiModelProperty(value = "Success ID", example = "true", dataType = "boolean")
    private boolean success;

    @ApiModelProperty(value = "Success Message", example = "Person member Image super over 3 sheet, Please Delete Other again Add", dataType = "string")
    private String msg;

    @ApiModelProperty(value = "Exist Image IDs", example = "[1,2,3]", dataType = "list")
    private List<Long> faceImageIds;
}
