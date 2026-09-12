package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.AlgorithmAlarmLevel;

/**
* Algorithm Alert Level Relate Management
* @author Abyss
*/
public interface AlgorithmAlarmLevelService extends IService<AlgorithmAlarmLevel> {


    Long getLevelIdByAlgorithmId(Long algorithmId);

    void save(Long algorithmId, Long alarmLevelId);

    void removeByAlgorithmId(Long algorithmId);

    Long getLevelIdByAlgorithmIdAndAccountId(Long algorithmId, Long accountId);
}