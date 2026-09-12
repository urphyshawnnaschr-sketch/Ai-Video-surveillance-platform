package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yihecode.camera.ai.entity.Camera;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Face Recognition Result - Face Recognition System
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Face Recognition Result")
@Data
@TableName("tbl_biz_face_report")
public class FaceReport {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Camera ID")
    @TableField("camera_id")
    private Long cameraId;

    @ApiModelProperty(value = "Whether Contain Stranger produce Person")
    @TableField("has_stranger")
    private Integer hasStranger;

    @ApiModelProperty(value = "Recognition Result")
    @TableField("result_json")
    private String resultJson;

    @ApiModelProperty(value = "Recognition File")
    @TableField("file_path")
    private String filePath;

    @ApiModelProperty(value = "Recognition Time")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Recognition Time (ms Value)")
    @TableField("created_mills")
    private Long createdMills;

    @ApiModelProperty(value = "Group id")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "Face Image id")
    @TableField("face_id")
    private Long faceId;

    @ApiModelProperty(value = "Face User id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "original image File Path")
    @TableField("source_file")
    private String sourceFile;

    @ApiModelProperty(value = "Similarity")
    @TableField("similarity")
    private Float similarity;

    @ApiModelProperty(value = "Camera Entity Info")
    @TableField(exist = false)
    private Camera camera;

    @ApiModelProperty(value = "Group Entity Info")
    @TableField(exist = false)
    private FaceGroup group;

    @ApiModelProperty(value = "Face User Entity Info")
    @TableField(exist = false)
    private FaceUser faceUser;

    @ApiModelProperty(value = "Create Time String")
    @TableField(exist = false)
    private String createdStr;
}