package com.yihecode.camera.ai.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Description: Camera Alert Draw Box
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
public class ReportMessage {

    /**
* Type HEART | REPORT
*/
    private String type;

    /**
* Camera id
*/
    private String cameraId;

    private String algorithmId;

    /**
* Draw Box Coordinate
*/
    private String params;

    private String cameraName;

    private String algorithmName;

    private String algorithmNameEn;

    private String alarmTime;

    private String wareName;

    private String id;

    private String webUrl;

    private String gbId;

    private String channelId;

    @ApiModelProperty(value = "Data Source 0 System, 1 Hikvision")
    private String dataSource;
}
