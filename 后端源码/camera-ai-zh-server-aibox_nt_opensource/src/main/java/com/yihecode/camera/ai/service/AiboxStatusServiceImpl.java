package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.AiboxStatus;
import com.yihecode.camera.ai.mapper.AiboxStatusMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* Edge Box Resource Monitor Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class AiboxStatusServiceImpl extends ServiceImpl<AiboxStatusMapper, AiboxStatus> implements AiboxStatusService {

    /**
* Get most after One Resource Status Record
*
* @param aiboxId
* @return
*/
    @Override
    public AiboxStatus getLast(Long aiboxId) {
        LambdaQueryWrapper<AiboxStatus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiboxStatus::getAiboxId, aiboxId);
        queryWrapper.orderByDesc(AiboxStatus::getCreatedAt);
        queryWrapper.last("limit 0, 1");
        return this.getOne(queryWrapper);
    }

    /**
* Query Recent N Data
*
* @param aiboxId
* @return
*/
    @Override
    public List<AiboxStatus> listLast(Long aiboxId) {
        LambdaQueryWrapper<AiboxStatus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiboxStatus::getAiboxId, aiboxId);
        queryWrapper.orderByDesc(AiboxStatus::getCreatedAt);
        queryWrapper.last("limit 0, 10");
        return this.list(queryWrapper);
    }
}