package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ReportSummary;

import java.util.List;

/**
* Alarm Count Day Data
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface ReportSummaryService extends IService<ReportSummary> {

    /**
* By Year Month Day Query Data
* @param year
* @param month
* @param day
* @return
*/
    ReportSummary getData(int year, int month, int day);

    /**
* Query All Data
* @return
*/
    List<ReportSummary> listAll();

    List<ReportSummary> listData(int startDate);
}