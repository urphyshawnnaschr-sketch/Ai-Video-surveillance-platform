package com.yihecode.camera.ai.entity.ap;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "Annotation Image Entity")
@Data
@TableName(value = "ap_images", autoResultMap = true)
public class Image {
    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
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

    @ApiModelProperty(value = "Data Set id", dataType = "long", example = "1234567890")
    private Long dataFileId;

    @JsonIgnore
    @ApiModelProperty(value = "Storage Address", dataType = "string", example = "/data/camera/2023/3/30/1641425040524783617/1641440311620743169-d72d7a00-cf03-11ed-aaf9-0242ac140004.jpg")
    private String storagePath;

    @ApiModelProperty(value = "Image Width", dataType = "int", example = "1234567890")
    private Integer width;

    @ApiModelProperty(value = "Image high Degree", dataType = "int", example = "1234567890")
    private Integer height;

    @ApiModelProperty(value = "Image Deep Degree", dataType = "int", example = "1234567890")
    private Integer depth;

    @ApiModelProperty(value = "Image md5", dataType = "string", example = "1234567890")
    private String md5;

    @ApiModelProperty(value = "Image Status", dataType = "int", example = "0- not Annotation,6- Import Annotation,1- Smart can Annotation,4- Person work Annotation in,5- Person work Annotation, 11- Quality Check in,15- Quality Check Pass,16- Type return")
    private Integer status;

    @ApiModelProperty(value = "Whether is Quality Check Get sample", dataType = "int", example = "0 not is 1 is")
    private int needReview;

    @ApiModelProperty(value = "Annotation member id", dataType = "long", example = "1234567890")
    private Long labelUserId;

    @ApiModelProperty(value = "Quality Check member id", dataType = "long", example = "1234567890")
    private Long reviewUserId;

    @ApiModelProperty(value = "Claim Time", dataType = "date")
    private Date assignedAt;

    @ApiModelProperty(value = "Expire Time", dataType = "date")
    private Date expiredAt;

    @ApiModelProperty(value = "Recent One sub Submit id", dataType = "long", example = "1234567890")
    private Long commitId;

    @ApiModelProperty(value = "Recent One sub Submit Annotation Info")
    @TableField(exist = false)
    private Commit commit;

    @ApiModelProperty(value = "Recent One sub Submit Annotation Info Corresponding Quality Check Info")
    @TableField(exist = false)
    private Review review;

    @Version
    @ApiModelProperty("Happy View Lock")
    private Integer version;

}
