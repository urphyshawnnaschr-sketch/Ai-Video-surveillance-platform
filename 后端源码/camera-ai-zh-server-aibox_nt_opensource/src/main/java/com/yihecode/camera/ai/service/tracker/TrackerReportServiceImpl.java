package com.yihecode.camera.ai.service.tracker;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.tracker.TrackerReport;
import com.yihecode.camera.ai.mapper.tracker.TrackerReportMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
* Person Stream Quantity Tracking Recognition Result - Person Stream Quantity Tracking child Module
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class TrackerReportServiceImpl extends ServiceImpl<TrackerReportMapper, TrackerReport> implements TrackerReportService {

    /**
* Page Query
*
* @param page
* @param limit
* @param cameraIds
* @param startTime
* @param endTime
* @return
*/
    @Override
    public IPage<TrackerReport> listPage(Integer page, Integer limit, List<Long> cameraIds, List<Long> departIds, Date startTime, Date endTime) {
        //
Page<TrackerReport> pageObj = new Page<>(page, limit);
pageObj.setCountId("selectCustomCount");
LambdaQueryWrapper<TrackerReport> queryWrapper = new LambdaQueryWrapper<>();
if(cameraIds!= null &&!cameraIds.isEmpty()) {
queryWrapper.in(TrackerReport::getCameraId, cameraIds);
}
if(departIds!= null &&!departIds.isEmpty()) {
queryWrapper.in(TrackerReport::getDepartId, departIds);
}
if(startTime!= null) {
queryWrapper.ge(TrackerReport::getCreatedAt, startTime);
}
if(endTime!= null) {
queryWrapper.le(TrackerReport::getCreatedAt, endTime);
}
queryWrapper.groupBy(TrackerReport::getCameraId);
return this.getBaseMapper().selectCustomPage(pageObj, queryWrapper);
}

/**
* Query Count Data
*
* @param startTime
* @param endTime
*/
@Override
public Map<String, Integer> countSummary(Date startTime, Date endTime) {
List<Map<String, Object>> countDatas = this.getBaseMapper().selectSummary(startTime, endTime);

//
Map<String, Integer> dataMap = new HashMap<>();
dataMap.put("enterCount", 0);
dataMap.put("leaveCount", 0);
dataMap.put("remainCount", 0);
if(countDatas!= null &&!countDatas.isEmpty()) {
Map<String, Object> countData = countDatas.get(0);
if(countData!= null) {
Integer enterCounts = Convert.toInt(countData.get("enter_counts"), 0);
Integer leaveCounts = Convert.toInt(countData.get("leave_counts"), 0);
int remainCounts = enterCounts - leaveCounts;
dataMap.put("enterCount", enterCounts);
dataMap.put("leaveCount", leaveCounts);
dataMap.put("remainCount", Math.max(0, remainCounts));
}
}
return dataMap;
}

/**
* By Camera in Line Count
*
* @param startTime
* @param endTime
* @return
*/
@Override
public List<TrackerReport> countGroupbyCamera(Date startTime, Date endTime) {
LambdaQueryWrapper<TrackerReport> queryWrapper = new LambdaQueryWrapper<>();
if(startTime!= null) {
queryWrapper.ge(TrackerReport::getCreatedAt, startTime);
}
if(endTime!= null) {
queryWrapper.le(TrackerReport::getCreatedAt, endTime);
}
queryWrapper.groupBy(TrackerReport::getCameraId);
return this.getBaseMapper().selectGroupbyCamera(queryWrapper);
}

/**
* by h Group Count
*
* @param startTime
* @param endTime
* @return
*/
@Override
public List<Map<String, Object>> groupByHour(Date startTime, Date endTime) {
return this.getBaseMapper().selectGroupByHour(startTime, endTime);
}

/**
* by Week Group Count
*
* @param startTime
* @param endTime
* @return
*/
@Override
public List<Map<String, Object>> groupByWeek(Date startTime, Date endTime) {
return this.getBaseMapper().selectGroupByWeek(startTime, endTime);
}

/**
* by Month Group Count
*
* @param startTime
* @param endTime
* @return
*/
@Override
public List<Map<String, Object>> groupByMonth(Date startTime, Date endTime) {
return this.getBaseMapper().selectGroupByMonth(startTime, endTime);
}
}
