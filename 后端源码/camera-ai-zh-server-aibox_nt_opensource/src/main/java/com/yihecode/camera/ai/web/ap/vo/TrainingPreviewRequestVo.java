package com.yihecode.camera.ai.web.ap.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("increase Quantity Training Preview Param")
public class TrainingPreviewRequestVo {

    private TrainingBaseVo trainingBaseVo;

    private Integer page;

    private Integer limit;
}
