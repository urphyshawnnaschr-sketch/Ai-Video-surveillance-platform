package com.yihecode.camera.ai.web.ap.vo;


import com.yihecode.camera.ai.dto.ApProjectDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("increase Quantity Training Create Project Param")
public class TrainingCreateProjectRequestVo {

    private TrainingBaseVo trainingBaseVo;
    @ApiModelProperty("Project Param")
    private ApProjectDTO projectDTO;
}
