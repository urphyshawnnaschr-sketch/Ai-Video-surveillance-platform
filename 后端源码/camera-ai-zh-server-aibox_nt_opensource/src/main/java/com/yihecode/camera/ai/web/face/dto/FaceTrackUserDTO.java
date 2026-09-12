package com.yihecode.camera.ai.web.face.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Person member Info
*
* @author zhou
* @since 2025.6.20
*/
@ApiModel(value = "Person member Info")
@Data
public class FaceTrackUserDTO {

    @ApiModelProperty(value = "ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "Name", example = "sheet Three")
    private String name;

    @ApiModelProperty(value = "Phone", example = "18888888888")
    private String tel;

    @ApiModelProperty(value = "Remark", example = "One Remark")
    private String remark;

}
