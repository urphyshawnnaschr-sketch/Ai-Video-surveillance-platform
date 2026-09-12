package com.yihecode.camera.ai.web.api.aibox.vo;

import lombok.Data;

/**
* Upload Recording Data, not Contain File
*
* @author 465769438@qq.com
* @since 2025/3/7
*/
@Data
public class UploadRecordDataVo {

    private String app;

    private String mediaServerId;

    private String stream;

    private String url;

    private Long startTime;

    private Integer timeLen;

    private String fileName;

    private Long fileSize;

    private String key;

    private String sn;

}
