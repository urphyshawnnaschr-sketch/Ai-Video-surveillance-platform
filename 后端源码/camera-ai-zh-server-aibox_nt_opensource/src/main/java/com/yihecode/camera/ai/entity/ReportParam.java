package com.yihecode.camera.ai.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Alarm Management Entity - param")
public class ReportParam {
    //[{"type":"people","position": [1441, 405, 1649, 593],"confidence": 0.65}, {"type":"people","position": [172, 261, 454, 645],"confidence": 0.63}, {"type":"people","position": [860, 140, 1026, 345],"confidence": 0.56}, {"type":"people","position": [724, 58, 851, 200],"confidence": 0.31}]
// [{"confidence": 0.7,"type":"no_wear","position": [762, 60, 828, 131]}, {"confidence": 0.68,"type":"no_wear","position": [371, 225, 466, 319]}, {"confidence": 0.58,"type":"no_wear","position": [932, 145, 1004, 231]}]

private String type;
private List<Float> position;
private Float confidence;
}
