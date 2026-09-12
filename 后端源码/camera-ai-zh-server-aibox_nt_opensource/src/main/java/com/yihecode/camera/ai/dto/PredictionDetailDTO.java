package com.yihecode.camera.ai.dto;

import lombok.Data;

import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/24 17:56
* @Describe
* @Version 1.0
*/
@Data
public class PredictionDetailDTO {
    private Float confidence;
    private List<Float> position;
    private String type;
}
