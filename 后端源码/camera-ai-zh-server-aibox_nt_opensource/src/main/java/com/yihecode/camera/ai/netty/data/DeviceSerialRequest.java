package com.yihecode.camera.ai.netty.data;

public class DeviceSerialRequest extends Request {

    public DeviceSerialRequest() {
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
