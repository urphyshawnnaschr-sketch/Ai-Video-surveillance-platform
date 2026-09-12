package com.yihecode.camera.ai.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.AlarmLevel;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.AlgorithmAlarmLevel;
import com.yihecode.camera.ai.mapper.AlarmLevelMapper;
import com.yihecode.camera.ai.mapper.AlgorithmAlarmLevelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* Algorithm Alert Level Relate Management
* @author Abyss
*/
@Service
public class AlgorithmAlarmLevelServiceImpl extends ServiceImpl<AlgorithmAlarmLevelMapper, AlgorithmAlarmLevel> implements AlgorithmAlarmLevelService {

    @Override
    public Long getLevelIdByAlgorithmId(Long algorithmId) {
        Long accountId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<AlgorithmAlarmLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlgorithmAlarmLevel::getAlgorithmId, algorithmId);
        queryWrapper.eq(AlgorithmAlarmLevel::getAccountId, accountId);
        AlgorithmAlarmLevel algorithmAlarmLevel = this.getOne(queryWrapper);
        if (algorithmAlarmLevel == null) {
            return null;
        } else {
            return algorithmAlarmLevel.getLevelId();
        }
    }

    @Override
    public void save(Long algorithmId, Long alarmLevelId) {
        Long accountId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<AlgorithmAlarmLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlgorithmAlarmLevel::getAlgorithmId, algorithmId);
        queryWrapper.eq(AlgorithmAlarmLevel::getAccountId, accountId);
        this.remove(queryWrapper);
        AlgorithmAlarmLevel algorithmAlarmLevel = new AlgorithmAlarmLevel();
        algorithmAlarmLevel.setAlgorithmId(algorithmId);
        algorithmAlarmLevel.setAccountId(accountId);
        algorithmAlarmLevel.setLevelId(alarmLevelId);
        this.save(algorithmAlarmLevel);
    }

    @Override
    public void removeByAlgorithmId(Long algorithmId) {
        LambdaQueryWrapper<AlgorithmAlarmLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlgorithmAlarmLevel::getAlgorithmId, algorithmId);
        this.remove(queryWrapper);
    }

    @Override
    public Long getLevelIdByAlgorithmIdAndAccountId(Long algorithmId, Long accountId) {
        LambdaQueryWrapper<AlgorithmAlarmLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlgorithmAlarmLevel::getAlgorithmId, algorithmId);
        queryWrapper.eq(AlgorithmAlarmLevel::getAccountId, accountId);
        AlgorithmAlarmLevel algorithmAlarmLevel = this.getOne(queryWrapper);
        if (algorithmAlarmLevel == null) {
            return null;
        } else {
            return algorithmAlarmLevel.getLevelId();
        }
    }
}