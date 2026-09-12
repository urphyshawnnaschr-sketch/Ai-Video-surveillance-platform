package com.yihecode.camera.ai.dto.coco;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/29 15:02
* @Describe
* @Version 1.0
*/
@Data
public class CocoAnnotation {
    private Long id;
    private Long image_id;

    private List<Double[][]> segmentation;

    private String shape;

    private BigDecimal area;

    private Integer iscrowd;

    private List<Float> bbox;

    private Integer category_id;
}
