package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* ap Team Complete member Batch Create
* @author zhoumingxing
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamBatchRequestVo {

    @ApiModelProperty(value = "Team ID", dataType = "long", example = "")
    private Long teamId;

    @ApiModelProperty(value = "Create Count", dataType = "integer", example = "")
    private Integer userNum;

    @ApiModelProperty(value = "Role Ids", dataType = "list", example = "")
    private List<Long> roleIds;

    @ApiModelProperty(value = "Account front Concat", dataType = "string", example = "")
    private String prefix;
}
