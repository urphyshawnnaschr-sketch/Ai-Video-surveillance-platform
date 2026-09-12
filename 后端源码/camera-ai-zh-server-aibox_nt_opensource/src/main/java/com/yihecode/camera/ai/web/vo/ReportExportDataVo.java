package com.yihecode.camera.ai.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
* Alarm Data Export
* @author zhoumingxing
* @date 2024/7/1
*/
@Data
@Getter
@Setter
@EqualsAndHashCode
public class ReportExportDataVo {

    @ExcelProperty("Camera Name")
    private String cameraName;

    @ExcelProperty("Alarm Algorithm")
    private String algorithmName;

    @ExcelProperty("belong belong Department")
    private String departName;

    @ExcelProperty("Box No")
    private String boxSn;

    @ExcelProperty("Box IP")
    private String boxIp;

    @ExcelProperty("Alarm Image")
    private String fileName;

    @ExcelProperty("Alarm Data")
    private String params;

    @ExcelProperty("Alarm Time")
    private String createdAt;

    @ExcelProperty("Process Status")
    private String auditResult;

    @ExcelProperty("Process Time")
    private String auditAt;

    @ExcelProperty("Process Hour long")
    private String auditTimeLen;
    @ExcelProperty("Alert Annotation")
    private String mark;
}
