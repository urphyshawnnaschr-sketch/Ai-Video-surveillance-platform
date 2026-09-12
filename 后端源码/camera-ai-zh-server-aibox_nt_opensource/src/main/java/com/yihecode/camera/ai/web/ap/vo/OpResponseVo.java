package com.yihecode.camera.ai.web.ap.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
@ApiModel("Operation Common Back")
public class OpResponseVo {
    @ApiModelProperty("Success Image ids")
    private List<Long> successIds;

    @ApiModelProperty("Failed Tip Message")
    private Map<Long, String> errMsg;
}
