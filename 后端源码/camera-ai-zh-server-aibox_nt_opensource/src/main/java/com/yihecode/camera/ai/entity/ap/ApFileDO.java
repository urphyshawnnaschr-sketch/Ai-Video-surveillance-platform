package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.math.BigDecimal;

/**
* pFile DO
*/
@ApiModel(value = "Data Set Entity")
@Data
@Builder
@TableName(value = "ap_file", autoResultMap = true)
public class ApFileDO {

    /**
* ID
*/
    @ApiModelProperty(value = "Primary Key", dataType = "long",required = true)
    private Long id;

    /**
* Create Time
*/
    @ApiModelProperty(value = "Create Time")
    private Date createdAt;

    /**
* Modify Time
*/
    @ApiModelProperty(value = "Modify Time")
    private Date updatedAt;

    /**
* Delete Time
*/
    @ApiModelProperty(value = "Delete Time")
    private Date deletedAt;

    /**
* User ID
*/
    @ApiModelProperty(value = "User ID")
    private Long userId;

    /**
* File Type, 1-zip Data Set,2-excel table Grid,3-doc
*/
    @ApiModelProperty(value = "piece Type, 1-zip Data Set,2-excel table Grid,3-doc")
    private Integer fileType;

    /**
* File Address
*/
    @ApiModelProperty(value = "File Address")
    private String rawData;

    /**
* File Status
*/
    @ApiModelProperty(value = "File Status 1 Parse")
    private String status;

}