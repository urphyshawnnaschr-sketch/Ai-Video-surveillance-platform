package com.yihecode.camera.ai.web.ap.vo;

import com.yihecode.camera.ai.entity.ap.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@ApiModel("Annotation Image Back")
public class ImageVo {

    @ApiModelProperty("Image")
    private List<Image> images;

    @ApiModelProperty("Total")
    private Long total;
}
