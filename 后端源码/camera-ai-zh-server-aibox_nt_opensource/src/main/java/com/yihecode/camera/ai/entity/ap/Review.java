package com.yihecode.camera.ai.entity.ap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yihecode.camera.ai.enums.ap.ReviewAction;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel(value = "Quality Check Entity")
@Data
@TableName(value = "ap_reviews", autoResultMap = true)
public class Review {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890")
    private Long id;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    private Date updatedAt;

    @TableLogic(value = "null", delval = "now()")
    @JsonIgnore
    private Date deletedAt;

    @ApiModelProperty(value = "Project id", dataType = "long", example = "1234567890", required = true)
    private Long projectId;

    @ApiModelProperty(value = "Image ID", dataType = "long", example = "1234567890", required = true)
    private Long imageId;

    @ApiModelProperty(value = "Submit ID", dataType = "long", example = "1234567890")
    private Long commitId;

    @ApiModelProperty(value = "Quality Check member ID", dataType = "long", example = "1234567890")
    private Long reviewUserId;

    @ApiModelProperty(value = "Quality Check Result", dataType = "int", example = "1 Pass,3 Rejected,7 Modify")
    private Integer reviewAction;

    @ApiModelProperty(value = "Quality Check Meaning View", dataType = "string")
    private String comment;
}
