package com.yihecode.camera.ai.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Algorithm Alert Level Relate Management
* @author Abyss
*/
@ApiModel(value = "Algorithm Alert Level Relate Management")
@Data
@TableName(value = "tbl_biz_algorithm_alarm_level", autoResultMap = true)
public class AlgorithmAlarmLevel {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "User id")
    @TableField("account_id")
    private Long accountId;

    @ApiModelProperty(value = "Algorithm id")
    @TableField("algorithm_id")
    private Long algorithmId;

    @ApiModelProperty(value = "Alert Level id")
    @TableField("level_id")
    private Long levelId;


}