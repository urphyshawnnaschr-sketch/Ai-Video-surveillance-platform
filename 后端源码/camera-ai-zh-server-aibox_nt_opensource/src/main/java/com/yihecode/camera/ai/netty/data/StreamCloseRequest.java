package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

public class StreamCloseRequest extends Request {

    @JSONField(name = "camera_id")
    private Long cameraId;

    @JSONField(name = "cloud_stream_port")
    private Integer cloudStreamPort;

    public StreamCloseRequest() {
        super();
    }

    public Long getCameraId() {
        return cameraId;
    }

    public void setCameraId(Long cameraId) {
        this.cameraId = cameraId;
    }

    public Integer getCloudStreamPort() {
        return cloudStreamPort;
    }

    public void setCloudStreamPort(Integer cloudStreamPort) {
        this.cloudStreamPort = cloudStreamPort;
    }
}
