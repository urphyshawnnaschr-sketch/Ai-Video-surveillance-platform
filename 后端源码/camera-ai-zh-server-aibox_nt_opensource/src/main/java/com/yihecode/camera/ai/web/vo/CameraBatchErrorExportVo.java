package com.yihecode.camera.ai.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CameraBatchErrorExportVo {

    /**
* Index
*/
    @ExcelProperty("Index")
    private Integer idx;

    /**
* Brand Name
*/
    @ExcelProperty("Brand")
    private String brand;

    /**
* Camera Name
*/
    @ExcelProperty("Camera Name")
    private String name;
    /**
* ip Address
*/
    @ExcelProperty("ip Address")
    private String ipHost;

    /**
* ip Address
*/
    @ExcelProperty("Port")
    private String port;

    /**
* Channel No
*/
    @ExcelProperty("Channel No")
    private String channel;

    /**
* Account
*/
    @ExcelProperty("Account")
    private String account;

    /**
* Password
*/
    @ExcelProperty("Password")
    private String password;

    /**
* Bind Algorithm, multi with Comma No Split
*/
    @ExcelProperty("Relate Algorithm")
    private String algorithmNames;

    /**
* Region Name
*/
    @ExcelProperty("Point Bit belong belong")
    private String locationName;

    /**
* Alert Interval
*/
    @ExcelProperty("Alert Interval")
    private String alarmInterval;

    /**
* Recognition Interval
*/
    @ExcelProperty("Recognition Interval")
    private String intervalTime;

    /**
* Custom Video Stream Address
*/
    @ExcelProperty("Video Stream Address")
    private String rtspUrl;

    /**
* Error Note
*/
    @ExcelProperty("Error Note")
    private String mistakeDesc;

}
