package com.yihecode.camera.ai.service.tracker;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.tracker.TrackerReport;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* Person Stream Quantity Tracking Recognition Result - Person Stream Quantity Tracking child Module
*
* @Author 465769438@qq.com
*/
public interface TrackerReportService extends IService<TrackerReport> {

    /**
* Page Query
* @param page
* @param limit
* @param cameraIds
* @param startTime
* @param endTime
* @return
*/
    IPage<TrackerReport> listPage(Integer page, Integer limit, List<Long> cameraIds, List<Long> departIds, Date startTime, Date endTime);

    /**
* Query Count Data
* @param startTime
* @param endTime
*/
    Map<String, Integer> countSummary(Date startTime, Date endTime);

    /**
* By Camera in Line Count
* @param startTime
* @param endTime
* @return
*/
    List<TrackerReport> countGroupbyCamera(Date startTime, Date endTime);

    /**
* by h Group Count
* @param startTime
* @param endTime
* @return
*/
    List<Map<String, Object>> groupByHour(Date startTime, Date endTime);

    /**
* by Week Group Count
* @param startTime
* @param endTime
* @return
*/
    List<Map<String, Object>> groupByWeek(Date startTime, Date endTime);

    /**
* by Month Group Count
* @param startTime
* @param endTime
* @return
*/
    List<Map<String, Object>> groupByMonth(Date startTime, Date endTime);
}

