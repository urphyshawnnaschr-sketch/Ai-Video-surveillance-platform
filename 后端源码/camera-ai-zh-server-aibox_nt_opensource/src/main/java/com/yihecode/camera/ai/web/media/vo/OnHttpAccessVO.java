package com.yihecode.camera.ai.web.media.vo;

import lombok.Data;

/**
* zlm hook on_http_access Event Param
*
* @author zhoumingxing 465769438@qq.com
* @since 2025/2/12
*/
@Data
public class OnHttpAccessVO {

    private String mediaServerId;

    private String id;

    private String ip;

    private boolean isDir;

    private String params;

    private String path;

    private int port;

}
