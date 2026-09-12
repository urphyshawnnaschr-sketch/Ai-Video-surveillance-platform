package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class AlgoExtrasRequest extends Request {

    @JSONField(name = "algo_id")
    private Long algoId;

    @JSONField(name = "extras")
    private String extras;
}
