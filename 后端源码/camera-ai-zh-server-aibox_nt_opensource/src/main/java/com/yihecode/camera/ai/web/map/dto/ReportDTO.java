package com.yihecode.camera.ai.web.map.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ReportDTO {
    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Camera id")
    @TableField("camera_id")
    private Long cameraId;

    @ApiModelProperty(value = "Algorithm id")
    @TableField("algorithm_id")
    private Long algorithmId;

    @ApiModelProperty(value = "Camera Name")
    @TableField(exist = false)
    private String cameraName;

    @ApiModelProperty(value = "Algorithm Name")
    @TableField(exist = false)
    private String algorithmName;

    @ApiModelProperty(value = "Create Time")
    @TableField(exist = false)
    private Date createdAt;

    @ApiModelProperty(value = "Delete remaining remainder Number")
    @TableField(exist = false)
    private int limit;
    @ApiModelProperty(value = "Annotation 0- Pending Fixed, 1- Correct Report, 2- wrong Report")
    private Integer mark;


}
