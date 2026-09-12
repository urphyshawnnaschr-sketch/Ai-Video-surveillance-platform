package com.yihecode.camera.ai.mapper.tracker;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.entity.tracker.TrackerReport;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;
import java.util.List;

/**
* Person Stream Quantity Tracking Recognition Result - Person Stream Quantity Tracking child Module
*/
public interface TrackerReportMapper extends BaseMapper<TrackerReport> {

    /**
* Count Total Count
* @param startTime
* @param endTime
* @return
*/
    List<Map<String, Object>> selectSummary(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
* Custom Count Query Data
* @param page
* @param ew
* @return
*/
    Page<TrackerReport> selectCustomPage(IPage<TrackerReport> page, @Param(Constants.WRAPPER) Wrapper<TrackerReport> ew);

    /**
* Custom Count Query Data
* @param ew
* @return
*/
    int selectCustomCount(@Param(Constants.WRAPPER) Wrapper<TrackerReport> ew);

    /**
* By Camera in Line Count
* @param ew
* @return
*/
    List<TrackerReport> selectGroupbyCamera(@Param(Constants.WRAPPER) Wrapper<TrackerReport> ew);

    /**
* by h Group Query
* @param startTime
* @param endTime
* @return
*/
    List<Map<String, Object>> selectGroupByHour(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
* by Week Group Count
* @param startTime
* @param endTime
* @return
*/
    List<Map<String, Object>> selectGroupByWeek(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
* by Month Group Count
* @param startTime
* @param endTime
* @return
*/
    List<Map<String, Object>> selectGroupByMonth(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
