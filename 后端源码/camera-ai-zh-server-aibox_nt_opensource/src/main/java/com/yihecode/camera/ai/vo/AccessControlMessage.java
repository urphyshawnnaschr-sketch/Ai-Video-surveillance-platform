package com.yihecode.camera.ai.vo;

import lombok.Data;

/**
* Description: Camera Alert Draw Box
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
public class AccessControlMessage {

    /**
* Type 1 Alarm 0 Normal out in 3 Clear Divide Info 4 Update Title
*/
    private String type;

    /**
* member work Name
*/
    private String employeeName;

    /**
* work No
*/
    private String employeeNumber;

    /**
* Image
*/
    private String picture;
    /**
* 0 in 1 out
*/
    private String inoutStatus;

    /**
* Time
*/
    private String time;

    /**
* Title
*/
    private String title;


}
