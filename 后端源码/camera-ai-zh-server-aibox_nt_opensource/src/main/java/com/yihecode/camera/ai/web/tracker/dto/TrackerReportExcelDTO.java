package com.yihecode.camera.ai.web.tracker.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
* Data Export
* @author zhoumingxing
* @date 2024/9/14
*/
@Data
public class TrackerReportExcelDTO {

    /**
* belong belong Box Name
*/
    @ExcelProperty("belong belong Box Name")
    private String boxName;

    /**
* belong belong Box IP
*/
    @ExcelProperty("belong belong Box IP")
    private String boxIp;

    /**
* Brand Name
*/
    @ExcelProperty("Camera Name")
    private String cameraName;

    /**
* in in Headcount
*/
    @ExcelProperty("in in Headcount")
    private Integer enterCount;

    /**
* Away open Headcount"
*/
    @ExcelProperty("Away open Headcount")
    private Integer leaveCount;

    /**
* remaining remainder Headcount
*/
    @ExcelProperty("remaining remainder Headcount")
    private Integer remainCount;
}
