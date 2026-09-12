package com.yihecode.camera.ai.javacv;

import lombok.Builder;
import lombok.Data;

/**
* Video Info
* @author zhoumingxing
* @date 2024/8/28
*/
@Data
@Builder
public class VideoInfo {

    /**
* Video Code Info
*/
    private String videoCodec;

    /**
* Video Frame Rate
*/
    private Integer videoFps;

    /**
* Snapshot according File Name
*/
    private String fileName;

    /**
* Video Width
*/
    private Integer videoWidth;

    /**
* Video high Degree
*/
    private Integer videoHeight;
}
