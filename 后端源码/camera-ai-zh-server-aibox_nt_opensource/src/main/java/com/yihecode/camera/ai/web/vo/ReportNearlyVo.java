package com.yihecode.camera.ai.web.vo;

import lombok.Data;

import java.util.List;

/**
* Alarm Temp near Data Query Param
*/
@Data
public class ReportNearlyVo {

    private List<Integer> auditResult;
    private List<Integer> markList;

    private List<Long> departIds;

    private Long cameraId;

    private Long algorithmId;

    private String startDate;

    private String endDate;

    //0- left Side Temp near No One,1- right Side Temp near No One, front Raise is Need By id in Line Sort (left Side up order, right Side down order) after Get Value
private Integer type = 0;

// Current Alarm ID
private Long id;
}
