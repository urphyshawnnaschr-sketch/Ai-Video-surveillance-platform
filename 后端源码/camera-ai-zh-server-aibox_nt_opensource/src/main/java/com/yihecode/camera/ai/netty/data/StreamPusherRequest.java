package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

public class StreamPusherRequest extends Request {

    @JSONField(name = "port")
    private Integer cloudStreamPort;

    @JSONField(name = "camera_id")
    private Long cameraId;

    @JSONField(name = "rtsp_url")
    private String rtspUrl;

    public StreamPusherRequest() {
        super();
    }

    public Integer getCloudStreamPort() {
        return cloudStreamPort;
    }

    public void setCloudStreamPort(Integer cloudStreamPort) {
        this.cloudStreamPort = cloudStreamPort;
    }

    public Long getCameraId() {
        return cameraId;
    }

    public void setCameraId(Long cameraId) {
        this.cameraId = cameraId;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }
}
