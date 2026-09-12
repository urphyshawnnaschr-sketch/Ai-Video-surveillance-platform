package com.yihecode.camera.ai.dto.xml;

import lombok.Data;

/**
* @Author lichangliang
* @Date 2023/7/28 22:40
* @Describe
* @Version 1.0
*/
@Data
public class BndBox {
    private float xmin;
    private float ymin;
    private float xmax;
    private float ymax;
    private Integer rotate_angle = 0;
}
