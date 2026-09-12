package com.yihecode.camera.ai.dto.coco;

import lombok.Data;

/**
* @Author lichangliang
* @Date 2023/7/29 15:00
* @Describe
* @Version 1.0
*/
@Data
public class CocoImage {

    private Long id;

    private Integer width;
    private Integer height;
    private String file_name;
}
