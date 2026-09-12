package com.yihecode.camera.ai.web.media.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
* zlm hook on_record_mp4 Event Param
*
* @author zhoumingxing 465769438@qq.com
* @since 2025/2/12
*/
@Data
public class OnRecordMp4VO {

    private String mediaServerId;

    private String app;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_path")
    private String filePath;

    @JsonProperty("file_size")
    private long fileSize;

    private String folder;

    @JsonProperty("start_time")
    private long startTime;

    private String stream;

    @JsonProperty("time_len")
    private double timeLen;

    private String url;

    private String vhost;
}
