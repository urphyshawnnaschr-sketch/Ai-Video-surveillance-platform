package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
* Algorithm Download Task Entity
* @author Abyss
* @date 2023/12/11 15:51
*/
@ApiModel(value = "Algorithm Download Task Entity")
@Data
@TableName(value = "tbl_biz_algorithm_task", autoResultMap = true)
public class AlgorithmTask implements Serializable {

    @ApiModelProperty(value = "Primary Key", dataType = "long", required = true)
    private Long id;

    @ApiModelProperty(value = "Model English Name", dataType = "string", example = "fire", required = true)
    @TableField("name_en")
    private String nameEn;

    @ApiModelProperty(value = "Algorithm File Package Name", dataType = "string", example = "chaoxing-fire-1.0.zip", required = true)
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "Algorithm File Package Temp Hour Storage Path", dataType = "string", example = "fire", required = true)
    @TableField("file_path")
    private String filePath;

    @ApiModelProperty(value = "Task Status", dataType = "string", example = "0- In Progress Download, 1- Success, 2- Failed")
    @TableField("state")
    private String state;

    @ApiModelProperty(value = "remark", dataType = "string")
    @TableField("remark")
    private String remark;

}

