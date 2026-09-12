package com.yihecode.camera.ai.dto;

import lombok.Data;

import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/24 17:54
* @Describe
* @Version 1.0
*/
@Data
public class ModelPredictionDTO {
    private String algorithm_name;

    private Long algorithm_id;

    private List<PredictionDetailDTO> data;
}
