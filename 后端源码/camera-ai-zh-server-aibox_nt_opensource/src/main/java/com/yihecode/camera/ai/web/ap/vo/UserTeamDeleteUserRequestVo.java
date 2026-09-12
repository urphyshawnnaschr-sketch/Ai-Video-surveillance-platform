package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* from Team Delete Complete member
* @author zhoumingxing
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamDeleteUserRequestVo {

    @ApiModelProperty(value = "Team ID", dataType = "long", example = "")
    private Long teamId;

    @ApiModelProperty(value = "User ID", dataType = "long", example = "")
    private Long userId;

}
