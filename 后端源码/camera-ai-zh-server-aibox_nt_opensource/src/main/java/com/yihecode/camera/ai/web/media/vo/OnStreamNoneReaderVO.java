package com.yihecode.camera.ai.web.media.vo;

import lombok.Data;

/**
* zlm hook on_stream_none_reader Event Param
*
* @author zhoumingxing 465769438@qq.com
* @since 2025/2/12
*/
@Data
public class OnStreamNoneReaderVO {

    private String mediaServerId;

    private String app;

    private String schema;

    private String stream;

    private String vhost;
}
