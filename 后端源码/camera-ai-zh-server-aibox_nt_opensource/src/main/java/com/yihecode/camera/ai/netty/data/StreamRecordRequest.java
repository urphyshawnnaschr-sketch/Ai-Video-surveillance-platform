package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

public class StreamRecordRequest extends Request {

    @JSONField(name = "cameras")
    private List<Map<String, Object>> cameras;

    @JSONField(name = "record_type")
    private Integer recordType;

    public StreamRecordRequest() {
        super();
    }

    public List<Map<String, Object>> getCameras() {
        return cameras;
    }

    public void setCameras(List<Map<String, Object>> cameras) {
        this.cameras = cameras;
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }
}
