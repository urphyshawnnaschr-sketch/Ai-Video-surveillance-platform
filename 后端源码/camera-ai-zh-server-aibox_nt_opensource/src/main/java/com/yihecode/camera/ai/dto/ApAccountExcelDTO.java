package com.yihecode.camera.ai.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApAccountExcelDTO implements Serializable {

    @ExcelProperty("Account Name")
    private String account;

    @ExcelProperty("Account Password")
    private String password;

    @ExcelProperty("User Name")
    private String name;

    @ExcelProperty("belong belong Team")
    private String teamName;

    @ExcelProperty("Role Type")
    private String roleNames;

    @ExcelProperty("Create Time")
    private String createdAt;
}