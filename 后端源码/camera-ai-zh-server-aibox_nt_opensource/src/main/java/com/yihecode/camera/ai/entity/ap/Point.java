package com.yihecode.camera.ai.entity.ap;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {

    @ApiModelProperty(example = "1292.957746478873")
    private Float x;
    @ApiModelProperty(example = "308.4507042253521")
    private Float y;
}
