package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* Batch Delete Account
* @author zhoumingxing
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBatchDeleteRequestVo {

    @ApiModelProperty(value = "User IDs", dataType = "list", example = "")
    private List<Long> accountIds;

}
