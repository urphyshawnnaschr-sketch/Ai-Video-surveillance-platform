package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Role Permission Entity
* @author zhoumingxing
*/
@ApiModel(value = "Role Permission Entity")
@Data
@TableName(value = "ap_role_permission", autoResultMap = true)
public class RolePermission {

    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Role id", dataType = "long", example = "0")
    private Long roleId;

    @ApiModelProperty(value = "Menu id", dataType = "long", example = "0")
    private Long menuId;

}
