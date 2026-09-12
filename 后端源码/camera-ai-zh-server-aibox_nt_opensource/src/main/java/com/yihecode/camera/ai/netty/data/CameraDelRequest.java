package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class CameraDelRequest extends Request {

    @JSONField(name = "camera_id")
    private Long cameraId;
}
