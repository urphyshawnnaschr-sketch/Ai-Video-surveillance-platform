package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel("Submit Annotation Info Request")
@NoArgsConstructor
@AllArgsConstructor
public class CommitRequestVo {

    @ApiModelProperty(value = "Project id", dataType = "long", example = "1234567890", required = true)
    private Long projectId;

    private List<CommitVo> commits;
}
