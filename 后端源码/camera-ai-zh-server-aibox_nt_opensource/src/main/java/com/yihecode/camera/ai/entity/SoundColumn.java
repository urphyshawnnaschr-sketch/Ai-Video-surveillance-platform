package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* IP Speaker Pole Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
@ApiModel(value = "IP Speaker Pole Management")
@TableName("tbl_biz_sound_column")
public class SoundColumn {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Speaker Pole Server")
    private String server;

    @ApiModelProperty(value = "Speaker Pole No")
    private String sn;

    @ApiModelProperty(value = "Speaker Pole Audio Quantity")
    private Integer vol;

    @ApiModelProperty(value = "Speaker Pole Type")
    private String type;

    @ApiModelProperty(value = "Speaker Pole Account")
    private String userName;

    @ApiModelProperty(value = "Speaker Pole Account Password")
    private String password;

}