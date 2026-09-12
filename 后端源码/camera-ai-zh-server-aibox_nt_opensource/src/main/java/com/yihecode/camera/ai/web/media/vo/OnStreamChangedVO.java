package com.yihecode.camera.ai.web.media.vo;

import lombok.Data;

/**
* zlm hook on_stream_changed Event Param
*
* @author zhoumingxing 465769438@qq.com
* @since 2025/2/12
*/
@Data
public class OnStreamChangedVO {

    private String mediaServerId;

    private String app;

    private boolean regist;

    private String schema;

    private String stream;

    private String vhost;
}
