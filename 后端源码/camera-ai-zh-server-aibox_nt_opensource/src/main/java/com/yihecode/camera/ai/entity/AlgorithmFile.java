package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
* Algorithm File Entity
* @author Abyss
* @date 2023/12/11 15:48
*/
@ApiModel(value = "Algorithm File Entity")
@Data
@TableName(value = "tbl_biz_algorithm_file", autoResultMap = true)
public class AlgorithmFile implements Serializable {

    @ApiModelProperty(value = "Primary Key", dataType = "long", required = true)
    private Long id;

    @ApiModelProperty(value = "Hardware Platform", dataType = "string")
    @TableField("platform")
    private String platform;

    @ApiModelProperty(value = "Model English Name", dataType = "string", example = "fire", required = true)
    @TableField("name_en")
    private String nameEn;

    @ApiModelProperty(value = "File Name", dataType = "string")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "sha256", dataType = "string")
    @TableField("file_sha256")
    private String fileSha256;

    @TableField(exist = false)
    private String size;

}

