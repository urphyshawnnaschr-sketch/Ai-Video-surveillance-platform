package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "By The belong Organization and The belong Group Query Camera List")
@Data
public class CameraListData7Vo {

    @ApiModelProperty(value = "The belong Department ID List", example = "[1,2,3]", dataType = "list")
    private List<Long> departIds;

    @ApiModelProperty(value = "The belong Group ID List", example = "[1,2,3]", dataType = "list")
    private List<Long> cameraGroupIds;

    @ApiModelProperty(value = "Check select Camera ID", example = "[1,2,3]", dataType = "list")
    private List<Long> cameraIds;

    @ApiModelProperty(value = "Whether Run in,0 or empty - All,1- is", example = "0", dataType = "int")
    private Integer running;

    @ApiModelProperty(value = "Page Number", example = "1", dataType = "int")
    private Integer page = 1;

    @ApiModelProperty(value = "Pagination Count", example = "6", dataType = "int")
    private Integer limit = 6;
}
