package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
* Recording
*/
@Data
@TableName(value = "tbl_biz_record", autoResultMap = true)
public class Record {

    private Long id;

    @TableField("media_server_id")
    private String mediaServerId;

    @TableField("app")
    private String app;

    @TableField("stream")
    private String stream;

    @TableField("file_name")
    private String fileName;

    @TableField("file_size")
    private Long fileSize;

    @TableField("start_time")
    private Long startTime;

    @TableField("end_time")
    private Long endTime;

    @TableField("time_len")
    private Integer timeLen;

    @TableField("file_url")
    private String fileUrl;

    @TableField("flag")
    private Integer flag;

    @TableField("upload_flag")
    private Integer uploadFlag;

    @TableField("location_id")
    private Long locationId;

    @TableField("record_path")
    private String recordPath;
}
