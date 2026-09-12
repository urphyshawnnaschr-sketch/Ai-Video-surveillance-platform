package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Camera Group Detail Query
*/
@Data
@ApiModel(value = "Page Query Param")
public class CameraGroupItemListVo {

    @ApiModelProperty(value = "Page Number", example = "1", required = true)
    private Integer page = 1;

    @ApiModelProperty(value = "Pagination Count", example = "10", required = true)
    private Integer limit = 10;

    @ApiModelProperty(value = "Group ID", example = "1", required = true)
    private Long groupId;

    @ApiModelProperty(value = "Group ID", example = "1", required = true)
    private String name;

    @ApiModelProperty(hidden = true)
    private List<Long> cameraIds;
}
