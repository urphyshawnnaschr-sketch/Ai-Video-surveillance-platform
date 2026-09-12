package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* Algorithm Uninstall or Card Delete Record table
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Algorithm Uninstall or Card Delete Record table")
@Data
@TableName(value = "tbl_biz_algorithm_box")
public class AlgorithmBox implements Serializable {

    @ApiModelProperty(value = "Primary Key", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "Algorithm ID", dataType = "long")
    @TableField("algo_id")
    private Long algoId;

    @ApiModelProperty(value = "Algorithm Name", dataType = "string")
    @TableField("algo_name")
    private String algoName;

    @ApiModelProperty(value = "Algorithm Code", dataType = "string")
    @TableField("algo_code")
    private String algoCode;

    @ApiModelProperty(value = "Process Status,0- Wait Process,1- Processing,2- Process success,3- Process failed", dataType = "int")
    @TableField("state")
    private Integer state;

    @ApiModelProperty(value = "Message", dataType = "string")
    @TableField("msg")
    private String msg;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Execute Time", dataType = "date")
    @TableField("exec_at")
    private Date execAt;

    @ApiModelProperty(value = "Box ID", dataType = "date")
    @TableField("box_id")
    private Long boxId;

    @ApiModelProperty(value = "Process Type, 0- Delete Algorithm File,1- Update Expand Param", dataType = "date")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "Timestamp, By Timestamp in Line up order Row Column in Line Optimize First Process", dataType = "long")
    @TableField("time_mills")
    private Long timeMills;
}

