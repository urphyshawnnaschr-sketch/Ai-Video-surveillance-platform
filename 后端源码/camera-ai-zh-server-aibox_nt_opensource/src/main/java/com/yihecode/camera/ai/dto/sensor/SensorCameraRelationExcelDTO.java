package com.yihecode.camera.ai.dto.sensor;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorCameraRelationExcelDTO {
    @ExcelProperty(index = 0)
    private String sensorName;

    @ExcelProperty(index = 1)
    private String sensorCode;

    @ExcelProperty(index = 2)
    private String cameraName;
}

