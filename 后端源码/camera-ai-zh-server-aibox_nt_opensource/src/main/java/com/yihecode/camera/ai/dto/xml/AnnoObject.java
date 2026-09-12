package com.yihecode.camera.ai.dto.xml;

import lombok.Data;

/**
* @Author lichangliang
* @Date 2023/7/26 23:04
* @Describe
* @Version 1.0
*/
@Data
public class AnnoObject {
    private String name;
    private String pose;
    private Integer truncated = 0;
    private Integer difficult = 0;
    private BndBox bndbox;
}
