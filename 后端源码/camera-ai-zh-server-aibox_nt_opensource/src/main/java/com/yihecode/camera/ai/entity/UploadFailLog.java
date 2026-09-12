package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
@Data
@ApiModel(value = "File Upload failed Record table")
@TableName("upload_fail_log")
public class UploadFailLog {
    private Long id;
    @TableField("path")
    private String path;
    @TableField("status")
    private String status;
    @TableField("created_at")
    private Date createdAt;
    @TableField("updated_at")
    private Date updatedAt;
}
