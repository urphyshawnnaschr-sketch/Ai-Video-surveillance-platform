package com.yihecode.camera.ai.netty.data;

import lombok.Data;

public class ActivationStatusRequest extends Request{

    public ActivationStatusRequest() {
        super();
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + getType() + '\'' +
                ", sn='" + getSn() + '\'' +
                '}';
    }
}
