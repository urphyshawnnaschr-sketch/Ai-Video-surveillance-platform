package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Description: SMS Push Phone Entity
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
@ApiModel(value = "SMS Push Phone Entity")
@TableName("tbl_biz_sms_phone")
public class SmsPhone {

    /**
* Primary Key id
*/
    @ApiModelProperty(value = "Data ID")
    private Long id;

    /**
* Phone code
*/
    @ApiModelProperty(value = "Phone code", example = "18888888888")
    private String phone;

    @ApiModelProperty(value = "Login Account ID")
    @TableField("account_id")
    private Long accountId;
}
