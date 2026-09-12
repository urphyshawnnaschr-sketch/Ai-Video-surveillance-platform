package com.yihecode.camera.ai.web.media.vo;

import lombok.Data;

/**
* zlm hook on_play Event Param
*
* @author zhoumingxing 465769438@qq.com
* @since 2025/2/12
*/
@Data
public class OnPlayVO {

    private String mediaServerId;

    private String app;

    private String id;

    private String ip;

    private String params;

    private int port;

    private String schema;

    private String protocol;

    private String stream;

    private String vhost;

}
