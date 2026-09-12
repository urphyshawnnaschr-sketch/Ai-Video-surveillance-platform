package com.yihecode.camera.ai.web.vo;

import lombok.Data;

import java.util.List;

/**
* Alarm Data summary total Query Param
*/
@Data
public class ReportStatVo {

    private List<Long> departIds;

    private List<Long> locationIds;

    private String startDate;

    private String endDate;
}
