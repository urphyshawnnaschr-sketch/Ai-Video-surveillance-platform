package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ReportSummaryTask;

import java.util.Date;

/**
* Alarm Count Day Data Task
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface ReportSummaryTaskService extends IService<ReportSummaryTask> {

    /**
* Create Record
*/
    void addData(Date reportDate);
}