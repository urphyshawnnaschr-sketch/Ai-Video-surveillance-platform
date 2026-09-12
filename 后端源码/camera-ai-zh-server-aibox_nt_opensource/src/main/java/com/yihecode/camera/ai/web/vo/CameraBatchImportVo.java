package com.yihecode.camera.ai.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CameraBatchImportVo {

    /**
* Brand Name
*/
    @ExcelProperty(index = 1)
    private String brand;

    /**
* Camera Name
*/
    @ExcelProperty(index = 2)
    private String name;

    /**
* ip Address
*/
    @ExcelProperty(index = 3)
    private String ipHost;

    /**
* ip Address
*/
    @ExcelProperty(index = 4)
    private String port;

    /**
* Channel No
*/
    @ExcelProperty(index = 5)
    private String channel;

    /**
* Account
*/
    @ExcelProperty(index = 6)
    private String account;

    /**
* Password
*/
    @ExcelProperty(index = 7)
    private String password;

    /**
* Bind Algorithm, multi with Comma No Split
*/
    @ExcelProperty(index = 8)
    private String algorithmNames;

    /**
* Region Name
*/
    @ExcelProperty(index = 9)
    private String locationName;

    /**
* Alert Interval
*/
    @ExcelProperty(index = 10)
    private String alarmInterval;

    /**
* Recognition Interval
*/
    @ExcelProperty(index = 11)
    private String intervalTime;

    /**
* Custom Video Stream Address
*/
    @ExcelProperty(index = 12)
    private String rtspUrl;

    /**
* Region Type - Frontend transmit Value
*/
    private String locationType;

    /**
* Import File Name
*/
    private String filename;

    /**
* Import Batch Label
*/
    private String tag;

}
