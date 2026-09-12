package com.yihecode.camera.ai.dto.coco;

import lombok.Data;

import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/29 14:55
* @Describe
* @Version 1.0
*/
@Data
public class Coco {
    private DeatilInfo info;

    private List<CocoLicense> licenses;

    private List<CocoImage> images;

    private List<CocoAnnotation> annotations;

    private List<CocoCategorie> categories;
}
