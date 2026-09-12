package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Edge Box Status Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Edge Box Status Management")
@Data
@TableName("tbl_biz_aibox_status")
public class AiboxStatus {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Box id")
    @TableField("aibox_id")
    private Long aiboxId;

    @ApiModelProperty(value = "Box Code")
    @TableField("aibox_sn")
    private String aiboxSn;

    @ApiModelProperty(value = "Disk make Use Rate")
    @TableField("disk_used")
    private Double disKUsed;

    @ApiModelProperty(value = "inner Store make Use Rate")
    @TableField("memory_used")
    private Double memoryUsed;

    @ApiModelProperty(value = "cpu make Use Rate")
    @TableField("cpu_used")
    private Double cpuUsed;

    @ApiModelProperty(value = "apu make Use Rate")
    @TableField("apu_used")
    private Double apuUsed;

    @ApiModelProperty(value = "vic make Use Rate")
    @TableField("vic_used")
    private Double vicUsed;

    @ApiModelProperty(value = "ipe make Use Rate")
    @TableField("ipe_used")
    private Double ipeUsed;

    @ApiModelProperty(value = "Chip Temperature")
    @TableField("temperature_used")
    private Double temperatureUsed;

    @ApiModelProperty(value = "Create Time")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Time Label, Format: Hour: part: s")
    @TableField("time_tag")
    private String timeTag;
}