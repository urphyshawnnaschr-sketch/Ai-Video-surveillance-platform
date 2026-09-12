package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ReportPeriod;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ReportPeriodMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* Alert Hour Segment Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class ReportPeriodServiceImpl extends ServiceImpl<ReportPeriodMapper, ReportPeriod> implements ReportPeriodService {

    /**
*
* @param cameraId
* @return
*/
    @Override
    public List<Long> listAlgorithmId(Long cameraId) {
        LambdaQueryWrapper<ReportPeriod> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportPeriod::getCameraId, cameraId);

        List<ReportPeriod> list = this.list(queryWrapper);
        if(list == null) {
            list = new ArrayList<>();
        }

        //
List<Long> algorithmIds = new ArrayList<>();
for(ReportPeriod reportPeriod: list) {
algorithmIds.add(reportPeriod.getAlgorithmId());
}

return algorithmIds;
}

/**
*
* @param cameraId
* @param algorithmId
*/
@Override
public void deleteByCameraAndAlgorithm(Long cameraId, Long algorithmId) {
LambdaQueryWrapper<ReportPeriod> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(ReportPeriod::getCameraId, cameraId);
queryWrapper.eq(ReportPeriod::getAlgorithmId, algorithmId);
this.remove(queryWrapper);
}

/**
*
* @param cameraId
* @param algorithmId
* @return
*/
@Override
public List<ReportPeriod> listData(Long cameraId, Long algorithmId) {
LambdaQueryWrapper<ReportPeriod> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(ReportPeriod::getCameraId, cameraId);
queryWrapper.eq(ReportPeriod::getAlgorithmId, algorithmId);
queryWrapper.orderByAsc(ReportPeriod::getStartTime);

//
List<ReportPeriod> reportPeriodList = this.list(queryWrapper);
if(reportPeriodList == null) {
return new ArrayList<>();
}
return reportPeriodList;
}

/**
* Save Data
*
* @param reportPeriod
*/
@Override
public void saveData(ReportPeriod reportPeriod) throws BizException {
// Query Config Hour Segment
List<ReportPeriod> reportPeriods = this.listData(reportPeriod.getCameraId(), reportPeriod.getAlgorithmId());
// Add Hour Segment
if(reportPeriod.getId() == null) {
// Validate Hour Segment Whether re reply
for(ReportPeriod reportPeriod1: reportPeriods) {
if((reportPeriod1.getStartTime() < reportPeriod.getStartTime() && reportPeriod1.getEndTime() > reportPeriod.getStartTime())
|| (reportPeriod1.getStartTime() < reportPeriod.getEndTime() && reportPeriod1.getEndTime() > reportPeriod.getEndTime())) {
throw new BizException("Config Time Segment re reply");
}
}
//
this.save(reportPeriod);
return;
}

// Modify Hour Segment
// Validate Hour Segment Whether re reply
for(ReportPeriod reportPeriod1: reportPeriods) {
// Current Record, Skip
if(reportPeriod1.getId().equals(reportPeriod.getId())) {
continue;
}
//
if((reportPeriod1.getStartTime() < reportPeriod.getStartTime() && reportPeriod1.getEndTime() > reportPeriod.getStartTime())
|| (reportPeriod1.getStartTime() < reportPeriod.getEndTime() && reportPeriod1.getEndTime() > reportPeriod.getEndTime())) {
throw new BizException("Config Time Segment re reply");
}
}
this.updateById(reportPeriod);
}

/**
* By Camera Query
*
* @param cameraId
* @return
*/
@Override
public List<ReportPeriod> listByCamera(Long cameraId) {
LambdaQueryWrapper<ReportPeriod> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(ReportPeriod::getCameraId, cameraId);
return this.list(queryWrapper);
}

/**
* By Camera Delete
*
* @param cameraId
*/
@Override
public void deleteByCamera(Long cameraId) {
LambdaQueryWrapper<ReportPeriod> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(ReportPeriod::getCameraId, cameraId);
this.remove(queryWrapper);
}

}