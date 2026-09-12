package com.yihecode.camera.ai.web.ap.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Annotation Image List Query Param")
public class ImageSearchRequestVo {

    @ApiModelProperty("Project ID")
    private Long projectId;

    @ApiModelProperty("Annotation Status, 1 not Annotation 2 Annotation in 3 Annotation")
    private Integer labelStatus;

    @ApiModelProperty("Annotation member")
    private Long labelUserId;

    @ApiModelProperty("Label")
    private String label;

    @ApiModelProperty("Quality Check Status, 1 Pending Quality Check 2 Quality Check in 3 Quality Check Pass 4 Quality Check Rejected 8 Quality Check (3+4)")
    private Integer reviewStatus;

    @ApiModelProperty("Quality Check member")
    private Long reviewUserId;
    private Integer page;
    private Integer limit;
}
