package com.yihecode.camera.ai.web.ap.vo;

import com.yihecode.camera.ai.entity.ap.AnnotationData;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationVo {

    @ApiModelProperty(value = "One Level Tag Name", dataType = "string", example = "Person")
    private String tagName;

    @ApiModelProperty(value = "Annotation Type", dataType = "int", example = "1 Image Category 2 Rectangle Box 5 form Point 7 multi Edge Shape")
    private Integer annotationType;

    @ApiModelProperty(value = "Annotation Box")
    private AnnotationData annotation;
}
