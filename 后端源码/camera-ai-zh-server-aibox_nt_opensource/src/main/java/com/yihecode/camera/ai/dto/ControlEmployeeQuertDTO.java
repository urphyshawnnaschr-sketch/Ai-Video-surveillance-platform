package com.yihecode.camera.ai.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
* @Author lichangliang
* @Date 2023/6/29 19:16
* @Describe
* @Version 1.0
*/
@Data
@Builder
public class ControlEmployeeQuertDTO {
    private Long id;

    private String employeeName;

    private String employeeNumber;

    private String area;

    private String employeePicture;

    private Date createTime;
}
