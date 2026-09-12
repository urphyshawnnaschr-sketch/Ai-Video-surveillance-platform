package com.yihecode.camera.ai.vo.ap;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/27 20:19
* @Describe
* @Version 1.0
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEfficiencyVO {
    @ApiModelProperty(value = "Image Info")
    private List<EfficiencyImageVO> imageVOList;

    @ApiModelProperty(value = "Quality Check Info")
    private List<EfficiencyReviewVO> reviewVOList;
}
