package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ReportPeriod;
import com.yihecode.camera.ai.exception.BizException;

import java.util.List;

/**
* Alert Hour Segment Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface ReportPeriodService extends IService<ReportPeriod> {

    /**
*
* @param cameraId
* @return
*/
    List<Long> listAlgorithmId(Long cameraId);

    /**
*
* @param cameraId
* @param algorithmId
*/
    void deleteByCameraAndAlgorithm(Long cameraId, Long algorithmId);

    /**
*
* @param cameraId
* @param algorithmId
* @return
*/
    List<ReportPeriod> listData(Long cameraId, Long algorithmId);

    /**
* Save Data
* @param reportPeriod
*/
    void saveData(ReportPeriod reportPeriod) throws BizException;

    /**
* By Camera Query
* @param cameraId
* @return
*/
    List<ReportPeriod> listByCamera(Long cameraId);

    /**
* By Camera Delete
* @param cameraId
*/
    void deleteByCamera(Long cameraId);
}