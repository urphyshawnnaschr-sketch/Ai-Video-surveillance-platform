package com.yihecode.camera.ai.entity.ap;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationData {

    @ApiModelProperty(value = "Rectangle Box, left up x, left up y, right down x, right down y")
    private List<Float> box; //[750.5172413793102,65.17241379310344,869.6896551724137,189.9310344827586]

@ApiModelProperty(value ="Line")
private List<Point> line;

@ApiModelProperty(value ="Point")
private Point point;

@ApiModelProperty(value ="Label, from One Level to Leaf child")
private List<Label> label;


}


