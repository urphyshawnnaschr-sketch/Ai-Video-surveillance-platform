package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

public class ImageCaptureRequest extends Request {

    @JSONField(name = "rtsp_url")
    private String rtspUrl;

    public ImageCaptureRequest() {
        super();
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }
}
