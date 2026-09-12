package com.yihecode.camera.ai.web.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Camera Query DTO")
public class CameraDTO {

    private Integer state;

    private Integer running;

    private List<Long> locationIdList;

    private List<Long> idList;

    private List<Long> defaultIdList;

}
