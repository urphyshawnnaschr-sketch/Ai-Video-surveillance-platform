package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Description: Voice Push Phone Entity
*
*/
@Data
@ApiModel(value = "Voice Push Phone Entity")
@TableName("tbl_biz_voice_phone")
public class VoicePhone {

    /**
* Primary Key id
*/
    @ApiModelProperty(name = "Data ID")
    private Long id;

    /**
* Phone code
*/
    @ApiModelProperty(name = "Phone code")
    private String phone;

    /**
* Alert Level
*/
    @ApiModelProperty(name = "Alert Level")
    private Long levelId;

    /**
* Alert Level
*/
    @ApiModelProperty(name = "Alert Level")
    private Long accountId;
}
