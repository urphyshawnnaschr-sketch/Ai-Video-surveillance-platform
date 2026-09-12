package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
* Box Device Version Record table
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Box Device Version Record table")
@Data
@TableName(value = "tbl_biz_box_version")
public class BoxVersion implements Serializable {

    @ApiModelProperty(value = "Primary Key", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "Algorithm ID", dataType = "long")
    @TableField("algorithm_id")
    private Long algorithmId;

    @ApiModelProperty(value = "Box Device ID", dataType = "long")
    @TableField("box_id")
    private Long boxId;

    @ApiModelProperty(value = "Algorithm Version No", dataType = "string")
    @TableField("version_num")
    private String versionNum;
}

