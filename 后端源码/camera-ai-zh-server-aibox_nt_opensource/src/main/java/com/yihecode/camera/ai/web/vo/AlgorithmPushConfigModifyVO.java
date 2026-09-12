package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* Algorithm Push Config
*/
@ApiModel("Third Party Push Data Config")
@Data
public class AlgorithmPushConfigModifyVO {

    @ApiModelProperty("Data List")
    private List<AlgorithmPushConfigVO> list;

}
