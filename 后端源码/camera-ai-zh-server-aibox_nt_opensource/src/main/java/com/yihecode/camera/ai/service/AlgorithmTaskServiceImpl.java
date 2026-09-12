package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.AlgorithmTask;
import com.yihecode.camera.ai.mapper.AlgorithmTaskMapper;
import org.springframework.stereotype.Service;

/**
* Algorithm Download Task table
* @author Abyss
* @date 2023/12/13 15:54
*/
@Service
public class AlgorithmTaskServiceImpl extends ServiceImpl<AlgorithmTaskMapper, AlgorithmTask> implements AlgorithmTaskService {
    /**
* By File Name Query Download Task
*
* @param fileName
* @return
*/
    @Override
    public AlgorithmTask getByFileName(String fileName) {
        LambdaQueryWrapper<AlgorithmTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlgorithmTask::getFileName, fileName);
        return this.getOne(queryWrapper, false);
    }

    /**
* By File Path Query Download Task
*
* @param path
* @return
*/
    @Override
    public int getByFilePath(String path) {
        LambdaQueryWrapper<AlgorithmTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlgorithmTask::getFilePath, path);
        return this.count(queryWrapper);
    }

    /**
* Delete All Download Task
*/
    @Override
    public void removeAll() {
        LambdaQueryWrapper<AlgorithmTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.gt(AlgorithmTask::getState, -1);
        this.remove(queryWrapper);
    }
}