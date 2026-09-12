package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

public class Request {

    @JSONField(name = "type")
    private String type;

    @JSONField(name = "sn")
    private String sn;

    @JSONField(name = "request_id")
    private String requestId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
