package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

public class ActivateDeviceRequest extends Request {

    @JSONField(name = "active_code")
    private String activeCode;

    public ActivateDeviceRequest() {
        super();
    }

    public String getActiveCode() {
        return activeCode;
    }

    public void setActiveCode(String activeCode) {
        this.activeCode = activeCode;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + getType() + '\'' +
                ", sn='" + getSn() + '\'' +
                ", activeCode='" + getActiveCode() + '\'' +
                '}';
    }

}
