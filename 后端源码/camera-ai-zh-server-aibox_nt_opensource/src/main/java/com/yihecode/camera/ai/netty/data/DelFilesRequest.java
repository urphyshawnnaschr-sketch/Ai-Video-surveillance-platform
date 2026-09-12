package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class DelFilesRequest extends Request {

    @JSONField(name = "algo_id")
    private Long algoId;

    @JSONField(name = "algo_name")
    private String algoName;

    @JSONField(name = "algo_code")
    private String algoCode;
}
