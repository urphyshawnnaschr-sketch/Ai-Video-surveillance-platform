package com.yihecode.camera.ai.media;

import lombok.Data;

@Data
public class MediaStreamNode {

    private String schema;

    private String app;

    private String stream;

    private boolean isRecordMp4;

    private Integer readerCount;

    private String originUrl;
}
