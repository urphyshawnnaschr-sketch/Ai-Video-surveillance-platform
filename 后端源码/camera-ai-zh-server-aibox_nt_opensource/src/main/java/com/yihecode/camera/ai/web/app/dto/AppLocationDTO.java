package com.yihecode.camera.ai.web.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("app End _ Box Info")
@Data
public class AppLocationDTO {

    @ApiModelProperty(value = "Box ID", example = "1")
    private Long boxId;

    @ApiModelProperty(value = "Box Name", example = "Warehouse Room 1")
    private String boxName;

    @ApiModelProperty(value = "Box Index", example = "abcd1234")
    private String boxNo;

    @ApiModelProperty(value = "Box IP", example = "192.168.0.123")
    private String boxIpAddr;

    @ApiModelProperty(value = "Activate Time", example = "2025-02-03 11:22:33")
    private String activeTime;

    @ApiModelProperty(value = "Online Status, 0- Offline,1- Online", example = "1")
    private Integer online;

    @ApiModelProperty(value = "Online Status Description", example = "Online")
    private String onlineText;

    @ApiModelProperty(value = "Camera Count", example = "10")
    private Integer cameraCount;

    @ApiModelProperty(value = "Camera List")
    private List<AppCameraDTO> cameras;
}
