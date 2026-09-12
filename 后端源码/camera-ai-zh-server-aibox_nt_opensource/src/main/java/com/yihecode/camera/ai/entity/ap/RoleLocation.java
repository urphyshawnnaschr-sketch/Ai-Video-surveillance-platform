package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Role Region Entity
* @author Abyss
*/
@ApiModel(value = "Role Region Entity")
@Data
@TableName(value = "ap_role_location", autoResultMap = true)
public class RoleLocation {

    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Role id", dataType = "long", example = "0")
    private Long roleId;

    @ApiModelProperty(value = "Region id", dataType = "long", example = "0")
    private Long locationId;

}
