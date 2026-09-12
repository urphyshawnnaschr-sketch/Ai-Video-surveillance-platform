package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* ap Team Complete member Param
* @author zhoumingxing
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamRelationshipsRequestVo {

    @ApiModelProperty(value = "ID", dataType = "long", example = "")
    private Long id;

    @ApiModelProperty(value = "Image ID", dataType = "long", example = "")
    private Long userId;

    @ApiModelProperty(value = "Login Account", dataType = "string", example = "")
    private String account;

    @ApiModelProperty(value = "Login Password", dataType = "string", example = "")
    private String password;

    @ApiModelProperty(value = "User Name", dataType = "string", example = "")
    private String name;

    @ApiModelProperty(value = "Status ID", dataType = "integer", example = "0")
    private Integer state;

    @ApiModelProperty(value = "Team ID", dataType = "long", example = "0")
    private Long teamId;

}
