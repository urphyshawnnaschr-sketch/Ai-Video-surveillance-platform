package com.yihecode.camera.ai.web.ap.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("Fine Fix Annotation Box Request")
public class RefineRequestVo {

    @ApiModelProperty(value = "Storage Address", dataType = "string", example = "/data/camera/2023/3/30/1641425040524783617/1641440311620743169-d72d7a00-cf03-11ed-aaf9-0242ac140004.jpg")
    private String storagePath;

    private AnnotationVo annotation;
}
